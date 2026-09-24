package nz.ac.auckland.se206.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javafx.concurrent.Task;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest.Model;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest.ReasoningEffort;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.chat.openai.ChatResponse;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.prompts.PromptEngineering;

public class RationaleChatController {
  private ChatCompletionRequest request;

  private ChatCompletionRequest createRequest() throws ApiProxyException {
    ApiProxyConfig config = ApiProxyConfig.readConfig();

    return new ChatCompletionRequest(config)
        .setModel(Model.GPT_5_4_NANO)
        .setReasoningEffort(ReasoningEffort.MEDIUM)
        .setMaxCompletionTokens(750);
  }

  public void evaluateRationale(
      String selectedIntervention,
      String rationale,
      Consumer<String> onSuccess,
      Runnable onFailure) {

    Task<String> rationaleTask =
        new Task<>() {
          @Override
          protected String call() throws Exception {
            request = createRequest();

            Map<String, String> map = new HashMap<>();
            map.put("selected intervention", selectedIntervention);
            map.put("player rationale", rationale);

            String prompt = PromptEngineering.getPrompt("rationaleEvaluator.txt", map);

            request.addMessage(new ChatMessage("developer", prompt));

            ChatCompletionResult result = request.execute();
            ChatResponse response = result.getChatResponse();

            return response.getChatMessage().getContent();
          }
        };

    rationaleTask.setOnSucceeded(
        event -> {
          String evaluation = rationaleTask.getValue();
          System.out.println("Evaluation: " + evaluation);
          onSuccess.accept(evaluation);
        });

    rationaleTask.setOnFailed(
        event -> {
          rationaleTask.getException().printStackTrace();
          onFailure.run();
        });

    Thread worker = new Thread(rationaleTask, "rationale-background-worker");
    worker.setDaemon(true);
    worker.start();
  }
}
