package software.amazon.ce.costcategory

import software.amazon.awssdk.services.costexplorer.model.CreateCostCategoryDefinitionRequest
import software.amazon.awssdk.services.costexplorer.model.CreateCostCategoryDefinitionResponse
import software.amazon.cloudformation.proxy.OperationStatus

import static software.amazon.ce.costcategory.Fixtures.*

class CreateHandlerTest extends HandlerSpecification {

    def handler = new CreateHandler()

    def "Test: handleRequest"() {
        given:
        def createResponse = CreateCostCategoryDefinitionResponse.builder()
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
        1 * proxy.injectCredentialsAndInvokeV2(*_) >> { CreateCostCategoryDefinitionRequest createRequest, _ ->
            createRequest.name() == model.name
            createRequest.ruleVersionAsString() == model.ruleVersion
            createRequest.rules() == [ RULE_DIMENSION ]

            createResponse
        }

        model.arn == COST_CATEGORY_ARN
        model.effectiveStart == COST_CATEGORY_EFFECTIVE_START

        event.status == OperationStatus.SUCCESS
        event.resourceModel == model

    }
}
