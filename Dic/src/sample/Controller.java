package sample;


import com.mysql.jdbc.PreparedStatement;
import com.sun.speech.freetts.VoiceManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.Window;




import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller {

    @FXML
    TextField inPut, inputword, inputexplain, inputdelete, inputeditword, inputeditexplain;
    @FXML
    Button translate;
    @FXML
    Button insertbutton;
    @FXML
    ListView listView;
    @FXML
    WebView webView;
    @FXML
    AnchorPane pane;
    @FXML
    Button Speak, deletebutton;
    @FXML
    Button Translate,Insert, Delete, Edit;
    @FXML
    AnchorPane panetranslate, paneinsert, paneedit, panedelete;



    public String timTu(String a) throws SQLException {
        String c="";
        Statement statement = SQLConnect.getConnection().createStatement();
        String Sql = "SELECT * FROM tbl_edict WHERE word = '"+a+"'";
        ResultSet rs = statement.executeQuery(Sql);
        if(rs.next())
            return (rs.getString("detail"));
        else return "";
    }

    public void inputfrkb() {
        // set sự kiện cho phím Enter
        inPut.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                String text = inPut.getText();
                text = text.toLowerCase();
                if ("".equals(text)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("FBI WARNING ");
                    alert.setHeaderText("                       Từ chưa được nhập");
                    alert.setContentText("Có vẻ như bạn chưa nhập từ");
                    alert.show();
                }else {
                    try {
                        if (timTu(text).equals("")) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("ERROR");
                            alert.setHeaderText("                TỪ VỪA NHẬP KHÔNG HỢP LỆ!");
                            alert.setContentText("Bạn vừa nhập sai từ hoặc bạn vừa cào phím");
                            alert.show();
                        }
                        else webView.getEngine().loadContent(timTu(text));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }



    //hàm hiện từ lên listview và gợi ý từ tìm kiếm
    public void searchsuggest() throws SQLException {
        int i=1;
        ArrayList<String> DS1 = new ArrayList();
        Statement statement = SQLConnect.getConnection().createStatement();
        String Sql = "SELECT * FROM tbl_edict";
        ResultSet rs = statement.executeQuery(Sql);
        while(rs.next()){
            DS1.add(rs.getString("word"));
        }
        if (i==1) {
            ObservableList<String> listWord = FXCollections.observableArrayList(DS1);
            FilteredList<String> filteredData = new FilteredList<>(listWord, s -> true);
            listView.setItems(filteredData);
            i++;

            inPut.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(s -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String tolower = newValue.toLowerCase();
                    if (s.toLowerCase().startsWith(tolower)) {
                        return true;
                    }
                    return false;
                });
                listView.setItems(filteredData);
            });
        }
    }

    public void clickinlist() throws SQLException{

        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent click) {

                if (click.getClickCount() == 1) {
                    //Use ListView's getSelected Item
                    //currentItemSelected = listView.getSelectionModel().getSelectedItem();//use this to do whatever you want to. Open Link etc.
                    String text = (String) listView.getSelectionModel().getSelectedItem();
                    try {
                        inPut.setText(text);
                        webView.getEngine().loadContent(timTu(text));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });}


    public void Submit(ActionEvent event) throws SQLException {

        String text;
        if (event.getSource() == translate) {
            //bấm Phím dịch
            text = inPut.getText();
            text = text.toLowerCase();      // chuyển về chữ thường

            if ("".equals(text)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("FBI WARNING ");
                alert.setHeaderText("                       TỪ CHƯA ĐƯỢC NHẬP!");
                alert.setContentText("Bạn chưa nhập từ vào");
                alert.show();
            } else if (timTu(text).equals("")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("                TỪ VỪA NHẬP KHÔNG HỢP LỆ!");
                alert.setContentText("Bạn nhập sai hoặc là bạn vừa cào phím");
                alert.show();
            } else webView.getEngine().loadContent(timTu(text));
        }

    }
    public void Click(ActionEvent event)throws SQLException{
        if(event.getSource() == Translate) {
            panetranslate.toFront();
            searchsuggest();

        }
        else if(event.getSource() == Insert ) {
            paneinsert.toFront();
        }
        else if(event.getSource() == Edit ) {
            paneedit.toFront();
        }
        else if(event.getSource() == Delete ) {
            panedelete.toFront();
        }

    }
    public boolean CheckWordInDataBase(String word) throws SQLException {
        String c="";
        Statement statement = SQLConnect.getConnection().createStatement();
        String Sql = "SELECT * FROM tbl_edict WHERE word = '"+word+"'";
        ResultSet rs = statement.executeQuery(Sql);
        if(rs.next())
            return true;
        else return false;
    }

        public void SetInsertButton(ActionEvent event) throws SQLException {
        Statement statement = SQLConnect.getConnection().createStatement();

        String word = inputword.getText();
        String detail = inputexplain.getText();
        word.toLowerCase();
        detail.toLowerCase();
            System.out.println(word);
        if(!CheckWordInDataBase(word)) {
            try {
                String sql = "INSERT INTO tbl_edict(word, detail) VALUES ('"+word+"', '"+detail+"')";
                Alert alert = new Alert(Alert.AlertType.NONE, "BẠN CÓ CHẮC THÊM TỪ!", ButtonType.YES, ButtonType.NO);
                if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                    statement.executeUpdate(sql);

                    Alert confirm  = new Alert(Alert.AlertType.INFORMATION);
                    confirm.setTitle("THÔNG BÁO");
                    confirm.setHeaderText("                     ĐÃ THÊM");
                    confirm.setContentText("*WARNING: FBI");
                    confirm.show();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("THÔNG BÁO");
            alert.setHeaderText("                       TỪ BẠN NHẬP ĐÃ TỒN TẠI!");
            alert.setContentText("*WARNING: FBI");
            alert.show();
        }
        }
        public void SetDeleteButton(ActionEvent event) throws SQLException {
            Statement statement = SQLConnect.getConnection().createStatement();

            String word = inputdelete.getText();

            if(CheckWordInDataBase(word)) {

                try {
                    String sql = "DELETE  FROM tbl_edict WHERE word = '"+ word+"'";
                    Alert alert = new Alert(Alert.AlertType.NONE, "BẠN CÓ CHẮC MUỐN XÓA TỪ!", ButtonType.YES, ButtonType.NO);
                    if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                        statement.executeUpdate(sql);
                    }

                    Alert delete = new Alert(Alert.AlertType.WARNING);
                    delete.setTitle("THÔNG BÁO");
                    delete.setHeaderText("               ĐÃ XÓA!");
                    delete.setContentText("*WARNING: FBI");
                    delete.show();

                }
                catch (Exception e){
                    e.printStackTrace();
                }


            }
            else{
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("THÔNG BÁO");
                alert.setHeaderText("               TỪ KHÔNG CÓ SẴN!");
                alert.show();
            }
        }
        public void SetEditButton(ActionEvent event) throws SQLException {
            Statement statement = SQLConnect.getConnection().createStatement();
            String word = inputeditword.getText();
            String detail = inputeditexplain.getText();

            if(CheckWordInDataBase(word)){
                String sql = "UPDATE tbl_edict set detail = '"+detail+"' WHERE word = '"+word+"'";
                Alert alert = new Alert(Alert.AlertType.NONE, "BẠN CÓ SỬA TỪ!", ButtonType.YES, ButtonType.NO);
                if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                    statement.executeUpdate(sql);
                    Alert delete = new Alert(Alert.AlertType.WARNING);
                    delete.setTitle("THÔNG BÁO");
                    delete.setHeaderText("               ĐÃ SỬA!");
                    delete.show();
                }

            }
            else{
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("THÔNG BÁO");
                alert.setHeaderText("               TỪ KHÔNG CÓ SẴN!");

                alert.show();
            }
        }



    private void buttonClicked(){
        String message = "";
        ObservableList<String> movies;
        movies = listView.getSelectionModel().getSelectedItems();

        for(String m:movies) {
            message += m + "\n";
        }
        try {
            timTu(message);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void  Voice(ActionEvent event )  {
        VoiceManager voiceManager = VoiceManager.getInstance();

        com.sun.speech.freetts.Voice syntheticVoice = voiceManager.getVoice("kevin16");
        syntheticVoice.allocate();
        String text = inPut.getText();
        syntheticVoice.speak(text);
        syntheticVoice.deallocate();
    }

}



