package software.amazon.ce.anomalymonitor;

import lombok.experimental.UtilityClass;
import software.amazon.awssdk.services.costexplorer.model.*;

import java.util.List;

@UtilityClass
public class RequestBuilder {
    public static CreateAnomalyMonitorRequest buildCreateAnomalyMonitorRequest(ResourceModel model) {
        Expression monitorSpec = model.getMonitorSpecification() != null ? Utils.toExpresionFromJson(model.getMonitorSpecification()) : null;
        AnomalyMonitor anomalyMonitor = AnomalyMonitor.builder()
                .monitorName(model.getMonitorName())
                .monitorType(model.getMonitorType())
                .monitorDimension(model.getMonitorDimension())
                .monitorSpecification(monitorSpec)
                .build();

        return CreateAnomalyMonitorRequest.builder()
                .anomalyMonitor(anomalyMonitor)
                .resourceTagList(model.getResourceTags())
                .build();
    }

    public static UpdateAnomalyMonitorRequest buildUpdateAnomalyMonitorRequest(ResourceModel model) {
        return UpdateAnomalyMonitorRequest.builder()
                .monitorArn(model.getMonitorArn())
                .monitorName(model.getMonitorName())
                .build();
    }

    public static GetAnomalyMonitorsRequest buildGetAnomalyMonitorsRequest(List<String> monitorArns, String nextPageToken) {
        return GetAnomalyMonitorsRequest.builder()
                .monitorArnList(monitorArns)
                .nextPageToken(nextPageToken)
                .build();
    }

    public static DeleteAnomalyMonitorRequest buildDeleteAnomalyMonitorRequest(ResourceModel model) {
        return DeleteAnomalyMonitorRequest.builder()
                .monitorArn(model.getMonitorArn())
                .build();
    }
}
