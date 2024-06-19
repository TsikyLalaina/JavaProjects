import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.application.Platform;

public class LoadingInterface extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.initStyle(StageStyle.UNDECORATED); // Remove window decorations
        primaryStage.setTitle("Loading...");

        StackPane root = new StackPane();
        root.setAlignment(Pos.CENTER);
        //root.setStyle("-fx-background-color: #F3F3F3;"); // Set background color

        // Create a progress indicator
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setStyle("-fx-progress-color: #39424e;"); // Customize color

        root.getChildren().add(progressIndicator);

        Scene scene = new Scene(root, 500, 300);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Simulate a loading process on a separate thread
        Thread loadingThread = new Thread(() -> {
            try {
                // Simulate some loading tasks
                Thread.sleep(2000); // Replace with actual tasks

                // Update UI on the JavaFX Application Thread
                Platform.runLater(() -> {
                    progressIndicator.setProgress(1); // Update progress
                    primaryStage.close();
                    // Open the main application window here
                    new MainInterface().start(new Stage());
                });
            } catch (InterruptedException e) {
                //e.printStackTrace();
                System.out.println(e);
            }
        });

        loadingThread.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
