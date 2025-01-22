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
    public static final String SUBSCRIPTION_NAME = "TestSubscriptionName";
    public static final String SUBSCRIPTION_ARN = "arn:aws:ce::123456789012:anomalysubscription/subscriptionId";
    public static final double THRESHOLD = 100;
    public static final String THRESHOLD_EXPRESSION = "{\"Dimensions\":{\"Key\":\"ANOMALY_TOTAL_IMPACT_PERCENTAGE\",\"MatchOptions\":[\"GREATER_THAN_OR_EQUAL\"],\"Values\":[\"100\"]}}";
    public static final String FREQUENCY = "DAILY";
    public static final String NEXT_TOKEN = "nextToken";
    public static final Subscriber SUBSCRIBER = Subscriber.builder()
            .address("test@gmail.com")
            .status("CONFIRMED")
            .type("EMAIL")
            .build();
    static final List<Subscriber> SUBSCRIBERS = Arrays.asList(SUBSCRIBER);
    public static final List<software.amazon.ce.anomalysubscription.Subscriber> CFN_MODEL_SUBSCRIBERS = ResourceModelTranslator.toSubscribers(SUBSCRIBERS);
    public static final String MONITOR_ARN_1 = "arn:aws:ce::123456789012:anomalymonitor/monitorId1";
    public static final String MONITOR_ARN_2 = "arn:aws:ce::123456789012:anomalymonitor/monitorId2";
    static final List<String> MONITOR_ARNS = Arrays.asList(MONITOR_ARN_1, MONITOR_ARN_2);
    public static final AnomalySubscription anomalySubscription = AnomalySubscription.builder()
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
    static final List<AnomalySubscription> anomalySubscriptions = Arrays.asList(anomalySubscription);
    public static final String RESOURCE_TAG_KEY = "TestResourceTagKey";
    public static final String RESOURCE_TAG_VALUE = "TestResourceTagValue";
    static final List<ResourceTag> RESOURCE_TAGS = Arrays.asList(ResourceTag.builder()
            .key(RESOURCE_TAG_KEY)
            .value(RESOURCE_TAG_VALUE)
            .build()
    );
}
