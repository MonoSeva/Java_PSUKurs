import javax.swing.*;
import java.io.*;


public class Folder
{

    public String folderPath;

    public Folder(String EnteredFolderName){
        folderPath = EnteredFolderName;
    }

    //Функция проверки существования папки
    public Boolean CheckFolder()
    {
        File CheckFolder = new File(folderPath);

        //Условие проверки существования папки
        if(CheckFolder.exists())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    //Функция проверки существования файла
    public Boolean CheckFile(String FileName)
    {
        File CheckFile = new File(folderPath + "\\" + FileName);

        //Условие проверки существования файла
        if(CheckFile.exists() && CheckFile.isFile())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    //Функция создания папки
    //Есть проверка на существовние папки
    public Boolean CreateFolder()
    {
        //Проверка существования папки перед созданием
        if(CheckFolder())
        {
            return false;
        }

        File CreateFolder = new File(folderPath);
        CreateFolder.mkdirs();
        return true;
    }

    //Функция обнаружения всех MP3-файлов в папке и их запись в плейлист
    //Есть проверка на существование папки
    //Есть вывод о ходе проверки
    public void DetectAllMp4(String PlaylistName) throws IOException
    {
        //Проверка существовования папки
        if(!CheckFolder())
        {
            return;
        }

        //Проверка существования файла плейлиста!
        if(!CheckFile(PlaylistName))
        {
            File CheckFile = new File(folderPath + "\\" + PlaylistName);
            CheckFile.createNewFile();
        }

        //Получение списка всех файлов из папки
        File CheckFolder = new File(folderPath);
        File AllFiles[] = CheckFolder.listFiles();

        PrintWriter FileWriter = new PrintWriter(folderPath + "\\" + PlaylistName);

        for (File f : AllFiles)
        {
            if(f.isFile() && f.exists() && f.getName().endsWith(".mp4"))
            {
                FileWriter.println(f.getName());
            }
        }
        FileWriter.close();
    }

    //Функция поиска файла в текущей папке
    //Вводить файлы только с приставкой
    public Boolean FindFile(String fileName)
    {
        //Проверка существовования папки
        if(!CheckFolder())
        {
            return false;
        }

        //Получение списка всех файлов из папки
        File CheckFolder = new File(folderPath);
        File AllFiles[] = CheckFolder.listFiles();

        for (File f : AllFiles)
        {
            //Условие поиска: одинак. имя, это файл, он существует, оканчивается .mp3
            if((fileName.compareTo(f.getName()) == 0) && f.isFile()
                    && f.exists())
            {
                return true;
            }
        }

        return false;
    }

    //Функция выбора и копирования файла в папку Server/AllVideos
    public Boolean GetFileInDirectory()
    {
        //Открытие окна выбора файла
        JFileChooser fileChoose = new JFileChooser();

        //Выбранный файл
        File origFile, copyFile;
        String choseFile = new String("_ESTEB_");

        int ret = fileChoose.showDialog(null, "Открыть файл");
        if(ret == JFileChooser.APPROVE_OPTION)
        {
            origFile = fileChoose.getSelectedFile();
        }
        else
        {
            return false;
        }

        InputStream  is = null;
        OutputStream os = null;

        copyFile = new File("Server/AllVideos/" + origFile.getName());

        try
        {
            is = new FileInputStream(origFile);
            os = new FileOutputStream(copyFile);
            byte[] buffer = new byte[1024];
            int length;

            while((length = is.read(buffer)) > 0)
            {
                os.write(buffer, 0, length);
            }

            is.close();
            os.close();
        }
        catch(IOException e)
        {
            System.out.println(e);
        }

        return true;
    }

    //Функция удаления файла из папки
    public Boolean DeleteFileOutDirectory()
    {
        //Открытие окна выбора файла
        JFileChooser fileChoose = new JFileChooser("Server/AllVideos");

        //Выбранный файл
        File origFile, copyFile;
        String choseFile = new String("_ESTEB_");

        //Открыть окно с выбором файла
        int ret = fileChoose.showDialog(null, "Удалить файл");
        if(ret == JFileChooser.APPROVE_OPTION)
        {
            origFile = fileChoose.getSelectedFile();
        }
        else
        {
            return false;
        }

        //Удаление файла
        origFile.delete();
        return true;
    }
}
