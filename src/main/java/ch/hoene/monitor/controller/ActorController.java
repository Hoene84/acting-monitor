package ch.hoene.monitor.controller;

import ch.hoene.monitor.model.TestExecution;
import ch.hoene.monitor.model.TestResult;
import ch.hoene.monitor.server.api.ActorApi;
import ch.hoene.monitor.service.SchedulerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class ActorController implements ActorApi {

    Logger logger = LoggerFactory.getLogger(ActorController.class);

    @Override
    public ResponseEntity<Void> receive(TestResult testResult, String testId, String instanceId, TestExecution body) {
        logger.info("TEST testResult: " + testResult.toString() + " testId: " + testId + " instanceId: " + instanceId + " body: " + body);
        return ResponseEntity.ok().build();
    }
}
