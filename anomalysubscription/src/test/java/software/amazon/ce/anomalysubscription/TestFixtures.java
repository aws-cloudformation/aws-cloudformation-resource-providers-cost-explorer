package software.amazon.ce.anomalysubscription;

import software.amazon.awssdk.services.costexplorer.model.AnomalySubscription;
import software.amazon.awssdk.services.costexplorer.model.Dimension;
import software.amazon.awssdk.services.costexplorer.model.DimensionValues;
import software.amazon.awssdk.services.costexplorer.model.Expression;
import software.amazon.awssdk.services.costexplorer.model.MatchOption;
import software.amazon.awssdk.services.costexplorer.model.Subscriber;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestFixtures {
    public static String SUBSCRIPTION_NAME = "TestSubscriptionName";
    public static String SUBSCRIPTION_ARN = "arn:aws:ce::123456789012:anomalysubscription/subscriptionId";
    public static double THRESHOLD = 100;
    public static String THRESHOLD_EXPRESSION = "{\"Dimensions\":{\"Key\":\"ANOMALY_TOTAL_IMPACT_PERCENTAGE\",\"MatchOptions\":[\"GREATER_THAN_OR_EQUAL\"],\"Values\":[\"100\"]}}";
    public static String FREQUENCY = "DAILY";
    public static String NEXT_TOKEN = "nextToken";
    public static Subscriber SUBSCRIBER = Subscriber.builder()
            .address("test@gmail.com")
            .status("CONFIRMED")
            .type("EMAIL")
            .build();
    public static List<Subscriber> SUBSCRIBERS = Arrays.asList(SUBSCRIBER);
    public static List<software.amazon.ce.anomalysubscription.Subscriber> CFN_MODEL_SUBSCRIBERS = ResourceModelTranslator.toSubscribers(SUBSCRIBERS);
    public static String MONITOR_ARN_1 = "arn:aws:ce::123456789012:anomalymonitor/monitorId1";
    public static String MONITOR_ARN_2 = "arn:aws:ce::123456789012:anomalymonitor/monitorId2";
    public static List<String> MONITOR_ARNS = Arrays.asList(MONITOR_ARN_1, MONITOR_ARN_2);
    public static AnomalySubscription anomalySubscription = AnomalySubscription.builder()
            .subscriptionArn(SUBSCRIPTION_ARN)
            .subscriptionName(SUBSCRIPTION_NAME)
            .monitorArnList(MONITOR_ARNS)
            .thresholdExpression(Expression.builder()
                    .dimensions(DimensionValues.builder()
                            .key(Dimension.ANOMALY_TOTAL_IMPACT_PERCENTAGE)
                            .matchOptions(Collections.singletonList(MatchOption.GREATER_THAN_OR_EQUAL))
                            .values(Collections.singletonList("100"))
                            .build())
                    .build())
            .subscribers(SUBSCRIBERS)
            .frequency(FREQUENCY)
            .build();
    public static List<AnomalySubscription> anomalySubscriptions = Arrays.asList(anomalySubscription);
    public static String RESOURCE_TAG_KEY = "TestResourceTagKey";
    public static String RESOURCE_TAG_VALUE = "TestResourceTagValue";
    public static List<ResourceTag> RESOURCE_TAGS = Arrays.asList(ResourceTag.builder()
            .key(RESOURCE_TAG_KEY)
            .value(RESOURCE_TAG_VALUE)
            .build()
    );
}
