// Maya Babu, mbab162, 659352321
// Importing needed package.
package nz.ac.auckland.se206.states;

// Importing needed library
import nz.ac.auckland.se206.GameStateContext;

/** The investigation phase: the player explores both epochs and gathers evidence. */
public class InvestigationState implements GameState {

  // Object that holds the timer.
  private final GameStateContext context;
  // Object that holds the code that defines what should happen when the two minute timer runs out.
  private final Runnable onInvestigationEnd;

  /**
   * Constructs the investigation state.
   *
   * @param context the shared game state context
   * @param onInvestigationEnd called when the investigation timer expires
   */
  public InvestigationState(GameStateContext context, Runnable onInvestigationEnd) {
    this.context = context;
    this.onInvestigationEnd = onInvestigationEnd;
  }

  /**
   * Starts the investigation timer once the investigation phase of the game has been started. The
   * timer counts down from 300 seconds (five minutes)
   */
  @Override
  public void onEnter() {
    context.startTimer(300, onInvestigationEnd);
  }
}
