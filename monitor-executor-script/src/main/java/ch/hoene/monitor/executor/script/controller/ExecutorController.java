package ch.hoene.monitor.executor.script.controller;

import ch.hoene.monitor.executor.script.model.ScriptTestRef;
import ch.hoene.monitor.executor.script.model.ScriptTestResult;
import ch.hoene.monitor.executor.script.model.ScriptType;
import ch.hoene.monitor.client.api.ActorApi;
import ch.hoene.monitor.client.invoker.ApiClient;
import ch.hoene.monitor.model.InstanceIdTestIdBody;
import ch.hoene.monitor.model.TestExecution;
import ch.hoene.monitor.model.TestRef;
import ch.hoene.monitor.model.TestResult;
import ch.hoene.monitor.server.api.ExecutorApi;
import ch.hoene.monitor.util.LoggingApiCallback;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class ExecutorController implements ExecutorApi {

    Logger logger = LoggerFactory.getLogger(ExecutorController.class);
    ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public ResponseEntity<List<TestRef>> getTests() {
        try {
            return new ResponseEntity<>(Arrays.asList(objectMapper.readValue(new File("./tests/tests.json"), TestRef[].class)), HttpStatusCode.valueOf(200));
        } catch (IOException e) {
            logger.error(new File(".").getAbsolutePath());
            logger.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<Void> execute(String instanceId, String testId, InstanceIdTestIdBody body) {

        try {
            ScriptTestRef scriptTestRef = Arrays.stream(objectMapper.readValue(new File("./tests/tests.json"), ScriptTestRef[].class)).filter(testRef -> testRef.getId().equals(testId)).findFirst().orElseThrow();
            TestResult testResult = this.executeScript(scriptTestRef.getScriptType(), scriptTestRef.getScript(), body.getParams());

            body.getPublishTo().forEach(pulishTo -> {
                try {
                    ApiClient apiClient = new ApiClient();
                    apiClient.setBasePath(pulishTo);
                    ActorApi actorApi = new ActorApi(apiClient);
                    actorApi.receiveAsync(
                            new TestExecution()
                                    .testRunId(UUID.randomUUID().toString())
                                    .executionTimeInMillis(5)
                                    .startTimestamp(OffsetDateTime.now()),
                            testResult,
                            testId,
                            instanceId,
                            new LoggingApiCallback<>(logger, pulishTo));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private TestResult executeScript(ScriptType scriptType, String script, Map<String, String> params) {

        String pidFileName = String.format("%s.%s.pid.json", script, UUID.randomUUID());
        String pidFilePath = String.format("./tests/%s/%s", scriptType.getDir(), pidFileName);
        File pidFile = new File(pidFilePath);
        try {
            String paramString = Stream.concat(params.entrySet().stream(), Stream.of(Map.entry("pidFile", pidFile)))
                    .map(param -> String.format("-%s %s", param.getKey(), param.getValue())).collect(Collectors.joining(" "));
            String cmdPattern = switch (scriptType) {
                case BASH -> "./tests/%s/%s %s";
                case POWERSHELL ->
                        "cmd.exe /c start /wait Powershell.exe -executionpolicy remotesigned -NoLogo -File ./tests/%s/%s %s";
            };
            String cmd = String.format(cmdPattern, scriptType.getDir(), script, paramString);
            logger.info("executing cmd: {}", cmd);
            Runtime.getRuntime().exec(cmd).waitFor();
            ScriptTestResult testResult = objectMapper.readValue(pidFile, ScriptTestResult.class);

            if(testResult.getErrors().length > 0) {
                Arrays.stream(testResult.getErrors()).forEach(error -> logger.error(String.format("Error calling %s script %s", scriptType, script), error));
                return TestResult.ERROR;
            }
            return testResult.getTestResult();
        } catch (Exception e) {
            logger.error(String.format("Error calling %s script %s", scriptType, script), e);
            return TestResult.ERROR;
        } finally {
            pidFile.delete();
        }
    }
}
