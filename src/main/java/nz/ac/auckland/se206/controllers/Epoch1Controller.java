package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
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

public class Epoch1Controller {

  @FXML private Rectangle alexAtDeskRect;
  @FXML private Rectangle drawerRect;
  @FXML private Label lblTimer;
  @FXML private AnchorPane chatPanel;
  @FXML private Button backButton;
  @FXML private ChatController chatPanelController;
  @FXML private Rectangle finalFolder;
  @FXML private Label openFolderLabel;
  AnchorPane currentlyVisible = null;
  @FXML private Button endInvestigationBtn;
  @FXML private Circle travelCircle;

  @FXML private AnchorPane alexAtDeskScene;
  @FXML private AnchorPane finalConcertFolder;
  @FXML private AnchorPane openFolder;
  @FXML private Label incompleteInvestigationLbl;
  @FXML private Button muteBtn; // mute button

  @FXML private Deque<AnchorPane> sceneHistory = new ArrayDeque<>();
  private List<AnchorPane> allScenes;

  private AudioClip drawerOpenSound =
      new AudioClip(
          getClass().getResource("/sounds/Epoch 1 Sounds/drawerOpen.mp3").toExternalForm());

  private AudioClip drawerCloseSound =
      new AudioClip(
          getClass().getResource("/sounds/Epoch 1 Sounds/drawerClose.mp3").toExternalForm());

  GameStateContext context = GameStateContext.getCurrentContext();

  private boolean discoveredList = false;

  // Initialise method that initialises the initial code when the scene first opens.
  @FXML
  public void initialize() {
    allScenes = List.of(alexAtDeskScene, finalConcertFolder, openFolder);
    chatPanel.setVisible(false);
    backButton.setVisible(false);
    alexAtDeskScene.setVisible(true);
    finalConcertFolder.setVisible(false);
    openFolder.setVisible(false);
    lblTimer.setVisible(true);
    incompleteInvestigationLbl.setVisible(false);
    bindTimer();
    if (!context.getMuted()) {
      muteBtn.setText("Unmute SFX");
    } else {
      muteBtn.setText("Mute SFX");
    }
  }

  private boolean listDiscoverySent = false;

  // onAlexClick opens the chat box for the scene when the rectangle/Alex is clicked
  @FXML
  private void alexAtDeskClick(MouseEvent event) throws IOException {
    chatPanelController.setEpoch("epoch 1");
    if (discoveredList == true && listDiscoverySent == false) {
      chatPanelController.notifyInteractableUsed(
          "The player has found your potential acts to cut list, and has discovered you are"
              + " planning on cutting Dan from the group. No need to hide it anymore.");
      listDiscoverySent = true;
    }

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

  @FXML
  private void finalFolderClick(MouseEvent event) {
    context.setInteractedE1(true);
    finalConcertFolder.setVisible(false);
    openFolder.setOpacity(1);
    openFolder.setVisible(true);
    sceneHistory.push(finalConcertFolder);
    openFolderLabel.setText("Dan and his band? Top choice to be cut?\nInteresting...");
    discoveredList = true;
  }

  @FXML
  private void drawerRectClick(MouseEvent event) {
    alexAtDeskScene.setVisible(false);
    finalConcertFolder.setVisible(true);
    finalConcertFolder.setOpacity(1);

    AudioManager.playSfx(drawerOpenSound);

    backButton.setVisible(true);
    sceneHistory.push(alexAtDeskScene);
  }

  // onExit method turns off the light around Alex when the user's mouse is off the rectangle
  @FXML
  private void onExit(MouseEvent event) {
    Node node = (Node) event.getSource();
    node.setCursor(Cursor.DEFAULT);
    node.setOpacity(0.0);
    node.setEffect(null);
  }

  @FXML
  private void backButtonClick(MouseEvent event) {
    AnchorPane previousAnchorPane = sceneHistory.pop();

    if (previousAnchorPane == alexAtDeskScene) {
      AudioManager.playSfx(drawerCloseSound);
    }

    AnchorPane currentlyVisible =
        allScenes.stream().filter(AnchorPane::isVisible).findFirst().orElse(null);

    if (currentlyVisible != null) {
      currentlyVisible.setVisible(false);
    }

    previousAnchorPane.setOpacity(1);
    previousAnchorPane.setVisible(true);

    backButton.setVisible(!sceneHistory.isEmpty());
  }

  /**
   * Called when the investigation is ended early with all requirements met. Hides the timer label
   * and transitions to the intervention selection screen.
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

  // Mute method for the mute button - checks whether mute button has been pressed already and sets
  // text accordingly mute or unmute
  @FXML
  private void onMute(ActionEvent event) {
    if (context.getMuted()) {
      muteBtn.setText("Unmute SFX");
      context.setMuted(false);
    } else {
      muteBtn.setText("Mute SFX");
      context.setMuted(true);
    }
  }
}
