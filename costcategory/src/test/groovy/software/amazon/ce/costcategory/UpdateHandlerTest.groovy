package software.amazon.ce.costcategory

import software.amazon.awssdk.awscore.exception.AwsErrorDetails
import software.amazon.awssdk.awscore.exception.AwsServiceException
import software.amazon.awssdk.services.cloudwatch.model.TagResourceResponse
import software.amazon.awssdk.services.costexplorer.model.ResourceNotFoundException
import software.amazon.awssdk.services.costexplorer.model.TagResourceRequest
import software.amazon.awssdk.services.costexplorer.model.UntagResourceRequest
import software.amazon.awssdk.services.costexplorer.model.UntagResourceResponse
import software.amazon.awssdk.services.costexplorer.model.UpdateCostCategoryDefinitionRequest
import software.amazon.awssdk.services.costexplorer.model.UpdateCostCategoryDefinitionResponse
import software.amazon.cloudformation.proxy.HandlerErrorCode
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
        1 * request.getPreviousResourceState() >> Mock(ResourceModel) { it ->
            it.getTags() >> []
        }
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

    def "Test: handleRequest with tagging"() {
        given:
        def updateResponse = UpdateCostCategoryDefinitionResponse.builder()
                .costCategoryArn(COST_CATEGORY_ARN)
                .effectiveStart(COST_CATEGORY_EFFECTIVE_START)
                .build()

        def untagResponse = UntagResourceResponse.builder().build()
        def tagResponse = TagResourceResponse.builder().build()

        def model = ResourceModel.builder()
                .arn(COST_CATEGORY_ARN)
                .name(COST_CATEGORY_NAME)
                .ruleVersion(RULE_VERSION)
                .rules("[ ${JSON_RULE_DIMENSION} ]")
                .splitChargeRules("[${JSON_SPLIT_CHARGE_RULE_FIXED}, ${JSON_SPLIT_CHARGE_RULE_PROPORTIONAL}, ${JSON_SPLIT_CHARGE_RULE_EVEN}]")
                .defaultValue(COST_CATEGORY_DEFAULT_VALUE)
                .tags(NEW_CFN_RESOURCE_TAGS)
                .build()

        when:
        def event = handler.handleRequest(proxy, request, callbackContext, logger)

        then:
        1 * request.getDesiredResourceState() >> model
        1 * request.getPreviousResourceState() >> Mock(ResourceModel) { it ->
            it.getTags() >> CFN_RESOURCE_TAGS
        }
        1 * proxy.injectCredentialsAndInvokeV2(*_) >> { UpdateCostCategoryDefinitionRequest updateRequest, _ ->
            assert updateRequest.costCategoryArn() == COST_CATEGORY_ARN
            assert updateRequest.ruleVersionAsString() == RULE_VERSION
            assert updateRequest.rules() == [ RULE_DIMENSION ]
            assert updateRequest.splitChargeRules() == [SPLIT_CHARGE_RULE_FIXED, SPLIT_CHARGE_RULE_PROPORTIONAL, SPLIT_CHARGE_RULE_EVEN]
            assert updateRequest.defaultValue() == COST_CATEGORY_DEFAULT_VALUE

            updateResponse
        }
        1 * proxy.injectCredentialsAndInvokeV2(*_) >> { UntagResourceRequest untagRequest, _ ->
            assert untagRequest.resourceArn() == COST_CATEGORY_ARN
            assert untagRequest.resourceTagKeys() == ["Key2"]
            untagResponse
        }
        1 * proxy.injectCredentialsAndInvokeV2(*_) >> { TagResourceRequest tagRequest, _ ->
            assert tagRequest.resourceArn() == COST_CATEGORY_ARN
            assert tagRequest.resourceTags() == [
                software.amazon.awssdk.services.costexplorer.model.ResourceTag.builder().key("Key3").value("Value3").build()
            ]
            tagResponse
        }

        event.status == OperationStatus.SUCCESS
        event.resourceModel == model
    }

    def "Test: handleRequest ResourceNotFound"() {
        given:
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

            throw ResourceNotFoundException.builder().message("error").build()
        }

        event.status == OperationStatus.FAILED
        event.errorCode == HandlerErrorCode.NotFound
    }


    def "Test: handleRequest.AccessDenied for tagging"() {
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
                .tags(NEW_CFN_RESOURCE_TAGS)
                .build()

        when:
        def event = handler.handleRequest(proxy, request, new CallbackContext(), logger)

        then:
        1 * request.getDesiredResourceState() >> model
        1 * request.getPreviousResourceState() >> Mock(ResourceModel) { it ->
            it.getTags() >> CFN_RESOURCE_TAGS
        }
        1 * proxy.injectCredentialsAndInvokeV2(*_) >> { UpdateCostCategoryDefinitionRequest updateRequest, _ ->
            assert updateRequest.costCategoryArn() == COST_CATEGORY_ARN
            assert updateRequest.ruleVersionAsString() == RULE_VERSION
            assert updateRequest.rules() == [ RULE_DIMENSION ]
            assert updateRequest.splitChargeRules() == [SPLIT_CHARGE_RULE_FIXED, SPLIT_CHARGE_RULE_PROPORTIONAL, SPLIT_CHARGE_RULE_EVEN]
            assert updateRequest.defaultValue() == COST_CATEGORY_DEFAULT_VALUE

            updateResponse
        }
        1 * proxy.injectCredentialsAndInvokeV2(*_) >> { UntagResourceRequest untagRequest, _ ->
            assert untagRequest.resourceArn() == COST_CATEGORY_ARN
            assert untagRequest.resourceTagKeys() == ["Key2"]

            throw AwsServiceException.builder()
                    .awsErrorDetails(
                            AwsErrorDetails.builder().errorMessage("error").errorCode("AccessDeniedException").build())
                    .build()
        }

        event.status == OperationStatus.FAILED
        event.errorCode == HandlerErrorCode.AccessDenied
    }
}
