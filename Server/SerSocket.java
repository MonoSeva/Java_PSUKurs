import java.net.*;
import java.io.*;

//Контейнер описывающий пару: Сетевой - Клиентский сокет
class SocketPair
{
    public  ServerSocket      serverSocket;       //Сервеный сокет для соединения
    public  Socket            clientSocket;       //Клиентский сокет для соединения
    public  DataInputStream   inputThread;        //Поток чтения из сокета
    public  DataOutputStream  outputThread;       //Поток записи в сокет
    public  int               currentPort;        //Порт, на котором находится пара
    public  String            currentIp;          //Ip клиента
    public  String            userLogin;          //Логин, под которым зашёл клиент
    public  Boolean           stateBlock = false; //Состояние блока (t-используется / f-не используется)
}

//Класс, описывающий объект сервера
public class SerSocket
{
    private ServerSocket     guestSocket;       //Гостевой сокет для выдачи нужного порта
    private SocketPair[]     socketMatrix;      //Матрица контейнеров с сокетами
    private int              maxBlocks = 10;    //Максимально кол-во контейнеров в матрице (одновременных клиентов на сервере)
    private Boolean          flagClose = false; //Флаг остановки работы сервера
    private String           fileName;          //Название запрашиваемого файла

    //Основной конструктор для создания серверного сокета
    public SerSocket()
    {
        try
        {
            //Создание гостевого сокета, выдающего номера портов
            guestSocket = new ServerSocket(8000);

            //Создание контейнеров для сокетов
            socketMatrix = new SocketPair[maxBlocks];

            for(int i = 0; i < maxBlocks; ++i){
                socketMatrix[i] = new SocketPair();
            }

            //Создание отдельного потока для функции обработки заявок
            Thread qui = new Thread(() -> TakeNeedPort());
            qui.start();
        }
        catch(UnknownHostException u)
        {
            System.out.println(u);
            return;
        }
        catch(IOException i)
        {
            System.out.println(i);
            return;
        }
    }

    //Поточная функция. Организует раздачу портов (контейнеров) для сокетов, ожидающих сервер.
    //Высоконагруженная функция!!!
    private void TakeNeedPort()
    {
        Socket            copySocket; //Доп. сокет для отлавливания соединения
        DataOutputStream  output;     //Поток записи в сокет

        try
        {
            //Пока не выставлен флаг на закрытие сервера, обрабатываем заявки
            while(true)
            {
                try {
                    Thread.sleep(100);
                }
                catch(InterruptedException i)
                {
                    System.out.println("Except");
                    return;
                }

                //Ждём соединения в порт заявок.
                //Затем, уведомлояем о соединении и открываем поток для отправки сообщений клиенту
                copySocket = guestSocket.accept();
                output = new DataOutputStream(copySocket.getOutputStream());
                System.out.println("\nNew Guest! Taking him new port...");

                //Ищем пустой контейнер под соединение
                for(int i = 1; i < maxBlocks; ++i)
                {
                    //Если такой блок найден - заполняем его актуальными данными
                    if(socketMatrix[i].stateBlock != true)
                    {
                        socketMatrix[i].stateBlock   = true;     //Бронируем контейнер
                        socketMatrix[i].currentPort  = 8000 + i; //Расчёт порта
                        socketMatrix[i].serverSocket =
                                new ServerSocket(socketMatrix[i].currentPort); //Создаём сетевой сокет в забронированном блоке

                        //Сообщаем клиенту, что контейнер готов для работы, высылая номер порта
                        output.writeInt(socketMatrix[i].currentPort);

                        //Создание отдельного потока для каждого контейнера / сетевого сокета
                        ServerThreadCreate(socketMatrix[i]);
                        break;
                    }
                }
            }
        }
        catch(IOException i)
        {
            System.out.println(i);
            return;
        }
    }

    //Создаёт потоки для независимой работы сетевых сокетов.
    //Нужна только для корректного создания потока с функцией ServerSockWork()!
    private void ServerThreadCreate(SocketPair currentPair)
    {
        Thread qui = new Thread(() -> WorkWithClient(currentPair));
        qui.start();
    }

