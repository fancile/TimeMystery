// Maya Babu, mab162, 659352321
// Importing package needed.
package nz.ac.auckland.se206.states;

// Importing libraries needed.
import javafx.scene.control.Label;
import nz.ac.auckland.se206.GameStateContext;

/** The intervention-selection phase: the player has 30 seconds to choose an intervention. */
public class InterventionSelectionState implements GameState {

  // Object that holds the thirty second timer.
  private final GameStateContext context;

  // Object that holds code on what to do if time runs out and players have not chosen an
  // intervention.
  private final Runnable onTimeout;

  // FXML fields for SceneBuilder elements.
  private final Label timerLabel;

  /**
   * Constructs the intervention-selection state.
   *
   * @param context the shared game state context
   * @param onTimeout called when the 30-second timer expires with no selection made
   * @param timerLabel the label used to display remaining time
   */
  public InterventionSelectionState(
      GameStateContext context, Runnable onTimeout, Label timerLabel) {
    this.context = context;
    this.onTimeout = onTimeout;
    this.timerLabel = timerLabel;
  }

  /** Constructs the intervention-selection state. */
  @Override
  public void onEnter() {
    // Starting the thirty second timer
    context.startTimer(30, onTimeout);
    // Showing the label that shows the time remaining
    if (timerLabel != null) {
      timerLabel.setVisible(true);
    }
  }
}
