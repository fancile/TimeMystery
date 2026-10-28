package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.AudioManager;
import nz.ac.auckland.se206.GameStateContext;

/**
 * Controls the Epoch 3 scene, including the pavement investigation, plank puzzle, chat panel,
 * countdown display, and transitions to the next game stage.
 */
public class Epoch3Controller {
  @FXML private Rectangle Alex;
  @FXML private Rectangle pavement;
  @FXML private Label lblTimer;
  @FXML private Button BACK;
  @FXML private AnchorPane match;
  @FXML private Button endInvestigationBtn;
  @FXML private Label incompleteInvestigationLbl;
  @FXML private AnchorPane openingEpoch3;
  @FXML private AnchorPane pavementView;
  @FXML private ImageView plank1;
  @FXML private ImageView plank2;
  @FXML private Rectangle wiresAndCigs;
  @FXML private Label zoomedDebrisLbl;
  @FXML private AnchorPane zoomDebris;

  @FXML private AnchorPane chatPanel;
  @FXML private ChatController chatPanelController; // Reference to the chat controller
  @FXML private Button muteBtn; // mute button

  @FXML private AnchorPane travelClock;

  private GameStateContext context =
      GameStateContext.getCurrentContext(); // Reference to the GameStateContext

  // Holds where the mouse is
  private double mouseX;
  private double mouseY;

  // The starting X and Y layout coordinates of the first plank
  private double plank1StartX;
  private double plank1StartY;
  // The starting X and Y layout coordinates of the second plank
  private double plank2StartX;
  private double plank2StartY;

  // Stores if the first plank has been moved
  private boolean plank1Moved = false;
  // Stores if the second plank has been moved
  private boolean plank2Moved = false;

  /**
   * Initializes the Epoch 3 scene, records the planks' starting positions, configures the timer and
   * mute control, and starts the five-minute investigation timer.
   */
  @FXML
  public void initialize() {
    chatPanel.setVisible(false);
    openingEpoch3.setVisible(true);
    pavementView.setVisible(false);
    incompleteInvestigationLbl.setVisible(false);
    zoomDebris.setVisible(false);
    muteBtn.setVisible(true);

    plank1StartX = plank1.getLayoutX();
    plank1StartY = plank1.getLayoutY();

    plank2StartX = plank2.getLayoutX();
    plank2StartY = plank2.getLayoutY();

    wiresAndCigs.setVisible(false);

    bindTimer();
    if (!context.getMuted()) {
      muteBtn.setText("Unmute SFX");
    } else {
      muteBtn.setText("Mute SFX");
    }
    BACK.setVisible(false);
  }

  /**
   * Handles clicks on interactive rectangles in the scene. Clicking Alex opens the chat panel;
   * clicking the pavement switches to the pavement view and resets the first plank position.
   *
   * @param event the mouse event raised by the clicked rectangle
   */
  @FXML
  public void onRectangleClick(MouseEvent event) {
    switch (((Rectangle) event.getSource()).getId()) {
      case "Alex":
        chatPanelController.setEpoch("epoch 3");
        // the epoch for the chat controller
        chatPanel.setVisible(true); // Show the chat panel
        chatPanel.toFront(); // Bring the chat panel to the front
        break;
      case "pavement":
        BACK.setVisible(true);
        openingEpoch3.setVisible(false);
        pavementView.setVisible(true);
        break;
    }
  }

  /**
   * Handles the play-again and back button actions, including navigation and incomplete-
   * investigation feedback.
   *
   * @param event the mouse event raised by the clicked button
   * @throws IOException if JavaFX cannot load the requested scene
   */
  @FXML
  public void onButtonClick(MouseEvent event) throws IOException {
    switch (((Button) event.getSource()).getId()) {
      case "BACK":
        if (zoomDebris.isVisible()) {
          zoomDebris.setVisible(false);
          pavementView.setVisible(true);
        } else if (pavementView.isVisible()) {
          pavementView.setVisible(false);
          openingEpoch3.setVisible(true);
          BACK.setVisible(false);
        }
        break;
    }
  }

  /**
   * Applies the standard hover appearance to the source node by changing its cursor, opacity, and
   * shadow effect.
   *
   * @param event the mouse event raised by the hovered node
   */
  @FXML
  private void onHover(MouseEvent event) {
    Node node = (Node) event.getSource();
    node.setCursor(Cursor.HAND);
    node.setOpacity(0.3);
    node.setEffect(new DropShadow(50, Color.GOLD));
  }

  /**
   * Called when the investigation is ended early with all requirements met. Hides the timer label
   * and transitions to the intervention selection screen.
   *
   * @throws IOException if JavaFX cannot load the requested scene
   */
  private void endInvestigation() throws IOException {
    lblTimer.setVisible(false);
    App.setRoot("interventionSelection");
  }

  /**
   * Handles a click on the "Make your decision" button. If the investigation is complete, stops the
   * timer and moves to intervention selection. Otherwise, shows a message telling the player what
   * they still need to do.
   *
   * @param event the mouse event raised by the decision button
   * @throws IOException if JavaFX cannot load the intervention selection scene
   */
  @FXML
  private void onDecisionButtonClick(MouseEvent event) throws IOException {
    if (context.isInvestigationComplete()) {
      context.stopTimer();
      endInvestigation();
    } else {
      showIncompleteInvestigation(context.getIncompleteMessage());
    }
  }

