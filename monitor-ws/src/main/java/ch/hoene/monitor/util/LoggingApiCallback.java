package ch.hoene.monitor.util;

import ch.hoene.monitor.client.invoker.ApiCallback;
import ch.hoene.monitor.client.invoker.ApiException;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class LoggingApiCallback<T> implements ApiCallback<T> {

    private final Logger logger;
    private final String endPointId;
    private final Consumer<T> onSuccess;

    public LoggingApiCallback(Logger logger, String endPointId, Consumer<T> onSuccess) {
        this.logger = logger;
        this.endPointId = endPointId;
        this.onSuccess = onSuccess;
    }

    public LoggingApiCallback(Logger logger, String endPointId) {
        this.logger = logger;
        this.endPointId = endPointId;
        this.onSuccess = t -> {
        };
    }

    @Override
    public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
        logger.error("Calling [" + endPointId + "] failed, statusCode: " + statusCode, e);
    }

    @Override
    public void onSuccess(T result, int statusCode, Map<String, List<String>> responseHeaders) {
        logger.info("Calling Executor [" + endPointId + "] succesful, statusCode: " + statusCode);
        onSuccess.accept(result);
    }

    @Override
    public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {
        //nothing
    }

    @Override
    public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {
        //nothing
    }
}
