import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

//Класс окна с воспроизводимым внутри видео
public class Video {

    private JFrame                       videoFrame;           //Экран для вывода видео
    private EmbeddedMediaPlayerComponent mediaPlayerComponent; //Проигрыватель
    private JButton playButton;
    private JButton pauseButton;
    private JButton nextButton;
    private JButton previousButton;
    private JPanel  panel;
    private JPanel  panelMedia;
    private JButton stopButton;
    private JButton resetButton;
    private JButton fastForwardButton;
    private JButton rewindButton;
    private JButton volumeUpButton;
    private JButton volumeDownButton;
    private JButton skipForwardButton;
    private JButton skipBackwardButton;

    Video(DefaultListModel<String> musiclist)
    {
        videoFrame = new JFrame("Плеер");
        videoFrame.setVisible(true);
        videoFrame.setSize(900, 600);
        videoFrame.setLocation(120, 100);
        videoFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        videoFrame.setContentPane(panel);//задаёт содержимое окна с рамкой

        panelMedia = new JPanel();
        mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        panelMedia.add(mediaPlayerComponent);

        videoFrame.setContentPane(mediaPlayerComponent);
        videoFrame.setContentPane(new JPanel(new BorderLayout()));
        videoFrame.getContentPane().add(mediaPlayerComponent, BorderLayout.CENTER);
        videoFrame.getContentPane().add(panel, BorderLayout.SOUTH);
        videoFrame.setVisible(false);

        //Листенеры//

        //Кнопка закрытия окна
        videoFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mediaPlayerComponent.release();
                videoFrame.setVisible(false);
            }
        });

        //Кнопка Play
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mediaPlayerComponent.getMediaPlayer().play();
            }
        });

        //Кнопка Pause
        pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mediaPlayerComponent.getMediaPlayer().pause();
            }
        });

        //Кнопка Next
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                int curindex = 0;
                String data = "";

                for(;curindex < musiclist.size(); curindex++)
                {
                    data = musiclist.getElementAt(curindex);
                    if(data.compareTo(videoFrame.getName()) == 0)
                    {
                        if(curindex == musiclist.size()-1)
                        {
                            data = videoFrame.getName();
                            break;
                        }
                        else
                        {
                            data = musiclist.getElementAt(curindex+1);
                            break;
                        }
                    }
                }

                videoFrame.setName(data);
                mediaPlayerComponent.getMediaPlayer().stop();
                mediaPlayerComponent.getMediaPlayer().playMedia("Server/AllVideos/" + data);
            }
        });

        //Кнопка Previous
        previousButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                int curindex = 0;
                String data = "";

                for(;curindex < musiclist.size(); curindex++)
                {
                    data = musiclist.getElementAt(curindex);
                    if(data.compareTo(videoFrame.getName()) == 0)
                    {
                        if(curindex == 0)
                        {
                            data = videoFrame.getName();
                            break;
                        }
                        else
                        {
                            data = musiclist.getElementAt(curindex-1);
                            break;
                        }
                    }
                }

                videoFrame.setName(data);
                mediaPlayerComponent.getMediaPlayer().stop();
                mediaPlayerComponent.getMediaPlayer().playMedia("Server/AllVideos/" + data);
            }
        });

        //Кнопка Stop
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mediaPlayerComponent.getMediaPlayer().stop();
            }
        });

        //Кнопка Reset
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mediaPlayerComponent.getMediaPlayer().setTime(0);
            }
        });

        //Кнопка >> (ускорение в 2 раза)
        fastForwardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mediaPlayerComponent.getMediaPlayer().setRate(mediaPlayerComponent.getMediaPlayer().getRate()*2);
            }
        });

        //Кнопка << (замеделние в 2 раза)
        rewindButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mediaPlayerComponent.getMediaPlayer().setRate(mediaPlayerComponent.getMediaPlayer().getRate()/2);
            }
        });

        //Кнопка + (увеличение громкости)
        volumeUpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mediaPlayerComponent.getMediaPlayer().setVolume(mediaPlayerComponent.getMediaPlayer().getVolume() + 10);
            }
        });

        //Кнопка - (уменьшение грокости)
        volumeDownButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mediaPlayerComponent.getMediaPlayer().setVolume(mediaPlayerComponent.getMediaPlayer().getVolume() - 10);
            }
        });

        //Кнопка >>10 (перемотка на 10 секунд вперёд)
        skipForwardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mediaPlayerComponent.getMediaPlayer().skip(10000);
            }
        });

        //Кнопка 10<< (перемотка на 10 секунд назад)
        skipBackwardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mediaPlayerComponent.getMediaPlayer().skip(-10000);
            }
        });
    }

    //Интерфейс для проигрывания файлв в формате mp4
    //Путь к папке с файлом
    //Проигрываемый файл
    public void PlayingMp4File(String path, String fileName)
    {
        //Показ окна проигрывателя и проигрывание видео
        videoFrame.setVisible(true);
        videoFrame.setName(fileName);
        mediaPlayerComponent.getMediaPlayer().playMedia(path + fileName);
    }
}