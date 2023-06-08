import com.sun.jna.NativeLibrary;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class Main {
    public static void main(String[] args)
    {
        //Привязка библиотеки vlc
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:\\Program Files\\VideoLAN\\VLC");
        new NativeDiscovery().discover();

        //Вывод окна клиента
        ClientWindow cw = new ClientWindow();
        cw.ViewServerWindow(true);

        //Создание объекта клиента
        CliSocket nSocket = new CliSocket();

        //Создание объекта "папка клиента" для отслеживания новых файлов
        Folder detectFolder = new Folder("Client/Videos");

        //Пока запущено основное окно
        while(cw.flagStop == false)
        {
            //Обработка подключения клиента к серверу
            if(cw.flagAuth == true)
            {
                nSocket.StartClientWork();
                cw.flagAuth = false;
            }

            //Обработка отключения клиента от сервера
            if(cw.flagDsAt == true)
            {
                nSocket.StopCli();
                cw.flagDsAt = false;
                nSocket.authorization = false;
                nSocket.stop = false;
                nSocket.clientLogin = "null";
            }

            //Анализ резульата авторизации
            if(nSocket.authorization != true)
            {
                cw.SetStringState("Server: Disconnect");
            }
            else
            {
                //Получить список всех видео с сервера
                nSocket.GetCli("AllVideos.txt");

                //Задержка для устранения конфликта передачи
                try
                {
                    Thread.sleep(1000);
                }
                catch(InterruptedException i)
                {
                    System.out.println("Except");
                    return;
                }

                //Скачать нужный файл
                if(cw.flagDown == true)
                {
                    nSocket.GetCli(cw.downFile);
                    cw.flagDown = false;
                }

                //Обновление строки состояния клиента
                cw.SetStringState("Server: Connect. Login: " + nSocket.clientLogin);

                //Обновление списка серверных треков
                cw.UpdateVideosList(cw.allServerVideos, "Client/Playlists", "AllVideos.txt");
            }

            //Обновление списка локальных треков
            detectFolder.DetectAllMp4("ClientPlaylist.txt");
            cw.UpdateVideosList(cw.allOurVideos, "Client/Videos", "ClientPlaylist.txt");

            //Задержка между обновлением харатеристик клиента
            try
            {
                Thread.sleep(2000);
            }
            catch(InterruptedException i)
            {
                System.out.println("Except");
                return;
            }

            //Реакция на остановку программы (Находится здесь, т.к необходимо вручную вызвать остановку)
            if(cw.flagStop == true)
            {
                //Обрыв соединения с сервером
                nSocket.StopCli();
                System.out.println("End");
            }
        }
    }
}
