package software.amazon.ce.anomalysubscription;

import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.CreateAnomalySubscriptionResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.cloudformation.proxy.HandlerErrorCode;


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
        CreateAnomalySubscriptionResponse response = proxy.injectCredentialsAndInvokeV2(
                RequestBuilder.buildCreateAnomalySubscriptionRequest(model),
                costExplorerClient::createAnomalySubscription
        );

        model.setSubscriptionArn(response.subscriptionArn());

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(model)
                .status(OperationStatus.SUCCESS)
                .build();
    }

    private boolean hasReadOnlyProperties(final ResourceModel model) {
        return model.getSubscriptionArn() != null;
    }
}
