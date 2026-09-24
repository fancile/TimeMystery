// Importing needed packages
package nz.ac.auckland.se206.controllers;

// Importing needed libraries
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.states.Interventions;
import nz.ac.auckland.se206.states.RationaleState;

public class RationaleController {
  // Creating FXML fields for all the elements of the rationale scene.
  @FXML private Button submitRationaleBtn;
  @FXML private Label rationaleExplanationLbl;
  @FXML private Label timerLbl;
  @FXML private Label evaluationLbl;
  @FXML private Label retryEvaluationLbl;
  @FXML private Label interventionChosenLbl;
  @FXML private TextArea rationaleTxtArea;

  // Creating a new rationaleChatController object to handle sending and evaluating the rationale
  private RationaleChatController rationaleChatController = new RationaleChatController();
  // Getting current game state context
  private GameStateContext context = GameStateContext.getCurrentContext();
  // Holds the intervention that the user chose in the previous scene
  private String chosenIntervention = "";
  // Holds the rationale that the user writes.
  private String rationale = "";

  // Holds whether the rationale comes back as supported or not.
  private boolean rationaleSupported = false;

  // Standard no-arg constructor required by JavaFX.
  public RationaleController() {}

  /** Initalises the scene */
  @FXML
  public void initialize() {
    // Setting visibility of elements of the scene
    evaluationLbl.setVisible(false);
    retryEvaluationLbl.setVisible(false);
    rationaleTxtArea.setVisible(true);
    // Enabling the submit rationale button
    submitRationaleBtn.setDisable(false);
    submitRationaleBtn.setText("Submit");

    // Getting the intervention the player chose
    chosenIntervention = InterventionSelectionController.getChosenIntervention();

    // Setting the text of label in the file that shows what intervention the player chose in case
    // they need a reminder.
    if (chosenIntervention.equals("hire_security")) {
      interventionChosenLbl.setText("You chose to hire security.");
    } else if (chosenIntervention.equals("cancel_concert")) {
      interventionChosenLbl.setText("You chose to cancel the concert.");
    } else if (chosenIntervention.equals("report_panel")) {
      interventionChosenLbl.setText("You chose to report the panel.");
    } else {
      interventionChosenLbl.setText("You chose to convince Alex.");
    }

    // Setting the text of the rationale explanation label
    rationaleExplanationLbl.setText(
        "Interesting choice, detective!\n\n"
            + "Why do you say that?\n\n"
            + "Write your analysis in the file on the right.\n\n"
            + "Include evidence you found, information you got from Alex, and why your choice will"
            + " save Harmony Hall.\n\nDid you save Harmony Hall? Let's see!");

    // Rationale timer - calculating and displaying how much time the player has left
    context
        .secondsRemainingProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              int secs = newVal.intValue();
              int minutes = secs / 60;
              int seconds = secs % 60;
              timerLbl.setText(String.format("%d:%02d", minutes, seconds));
            });

    // On timeout, execute the timeout function.
    context.setState(
        new RationaleState(
            context,
            () -> {
              try {
                onTimeout();
              } catch (IOException e) {
                e.printStackTrace();
              }
            },
            timerLbl));
  }

  /**
   * Handles submitting and evaluating the player's rationale. Send their rationale to the
   * evaluator, and displays the evaluation on its designated label.
   *
   * @param event the mouse event triggered by clicking the Submit button
   * @throws IOException if there is an I/O error
   */
  @FXML
  public void submitRationaleClick(MouseEvent event) throws IOException {
    // If the rationale has already been evaluated, go to the verdict screen.
    if (submitRationaleBtn.getText().equals("Next")) {
      VerdictController.setVerdictType(chosenIntervention, rationaleSupported);
      App.setRoot("verdict");
      return;
    }

    // If the previous evaluation failed, try again.
    if (submitRationaleBtn.getText().equals("Retry")) {
      submitRationaleBtn.setDisable(true);
      // Set the X and Y layout of the button when it reads "Evaluating..."
      submitRationaleBtn.setPrefWidth(100);
      submitRationaleBtn.setLayoutX(507);
      submitRationaleBtn.setText("Evaluating...");

      // Send the rationale typed to the RationaleChatContoller.
      rationaleChatController.evaluateRationale(
          chosenIntervention,
          rationale,
          evaluation -> {
            // When the evaluation is finished
            System.out.println("Evaluation finished.");
            // Hide the retry evaluation label if it was shown
            retryEvaluationLbl.setVisible(false);
            // Store if the rationale is supported or not
            rationaleSupported = evaluation.startsWith("Supported");

            // Get the evaluation from the AI
            String displayEvaluation =
                evaluation.replaceFirst("^(Supported|Unsupported)\\.?\\s*", "Interesting! ");

            // Show and display the evaluation to the player
            evaluationLbl.setVisible(true);
            // Re-enable the button so the user can click next and position it.
            submitRationaleBtn.setDisable(false);
            evaluationLbl.setText(displayEvaluation);

            submitRationaleBtn.setDisable(false);
            submitRationaleBtn.setPrefWidth(67);
            submitRationaleBtn.setLayoutX(532);
            submitRationaleBtn.setText("Next");
          },
          () -> {
            System.out.println("Retry failed.");

            Interventions intervention = Interventions.from(chosenIntervention);

            rationaleTxtArea.setVisible(false);
            retryEvaluationLbl.setVisible(true);
            retryEvaluationLbl.setText(
                "Evaluation unavailable right now.\n\n"
                    + (intervention.isSuccess()
                        ? "Your intervention worked:\n"
                        : "Your intervention did not work:\n")
                    + intervention.getConsequence());

            submitRationaleBtn.setDisable(false);
            submitRationaleBtn.setText("Retry");
          });

      return;
    }

    // Get the player's rationale.
    rationale = rationaleTxtArea.getText();

    // A blank rationale is automatically unsupported.
    if (rationale.trim().isEmpty()) {
      System.out.println("Blank rationale.");
      VerdictController.setVerdictType(chosenIntervention, false);
      // Cut straight to a fail scene with unsupported rationale
      App.setRoot("verdict");
      return;
    }

    // Printint out the rationale in the console.
    System.out.println("Rationale: " + rationale);

    // Setting the button to be disables when it reads "Evaluating..."
    submitRationaleBtn.setDisable(true);
    submitRationaleBtn.setPrefWidth(100);
    submitRationaleBtn.setLayoutX(507);
    submitRationaleBtn.setText("Evaluating...");

    // Send the rationale typed to the RationaleChatContoller.
    rationaleChatController.evaluateRationale(
        chosenIntervention,
        rationale,
        evaluation -> {
          System.out.println("Evaluation finished.");
          retryEvaluationLbl.setVisible(false);
          rationaleTxtArea.setVisible(false);

          // Printing the evaluation
          System.out.println("GPT response: " + evaluation);

          // Getting if the rationale is supported or not
          rationaleSupported = evaluation.startsWith("Supported");

          // Holds the evaluation
          String displayEvaluation =
              evaluation.replaceFirst("^(Supported|Unsupported)\\.?\\s*", "Interesting! ");

          // Displaying the intervention
          evaluationLbl.setVisible(true);
          submitRationaleBtn.setDisable(false);
          submitRationaleBtn.setPrefWidth(67);
          submitRationaleBtn.setLayoutX(532);
          submitRationaleBtn.setText("Next");

          evaluationLbl.setText(displayEvaluation);
        },
        () -> {
          System.out.println("Evaluation failed.");

          Interventions intervention = Interventions.from(chosenIntervention);

          rationaleTxtArea.setVisible(false);
          retryEvaluationLbl.setVisible(true);
          retryEvaluationLbl.setText(
              "Evaluation unavailable right now.\n\n"
                  + (intervention.isSuccess()
                      ? "Your intervention worked:\n"
                      : "Your intervention did not work:\n")
                  + intervention.getConsequence());

          submitRationaleBtn.setDisable(false);
          submitRationaleBtn.setPrefWidth(67);
          submitRationaleBtn.setLayoutX(686);
          submitRationaleBtn.setText("Retry");
        });

    // Stopping the rationale timer once the player hits submit
    context.stopTimer();
  }

  /**
   * Handles what happens if the timer runs out before the user types their evaluation and clicks
   * Submit
   *
   * @throws IOException if there is an I/O error
   */
  private void onTimeout() throws IOException {
    // Get whatever rationale text the user typed up and until timeout.
    rationale = rationaleTxtArea.getText();

    // If the timer runs out and there is no rationale, treat it as unsupported.
    if (rationale.trim().isEmpty()) {
      System.out.println("Rationale timed out with no text.");
      VerdictController.setVerdictType(chosenIntervention, false);
      // Go straight to a fail scene with unsopported evidence.
      App.setRoot("verdict");
      return;
    }

    // Setting the text and X and Y layout of the button when it is evaluating the rationale
    submitRationaleBtn.setDisable(true);
    submitRationaleBtn.setPrefWidth(100);
    submitRationaleBtn.setLayoutX(507);
    submitRationaleBtn.setText("Evaluating...");

    // Logging that the timer timed-out in the console
    System.out.println("Rationale timed out. Auto-submitting: " + rationale);

    // Send whatever text the player had typed to be evaluated
    rationaleChatController.evaluateRationale(
        chosenIntervention,
        rationale,
        evaluation -> {
          System.out.println("Evaluation finished.");
          // Hide the retry evaluation button if it was shown
          retryEvaluationLbl.setVisible(false);
          // Determine if the rationale was supported or not.
          rationaleSupported = evaluation.startsWith("Supported");
          // Hold the recieved evaluation and display it
          String displayEvaluation =
              evaluation.replaceFirst("^(Supported|Unsupported)\\.?\\s*", "Interesting! ");
          evaluationLbl.setText(displayEvaluation);
          evaluationLbl.setVisible(true);

          // Show the next button for users to click to the verdict scene.
          submitRationaleBtn.setDisable(false);
          submitRationaleBtn.setPrefWidth(67);
          submitRationaleBtn.setLayoutX(532);
          submitRationaleBtn.setText("Next");
        },
        () -> {
          Interventions intervention = Interventions.from(chosenIntervention);

          rationaleTxtArea.setVisible(false);
          retryEvaluationLbl.setVisible(true);
          retryEvaluationLbl.setText(
              "Evaluation unavailable right now.\n\n"
                  + (intervention.isSuccess()
                      ? "Your intervention worked:\n"
                      : "Your intervention did not work:\n")
                  + intervention.getConsequence());

          submitRationaleBtn.setDisable(false);
          submitRationaleBtn.setText("Retry");
        });
  }
}
