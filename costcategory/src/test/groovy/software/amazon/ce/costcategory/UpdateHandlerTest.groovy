package software.amazon.ce.costcategory

import software.amazon.awssdk.services.costexplorer.model.UpdateCostCategoryDefinitionRequest
import software.amazon.awssdk.services.costexplorer.model.UpdateCostCategoryDefinitionResponse
import software.amazon.cloudformation.proxy.OperationStatus

import static software.amazon.ce.costcategory.Fixtures.*

class UpdateHandlerTest extends HandlerSpecification {

    def handler = new UpdateHandler(ceClient)

    def "Test: handleRequest"() {
        given:
        def updateResponse = UpdateCostCategoryDefinitionResponse.builder()
            .costCategoryArn(COST_CATEGORY_ARN)
            .effectiveStart(COST_CATEGORY_EFFECTIVE_START)
            .build()

        def model = ResourceModel.builder()
            .arn(COST_CATEGORY_ARN)
            .name(COST_CATEGORY_NAME)
            .ruleVersion(RULE_VERSION)
            .rules("[ ${JSON_RULE_DIMENSION} ]")
            .splitChargeRules("[${JSON_SPLIT_CHARGE_RULE_FIXED}, ${JSON_SPLIT_CHARGE_RULE_PROPORTIONAL}, ${JSON_SPLIT_CHARGE_RULE_EVEN}]")
            .defaultValue(COST_CATEGORY_DEFAULT_VALUE)
            .build()

        when:
        def event = handler.handleRequest(proxy, request, callbackContext, logger)

        then:
        1 * request.getDesiredResourceState() >> model
        1 * proxy.injectCredentialsAndInvokeV2(*_) >> { UpdateCostCategoryDefinitionRequest updateRequest, _ ->
            assert updateRequest.costCategoryArn() == COST_CATEGORY_ARN
            assert updateRequest.ruleVersionAsString() == RULE_VERSION
            assert updateRequest.rules() == [ RULE_DIMENSION ]
            assert updateRequest.splitChargeRules() == [SPLIT_CHARGE_RULE_FIXED, SPLIT_CHARGE_RULE_PROPORTIONAL, SPLIT_CHARGE_RULE_EVEN]
            assert updateRequest.defaultValue() == COST_CATEGORY_DEFAULT_VALUE

            updateResponse
        }

        event.status == OperationStatus.SUCCESS
        event.resourceModel == model
    }
}
