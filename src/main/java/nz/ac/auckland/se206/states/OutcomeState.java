// Maya Babu, mbab162, 659352321
// Importing needed package(s).
package nz.ac.auckland.se206.states;

// Imported needed libraries.
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import nz.ac.auckland.se206.GameStateContext;

/** The outcome phase: the player's chosen intervention's consequence is displayed. */
public class OutcomeState implements GameState {

  // An object that holds the timer.
  private GameStateContext context;
  // Holds the intervention that was chosen by the player.
  private Interventions intervention;

  // FXML handles for SceneBuilder elements
  private AnchorPane guessPanel;
  private AnchorPane outcomeScene;
  private Label outcomeText;
  private Label successOrFail;
  private Label timerLabel;
  private Button playAgainButton;

  /**
   * Constructs the outcome state for a specific chosen intervention.
   *
   * @param context the shared game state context
   * @param intervention the intervention the player chose (or the default, on timeout)
   * @param guessPanel the intervention-selection pane, hidden when the outcome shows
   * @param outcomeScene the outcome pane, shown for this phase
   * @param outcomeText the label displaying the consequence text
   * @param successOrFail the label displaying SUCCESS or FAIL
   * @param timerLabel the countdown label, hidden for this phase
   * @param playAgainButton the replay button, shown for this phase
   */
  public OutcomeState(
      GameStateContext context,
      Interventions intervention,
      AnchorPane guessPanel,
      AnchorPane outcomeScene,
      Label outcomeText,
      Label successOrFail,
      Label timerLabel,
      Button playAgainButton) {
    this.context = context;
    this.intervention = intervention;
    this.guessPanel = guessPanel;
    this.outcomeScene = outcomeScene;
    this.outcomeText = outcomeText;
    this.successOrFail = successOrFail;
    this.timerLabel = timerLabel;
    this.playAgainButton = playAgainButton;
  }

  /**
   * Executes logic for when the consequence is shown (te user as clicked their chosen
   * intervention): stops the timer, updates feedback labels based on the chosen intervention, and
   * updates pane visibility to show users the consequence text.
   */
  @Override
  public void onEnter() {
    // Stopping the thiry second timer
    context.stopTimer();

    // Setting the text in the label to the consequnce matching the player's choice.
    outcomeText.setText(this.intervention.getConsequence());
    // Setting the text in the bottom label that tells users if their chosen intervention was
    // correct (Success) or wrong (fail) in stopping the fire from the game
    successOrFail.setText(intervention.isSuccess() ? "SUCCESS" : "FAIL");
    // Setting the colour of the word Sucess or Fail depending on what word is being displayed.
    successOrFail.setTextFill(intervention.isSuccess() ? Color.GREEN : Color.RED);

    // Hiding uneeded panes and backgrounds, hding the thirty second timer label.
    guessPanel.setVisible(false);
    outcomeScene.setVisible(true);
    timerLabel.setVisible(false);
    playAgainButton.setVisible(true);
  }
}
