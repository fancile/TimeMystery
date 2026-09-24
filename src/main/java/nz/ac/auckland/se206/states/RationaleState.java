// Maya Babu, mab162, 659352321
// Importing package needed.
package nz.ac.auckland.se206.states;

// Importing libraries needed.
import javafx.scene.control.Label;
import nz.ac.auckland.se206.GameStateContext;

public class RationaleState implements GameState {

  private final GameStateContext context;
  private final Runnable onTimeout;
  private final Label timerLabel;

  public RationaleState(GameStateContext context, Runnable onTimeout, Label timerLabel) {
    this.context = context;
    this.onTimeout = onTimeout;
    this.timerLabel = timerLabel;
  }

  @Override
  public void onEnter() {
    context.startTimer(60, onTimeout);

    if (timerLabel != null) {
      timerLabel.setVisible(true);
    }
  }
}
