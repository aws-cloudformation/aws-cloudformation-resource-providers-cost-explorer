package software.amazon.ce.anomalymonitor;

import software.amazon.awssdk.core.internal.http.loader.DefaultSdkHttpClientBuilder;
import software.amazon.awssdk.http.SdkHttpConfigurationOption;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.utils.AttributeMap;

import java.time.Duration;

public abstract class AnomalyMonitorBaseHandler extends BaseHandler<CallbackContext> {
    
    private static final Duration HTTP_READ_TIMEOUT = Duration.ofSeconds(65);

    protected final CostExplorerClient costExplorerClient;

    public AnomalyMonitorBaseHandler() {
        AttributeMap httpOptions = AttributeMap.builder()
                .put(SdkHttpConfigurationOption.READ_TIMEOUT, HTTP_READ_TIMEOUT)
                .build();

        this.costExplorerClient = CostExplorerClient.builder()
                .httpClient(new DefaultSdkHttpClientBuilder().buildWithDefaults(httpOptions))
                .build();
    }
}
