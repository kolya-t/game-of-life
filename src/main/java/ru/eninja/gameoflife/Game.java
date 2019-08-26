package ru.eninja.gameoflife;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by eninja on 29.04.2015.
 */
public class Game {
    private int             width;      // Ширина поля
    private int             height;     // Высота поля
    private Color           bgColor;    // Цвет фона
    private Color           cellColor;  // Цвет клеток
    private HashSet<Cell>   living;     // Список живых клеток
    private HashSet<Cell>   gestating;  // Список клеток, готовых к возрождению
    private int             delay;      // Задержка между кадрами

    private int             resX;       // Количество пикселей по ширине
    private int             resY;       // Количество пикселей по высоте
    private int             cx;         // Центр по ширине
    private int             cy;         // Центр по высоте
    private double          zoom;       // Коэффициент масштабирования

    private double          fps;        // FPS
    private long            genTime;    // Время просчета текущего кадра
    private long            drawTime;   // Время отрисовки текущего кадра

    public Game (int resX, int resY, Color bgColor, Color cellColor) {
        this.resX = resX;
        this.resY = resY;
        this.bgColor = bgColor;
        this.cellColor = cellColor;
        living = new HashSet<>();
        gestating = new HashSet<>();
        this.delay = 0;
    }

    /* Мотод, запускающий игру */
    public void start() {
        StdDraw.setCanvasSize(resX, resY);
        randInit();
//        loadCells(".\\caterpillar.rle");
//        loadCells(".\\Unnamed_Blinker_Ship_1.rle");
//        loadCells(".\\achims_p144.rle");
//        loadCells(".\\lala.rle");

        setZoom(1);
        while (true) {
            draw();
            keyChecker();
            generateNext();
        }
    }

    private void draw() {
        long startTime = System.currentTimeMillis();
        StdDraw.show(delay);
        StdDraw.clear(bgColor);
        StdDraw.setPenColor(cellColor);

        for (Cell cell : living) {
//            StdDraw.filledCircle(cell.getX() + cx, cell.getY() + cy, 0.45);
            StdDraw.filledSquare(cell.getX() + cx, cell.getY() + cy, 0.45);
//            StdDraw.point(cell.getX() + cx, cell.getY() + cy);
        }

        drawTime = System.currentTimeMillis() - startTime;
        fps = 1000. / (genTime + drawTime);
        // Вывод информации (FPS, время просчета кадра, время отрисовки кадра)
        StdDraw.setPenColor(bgColor);
        StdDraw.filledRectangle(0, 0, 230 * zoom, 50 * zoom);
        StdDraw.setPenColor(cellColor);
        StdDraw.textLeft(10 * zoom, 40 * zoom, "FPS: " + (fps > 5 ? (int) fps : fps));
        StdDraw.textLeft(10 * zoom, 20 * zoom, "Gen: " +
                (genTime > 5000 ? (genTime / 1000) + "s" : genTime + "ms"));
        StdDraw.textLeft(110 * zoom, 20 * zoom, "Draw: " +
                (drawTime > 5000 ? (drawTime / 1000) + "s" : drawTime + "ms"));

        StdDraw.show();
    }

    private void loadCells(String filename) {
        long startTime = System.currentTimeMillis();
        try {
//            Reader reader = new FileReader(filename);
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            System.out.println("File can load a very long time. Please wait");
            // Чтение комментариев
            int rd;
            while (true) {
                rd = reader.read();
                if (rd == '#') {
                    while (reader.read() != '\n') continue; // Пропустить строку
                } else {
                    break;
                }
            }

            /* Чтение правил */
            int[] size = new int[2];
            for (int i = 0; i < 2; i++) {
                // Пропуск до числа
                while (true) {
                    if (rd >= '0' && rd <= '9') {
                        break;
                    } else {
                        rd = reader.read();
                    }
                }
                // Запись числа
                while (rd >= '0' && rd <= '9') {
                    size[i] = size[i] * 10 + (rd - '0');
                    rd = reader.read();
                }
            }
            width = size[0];
            height = size[1];
            // Если строка не закончилась - пропустить до конца
            while (rd != '\n') {
                rd = reader.read();
            }

            /* Обработка вселенной */
            int x = 0;
            int y = 0;
            int num = 0;
            do {
                rd = reader.read();
                if (rd >= '0' && rd <='9') {
                    num = num * 10 + (rd - '0');
                    continue;
                }
                if (num == 0) {
                    num = 1;
                }
                switch (rd) {
                    case 'b':
                        x += num;
                        break;
                    case 'o':
                        for (int j = 0; j < num; j++) {
                            living.add(new Cell(x++, y));
                        }
                        break;
                    case '$':
                        y += num;
                        x = 0;
                        break;
                }
                num = 0;
            } while (rd != '!');

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            System.exit(1);
        }
        genTime = System.currentTimeMillis() - startTime;
        System.out.println("File has been successfully loaded in " +
                genTime + " ms");
    }

