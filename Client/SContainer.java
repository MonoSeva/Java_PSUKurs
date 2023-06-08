import javax.swing.*;

//Класс серверных контейнеров форм (Список - Граф. представление - Кнопка)
public class SContainer
{
    public  DefaultListModel<String>  allList;    //Список элементов в таблице
    public  JButton                   addButton;  //Кнопка добавления в список
    public  JButton                   delButton;  //Кнопка удаления из списка
    public  JList <String>            table;      //Выводимая таблица
    private JLabel                    name;       //Подпись контейнера
    private JScrollPane               scroll;     //Скроллбар контейнера

    private int xPos  = 0; //Позиции контейнера
    private int yPos  = 0;

    private int width = 0; //Ширина контейнера

    private int high = 410;//Высота списка в контейнере

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

        //Настройка надписи над контейнером
        name.setHorizontalAlignment(SwingConstants.CENTER);
        name.setVerticalAlignment(SwingConstants.CENTER);
        baseFrame.add(name);
    }

    //Функция фиксации изменения размеров контейнера
    private void ChangeContainerPosition()
    {
        name.setBounds(xPos, yPos, width, 30);
        table.setBounds(xPos, yPos + 30, width, high);
        addButton.setBounds(xPos, yPos + 440, width, 30);
        delButton.setBounds(xPos, yPos + 470, width, 30);
        scroll.setBounds(xPos + width, yPos + 30, 20, 500);
    }

    //Интерфейс для задания координат контейнера
    public void SetContainerPosition(int x, int y)
    {
        xPos = x;
        yPos = y;
        ChangeContainerPosition();
    }

    //Интерфейс для задания ширины контейнера
    public void SetContainerWidth(int enterWidth)
    {
        width = enterWidth;
        ChangeContainerPosition();
    }

    //Интерфейс для задания названия кнопок
    public void SetButtonsNames(String firstButton, String secondButton)
    {
        addButton.setText(firstButton);
        delButton.setText(secondButton);
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

