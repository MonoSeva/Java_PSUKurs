import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//Класс окна ввода логина
public class LoginWindow {

    private JFrame     loginFrame;  //Форма для окна логина
    private JTextField loginEnter;  //Строка для ввода логина
    private JButton    loginButton; //Кнопка "Login"
    private JLabel     stateString; //Строка состояния окна логина

    public  Boolean    flagResult = false;  //Флаг фиксации результата

    LoginWindow()
    {
        loginFrame  = new JFrame("Login on Server");
        loginEnter  = new JTextField("");
        loginButton = new JButton("Login");
        stateString = new JLabel();

        //Настройка окна ввода логина
        loginFrame.setBounds(500, 500, 300, 120);
        loginFrame.setLayout(null);
        loginFrame.setVisible(false);
        loginFrame.setResizable(false);
        loginFrame.setUndecorated(true);

        //Настройка элементов окна логина
        stateString.setBounds(0, 0, 300, 50);
        stateString.setText("Enter Login");
        stateString.setHorizontalAlignment(JTextField.CENTER);
        stateString.setVerticalAlignment(JTextField.CENTER);

        loginEnter.setBounds(0, 50, 300, 30);
        loginButton.setBounds(0, 80, 300, 40);

        //Добавление элементов в окно
        loginFrame.add(loginEnter);
        loginFrame.add(loginButton);
        loginFrame.add(stateString);

        //Установка листенеров
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flagResult = true;
            }
        });
    }

    //Интерфейс для показа / сокрытия окна
    //true - показать окно / false - скрыть окно
    public void ViewLoginWindow(Boolean viewMode)
    {
        loginFrame.setVisible(viewMode);
    }

    //Интерфейс для изменения строки состояния
    //Новое значение строки состояния
    public void SetStringState(String enterState)
    {
        stateString.setText(enterState);
    }

    //Интерфейс для получения введённого логина
    public String GetLoginEnter()
    {
        return loginEnter.getText();
    }
}
