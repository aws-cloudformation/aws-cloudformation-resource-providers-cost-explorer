package software.amazon.ce.costcategory;

import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.cloudformation.proxy.*;

/**
 * CloudFormation invokes this handler when the resource is deleted,
 * either when the resource is deleted from the stack as part of a stack update operation, or the stack itself is deleted.
 */
public class DeleteHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();
        final CostExplorerClient client = CostExplorerClient.create();

        proxy.injectCredentialsAndInvokeV2(
                CostCategoryRequestBuilder.buildDeleteRequest(model),
                client::deleteCostCategoryDefinition
        );

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModel(model)
            .status(OperationStatus.SUCCESS)
            .build();
    }
}
