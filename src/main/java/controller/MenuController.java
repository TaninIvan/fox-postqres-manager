package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.sql.*;

public class MenuController {

    @FXML
    private Pane mousePane;

    @FXML
    private Button createDbButton;

    @FXML
    private Button logOut;

    @FXML
    private Button createNewUserButton;

    @FXML
    private Button deleteDbButton;

    @FXML
    private Button createTableButton;

    @FXML
    private Button clearTableButton;

    @FXML
    private Button findFoxNameButton;

    @FXML
    private Button addNewFoxButton;

    @FXML
    private Button updateFoxInfoButton;

    @FXML
    private Button deleteFoxButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Label currentUserLabel;

    @FXML
    private Label currentDBLabel;

    @FXML
    private TextField createDbNameField;

    @FXML
    private TextField deleteDbName;

    @FXML
    private TextField tableNameButton;

    @FXML
    private TextField foxIdField;

    @FXML
    private TextField foxNameField;

    @FXML
    private TextField ownerFioField;

    @FXML
    private TextField birthdayField;

    @FXML
    private TextField colorField;

    @FXML
    private TableView<Fox> foxTable;

    @FXML
    private TableColumn<Fox, Integer> foxIdColumn;

    @FXML
    private TableColumn<Fox, String> foxNameColumn;

    @FXML
    private TableColumn<Fox, String> ownerFioColumn;

    @FXML
    private TableColumn<Fox, Date> birthdayColumn;

    @FXML
    private TableColumn<Fox, String> colorColumn;

    private ObservableList<Fox> foxesData = FXCollections.observableArrayList();
    private ObservableList<Fox> searchData = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        this.initData();

        foxIdColumn.setCellValueFactory(new PropertyValueFactory<Fox, Integer>("id"));
        foxNameColumn.setCellValueFactory(new PropertyValueFactory<Fox, String>("name"));
        ownerFioColumn.setCellValueFactory(new PropertyValueFactory<Fox, String>("owner_fio"));
        birthdayColumn.setCellValueFactory(new PropertyValueFactory<Fox, Date>("birthday"));
        colorColumn.setCellValueFactory(new PropertyValueFactory<Fox, String>("color"));

