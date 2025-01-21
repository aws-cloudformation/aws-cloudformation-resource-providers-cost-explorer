package software.amazon.ce.anomalymonitor;

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

import static software.amazon.ce.anomalymonitor.Utils.LAST_EVALUATED_DATE_PLACEHOLDER;

public class ReadHandler extends AnomalyMonitorBaseHandler {

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
            .then(progress -> proxy.initiate("AWS-CE-AnomalyMonitor::Read", proxyClient, resourceModel, callbackContext)
                .translateToServiceRequest(model -> {
                    List<String> monitorArns = Stream.of(model.getMonitorArn()).collect(Collectors.toList());
                    return RequestBuilder.buildGetAnomalyMonitorsRequest(monitorArns, null);
                })
                .makeServiceCall((awsRequest, client) -> {
                    return proxyClient.injectCredentialsAndInvokeV2(awsRequest, proxyClient.client()::getAnomalyMonitors);
                })
                .handleError((awsRequest, exception, client, model, context) -> {
                    if (exception instanceof UnknownMonitorException) {
                        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .resourceModel(resourceModel)
                            .status(OperationStatus.FAILED)
                            .errorCode(HandlerErrorCode.NotFound)
                            .build();
                    }
                    throw exception;
                })
                .done(response -> {
                    if (response.anomalyMonitors().isEmpty()) {
                        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .resourceModel(resourceModel)
                            .status(OperationStatus.FAILED)
                            .errorCode(HandlerErrorCode.NotFound)
                            .build();
                    }

                    AnomalyMonitor anomalyMonitor = response.anomalyMonitors().get(0);
                    resourceModel.setMonitorArn(anomalyMonitor.monitorArn());
                    resourceModel.setMonitorName(anomalyMonitor.monitorName());
                    resourceModel.setCreationDate(anomalyMonitor.creationDate());
                    resourceModel.setLastUpdatedDate(anomalyMonitor.lastUpdatedDate());
                    resourceModel.setLastEvaluatedDate(anomalyMonitor.lastEvaluatedDate() != null ? anomalyMonitor.lastEvaluatedDate() : LAST_EVALUATED_DATE_PLACEHOLDER);
                    resourceModel.setMonitorType(anomalyMonitor.monitorType().toString());
                    resourceModel.setMonitorDimension(anomalyMonitor.monitorDimension() != null ? anomalyMonitor.monitorDimension().toString() : null);
                    resourceModel.setMonitorSpecification(anomalyMonitor.monitorSpecification() != null ? Utils.toJson(anomalyMonitor.monitorSpecification()) : null);
                    resourceModel.setDimensionalValueCount(anomalyMonitor.dimensionalValueCount());
                    return ProgressEvent.progress(resourceModel, callbackContext);
                })
            ).then(progress -> proxy.initiate("AWS-CE-AnomalyMonitor::ListTags", proxyClient, resourceModel, callbackContext)
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
