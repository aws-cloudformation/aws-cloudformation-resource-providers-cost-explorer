package software.amazon.ce.costcategory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.google.common.collect.ImmutableMap;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.MapUtils;
import software.amazon.awssdk.services.costexplorer.model.*;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Parser between JSON array string and list of CostCategoryDataType.
 */
@UtilityClass
public class CostCategoryParser {
    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    static ObjectWriter objectWriter;

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

        // Use upper case in JSON key
        OBJECT_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE);

        // SDK model has private field without getter/setter, make Jackson to use field directly
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        // Skip null or empty values when serializing to JSON
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        // SDK model has no default constructor, and build class is private.
        // Add custom builder so Jackson can deserialize the class.
        final Map<Class<?>, Class<?>> buildersMap = ImmutableMap.<Class<?>, Class<?>>builder()
                .put(CostCategoryRule.class, CostCategoryRule.serializableBuilderClass())
                .put(Expression.class, Expression.serializableBuilderClass())
                .put(DimensionValues.class, DimensionValues.serializableBuilderClass())
                .put(TagValues.class, TagValues.serializableBuilderClass())
                .put(CostCategoryValues.class, CostCategoryValues.serializableBuilderClass())
                .put(CostCategoryInheritedValueDimension.class, CostCategoryInheritedValueDimension.serializableBuilderClass())
                .put(CostCategorySplitChargeRule.class, CostCategorySplitChargeRule.serializableBuilderClass())
                .put(CostCategorySplitChargeRuleParameter.class, CostCategorySplitChargeRuleParameter.serializableBuilderClass())
                .build();

        OBJECT_MAPPER.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
            private static final long serialVersionUID = 1L;

            @Override
            public Class<?> findPOJOBuilder(AnnotatedClass ac) {
                if (buildersMap.containsKey(ac.getRawType())) {
                    return buildersMap.get(ac.getRawType());
                }
                return super.findPOJOBuilder(ac);
            }
        });
        objectWriter = OBJECT_MAPPER.writerWithDefaultPrettyPrinter();
    }


    /**
     * Parser JSON array string to list of {@link CostCategoryRule}.
     */
    public static List<CostCategoryRule> costCategoryRulesFromJson(String json) {
        try {
            CostCategoryRule[] rules = OBJECT_MAPPER.readValue(json, CostCategoryRule[].class);

            return Arrays.asList(rules);
        } catch (JsonMappingException ex) {
            throw new CfnInvalidRequestException(String.format("Unsupported JSON array '%s' for cost category rules", json), ex);
        } catch (Exception ex) {
            throw new CfnInvalidRequestException(String.format("Invalid JSON array '%s' for cost category rules", json), ex);
        }
    }

    /**
     * Parser list of {@link CostCategoryRule} into JSON array string.
     */
    public static String costCategoryRulesToJson(List<CostCategoryRule> rules) {
        try {
            return objectWriter.writeValueAsString(rules);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parser JSON array string to list of {@link CostCategorySplitChargeRule}.
     */
    public static List<CostCategorySplitChargeRule> costCategorySplitChargeRulesFromJson(String json) {
        if (json == null) {
            return null;
        }
        try {
            CostCategorySplitChargeRule[] rules = OBJECT_MAPPER.readValue(json, CostCategorySplitChargeRule[].class);

            return Arrays.asList(rules);
        } catch (JsonMappingException ex) {
            throw new CfnInvalidRequestException(String.format("Unsupported JSON array '%s' for cost category split charge rules", json), ex);
        } catch (Exception ex) {
            throw new CfnInvalidRequestException(String.format("Invalid JSON array '%s' for cost category split charge rules", json), ex);
        }
    }

    /**
     * Parser list of {@link CostCategorySplitChargeRule} into JSON array string.
     */
    public static String costCategorySplitChargeRulesToJson(CostCategory costCategory) {
        if (!costCategory.hasSplitChargeRules()) {
            return null;
        }
        try {
            return objectWriter.writeValueAsString(costCategory.splitChargeRules());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<software.amazon.awssdk.services.costexplorer.model.ResourceTag> toSDKResourceTags(Map<String, String> resourceTags) {
        if (MapUtils.isEmpty(resourceTags)) {
            return Collections.emptyList();
        }

        return resourceTags.entrySet().stream().filter(Objects::nonNull).map(
                        resourceTag -> software.amazon.awssdk.services.costexplorer.model.ResourceTag.builder()
                                .key(resourceTag.getKey())
                                .value(resourceTag.getValue())
                                .build())
                .collect(Collectors.toList());
    }

    public static List<ResourceTag> toCFNResourceTags(List<software.amazon.awssdk.services.costexplorer.model.ResourceTag> resourceTags) {
        if (resourceTags == null) {
            return Collections.emptyList();
        }

        return resourceTags.stream().filter(Objects::nonNull).map(
                        resourceTag -> ResourceTag.builder()
                                .key(resourceTag.key())
                                .value(resourceTag.value())
                                .build())
                .collect(Collectors.toList());
    }
}
