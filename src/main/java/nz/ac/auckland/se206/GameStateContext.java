// Maya Babu, mbab162, 659352321
// Importing package needed
package nz.ac.auckland.se206;

// Importing needed libraries.
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Duration;
import nz.ac.auckland.se206.states.GameState;

/**
 * Holds shared game state for the investigation phase, specifically the countdown timer. Ticks once
 * per second and notifies a callback when time runs out.
 */
public class GameStateContext {
  // The number of seconds left on the countdown.
  private final IntegerProperty secondsRemaining;
  // The timer that is used for both the investigation and intervention stages
  private final Timeline timer;
  // Called if (or when) the timer reaches zero.
  private Runnable onTimerExpired;
  // The current active game state (investigation, intervention selection, or outcome)
  private GameState currentState;
  // The current context of the game state
  private static GameStateContext context;

  // Boolean variables for user interacting with npc from each epoch
  private boolean spokenEpoch1Npc = false;
  private boolean spokenEpoch2Npc = false;
  private boolean spokenEpoch3Npc = false;

  // Boolean variables to check if user has found each interactable in each epoch
  private boolean epoch1InteractFound = false;
  private boolean epoch2InteractFound = false;
  private boolean epoch3InteractFound = false;

  // Global mute boolean variable for all epochs
  private boolean notMuted = true;

  /**
   * Checks missing tasks and returns a message telling the player exactly what categories they need
   * to finish.
   */
  public String getIncompleteMessage() {
    boolean missingAlexes = !spokenEpoch1Npc || !spokenEpoch2Npc || !spokenEpoch3Npc;
    boolean missingEvidence = !epoch1InteractFound || !epoch2InteractFound || !epoch3InteractFound;

    if (missingAlexes && missingEvidence) {
      return "You have not finished the investigation! Ensure you have spoken to all Alexes and"
          + " found all pieces of evidence.";
    } else if (missingAlexes) {
      return "You have not finished the investigation! Ensure you have spoken to all Alexes.";
    } else if (missingEvidence) {
      return "You have not finished the investigation! Ensure you have found all pieces of"
          + " evidence.";
    }

    return "";
  }

  /**
   * Constructs a new GameStateContext, setting up the shared countdown timer. The timer decrements
   * once per second, decrementing secondsRemaining, and calls onTimerExpired if it reaches zero.
   */
  public GameStateContext() {
    timer = new Timeline();
    secondsRemaining = new SimpleIntegerProperty(300);

    // Defines what happens on every second passed of the timer - decrement the remaining seconds,
    // and if it reaches zero, stop the timer and call on needed functions, depeding on what timer
    // ran out (investigation or intervention)
    KeyFrame oneSecond =
        new KeyFrame(
            Duration.seconds(1),
            event -> {
              secondsRemaining.set(secondsRemaining.get() - 1);

              if (secondsRemaining.get() <= 0) {
                timer.stop();
                if (onTimerExpired != null) {
                  onTimerExpired.run();
                }
              }
            });

    // Attaching the tick behaviour to the timer, and setting it to always repeat. It gets stopped
    // by stopTimer(), or automatically once secondsRemaining reaches zero.
    timer.getKeyFrames().add(oneSecond);
    timer.setCycleCount(Animation.INDEFINITE);
  }

  /**
   * Starts the countdown timer, if it is not already running. Resets the countdown to the given
   * duration each time it is started, so the same timer can be reused across both the investigation
   * and intervention-selection stages.
   *
   * @param durationSeconds how many seconds to count down from
   * @param onTimerExpired called once, when the timer reaches zero
   */
  public void startTimer(int durationSeconds, Runnable onTimerExpired) {
    // Prevents a second countdown from starting on top of one that is already running by checking
    // if there is already a timer going.
    if (timer.getStatus() == Animation.Status.RUNNING) {
      return;
    }
    secondsRemaining.setValue(durationSeconds);
    this.onTimerExpired = onTimerExpired;
    timer.play();
  }

  /**
   * Stops the countdown timer. Called if the player clicks the Make a Decision button, or if they
   * click an intervention.
   */
  public void stopTimer() {
    timer.stop();
  }

  /**
   * Exposes the remaining seconds as a read-only property, so the UI can display it without being
   * able to modify it directly.
   *
   * @return the read-only seconds-remaining property
   */
  public ReadOnlyIntegerProperty secondsRemainingProperty() {
    return secondsRemaining;
  }

  /**
   * Sets the currently active game state and notifies it that it has become active, so it can
   * perform any setup it owns.
   *
   * @param state the new state to activate
   */
  public void setState(GameState state) {
    this.currentState = state;
    state.onEnter();
  }

  /**
   * Gets the currently active game phase.
   *
   * @return the current state
   */
  public GameState getCurrentGameState() {
    return currentState;
  }

  public static GameStateContext getCurrentContext() {
    if (context == null) {
      context = new GameStateContext();
    }
    return context;
  }

  // Method to check if the user has completed finding all interactables and talked to npcs in every
  // epoch

  public boolean isInvestigationComplete() {
    return spokenEpoch1Npc
        && spokenEpoch2Npc
        && spokenEpoch3Npc
        && epoch1InteractFound
        && epoch2InteractFound
        && epoch3InteractFound;
  }

  // Reset method that resets all the variables back to false for NPC and Interactable values
  public void resetContext() {
    timer.stop();
    onTimerExpired = null;
    secondsRemaining.set(300);

    spokenEpoch1Npc = false;
    spokenEpoch2Npc = false;
    spokenEpoch3Npc = false;
    epoch1InteractFound = false;
    epoch2InteractFound = false;
    epoch3InteractFound = false;
  }

  // Setter methods for setting the boolean variables for interactables and NPCs
  public void setSpokenEpoch1(boolean value) {
    spokenEpoch1Npc = value;
  }

  public void setSpokenEpoch2(boolean value) {
    spokenEpoch2Npc = value;
  }

  public void setSpokenEpoch3(boolean value) {
    spokenEpoch3Npc = value;
  }

  public void setInteractedE1(boolean value) {
    epoch1InteractFound = value;
  }

  public void setInteractedE2(boolean value) {
    epoch2InteractFound = value;
  }

  public void setInteractedE3(boolean value) {
    epoch3InteractFound = value;
  }

  // Getter methods for getting the boolean variables for interactables and NPCs
  public boolean getSpokenEpoch1() {
    return spokenEpoch1Npc;
  }

  public boolean getSpokenEpoch2() {
    return spokenEpoch2Npc;
  }

  public boolean getspokenEpoch3() {
    return spokenEpoch3Npc;
  }

  public boolean getInteractedE1() {
    return epoch1InteractFound;
  }

  public boolean getInteractedE2() {
    return epoch2InteractFound;
  }

  public boolean getInteractedE3() {
    return epoch3InteractFound;
  }

  // Setter method for changihng the mute boolean variable
  public void setMuted(boolean var) {
    notMuted = var;
  }

  // Getter method for the muted boolean variable
  public boolean getMuted() {
    return notMuted;
  }
}
