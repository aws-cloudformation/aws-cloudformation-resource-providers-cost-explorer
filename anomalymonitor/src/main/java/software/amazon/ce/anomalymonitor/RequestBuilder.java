package software.amazon.ce.anomalymonitor;

import lombok.experimental.UtilityClass;
import software.amazon.awssdk.services.costexplorer.model.AnomalyMonitor;
import software.amazon.awssdk.services.costexplorer.model.Expression;
import software.amazon.awssdk.services.costexplorer.model.CreateAnomalyMonitorRequest;
import software.amazon.awssdk.services.costexplorer.model.DeleteAnomalyMonitorRequest;
import software.amazon.awssdk.services.costexplorer.model.GetAnomalyMonitorsRequest;
import software.amazon.awssdk.services.costexplorer.model.ResourceTag;
import software.amazon.awssdk.services.costexplorer.model.UpdateAnomalyMonitorRequest;
import software.amazon.awssdk.services.costexplorer.model.ListTagsForResourceRequest;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.List;

@UtilityClass
public class RequestBuilder {
    public static CreateAnomalyMonitorRequest buildCreateAnomalyMonitorRequest(ResourceModel model, ResourceHandlerRequest <ResourceModel> request) {
        // This request builder forwards along whatever's in the ResourceModel to a CreateAnomalyMonitorRequest.
        // Note that the Create API does not allow for both MonitorDimension and MonitorSpecification at once,
        // it's up to the supplier of the ResourceModel to guarantee that
        Expression monitorSpec = model.getMonitorSpecification() != null ? Utils.toExpressionFromJson(model.getMonitorSpecification()) : null;
        AnomalyMonitor anomalyMonitor = AnomalyMonitor.builder()
                .monitorName(model.getMonitorName())
                .monitorType(model.getMonitorType())
                .monitorDimension(model.getMonitorDimension())
                .monitorSpecification(monitorSpec)
                .build();

        List<ResourceTag> tagList = ResourceModelTranslator.toSDKResourceTags(TagHelper.generateTagsForCreate(request));

        return CreateAnomalyMonitorRequest.builder()
                .anomalyMonitor(anomalyMonitor)
                .resourceTags(tagList)
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

    public static ListTagsForResourceRequest buildListTagsForResourceRequest(ResourceModel model) {
        return ListTagsForResourceRequest.builder()
                .resourceArn(model.getMonitorArn())
                .build();
    }
}
