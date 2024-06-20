package software.amazon.ce.costcategory;

import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.CostCategory;
import software.amazon.awssdk.services.costexplorer.model.DescribeCostCategoryDefinitionResponse;
import software.amazon.awssdk.services.costexplorer.model.ResourceNotFoundException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

/**
 * CloudFormation invokes this handler as part of a stack update operation
 * when detailed information about the resource's current state is required.
 */
public class ReadHandler extends CostCategoryBaseHandler {

    public ReadHandler() {
        super();
    }

    public ReadHandler(CostExplorerClient costExplorerClient) {
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
            DescribeCostCategoryDefinitionResponse response = proxy.injectCredentialsAndInvokeV2(
                    CostCategoryRequestBuilder.buildDescribeRequest(model),
                    costExplorerClient::describeCostCategoryDefinition
            );

            CostCategory costCategory = response.costCategory();
            model.setName(costCategory.name());
            model.setEffectiveStart(costCategory.effectiveStart());
            model.setRuleVersion(costCategory.ruleVersionAsString());
            model.setRules(CostCategoryParser.costCategoryRulesToJson(costCategory.rules()));
            model.setSplitChargeRules(CostCategoryParser.costCategorySplitChargeRulesToJson(costCategory));
            model.setDefaultValue(costCategory.defaultValue());

            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModel(model)
                    .status(OperationStatus.SUCCESS)
                    .build();
        } catch (ResourceNotFoundException ex) {
            throw new CfnNotFoundException(ex);
        }
    }
}
