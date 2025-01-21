package software.amazon.ce.anomalysubscription;

import lombok.experimental.UtilityClass;
import software.amazon.awssdk.services.costexplorer.model.AnomalySubscription;
import software.amazon.awssdk.services.costexplorer.model.CreateAnomalySubscriptionRequest;
import software.amazon.awssdk.services.costexplorer.model.DeleteAnomalySubscriptionRequest;
import software.amazon.awssdk.services.costexplorer.model.GetAnomalySubscriptionsRequest;
import software.amazon.awssdk.services.costexplorer.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.costexplorer.model.ResourceTag;
import software.amazon.awssdk.services.costexplorer.model.UpdateAnomalySubscriptionRequest;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.List;

@UtilityClass
@SuppressWarnings("deprecation")
public class RequestBuilder {
    public static CreateAnomalySubscriptionRequest buildCreateAnomalySubscriptionRequest(ResourceModel model, ResourceHandlerRequest<ResourceModel> request) {
        // This request builder forwards along whatever's in the ResourceModel to a CreateAnomalySubscriptionRequest.
        // Note that the Create API does not allow for both Threshold and ThresholdExpression at once,
        // it's up to the supplier of the ResourceModel to guarantee that
        AnomalySubscription anomalySubscription = AnomalySubscription.builder()
                .subscriptionName(model.getSubscriptionName())
                .threshold(model.getThreshold())
                .thresholdExpression(model.getThresholdExpression() != null ? Utils.toExpressionFromJson(model.getThresholdExpression()) : null)
                .frequency(model.getFrequency())
                .monitorArnList(model.getMonitorArnList())
                .subscribers(ResourceModelTranslator.toSDKSubscribers(model.getSubscribers()))
                .build();

        List<ResourceTag> tagList = ResourceModelTranslator.toSDKResourceTags(TagHelper.generateTagsForCreate(request));

        return CreateAnomalySubscriptionRequest.builder()
                .anomalySubscription(anomalySubscription)
                .resourceTags(tagList)
                .build();
    }

    public static UpdateAnomalySubscriptionRequest buildUpdateAnomalySubscriptionRequest(ResourceModel model) {
        // This request builder forwards along whatever's in the ResourceModel to an UpdateAnomalySubscriptionRequest.
        // Note that the Update API does not allow for both Threshold and ThresholdExpression at once,
        // it's up to the supplier of the ResourceModel to guarantee that
        return UpdateAnomalySubscriptionRequest.builder()
                .subscriptionArn(model.getSubscriptionArn())
                .subscriptionName(model.getSubscriptionName())
                .threshold(model.getThreshold())
                .thresholdExpression(model.getThresholdExpression() != null ? Utils.toExpressionFromJson(model.getThresholdExpression()) : null)
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

    public static ListTagsForResourceRequest buildListTagsForResourceRequest(ResourceModel model) {
        return ListTagsForResourceRequest.builder()
                .resourceArn(model.getSubscriptionArn())
                .build();
    }
}
