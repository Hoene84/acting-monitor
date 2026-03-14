package ch.hoene.monitor.executor.script.model;

import ch.hoene.monitor.model.TestResult;

public class ScriptTestResult {
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