    //Поточная функция. Осуществляет работу сетевого сокета на отдельном потоке / в отдельном контейнере.
    //Высоконагруженная функция!
    private void WorkWithClient(SocketPair currentPair)
    {

        try
        {
            currentPair.clientSocket = currentPair.serverSocket.accept();         //Ожидание подключение клиента через новый порт
            GetClientIp(currentPair);                                             //Получение IP клиента
            System.out.println("New Client on port: " + currentPair.currentPort); //Сообщение о подключении
            System.out.println("Client IP: " + currentPair.currentIp);

            //Создание потоков обмена данными
            currentPair.inputThread  = new DataInputStream(currentPair.clientSocket.getInputStream());
            currentPair.outputThread = new DataOutputStream(currentPair.clientSocket.getOutputStream());

            //Авторизация клиента
            currentPair.userLogin = ClientAuthorization(currentPair);

            //Цикл обработки сообщений
            while(flagClose != true)
            {
                MessageListener(currentPair);
            }

            //Завершение работы с контейнером
            currentPair.serverSocket.close();
            currentPair.stateBlock = false;
            currentPair.userLogin = "null";

            currentPair.inputThread.close();
            currentPair.outputThread.close();

            flagClose = false;

        }
        catch(IOException i)
        {
            System.out.println(i);
            return;
        }
    }

    //Функция аудита сообщений сервера
    private void MessageListener(SocketPair currentPair)
    {
        String messageIdentifier; //Переменная, хранящая идентификатор сервера

        try
        {
            //Получение кода сообщения
            messageIdentifier = currentPair.inputThread.readUTF();

            //Отключение соединения
            if(messageIdentifier.compareTo("0") == 0)
            {
                flagClose = true;
            }
            //Отправить файл
            else if(messageIdentifier.compareTo("1") == 0)
            {
                currentPair.outputThread.writeUTF("2");
                SendFile(currentPair);
            }
            //Запросить файл
            else if(messageIdentifier.compareTo("2") == 0)
            {
                currentPair.outputThread.writeUTF("1");
                GetFileMessage(currentPair);
            }
            //Переадресовать запрос файла на сервер
            else if(messageIdentifier.compareTo("3") == 0)
            {
                currentPair.outputThread.writeUTF("2");
            }
        }
        catch(IOException i)
        {
            System.out.println(i);
            return;
        }
    }

    //Функция-запрос на получение файла
    private Boolean GetFileMessage(SocketPair currentPair)
    {
        FileOutputStream fileStream;                    //Поток для файла
        Long             sizeFile;                      //Размер файла, который мы отправляем
        byte[]           bytesFile = new byte[16*1024]; //Буффер для хранения файла
        int              count;                         //Переменная для контроля передачи

        try
        {
            //Оповещение о начале получения файла
            System.out.println("Start getting file..." + fileName);

            //Запрос на получение файла
            currentPair.outputThread.writeUTF(fileName);

            //Выборка директории получения в зависимости от типа файла
            if(fileName.endsWith(".mp4")){
                fileStream = new FileOutputStream("Server\\AllVideos\\" + fileName);
            }
            else if(fileName.endsWith(".txt"))
            {
                fileStream = new FileOutputStream("Server\\" + currentPair.userLogin +"\\" + fileName);
            }
            else
            {
                return false;
            }

            //Ответ сервера на наличие файла
            if(currentPair.inputThread.readBoolean() == true) //Файл есть и он придёт
            {
                //Ожидание отправки файла
                sizeFile = currentPair.inputThread.readLong();

                while (sizeFile > 0 && (count = currentPair.inputThread.read(bytesFile, 0, (int) Math.min(bytesFile.length, sizeFile))) != -1)
                {
                    fileStream.write(bytesFile, 0, count);
                    sizeFile -= count;
                }

                //Окончание получения файла. Закрытие файлового потока.
                fileStream.close();
                System.out.println("File " + fileName + " " + " get!");
                return true;
            }
            else //Файла нет и он не придёт
            {
                System.out.println("Client don't have file: " + fileName);
                return false;
            }
        }
        catch(IOException i)
        {
            System.out.println(i);
            return false;
        }
    }

