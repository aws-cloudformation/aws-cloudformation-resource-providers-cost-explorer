package software.amazon.ce.costcategory;

import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.CreateCostCategoryDefinitionResponse;
import software.amazon.cloudformation.proxy.*;

public class CreateHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();

        CreateCostCategoryDefinitionResponse response = proxy.injectCredentialsAndInvokeV2(
                RequestBuilder.buildCreateRequest(model),
                CostExplorerClient.builder().build()::createCostCategoryDefinition
        );

        model.setArn(response.costCategoryArn());
        model.setEffectiveStart(response.effectiveStart());

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModel(model)
            .status(OperationStatus.SUCCESS)
            .build();
    }
}
