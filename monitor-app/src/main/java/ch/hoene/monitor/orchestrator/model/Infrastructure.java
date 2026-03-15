package ch.hoene.monitor.orchestrator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Infrastructure {
    private Map<String, Component> components;
    private Params params = new Params();
    private List<String> defaultActors = new ArrayList<>();
    private List<String> defaultExecutors = new ArrayList<>();

    public Map<String, Component> getComponents() {
        return components;
    }

    public void setComponents(Map<String, Component> components) {
        this.components = components;
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public List<String> getDefaultActors() {
        return defaultActors;
    }

    public void setDefaultActors(List<String> defaultActors) {
        this.defaultActors = defaultActors;
    }

    public List<String> getDefaultExecutors() {
        return defaultExecutors;
    }

    public void setDefaultExecutors(List<String> defaultExecutors) {
        this.defaultExecutors = defaultExecutors;
    }
}
