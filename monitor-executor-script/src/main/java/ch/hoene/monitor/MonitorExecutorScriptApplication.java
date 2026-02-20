package ch.hoene.monitor;

import ch.hoene.monitor.client.api.OrchestratorApi;
import ch.hoene.monitor.client.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class MonitorExecutorScriptApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitorExecutorScriptApplication.class, args);

    }

}