  /**
   * Handles the travel button click and navigates to the travel selection page.
   *
   * @param event the mouse event raised by the travel button
   * @throws IOException if JavaFX cannot load the travel page
   */
  @FXML
  private void onTravelClick(MouseEvent event) throws IOException {
    App.setRoot("travelPage");
  }

  /**
   * Restores the source node's default cursor and shadow, and hides the node.
   *
   * @param event the mouse event raised by the node being exited
   */
  @FXML
  private void onExit(MouseEvent event) {
    Node node = (Node) event.getSource();
    node.setCursor(Cursor.DEFAULT);
    node.setOpacity(0.0);
    node.setEffect(null);
  }

  /**
   * Attaches a listener to the shared countdown timer, so the label updates every time the
   * remaining time changes. Immediately sets the label's initial value.
   */
  private void bindTimer() {
    if (lblTimer == null) {
      return;
    }

    context
        .secondsRemainingProperty()
        .addListener((obs, oldVal, newVal) -> updateTimerLabel(newVal.intValue()));

    updateTimerLabel(context.secondsRemainingProperty().get());
  }

  /**
   * Formats the given number of total seconds as in minutes and seconds, and displays it in the
   * timer label.
   *
   * @param totalSeconds the number of seconds remaining in the section of the game (e.g.
   *     investigation, intervention)
   */
  private void updateTimerLabel(int totalSeconds) {
    int minutes = totalSeconds / 60;
    int seconds = totalSeconds % 60;
    lblTimer.setText(String.format("%d:%02d", minutes, seconds));
  }

  /**
   * Displays the supplied incomplete-investigation message for three seconds.
   *
   * @param text the message explaining which investigation tasks remain incomplete
   */
  private void showIncompleteInvestigation(String text) {
    incompleteInvestigationLbl.setText(text);
    incompleteInvestigationLbl.setVisible(true);

    PauseTransition delay = new PauseTransition(Duration.seconds(3));
    delay.setOnFinished(event -> incompleteInvestigationLbl.setVisible(false));
    delay.play();
  }

  /**
   * Toggles the shared mute state and updates the mute button text accordingly.
   *
   * @param event the action event raised by the mute button
   */
  @FXML
  private void onMute(ActionEvent event) {
    if (context.getMuted()) {
      muteBtn.setText("Unmute SFX");
      context.setMuted(false);
      AudioManager.setMuted(true);
    } else {
      muteBtn.setText("Mute SFX");
      context.setMuted(true);
      AudioManager.setMuted(false);
    }
  }

  // PLANK ONE LOGIC
  @FXML
  private void onPlankPress(MouseEvent event) {
    ImageView plank = (ImageView) event.getSource();
    mouseX = event.getSceneX() - plank.getLayoutX();
    mouseY = event.getSceneY() - plank.getLayoutY();
  }

  @FXML
  private void onPlankDrag(MouseEvent event) {
    ImageView plank = (ImageView) event.getSource();
    plank.setLayoutX(event.getSceneX() - mouseX);
    plank.setLayoutY(event.getSceneY() - mouseY);
  }

  @FXML
  private void onPlankRelease(MouseEvent event) {
    ImageView plank = (ImageView) event.getSource();

    double startX;
    double startY;

    if (plank == plank1) {
      startX = plank1StartX;
      startY = plank1StartY;
    } else {
      startX = plank2StartX;
      startY = plank2StartY;
    }

    double distanceX = plank.getLayoutX() - startX;
    double distanceY = plank.getLayoutY() - startY;

    double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);

    if (distance > 200) {
      if (plank == plank1) {
        plank1Moved = true;
      } else {
        plank2Moved = true;
      }
    }

    if (plank1Moved == true && plank2Moved == true) {
      wiresAndCigs.setVisible(true);
      context.setInteractedE3(true);
    }
  }

  @FXML
  private void wiresAndCigsClick(MouseEvent event) {
    zoomDebris.setVisible(true);
    zoomDebris.toFront();
    muteBtn.toFront();
    lblTimer.toFront();
    endInvestigationBtn.toFront();
    incompleteInvestigationLbl.toFront();
    BACK.toFront();
    zoomedDebrisLbl.setText(
        "Whoa!\n"
            + "Burnt wires? Next to cigs?\n"
            + "As if the cigs burnt them...\n"
            + "It's worth asking Alex about this.");
    chatPanelController.notifyInteractableUsed(
        "The player has found cigarettes next to wires. This is quite shocking. Soon, feel free"
            + " to discuss how Dan was a smoker.");
  }

  @FXML
  private void onPlankHover(MouseEvent event) {
    Node node = (Node) event.getSource();
    node.setCursor(Cursor.HAND);
    node.setOpacity(0.9);
    node.setEffect(new DropShadow(50, Color.web("d07a71")));
  }

  @FXML
  private void onPlankExit(MouseEvent event) {
    Node node = (Node) event.getSource();
    node.setCursor(Cursor.DEFAULT);
    node.setOpacity(1);
    node.setEffect(null);
  }
}
