package software.amazon.ce.costcategory

import software.amazon.awssdk.awscore.exception.AwsErrorDetails
import software.amazon.awssdk.awscore.exception.AwsServiceException
import software.amazon.awssdk.services.costexplorer.model.CreateCostCategoryDefinitionRequest
import software.amazon.awssdk.services.costexplorer.model.CreateCostCategoryDefinitionResponse
import software.amazon.cloudformation.proxy.HandlerErrorCode
import software.amazon.cloudformation.proxy.OperationStatus

import static software.amazon.ce.costcategory.Fixtures.*

class CreateHandlerTest extends HandlerSpecification {

    def handler = new CreateHandler(ceClient)

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
                .resourceTags(CFN_RESOURCE_TAGS)
                .build()

        when:
        def event = handler.handleRequest(proxy, request, callbackContext, logger)

        then:
        2 * request.getDesiredResourceState() >> model
        request.getDesiredResourceTags() >> RESOURCE_TAGS_MAP
        1 * proxy.injectCredentialsAndInvokeV2(*_) >> { CreateCostCategoryDefinitionRequest createRequest, _ ->
            assert createRequest.name() == model.name
            assert createRequest.ruleVersionAsString() == model.ruleVersion
            assert createRequest.rules() == [ RULE_DIMENSION, RULE_INHERITED_VALUE ]
            assert createRequest.splitChargeRules() == [SPLIT_CHARGE_RULE_FIXED, SPLIT_CHARGE_RULE_PROPORTIONAL, SPLIT_CHARGE_RULE_EVEN]
            assert createRequest.defaultValue() == COST_CATEGORY_DEFAULT_VALUE
            assert createRequest.resourceTags() as Set == SDK_RESOURCE_TAGS as Set
            createResponse
        }

        model.arn == COST_CATEGORY_ARN
        model.effectiveStart == COST_CATEGORY_EFFECTIVE_START

        event.status == OperationStatus.SUCCESS
        event.resourceModel == model

    }

    def "Test: handleRequest throws AlreadyExists when resource already exist"() {
        given:
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
        2 * request.getDesiredResourceState() >> model
        1 * proxy.injectCredentialsAndInvokeV2(*_) >> {
            throw AwsServiceException.builder()
                .awsErrorDetails(
                    AwsErrorDetails.builder().errorMessage(ERROR_MESSAGE_CC_ALREADY_EXISTS).build())
                .build()

        }
        event.getStatus() == OperationStatus.FAILED
        event.getErrorCode() == HandlerErrorCode.AlreadyExists
    }
}
