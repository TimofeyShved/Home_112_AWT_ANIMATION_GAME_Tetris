package com.AWT;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.AWT.Shape.Tetrominoes;

public class Board extends JPanel implements ActionListener { // класс для игры, где будут происходить действия / в виде панели

    private final int BoardWidth = 10; // размеры
    private final int BoardHeight = 22;

    private Timer timer; // таймер
    private boolean isFallingFinished = false; // статусы / isFallingFinished Переменная определяет, если форма Tetris закончил падать , и затем мы должны создать новую форму.
    private boolean isStarted = false;
    private boolean isPaused = false;
    private int numLinesRemoved = 0; // В numLinesRemovedпод считывает количество строк, мы удалили до сих пор.
    private int curX = 0;
    private int curY = 0; // curX И curY переменные определяют фактическое положение падающей формы Тетриса.
    private JLabel statusbar;
    private Shape curPiece;
    private Shape.Tetrominoes[] board;

    public Board(Tetris parent) { // конструктор
        initBoard(parent); // инициализация
    }

    // ------------------------------------------------------------------------------ инициализация

    private void initBoard(Tetris parent) { // инициализация

        setFocusable(true); // Мы должны явно вызвать setFocusable() метод. Теперь на плате есть ввод с клавиатуры.
        curPiece = new Shape(); // фигуры
        timer = new Timer(400, this); // запускаем таймер
        timer.start();

        statusbar =  parent.getStatusBar();
        board = new Tetrominoes[BoardWidth * BoardHeight];
        addKeyListener(new TAdapter());
        clearBoard();
    }

    @Override // В нашем случае таймер вызывает actionPerformed()метод каждые 400 мсек.
    public void actionPerformed(ActionEvent e) {

        if (isFallingFinished) { // В actionPerformed()методе проверяет , если падение закончилось.
                                    // Если это так, создается новая фигура.
            isFallingFinished = false;
            newPiece();
        } else {
            // В противном случае падающий кусок тетриса переходит на одну строчку вниз.
            oneLineDown();
        }
    }

    private int squareWidth() { return (int) getSize().getWidth() / BoardWidth; }
    private int squareHeight() { return (int) getSize().getHeight() / BoardHeight; }
    private Tetrominoes shapeAt(int x, int y) { return board[(y * BoardWidth) + x]; }


    public void start()  { // --------------------------------------------------- запуск

        if (isPaused)
            return;

        isStarted = true;
        isFallingFinished = false;
        numLinesRemoved = 0;
        clearBoard();

        newPiece();
        timer.start();
    }

    private void pause()  { // ------------------------------------------------------ пауза

        if (!isStarted)
            return;

        isPaused = !isPaused;

        if (isPaused) {

            timer.stop();
            statusbar.setText("paused");
        } else {

            timer.start();
            statusbar.setText(String.valueOf(numLinesRemoved));
        }

        repaint();
    }

    // ---------------------------------------------------------Внутри метода doDrawing () мы рисуем все объекты на доске. Картина состоит из двух этапов.

    private void doDrawing(Graphics g) {

        Dimension size = getSize();
        int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();

        for (int i = 0; i < BoardHeight; ++i) { // На первом этапе мы рисуем все формы или остатки фигур, которые были опущены на нижнюю часть доски.

            for (int j = 0; j < BoardWidth; ++j) {// Все квадраты запоминаются в массиве доски. Мы обращаемся к нему с помощью shapeAt()метода.

                Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);

                if (shape != Tetrominoes.NoShape)
                    drawSquare(g, 0 + j * squareWidth(),
                            boardTop + i * squareHeight(), shape);
            }
        }

        if (curPiece.getShape() != Tetrominoes.NoShape) { // На втором этапе мы рисуем собственно падающую деталь.

            for (int i = 0; i < 4; ++i) {

                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                drawSquare(g, 0 + x * squareWidth(),
                        boardTop + (BoardHeight - y - 1) * squareHeight(),
                        curPiece.getShape());
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) { // отрисовка компонентов
        super.paintComponent(g);
        doDrawing(g);
    }

    // --------------------------------------------------------------------------- Если мы нажмем клавишу пробела, кусок опускается вниз.
    private void dropDown() {

        int newY = curY;

        while (newY > 0) { // Мы просто пытаемся опустить кусок на одну линию вниз, пока он не достигнет низа или вершины другого выпавшего фрагмента Тетриса.

            if (!tryMove(curPiece, curX, newY - 1))
                break;
            --newY;
        }

        pieceDropped();
    }

    private void oneLineDown()  {

        if (!tryMove(curPiece, curX, curY - 1))
            pieceDropped();
    }

    // ----------------------------------------------------------- clearBoard() Метод заполняет доску с пустым NoShapes. Позже это используется при обнаружении столкновений.
    private void clearBoard() {

        for (int i = 0; i < BoardHeight * BoardWidth; ++i)
            board[i] = Tetrominoes.NoShape;
    }

    // ---------------------------------------------------------------- pieceDropped() Метод помещает падающий кусок в доски массива.
    private void pieceDropped() {
        //Еще раз, доска содержит все квадраты фигур и остатки тех фигур, которые закончили падать.
        for (int i = 0; i < 4; ++i) {

            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * BoardWidth) + x] = curPiece.getShape();
        }
                            //Когда фигура закончила падать, пора проверить, сможем ли мы убрать несколько линий с доски.
        removeFullLines(); //Это работа removeFullLines() метода. Затем создаем новый кусок. Точнее, мы пытаемся создать новое произведение.

