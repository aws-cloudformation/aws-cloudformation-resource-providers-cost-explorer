package software.amazon.ce.costcategory;

import lombok.experimental.UtilityClass;
import software.amazon.awssdk.services.costexplorer.model.*;

/**
 * Build API request based on resource model.
 */
@UtilityClass
public class CostCategoryRequestBuilder {
    public static CreateCostCategoryDefinitionRequest buildCreateRequest(ResourceModel model) {
        return CreateCostCategoryDefinitionRequest.builder()
                .name(model.getName())
                .ruleVersion(model.getRuleVersion())
                .rules(CostCategoryRulesParser.fromJson(model.getRules()))
                .build();
    }

    public static UpdateCostCategoryDefinitionRequest buildUpdateRequest(ResourceModel model) {
        return UpdateCostCategoryDefinitionRequest.builder()
                .costCategoryArn(model.getArn())
                .ruleVersion(model.getRuleVersion())
                .rules(CostCategoryRulesParser.fromJson(model.getRules()))
                .build();
    }

    public static DescribeCostCategoryDefinitionRequest buildDescribeRequest(ResourceModel model) {
        return DescribeCostCategoryDefinitionRequest.builder()
                .costCategoryArn(model.getArn())
                .build();
    }

    public static DeleteCostCategoryDefinitionRequest buildDeleteRequest(ResourceModel model) {
        return DeleteCostCategoryDefinitionRequest.builder()
                .costCategoryArn(model.getArn())
                .build();
    }

    public static ListCostCategoryDefinitionsRequest buildListRequest(String nextToken) {
        return ListCostCategoryDefinitionsRequest.builder()
                .nextToken(nextToken)
                .build();
    }
}
