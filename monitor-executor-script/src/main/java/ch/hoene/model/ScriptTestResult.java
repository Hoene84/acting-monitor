package ch.hoene.model;

import ch.hoene.monitor.model.TestRef;
import ch.hoene.monitor.model.TestResult;

public class ScriptTestResult extends TestRef {
    private TestResult testResult;
    private String[] errors;

    public String[] getErrors() {
        return errors;
    }

    public void setErrors(String[] errors) {
        this.errors = errors;
    }

    public TestResult getTestResult() {
        return testResult;
    }

    public void setTestResult(TestResult testResult) {
        this.testResult = testResult;
    }
}
