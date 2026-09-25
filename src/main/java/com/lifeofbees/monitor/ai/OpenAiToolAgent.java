package com.lifeofbees.monitor.ai;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.lifeofbees.monitor.monitoring.WebsiteStatus;
import com.lifeofbees.monitor.tool.ApplicationStatusTool;
import com.lifeofbees.monitor.tool.ReadApplicationLogTool;
import com.lifeofbees.monitor.tool.RestartApplicationTool;
import com.lifeofbees.monitor.tool.WebsiteCheckTool;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseFunctionToolCall;
import com.openai.models.responses.ResponseInputItem;
import com.openai.models.responses.ResponseOutputText;
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

    public AiInvestigationResult investigateWebsite() {

        List<ResponseInputItem> inputs = new ArrayList<>();
        boolean websiteRecovered = false;
        List<String> actions = new ArrayList<>();
        boolean restartPerformed = false;
        boolean verificationRequired = false;


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
                                        
                                        You may restart the application container at most once
                                        during this investigation.
                                        
                                         Never restart the application more than once.
                                        
                                        If you restart the application, you must check the website
                                        again afterwards.
                                        
                                        After the post-restart website check, do not restart the
                                         application again.
                                        
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
                        .addTool(ReadApplicationLog.class)
                        .addTool(RestartApplication.class);

        while (true) {

            paramsBuilder.input(
                    ResponseCreateParams.Input.ofResponse(inputs)
            );

            var response =
                    openAIClient.responses()
                            .create(paramsBuilder.build());

            boolean hasFunctionCall = false;

            for (var item : response.output()) {

                if (item.isReasoning()) {

                    inputs.add(
                            ResponseInputItem.ofReasoning(
                                    item.asReasoning()
                            )
                    );
                }

                if (item.isFunctionCall()) {

                    hasFunctionCall = true;

                    ResponseFunctionToolCall functionCall =
                            item.asFunctionCall();

                    inputs.add(
                            ResponseInputItem.ofFunctionCall(functionCall)
                    );

                    if (functionCall.name().equals("CheckWebsite")) {

                        WebsiteStatus status =
                                websiteCheckTool.checkWebsite();
                        websiteRecovered = status.available();
                        if (restartPerformed) {
                            verificationRequired = false;
                        }
                        actions.add(
                                "CheckWebsite: HTTP "
                                        + status.statusCode()
                                        + ", available="
                                        + status.available()
                        );
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
                        actions.add(
                                "CheckApplicationStatus: " + status
                        );
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
                        actions.add(
                                "ReadApplicationLog: " + logs
                        );
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

                        String result;

                        if (restartPerformed) {

                            result =
                                    "Restart denied: the application has already "
                                            + "been restarted during this investigation.";

                        } else {

                            result =
                                    restartApplicationTool.restartApplication();

                            restartPerformed = true;
                            verificationRequired = true;
                        }

                        actions.add(
                                "RestartApplication: " + result
                        );

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


            if (!hasFunctionCall && verificationRequired) {

                WebsiteStatus status =
                        websiteCheckTool.checkWebsite();

                websiteRecovered = status.available();
                verificationRequired = false;

                actions.add(
                        "CheckWebsite (mandatory after restart): HTTP "
                                + status.statusCode()
                                + ", available="
                                + status.available()
                );

                inputs.add(
                        ResponseInputItem.ofMessage(
                                ResponseInputItem.Message.builder()
                                        .addInputTextContent(
                                                "Mandatory website verification after restart: "
                                                        + "HTTP status "
                                                        + status.statusCode()
                                                        + ", available="
                                                        + status.available()
                                        )
                                        .role(ResponseInputItem.Message.Role.USER)
                                        .build()
                        )
                );

                continue;
            }

            if (!hasFunctionCall) {

                String report =
                        response.output()
                                .stream()
                                .flatMap(item -> item.message().stream())
                                .flatMap(message -> message.content().stream())
                                .flatMap(content -> content.outputText().stream())
                                .map(ResponseOutputText::text)
                                .reduce("", (a, b) -> a + b);

                return new AiInvestigationResult(
                        report,
                        websiteRecovered,
                        actions
                );
            }
        }
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