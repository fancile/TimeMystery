// Maya Babu, mbab162, 659352321
// Importing needed package(s).
package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
// Importing needed libraries and files.
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.chat.openai.ChatResponse;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.MemorySystem.Epoch;
import nz.ac.auckland.se206.MemorySystem.Epoch1;
import nz.ac.auckland.se206.MemorySystem.Epoch2;
import nz.ac.auckland.se206.MemorySystem.Epoch3;
import nz.ac.auckland.se206.prompts.PromptEngineering;

/**
 * Controller class for the chat view. Handles user interactions and communication with the GPT
 * model via the API proxy.
 */
public class ChatController {

  // FXML handles for components of chat
  @FXML private TextArea txtaChat;
  @FXML private TextField txtInput;
  @FXML private Button btnSend;
  @FXML private Button closeChatButton;
  @FXML private AnchorPane chatRoot;
  @FXML private Button retryButton;

  // Points at whichever Alex is being spoken to.
  private ChatCompletionRequest activeRequest;

  private static boolean epoch1Initialized = false;
  private static boolean epoch2Initialized = false;
  private static boolean epoch3Initialized = false;

  // For the background thread
  private static Task<ChatMessage> currentGptTask;
  // Stores developer messages that arrive while GPT is already processing another message.
  private final Deque<ChatMessage> pendingDeveloperMessages = new ArrayDeque<>();

  // Present or past, depending on which Epoch is being spoken to.
  private String currentEpoch = "";

  private static Epoch1 epoch1 = new Epoch1();
  private static Epoch2 epoch2 = new Epoch2();
  private static Epoch3 epoch3 = new Epoch3();
  private static Epoch selectedEpoch = null;

  // Holds the last attemped  message, so it can be reused if the LLM fails to
  // generate the next message
  private String lastAttemptedMessage = "";

  // Called when the player closes the chat panel, so the parent epoch controller can hide its
  // own wrapping pane too, if it has one.
  private Runnable onCloseCallback;

  /**
   * Sets a callback to run whenever the chat panel is closed, so the epoch controller that opened
   * it can react (e.g. hide its own wrapping container).
   *
   * @param callback the code to run when chat is closed
   */
  public void setOnCloseCallback(Runnable callback) {
    this.onCloseCallback = callback;
  }

  /** Initializes the chat view. */
  @FXML
  public void initialize() {
    showRetryButton(false);
  }

  /**
   * Loads the developer prompt for the specified prompt file. An empty placeholder map is supplied
   * when the prompt template is loaded.
   *
   * @param promptFile the name of the prompt file to load
   * @return the developer prompt text for the requested epoch
   */
  private String getDeveloperPrompt(String promptFile) {
    Map<String, String> map = new HashMap<>();
    return PromptEngineering.getPrompt(promptFile, map);
  }

