package software.amazon.ce.anomalysubscription;

import lombok.experimental.UtilityClass;
import software.amazon.awssdk.services.costexplorer.model.AnomalySubscription;
import software.amazon.awssdk.services.costexplorer.model.CreateAnomalySubscriptionRequest;
import software.amazon.awssdk.services.costexplorer.model.DeleteAnomalySubscriptionRequest;
import software.amazon.awssdk.services.costexplorer.model.GetAnomalySubscriptionsRequest;
import software.amazon.awssdk.services.costexplorer.model.ResourceTag;
import software.amazon.awssdk.services.costexplorer.model.UpdateAnomalySubscriptionRequest;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.List;

@UtilityClass
public class RequestBuilder {
    public static CreateAnomalySubscriptionRequest buildCreateAnomalySubscriptionRequest(ResourceModel model, ResourceHandlerRequest<ResourceModel> request) {
        AnomalySubscription anomalySubscription = AnomalySubscription.builder()
                .subscriptionName(model.getSubscriptionName())
                .threshold(model.getThreshold())
                .frequency(model.getFrequency())
                .monitorArnList(model.getMonitorArnList())
                .subscribers(ResourceModelTranslator.toSDKSubscribers(model.getSubscribers()))
                .build();

        List<ResourceTag> tagList = ResourceModelTranslator.toSDKResourceTags(TagHelper.generateTagsForCreate(model, request));

        return CreateAnomalySubscriptionRequest.builder()
                .anomalySubscription(anomalySubscription)
                .resourceTags(tagList)
                .build();
    }

    public static UpdateAnomalySubscriptionRequest buildUpdateAnomalySubscriptionRequest(ResourceModel model) {
        return UpdateAnomalySubscriptionRequest.builder()
                .subscriptionArn(model.getSubscriptionArn())
                .subscriptionName(model.getSubscriptionName())
                .threshold(model.getThreshold())
                .frequency(model.getFrequency())
                .subscribers(ResourceModelTranslator.toSDKSubscribers(model.getSubscribers()))
                .monitorArnList(model.getMonitorArnList())
                .build();
    }

    public static GetAnomalySubscriptionsRequest buildGetAnomalySubscriptionsRequest(List<String> subscriptionArns, String nextPageToken) {
        return GetAnomalySubscriptionsRequest.builder()
                .subscriptionArnList(subscriptionArns)
                .nextPageToken(nextPageToken)
                .build();
    }

    public static DeleteAnomalySubscriptionRequest buildDeleteAnomalySubscriptionRequest(ResourceModel model) {
        return DeleteAnomalySubscriptionRequest.builder()
                .subscriptionArn(model.getSubscriptionArn())
                .build();
    }
}
