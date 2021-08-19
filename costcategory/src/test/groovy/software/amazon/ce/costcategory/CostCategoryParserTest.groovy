package software.amazon.ce.costcategory

import software.amazon.awssdk.services.costexplorer.model.CostCategoryRule
import software.amazon.awssdk.services.costexplorer.model.CostCategorySplitChargeRule
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException
import spock.lang.Specification
import spock.lang.Unroll

import static software.amazon.ce.costcategory.Fixtures.*
import static software.amazon.ce.costcategory.Fixtures.JSON_SPLIT_CHARGE_RULE_EVEN
import static software.amazon.ce.costcategory.Fixtures.JSON_SPLIT_CHARGE_RULE_FIXED
import static software.amazon.ce.costcategory.Fixtures.JSON_SPLIT_CHARGE_RULE_PROPORTIONAL
import static software.amazon.ce.costcategory.Fixtures.SPLIT_CHARGE_RULE_EVEN
import static software.amazon.ce.costcategory.Fixtures.SPLIT_CHARGE_RULE_FIXED
import static software.amazon.ce.costcategory.Fixtures.SPLIT_CHARGE_RULE_PROPORTIONAL

class CostCategoryParserTest extends Specification {

    @Unroll
    def "Test: costCategoryRulesToJson for #rule -> #expectedJson"(String expectedJson, CostCategoryRule rule) {
        when:
        def jsonArray = CostCategoryParser.costCategoryRulesToJson([rule])

        then:
        jsonArray == "[ ${expectedJson} ]"

        where:
        expectedJson              | rule
        JSON_RULE_DIMENSION       | RULE_DIMENSION
        JSON_RULE_TAG             | RULE_TAG
        JSON_RULE_COST_CATEGORY   | RULE_COST_CATEGORY
        JSON_RULE_AND             | RULE_AND
        JSON_RULE_OR              | RULE_OR
        JSON_RULE_NOT             | RULE_NOT
        JSON_RULE_INHERITED_VALUE | RULE_INHERITED_VALUE
    }

    @Unroll
    def "Test: costCategoryRulesFromJson for #json -> #expectedRule"(String json, CostCategoryRule expectedRule) {
        when:
        def rules = CostCategoryParser.costCategoryRulesFromJson("[ ${json} ]")

        then:
        rules == [expectedRule]

        where:
        json                      | expectedRule
        JSON_RULE_DIMENSION       | RULE_DIMENSION
        JSON_RULE_TAG             | RULE_TAG
        JSON_RULE_COST_CATEGORY   | RULE_COST_CATEGORY
        JSON_RULE_AND             | RULE_AND
        JSON_RULE_OR              | RULE_OR
        JSON_RULE_NOT             | RULE_NOT
        JSON_RULE_INHERITED_VALUE | RULE_INHERITED_VALUE
    }

    def "Test: costCategoryRulesFromJson for invalid json"() {
        given:
        def json = '''invalid_json'''

        when:
        CostCategoryParser.costCategoryRulesFromJson(json)

        then:
        def ex = thrown(CfnInvalidRequestException)
        ex.message.contains("Invalid JSON array")
    }

    def "Test: costCategoryRulesFromJson for not supported json"() {
        given:
        def json = '''[
{
  "Name": "Test",
  "Value": "json"
}
]'''
        when:
        CostCategoryParser.costCategoryRulesFromJson(json)

        then:
        def ex = thrown(CfnInvalidRequestException)
        ex.message.contains("Unsupported JSON array")
    }

    @Unroll
    def "Test: costCategorySplitChargeRulesToJson for #rule -> #expectedJson"(String expectedJson, CostCategorySplitChargeRule rule) {
        when:
        def jsonArray = CostCategoryParser.costCategorySplitChargeRulesToJson([rule])

        then:
        jsonArray == "[ ${expectedJson} ]"

        where:
        expectedJson                        | rule
        null                                | null
        JSON_SPLIT_CHARGE_RULE_PROPORTIONAL | SPLIT_CHARGE_RULE_PROPORTIONAL
        JSON_SPLIT_CHARGE_RULE_EVEN         | SPLIT_CHARGE_RULE_EVEN
        JSON_SPLIT_CHARGE_RULE_FIXED        | SPLIT_CHARGE_RULE_FIXED
    }

    @Unroll
    def "Test: costCategorySplitChargeRulesFromJson for #json -> #expectedRule"(String json, CostCategorySplitChargeRule expectedRule) {
        when:
        def rules = CostCategoryParser.costCategorySplitChargeRulesFromJson("[ ${json} ]")

        then:
        rules == [expectedRule]

        where:
        json                                | expectedRule
        null                                | null
        JSON_SPLIT_CHARGE_RULE_PROPORTIONAL | SPLIT_CHARGE_RULE_PROPORTIONAL
        JSON_SPLIT_CHARGE_RULE_EVEN         | SPLIT_CHARGE_RULE_EVEN
        JSON_SPLIT_CHARGE_RULE_FIXED        | SPLIT_CHARGE_RULE_FIXED
    }

    def "Test: costCategorySplitChargeRulesFromJson for invalid json"() {
        given:
        def json = '''invalid_json'''

        when:
        CostCategoryParser.costCategorySplitChargeRulesFromJson(json)

        then:
        def ex = thrown(CfnInvalidRequestException)
        ex.message.contains("Invalid JSON array")
    }

    def "Test: costCategorySplitChargeRulesFromJson for not supported json"() {
        given:
        def json = '''[
{
  "Name": "Test",
  "Value": "json"
}
]'''
        when:
        CostCategoryParser.costCategorySplitChargeRulesFromJson(json)

        then:
        def ex = thrown(CfnInvalidRequestException)
        ex.message.contains("Unsupported JSON array")
    }
}
