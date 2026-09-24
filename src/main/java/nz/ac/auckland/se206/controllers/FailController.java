// Importing needed packages
package nz.ac.auckland.se206.controllers;

// Importing needed libraries
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;

public class FailController {

  // Create FXML fields for the scene's elements.
  @FXML private Label failLbl;
  @FXML private Button replayBtn;

  // Get the current state of the game.
  private GameStateContext context = GameStateContext.getCurrentContext();
  // Create a new chat controller instance.
  private ChatController chatPanelController = new ChatController();

  /** Initialise function that sets label text. */
  @FXML
  private void initialize() {
    failLbl.setVisible(true);
    // Setting label text
    failLbl.setText("You ran out of time!\n\nWhat a shame.\n\nWhy don't you try again, detective?");
  }

  /**
   * Resets the game so that the user can play from a clean slate.
   *
   * @param event he action event triggered by clicking the reset button.
   * @throws IOException if the intro scene cannot be loaded
   */
  @FXML
  private void onResetBtn(ActionEvent event) throws IOException {
    failLbl.setVisible(false);
    // Call the GameStateContext resetContext method
    context.resetContext();

    // Clear LLM Memory
    if (chatPanelController != null) {
      chatPanelController.resetChat();
    }
    context.stopTimer();
    // Reset scene back into the intro scene
    App.setRoot("intro");
  }
}
