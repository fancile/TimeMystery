// Import needed packages
package nz.ac.auckland.se206.controllers;

// Import needed libraries
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.states.InterventionSelectionState;

public class InterventionSelectionController {
  // Creating FXML fields for all the elements of the intervention selection scene
  @FXML private Button hireButton;
  @FXML private Button cancelButton;
  @FXML private Button reportButton;
  @FXML private Button helpButton;
  @FXML private Label timerLabel;

  // Holds the intervention that the user chooses
  private static String chosenIntervention = "";

  // Gets the current game state context
  private GameStateContext context = GameStateContext.getCurrentContext();

  /** Starts 30 second timer when the scene loads. */
  @FXML
  public void initialize() throws IOException {
    System.out.println("entering intervention selection");
    context
        .secondsRemainingProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              int secs = newVal.intValue();
              int minutes = secs / 60;
              int seconds = secs % 60;
              timerLabel.setText(String.format("%d:%02d", minutes, seconds));
            });

    // Set state and start the timer
    context.setState(new InterventionSelectionState(context, this::onTimeout, timerLabel));
  }

  /** Handles if the thirty second timer runs out */
  private void onTimeout() {
    try {
      // Go to the fail scene
      App.setRoot("fail");
    } catch (IOException e) {
      // Print the exception
      e.printStackTrace();
    }
  }

  /**
   * Handles what happens if the player clicks to hire security as their intervention.
   *
   * @param event the mouse event triggered when the user clicks on the hire button
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void hireButtonClick(MouseEvent event) throws IOException {
    // Stop the timer
    context.stopTimer();
    System.out.println("Hire clicked!");
    // Setting the chosen intervention to be that they hired security
    chosenIntervention = "hire_security";
    // Go to the rationale scene
    App.setRoot("rationale");
  }

  /**
   * Handles what happens if the player clicks to cancel the concert as their intervention.
   *
   * @param event the mouse event triggered when the user clicks on the cancel button
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void cancelButtonClick(MouseEvent event) throws IOException {
    // Stop the timer
    context.stopTimer();
    System.out.println("Cancel clicked!");
    // Set the chosen intervention to cancel concert
    chosenIntervention = "cancel_concert";
    // Go to the rationale scene
    App.setRoot("rationale");
  }

  /**
   * Handles what happens if the player clicks to report the panel as their intervention.
   *
   * @param event the mouse event triggered when the user clicks on the report button
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void reportButtonClick(MouseEvent event) throws IOException {
    // Stop the timer
    context.stopTimer();
    System.out.println("Report clicked!");
    // Set the chosen intervention to report panel
    chosenIntervention = "report_panel";
    // Go to the rationale scene
    App.setRoot("rationale");
  }

  /**
   * Handles what happens if the player clicks to convince Alex as their intervention.
   *
   * @param event the mouse event triggered when the user clicks on the convince button
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void helpButtonClick(MouseEvent event) throws IOException {
    // Stop the timer
    context.stopTimer();
    System.out.println("Help clicked!");
    // Set the chosen intervention to be helping Dan/convincing Alex
    chosenIntervention = "help_dan";
    // Go to the rationale scene
    App.setRoot("rationale");
  }

  /**
   * Returns the player's chosen intervention when called in rationale controller.
   *
   * @returns the player's chosen intervention.
   */
  public static String getChosenIntervention() {
    return chosenIntervention;
  }
}
