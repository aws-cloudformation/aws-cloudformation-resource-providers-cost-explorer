package software.amazon.ce.costcategory;

import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.Map;
import java.util.Set;

/**
 * CloudFormation invokes this handler when the resource is updated as part of a stack update operation.
 */
public class UpdateHandler extends CostCategoryBaseHandler {

    public UpdateHandler() {
        super();
    }

    public UpdateHandler(CostExplorerClient costExplorerClient) {
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

        return ProgressEvent.progress(model, callbackContext)
            .then(progress -> proxy.initiate("AWS-CE-CostCategory::Update", proxyClient, model, callbackContext)
                .translateToServiceRequest(CostCategoryRequestBuilder::buildUpdateRequest)
                .makeServiceCall((awsRequest, client) -> client.injectCredentialsAndInvokeV2(awsRequest, client.client()::updateCostCategoryDefinition))
                .handleError((awsRequest, exception, client, _model, context) -> handleError(exception, _model, context))
                .progress()
            )
            .then(progress -> {
                final Map<String, String> previousTags = TagHelper.getPreviouslyAttachedTags(request);
                final Map<String, String> desiredTags = TagHelper.getNewDesiredTags(model, request);

                final Map<String, String> tagsToAdd = TagHelper.generateTagsToAdd(previousTags, desiredTags);
                final Set<String> tagsToRemove = TagHelper.generateTagsToRemove(previousTags, desiredTags);

                try {
                    if (!tagsToRemove.isEmpty()) {
                        proxy.injectCredentialsAndInvokeV2(CostCategoryRequestBuilder.buildUntagResourceRequest(progress.getResourceModel(), tagsToRemove), costExplorerClient::untagResource);
                    }
                    if (!tagsToAdd.isEmpty()) {
                        proxy.injectCredentialsAndInvokeV2(CostCategoryRequestBuilder.buildTagResourceRequest(progress.getResourceModel(), tagsToAdd), costExplorerClient::tagResource);
                    }
                } catch (Exception e) {
                    return handleError(e, model, callbackContext);
                }
                return ProgressEvent.defaultSuccessHandler(progress.getResourceModel());
              }
            );
    }
}
