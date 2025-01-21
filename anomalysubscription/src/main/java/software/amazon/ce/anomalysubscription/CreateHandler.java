package software.amazon.ce.anomalysubscription;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.CreateAnomalySubscriptionResponse;
import software.amazon.awssdk.services.costexplorer.model.UnknownSubscriptionException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.cloudformation.proxy.HandlerErrorCode;

import static software.amazon.ce.anomalysubscription.Utils.SUBSCRIPTION_ALREADY_EXISTS;

public class CreateHandler extends AnomalySubscriptionBaseHandler {

    public CreateHandler() {
        super();
    }

    public CreateHandler(CostExplorerClient costExplorerClient) {
        super(costExplorerClient);
    }

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();

        /**
         * Make sure user is not trying to assign values to readOnlyProperties
         * */
        if (hasReadOnlyProperties(model)) {
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModel(model)
                    .status(OperationStatus.FAILED)
                    .errorCode(HandlerErrorCode.InvalidRequest)
                    .message("Attempting to set a ReadOnly Property.")
                    .build();
        }

        try {
            CreateAnomalySubscriptionResponse response = proxy.injectCredentialsAndInvokeV2(
                    RequestBuilder.buildCreateAnomalySubscriptionRequest(model, request),
                    costExplorerClient::createAnomalySubscription
            );

            model.setSubscriptionArn(response.subscriptionArn());
        } catch (Exception e) {
            if (e instanceof AwsServiceException) {
                String errorMessage = ((AwsServiceException) e).awsErrorDetails().errorMessage();
                if (SUBSCRIPTION_ALREADY_EXISTS.equals(errorMessage)) {
                    return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .resourceModel(model)
                            .status(OperationStatus.FAILED)
                            .errorCode(HandlerErrorCode.AlreadyExists)
                            .build();
                }
                throw e;
            }
            throw e;
        }

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(model)
                .status(OperationStatus.SUCCESS)
                .build();
    }

    private boolean hasReadOnlyProperties(final ResourceModel model) {
        return model.getSubscriptionArn() != null;
    }
}
