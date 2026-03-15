package ch.hoene.monitor.orchestrator.model;

import java.util.ArrayList;
import java.util.List;

public class Component {
    private List<String> instances = new ArrayList<>();
    private List<String> childs = new ArrayList<>();
    private List<String> types = new ArrayList<>();

    public List<String> getInstances() {
        return instances;
    }

    public void setInstances(List<String> instances) {
        this.instances = instances;
    }

    public List<String> getChilds() {
        return childs;
    }

    public void setChilds(List<String> childs) {
        this.childs = childs;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }
}
