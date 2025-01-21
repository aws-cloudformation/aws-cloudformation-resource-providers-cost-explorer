package software.amazon.ce.anomalymonitor;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.CreateAnomalyMonitorResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.cloudformation.proxy.HandlerErrorCode;

import static software.amazon.ce.anomalymonitor.Utils.DIMENSIONAL_MONITOR_ALREADY_EXISTS;
import static software.amazon.ce.anomalymonitor.Utils.MONITOR_ALREADY_EXISTS;

/**
 * create: CloudFormation invokes this handler when the resource is initially created during stack create operations.
 * */
public class CreateHandler extends AnomalyMonitorBaseHandler {

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
            CreateAnomalyMonitorResponse response = proxy.injectCredentialsAndInvokeV2(
                    RequestBuilder.buildCreateAnomalyMonitorRequest(model, request),
                    costExplorerClient::createAnomalyMonitor
            );

            model.setMonitorArn(response.monitorArn());
        } catch (Exception e) {
            if (e instanceof AwsServiceException) {
                String errorMessage = ((AwsServiceException) e).awsErrorDetails().errorMessage();

                // if duplicate monitor, return an AlreadyExists exception per CFN contract tests
                if (MONITOR_ALREADY_EXISTS.equals(errorMessage) || DIMENSIONAL_MONITOR_ALREADY_EXISTS.equals(errorMessage)) {
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
        return model.getMonitorArn() != null || model.getCreationDate() != null || model.getDimensionalValueCount() != null
            || model.getLastEvaluatedDate() != null || model.getLastUpdatedDate() != null || model.getDimensionalValueCount() != null;
    }
}
