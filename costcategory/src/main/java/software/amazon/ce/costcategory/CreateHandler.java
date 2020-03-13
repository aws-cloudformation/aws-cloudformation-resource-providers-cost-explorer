package software.amazon.ce.costcategory;

import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.CreateCostCategoryDefinitionResponse;
import software.amazon.cloudformation.proxy.*;

/**
 * CloudFormation invokes this handler when the resource is initially created during stack create operations.
 */
public class CreateHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();
        final CostExplorerClient client = CostExplorerClient.create();

        CreateCostCategoryDefinitionResponse response = proxy.injectCredentialsAndInvokeV2(
                CostCategoryRequestBuilder.buildCreateRequest(model),
                client::createCostCategoryDefinition
        );

        model.setArn(response.costCategoryArn());
        model.setEffectiveStart(response.effectiveStart());

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModel(model)
            .status(OperationStatus.SUCCESS)
            .build();
    }
}
