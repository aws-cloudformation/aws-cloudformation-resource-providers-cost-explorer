package software.amazon.ce.costcategory

import software.amazon.awssdk.services.costexplorer.model.CostCategoryInheritedValueDimension
import software.amazon.awssdk.services.costexplorer.model.CostCategoryRule
import software.amazon.awssdk.services.costexplorer.model.CostCategoryValues
import software.amazon.awssdk.services.costexplorer.model.DimensionValues
import software.amazon.awssdk.services.costexplorer.model.Expression
import software.amazon.awssdk.services.costexplorer.model.MatchOption
import software.amazon.awssdk.services.costexplorer.model.TagValues

class Fixtures {
    static final COST_CATEGORY_NAME = "CCName"

    static final RULE_VERSION = "CostCategoryExpression.v1"

    static final COST_CATEGORY_ARN = "arn:aws:ce::102983441962:costcategory/a2b4cf0a-7d66-474c-9f1a-3b626f49408e"

    static final COST_CATEGORY_EFFECTIVE_START = "2019-05-01T00:00:00Z"

    static final COST_CATEGORY_DEFAULT_VALUE = "DefaultValue"

    static final JSON_RULE_DIMENSION = '''{
  "Value" : "Dimension",
  "Rule" : {
    "Dimensions" : {
      "Key" : "SERVICE_CODE",
      "Values" : [ "EC2", "KMS" ],
      "MatchOptions" : [ "EQUALS" ]
    }
  }
}'''

    static final Expression EXPRESSION_DIMENSION = Expression.builder().dimensions(
            DimensionValues.builder()
                    .key("SERVICE_CODE")
                    .values(["EC2", "KMS"])
                    .matchOptions([MatchOption.EQUALS])
                    .build()).build()

    static final CostCategoryRule RULE_DIMENSION = CostCategoryRule.builder()
            .value("Dimension")
            .rule(EXPRESSION_DIMENSION).build()

    static final String JSON_RULE_TAG = '''{
  "Value" : "Tag",
  "Rule" : {
    "Tags" : {
      "Key" : "Tag",
      "Values" : [ "V" ],
      "MatchOptions" : [ "CONTAINS", "CASE_SENSITIVE" ]
    }
  }
}'''

    static final Expression EXPRESSION_TAG = Expression.builder().tags(
            TagValues.builder()
                    .key("Tag")
                    .values(["V"])
                    .matchOptions([MatchOption.CONTAINS, MatchOption.CASE_SENSITIVE])
                    .build()).build()
    static final CostCategoryRule RULE_TAG = CostCategoryRule.builder()
            .value("Tag")
            .rule(EXPRESSION_TAG).build()

    static final String JSON_RULE_COST_CATEGORY = '''{
  "Value" : "CostCategory",
  "Rule" : {
    "CostCategories" : {
      "Key" : "Department",
      "Values" : [ "Dev", "HR" ]
    }
  }
}'''

    static final CostCategoryRule RULE_COST_CATEGORY = CostCategoryRule.builder()
            .value("CostCategory")
            .rule(Expression.builder().costCategories(
                    CostCategoryValues.builder()
                        .key("Department")
                        .values(["Dev", "HR"])
                        .build()
            ).build()).build()


    static final String JSON_RULE_AND = '''{
  "Value" : "And",
  "Rule" : {
    "And" : [ {
      "Dimensions" : {
        "Key" : "SERVICE_CODE",
        "Values" : [ "EC2", "KMS" ],
        "MatchOptions" : [ "EQUALS" ]
      }
    }, {
      "Tags" : {
        "Key" : "Tag",
        "Values" : [ "V" ],
        "MatchOptions" : [ "CONTAINS", "CASE_SENSITIVE" ]
      }
    } ]
  }
}'''

    static final Expression EXPRESSION_AND = Expression.builder().and(
            EXPRESSION_DIMENSION, EXPRESSION_TAG).build()

    static final CostCategoryRule RULE_AND = CostCategoryRule.builder()
            .value("And")
            .rule(EXPRESSION_AND).build()

    static final String JSON_RULE_OR = '''{
  "Value" : "Or",
  "Rule" : {
    "Or" : [ {
      "Dimensions" : {
        "Key" : "SERVICE_CODE",
        "Values" : [ "EC2", "KMS" ],
        "MatchOptions" : [ "EQUALS" ]
      }
    }, {
      "Tags" : {
        "Key" : "Tag",
        "Values" : [ "V" ],
        "MatchOptions" : [ "CONTAINS", "CASE_SENSITIVE" ]
      }
    } ]
  }
}'''

    static final Expression EXPRESSION_OR = Expression.builder().or(
            EXPRESSION_DIMENSION, EXPRESSION_TAG).build()

    static final CostCategoryRule RULE_OR = CostCategoryRule.builder()
            .value("Or")
            .rule(EXPRESSION_OR).build()

    static final String JSON_RULE_NOT = '''{
  "Value" : "Not",
  "Rule" : {
    "Not" : {
      "Tags" : {
        "Key" : "Tag",
        "Values" : [ "V" ],
        "MatchOptions" : [ "CONTAINS", "CASE_SENSITIVE" ]
      }
    }
  }
}'''

    static final Expression EXPRESSION_NOT = Expression.builder().not(EXPRESSION_TAG).build()

    static final CostCategoryRule RULE_NOT = CostCategoryRule.builder()
            .value("Not")
            .rule(EXPRESSION_NOT).build()

    static final String JSON_RULE_INHERITED_VALUE = '''{
  "Type" : "INHERITED_VALUE",
  "InheritedValue" : {
      "DimensionName" : "TAG",
      "DimensionKey" : "DevUsage"
  }
}'''

    static final CostCategoryInheritedValueDimension INHERITED_VALUE_DIMENSION = CostCategoryInheritedValueDimension.builder()
            .dimensionName("TAG").dimensionKey("DevUsage").build();

    static final CostCategoryRule RULE_INHERITED_VALUE = CostCategoryRule.builder()
            .type("INHERITED_VALUE").inheritedValue(INHERITED_VALUE_DIMENSION).build();

}
