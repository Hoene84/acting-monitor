package ch.hoene.monitor.service;

import ch.hoene.monitor.client.api.ExecutorApi;
import ch.hoene.monitor.client.api.OrchestratorApi;
import ch.hoene.monitor.client.invoker.ApiClient;
import ch.hoene.monitor.client.invoker.ApiException;
import ch.hoene.monitor.model.InstanceIdTestIdBody;
import ch.hoene.monitor.util.LoggingApiCallback;
import ch.hoene.monitor.util.MonitorConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Component
public class SchedulerService {

    Logger logger = LoggerFactory.getLogger(SchedulerService.class);

    private int retries = 5; //TODO: replace with proper error handling
    private boolean initalized = false; //TODO: replace with proper error handling

    final OrchestratorApi orchestratorApi;

    public SchedulerService(OrchestratorApi orchestratorApi) throws ApiException {
        this.orchestratorApi = orchestratorApi;
    }

    @Bean
    public Executor taskExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Scheduled(fixedRate = 1000)
    public void scheduleJobs() {
        logger.info("schedule jobs, retries: " + retries + " initalized: " + initalized );
        if(initalized) {
            return;
        } else if (retries-- == 0) {
            throw new MonitorConfigurationException("Not able connect");
        }
        try {
            orchestratorApi.getInstances().forEach(instance -> {
                instance.getExecutors().forEach(executor -> {
                    ApiClient apiClient = new ApiClient();
                    apiClient.setBasePath(executor);
                    ExecutorApi executorApi = new ExecutorApi(apiClient);
                    try {
                        executorApi.getTestsAsync(new LoggingApiCallback<>(logger, executor, testRefs -> testRefs.stream()
                                .filter(testRef -> testRef.getSuitableFor().equals(instance.getType()))
                                .forEach(testRef -> {
                                    List<String> missingParams = testRef.getRequiredParams().stream().filter(e -> !instance.getParams().containsKey(e)).toList();
                                    if (missingParams.isEmpty()) {
                                        TimeUnit timeUnit = switch (testRef.getFrequency()) {
                                            case SECOND -> TimeUnit.SECONDS;
                                            case MINUTE -> TimeUnit.MINUTES;
                                            case HOUR -> TimeUnit.HOURS;
                                        };
                                        logger.info("schedule test " + testRef.getId() + " for instance " + instance.getId() + " every: " + timeUnit);
                                        Executors.newScheduledThreadPool(10).scheduleWithFixedDelay(
                                                () -> {
                                                    try {
                                                        logger.info("call executor " + executor + " for instance " + instance.getId() + " for test " + testRef.getId());
                                                        executorApi.executeAsync(
                                                                new InstanceIdTestIdBody()
                                                                        .params(instance.getParams())
                                                                        .publishTo(instance.getPublishTo()),
                                                                instance.getId(),
                                                                testRef.getId(),
                                                                new LoggingApiCallback<>(logger, executor));
                                                    } catch (Exception e) {
                                                        logger.error("call executor " + executor + " for instance " + instance.getId() + " for test " + testRef.getId(), e);
                                                    }
                                                },
                                                1,
                                                1,
                                                timeUnit
                                        );
                                    } else  {
                                        throw new MonitorConfigurationException("Instance" + instance.getId() + " of type " + instance.getType() + " has missing parameters for test " + executor + "/" +  testRef.getId() + ": " + missingParams);
                                    }
                                })));
                    } catch (ApiException e) {
                        new MonitorConfigurationException("Cannot get available tests for executor " + executor, e);
                    }

                });
            });
            initalized = true;
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

}
