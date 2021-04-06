package software.amazon.ce.costcategory

import software.amazon.awssdk.services.costexplorer.model.CostCategoryRule
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException
import spock.lang.Specification
import spock.lang.Unroll

import static software.amazon.ce.costcategory.Fixtures.*

class CostCategoryRulesParserTest extends Specification {

    @Unroll
    def "Test: toJson for #rule -> #expectedJson"(String expectedJson, CostCategoryRule rule) {
        when:
        def jsonArray = CostCategoryRulesParser.toJson([rule])

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
    def "Test: fromJson for #json -> #expectedRule"(String json, CostCategoryRule expectedRule) {
        when:
        def rules = CostCategoryRulesParser.fromJson("[ ${json} ]")

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

    def "Test: fromJson for invalid json"() {
        given:
        def json = '''invalid_json'''

        when:
        CostCategoryRulesParser.fromJson(json)

        then:
        def ex = thrown(CfnInvalidRequestException)
        ex.message.contains("Invalid JSON array")
    }

    def "Test: fromJson for not supported json"() {
        given:
        def json = '''[
{
  "Name": "Test",
  "Value": "json"
}
]'''
        when:
        CostCategoryRulesParser.fromJson(json)

        then:
        def ex = thrown(CfnInvalidRequestException)
        ex.message.contains("Unsupported JSON array")
    }
}
