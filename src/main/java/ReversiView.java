import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ReversiView extends Application {
    public static void main(String[] args) { Application.launch(args); }

    @Override
    public void start(Stage stage) {
        final ReversiController controller = new ReversiController();

        // Установка иконки приложения
        stage.getIcons().add(new Image("icon.png"));

        // Настройка окна
        stage.setHeight(630);
        stage.setWidth(580);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.setTitle("Reversi");

        // Настройка визуализации надписи "WHITE"
        controller.white.setFont(Font.font(null, 18));

        // Настройка визуализации счета игры
        controller.whiteScoreText.setFont(Font.font(null, 18));
        controller.blackScoreText.setFont(Font.font(null, 18));

        // Настройка визуализации кнопки RESTART
        controller.restart.setLayoutX(250);
        controller.restart.setLayoutY(10);

        // Настройка визуализации надписи "BLACK"
        controller.black.setFont(Font.font(null, 18));

        // Настройка визуализации вертикальной нумерации клеток
        controller.verticalNumeration.setLayoutX(30);
        controller.verticalNumeration.setLayoutY(90);

        // Настройка визуализации горизонтальной нумерации клеток
        controller.horizontalNumeration.setLayoutX(75);
        controller.horizontalNumeration.setLayoutY(560);

        // Настройка визуализации игрового поля
        controller.grid.setLayoutX(50);
        controller.grid.setLayoutY(70);
        controller.grid.setGridLinesVisible(true);

        // Настройка визуализации гиперссылки Read About Reversi In Wikipedia
        controller.hyperlink.setLayoutX(185);
        controller.hyperlink.setLayoutY(575);
        controller.hyperlink.setStyle("-fx-text-fill:black;");
        controller.hyperlink.setUnderline(true);

        // Настройка визуализации указателя хода
        controller.helper.setFont(Font.font(null,17));

        // Настройка визуализация окончания партии
        controller.rect.setX(130);
        controller.rect.setY(270);
        controller.rect.setWidth(320);
        controller.rect.setHeight(80);
        controller.rect.setFill(Color.GREENYELLOW);
        controller.text1.setFont(Font.font(null, 20));
        controller.text2.setFont(Font.font(null, 20));

        // Добавление всех элементов в окно
        final Group root = new Group(controller.white,
                controller.whiteScoreText, controller.restart,
                controller.black, controller.blackScoreText,
                controller.horizontalNumeration, controller.verticalNumeration,
                controller.grid, controller.helper, controller.hyperlink,
                controller.rect, controller.text1, controller.text2);
        final Scene scene = new Scene(root, Color.LIGHTSEAGREEN);
        stage.setScene(scene);

        // Начало игры
        controller.createCanvas();
        controller.start();

        // Показ окна
        stage.show();

        // Перезапуск игры
        controller.restart.setOnMouseClicked((e) -> controller.start());

        // Открытие Wikipedia статьи об игре
        controller.hyperlink.setOnMouseClicked((e) -> controller.openWiki());

        // Нажатие мыши на grid
        controller.grid.setOnMousePressed((e) -> controller.makeNextStep());

        // Наведение (уход) мыши на (с) grid
        controller.showNextPotentialStep();
    }
}
