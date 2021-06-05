package com.AWT;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;


public class Tetris extends JFrame { // наш главный класс (Тетрис)

    private JLabel statusbar; // переменная для отображения данных о игре (счёт и статус)

    public Tetris() {// конструтор
        initUI(); // инициализация
    }

    private void initUI() {
        statusbar = new JLabel(" 0"); // начальный счёт при запуске игры
        add(statusbar, BorderLayout.SOUTH); // добавляем её на форму вниз
        Board board = new Board(this); // создаём класс для игры, где будут происходить действия / в виде панели
        add(board); // добавляем её на панель
        board.start(); // и запускаем его

        setSize(200, 400); // размеры
        setTitle("Tetris"); // заголовок
        setDefaultCloseOperation(EXIT_ON_CLOSE); // кнопка выхода
        setLocationRelativeTo(null); // по центру
    }

    public JLabel getStatusBar() { // метод для возврата статуса
        return statusbar;
    }

    public static void main(String[] args) {// метод главный для запуска всего

        SwingUtilities.invokeLater(new Runnable() { //  поток

            @Override
            public void run() { // запускаем

                Tetris game = new Tetris(); // наш класс в виде формы
                game.setVisible(true);// видимость
            }
        });
    }
}
