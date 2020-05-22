package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import java.sql.Connection;

public class LoginController {

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField loginField;

    @FXML
    private TextField dbNameField;

    @FXML
    private Label statusBar;

    @FXML
    void login(ActionEvent event) {
        try {
            PostqresController postqresController =
                    new PostqresController(dbNameField.getText(),loginField.getText(),passwordField.getText());
            Connection connection = PostqresController.createConnection();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/menu.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.setTitle("Fox DB manager");
            stage.show();
            ((Node)(event.getSource())).getScene().getWindow().hide();
        } catch (Exception exception) {
            statusBar.setTextFill(Paint.valueOf("RED"));
            statusBar.setText(exception.getMessage());
        }

    }

}