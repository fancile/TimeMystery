// Maya Babu, mbab162, 659352321
// Importing needed package.
package nz.ac.auckland.se206;

// Importing needed libraries.
import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.InputEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * This is the entry point of the JavaFX application. This class initializes and runs the JavaFX
 * application.
 */
public class App extends Application {

  private static Scene scene;
  private static StackPane sceneContainer;
  private static Rectangle blackout;
  private static FadeTransition activeTransition;
  private static boolean transitioning = false;

  /**
   * The main method that launches the JavaFX application.
   *
   * @param args the command line arguments
   */
  public static void main(final String[] args) {
    launch();
  }

  /**
   * Changes the displayed scene with a fade through black.
   *
   * @param fxml the name of the FXML file (without extension)
   * @throws IOException retained for compatibility with existing callers;
   *     asynchronous loading failures are handled inside the transition
   */
  public static void setRoot(String fxml) throws IOException {
    // A newer navigation request replaces the current transition.
    if (activeTransition != null) {
      activeTransition.stop();
    }

    transitioning = true;
    blackout.setMouseTransparent(false);

    activeTransition = new FadeTransition(Duration.millis(250), blackout);
    activeTransition.setFromValue(blackout.getOpacity());
    activeTransition.setToValue(1.0);

    activeTransition.setOnFinished(
        event -> {
          try {
            // Load only when the screen is black.
            Parent nextRoot = loadFxml(fxml);
            sceneContainer.getChildren().set(0, nextRoot);

            revealScene(nextRoot, null);
          } catch (IOException | RuntimeException exception) {
            // Keep the previous scene visible if loading fails.
            exception.printStackTrace();

            Parent previousRoot = (Parent) sceneContainer.getChildren().get(0);

            revealScene(
                previousRoot,
                () -> {
                  Alert alert = new Alert(Alert.AlertType.ERROR);
                  alert.initOwner(scene.getWindow());
                  alert.setTitle("Unable to open scene");
                  alert.setHeaderText("The next scene could not be loaded.");
                  alert.setContentText("Please try again.");
                  alert.show();
                });
          }
        });

    activeTransition.play();
  }

  /** Fades away the black overlay and restores interaction. */
  private static void revealScene(Parent root, Runnable afterReveal) {
    activeTransition = new FadeTransition(Duration.millis(350), blackout);
    activeTransition.setFromValue(blackout.getOpacity());
    activeTransition.setToValue(0.0);

    activeTransition.setOnFinished(
        event -> {
          transitioning = false;
          blackout.setMouseTransparent(true);
          activeTransition = null;
          root.requestFocus();

          if (afterReveal != null) {
            afterReveal.run();
          }
        });

    activeTransition.play();
  }

  /**
   * Loads the FXML file and returns the associated node. The method expects that the file is
   * located in "src/main/resources/fxml".
   *
   * @param fxml the name of the FXML file (without extension)
   * @return the root node of the FXML file
   * @throws IOException if the FXML file is not found
   */
  private static Parent loadFxml(final String fxml) throws IOException {
    return new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml")).load();
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "intro" scene.
   *
   * @param stage the primary stage of the application
   * @throws IOException if the "src/main/resources/fxml/intro.fxml" file is not found
   */
  @Override
  public void start(final Stage stage) throws IOException {
    Parent root = loadFxml("intro");
    // The overlay is unmanaged so it does not affect window sizing.
    blackout = new Rectangle();
    blackout.setFill(Color.BLACK);
    blackout.setOpacity(0.0);
    blackout.setManaged(false);
    blackout.setMouseTransparent(true);

    sceneContainer = new StackPane(root, blackout);
    scene = new Scene(sceneContainer);

    blackout.widthProperty().bind(sceneContainer.widthProperty());
    blackout.heightProperty().bind(sceneContainer.heightProperty());

    // Block mouse and keyboard input while a transition is running.
    scene.addEventFilter(
        InputEvent.ANY,
        event -> {
          if (transitioning) {
            event.consume();
          }
        });

    scene.getStylesheets().add(App.class.getResource("/css/style.css").toExternalForm());

    stage.setScene(scene);
    stage.show();
    root.requestFocus();
  }
}
