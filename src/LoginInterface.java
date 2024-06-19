import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.*;

public class LoginInterface extends Application {

    private TextField userTextField;
    private PasswordField pwBox;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("ExplDemo");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        // Header with logo or application name
        Label headerLabel = new Label("Login");
        headerLabel.setId("headerLabel");
        grid.add(headerLabel, 0, 0, 2, 1);

        // User icon and field
        ImageView userIcon = new ImageView(new Image(getClass().getResourceAsStream("icon/user-icon.png")));
        userIcon.setFitHeight(20);
        userIcon.setFitWidth(20);
        userIcon.setTranslateX(10);
        userTextField = new TextField();
        userTextField.setPromptText("Enter username");
        userTextField.setPrefHeight(40);
        StackPane userFieldWithIcon = new StackPane();
        userFieldWithIcon.getChildren().addAll(userTextField, userIcon);
        StackPane.setAlignment(userIcon, Pos.CENTER_LEFT);
        grid.add(userFieldWithIcon, 0, 1);

        // Lock icon and field
        ImageView lockIcon = new ImageView(new Image(getClass().getResourceAsStream("icon/lock-icon.png")));
        lockIcon.setFitHeight(20);
        lockIcon.setFitWidth(20);
        lockIcon.setTranslateX(10);
        pwBox = new PasswordField();
        pwBox.setPromptText("Enter password");
        pwBox.setPrefHeight(40);
        StackPane pwFieldWithIcon = new StackPane();
        pwFieldWithIcon.getChildren().addAll(pwBox, lockIcon);
        StackPane.setAlignment(lockIcon, Pos.CENTER_LEFT);
        grid.add(pwFieldWithIcon, 0, 2);

        // Login button
        Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 0, 4);

        // Footer text
        Label footerLabel = new Label("© 2024 My Application. All rights reserved.");
        footerLabel.setId("footerLabel");
        grid.add(footerLabel, 0, 7, 2, 1);

        Scene scene = new Scene(grid, 400, 350);
        primaryStage.setScene(scene);

        // Fade In Animation for the grid
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(2), grid);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // Shake Animation for incorrect credentials
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), grid);
        shake.setFromX(0);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);

        // Trigger the shake animation if login fails
        btn.setOnAction(event -> {
            if (!validateCredentials(userTextField.getText(), pwBox.getText())) {
                shake.playFromStart();
            }else{
		new LoadingInterface().start(new Stage()); // Open the main interface in a new window
            	primaryStage.close();
	    }
        });

        // Linking the external CSS file
        scene.getStylesheets().add(getClass().getResource("css/style.css").toExternalForm());

        primaryStage.show();
    }

    // Method to validate credentials against MySQL database
    private boolean validateCredentials(String username, String password) {
        final String jdbcUrl = "jdbc:mysql://127.0.0.1:3307/expldemo";
        final String dbUser = "root";
        final String dbPassword = "";

        String sql = "SELECT * FROM users WHERE UserName = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // User exists with the provided username and password
                return true;
            }
        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.println(e);
        }
	return false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
