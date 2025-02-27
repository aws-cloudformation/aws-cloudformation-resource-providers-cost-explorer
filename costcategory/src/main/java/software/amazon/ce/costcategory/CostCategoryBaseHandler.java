package software.amazon.ce.costcategory;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.internal.http.loader.DefaultSdkHttpClientBuilder;
import software.amazon.awssdk.http.SdkHttpConfigurationOption;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.ResourceNotFoundException;
import software.amazon.awssdk.utils.AttributeMap;
import software.amazon.awssdk.utils.ImmutableMap;
import software.amazon.cloudformation.exceptions.BaseHandlerException;
import software.amazon.cloudformation.exceptions.CfnAccessDeniedException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

/**
 * Base class for cost category resource handler.
 */
public abstract class CostCategoryBaseHandler extends BaseHandler<CallbackContext> {

    public static final String COST_CATEGORY_ALREADY_EXISTS_PATTERN
        = "^Failed to create Cost Category: Cost category name .* already exists";
    public static final String ACCESS_DENIED_EXCEPTION = "AccessDeniedException";

    /**
     * The timeout for cost category API on server side is 60 seconds.
     * Change the read timeout to be more than 60 seconds, so the client will
     * wait for the server instead of retrying when creating cost category.
     */
    private static final Duration HTTP_READ_TIMEOUT = Duration.ofSeconds(65);

    protected final CostExplorerClient costExplorerClient;

    // Cost Categories is global in partition. Thus, in order to choose the global region when constructing the client,
    // we need to have this map from partition to global region
    protected static final Map<String, Region> partitionToGlobalRegionMap = ImmutableMap.of(
            Region.CN_NORTH_1.metadata().partition().name(), Region.AWS_CN_GLOBAL,
            Region.US_EAST_1.metadata().partition().name(), Region.AWS_GLOBAL
    );

    public CostCategoryBaseHandler() {
        AttributeMap httpOptions = AttributeMap.builder()
                .put(SdkHttpConfigurationOption.READ_TIMEOUT, HTTP_READ_TIMEOUT)
                .build();

        this.costExplorerClient = CostExplorerClient.builder()
                .httpClient(new DefaultSdkHttpClientBuilder().buildWithDefaults(httpOptions))
                .region(partitionToGlobalRegionMap.get(
                        Region.of(System.getenv("AWS_REGION")).metadata().partition().name()))
                .build();
    }

    public CostCategoryBaseHandler(CostExplorerClient costExplorerClient) {
        this.costExplorerClient = costExplorerClient;
    }

    @Override
    public final ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {
        return handleRequest(
            proxy,
            request,
            callbackContext != null ? callbackContext : new CallbackContext(),
            proxy.newProxy(() -> costExplorerClient),
            logger
        );
    }

    protected abstract ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<CostExplorerClient> proxyClient,
        final Logger logger);

    protected ProgressEvent<ResourceModel, CallbackContext> handleError(
            final Exception e,
            final ResourceModel model,
            final CallbackContext context) {

        BaseHandlerException ex;
        if (e instanceof AwsServiceException) {
            String errorMessage = getErrorMessage(e);
            String errorCode = getErrorCode(e);
            // if duplicated cost category, return an AlreadyExists exception per CFN contract test
            if (errorMessage.matches(COST_CATEGORY_ALREADY_EXISTS_PATTERN)) {
                return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModel(model)
                    .status(OperationStatus.FAILED)
                    .errorCode(HandlerErrorCode.AlreadyExists)
                    .build();
            } else if (ACCESS_DENIED_EXCEPTION.equals(errorCode)) {
                ex = new CfnAccessDeniedException(e);
            } else if (e instanceof ResourceNotFoundException) {
                ex = new CfnNotFoundException(e);
            } else {
                ex = new CfnGeneralServiceException(e);
            }
        }  else {
            ex = new CfnGeneralServiceException(e);
        }

        return ProgressEvent.failed(model, context, ex.getErrorCode(), ex.getMessage());
    }

    protected static String getErrorMessage(Exception e) {
        if (e instanceof AwsServiceException) {
            AwsServiceException ex = (AwsServiceException) e;
            if(ex.awsErrorDetails() != null)
                return ex.awsErrorDetails().errorMessage();
        }
        return e.getMessage();
    }

    protected static String getErrorCode(Exception e) {
        if (e instanceof AwsServiceException) {
            AwsServiceException ex = (AwsServiceException) e;
            if(ex.awsErrorDetails() != null)
                return ex.awsErrorDetails().errorCode();
        }
        return e.getMessage();
    }

}
