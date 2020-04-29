package software.amazon.ce.costcategory;

import software.amazon.awssdk.services.costexplorer.model.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

/**
 * CloudFormation invokes this handler when the resource is deleted,
 * either when the resource is deleted from the stack as part of a stack update operation, or the stack itself is deleted.
 */
public class DeleteHandler extends CostCategoryBaseHandler {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();

        try {
            proxy.injectCredentialsAndInvokeV2(
                    CostCategoryRequestBuilder.buildDeleteRequest(model),
                    costExplorerClient::deleteCostCategoryDefinition
            );
        } catch (ResourceNotFoundException ex) {
            // If resource has already been deleted outside stack, skip deletion and mark it success.
            logger.log(String.format("Cost category %s does not exist, skip deletion.", model.getArn()));
        }

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModel(model)
            .status(OperationStatus.SUCCESS)
            .build();
    }
}
