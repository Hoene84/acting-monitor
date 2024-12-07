package ch.hoene.monitor.controller;

import ch.hoene.monitor.model.Instance;
import ch.hoene.monitor.model.TestParams;
import ch.hoene.monitor.server.api.OrchestratorApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrchestratorController implements OrchestratorApi {

    @Value("${ch.hoene.monitor.defaultExecutorUrl}")
    private String defaultExecutorUrl;

    @Value("${ch.hoene.monitor.defaultActor}")
    private String defaultActor;



    @Override
    public ResponseEntity<List<Instance>> getInstances() {
        TestParams testParams = new TestParams();
        testParams.put("url", "www.google.com");
        return new ResponseEntity<>(
                List.of(
                        new Instance()
                                .id("google")
                                .type("website")
                                .addExecutorsItem(defaultExecutorUrl)
                                .publishTo(List.of(
                                        defaultActor
                                ))
                                .params(testParams)
                ),
                HttpStatusCode.valueOf(200));
    }
}