        if (!isFallingFinished)
            newPiece();
    }

    //---------------------------------------------------------------------- newPiece() Метод создает новый Тетрис кусок.
    private void newPiece()  {

        curPiece.setRandomShape(); // Изделие приобретает новую случайную форму.
        curX = BoardWidth / 2 + 1; // Затем мы вычисляем начальное значение curX и curY значения.
        curY = BoardHeight - 1 + curPiece.minY();

        if (!tryMove(curPiece, curX, curY)) { // Если мы не можем перейти на исходные позиции, игра окончена. Мы на вершине.
            // Таймер остановлен. Мы помещаем игру поверх строки в строку состояния.
            curPiece.setShape(Tetrominoes.NoShape);
            timer.stop();
            isStarted = false;
            statusbar.setText("game over");
        }
    }

    // ---------------------------------------------------------------------------- tryMove() Метод пытается переместить часть Тетрис.
    private boolean tryMove(Shape newPiece, int newX, int newY) {

        for (int i = 0; i < 4; ++i) { // Метод возвращает false, если он достиг границ доски или находится рядом с уже выпавшими фишками тетриса.

            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);

            if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
                return false;

            if (shapeAt(x, y) != Tetrominoes.NoShape)
                return false;
        }

        curPiece = newPiece;
        curX = newX;
        curY = newY;

        repaint();

        return true;
    }

    // -------------------------------------------- Внутри removeFullLines()метода мы проверяем, есть ли среди всех строк на доске какая-либо полная строка .
    private void removeFullLines() {

        int numFullLines = 0;

        for (int i = BoardHeight - 1; i >= 0; --i) { // Если есть хотя бы одна полная строка, она удаляется
            boolean lineIsFull = true;

            for (int j = 0; j < BoardWidth; ++j) {
                if (shapeAt(j, i) == Tetrominoes.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {//Найдя полную строку, увеличиваем счетчик
                ++numFullLines;
                for (int k = i; k < BoardHeight - 1; ++k) {
                    for (int j = 0; j < BoardWidth; ++j)
                        board[(k * BoardWidth) + j] = shapeAt(j, k + 1);
                }
            }
        }

        if (numFullLines > 0) { // Перемещаем все строки над полным рядом на одну строку вниз. Таким образом мы уничтожаем всю линию.

            numLinesRemoved += numFullLines;
            statusbar.setText(String.valueOf(numLinesRemoved));
            isFallingFinished = true;
            curPiece.setShape(Tetrominoes.NoShape);
            repaint();
        }
    }

    // ---------------------------------------------------------------Каждая фигура тетриса состоит из четырех квадратов.
    // ---------------------------------------------------------------Каждый из квадратов нарисован drawSquare()методом. Фишки тетриса имеют разные цвета.
    private void drawSquare(Graphics g, int x, int y, Tetrominoes shape)  {

        Color colors[] = { new Color(0, 0, 0), new Color(204, 102, 102),
                new Color(102, 204, 102), new Color(102, 102, 204),
                new Color(204, 204, 102), new Color(204, 102, 204),
                new Color(102, 204, 204), new Color(218, 170, 0)
        };
        // Левая и верхняя стороны квадрата отображаются более ярким цветом.
        // Точно так же нижняя и правая стороны нарисованы более темными цветами. Это для имитации 3D-кромки.

        Color color = colors[shape.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + 1);

    }

    // --------------------------------------------------- Управляем игрой с клавиатуры. Механизм управления реализован с помощью KeyAdapter.
    // --------------------------------------------------- Это внутренний класс, который переопределяет keyPressed()метод.
    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            if (!isStarted || curPiece.getShape() == Tetrominoes.NoShape) {
                return;
            }

            int keycode = e.getKeyCode();

            if (keycode == 'p' || keycode == 'P') {
                pause();
                return;
            }

            if (isPaused)
                return;

            switch (keycode) {

                case KeyEvent.VK_LEFT: // Если мы нажмем клавишу со стрелкой влево, мы попытаемся переместить падающую фигуру на один квадрат влево.
                    tryMove(curPiece, curX - 1, curY);
                    break;

                case KeyEvent.VK_RIGHT:
                    tryMove(curPiece, curX + 1, curY);
                    break;

                case KeyEvent.VK_DOWN:
                    tryMove(curPiece.rotateRight(), curX, curY);
                    break;

                case KeyEvent.VK_UP:
                    tryMove(curPiece.rotateLeft(), curX, curY);
                    break;

                case KeyEvent.VK_SPACE:
                    dropDown();
                    break;

                case 'd':
                    oneLineDown();
                    break;

                case 'D':
                    oneLineDown();
                    break;
            }
        }
    }
}
