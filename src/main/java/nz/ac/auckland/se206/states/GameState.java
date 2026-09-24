package nz.ac.auckland.se206.states;

/** Represents a phase of the game: investigation, intervention selection, or outcome. */
public interface GameState {

  /** Called when this state becomes the active phase. */
  public void onEnter();
}
