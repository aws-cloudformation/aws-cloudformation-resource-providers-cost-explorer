package software.amazon.ce.costcategory

import software.amazon.awssdk.services.costexplorer.model.CostCategoryReference
import software.amazon.awssdk.services.costexplorer.model.ListCostCategoryDefinitionsRequest
import software.amazon.awssdk.services.costexplorer.model.ListCostCategoryDefinitionsResponse
import software.amazon.cloudformation.proxy.OperationStatus

import static software.amazon.ce.costcategory.Fixtures.*

class ListHandlerTest extends HandlerSpecification {

    def handler = new ListHandler()

    def "Test: handleRequest"() {
        given:
        def listResponse = ListCostCategoryDefinitionsResponse.builder()
            .costCategoryReferences(CostCategoryReference.builder()
                    .costCategoryArn(COST_CATEGORY_ARN)
                    .name(COST_CATEGORY_NAME)
                    .effectiveStart(COST_CATEGORY_EFFECTIVE_START)
                    .numberOfRules(1)
                    .build()
            ).build()

        def nextToken = "nextToken"

        def expectedModel = ResourceModel.builder()
                .arn(COST_CATEGORY_ARN)
                .name(COST_CATEGORY_NAME)
                .effectiveStart(COST_CATEGORY_EFFECTIVE_START)
                .build()

        when:
        def event = handler.handleRequest(proxy, request, callbackContext, logger)

        then:
        1 * request.getNextToken() >> nextToken

        1 * proxy.injectCredentialsAndInvokeV2(*_) >> { ListCostCategoryDefinitionsRequest listRequest, _ ->
            assert listRequest.nextToken() == nextToken
            listResponse
        }

        event.status == OperationStatus.SUCCESS
        event.resourceModels == [ expectedModel ]
    }
}
