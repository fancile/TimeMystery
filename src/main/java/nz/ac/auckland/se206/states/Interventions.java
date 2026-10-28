// Maya Babu, mbab162, 659352321

// Importing needed package(s).
package nz.ac.auckland.se206.states;

/**
 * An enum that holds the four consequences and outcomes that users get when they choose different
 * interventions. There are four of them. This enum is referenced when a button to choose an
 * intervention is clicked.
 */
public enum Interventions {
  // If player chooses to fix the panel to hall managament.
  FIX(
      "You spend the evening repairing the faulty wiring, leaving nothing loose that could"
          + " spark.\n\n"
          + "The panel hums steadily - it seems to be working.\n\n"
          + "But when Dan returns that night, he sees it's been fixed. Furious, he takes his"
          + " drumstick and smashes the panel all over again.\n\n"
          + "The hall burns down that night.\n\n"
          + "The problem is not resolved.\n\nBetter luck next time, detective!",
      false),

  CANCEL(
      "You plead with Alex to cancel the concert outright.\n\n"
          + "He agrees, reluctantly.\n\n"
          + "But this didn't stop Dan's anger over being cut - and he"
          + " burns down the hall in revenge anyways.\n\n"
          + "The hall burns down that night.\n\n"
          + "The problem is not resolved.\n\nBetter luck next time, detective!",
      false),

  HELP(
      "You bring Alex and Dan together, urging them to talk it out.\n\n"
          + "Alex relents and agrees to keep Dan in the lineup. Dan's face lights up.\n\n"
          + "But Alex can't resist one last dig - Dan's slot will be pushed to the very"
          + " end of the show, and might still be cut. Dan's anger comes roaring back.\n\n"
          + "The hall burns down that night.\n\n"
          + "The problem is not resolved.\n\nBetter luck next time, detective!",
      false),

  HIRE(
      "You arrange overnight security for Harmony Hall.\n\n"
          + "Guards patrol the venue through the night, watchful and steady.\n\n"
          + "When Dan slips in to set his plan in motion, he sees them - and freezes."
          + " He flees before he can do any damage.\n\n"
          + "The hall stands the next morning.\n\n"
          + "The problem is resolved.\n\nWell done, detective!",
      true);

  private String consequece;
  private boolean success;

  // Constructor that sets the consequece and the success for the object
  Interventions(String consequence, boolean success) {
    this.consequece = consequence;
    this.success = success;
  }

  // Getter method for getting consequence
  public String getConsequence() {
    return this.consequece;
  }

  // Getter method for getting success or not
  public boolean isSuccess() {
    return this.success;
  }

  public static Interventions from(String intervention) {
    switch (intervention) {
      case "hire_security":
        return HIRE;
      case "cancel_concert":
        return CANCEL;
      case "fix_panel":
        return FIX;
      case "help_dan":
        return HELP;
      default:
        throw new IllegalArgumentException("Unknown intervention: " + intervention);
    }
  }
}
