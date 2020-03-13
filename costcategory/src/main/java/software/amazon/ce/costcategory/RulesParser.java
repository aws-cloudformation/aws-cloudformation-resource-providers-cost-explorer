package software.amazon.ce.costcategory;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.experimental.UtilityClass;
import software.amazon.awssdk.services.costexplorer.model.*;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;

import java.util.*;

/**
 * Parser between JSON array string and list of {@link CostCategoryRule}.
 */
@UtilityClass
public class RulesParser {
    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    static ObjectWriter objectWriter;

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

        // Use upper case in JSON key
        OBJECT_MAPPER.setPropertyNamingStrategy(new PropertyNamingStrategy.UpperCamelCaseStrategy());

        // SDK model has private field without getter/setter, make Jackson to use field directly
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        // Skip null or empty values when serializing to JSON
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        // SDK model has no default constructor, and build class is private.
        // Mixin custom builder so Jackson can deserialize the class.
        OBJECT_MAPPER.addMixIn(CostCategoryRule.class, CostCategoryRuleBuilder.class);
        OBJECT_MAPPER.addMixIn(Expression.class, ExpressionBuilder.class);
        OBJECT_MAPPER.addMixIn(DimensionValues.class, DimensionValuesBuilder.class);
        OBJECT_MAPPER.addMixIn(TagValues.class, TagValuesBuilder.class);
        OBJECT_MAPPER.addMixIn(CostCategoryValues.class, CostCategoryValuesBuilder.class);

        objectWriter = OBJECT_MAPPER.writerWithDefaultPrettyPrinter();
    }

    public static List<CostCategoryRule> fromJson(String json) {
        try {
            CostCategoryRule[] rules = OBJECT_MAPPER.readValue(json, CostCategoryRule[].class);

            return Arrays.asList(rules);
        } catch (JsonMappingException ex) {
            throw new CfnInvalidRequestException(String.format("Unsupported JSON array '%s' for cost category rules", json), ex);
        } catch (Exception ex) {
            throw new CfnInvalidRequestException(String.format("Invalid JSON array '%s' for cost category rules", json), ex);
        }
    }

    public static String toJson(List<CostCategoryRule> rules) {
        try {
            return objectWriter.writeValueAsString(rules);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @JsonDeserialize(builder = CostCategoryRuleBuilder.class)
    static class CostCategoryRuleBuilder {
        private String value;
        private Expression rule;

        public CostCategoryRule build() {
            return CostCategoryRule.builder()
                    .value(value)
                    .rule(rule)
                    .build();
        }
    }

    @JsonDeserialize(builder = ExpressionBuilder.class)
    static class ExpressionBuilder {
        private Collection<Expression> and;
        private Collection<Expression> or;
        private Expression not;
        private DimensionValues dimensions;
        private TagValues tags;
        private CostCategoryValues costCategories;

        public Expression build() {
            return Expression.builder()
                    .and(and)
                    .or(or)
                    .not(not)
                    .dimensions(dimensions)
                    .tags(tags)
                    .costCategories(costCategories)
                    .build();
        }
    }

    @JsonDeserialize(builder = DimensionValuesBuilder.class)
    static class DimensionValuesBuilder {
        private String key;
        private Collection<String> values;
        private Collection<MatchOption> matchOptions;

        public DimensionValues build() {
            return DimensionValues.builder()
                    .key(key)
                    .values(values)
                    .matchOptions(matchOptions)
                    .build();
        }
    }

    @JsonDeserialize(builder = TagValuesBuilder.class)
    static class TagValuesBuilder {
        private String key;
        private Collection<String> values;
        private Collection<MatchOption> matchOptions;

        public TagValues build() {
            return TagValues.builder()
                    .key(key)
                    .values(values)
                    .matchOptions(matchOptions)
                    .build();
        }
    }

    @JsonDeserialize(builder = CostCategoryValuesBuilder.class)
    static class CostCategoryValuesBuilder {
        private String key;
        private Collection<String> values;

        public CostCategoryValues build() {
            return CostCategoryValues.builder()
                    .key(key)
                    .values(values)
                    .build();
        }
    }
}
