import java.net.*;
import java.io.*;
import java.util.concurrent.TimeUnit;

//Архитектура, отвечающая за связь с сервером
public class CliSocket
{
    private Socket           socket;       //Сокет для соединения с сервером
    private DataInputStream  input;        //Поток для чтения из сокета
    private DataOutputStream output;       //Поток для записи в сокет
    private int              portNumber;   //Номер порта, на котором мы будем работать с сервером
    private String           fileName;     //Имя файла для отправки

    //Публичные переменные для запросов
    public String  clientLogin   = "No";  //Логин клиента
    public Boolean authorization = false; //Флаг авторизации. Если она пройдена, начинае работу.
    public Boolean get           = false; //Флаг на получение трека с сервера
    public Boolean stop          = false; //Флаг на завершение работы

    //Сборка и вычисления происходят в отдельном потоке!
    public CliSocket()
    {
        //Функция проверки рабочих папок
        StartUp();
    }

    //Функция начала проверки необходмых папок (Подготовка)
    //Функция создаст необходимые папки, если они отсутствуют
    private void StartUp()
    {
        //Проверка на наличие папки Server
        Folder CheckedFolder = new Folder("Client");
        CheckedFolder.CreateFolder();

        //Проверка на наличие папки Client/Tracks
        CheckedFolder.folderPath = "Client\\Videos";
        CheckedFolder.CreateFolder();

        //Проверка на наличие папки Client/Playlists
        CheckedFolder.folderPath = "Client\\Playlists";
        CheckedFolder.CreateFolder();
    }

    //Основная функция работы с сервером
    private void WorkWithServer()
    {
        try {
            //Получение основного порта для работы
            GetServerPort();

            //Цепляемся на отправленный сервером порт и начинаем работу
            socket = new Socket("127.0.0.1", portNumber);

            //Настройка потока для прослушки, отправки сообщений и ввода с консоли
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            //Авторизация на сервере
            ServerAuthorization();
            authorization = true;

            //Работа с сервером
            while(stop == false)
            {
                MessageListener();
            }

            //Закрытие клиентского сокета
            socket.close();
            input.close();
            output.close();
        }
        catch(IOException i)
        {
            System.out.println(i);
            return;
        }
    }

    //Функция, получающая порт для работы с сервером
    //Порт записывается в portNumber
    private void GetServerPort()
    {
        try
        {
            //Цепляемся на 8000 порт и ждём от сервера информации о другом доступном порте
            socket = new Socket("127.0.0.1", 8000);

            //Организуем поток для получения ответа
            input = new DataInputStream(socket.getInputStream());

            //Начинаем слушать до тех пор, пока у нас не появится ответ
            portNumber = input.readInt();

            //После получения ответа - закрываем сокет и поток
            socket.close();
            input.close();
        }
        catch(IOException i)
        {
            System.out.println(i);
            return;
        }
    }

    //Функция-запрос к серверу получения файла
    private Boolean GetFileMessage()
    {
        FileOutputStream fileStream;                    //Поток для файла
        Long             sizeFile;                      //Размер файла, который мы отправляем
        byte[]           bytesFile = new byte[16*1024]; //Буффер для хранения файла
        int              count;                         //Переменная для контроля передачи

        try
        {
            //Запрос на получение файла
            output.writeUTF(fileName);

            //Выборка директории получения в зависимости от типа файла
            if(fileName.endsWith(".mp4")){
                fileStream = new FileOutputStream("Client\\Videos\\" + fileName);
            }
            else if(fileName.endsWith(".txt"))
            {
                fileStream = new FileOutputStream("Client\\Playlists\\" + fileName);
            }
            else
            {
                return false;
            }

            //Ответ сервера на наличие файла
            if(input.readBoolean() == true) //Файл есть и он придёт
            {
                //Ожидание отправки файла
                sizeFile = input.readLong();

                while (sizeFile > 0 && (count = input.read(bytesFile, 0, (int) Math.min(bytesFile.length, sizeFile))) != -1)
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
                return false;
            }
        }
        catch(IOException i)
        {
            System.out.println(i);
            return false;
        }
    }

