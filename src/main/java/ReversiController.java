import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class ReversiController {
    final private ReversiModel model = new ReversiModel();

    // Двумерный массив для изображений (Canvas) ячеек
    final private static Canvas[][] arrayCanvas = new Canvas[8][8];

    // Надпись WHITE
    final Text white = new Text(430, 30, "WHITE: ");

    // Счет
    final Text blackScoreText = new Text(135, 30, "");
    final Text whiteScoreText = new Text(500, 30, "");

    // RESTART
    final Button restart = new Button("RESTART");

    // Надпись BLACK
    final Text black = new Text(65, 30, "BLACK: ");

    // Вертикальная нумерация клеток
    final VBox verticalNumeration = new VBox(44, new Text("8"), new Text("7"), new Text("6"),
            new Text("5"), new Text("4"), new Text("3"), new Text("2"), new Text("1"));

    // Горизонтальная нумерация клеток
    final HBox horizontalNumeration = new HBox(51, new Text("A"), new Text("B"), new Text("C"),
            new Text("D"), new Text("E"), new Text("F"), new Text("G"), new Text("H"));

    // Контейнер GridPane для хранения Canvas с нарисованными клетками
    // (визуализация игрового поля)
    final GridPane grid = new GridPane();

    // Ссылка на Wikipedia статью об игре
    final Hyperlink hyperlink = new Hyperlink("Read About Reversi In Wikipedia");

    // Указатель хода
    final Text helper = new Text(225, 60, "");

    // Визуализация окончания партии
    final Rectangle rect = new Rectangle();
    final Text text1 = new Text(232, 300, "");
    final Text text2 = new Text(160, 335, "Press RESTART to continue");

    // Создать 64 Canvas и поместить их в arrayCanvas и в gridPane
    void createCanvas() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                arrayCanvas[i][j] = new Canvas(60, 60);
                grid.add(arrayCanvas[i][j], j, i);
            }
    }

    // Задать всем параметрам начальные значения
    void start() {
        model.initialValues();
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                updateSquare(i, j, model.getValueFromArray(i, j));

        model.setPossibleMoves();
        for (Pair<Integer, Integer> p : model.getPossibleMoves())
            updateSquare(p.getKey(), p.getValue(), 3);

        rect.setVisible(false);
        text1.setVisible(false);
        text2.setVisible(false);

        helper.setText("1 player (black)");

        whiteScoreText.setText(String.valueOf(model.getWhiteScoreByte()));
        blackScoreText.setText(String.valueOf(model.getBlackScoreByte()));
    }

    // Перерисовать заданную клетку поля
    // Возможные значения mode:
    // 0 - клетка пустая
    // 1 - клетка занята черной фишкой
    // 2 - клетка занята белой фишкой
    // 3 - клетка подсвечивается (доступная для слудующего хода)
    // 4 - клетка со звездой
    private void updateSquare(int i, int j, int mode) {
        Canvas result = arrayCanvas[i][j];
        GraphicsContext picture = result.getGraphicsContext2D();

        // Если mode = 4 (звезда), то не нужно затирать имеющийся рисунок
        if (mode != 4) {
            picture.clearRect(0, 0, 60, 60);
            picture.setGlobalAlpha(1);
        }

        // Рисование пустой клетки
        picture.setStroke(Color.BLACK);
        picture.setLineWidth(1.0);
        picture.strokeRect(0, 0, 60, 60);

        // Добавление в пустую клетку черной фишки
        if (mode == 1) {
            picture.setFill(Color.BLACK);
            picture.fillOval(5, 5, 50,50);

            // Developer's signature :)
            picture.setFill(Color.WHITE);
            picture.fillText("Jeus", 15, 34);
        }

        // Добавление в пустую клетку белой фишки
        if (mode == 2) {
            picture.setFill(Color.WHITE);
            picture.fillOval(5, 5, 50,50);

            // Developer's signature :)
            picture.setFill(Color.BLACK);
            picture.fillText("Andy", 14, 34);
        }

        // Добавление в пустую клетку желтого выделения,
        // указывающего, что в эту клетку можно сходить
        if (mode == 3) {
            picture.setStroke(Color.YELLOW);
            picture.setLineWidth(2.0);
            picture.strokeRoundRect(6, 6, 48,48, 10, 10);
        }

        // Добавление в клетку полупрозрачной звезды
        if (mode == 4) {
            // Координаты вершин звезды
            double[] x = {9, 23, 30, 37, 51, 40, 45, 30, 15, 19};
            double[] y = {23, 21, 8, 21, 23, 34, 49, 42, 49, 34};
            picture.setFill(Color.GOLD);
            picture.setGlobalAlpha(0.9);
            picture.fillPolygon(x, y, 10);
        }
    }

    // Сделать ход
    void makeNextStep() {
        grid.getChildren()
                .forEach(grid ->
                        grid.setOnMouseReleased((e) -> {
                            if (e.getClickCount() == 1) {
                                Node source = (javafx.scene.Node) e.getSource();

                                int i = GridPane.getRowIndex(source);
                                int j = GridPane.getColumnIndex(source);

                                // Проверка корректности выбранной клети
                                boolean tmpFl = false;
                                for (Pair<Integer, Integer> p : model.getPossibleMoves())
                                    if (p.getKey() == i && p.getValue() == j) {
                                        tmpFl = true;
                                        break;
                                    }

                                if (tmpFl) {
                                    // Перед тем как сделать очередной ход,
                                    // нужно убрать старые желтые выделения клеток
                                    for (Pair<Integer, Integer> p : model.getPossibleMoves())
                                        updateSquare(p.getKey(), p.getValue(), 0);

                                    // Сделать ход
                                    model.analyzeAction(i, j, true, false);
                                    // Перерисовать
                                    for (Pair<Integer, Integer> p : model.getRepaintSquare())
                                        updateSquare(p.getKey(), p.getValue(),
                                                model.getValueFromArray(p.getKey(), p.getValue()));

                                    model.setPossibleMoves();
                                    if (model.getPossibleMoves().isEmpty())
                                        gameOver();
                                    else
                                        nextPlayer();
                                }
                            }
                        }));
    }

    // Выделить фишки, которыми можно завладеть, если сделать ход в клетку,
    // на которую наведена мышка
    void showNextPotentialStep() {
        for (Node g : grid.getChildren()) {
            // Наведение мыши на клетку
            g.setOnMouseEntered((e) -> {
                Node source = (javafx.scene.Node) e.getSource();

                Object row = GridPane.getRowIndex(source);
                Object column = GridPane.getColumnIndex(source);

                if (row != null && column != null) {
                    for (Pair<Integer, Integer> p1 : model.getPossibleMoves())
                        if (p1.getKey() == row && p1.getValue() == column) {
                            model.analyzeAction((int)row, (int)column, false, true);
                            for (Pair<Integer, Integer> p2 : model.getRepaintSquare())
                                updateSquare(p2.getKey(), p2.getValue(), 4);
                    }
                }
            });

            // Уход мыши из клетки
            g.setOnMouseExited((e) -> {
                List<Pair<Integer, Integer>> tmp = model.getRepaintSquare();
                for (int i = 0; i < tmp.size(); i++) {
                    int key = tmp.get(i).getKey();
                    int value = tmp.get(i).getValue();

                    // 0 элемент всегда содержит координаты клетки,
                    // которой нужно вернуть желтое выделение
                    if (i == 0)
                        updateSquare(key, value, 3);
                    else
                        updateSquare(key, value, model.getValueFromArray(key, value));
                }
            });
        }
    }

    // Перадать управление другому игроку
    private void nextPlayer() {
        // Выделить клетки, куда можно будет сходить
        for (Pair<Integer, Integer> p : model.getPossibleMoves())
            updateSquare(p.getKey(), p.getValue(), 3);

        if (model.getFl())
            helper.setText("1 player (black)");
        else
            helper.setText("2 player (white)");

        whiteScoreText.setText(String.valueOf(model.getWhiteScoreByte()));
        blackScoreText.setText(String.valueOf(model.getBlackScoreByte()));
    }

    // Сообщить о конце партии и ее результате
    private void gameOver() {
        rect.setVisible(true);
        text1.setVisible(true);
        text2.setVisible(true);
        helper.setText("---Game Over---");

        byte black = model.getBlackScoreByte();
        byte white = model.getWhiteScoreByte();

        whiteScoreText.setText(String.valueOf(white));
        blackScoreText.setText(String.valueOf(black));

        if (black > white)
            text1.setText("Black won");
        else if (white > black)
                text1.setText("White won");
            else
                text1.setText("Drawn game");
    }

    // Открыть Wikipedia статью об игре в браузере
    void openWiki() {
        try {
            Desktop.getDesktop().browse(new URI("https://en.wikipedia.org/wiki/Reversi"));
        }
        catch (URISyntaxException | IOException e) {
            hyperlink.setVisible(false);
        }
    }
}
