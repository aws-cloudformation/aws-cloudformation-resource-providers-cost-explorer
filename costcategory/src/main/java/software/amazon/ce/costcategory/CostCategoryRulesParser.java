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
import software.amazon.awssdk.services.costexplorer.model.*;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Parser between JSON array string and list of {@link CostCategoryRule}.
 */
@UtilityClass
public class CostCategoryRulesParser {
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
        // Add custom builder so Jackson can deserialize the class.
        final Map<Class<?>, Class<?>> buildersMap = ImmutableMap.of(
                CostCategoryRule.class, CostCategoryRule.serializableBuilderClass(),
                Expression.class, Expression.serializableBuilderClass(),
                DimensionValues.class, DimensionValues.serializableBuilderClass(),
                TagValues.class, TagValues.serializableBuilderClass(),
                CostCategoryValues.class, CostCategoryValues.serializableBuilderClass()
        );
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
}