    //Функция-ответ на клиентский запрос файла
    private Boolean SendFile()
    {
        Folder          clientFolder;                  //Папка для извлечения файла
        FileInputStream fileStream;                    //Поток для файла
        byte[]          bytesFile = new byte[16*1024]; //Размер файла, который мы отправляем
        Long            sizeFile;                      //Размер файла, который мы отправляем
        String          needFile;                      //Строка, хранящее значение нужного клиенту файла
        int             count;                         //Переменная для контроля передачи

        try {
            //Забираем название файла
            needFile = input.readUTF();

            //Выбор директории. Откуда будем брать файл!?
            if(needFile.endsWith(".mp4")){
                clientFolder = new Folder("Client\\Videos\\");
            }
            else
            {
                clientFolder = new Folder("Client\\Playlists\\");
            }

            //Задаём начальные признаки отправки
            File sendFile = new File(clientFolder.folderPath + needFile);
            sizeFile      = sendFile.length();
            fileStream    = new FileInputStream(sendFile);

            //Если файл существует
            if (clientFolder.FindFile(needFile)) {
                output.writeBoolean(true);
                output.writeLong(sizeFile);

                while ((count = fileStream.read(bytesFile)) != -1) {
                    output.write(bytesFile, 0, count);
                    output.flush();
                }

                fileStream.close();
                return true;
            }
            //Если файла не существует
            else
            {
                output.writeBoolean(false);
                return false;
            }
        }
        catch(IOException i)
        {
            System.out.println(i);
            return false;
        }
    }

    //Функция, выполняющая авторизацию на сервере
    private void ServerAuthorization()
    {
        //Создание окна авторизации
        LoginWindow log = new LoginWindow();
        log.ViewLoginWindow(true);

        String userAnswer = "1";

        try
        {
            //Цикл отправки логина
            while(userAnswer.compareTo("1") == 0)
            {
                //Ожидание ввода пользователя
                //Нужна небольшая задержка, чтобы цикл осуществил срабатывание условия
                while(log.flagResult == false)
                {
                    try
                    {
                        Thread.sleep(100);
                    }
                    catch(InterruptedException i)
                    {
                        System.out.println("Except");
                        return;
                    }
                }

                //Уведомление пользователя, отправка логина на сервер
                log.SetStringState("Start Authorization...");
                clientLogin = log.GetLoginEnter();
                output.writeUTF(clientLogin);

                //Ожидание ответа о корректности логина
                //Если логин корректен
                if(input.readBoolean() == true)
                {
                    log.SetStringState("Login found!");
                    log.SetStringState("Server entering...");
                    log.ViewLoginWindow(false);
                    return;
                }
                //Если логин - некорректен, повторяем ввод
                else
                {
                    log.SetStringState("Login not found! Input other login!");
                    output.writeUTF("1");
                    log.flagResult = false;
                }
            }
        }
        catch(EOFException eof)
        {
            System.out.println(eof);
            return;
        }
        catch(IOException i)
        {
            System.out.println(i);
            return;
        }
    }

    //Функция аудита сообщений сервера
    //Должна быть в цикле
    private void MessageListener()
    {
        String messageIdentifier; //Переменная, хранящая идентификатор сообщения

        try
        {
            //Получение кода сообщения
            messageIdentifier = input.readUTF();

            //Отключение соединения
            if(messageIdentifier.compareTo("0") == 0)
            {
                stop = true;
            }
            //Отправить файл
            else if(messageIdentifier.compareTo("1") == 0)
            {
                output.writeUTF("2");
                SendFile();
            }
            //Запросить файл
            else if(messageIdentifier.compareTo("2") == 0)
            {
                GetFileMessage();
            }
        }
        catch(IOException i)
        {
            System.out.println(i);
            return;
        }
    }

    //Интерфейс запуска авторизации
    public void StartClientWork()
    {
        //Создание отдельного потока для функции работы сокета
        Thread qui = new Thread(() -> WorkWithServer());
        qui.start();
    }

    //Функция получения определённого трека с сервера
    //Имя трека, который необходимо получить с сервера (***.mp3)
    public void GetCli(String trackName)
    {
        try
        {
            if(authorization == true){
                fileName = trackName;
                output.writeUTF("1");
            }
        }
        catch(IOException i)
        {
            System.out.println(i);
            return;
        }
    }

    //Функция остановки работы клиентского сокета
    public void StopCli()
    {
        try
        {
            output.writeUTF("0");
        }
        catch(IOException n)
        {
            System.out.println(n);
            return;
        }
    }
}