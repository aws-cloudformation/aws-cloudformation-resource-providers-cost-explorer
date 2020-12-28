package software.amazon.ce.anomalymonitor;

import software.amazon.awssdk.services.costexplorer.model.UnknownMonitorException;
import software.amazon.awssdk.services.costexplorer.model.DeleteAnomalyMonitorResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.HandlerErrorCode;

public class DeleteHandler extends AnomalyMonitorBaseHandler {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();

        try {
            DeleteAnomalyMonitorResponse response = proxy.injectCredentialsAndInvokeV2(
                    RequestBuilder.buildDeleteAnomalyMonitorRequest(model),
                    costExplorerClient::deleteAnomalyMonitor
            );
        } catch (UnknownMonitorException e) {
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
