package ch.hoene.monitor.orchestrator.controller;

import ch.hoene.monitor.model.Instance;
import ch.hoene.monitor.model.TestParams;
import ch.hoene.monitor.orchestrator.model.Component;
import ch.hoene.monitor.orchestrator.model.Infrastructure;
import ch.hoene.monitor.server.api.OrchestratorApi;
import ch.hoene.monitor.util.MonitorConfigurationException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

@RestController
public class OrchestratorController implements OrchestratorApi {

    Logger logger = LoggerFactory.getLogger(OrchestratorController.class);
    ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public ResponseEntity<List<Instance>> getInstances() {
        try {

            Infrastructure infrastructure = objectMapper.readValue(new File("./infrastructure/infrastructure.json"), Infrastructure.class);
            List<Instance> instances = new ArrayList<>();
            addInstances("root", instances, new ArrayList<>(), new HashMap<>(), infrastructure);
            return new ResponseEntity<>(instances, HttpStatusCode.valueOf(200));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private void addInstances(String componentId, List<Instance> instanceCollector, List<String> parents, Map<String, String> parentParams, Infrastructure infrastructure) {

        Component component = Optional.ofNullable(infrastructure.getComponents().get(componentId))
                .orElseThrow(() -> new MonitorConfigurationException("component %s not found for child of %s".formatted(componentId, String.join("/", parents))));

        List<String> types = new ArrayList<>(component.getTypes());
        types.add(componentId);

        TestParams params = new TestParams();
        params.putAll(infrastructure.getParams().getComponents().getOrDefault(componentId, new HashMap<>()));
        params.putAll(parentParams);

        component.getInstances().forEach(instanceId -> {

            TestParams instanceParams = new TestParams();
            instanceParams.putAll(params);
            instanceParams.putAll(infrastructure.getParams().getInstances().getOrDefault(instanceId, new HashMap<>()));

            instanceCollector.add(
                    new Instance()
                            .id(instanceId)
                            .parents(parents)
                            .types(types)
                            .executors(infrastructure.getDefaultExecutors())
                            .publishTo(infrastructure.getDefaultActors())
                            .params(instanceParams));
        });


        List<String> instances = component.getInstances();
        //for components without instances, just create the childs with a pseudo parent with componentId as id
        if(instances.isEmpty()) {
            instances.add(componentId);
        }

        instances.forEach(instanceId -> {
            component.getChilds().forEach(childComponentId -> {
                addInstances(
                        childComponentId,
                        instanceCollector,
                        Stream.concat(parents.stream(), Stream.of(instanceId)).toList(),
                        params,
                        infrastructure);
            });
        });
    }
}
