package software.amazon.ce.costcategory;

import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.CostCategory;
import software.amazon.awssdk.services.costexplorer.model.DescribeCostCategoryDefinitionResponse;
import software.amazon.cloudformation.proxy.*;

/**
 * CloudFormation invokes this handler as part of a stack update operation
 * when detailed information about the resource's current state is required.
 */
public class ReadHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();
        final CostExplorerClient client = CostExplorerClient.create();

        DescribeCostCategoryDefinitionResponse response = proxy.injectCredentialsAndInvokeV2(
                CostCategoryRequestBuilder.buildDescribeRequest(model),
                client::describeCostCategoryDefinition
        );

        CostCategory costCategory = response.costCategory();
        model.setName(costCategory.name());
        model.setEffectiveStart(costCategory.effectiveStart());
        model.setRuleVersion(costCategory.ruleVersionAsString());
        model.setRules(CostCategoryRulesParser.toJson(costCategory.rules()));

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModel(model)
            .status(OperationStatus.SUCCESS)
            .build();
    }
}
