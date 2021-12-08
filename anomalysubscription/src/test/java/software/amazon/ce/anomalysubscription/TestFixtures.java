package software.amazon.ce.anomalysubscription;

import software.amazon.awssdk.services.costexplorer.model.*;
import software.amazon.awssdk.services.costexplorer.model.Subscriber;

import java.util.Arrays;
import java.util.List;

public class TestFixtures {
    public static String SUBSCRIPTION_NAME = "TestSubscriptionName";
    public static String SUBSCRIPTION_ARN = "arn:aws:ce::123456789012:anomalysubscription/subscriptionId";
    public static double THRESHOLD = 100;
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
            .threshold(THRESHOLD)
            .subscribers(SUBSCRIBERS)
            .frequency(FREQUENCY)
            .build();
    public static List<AnomalySubscription> anomalySubscriptions = Arrays.asList(anomalySubscription);
    public static List<ResourceTag> RESOURCE_TAGS = Arrays.asList(ResourceTag.builder()
            .resourceTagKey("TestResourceTagKey")
            .resourceTagValue("TestResourceTagValue")
            .build()
    );
}
