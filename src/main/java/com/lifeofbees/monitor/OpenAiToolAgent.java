package com.lifeofbees.monitor;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseFunctionToolCall;
import com.openai.models.responses.ResponseInputItem;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OpenAiToolAgent {

    private final OpenAIClient openAIClient;
    private final WebsiteCheckTool websiteCheckTool;
    private final ApplicationStatusTool applicationStatusTool;
    private final ReadApplicationLogTool readApplicationLogTool;
    private final RestartApplicationTool restartApplicationTool;

    public OpenAiToolAgent(
            OpenAIClient openAIClient,
            WebsiteCheckTool websiteCheckTool,
            ApplicationStatusTool applicationStatusTool,
            ReadApplicationLogTool readApplicationLogTool,
            RestartApplicationTool restartApplicationTool) {

        this.openAIClient = openAIClient;
        this.websiteCheckTool = websiteCheckTool;
        this.applicationStatusTool = applicationStatusTool;
        this.readApplicationLogTool = readApplicationLogTool;
        this.restartApplicationTool = restartApplicationTool;
    }

    public String investigateWebsite() {

        List<ResponseInputItem> inputs = new ArrayList<>();

        inputs.add(
                ResponseInputItem.ofMessage(
                        ResponseInputItem.Message.builder()
                                .addInputTextContent("""
                                        Investigate the website
                                        https://lifeofbees.co.uk.
                                        
                                        The website has been reported as unavailable.
                                        
                                        You have these tools:
                                        
                                        - checkWebsite: checks whether the website is responding.
                                        - checkApplicationStatus: checks whether the Spring Boot
                                          application container is running.
                                        - readApplicationLog: reads the recent application logs.
                                        - restartApplication: restarts the Spring Boot application
                                          container.
                                        
                                        Investigate the problem before attempting any repair.
                                        
                                        Check the application status and read the application logs
                                        when appropriate.
                                        
                                        Only restart the application if your investigation indicates
                                        that restarting it could reasonably resolve the problem.
                                        
                                        After a restart, always check the website again.
                                        
                                        Do not perform any other repair action.
                                        
                                        At the end, provide a short report describing:
                                        - what you found
                                        - what action you took
                                        - whether the website recovered
                                        """)
                                .role(ResponseInputItem.Message.Role.USER)
                                .build()
                )
        );

        ResponseCreateParams.Builder paramsBuilder =
                ResponseCreateParams.builder()
                        .model(ChatModel.GPT_5)
                        .addTool(CheckWebsite.class)
                        .addTool(CheckApplicationStatus.class)
                        .addTool((ReadApplicationLog.class))
                        .addTool(RestartApplication.class)
                        .input(ResponseCreateParams.Input.ofResponse(inputs));

        var response =
                openAIClient.responses()
                        .create(paramsBuilder.build());

        for (var item : response.output()) {

            if (item.isReasoning()) {

                inputs.add(
                        ResponseInputItem.ofReasoning(
                                item.asReasoning()
                        )
                );
            }

            if (item.isFunctionCall()) {

                ResponseFunctionToolCall functionCall =
                        item.asFunctionCall();

                inputs.add(
                        ResponseInputItem.ofFunctionCall(functionCall)
                );

                if (functionCall.name().equals("CheckWebsite")) {

                    WebsiteStatus status =
                            websiteCheckTool.checkWebsite();

                    inputs.add(
                            ResponseInputItem.ofFunctionCallOutput(
                                    ResponseInputItem.FunctionCallOutput
                                            .builder()
                                            .callId(functionCall.callId())
                                            .outputAsJson(status)
                                            .build()
                            )
                    );
                }

                if (functionCall.name().equals("CheckApplicationStatus")) {

                    String status =
                            applicationStatusTool.checkApplicationStatus();

                    inputs.add(
                            ResponseInputItem.ofFunctionCallOutput(
                                    ResponseInputItem.FunctionCallOutput
                                            .builder()
                                            .callId(functionCall.callId())
                                            .output(status)
                                            .build()
                            )
                    );
                }
                if (functionCall.name().equals("ReadApplicationLog")) {

                    String logs =
                            readApplicationLogTool.readApplicationLog();

                    inputs.add(
                            ResponseInputItem.ofFunctionCallOutput(
                                    ResponseInputItem.FunctionCallOutput
                                            .builder()
                                            .callId(functionCall.callId())
                                            .output(logs)
                                            .build()
                            )
                    );
                }
                if (functionCall.name().equals("RestartApplication")) {

                    String result =
                            restartApplicationTool.restartApplication();

                    inputs.add(
                            ResponseInputItem.ofFunctionCallOutput(
                                    ResponseInputItem.FunctionCallOutput
                                            .builder()
                                            .callId(functionCall.callId())
                                            .output(result)
                                            .build()
                            )
                    );
                }
            }
        }

        paramsBuilder.input(
                ResponseCreateParams.Input.ofResponse(inputs)
        );

        var finalResponse =
                openAIClient.responses()
                        .create(paramsBuilder.build());

        return finalResponse.output()
                .stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .map(outputText -> outputText.text())
                .reduce("", (a, b) -> a + b);
    }

    @JsonClassDescription(
            "Checks whether the LifeOfBees website is responding."
    )
    public static class CheckWebsite {

        @com.fasterxml.jackson.annotation.JsonPropertyDescription(
                "Reason why the website should be checked."
        )
        public String reason;

        public WebsiteStatus execute() {
            return null;
        }
    }

    @JsonClassDescription(
            "Checks the status of the LifeOfBees Spring Boot application."
    )
    public static class CheckApplicationStatus {

        @com.fasterxml.jackson.annotation.JsonPropertyDescription(
                "Reason why the application status should be checked."
        )
        public String reason;

        public String execute() {
            return null;
        }
    }

    @JsonClassDescription(
            "Reads the recent logs of the LifeOfBees Spring Boot application."
    )
    public static class ReadApplicationLog {

        @com.fasterxml.jackson.annotation.JsonPropertyDescription(
                "Reason why the application logs should be read."
        )
        public String reason;

        public String execute() {
            return null;
        }
    }

    @JsonClassDescription(
            "Restarts the LifeOfBees Spring Boot application container."
    )
    public static class RestartApplication {

        @com.fasterxml.jackson.annotation.JsonPropertyDescription(
                "Reason why the application container should be restarted."
        )
        public String reason;

        public String execute() {
            return null;
        }
    }
}