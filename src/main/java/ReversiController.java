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

public class ReversiController {
    private ReversiModel model = new ReversiModel();

    // Двумерный массив для изображений (Canvas) ячеек
    private final static Canvas[][] arrayCanvas = new Canvas[8][8];

    // Визуализация надписи WHITE
    final Text white = new Text(430, 30, "WHITE: ");

    // Визуализация счета
    final Text blackScoreText = new Text(135, 30, "");
    final Text whiteScoreText = new Text(500, 30, "");

    // Визуализация кнопки RESTART
    final Button restart = new Button("RESTART");

    // Визуализация надписи BLACK
    final Text black = new Text(65, 30, "BLACK: ");

    // Взуализация вертикальной нумерации клеток
    final VBox verticalNumeration = new VBox(44, new Text("8"), new Text("7"), new Text("6"),
            new Text("5"), new Text("4"), new Text("3"), new Text("2"), new Text("1"));

    // Взуализация горизонтальной нумерации клеток
    final HBox horizontalNumeration = new HBox(51, new Text("A"), new Text("B"), new Text("C"),
            new Text("D"), new Text("E"), new Text("F"), new Text("G"), new Text("H"));

    // Контейнер GridPane для хранения Canvas с нарисованными клетками
    // (визуализация игрового поля)
    final GridPane grid = new GridPane();

    // Ссылка на Wikipedia статью об игре
    final Hyperlink hyperlink = new Hyperlink("Read About Reversi In Wikipedia");

    // Визуализация указателя хода
    final Text helper = new Text(220, 60, "");

    // Визуализация окнчания партии
    final Rectangle rect = new Rectangle();
    final Text text = new Text(100, 320, "Game Over. Press RESTART to continue");

    void forRestart() {
        model.initialValues();
        for (Pair<Integer, Integer> p : model.repaintSquare)
            updateSquare(p.getKey(), p.getValue(), model.array[p.getKey()][p.getValue()]);

        model.setPossibleMoves();
        for (Pair<Integer, Integer> p : model.possibleMoves)
            updateSquare(p.getKey(), p.getValue(), 3);

        // Скрытие сообщения о завершении партии
        rect.setVisible(false);
        text.setVisible(false);

        // Установка подсказки, кто ходит
        helper.setText("1 player (black)");

        // Отображение начального счета
        whiteScoreText.setText(String.valueOf(model.getWhiteScoreByte()));
        blackScoreText.setText(String.valueOf(model.getBlackScoreByte()));
    }

    // Перерисовывает заданную клетки поля; mode:
    // 0 - клетка пустая
    // 1 - клетка занята черной фишкой
    // 2 - клетка занята белой фишкой
    // 3 - клетка подсвечивается (доступная для слудующего хода)
    // 4 - клетка со звездой
    private void updateSquare(int i, int j, int mode) {
        Canvas result = arrayCanvas[i][j];

        // Флаг = true, если очередной элемент arrayCanvas еще не создан
        boolean tmpFl = result == null;
        if (tmpFl) result = new Canvas(60, 60);

        GraphicsContext picture = result.getGraphicsContext2D();

        // Очистка прежнего изображеия
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
            picture.fillText("Jeus", 14, 34);
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
            picture.strokeRoundRect(7, 7, 45,45, 10, 10);
        }

        // Добавление в пустую клетку звезды
        if (mode == 4) {
            // Координаты вершин звезды
            double[] x = {9, 23, 30, 37, 51, 40, 45, 30, 15, 19};
            double[] y = {23, 21, 8, 21, 23, 34, 49, 42, 49, 34};
            picture.setFill(Color.GOLD);
            picture.setGlobalAlpha(0.9);
            picture.fillPolygon(x, y, 10);
        }

        // Присваение перерисованной Canvas
        arrayCanvas[i][j] = result;

        // Если это первая отрисовка поля, то добавляем Canvas'ы в grid
        if (tmpFl) grid.add(arrayCanvas[i][j], j, i);
    }

    // Нахождение координат выбранной игроком клетки grid
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
                                for (Pair<Integer, Integer> p : model.possibleMoves)
                                    if (p.getKey() == i && p.getValue() == j) {
                                        tmpFl = true;
                                        break;
                                    }

                                if (tmpFl) {
                                    // Перед тем как сделать очередной ход,
                                    // нужно убрать старые желтые выделения клеток
                                    for (Pair<Integer, Integer> p : model.possibleMoves)
                                        updateSquare(p.getKey(), p.getValue(), 0);

                                    model.analyzeAction(i, j, true, false);

                                    for (Pair<Integer, Integer> p : model.repaintSquare)
                                        updateSquare(p.getKey(), p.getValue(), model.array[p.getKey()][p.getValue()]);

                                    model.setPossibleMoves();
                                    if (!model.possibleMoves.isEmpty()) {
                                        for (Pair<Integer, Integer> p : model.possibleMoves)
                                            updateSquare(p.getKey(), p.getValue(), 3);

                                        if (model.fl)
                                            helper.setText("1 player (black)");
                                        else
                                            helper.setText("2 player (white)");

                                        whiteScoreText.setText(String.valueOf(model.getWhiteScoreByte()));
                                        blackScoreText.setText(String.valueOf(model.getBlackScoreByte()));
                                    } else gameOver();
                                }
                            }
                        }));
    }

    // Обработчики событий наведения и удаления мыши с клетки
    void showNextPotentialStep() {
        for (Node g : grid.getChildren()) {
            // Приход мыши в клетку
            g.setOnMouseEntered((e) -> {
                Node source = (javafx.scene.Node) e.getSource();

                Object row = GridPane.getRowIndex(source);
                Object column = GridPane.getColumnIndex(source);
                if (row != null && column != null) {
                    boolean tmpFl = false;
                    for (Pair<Integer, Integer> p : model.possibleMoves)
                        if (p.getKey() == row && p.getValue() == column) {
                            tmpFl = true;
                            break;
                        }

                    if (tmpFl) {
                        model.analyzeAction((int)row, (int)column, false, true);
                        for (Pair<Integer, Integer> p : model.repaintSquare)
                            updateSquare(p.getKey(), p.getValue(), 4);
                    }
                }
            });

            // Уход мыши из клетки
            g.setOnMouseExited((e) -> {
                for (Pair<Integer, Integer> p : model.repaintSquare)
                    updateSquare(p.getKey(), p.getValue(), model.array[p.getKey()][p.getValue()]);
            });
        }
    }

    // Сообщает о конце партии и ее результате
    private void gameOver() {
        rect.setVisible(true);
        text.setVisible(true);

        byte black = model.getBlackScoreByte();
        byte white = model.getWhiteScoreByte();

        if (black > white) {
            helper.setText("1 player (black) won");
            return;
        }
        if (white > black) {
            helper.setText("2 player (white) won");
            return;
        }

        helper.setText("Drawn game");
    }

    // Открытие Wikipedia статьи об игре в браузере
    void openWiki() {
        try {
            Desktop.getDesktop().browse(new URI("https://en.wikipedia.org/wiki/Reversi"));
        }
        catch (URISyntaxException | IOException e) {
            hyperlink.setVisible(false);
        }
    }
}