    //Функция-ответ на запрос файла от клиента
    private Boolean SendFile(SocketPair currentPair)
    {
        Folder          serverFolder;                  //Папка для извлечения файла
        FileInputStream fileStream;                    //Поток для файла
        byte[]          bytesFile = new byte[16*1024]; //Размер файла, который мы отправляем
        Long            sizeFile;                      //Размер файла, который мы отправляем
        String          needFile;                      //Строка, хранящее значение нужного клиенту файла
        int             count;                         //Переменная для контроля передачи

        try {
            //Забираем название файла
            needFile = currentPair.inputThread.readUTF();

            //Выбор директории. Откуда будем брать файл!?
            serverFolder = new Folder("Server\\AllVideos\\");

            //Оповешение о начале отправки файла
            System.out.println("Client " + currentPair.currentIp + " " + currentPair.currentPort + " need file " + needFile);
            System.out.println("Start Sending...");

            //Задаём начальные признаки отправки
            File sendFile = new File(serverFolder.folderPath + needFile);
            sizeFile      = sendFile.length();
            fileStream    = new FileInputStream(sendFile);

            //Если файл существует
            if (serverFolder.FindFile(needFile)) {
                currentPair.outputThread.writeBoolean(true);
                System.out.println("File exists. Sending...");
                currentPair.outputThread.writeLong(sizeFile);

                while ((count = fileStream.read(bytesFile)) != -1) {
                    currentPair.outputThread.write(bytesFile, 0, count);
                    currentPair.outputThread.flush();
                }
                System.out.println("File send");
                fileStream.close();
                return true;
            }
            //Если файла не существует
            else
            {
                System.out.println("File doesn't exists");
                currentPair.outputThread.writeBoolean(false);
                return false;
            }
        }
        catch(IOException i)
        {
            System.out.println(i);
            return false;
        }
    }

    //Функция, выполняющая авторизацию пользователя
    private String ClientAuthorization(SocketPair currentPair)
    {
        String userLogin  = "";     //Переменная для хранения пользовательского логина
        String userChoose = "1";    //Переменная для хранения выбора пользовтаеля

        try
        {
            //Цикл обработки логина пользователя
            while(userChoose.compareTo("1") == 0)
            {
                //Считывание логина пользователя
                userLogin = currentPair.inputThread.readUTF();

                //Создание объекта папка с нужным логином
                Folder baseFolder = new Folder("Server\\" + userLogin);

                //Проверка существования папки с логином
                if (baseFolder.CheckFolder())
                {
                    //Отправка метки о принятии и сопроводительного сообщения
                    currentPair.outputThread.writeBoolean(true);
                    return userLogin;
                }
                else
                {
                    //Отправка метки об отказе и сопроводительного сообщения
                    currentPair.outputThread.writeBoolean(false);

                    //Считывание ответа пользователя
                    userChoose = currentPair.inputThread.readUTF();

                    //Если он предпочёл создать новый логин...
                    if(userChoose.compareTo("2") == 0)
                    {
                        baseFolder.CreateFolder(); //Создать логин по введённым данным
                        return userLogin;
                    }
                }
            }

            return userLogin;
        }
        catch(IOException i)
        {
            System.out.println(i);
            return "ExceptionsThrowAttic!";
        }
    }

    //Функция, подсчитывающая клиентский IP и помещающая его в currentIP
    private void GetClientIp(SocketPair currentPair)
    {
        InetSocketAddress clientAddress = (InetSocketAddress)currentPair.clientSocket.getRemoteSocketAddress();
        currentPair.currentIp = clientAddress.getAddress().getHostAddress();
    }

    //Функция получения списка контейнеров
    //Необходимо для работы с списком клиентов в интерфейсе
    public SocketPair[] GetSocketList()
    {
        return socketMatrix;
    }

    //Функция получения максимального кол-ва контейнеров
    public int GetContainerMax()
    {
        return maxBlocks;
    }
}
