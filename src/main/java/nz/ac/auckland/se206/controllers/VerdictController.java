package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.states.Interventions;

public class VerdictController {

  @FXML private ImageView outcomeImage;
  @FXML private Label successOrFailLbl;
  @FXML private Label outcomeLbl;
  @FXML private Button replayBtn;

  private static String chosenIntervention = "";
  private static boolean rationaleSupported = false;
  private GameStateContext context = GameStateContext.getCurrentContext();
  private ChatController chatPanelController = new ChatController();

  // setter method to set the chosenIntervention and rationaleSupported method
  public static void setVerdictType(String intervention, boolean supported) {
    chosenIntervention = intervention;
    rationaleSupported = supported;
  }

  // Initialisation method that runs when the scene first opens
  @FXML
  public void initialize() {
    Interventions intervention = Interventions.from(chosenIntervention);

    outcomeLbl.setText(intervention.getConsequence());

    if (intervention.isSuccess()) {
      outcomeImage.setImage(
          new Image(
              getClass().getResource("/images/Other Images/gameOpening.png").toExternalForm()));
    } else {
      outcomeImage.setImage(
          new Image(
              getClass().getResource("/images/Other Images/hallOnFire.jpg").toExternalForm()));
    }

    if (intervention.isSuccess() && rationaleSupported) {
      successOrFailLbl.setText("Correct intervention and supported rationale.");
    } else if (intervention.isSuccess()) {
      successOrFailLbl.setText("Correct intervention, but unsupported rationale.");
    } else if (rationaleSupported) {
      successOrFailLbl.setText("Incorrect intervention, but supported rationale.");
    } else {
      successOrFailLbl.setText("Incorrect intervention and unsupported rationale.");
    }
  }

  // Java action method for the replay button that resets the game and stops the timer then sets the
  // root scene to intro scene
  @FXML
  public void onReplayBtn(ActionEvent event) throws IOException {
    // Calle the GameStateContext resetContext method
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