    private void setZoom(double zoom) {
        this.zoom = zoom;
        StdDraw.setXscale(0, resX * zoom - 1);
        StdDraw.setYscale(resY * zoom - 1, 0);
        cx = (int)(resX * zoom - width) / 2;
        cy = (int)(resY * zoom - height) / 2;
    }

    private void randInit() {
        width = 4000;
        height = 3000;

        long startTime = System.currentTimeMillis();
        Random random = new Random();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (random.nextDouble() % 1.0 < 0.03) {
                    living.add(new Cell(x, y));
                }
            }
        }
        genTime = System.currentTimeMillis() - startTime;
    }

    private void generateNext() {
        long startTime = System.currentTimeMillis();
        // Подсчет соседей всех живых клеток;
        Cell neighb = new Cell(0, 0);
        for (Cell cell : living) {
            cell.neighbCount = 0;
            for (int x = -1; x < 2; x++) {
                neighb.setX(cell.getX() + x);
                for (int y = -1; y < 2; y++) {
                    if (x == 0 && y == 0) continue; // Пропуск самого себя
                    neighb.setY(cell.getY() + y);
                    if (living.contains(neighb)) {
                        cell.neighbCount++;
                    } else if (!gestating.contains(neighb)) {
                        gestating.add(new Cell(neighb.getX(), neighb.getY()));
                    }
                }
            }
        }

        // Подсчет соседей у gestating клеток
//        iterator = gestating.iterator();
        for (Cell cell : gestating) {
            cell.neighbCount = 0;
            for (int x = -1; x < 2; x++) {
                neighb.setX(cell.getX() + x);
                for (int y = -1; y < 2; y++) {
                    if (x == 0 && y == 0) continue; // Пропуск самого себя
                    neighb.setY(cell.getY() + y);
                    if (living.contains(neighb)) {
                        cell.neighbCount++;
                    }
                }
            }
        }

        // Проход по живым и решение что с ними сделать
        Iterator<Cell> iterator = living.iterator();
        while (iterator.hasNext()) {
            Cell cell = iterator.next();
            if (cell.neighbCount < 2 || cell.neighbCount > 3) {
                iterator.remove();
            }
        }

        // Проход по gestating клеткам и решение что с ними сделать
        for (Cell cell : gestating) {
            if (cell.neighbCount == 3) {
                living.add(cell);
            }
        }
        gestating.clear();

        genTime = System.currentTimeMillis() - startTime;
    }

    private void keyChecker() {
        if (StdDraw.hasNextKeyTyped()) {
            int key = StdDraw.nextKeyTyped();
            switch (key) {
                // Выход - ESC
                case 27:
                    System.exit(0);
                    break;

                /* Масштабирование */
                // Q - zoom out
                case  113:
                    setZoom(zoom * 2);
                    break;
                // E - zoom in
                case 101:
                    setZoom(zoom / 2);
                    break;

                /* Движение по полю - WASD */
                // A
                case 97:
                    cx += 20 * zoom;
                    break;
                // D
                case 100:
                    cx -= 20 * zoom;
                    break;
                // W
                case 119:
                    cy += 20 * zoom;
                    break;
                // S
                case 115:
                    cy -= 20 * zoom;
                    break;
            }
        }
    }
}
