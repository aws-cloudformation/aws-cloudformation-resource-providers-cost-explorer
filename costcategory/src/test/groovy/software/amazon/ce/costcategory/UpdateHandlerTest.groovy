package software.amazon.ce.costcategory

import software.amazon.awssdk.services.costexplorer.model.UpdateCostCategoryDefinitionRequest
import software.amazon.awssdk.services.costexplorer.model.UpdateCostCategoryDefinitionResponse
import software.amazon.cloudformation.proxy.OperationStatus

import static software.amazon.ce.costcategory.Fixtures.*

class UpdateHandlerTest extends HandlerSpecification {

    def handler = new UpdateHandler()

    def "Test: handleRequest"() {
        given:
        def updateResponse = UpdateCostCategoryDefinitionResponse.builder()
            .costCategoryArn(COST_CATEGORY_ARN)
            .effectiveStart(COST_CATEGORY_EFFECTIVE_START)
            .build()

        def model = ResourceModel.builder()
            .name(COST_CATEGORY_NAME)
            .ruleVersion(RULE_VERSION)
            .rules("[ ${JSON_RULE_DIMENSION} ]")
            .build()

        when:
        def event = handler.handleRequest(proxy, request, callbackContext, logger)

        then:
        1 * request.getDesiredResourceState() >> model
        1 * proxy.injectCredentialsAndInvokeV2(*_) >> { UpdateCostCategoryDefinitionRequest updateRequest, _ ->
            updateRequest.costCategoryArn() == COST_CATEGORY_ARN
            updateRequest.ruleVersionAsString() == RULE_VERSION
            updateRequest.rules() == [ RULE_DIMENSION ]

            updateResponse
        }

        event.status == OperationStatus.SUCCESS
        event.resourceModel == model
    }
}