        foxTable.setItems(foxesData);

    }

    private void initData() {
        try {
            foxesData = getData();
        } catch (Exception exception) {
            statusLabel.setTextFill(Paint.valueOf("RED"));
            statusLabel.setText(exception.getMessage());
        }
    }

    public ObservableList<Fox> getData() {
        try {
            PostqresController.connection.setAutoCommit(true);
            CallableStatement cstat1 = null;
            cstat1 = PostqresController.connection.prepareCall("SELECT * FROM fox ORDER BY id");
            ResultSet rs = cstat1.executeQuery();
            ObservableList<Fox> foxes = FXCollections.observableArrayList();
            while(rs.next()) {
                Fox fox = new Fox();
                fox.setId(rs.getInt(1));
                fox.setName(rs.getString(2));
                fox.setOwner_fio(rs.getString(3));
                fox.setBirthday(rs.getDate(4));
                fox.setColor(rs.getString(5));
                foxes.add(fox);
            }
            return foxes;
        } catch (Exception exception) {
            statusLabel.setTextFill(Paint.valueOf("RED"));
            statusLabel.setText(exception.getMessage());
            return FXCollections.observableArrayList();
        }
    }


    @FXML
    void createDB(ActionEvent event) {
        try {
            PostqresController.connection.setAutoCommit(true);
            PreparedStatement preState = null;
            preState = PostqresController.connection.prepareStatement("SELECT create_db(?)");
            preState.setString(1, createDbNameField.getText());
            preState.execute();
            preState.close();

            PostqresController.connection.close();
            PostqresController.setDbName(createDbNameField.getText());
            PostqresController.createConnection();
            PostqresController.connection.setAutoCommit(true);

            // Execute the commands in the script.sql file
            //Initialize the script runner
            ScriptRunner sr = new ScriptRunner( PostqresController.connection);
            //Creating a reader object
            Reader reader = new BufferedReader(new FileReader("src/main/resources/script.sql"));
            //Running the script
            sr.setDelimiter(";;");
            sr.runScript(reader);

            initialize();

            statusLabel.setTextFill(Paint.valueOf("BLACK"));
            statusLabel.setText("База данных '" + createDbNameField.getText() + "' успешно создана!");
        } catch (Exception exception) {
            statusLabel.setTextFill(Paint.valueOf("RED"));
            statusLabel.setText(exception.getMessage());
        }
    }

    @FXML
    void deleteDB(ActionEvent event) {
        try {
            PostqresController.connection.close();
            PostqresController.setDbName("postgres");
            PostqresController.createConnection();

            PostqresController.connection.setAutoCommit(true);
            PreparedStatement preState = null;
            preState = PostqresController.connection.prepareStatement("SELECT drop_db(?)");
            preState.setString(1, deleteDbName.getText());
            preState.execute();
            preState.close();
            initialize();
            statusLabel.setTextFill(Paint.valueOf("BLACK"));
            statusLabel.setText("База данных '" + deleteDbName.getText() + "' успешно удалена!");

        } catch (Exception exception) {
            statusLabel.setTextFill(Paint.valueOf("RED"));
            statusLabel.setText(exception.getMessage());
        }
    }

    @FXML
    public void showCurrentInfo(MouseEvent mouseEvent) {
        currentUserLabel.setText("Current user: " + PostqresController.user);
        currentDBLabel.setText("Current DB: " + PostqresController.dbName);
    }

    @FXML
    public void logOut(ActionEvent event) {
        try {
            PostqresController.connection.close();
            logOut.getScene().getWindow().hide();
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            stage.setTitle("Fox Postgres Manager");
            stage.setScene(new Scene(root, 740, 500));
            stage.show();
        } catch (Exception exception) {
            statusLabel.setTextFill(Paint.valueOf("RED"));
            statusLabel.setText(exception.getMessage());
        }
    }

    @FXML
    void createNewUser(ActionEvent event) {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/creationUser.fxml"));
            stage.setTitle("Creation of new user");
            stage.setScene(new Scene(root, 400, 250));
            stage.show();
        } catch (Exception exception) {
            statusLabel.setTextFill(Paint.valueOf("RED"));
            statusLabel.setText(exception.getMessage());
        }
    }

    @FXML
    void clearTable(ActionEvent event) {
        try {
            PostqresController.connection.setAutoCommit(true);
            PreparedStatement preState = null;
            preState = PostqresController.connection.prepareStatement("SELECT clear_table()");
            preState.execute();
            preState.close();
            initialize();
            statusLabel.setTextFill(Paint.valueOf("BLACK"));
            statusLabel.setText("Таблица успешно очищена!");
        } catch (SQLException exception) {
            statusLabel.setTextFill(Paint.valueOf("RED"));
            statusLabel.setText(exception.getMessage());
        }
    }

    @FXML
    void addNewFox(ActionEvent event) {
        try {
            PostqresController.connection.setAutoCommit(true);
            PreparedStatement preState = null;
            preState = PostqresController.connection.prepareStatement("SELECT new_fox(?,?,?,?,?)");
            preState.setInt(1, Integer.parseInt(foxIdField.getText()));
            preState.setString(2, foxNameField.getText());
            preState.setString(3, ownerFioField.getText());
            preState.setDate(4, Date.valueOf(birthdayField.getText()));
            preState.setString(5, colorField.getText());
            preState.execute();
            preState.close();
            initialize();
            statusLabel.setTextFill(Paint.valueOf("BLACK"));
            statusLabel.setText("Новая лиса добавлена!");
        } catch (Exception exception) {
            statusLabel.setTextFill(Paint.valueOf("RED"));
            statusLabel.setText(exception.getMessage());
        }
    }

    @FXML
    void deleteFoxByName(ActionEvent event) {
        try {
            PostqresController.connection.setAutoCommit(true);
            PreparedStatement preState = null;
            preState = PostqresController.connection.prepareStatement("SELECT delete_fox(?)");
            preState.setString(1, foxNameField.getText());
            preState.execute();
            preState.close();
            initialize();
            statusLabel.setTextFill(Paint.valueOf("BLACK"));
            statusLabel.setText("Лиса " + foxNameField.getText() + " удалена!");
        } catch (Exception exception) {
            statusLabel.setTextFill(Paint.valueOf("RED"));
            statusLabel.setText(exception.getMessage());
        }
    }

    @FXML
    void findFoxByName(ActionEvent event) {
        try {
            PostqresController.connection.setAutoCommit(true);
            CallableStatement cstat1 = null;
            cstat1 = PostqresController.connection.prepareCall("SELECT * FROM Fox AS f WHERE f.name = ?");
            cstat1.setString(1,foxNameField.getText());
            ResultSet rs = cstat1.executeQuery();
            ObservableList<Fox> foxes = FXCollections.observableArrayList();
            while(rs.next()) {
                Fox fox = new Fox();
                fox.setId(rs.getInt(1));
                fox.setName(rs.getString(2));
                fox.setOwner_fio(rs.getString(3));
                fox.setBirthday(rs.getDate(4));
                fox.setColor(rs.getString(5));
                foxes.add(fox);
            }
            foxesData = foxes;
            foxIdColumn.setCellValueFactory(new PropertyValueFactory<Fox, Integer>("id"));
            foxNameColumn.setCellValueFactory(new PropertyValueFactory<Fox, String>("name"));
            ownerFioColumn.setCellValueFactory(new PropertyValueFactory<Fox, String>("owner_fio"));
            birthdayColumn.setCellValueFactory(new PropertyValueFactory<Fox, Date>("birthday"));
            colorColumn.setCellValueFactory(new PropertyValueFactory<Fox, String>("color"));

            foxTable.setItems(foxesData);
            statusLabel.setTextFill(Paint.valueOf("BLACK"));
            statusLabel.setText("Лиса " + foxNameField.getText() + " найдена.");
        } catch (Exception exception) {
            statusLabel.setTextFill(Paint.valueOf("RED"));
            statusLabel.setText(exception.getMessage());
        }
    }

    @FXML
    void updateFoxInfo(ActionEvent event) {
        try {
            PostqresController.connection.setAutoCommit(true);
            PreparedStatement preState = null;
            preState = PostqresController.connection.prepareStatement("SELECT update_fox(?,?,?,?,?)");
            preState.setInt(1, Integer.parseInt(foxIdField.getText()));
            preState.setString(2, foxNameField.getText());
            preState.setString(3, ownerFioField.getText());
            preState.setDate(4, Date.valueOf(birthdayField.getText()));
            preState.setString(5, colorField.getText());
            preState.execute();
            preState.close();
            initialize();
            statusLabel.setTextFill(Paint.valueOf("BLACK"));
            statusLabel.setText("Новая лиса добавлена!");
        } catch (Exception exception) {
            statusLabel.setTextFill(Paint.valueOf("RED"));
            statusLabel.setText(exception.getMessage());
        }
    }
}


