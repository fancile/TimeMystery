package nz.ac.auckland.se206.MemorySystem;

import java.util.ArrayList;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;

/**
 * Defines the common state and behavior shared by all conversation epochs in the game timeline.
 * Each epoch stores its display name, message history, and GPT request.
 */
public abstract class Epoch {
  /** The display name of this epoch. */
  protected String name;

  /** The ordered history of messages exchanged during this epoch. */
  protected ArrayList<ChatMessage> messages;

  /**
   * Creates an epoch with the supplied display name and an empty message history.
   *
   * @param name the display name of the epoch
   */
  public Epoch(String name) {
    this.name = name;
    this.messages = new ArrayList<>();
  }

  /**
   * Returns the display name of this epoch.
   *
   * @return the epoch name
   */
  public abstract String getName();

  /**
   * Returns the ordered message history for this epoch.
   *
   * @return the list of messages exchanged during the epoch
   */
  public abstract ArrayList<ChatMessage> getMessages();

  /**
   * Returns the concrete epoch instance.
   *
   * @return this epoch
   */
  public abstract Epoch getEpoch();

  /**
   * Returns the GPT request associated with this epoch.
   *
   * @return the chat completion request for this epoch
   */
  public abstract ChatCompletionRequest getRequest();
}
