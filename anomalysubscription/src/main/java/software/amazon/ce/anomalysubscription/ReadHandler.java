package software.amazon.ce.anomalysubscription;

// TODO: replace all usage of SdkClient with your service client type, e.g; YourServiceAsyncClient
// import software.amazon.awssdk.services.yourservice.YourServiceAsyncClient;

import software.amazon.awssdk.services.costexplorer.model.*;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.cloudformation.proxy.HandlerErrorCode;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReadHandler extends AnomalySubscriptionBaseHandler {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();

        try {
            List<String> subscriptionArns = Stream.of(model.getSubscriptionArn()).collect(Collectors.toList());
            GetAnomalySubscriptionsResponse response = proxy.injectCredentialsAndInvokeV2(
                    RequestBuilder.buildGetAnomalySubscriptionsRequest(subscriptionArns, null),
                    costExplorerClient::getAnomalySubscriptions
            );
            if (response.anomalySubscriptions().isEmpty()) {
                return ProgressEvent.<ResourceModel, CallbackContext>builder()
                        .resourceModel(model)
                        .status(OperationStatus.FAILED)
                        .errorCode(HandlerErrorCode.NotFound)
                        .build();
            }
            AnomalySubscription anomalySubscription = response.anomalySubscriptions().get(0);
            model.setSubscriptionName(anomalySubscription.subscriptionName());
            model.setAccountId(anomalySubscription.accountId());
            model.setMonitorArnList(anomalySubscription.monitorArnList());
            model.setSubscribers(ResourceModelTranslator.toSubscribers(anomalySubscription.subscribers()));
            model.setThreshold(anomalySubscription.threshold());
            model.setFrequency(anomalySubscription.frequency().toString());
        } catch (UnknownSubscriptionException e) {
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModel(model)
                    .status(OperationStatus.FAILED)
                    .errorCode(HandlerErrorCode.NotFound)
                    .build();
        }

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(model)
                .status(OperationStatus.SUCCESS)
                .build();
    }
}
