package software.amazon.ce.costcategory;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.CreateCostCategoryDefinitionResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

/**
 * CloudFormation invokes this handler when the resource is initially created during stack create operations.
 */
public class CreateHandler extends CostCategoryBaseHandler {

    public static final String COST_CATEGORY_ALREADY_EXISTS_PATTERN
            = "^Failed to create Cost Category: Cost category name .* already exists";

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
        final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();

        try {
            CreateCostCategoryDefinitionResponse response = proxy.injectCredentialsAndInvokeV2(
                    CostCategoryRequestBuilder.buildCreateRequest(model, request),
                    costExplorerClient::createCostCategoryDefinition);

            model.setArn(response.costCategoryArn());
            model.setEffectiveStart(response.effectiveStart());
        } catch (Exception e) {
            if (e instanceof AwsServiceException) {
                String errorMessage = ((AwsServiceException) e).awsErrorDetails().errorMessage();
                // if duplicated cost category, return an AlreadyExists exception per CFN contract test
                if (errorMessage.matches(COST_CATEGORY_ALREADY_EXISTS_PATTERN)) {
                    return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .resourceModel(model)
                            .status(OperationStatus.FAILED)
                            .errorCode(HandlerErrorCode.AlreadyExists)
                            .build();
                }
                throw e;
            }
            throw e;
        }

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModel(model)
            .status(OperationStatus.SUCCESS)
            .build();
    }
}
