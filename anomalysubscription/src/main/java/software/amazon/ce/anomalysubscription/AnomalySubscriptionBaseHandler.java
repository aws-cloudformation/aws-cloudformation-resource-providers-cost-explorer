package software.amazon.ce.anomalysubscription;

import software.amazon.awssdk.core.internal.http.loader.DefaultSdkHttpClientBuilder;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.http.SdkHttpConfigurationOption;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.utils.AttributeMap;
import software.amazon.awssdk.utils.ImmutableMap;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Duration;
import java.util.Map;

/**
 * Base class for anomaly subscription resource handler.
 */
public abstract class AnomalySubscriptionBaseHandler extends BaseHandler<CallbackContext> {

    /**
     * The timeout for anomaly subscription API on server side is 60 seconds.
     * Change the read timeout to be more than 60 seconds, so the client will
     * wait for the server instead of retrying when creating anomaly subscription
     */
    private static final Duration HTTP_READ_TIMEOUT = Duration.ofSeconds(65);

    protected final CostExplorerClient costExplorerClient;

    // Anomaly Subscription is global in partition. Thus, in order to choose the global region when constructing the client,
    // we need to have this map from partition to global region
    protected static final Map<String, Region> partitionToGlobalRegionMap = ImmutableMap.of(
            Region.CN_NORTH_1.metadata().partition().name(), Region.AWS_CN_GLOBAL,
            Region.US_EAST_1.metadata().partition().name(), Region.AWS_GLOBAL
    );

    public AnomalySubscriptionBaseHandler() {
        AttributeMap httpOptions = AttributeMap.builder()
                .put(SdkHttpConfigurationOption.READ_TIMEOUT, HTTP_READ_TIMEOUT)
                .build();

        this.costExplorerClient = CostExplorerClient.builder()
                .httpClient(new DefaultSdkHttpClientBuilder().buildWithDefaults(httpOptions))
                .region(partitionToGlobalRegionMap.get(
                        Region.of(System.getenv("AWS_REGION")).metadata().partition().name()))
                .build();
    }

    public AnomalySubscriptionBaseHandler(CostExplorerClient costExplorerClient) {
        this.costExplorerClient = costExplorerClient;
    }

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger
    ) {
        return handleRequest(
            proxy,
            request,
            callbackContext != null ? callbackContext : new CallbackContext(),
            proxy.newProxy(() -> costExplorerClient),
            logger
        );
    }

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<CostExplorerClient> proxyClient,
        final Logger logger
    ) {
        throw new RuntimeException("Handler needs to implement either one of the handleRequest methods");
    }
}
