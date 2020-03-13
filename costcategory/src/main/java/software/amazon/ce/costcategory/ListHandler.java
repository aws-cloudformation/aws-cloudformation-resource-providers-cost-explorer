package software.amazon.ce.costcategory;

import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.ListCostCategoryDefinitionsResponse;
import software.amazon.cloudformation.proxy.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CloudFormation invokes this handler when summary information about multiple resources of this resource provider is required.
 */
public class ListHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final CostExplorerClient client = CostExplorerClient.create();

        ListCostCategoryDefinitionsResponse response = proxy.injectCredentialsAndInvokeV2(
                CostCategoryRequestBuilder.buildListRequest(request.getNextToken()),
                client::listCostCategoryDefinitions
        );

        List<ResourceModel> models = response.costCategoryReferences().stream()
                .map(c -> ResourceModel.builder()
                        .arn(c.costCategoryArn())
                        .effectiveStart(c.effectiveStart())
                        .name(c.name())
                        .build()
                ).collect(Collectors.toList());

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModels(models)
                .nextToken(response.nextToken())
                .status(OperationStatus.SUCCESS)
                .build();
    }
}
