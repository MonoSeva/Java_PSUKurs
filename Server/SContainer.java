import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//Класс серверных контейнеров форм (Список - Графю представление - Кнопка)
public class SContainer
{
    public DefaultListModel<String>  allList;    //Список элементов в таблице
    private JButton                  addButton;  //Кнопка добавления в список
    private JButton                  delButton;  //Кнопка удаления из списка
    private JLabel                   name;       //Подпись контейнера
    private JScrollPane              scroll;     //Скроллбар контейнера
    public  JList <String>           table;      //Выводимая таблица

    private int xPos  = 0; //Позиция контейнера
    private int yPos  = 0;
    private int width = 0; //Ширина контейнера

    private int high = 410; //Высота листа в контейнере

    //Конструктор контейнера
    //Окно, куда будут приставлены формы
    SContainer(JFrame baseFrame) {
        allList   = new DefaultListModel<String>();
        table     = new JList<>(allList);
        addButton = new JButton();
        delButton = new JButton();
        name      = new JLabel();
        scroll    = new JScrollPane();

        //Настройка кнопки добавления
        baseFrame.add(addButton);
        addButton.setText("Add Video");

        //Настройка кнопки удаления
        baseFrame.add(delButton);
        delButton.setText("Delete Video");

        //Настройка таблицы
        baseFrame.add(table);

        //Настройка надписи
        name.setHorizontalAlignment(SwingConstants.CENTER);
        name.setVerticalAlignment(SwingConstants.CENTER);
        baseFrame.add(name);


        //Установка листенеров
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Folder folder = new Folder("Server/AllVideos");
                folder.GetFileInDirectory();
            }
        });

        delButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Folder folder = new Folder("Server/AllVideos");
                folder.DeleteFileOutDirectory();
            }
        });
    }

    private void ChangeContainerPosition()
    {
        name.setBounds(xPos, yPos, width, 30);
        table.setBounds(xPos, yPos + 30, width, high);
        addButton.setBounds(xPos, yPos + 440, width, 30);
        delButton.setBounds(xPos, yPos + 470, width, 30);
        scroll.setBounds(xPos + width, yPos + 30, 20, 500);
    }

    //Интерфейс для задания координат контейнера
    public void SetContainerPosition(int X, int Y)
    {
        xPos = X;
        yPos = Y;
        ChangeContainerPosition();
    }

    //Интерфейс для задания ширины контейнера
    public void SetContainerWidth(int enterWidth)
    {
        width = enterWidth;
        ChangeContainerPosition();
    }

    //Интерфейс для сокрытия кнопок (необходимо в списке клиентов)
    //true - сделать видимыми / false - сделать невидимыми
    public void HideButtons(Boolean enterState)
    {
        addButton.setVisible(enterState);
        delButton.setVisible(enterState);
    }

    //Интерфейс для задания новой высоты листа в контейнере
    public void SetContainerListHigh(int enterHigh)
    {
        high = enterHigh;
        ChangeContainerPosition();
    }

    //Интерфейс для задания имени контейнера
    public void SetContainerName(String enterName)
    {
        name.setText(enterName);
    }

    //Интерфейс для добавления нового элемента в список
    public void AddNewElement(String newElement)
    {
        allList.addElement(newElement);
    }

    //Интерфейс для удаления элемента из списка
    public void RemoveAllElements()
    {
        allList.removeAllElements();
    }
}
