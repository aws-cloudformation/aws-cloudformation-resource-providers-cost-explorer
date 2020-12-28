package software.amazon.ce.anomalymonitor;

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

public class ReadHandler extends AnomalyMonitorBaseHandler {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();

        try {
            List<String> monitorArns = Stream.of(model.getMonitorArn()).collect(Collectors.toList());
            GetAnomalyMonitorsResponse response = proxy.injectCredentialsAndInvokeV2(
                RequestBuilder.buildGetAnomalyMonitorsRequest(monitorArns, null),
                costExplorerClient::getAnomalyMonitors
            );
            if (response.anomalyMonitors().isEmpty()) {
                return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModel(model)
                    .status(OperationStatus.FAILED)
                    .errorCode(HandlerErrorCode.NotFound)
                    .build();
            }
            AnomalyMonitor anomalyMonitor = response.anomalyMonitors().get(0);
            model.setMonitorName(anomalyMonitor.monitorName());
            model.setCreationDate(anomalyMonitor.creationDate());
            model.setLastUpdatedDate(anomalyMonitor.lastUpdatedDate());
            model.setLastEvaluatedDate(anomalyMonitor.lastEvaluatedDate());
            model.setMonitorType(anomalyMonitor.monitorType().toString());
            model.setMonitorDimension(anomalyMonitor.monitorDimension() != null ? anomalyMonitor.monitorDimension().toString() : null);
            model.setMonitorSpecification(anomalyMonitor.monitorSpecification() != null ? Utils.toJson(anomalyMonitor.monitorSpecification()) : null);
            model.setDimensionalValueCount(anomalyMonitor.dimensionalValueCount());
        } catch (UnknownMonitorException e) {
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
