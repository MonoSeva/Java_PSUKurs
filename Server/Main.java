import java.lang.Thread;
import java.io.IOException;
import com.sun.jna.NativeLibrary;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

//Основной класс
public class Main
{
    public static void main(String[] args) throws IOException
    {

        //Привязка библиотеки vlc
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:\\Program Files\\VideoLAN\\VLC");
        new NativeDiscovery().discover();

        /*Предподготовка*/

        //Проверка на наличие папки Server
        Folder CheckedFolder = new Folder("Server");
        CheckedFolder.CreateFolder();

        //Проверка на наличие папки Server/AllTracks
        CheckedFolder.folderPath = "Server\\AllVideos";
        CheckedFolder.CreateFolder();

        //Найти все MP4 файлы в папке Server\AllTracks
        CheckedFolder.DetectAllMp4("AllVideos.txt");

        /*_____________*/

        //Создание объекта сервера
        SerSocket OurServer = new SerSocket();

        //Вывод окна сервера
        ServerWindow sw = new ServerWindow();
        sw.ViewServerWindow(true);
        sw.SetStringState("Server stability work");

        while(true)
        {
            //Уведомление об обновлении сервера
            sw.SetStringState("Data update");

            //Обновление в списке клиентов
            sw.UpdateClientList(OurServer.GetSocketList(), OurServer.GetContainerMax());

            //Пересоздание списка видео
            CheckedFolder.DetectAllMp4("AllVideos.txt");

            //Обновление в спике видео
            sw.UpdateVideosList();

            try {
                sw.SetStringState("Server stability work");
                Thread.sleep(5000);
            }
            catch(InterruptedException i)
            {
                System.out.println("Except");
                return;
            }
        }
    }
}
