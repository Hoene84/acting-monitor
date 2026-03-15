package ch.hoene.monitor.orchestrator.model;

import java.util.HashMap;
import java.util.Map;

public class Params {

    private Map<String, Map<String, String>> components = new HashMap<>();
    private Map<String, Map<String, String>> instances = new HashMap<>();

    public Map<String, Map<String, String>> getComponents() {
        return components;
    }

    public void setComponents(Map<String, Map<String, String>> components) {
        this.components = components;
    }

    public Map<String, Map<String, String>> getInstances() {
        return instances;
    }

    public void setInstances(Map<String, Map<String, String>> instances) {
        this.instances = instances;
    }
}
