package ch.hoene.model;

public enum ScriptType {
    BASH("bash"),
    POWERSHELL("ps");

    private final String dir;

    ScriptType(String dir) {
        this.dir = dir;
    }

    public String getDir() {
        return dir;
    }
}
