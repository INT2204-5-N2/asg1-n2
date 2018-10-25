package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.getIcons().add(new Image("/icon/icon.png"));
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Dictionaries");
        primaryStage.setScene(new Scene(root, 1168, 610));
        primaryStage.resizableProperty().setValue(Boolean.FALSE);


        primaryStage.setOnCloseRequest(event -> {
            // Ngăn cửa sổ đóng lại
            event.consume();

            // Tắt chương trình
            shutdown(primaryStage);
        });
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }

    private void shutdown(Stage mainWindow) {
        Alert alert = new Alert(Alert.AlertType.NONE, "Bạn có chắc là muốn thoát chương trình?", ButtonType.YES, ButtonType.NO);
        if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            mainWindow.close();
        }
    }

}
