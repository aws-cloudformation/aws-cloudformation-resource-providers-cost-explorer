package software.amazon.ce.anomalysubscription;

import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.UnknownSubscriptionException;
import software.amazon.awssdk.services.costexplorer.model.DeleteAnomalySubscriptionResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.HandlerErrorCode;

public class DeleteHandler extends AnomalySubscriptionBaseHandler {

    public DeleteHandler() {
        super();
    }

    public DeleteHandler(CostExplorerClient costExplorerClient) {
        super(costExplorerClient);
    }

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();

        try {
            DeleteAnomalySubscriptionResponse response = proxy.injectCredentialsAndInvokeV2(
                    RequestBuilder.buildDeleteAnomalySubscriptionRequest(model),
                    costExplorerClient::deleteAnomalySubscription
            );
        } catch (UnknownSubscriptionException e) {
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .status(OperationStatus.FAILED)
                    .errorCode(HandlerErrorCode.NotFound)
                    .build();
        }

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .status(OperationStatus.SUCCESS)
                .build();
    }
}
