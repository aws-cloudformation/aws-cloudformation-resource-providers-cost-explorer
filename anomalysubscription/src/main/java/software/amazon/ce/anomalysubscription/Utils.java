package software.amazon.ce.anomalysubscription;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.google.common.collect.ImmutableMap;

import lombok.experimental.UtilityClass;
import software.amazon.awssdk.services.costexplorer.model.CostCategoryValues;
import software.amazon.awssdk.services.costexplorer.model.DimensionValues;
import software.amazon.awssdk.services.costexplorer.model.Expression;
import software.amazon.awssdk.services.costexplorer.model.TagValues;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;

import java.util.Map;

@UtilityClass
public class Utils {
    public static final String SUBSCRIPTION_ALREADY_EXISTS = "Cannot create a subscription with the same subscription name as an existing subscription";
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
        final Map<Class<?>, Class<?>> buildersMap = ImmutableMap.of(
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

    public static Expression toExpressionFromJson(String expressionJson) {
        try {
            return OBJECT_MAPPER.readValue(expressionJson, Expression.class);
        } catch (JsonMappingException e) {
            throw new CfnInvalidRequestException(String.format("Unsupported JSON '%s' for Expression", expressionJson), e);
        } catch (Exception e) {
            throw new CfnInvalidRequestException(String.format("Invalid JSON '%s' for Expression", expressionJson), e);
        }
    }

    public static String toJson(Expression expression) {
        try {
            return objectWriter.writeValueAsString(expression);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
