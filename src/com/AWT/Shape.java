package com.AWT;

import java.util.Random;

public class Shape { // предоставляет информацию о фрагменте тетриса

    protected enum Tetrominoes { NoShape, ZShape, SShape, LineShape, // Tetrominoes перечисление, содержит все семь фигур тетриса.
        TShape, SquareShape, LShape, MirroredLShape };               // Плюс пустая фигура, называемая здесь NoShape.

    // переменные
    private Tetrominoes pieceShape;
    private int coords[][];
    private int[][][] coordsTable;


    public Shape() { // Это конструктор Shape класса.
        coords = new int[4][2]; // В coord sмассиве хранятся фактические координаты фрагмента тетриса.
        setShape(Tetrominoes.NoShape);
    }

    public void setShape(Tetrominoes shape) { // выбор фигуры

        coordsTable = new int[][][] {       //В coordsTable массиве хранятся все возможные значения координат наших фигур Тетриса.
                { { 0, 0 },   { 0, 0 },   { 0, 0 },   { 0, 0 } },     // Это шаблон, из которого все части берут свои значения координат.
                { { 0, -1 },  { 0, 0 },   { -1, 0 },  { -1, 1 } }, // Например, числа {0, -1}, {0, 0}, {-1, 0}, {-1, -1} представляют повернутую S-образную форму.
                { { 0, -1 },  { 0, 0 },   { 1, 0 },   { 1, 1 } },
                { { 0, -1 },  { 0, 0 },   { 0, 1 },   { 0, 2 } },
                { { -1, 0 },  { 0, 0 },   { 1, 0 },   { 0, 1 } },
                { { 0, 0 },   { 1, 0 },   { 0, 1 },   { 1, 1 } },
                { { -1, -1 }, { 0, -1 },  { 0, 0 },   { 0, 1 } },
                { { 1, -1 },  { 0, -1 },  { 0, 0 },   { 0, 1 } }
        };

        for (int i = 0; i < 4 ; i++) { // Здесь мы помещаем один ряд значений координат от coordsTable до coords массива куска Тетриса.
            for (int j = 0; j < 2; ++j) {
                coords[i][j] = coordsTable[shape.ordinal()][i][j];
            }
        }

        pieceShape = shape; // записываем фигуру в переменную
    }

    // ------------------------------------------------------------------------- методы установки и возрата данных
    private void setX(int index, int x) { coords[index][0] = x; }
    private void setY(int index, int y) { coords[index][1] = y; }
    public int x(int index) { return coords[index][0]; }
    public int y(int index) { return coords[index][1]; }
    public Tetrominoes getShape()  { return pieceShape; }

    public void setRandomShape() { // рандомная фигура
        Random r = new Random();
        int x = Math.abs(r.nextInt()) % 7 + 1;
        Tetrominoes[] values = Tetrominoes.values();
        setShape(values[x]);
    }

    //---------------------------------------------------------------------------- перебор координат, для мин значения
    public int minX() {

        int m = coords[0][0];

        for (int i=0; i < 4; i++) {

            m = Math.min(m, coords[i][0]);
        }

        return m;
    }


    public int minY() {

        int m = coords[0][1];

        for (int i=0; i < 4; i++) {

            m = Math.min(m, coords[i][1]);
        }

        return m;
    }

    //------------------------------------------------------------------------------ повороты фигур

    public Shape rotateLeft() { // Этот код поворачивает кусок влево. Квадрат не нужно вращать. Поэтому мы просто возвращаем ссылку на текущий объект.

        if (pieceShape == Tetrominoes.SquareShape)
            return this;

        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; ++i) {

            result.setX(i, y(i));
            result.setY(i, -x(i));
        }

        return result;
    }

    public Shape rotateRight() { // Этот код поворачивает кусок вправо

        if (pieceShape == Tetrominoes.SquareShape)
            return this;

        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; ++i) {

            result.setX(i, -y(i));
            result.setY(i, x(i));
        }

        return result;
    }
}
