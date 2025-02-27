package software.amazon.ce.costcategory

import software.amazon.awssdk.services.costexplorer.model.DeleteCostCategoryDefinitionRequest
import software.amazon.awssdk.services.costexplorer.model.DeleteCostCategoryDefinitionResponse
import software.amazon.awssdk.services.costexplorer.model.ResourceNotFoundException
import software.amazon.cloudformation.exceptions.CfnNotFoundException
import software.amazon.cloudformation.proxy.HandlerErrorCode
import software.amazon.cloudformation.proxy.OperationStatus

import static software.amazon.ce.costcategory.Fixtures.*

class DeleteHandlerTest extends HandlerSpecification {

    def handler = new DeleteHandler(ceClient)

    def "Test: handleRequest"() {
        given:
        def model = ResourceModel.builder()
            .arn(COST_CATEGORY_ARN)
            .build()
        def deleteResponse = DeleteCostCategoryDefinitionResponse.builder().build()

        when:
        def event = handler.handleRequest(proxy, request, callbackContext, logger)

        then:
        1 * request.getDesiredResourceState() >> model
        1 * proxy.injectCredentialsAndInvokeV2(*_) >> { DeleteCostCategoryDefinitionRequest deleteRequest, _  ->
            assert deleteRequest.costCategoryArn() == model.arn
            deleteResponse
        }

        event.status == OperationStatus.SUCCESS
        event.resourceModel == null
    }

    def "Test: handleRequest throws CfnNotFoundException when resource has already been deleted"() {
        given:
        def model = ResourceModel.builder()
                .arn(COST_CATEGORY_ARN)
                .build()
        proxy.injectCredentialsAndInvokeV2(*_) >> { DeleteCostCategoryDefinitionRequest r, _ ->
            throw ResourceNotFoundException.builder().message("error").build()
        }

        when:
        def event = handler.handleRequest(proxy, request, callbackContext, logger)

        then:
        1 * request.getDesiredResourceState() >> model
        event.status == OperationStatus.FAILED
        event.errorCode == HandlerErrorCode.NotFound
    }
}
