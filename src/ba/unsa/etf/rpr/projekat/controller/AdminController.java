package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.data.Admin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminController {
    public Button adminTableBtn;
    public Button childTableBtn;
    public Button parentTableBtn;
    public Button classroomTableBtn;
    public Button teacherTableBtn;
    public Label labelWelcomeAdmin;
    private Admin activeAdmin;

    public AdminController(Admin admin) {
        activeAdmin = admin;
    }

    @FXML
    public void initialize() {
        labelWelcomeAdmin.setText("Welcome back, " + activeAdmin.toString());
    }

    public void openParentTable(ActionEvent actionEvent) {
        Stage stage = new Stage();
        Parent root = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/parentTable.fxml"));
        ParentTableController tableController = new ParentTableController();
        loader.setController(tableController);
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Parent table");
        stage.setScene(new Scene(root, 700, 400));
        stage.setResizable(false);
        stage.show();
    }
    public void openClassroomTable(ActionEvent actionEvent) {
        Stage stage = new Stage();
        Parent root = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/classroomTable.fxml"));
        ClassroomTableController tableController = new ClassroomTableController();
        loader.setController(tableController);
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Classroom table");
        stage.setScene(new Scene(root, 700, 400));
        stage.setResizable(false);
        stage.show();
    }
    public void openChildTable(ActionEvent actionEvent) {
        Stage stage = new Stage();
        Parent root = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/childTable.fxml"));
        ChildTableController tableController = new ChildTableController();
        loader.setController(tableController);
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Child table");
        stage.setScene(new Scene(root, 700, 400));
        stage.setResizable(false);
        stage.show();
    }
    public void openAdminTable(ActionEvent actionEvent) {
        Stage stage = new Stage();
        Parent root = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/adminTable.fxml"));
        AdminTableController tableController = new AdminTableController();
        loader.setController(tableController);
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Admin table");
        stage.setScene(new Scene(root, 700, 400));
        stage.setResizable(false);
        stage.show();
    }
    public void openTeacherTable(ActionEvent actionEvent) {
        Stage stage = new Stage();
        Parent root = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/teacherTable.fxml"));
        TeacherTableController tableController = new TeacherTableController();
        loader.setController(tableController);
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Teacher table");
        stage.setScene(new Scene(root, 700, 400));
        stage.setResizable(false);
        stage.show();
    }

}
