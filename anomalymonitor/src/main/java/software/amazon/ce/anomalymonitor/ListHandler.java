package software.amazon.ce.anomalymonitor;

import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
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

public class ListHandler extends AnomalyMonitorBaseHandler {

    public ListHandler() {
        super();
    }

    public ListHandler(CostExplorerClient costExplorerClient) {
        super(costExplorerClient);
    }

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        GetAnomalyMonitorsResponse response = proxy.injectCredentialsAndInvokeV2(
                RequestBuilder.buildGetAnomalyMonitorsRequest(null, request.getNextToken()),
                costExplorerClient::getAnomalyMonitors
        );

        final List<ResourceModel> models = response.anomalyMonitors().stream()
                .map(anomalyMonitor -> ResourceModel.builder()
                        .monitorArn(anomalyMonitor.monitorArn())
                        .monitorName(anomalyMonitor.monitorName())
                        .creationDate(anomalyMonitor.creationDate())
                        .lastUpdatedDate(anomalyMonitor.lastUpdatedDate())
                        .lastEvaluatedDate(anomalyMonitor.lastEvaluatedDate())
                        .monitorType(anomalyMonitor.monitorType().toString())
                        .monitorDimension(anomalyMonitor.monitorDimension() != null ? anomalyMonitor.monitorDimension().toString() : null)
                        .monitorSpecification(anomalyMonitor.monitorSpecification() != null ? Utils.toJson(anomalyMonitor.monitorSpecification()) : null)
                        .dimensionalValueCount(anomalyMonitor.dimensionalValueCount())
                        .build()
                ).collect(Collectors.toList());

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModels(models)
            .nextToken(response.nextPageToken())
            .status(OperationStatus.SUCCESS)
            .build();
    }
}
