package software.amazon.ce.costcategory

import software.amazon.awssdk.services.costexplorer.model.CreateCostCategoryDefinitionRequest
import software.amazon.awssdk.services.costexplorer.model.CreateCostCategoryDefinitionResponse
import software.amazon.cloudformation.proxy.OperationStatus

import static software.amazon.ce.costcategory.Fixtures.*
import static software.amazon.ce.costcategory.Fixtures.JSON_SPLIT_CHARGE_RULE_EVEN
import static software.amazon.ce.costcategory.Fixtures.JSON_SPLIT_CHARGE_RULE_FIXED
import static software.amazon.ce.costcategory.Fixtures.JSON_SPLIT_CHARGE_RULE_PROPORTIONAL

class CreateHandlerTest extends HandlerSpecification {

    def handler = new CreateHandler(TestUtils.generateTestClient())

    def "Test: handleRequest"() {
        given:
        def createResponse = CreateCostCategoryDefinitionResponse.builder()
            .costCategoryArn(COST_CATEGORY_ARN)
            .effectiveStart(COST_CATEGORY_EFFECTIVE_START)
            .build()

        def model = ResourceModel.builder()
                .name(COST_CATEGORY_NAME)
                .ruleVersion(RULE_VERSION)
                .rules("[ ${JSON_RULE_DIMENSION}, ${JSON_RULE_INHERITED_VALUE} ]")
                .splitChargeRules("[${JSON_SPLIT_CHARGE_RULE_FIXED}, ${JSON_SPLIT_CHARGE_RULE_PROPORTIONAL}, ${JSON_SPLIT_CHARGE_RULE_EVEN}]")
                .defaultValue(COST_CATEGORY_DEFAULT_VALUE)
                .build()

        when:
        def event = handler.handleRequest(proxy, request, callbackContext, logger)

        then:
        1 * request.getDesiredResourceState() >> model
        1 * proxy.injectCredentialsAndInvokeV2(*_) >> { CreateCostCategoryDefinitionRequest createRequest, _ ->
            assert createRequest.name() == model.name
            assert createRequest.ruleVersionAsString() == model.ruleVersion
            assert createRequest.rules() == [ RULE_DIMENSION, RULE_INHERITED_VALUE ]
            assert createRequest.splitChargeRules() == [SPLIT_CHARGE_RULE_FIXED, SPLIT_CHARGE_RULE_PROPORTIONAL, SPLIT_CHARGE_RULE_EVEN]
            assert createRequest.defaultValue() == COST_CATEGORY_DEFAULT_VALUE
            createResponse
        }

        model.arn == COST_CATEGORY_ARN
        model.effectiveStart == COST_CATEGORY_EFFECTIVE_START

        event.status == OperationStatus.SUCCESS
        event.resourceModel == model

    }
}
