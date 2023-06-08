import javax.swing.*;
import java.awt.event.*;
import java.io.*;

//Класс окна клиента
public class ClientWindow {

    private JFrame     clientFrame;     //Форма для окна клиента
    private JLabel     clientState;     //Строка о состоянии клиента
    public  SContainer allOurVideos;    //Контейнер форм для хранения списка локальных видео
    public  SContainer allServerVideos; //Контейнер форм для хранения списка серверных видео

    // Публичные флаги // Связь с интерфейсом сокетов
    public Boolean     flagStop = false;//Установка флага остановки
    public Boolean     flagAuth = false;//Установка флага подключения
    public Boolean     flagDsAt = false;//Установка флага отключения
    public Boolean     flagDown = false;//Установка флага закачки (скачивания файла)
    public String      downFile = "";   //Имя скачиваемого файла

    ClientWindow()
    {
        //Создание и настройка окна клиента
        clientFrame = new JFrame("Сlient Window");
        clientFrame.setSize(485, 600);
        clientFrame.setLayout(null);
        clientFrame.setVisible(false);
        clientFrame.setResizable(false);

        //Настройка контейнера с локальными видео
        allOurVideos = new SContainer(clientFrame);
        allOurVideos.SetContainerPosition(0, 0);
        allOurVideos.SetContainerWidth(200);
        allOurVideos.SetContainerName("Local Videos");
        allOurVideos.AddNewElement("No Videos!");

        //Настройка контейнера с серверными видео
        allServerVideos = new SContainer(clientFrame);
        allServerVideos.SetContainerPosition(250, 0);
        allServerVideos.SetContainerWidth(200);
        allServerVideos.SetContainerName("Server Videos");
        allServerVideos.AddNewElement("No Videos!");
        allServerVideos.SetButtonsNames("Connect", "Disconnect");
        allServerVideos.delButton.setVisible(false);
        allServerVideos.addButton.setSize(200, 60);

        //Установка листенеров

        //Реакция на закрытие окна через крестик
        clientFrame.addWindowListener(new WindowListener() {
            public void windowOpened(WindowEvent e) {}
            public void windowClosing(WindowEvent e) {
                flagStop = true;
            }
            public void windowClosed(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowActivated(WindowEvent e) {}
            public void windowDeactivated(WindowEvent e) {}
        });

        //Реакция на двойное нажатие по списку видео с сервера (скачать видео)
        allServerVideos.table.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent evt) {
                JList mList = (JList) evt.getSource();
                if (evt.getClickCount() == 2) {
                    flagDown = true;
                    downFile = allServerVideos.table.getSelectedValue().toString();
                }
            }

            public void mousePressed(MouseEvent evt) {}
            public void mouseReleased(MouseEvent evt) {}
            public void mouseEntered(MouseEvent evt) {}
            public void mouseExited(MouseEvent evt) {}
        });

        //Реакция на двойное нажатие по списку видео с локали (Воспроизвести видео)
        allOurVideos.table.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent evt) {
                JList mList = (JList) evt.getSource();
                if (evt.getClickCount() == 2) {
                    Video playingVideo = new Video(allOurVideos.allList);
                    playingVideo.PlayingMp4File("Client/Videos/", allOurVideos.table.getSelectedValue().toString());
                }
            }

            public void mousePressed(MouseEvent evt) {}
            public void mouseReleased(MouseEvent evt) {}
            public void mouseEntered(MouseEvent evt) {}
            public void mouseExited(MouseEvent evt) {}
        });


        //Add
        allOurVideos.addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Folder folder = new Folder("Client/Videos/");
                folder.CopyFileInDirectory("Client/Videos/");
            }
        });

        //Delete
        allOurVideos.delButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Folder folder = new Folder("Client/Videos/");
                folder.DeleteFileFromDirectory("Client/Videos/");
            }
        });

        //Connect
        allServerVideos.addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                flagAuth = true;
                allServerVideos.addButton.setVisible(false);
                allServerVideos.table.setSize(200, 470);
            }
        });

        //Disconnect
        allServerVideos.delButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            flagDsAt = true;
            }
        });

        //Добавление строки состояния
        clientState = new JLabel();
        clientState.setText("Change settings...");
        clientState.setBounds(0, 530, 485, 30);
        clientState.setVerticalAlignment(SwingConstants.CENTER);
        clientState.setHorizontalAlignment(SwingConstants.CENTER);
        clientFrame.add(clientState);
    }

    //Интерфейс для показа / сокрытия окна
    //true - показать окно / false - скрыть окно
    public void ViewServerWindow(Boolean viewMode)
    {
        clientFrame.setVisible(viewMode);
    }

    //Интерфейс для изменения строки состояния клиента
    //Новая строка состояния
    public void SetStringState(String enterState)
    {
        clientState.setText(enterState);
    }

    //Функция обновления списка видео
    //Необходимо занести в цикл для корректной работы
    //Обновляемый контейнер
    //Путь папки, в которой ищем файл
    //Имя файла
    public void UpdateVideosList(SContainer enterContainer, String enterFolderPath, String fileName)
    {
        Boolean control = false;

        //Убираем все элементы из списка в контейнере
        enterContainer.RemoveAllElements();

        try
        {
            File file = new File(enterFolderPath + "/" + fileName);
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String readLine = bufferedReader.readLine();

            while(readLine != null)
            {
                enterContainer.AddNewElement(readLine);
                readLine = bufferedReader.readLine();
                control = true;
            }

            //Елси нет элементов в списке
            if(control == false)
            {
                enterContainer.AddNewElement("No Videos!");
            }

        }
        catch(FileNotFoundException fe)
        {
            System.out.println(fe);
            return;
        }
        catch(IOException IOE)
        {
            System.out.println(IOE);
            return;
        }
    }
}