package software.amazon.ce.costcategory;

import software.amazon.awssdk.core.internal.http.loader.DefaultSdkHttpClientBuilder;
import software.amazon.awssdk.http.SdkHttpConfigurationOption;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.utils.AttributeMap;

import java.time.Duration;

/**
 * Base class for cost category resource handler.
 */
public abstract class CostCategoryBaseHandler extends BaseHandler<CallbackContext> {

    /**
     * The timeout for cost category API on server side is 60 seconds.
     * Change the read timeout to be more than 60 seconds, so the client will
     * wait for the server instead of retrying when creating cost category.
     */
    private static final Duration HTTP_READ_TIMEOUT = Duration.ofSeconds(65);

    protected final CostExplorerClient costExplorerClient;

    public CostCategoryBaseHandler() {
        AttributeMap httpOptions = AttributeMap.builder()
                .put(SdkHttpConfigurationOption.READ_TIMEOUT, HTTP_READ_TIMEOUT)
                .build();

        this.costExplorerClient = CostExplorerClient.builder()
                .httpClient(new DefaultSdkHttpClientBuilder().buildWithDefaults(httpOptions))
                .build();
    }
}
