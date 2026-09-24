package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;

public class TravelController {
  @FXML private AnchorPane beforeAnchorPane;
  @FXML private AnchorPane afterAnchorPane;
  @FXML private AnchorPane prepAnchorPane;
  @FXML private Label lblTimer;

  private GameStateContext context =
      GameStateContext.getCurrentContext(); // Reference to the GameStateContext

  @FXML
  public void initialize() {
    // Initialize the travel scene, set up any necessary data or UI elements
    // continue the timer
    bindTimer();
  }

  @FXML
  public void OnClicked(MouseEvent event) throws IOException {
    // Handle rectangle click events for travel scene
    switch (((AnchorPane) event.getSource()).getId()) {
      case "beforeAnchorPane":
        // Handle beforeAnchorPane click, jump to before scene
        App.setRoot("Epoch1");
        break;
      case "afterAnchorPane":
        // Handle afterAnchorPane click, jump to after scene
        App.setRoot("Epoch3");
        break;
      case "prepAnchorPane":
        // Handle prepAnchorPane click, jump to prep scene
        App.setRoot("Epoch2");
        break;
      default:
        break;
    }
  }

  @FXML
  public void OnEntered(MouseEvent event) {
    // Handle mouse enter events for travel scene
    AnchorPane pane = (AnchorPane) event.getSource();
    TranslateTransition hoverIn = new TranslateTransition(Duration.millis(200), pane);
    hoverIn.setToY(-10);
    hoverIn.play();
  }

  @FXML
  public void OnExited(MouseEvent event) {
    // Handle mouse exit events for travel scene
    AnchorPane pane = (AnchorPane) event.getSource();
    TranslateTransition hoverOut = new TranslateTransition(Duration.millis(200), pane);
    hoverOut.setToY(0);
    hoverOut.play();
  }

  /**
   * Attaches a listener to the shared countdown timer, so the label updates every time the
   * remaining time changes. Immediately sets the label's initial value.
   */
  private void bindTimer() {
    if (lblTimer == null) {
      return;
    }

    context
        .secondsRemainingProperty()
        .addListener((obs, oldVal, newVal) -> updateTimerLabel(newVal.intValue()));

    updateTimerLabel(context.secondsRemainingProperty().get());
  }

  /**
   * Formats the given number of total seconds as in minutes and seconds, and displays it in the
   * timer label.
   *
   * @param totalSeconds the number of seconds remaining in the section of the game (e.g.
   *     investigation, intervention)
   */
  private void updateTimerLabel(int totalSeconds) {
    int minutes = totalSeconds / 60;
    int seconds = totalSeconds % 60;
    lblTimer.setText(String.format("%d:%02d", minutes, seconds));
  }
}
