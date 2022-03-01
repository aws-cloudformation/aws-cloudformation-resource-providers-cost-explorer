package software.amazon.ce.anomalymonitor;

import software.amazon.awssdk.core.internal.http.loader.DefaultSdkHttpClientBuilder;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;

class TestUtils {
    static CostExplorerClient generateTestClient() {
        return CostExplorerClient.builder()
                .httpClient(new DefaultSdkHttpClientBuilder().build())
                .build();
    }
}