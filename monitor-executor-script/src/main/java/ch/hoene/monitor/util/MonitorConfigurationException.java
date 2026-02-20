package ch.hoene.monitor.util;

public class MonitorConfigurationException extends RuntimeException {
    public MonitorConfigurationException(String message) {
        super(message);
    }

    public MonitorConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
