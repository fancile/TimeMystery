package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.AudioManager;
import nz.ac.auckland.se206.GameStateContext;

public class Epoch2Controller {

  @FXML private Rectangle rectAlex;
  @FXML private Rectangle rectElectricalPanel;
  @FXML private Rectangle rectPanel;
  @FXML private Label lblTimer;
  @FXML private AnchorPane chatPanel;
  @FXML private AnchorPane chatPanelAnchorPane;
  @FXML private ChatController chatPanelController;
  @FXML private Label lblHint;
  @FXML private Button backButton;
  @FXML private Deque<AnchorPane> sceneHistory = new ArrayDeque<>();
  @FXML private AnchorPane panelAnchorPane;
  @FXML private AnchorPane panelOpenedAnchorPane;
  @FXML private AnchorPane epoch2MainPane;
  @FXML private Button decisionButton;
  @FXML private Circle travelCircle;
  @FXML private Label incompleteInvestigationLbl;
  @FXML private Rectangle danRectangle;
  @FXML private Button muteBtn;

  // The Dan speech
  private AudioClip danVoiceLine;

  private GameStateContext context = GameStateContext.getCurrentContext();

  // Initialise method that initialises the initial code when the scene first opens.
  @FXML
  public void initialize() {
    chatPanel.setVisible(false);
    backButton.setVisible(false);
    panelAnchorPane.setVisible(false);
    panelOpenedAnchorPane.setVisible(false);
    incompleteInvestigationLbl.setVisible(false);
    chatPanelAnchorPane.setVisible(false);
    sceneHistory.clear();
    bindTimer();
    if (!context.getMuted()) {
      muteBtn.setText("Unmute SFX");
    } else {
      muteBtn.setText("Mute SFX");
    }

    danVoiceLine =
        new AudioClip(getClass().getResource("/sounds/Epoch 2 Sounds/Dan.mp3").toExternalForm());
  }

  // Opens the chat box for the scene when Alex is clicked
  @FXML
  private void onAlexClick(MouseEvent event) throws IOException {
    System.out.println("ALEX CLICKED!");

    chatPanelController.setEpoch("epoch 2");

    chatPanelAnchorPane.setVisible(true);

    chatPanelController.setOnCloseCallback(() -> chatPanelAnchorPane.setVisible(false));
    chatPanel.setVisible(true);
    chatPanel.toFront();
    backButton.setVisible(false);
  }

  // onHover method shows light around Alex to show that he is clickable and is able to be
  // interacted with
  @FXML
  private void onHover(MouseEvent event) {
    Node node = (Node) event.getSource();
    node.setCursor(Cursor.HAND);
    node.setOpacity(0.3);
    node.setEffect(new DropShadow(50, Color.GOLD));
  }

  // onExit method turns off the light around Alex when the user's mouse is off the rectangle
  @FXML
  private void onExit(MouseEvent event) {
    Node node = (Node) event.getSource();
    node.setCursor(Cursor.DEFAULT);
    node.setOpacity(0.0);
    node.setEffect(null);
  }

  // pressing the back button changes the scene to the previous scene
  @FXML
  private void backButtonClick(MouseEvent event) {
    System.out.println("Go back pressed!");
    // Make all scenes visibility to false
    chatPanelAnchorPane.setVisible(false);
    lblHint.setVisible(false);
    chatPanel.setVisible(false);
    panelAnchorPane.setVisible(false);
    panelOpenedAnchorPane.setVisible(false);

    // check and make sure the sceneHistory is not empty
    if (!sceneHistory.isEmpty()) {
      // Popping the previous anchor pane
      AnchorPane previousAnchorPane = sceneHistory.pop();
      // set the previous scene visible to the user
      previousAnchorPane.setVisible(true);
    }

    // Make back button visible only when there is a scene to back into
    backButton.setVisible(!sceneHistory.isEmpty());
  }

  // Clicking on the panel from the main scene sets the panel closeup pane visible

  @FXML
  private void onClickPanelRectangle(MouseEvent event) {
    epoch2MainPane.setVisible(false);
    panelAnchorPane.setVisible(true);

    sceneHistory.push(epoch2MainPane);

    backButton.setVisible(true);
  }

  // Clicking on the electric panel opens the electrical panel up

  @FXML
  private void onClickPanel(MouseEvent event) {
    // set the epoch 2 interaction completed variable to true on GameStateContext via the setter
    context.setInteractedE2(true);
    lblTimer.toFront();
    panelAnchorPane.setVisible(false);
    // Set text for the hint label
    lblHint.setText(
        "Whoa!\n"
            + "It seems the wires have been hit repeatedly...\n"
            + "All this exposed wiring!\n"
            + "Does Alex know about this? I should ask...");

    chatPanelController.notifyInteractableUsed(
        "The player has found the broken electrical panel. You now know they have seen it. Feel"
            + " free to discuss that you know it's broken, and to talk about Dan if asked about"
            + " him.");

    sceneHistory.push(panelAnchorPane);

    panelOpenedAnchorPane.setVisible(true);
    lblHint.setVisible(true);
  }

  /**
   * Called when the investigation timer expires or is ended early. Hides the timer label and
   * transitions to the intervention selection screen.
   */
  private void endInvestigation() throws IOException {
    lblTimer.setVisible(false);
    if (!context.isInvestigationComplete()) {
      App.setRoot("fail");
    } else {
      App.setRoot("interventionSelection");
    }
  }

  /*
    Method for button that ends the investigation early.
    - Checks first whether isInvestigationComplete method returns true from GameStateContext
  */

  @FXML
  private void onDecisionButtonClick(MouseEvent event) throws IOException {
    System.out.println("Decision button click!");
    // Check if all investigation boolean are true via the isInvestigationComplete method from
    // GameStateContext
    if (context.isInvestigationComplete()) {
      context.stopTimer();
      endInvestigation();
    } else {
      showIncompleteInvestigation(context.getIncompleteMessage());
    }
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

  @FXML
  private void onTravelClick(MouseEvent event) throws IOException {
    // Handle travel button click, navigate to epoch 2
    App.setRoot("travelPage");
  }

  private void showIncompleteInvestigation(String text) {
    incompleteInvestigationLbl.setText(text);
    incompleteInvestigationLbl.setVisible(true);

    PauseTransition delay = new PauseTransition(Duration.seconds(3));
    delay.setOnFinished(event -> incompleteInvestigationLbl.setVisible(false));
    delay.play();
  }

  @FXML
  private void danRectangleClick(MouseEvent event) {
    danVoiceLine.play();
  }

  // onMute method mutes all SFX and sets the mute button text to either mute or unmute sfx
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
}
