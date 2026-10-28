// Importing needed package(s)
package nz.ac.auckland.se206.controllers;

// Importing needed libraries.
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.AudioManager;
import nz.ac.auckland.se206.GameStateContext;

public class IntroController {
  // Creating FXML files for the elements in the intro scene
  @FXML private Button introButton;
  @FXML private Label introTextLabel;
  @FXML private Button muteBtn;

  // create the GameStateContext object
  private GameStateContext context = new GameStateContext();

  // Holds the page number the player is currently on
  private int pageNumber = 1;
  // Holds the total number of pages into the intro
  private int totalPages = 4;

  /** Initalises the scene, shows the first page */
  @FXML
  public void initialize() {
    introButton.setPrefSize(68, 26);
    introButton.setVisible(true);
    introTextLabel.setVisible(true);
    pageNumber = 1;
    showPage(pageNumber);
    // Start BGM
    AudioManager.playBackgroundMusic();
    // Set the mute button text as required.
    if (!context.getMuted()) {
      muteBtn.setText("Unmute SFX");
    } else {
      muteBtn.setText("Mute SFX");
    }
  }

  // Mute method for the mute button - checks whether mute button has been pressed already and sets
  // text accordingly mute or unmute
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

  /**
   * Handles showing the correct page in the intro slate.
   *
   * @param page the page number that the user is on
   */
  public void showPage(int page) {
    // Switching between possible page numbers
    switch (page) {
      // If the player is on the first page of the intro slate
      case 1:
        // Setting the text of the intro label.
        introTextLabel.setText(
            "This is Harmony Hall.\n"
                + " Twenty years ago, it burnt down, days before a huge concert.\n"
                + " The hall's owner, Alex, never found out why.");
        // Setting the button text to say "Next"
        introButton.setText("Next");
        break;
      // If the player is on the second page of the intro slate
      case 2:
        introTextLabel.setText(
            "You are a time travelling investigator.\n"
                + " Move between the past and the present, speak with Alex, gather evidence,"
                + " and uncover what really happened, before the fire ever starts.");
        introButton.setText("Next");
        break;
      // If the player is on the third page of the intro slate
      case 3:
        introTextLabel.setText(
            "There is a clock in each epoch. Click it to travel between time periods.\n"
                + "Anything that glows gold can be examined, opened, or moved - and may hold a"
                + " clue.");
        introButton.setText("Next");
        break;
      // If the player is on the fourth and final page of the intro slate
      case 4:
        // Changing the size of the button to be able to fit the text
        introButton.setPrefSize(137, 26);
        introTextLabel.setText("Good luck!");
        // Setting the button text to say "Start Investigation"
        introButton.setText("Start Investigation");
        break;
    }
  }

  /**
   * Handles shifting between intro pages and starting the investigation
   *
   * @param event the mouse event triggered when the player clicks on the intro scene button
   */
  @FXML
  private void onIntroButtonClick(MouseEvent event) throws IOException {
    // Move to the next page if the player has not reached the final page.
    if (pageNumber < totalPages) {
      pageNumber++;
      showPage(pageNumber);
    } else {
      // Start the investigation once the player reaches the end of the intro.
      System.out.println("click");
      GameStateContext context = GameStateContext.getCurrentContext();
      // Start the five minute investigation timer.
      context.startTimer(
          300,
          () ->
              javafx.application.Platform.runLater(
                  () -> {
                    try {
                      ChatController.cancelCurrentGptTask();
                      // Check whether the investigation was completed when the timer ends.
                      if (context.isInvestigationComplete()) {
                        App.setRoot("interventionSelection");
                      } else {
                        // If incomplete, move to the fail screen.
                        App.setRoot("fail");
                      }
                    } catch (IOException e) {
                      // Print the error if the scene cannot be loaded.
                      e.printStackTrace();
                    }
                  }));
      // Move from the intro to the time travel page.
      App.setRoot("travelPage");
    }
  }
}
