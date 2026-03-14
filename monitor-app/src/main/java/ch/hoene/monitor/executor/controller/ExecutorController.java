package ch.hoene.monitor.executor.controller;

import ch.hoene.monitor.client.api.ActorApi;
import ch.hoene.monitor.client.invoker.ApiClient;
import ch.hoene.monitor.model.*;
import ch.hoene.monitor.server.api.ExecutorApi;
import ch.hoene.monitor.util.LoggingApiCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
public class ExecutorController implements ExecutorApi {

    Logger logger = LoggerFactory.getLogger(ExecutorController.class);

    @Override
    public ResponseEntity<List<TestRef>> getTests() {
        return new ResponseEntity<>(
                List.of(
                        new TestRef()
                                .id("NoopTest")
                                .suitableFor("website")
                                .frequency(Scheduling.SECOND)                ),
                HttpStatusCode.valueOf(200));
    }

    @Override
    public ResponseEntity<Void> execute(String instanceId, String testId, InstanceIdTestIdBody body) {
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
                        TestResult.SUCCESS,
                        testId,
                        instanceId,
                        new LoggingApiCallback<>(logger, pulishTo));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return ResponseEntity.ok().build();
    }
}
