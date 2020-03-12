package software.amazon.ce.costcategory;

import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;

import java.util.Arrays;
import java.util.List;

/**
 * Parse rules between object and JSON array string.
 */
public class JsonParser {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    }

    public static List<CostCategoryRule> fromJson(String json) {
        try {
            CostCategoryRule[] rules = OBJECT_MAPPER.readValue(json, CostCategoryRule[].class);

            return Arrays.asList(rules);
        } catch (JsonMappingException ex) {
            throw new CfnInvalidRequestException(String.format("Unsupported JSON array '%s' for cost category rules", json));
        } catch (Exception ex) {
            throw new CfnInvalidRequestException(String.format("Invalid JSON array '%s' for cost category rules", json));
        }
    }

    public static String toJson(List<CostCategoryRule> rules) {
        return Jackson.toJsonPrettyString(rules);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class CostCategoryRule {
        @JsonProperty("Value")
        private String value;

        @JsonProperty("Rule")
        private Expression rule;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    static class Expression {
        @JsonProperty("And")
        private List<Expression> and;

        @JsonProperty("Or")
        private List<Expression> or;

        @JsonProperty("Not")
        private Expression not;

        @JsonProperty("Dimensions")
        private DimensionValues dimensions;

        @JsonProperty("Tags")
        private TagValues tags;

        @JsonProperty("CostCategories")
        private CostCategoryValues costCategories;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class DimensionValues {
        @JsonProperty("Key")
        private String key;

        @JsonProperty("Values")
        private List<String> values;

        @JsonProperty("MatchOptions")
        private List<String> matchOptions;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class TagValues {
        @JsonProperty("Key")
        private String key;

        @JsonProperty("Values")
        private List<String> values;

        @JsonProperty("MatchOptions")
        private List<String> matchOptions;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class CostCategoryValues {
        @JsonProperty("Key")
        private String key;

        @JsonProperty("Values")
        private List<String> values;
    }
}
