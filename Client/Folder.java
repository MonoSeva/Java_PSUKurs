import javax.swing.*;
import java.io.*;

//Класс папки для работы с файлами
public class Folder
{
    public String folderPath; //Путь до папки

    public Folder(String enteredFolderName)
    {
        folderPath = enteredFolderName;
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

    //Функция проверки существования файла в данной папке
    public Boolean CheckFile(String fileName)
    {
        File CheckFile = new File(folderPath + "\\" + fileName);

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

    //Функция обнаружения всех MP4-файлов в папке и их запись в плейлист
    //Есть проверка на существование папки
    //Есть вывод о ходе проверки
    public void DetectAllMp4(String playlistName)
    {
        try {
            //Проверка существовования папки
            if (!CheckFolder()) {
                return;
            }

            //Проверка существования файла плейлиста
            if (!CheckFile(playlistName)) {
                File CheckFile = new File(folderPath + "\\" + playlistName);
                CheckFile.createNewFile();
            }

            //Получение списка всех файлов из папки
            File CheckFolder = new File(folderPath);
            File AllFiles[] = CheckFolder.listFiles();

            PrintWriter FileWriter = new PrintWriter(folderPath + "\\" + playlistName);

            for (File f : AllFiles) {
                if (f.isFile() && f.exists() && f.getName().endsWith(".mp4")) {
                    FileWriter.println(f.getName());
                }
            }

            FileWriter.close();
        }
        catch(IOException IOE)
        {
            System.out.println(IOE);
        }
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
            //Условие поиска: одинак. имя, это файл, он существует
            if((fileName.compareTo(f.getName()) == 0) && f.isFile()
                    && f.exists())
            {
                return true;
            }
        }

        return false;
    }

    //Функция выбора и копирования файла в папку
    //Папка, куда будет скопирована информация
    public Boolean CopyFileInDirectory(String enterFolderPath)
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

        copyFile = new File(enterFolderPath + origFile.getName());

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
    //Путь к папке, в которой лежит файл
    public Boolean DeleteFileFromDirectory(String enterFolderPath)
    {
        //Открытие окна выбора файла
        JFileChooser fileChoose = new JFileChooser(enterFolderPath);

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