  /**
   * Switches the active conversation to the given epoch. The first time an epoch is visited, its
   * ChatCompletionRequest is created and its developer prompt is sent. On later visits to either
   * epoch, it switches back to the existing conversation, rather than creating a new one. Whenever
   * present-Alex is visited and past-Alex has been spoken to but the summary hasn't been sent yet,
   * a summary of that conversation is sent, so present-Alex has access to it - regardless of
   * whether this is present-Alex's first visit or not.
   *
   * @param epoch a string that reads either "present" or "past", indicating which Alex to talk to
   */
  public void setEpoch(String epoch) {
    this.currentEpoch = epoch;
    selectedEpoch = null;

    try {
      if (epoch.equalsIgnoreCase("epoch 3") && !epoch3Initialized) {
        selectedEpoch = epoch3;
        activeRequest = selectedEpoch.getRequest();

        selectedEpoch
            .getRequest()
            .addMessage(new ChatMessage("developer", getDeveloperPrompt("epoch3Alex.txt")));

        String summary =
            "For context, here is the transcript of what the player discussed with"
                + " your past self in earlier epochs:\n"
                + "--- Epoch 1 ---\n"
                + formatMessages(epoch1)
                + "\n"
                + "--- Epoch 2 ---\n"
                + formatMessages(epoch2);

        epoch3.getRequest().addMessage(new ChatMessage("developer", summary));

        runGptBackground(new ChatMessage("developer", "hi"), true);
        epoch3Initialized = true;

      } else if (epoch.equalsIgnoreCase("epoch 2") && !epoch2Initialized) {
        selectedEpoch = epoch2;
        activeRequest = selectedEpoch.getRequest();

        selectedEpoch
            .getRequest()
            .addMessage(new ChatMessage("developer", getDeveloperPrompt("epoch2Alex.txt")));

        String summary =
            "For context, here is the full transcript of what the player already discussed with"
                + " your past self (labeled 'You' for the player's own words, and 'Alex' for what"
                + " your past self said). Do not confuse the two, and use this when considering"
                + " your next response:\n"
                + formatMessages(epoch1);

        epoch2.getRequest().addMessage(new ChatMessage("developer", summary));

        runGptBackground(new ChatMessage("developer", "hi"), true);
        epoch2Initialized = true;

      } else if (epoch.equalsIgnoreCase("epoch 1") && !epoch1Initialized) {
        selectedEpoch = epoch1;
        activeRequest = selectedEpoch.getRequest();

        selectedEpoch
            .getRequest()
            .addMessage(new ChatMessage("developer", getDeveloperPrompt("epoch1Alex.txt")));

        runGptBackground(new ChatMessage("developer", "hi"), true);
        epoch1Initialized = true;

      } else {
        switch (epoch.toLowerCase()) {
          case "epoch 1":
            selectedEpoch = epoch1;
            break;
          case "epoch 2":
            selectedEpoch = epoch2;
            break;
          case "epoch 3":
            selectedEpoch = epoch3;
            break;
          default:
            throw new IllegalArgumentException("Invalid epoch: " + epoch);
        }

        activeRequest = selectedEpoch.getRequest();
      }

      txtaChat.setText(formatMessages(selectedEpoch));
      txtaChat.positionCaret(txtaChat.getLength());

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Adds a developer note to the active conversation. If GPT is busy, the note is queued and sent
   * after the current task completes.
   *
   * @param note the developer note describing the player's latest interaction
   */
  public void notifyInteractableUsed(String note) {
    ChatMessage message = new ChatMessage("developer", note);

    if (activeRequest == null || currentGptTask != null) {
      pendingDeveloperMessages.add(message);
      return;
    }

    runGptBackground(message, true);
  }

  /**
   * Resets all chat state so a new playthrough starts with no memory of previous conversations.
   * Clears both epochs' conversation histories and displayed transcripts.
   */
  public void resetChat() {
    epoch1 = new Epoch1();
    epoch2 = new Epoch2();
    epoch3 = new Epoch3();
    selectedEpoch = null;

    epoch1Initialized = false;
    epoch2Initialized = false;
    epoch3Initialized = false;
    currentEpoch = "";
    if (txtaChat != null) {
      txtaChat.setText("");
    }
    if (pendingDeveloperMessages != null) {
      pendingDeveloperMessages.clear();
    }
  }

  /**
   * Appends a chat message to the correct epoch's transcript and refreshes the display. Also
   * records the message if it came from Alex, so it can be replayed via text-to-speech.
   *
   * @param msg the chat message to append
   */
  private void appendChatMessage(ChatMessage msg) {
    if (selectedEpoch != null) {
      selectedEpoch.getMessages().add(msg);
    }
    txtaChat.setText(formatMessages(selectedEpoch));
    txtaChat.positionCaret(txtaChat.getLength());
  }

  /**
   * Disables or re-enables the input field and send button while waiting for a GPT response, and
   * updates the send button's text accordingly.
   *
   * @param isWaiting true while a response is being awaited, false once it's done
   */
  private void gptWaiting(boolean isWaiting) {
    btnSend.setDisable(isWaiting);
    txtInput.setDisable(isWaiting);
    btnSend.setText(isWaiting ? "Thinking..." : "Send");
    if (isWaiting) {
      closeChatButton.setLayoutX(623);
    } else {
      closeChatButton.setLayoutX(592);
    }
  }

  /**
   * Sends a message to GPT on a background thread, so the JavaFX Application Thread is never
   * blocked and doesn't freeze. Displays the response once it arrives, or logs the failure if the
   * request fails.
   *
   * @param message the message to send
   * @param isSetupMessage true when this request initializes an epoch conversation
   */
  private void runGptBackground(ChatMessage message, boolean isSetupMessage) {
    // If the background thread is already doing something, do not give it another task.
    if (currentGptTask != null) {
      return;
    }

    gptWaiting(true);

    // Creates a new task
    currentGptTask =
        new Task<>() {
          /**
           * Runs the GPT request on the background worker thread.
           *
           * @return the response returned by the GPT model
           * @throws Exception if the request fails
           */
          @Override
          protected ChatMessage call() throws Exception {
            return runGpt(message);
          }
        };

    // If the task could get a response from GPT.
    currentGptTask.setOnSucceeded(
        event -> {
          // Getting the generated message
          ChatMessage result = currentGptTask.getValue();

          // Adding the response to the chatbox
          appendChatMessage(result);

          // Hide the retry button
          showRetryButton(false);

          // The current background task has finished
          currentGptTask = null;
          gptWaiting(false);

          // If a developer message arrived while GPT was busy, send it now.
          if (!pendingDeveloperMessages.isEmpty()) {
            ChatMessage pendingMessage = pendingDeveloperMessages.poll();
            runGptBackground(pendingMessage, true);
          } else {
            // Puts cursor back in the input box for the next message to be typed.
            txtInput.requestFocus();
          }
        });

    // If the task couldn't get a response from GPT.
    currentGptTask.setOnFailed(
        event -> {
          // Print error to console.
          currentGptTask.getException().printStackTrace();

          if (isSetupMessage == true) {
            String errorLine = ("Alex isn't responding right now. Please try again.");
            selectedEpoch.getMessages().add(new ChatMessage("assistant", errorLine));
            txtaChat.setText(formatMessages(selectedEpoch));
            txtaChat.positionCaret(txtaChat.getLength());
            showRetryButton(true);
          } else {
            // Showing an error message in the chat.
            String errorLine = "Something went wrong. Please try again.";
            // Put the last attempted message back in the text input for the player to easily resend
            // it
            selectedEpoch.getMessages().add(new ChatMessage("assistant", errorLine));
            txtaChat.setText(formatMessages(selectedEpoch));
            txtaChat.positionCaret(txtaChat.getLength());
            showRetryButton(true);
            txtInput.setText(lastAttemptedMessage);
          }

          // clears the task, re-enables button.
          currentGptTask = null;
          gptWaiting(false);
        });

    // If the task gets cancelled.
    currentGptTask.setOnCancelled(
        event -> {
          currentGptTask = null;
          gptWaiting(false);
        });

    // Creating a thread to run - run currentGptTask, and call the task background-worker
    Thread worker = new Thread(currentGptTask, "background-worker");
    // If the app closes while this is running, just kill the thread and let the app close
    worker.setDaemon(true);
    // Starts the thread's work. Thread starts running, keeping UI responsive.
    worker.start();
  }

  /**
   * Runs the GPT model with a given chat message.
   *
   * @param msg the chat message to process
   * @return the response chat message
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  private ChatMessage runGpt(ChatMessage msg) throws ApiProxyException {
    activeRequest.addMessage(msg);

    // The call to the LLM.
    ChatCompletionResult chatCompletionResult = activeRequest.execute();

    ChatResponse response = chatCompletionResult.getChatResponse();
    activeRequest.addMessage(response.getChatMessage());
    return response.getChatMessage();
  }

  // Determines the visibility of the retry setup button
  /**
   * Shows or hides the retry button used when an epoch setup request fails.
   *
   * @param show true to display the retry button, false to hide it
   */
  private void showRetryButton(boolean show) {
    retryButton.setVisible(show);
  }

  /**
   * Retries setting up the given epoch's conversation after a failed developer-prompt request.
   * Clears the partially-created request for that epoch so setEpoch treats it as a fresh first
   * visit, and re-runs it.
   *
   * @param epoch the epoch whose setup failed and should be retried
   */
  private void retrySetup(String epoch) {
    epoch = epoch.toLowerCase().trim();
    switch (epoch) {
      case "epoch 1":
        epoch1 = new Epoch1();
        epoch1Initialized = false;
        break;
      case "epoch 2":
        epoch2 = new Epoch2();
        epoch2Initialized = false;
        break;
      case "epoch 3":
        epoch3 = new Epoch3();
        epoch3Initialized = false;
        break;
    }
    setEpoch(epoch);
  }

  public static void cancelCurrentGptTask() {
    if (currentGptTask != null) {
      currentGptTask.cancel(true);
    }
  }

  // Handles if the retry button has been clicked - the setup failed, and the user is trying again
  /**
   * Retries setup for the current epoch after a failed developer-prompt request.
   *
   * @param event the mouse event raised by the retry button
   */
  @FXML
  private void retryButtonClick(MouseEvent event) {
    showRetryButton(false);
    retrySetup(currentEpoch);
  }

  /**
   * Sends a message to the GPT model.
   *
   * @param event the action event triggered by the send button
   */
  @FXML
  private void onSendMessage(ActionEvent event) {
    String message = txtInput.getText().trim();
    if (message.isEmpty()) {
      return;
    }
    lastAttemptedMessage = message;
    txtInput.clear();

    GameStateContext context = GameStateContext.getCurrentContext();
    switch (currentEpoch.toLowerCase().trim()) {
      case "epoch 1":
        context.setSpokenEpoch1(true);
        break;
      case "epoch 2":
        context.setSpokenEpoch2(true);
        break;
      case "epoch 3":
        context.setSpokenEpoch3(true);
        break;
      default:
        break;
    }

    ChatMessage msg = new ChatMessage("user", message);
    appendChatMessage(msg);
    runGptBackground(msg, false);
  }

  /**
   * Hides the chat view and invokes the optional callback supplied by the parent controller.
   *
   * @param event the mouse event raised by the close button
   * @throws IOException declared by the FXML event-handler signature
   */
  @FXML
  private void onCloseChat(MouseEvent event) throws IOException {
    chatRoot.setVisible(false);
    if (onCloseCallback != null) {
      onCloseCallback.run();
    }
  }

  /**
   * Converts an epoch's stored chat messages into the transcript displayed in the chat area.
   *
   * @param epoch the epoch whose conversation should be formatted
   * @return the formatted transcript with labels for Alex and the player
   */
  private String formatMessages(Epoch epoch) {
    StringBuilder builder = new StringBuilder();

    for (ChatMessage message : epoch.getMessages()) {
      if (message.getRole().equalsIgnoreCase("assistant")) {
        builder.append("Alex : ");
      } else if (message.getRole().equalsIgnoreCase("user")) {
        builder.append("You : ");
      }

      builder.append(message.getContent()).append("\n\n");
    }

    return builder.toString();
  }
}
