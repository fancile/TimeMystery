// Maya Babu, mbab162, 659352321

// Importing needed package(s).
package nz.ac.auckland.se206.states;

/**
 * An enum that holds the four consequences and outcomes that users get when they choose different
 * interventions. There are four of them. This enum is referenced when a button to choose an
 * intervention is clicked.
 */
public enum Interventions {
  // If player chooses to report the panel to hall managament.
  REPORT(
      "You storm into the office of hall management, giving an impassioned speech about your"
          + " evidence: the broken electrical panel and the cigarettes. Something is going to"
          + " happen, you warn.\n"
          + "The management pauses - and shrugs. Nothing they can do, they say. Your evidence is"
          + " circumstantial.\n"
          + "A drumstick? Cigarettes? A panel? Please.\n"
          + "The hall burns down that night.\n"
          + "The problem is not resolved.\nBetter luck next time, detective!",
      false),

  CANCEL(
      "You beg Alex to cancel the concert, believing this will prevent the fire.\n"
          + "He does so, begrudgingly.\n"
          + "However, Dan is still angry about being cut from the concert, and still exacts his"
          + " revenge.\n"
          + "The hall burns down that night.\n"
          + "The problem is not resolved.\n"
          + "Better luck next time, detective!",
      false),

  HELP(
      "You wrangle Alex and Dan into a room. You beg them to just talk to each other - and they"
          + " do.\n"
          + "Alex agrees to keep Dan in the show. Dan lights up.\n"
          + "But Alex is petty. He tells Dan that his song will be near the very end and may still"
          + " get cut. Dan's anger returns tenfold.\n"
          + "The hall burns down that night.\n"
          + "The problem is not resolved.\n"
          + "Better luck next time, detective!",
      false),

  HIRE(
      "You hire overnight security to watch Harmony Hall.\n"
          + "Three guards stand watch all night, patrolling the venue tirelessly.\n"
          + "When Dan sneaks over, he freezes - he'll be caught! He flees before he can be caught"
          + " or cause the fire.\n"
          + "The hall does not burn down that night.\n"
          + "The problem is resolved.\n"
          + "Awesome work, detective!",
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
      case "report_panel":
        return REPORT;
      case "help_dan":
        return HELP;
      default:
        throw new IllegalArgumentException("Unknown intervention: " + intervention);
    }
  }
}
