package ch.hoene.monitor;

import ch.hoene.monitor.client.api.OrchestratorApi;
import ch.hoene.monitor.client.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MonitorApplication {

    @Value("${ch.hoene.monitor.orchestratorBaseUrl}")
    private String orchestratorBaseUrl;

    public static void main(String[] args) {
        SpringApplication.run(MonitorApplication.class, args);

    }


    @Bean
    OrchestratorApi orchestratorApi() {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(orchestratorBaseUrl);
        apiClient.setDebugging(true);
        return new OrchestratorApi(apiClient);
    }

//    @PostConstruct
//    public void init(OrchestratorApi orchestratorApi) {
//        try {
//            orchestratorApi.getInstances().forEach(instance -> {
//                System.out.println(instance.getId());
//            });
//        } catch (ApiException e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println("Post" + orchestratorUrl);
//    }

}
