package software.amazon.ce.costcategory;

import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.CreateCostCategoryDefinitionResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

/**
 * CloudFormation invokes this handler when the resource is initially created during stack create operations.
 */
public class CreateHandler extends CostCategoryBaseHandler {



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
        final ProxyClient<CostExplorerClient> proxyClient,
        final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();

        try {
            CreateCostCategoryDefinitionResponse response = proxy.injectCredentialsAndInvokeV2(
                    CostCategoryRequestBuilder.buildCreateRequest(model, request),
                    costExplorerClient::createCostCategoryDefinition);

            model.setArn(response.costCategoryArn());
            model.setEffectiveStart(response.effectiveStart());
        } catch (Exception e) {
            return handleError(e, model, callbackContext);
        }

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModel(model)
            .status(OperationStatus.SUCCESS)
            .build();
    }
}
