package software.amazon.ce.costcategory;

import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.CostCategory;
import software.amazon.awssdk.services.costexplorer.model.DescribeCostCategoryDefinitionResponse;
import software.amazon.awssdk.services.costexplorer.model.ResourceNotFoundException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
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
        final ProxyClient<CostExplorerClient> proxyClient = proxy.newProxy(() -> costExplorerClient);

        return ProgressEvent.progress(model, callbackContext)
            .then(progress -> proxy.initiate("AWS-CE-CostCategory::Read", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                .translateToServiceRequest(CostCategoryRequestBuilder::buildDescribeRequest)
                .makeServiceCall((awsRequest, client) -> client.injectCredentialsAndInvokeV2(awsRequest, client.client()::describeCostCategoryDefinition))
                .handleError((awsRequest, exception, client, _model, context) -> {
                    if (exception instanceof ResourceNotFoundException) {
                        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                                .resourceModel(_model)
                                .status(OperationStatus.FAILED)
                                .errorCode(HandlerErrorCode.NotFound)
                                .build();
                    }
                    throw exception;
                })
                .done(response -> {
                    CostCategory costCategory = response.costCategory();
                    model.setName(costCategory.name());
                    model.setEffectiveStart(costCategory.effectiveStart());
                    model.setRuleVersion(costCategory.ruleVersionAsString());
                    model.setRules(CostCategoryParser.costCategoryRulesToJson(costCategory.rules()));
                    model.setSplitChargeRules(CostCategoryParser.costCategorySplitChargeRulesToJson(costCategory));
                    model.setDefaultValue(costCategory.defaultValue());

                    return ProgressEvent.progress(model, callbackContext);
                })
            )
            .then(progress -> proxy.initiate("AWS-CE-CostCategory::ListTags", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                .translateToServiceRequest(CostCategoryRequestBuilder::buildListTagsForResourceRequest)
                .makeServiceCall((awsRequest, client) -> client.injectCredentialsAndInvokeV2(awsRequest, client.client()::listTagsForResource))
                .done(response -> {
                    model.setResourceTags(CostCategoryParser.toCFNResourceTags(response.resourceTags()));
                    return ProgressEvent.defaultSuccessHandler(model);
                })
            );
    }
}
