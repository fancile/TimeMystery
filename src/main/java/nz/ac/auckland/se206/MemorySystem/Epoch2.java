package nz.ac.auckland.se206.MemorySystem;

import java.util.ArrayList;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest.Model;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest.ReasoningEffort;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;

/**
 * Represents the second conversation epoch, containing its message history and configured GPT
 * request.
 */
public class Epoch2 extends Epoch {
  /** The ordered message history for the Epoch 2 conversation. */
  private ArrayList<ChatMessage> messages = new ArrayList<>();

  /** The GPT request used to continue the Epoch 2 conversation. */
  private ChatCompletionRequest request;

  /*
   * Creates and configures the GPT request used by this epoch.
   */
  {
    try {
      ApiProxyConfig config = ApiProxyConfig.readConfig();
      request =
          new ChatCompletionRequest(config)
              .setModel(Model.GPT_5_4_NANO)
              .setReasoningEffort(ReasoningEffort.LOW)
              .setMaxCompletionTokens(250);
    } catch (ApiProxyException e) {
      e.printStackTrace();
    }
  }

  /** Creates the Epoch 2 conversation and assigns it the display name "Epoch 2". */
  public Epoch2() {
    super("Epoch 2");
  }

  /**
   * Returns the display name of this epoch.
   *
   * @return the epoch name
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * Returns the ordered message history for this epoch.
   *
   * @return the messages exchanged during Epoch 2
   */
  @Override
  public ArrayList<ChatMessage> getMessages() {
    return messages;
  }

  /**
   * Returns this concrete epoch instance.
   *
   * @return this Epoch 2 instance
   */
  @Override
  public Epoch getEpoch() {
    return this;
  }

  /**
   * Returns the GPT request associated with this epoch.
   *
   * @return the chat completion request for Epoch 2
   */
  @Override
  public ChatCompletionRequest getRequest() {
    return request;
  }
}
