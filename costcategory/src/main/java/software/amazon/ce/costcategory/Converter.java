package software.amazon.ce.costcategory;

import software.amazon.awssdk.services.costexplorer.model.CostCategoryRule;
import software.amazon.awssdk.services.costexplorer.model.CostCategoryValues;
import software.amazon.awssdk.services.costexplorer.model.DimensionValues;
import software.amazon.awssdk.services.costexplorer.model.Expression;
import software.amazon.awssdk.services.costexplorer.model.MatchOption;
import software.amazon.awssdk.services.costexplorer.model.TagValues;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Convert between JSON array string and list of {@link CostCategoryRule}.
 */
public class Converter {
    public static List<CostCategoryRule> fromJson(String json) {
        return JsonParser.fromJson(json).stream()
                .map(Converter::convert)
                .collect(Collectors.toList());
    }

    public static String toJson(List<CostCategoryRule> rules) {
        return JsonParser.toJson(rules.stream().map(Converter::convert).collect(Collectors.toList()));
    }

    private static CostCategoryRule convert(JsonParser.CostCategoryRule rule) {
        return CostCategoryRule.builder()
                .value(rule.getValue())
                .rule(convert(rule.getRule()))
                .build();
    }

    private static JsonParser.CostCategoryRule convert(CostCategoryRule rule) {
        return JsonParser.CostCategoryRule.builder()
                .value(rule.value())
                .rule(convert(rule.rule()))
                .build();
    }

    private static Expression convert(JsonParser.Expression expression) {
        if (expression == null) {
            return null;
        }

        return Expression.builder()
                .and(convertToExpressions(expression.getAnd()))
                .or(convertToExpressions(expression.getOr()))
                .not(convert(expression.getNot()))
                .dimensions(convert(expression.getDimensions()))
                .tags(convert(expression.getTags()))
                .costCategories(convert(expression.getCostCategories()))
                .build();
    }

    private static JsonParser.Expression convert(Expression expression) {
        if (expression == null) {
            return null;
        }

        return JsonParser.Expression.builder()
                .and(convertFromExpressions(expression.and()))
                .or(convertFromExpressions(expression.or()))
                .not(convert(expression.not()))
                .dimensions(convert(expression.dimensions()))
                .tags(convert(expression.tags()))
                .costCategories(convert(expression.costCategories()))
                .build();
    }

    private static CostCategoryValues convert(JsonParser.CostCategoryValues costCategories) {
        if (costCategories == null) {
            return null;
        }

        return CostCategoryValues.builder()
                .key(costCategories.getKey())
                .values(costCategories.getValues())
                .build();
    }

    private static JsonParser.CostCategoryValues convert(CostCategoryValues costCategories) {
        if (costCategories == null) {
            return null;
        }

        return JsonParser.CostCategoryValues.builder()
                .key(costCategories.key())
                .values(costCategories.values())
                .build();
    }

    private static TagValues convert(JsonParser.TagValues tags) {
        if (tags == null) {
            return null;
        }

        return TagValues.builder()
                .key(tags.getKey())
                .values(tags.getValues())
                .matchOptions(convertToMatchOptions(tags.getMatchOptions()))
                .build();
    }

    private static JsonParser.TagValues convert(TagValues tags) {
        if (tags == null) {
            return null;
        }

        return JsonParser.TagValues.builder()
                .key(tags.key())
                .values(tags.values())
                .matchOptions(convertFromMatchOptions(tags.matchOptions()))
                .build();
    }

    private static DimensionValues convert(JsonParser.DimensionValues dimensions) {
        if (dimensions == null) {
            return null;
        }

        return DimensionValues.builder()
                .key(dimensions.getKey())
                .values(dimensions.getValues())
                .matchOptions(convertToMatchOptions(dimensions.getMatchOptions()))
                .build();
    }

    private static JsonParser.DimensionValues convert(DimensionValues dimensions) {
        if (dimensions == null) {
            return null;
        }

        return JsonParser.DimensionValues.builder()
                .key(dimensions.keyAsString())
                .values(dimensions.values())
                .matchOptions(convertFromMatchOptions(dimensions.matchOptions()))
                .build();
    }

    private static List<Expression> convertToExpressions(List<JsonParser.Expression> expressions) {
        if (expressions == null) {
            return null;
        }

        return expressions.stream().map(Converter::convert).collect(Collectors.toList());
    }

    private static List<JsonParser.Expression> convertFromExpressions(List<Expression> expressions) {
        if (expressions == null) {
            return null;
        }

        return expressions.stream().map(Converter::convert).collect(Collectors.toList());
    }

    private static List<MatchOption> convertToMatchOptions(List<String> matchOptions) {
        if (matchOptions == null) {
            return null;
        }

        return matchOptions.stream().map(MatchOption::fromValue).collect(Collectors.toList());
    }

    private static List<String> convertFromMatchOptions(List<MatchOption> matchOptions) {
        if (matchOptions == null) {
            return null;
        }

        return matchOptions.stream().map(MatchOption::toString).collect(Collectors.toList());
    }
}
