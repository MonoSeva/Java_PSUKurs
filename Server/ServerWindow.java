import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;

//Класс окна сервера
public class ServerWindow {

    private JFrame     ServerFrame;  //Форма для окна сервера
    private SContainer allVideos;    //Контейнер форм для хранения списка музыки
    private SContainer allClients;   //Контейнер форм для хранения списка клиентов
    private JLabel     serverState;  //Строка о состоянии сервера

    ServerWindow()
    {
        //Создание и настройка окна сервера
        ServerFrame = new JFrame("Server Window");
        ServerFrame.setSize(485, 600);
        ServerFrame.setLayout(null);
        ServerFrame.setVisible(false);
        ServerFrame.setResizable(false);

        //Настройка контейнера с видео
        allVideos = new SContainer(ServerFrame);
        allVideos.SetContainerPosition(0, 0);
        allVideos.SetContainerWidth(200);
        allVideos.SetContainerName("Videos");
        allVideos.AddNewElement("No Videos!");

        //Настройка контейнера с клиентами
        allClients = new SContainer(ServerFrame);
        allClients.SetContainerPosition(250, 0);
        allClients.SetContainerWidth(200);
        allClients.SetContainerName("Clients");
        allClients.HideButtons(false);
        allClients.AddNewElement("No Clients!");
        allClients.SetContainerListHigh(470);

        //Добавление строки состояния
        serverState = new JLabel();
        serverState.setText("Change settings...");
        serverState.setBounds(0, 530, 485, 30);
        serverState.setVerticalAlignment(SwingConstants.CENTER);
        serverState.setHorizontalAlignment(SwingConstants.CENTER);
        ServerFrame.add(serverState);


        allVideos.table.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                JList mList = (JList) evt.getSource();
                if (evt.getClickCount() == 2) {
                    Video playingVideo = new Video(allVideos.allList);
                    playingVideo.PlayingMp4File("Server/AllVideos/", allVideos.table.getSelectedValue().toString());
                }
            }

            public void mousePressed(MouseEvent evt) {}
            public void mouseReleased(MouseEvent evt) {}
            public void mouseEntered(MouseEvent evt) {}
            public void mouseExited(MouseEvent evt) {}
        });

    }

    //Интерфейс для показа / сокрытия окна
    //true - показать окно / false - скрыть окно
    public void ViewServerWindow(Boolean viewMode)
    {
        ServerFrame.setVisible(viewMode);
    }

    //Интерфейс для изменения строки состояния сервера
    //Новая строка состояния
    public void SetStringState(String enterState)
    {
        serverState.setText(enterState);
    }

    //Функция обновления списка клиентов на сервере
    //Список контейнеров для работы
    //Максимальное кол-во контейнеров
    public void UpdateClientList(SocketPair[] actualPair, int actualSize)
    {
        Boolean control = false;

        //Убираем все элементы из списка
        allClients.RemoveAllElements();

        //Добавляет актуальные элементы
        for (int i = 0; i < actualSize; i++)
        {
            if(actualPair[i].stateBlock == true)
            {
                allClients.AddNewElement
                        (actualPair[i].currentPort + ":" + actualPair[i].userLogin + " Ip: " + actualPair[i].currentIp);

                control = true;
            }
        }

        //Если нет элементов в списке
        if(control == false){
            allClients.AddNewElement("No Clients!");
        }
    }

    //Функция обновления списка видео на сервере
    //Необходимо занести в цикл для корректной работы
    public void UpdateVideosList()
    {
        Boolean control = false;

        //Убираем все элементы из списка
        allVideos.RemoveAllElements();

        try
        {
            File file = new File("Server/AllVideos/AllVideos.txt");
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String readLine = bufferedReader.readLine();

            while(readLine != null)
            {
                allVideos.AddNewElement(readLine);
                readLine = bufferedReader.readLine();
                control = true;
            }

            //Елси нет элементов в списке
            if(control == false)
            {
                allVideos.AddNewElement("No Videos!");
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