package ch.hoene.model;

import ch.hoene.monitor.model.TestRef;

public class ScriptTestRef extends TestRef {
    private String script;
    private ScriptType scriptType;

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public ScriptType getScriptType() {
        return scriptType;
    }

    public void setScriptType(ScriptType scriptType) {
        this.scriptType = scriptType;
    }
}
