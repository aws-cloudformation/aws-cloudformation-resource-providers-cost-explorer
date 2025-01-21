package software.amazon.ce.anomalysubscription;

import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.*;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.ProxyClient;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class ReadHandler extends AnomalySubscriptionBaseHandler {

    public ReadHandler() {
        super();
    }

    public ReadHandler(CostExplorerClient costExplorerClient) {
        super(costExplorerClient);
    }

    @Override
    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<CostExplorerClient> proxyClient,
        final Logger logger
    ) {
        final ResourceModel resourceModel = request.getDesiredResourceState();

        return ProgressEvent.progress(resourceModel, callbackContext)
            .then(progress -> proxy.initiate("AWS-CE-AnomalySubscription::Read", proxyClient, resourceModel, callbackContext)
                .translateToServiceRequest(model -> {
                    List<String> subscriptionArns = Stream.of(model.getSubscriptionArn()).collect(Collectors.toList());
                    return RequestBuilder.buildGetAnomalySubscriptionsRequest(subscriptionArns, null);
                })
                .makeServiceCall((awsRequest, client) -> {
                    return proxyClient.injectCredentialsAndInvokeV2(awsRequest, proxyClient.client()::getAnomalySubscriptions);
                })
                .handleError((awsRequest, exception, client, model, context) -> {
                    if (exception instanceof UnknownSubscriptionException) {
                        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .resourceModel(resourceModel)
                            .status(OperationStatus.FAILED)
                            .errorCode(HandlerErrorCode.NotFound)
                            .build();
                    }
                    throw exception;
                })
                .done(response -> {
                    if (response.anomalySubscriptions().isEmpty()) {
                        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                                .resourceModel(resourceModel)
                                .status(OperationStatus.FAILED)
                                .errorCode(HandlerErrorCode.NotFound)
                                .build();
                    }

                    AnomalySubscription anomalySubscription = response.anomalySubscriptions().get(0);
                    resourceModel.setSubscriptionArn(anomalySubscription.subscriptionArn());
                    resourceModel.setSubscriptionName(anomalySubscription.subscriptionName());
                    resourceModel.setAccountId(anomalySubscription.accountId());
                    resourceModel.setMonitorArnList(anomalySubscription.monitorArnList());
                    resourceModel.setSubscribers(ResourceModelTranslator.toSubscribers(anomalySubscription.subscribers()));
                    resourceModel.setThreshold(anomalySubscription.threshold());
                    resourceModel.setThresholdExpression(Utils.toJson(anomalySubscription.thresholdExpression()));
                    resourceModel.setFrequency(anomalySubscription.frequency().toString());

                    return ProgressEvent.progress(resourceModel, callbackContext);
                })
            ).then(progress -> proxy.initiate("AWS-CE-AnomalySubscription::ListTags", proxyClient, resourceModel, callbackContext)
                .translateToServiceRequest(model -> RequestBuilder.buildListTagsForResourceRequest(model))
                .makeServiceCall((awsRequest, client) -> {
                    return proxyClient.injectCredentialsAndInvokeV2(awsRequest, proxyClient.client()::listTagsForResource);
                })
                .done(response -> {
                    resourceModel.setResourceTags(ResourceModelTranslator.toCFNResourceTags(response.resourceTags()));
                    return ProgressEvent.defaultSuccessHandler(resourceModel);
                })
            );
    }
}
