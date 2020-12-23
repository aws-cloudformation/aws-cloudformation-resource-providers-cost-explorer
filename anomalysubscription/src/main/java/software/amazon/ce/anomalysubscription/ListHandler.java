package software.amazon.ce.anomalysubscription;

import software.amazon.awssdk.services.costexplorer.model.*;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListHandler extends AnomalySubscriptionBaseHandler {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {

        GetAnomalySubscriptionsResponse response = proxy.injectCredentialsAndInvokeV2(
                RequestBuilder.buildGetAnomalySubscriptionsRequest(null, request.getNextToken()),
                costExplorerClient::getAnomalySubscriptions
        );

        final List<ResourceModel> models = response.anomalySubscriptions().stream()
                .map(anomalySubscription -> ResourceModel.builder()
                        .subscriptionArn(anomalySubscription.subscriptionArn())
                        .subscriptionName(anomalySubscription.subscriptionName())
                        .accountId(anomalySubscription.accountId())
                        .monitorArnList(anomalySubscription.monitorArnList())
                        .threshold(anomalySubscription.threshold())
                        .frequency(anomalySubscription.frequency().toString())
                        .subscribers(ResourceModelTranslator.toSubscribers(anomalySubscription.subscribers()))
                        .build()
                ).collect(Collectors.toList());

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModels(models)
                .nextToken(response.nextPageToken())
                .status(OperationStatus.SUCCESS)
                .build();
    }
}

