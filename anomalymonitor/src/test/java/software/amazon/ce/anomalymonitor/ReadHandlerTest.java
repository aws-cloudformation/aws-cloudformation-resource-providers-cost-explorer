package software.amazon.ce.anomalymonitor;

import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.*;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Credentials;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.LoggerProxy;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ReadHandlerTest {
    @Mock
    private Credentials credentials;

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private Logger logger;

    @Mock
    CostExplorerClient ceClient;

    @Mock
    ProxyClient<CostExplorerClient> proxyClient;

    private ReadHandler handler;

    @BeforeEach
    public void setup() {
        credentials = new Credentials("accessKey", "secretKey", "sessionToken");
        logger = mock(Logger.class);
        proxy = new AmazonWebServicesClientProxy(new LoggerProxy(), credentials, () -> Duration.ofSeconds(600).toMillis());
        ceClient = mock(CostExplorerClient.class);
        proxyClient = TestUtils.MOCK_PROXY(proxy, ceClient);

        handler = new ReadHandler(ceClient);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final ResourceModel expectedModel = ResourceModel.builder()
            .monitorArn(TestFixtures.MONITOR_ARN)
            .monitorName(TestFixtures.MONITOR_NAME)
            .creationDate(TestFixtures.DATE)
            .lastUpdatedDate(TestFixtures.DATE)
            .lastEvaluatedDate(TestFixtures.DATE)
            .monitorType(TestFixtures.MONITOR_TYPE_DIMENSIONAL)
            .monitorDimension(TestFixtures.DIMENSION)
            .dimensionalValueCount(TestFixtures.DIMENSIONAL_VALUE_COUNT)
            .resourceTags(TestFixtures.RESOURCE_TAGS)
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(ResourceModel.builder().monitorArn(TestFixtures.MONITOR_ARN).build())
            .build();

        List<AnomalyMonitor> mockMonitors = Stream.of(AnomalyMonitor.builder()
                .monitorArn(TestFixtures.MONITOR_ARN)
                .monitorName(TestFixtures.MONITOR_NAME)
                .creationDate(TestFixtures.DATE)
                .lastUpdatedDate(TestFixtures.DATE)
                .lastEvaluatedDate(TestFixtures.DATE)
                .monitorType(TestFixtures.MONITOR_TYPE_DIMENSIONAL)
                .monitorDimension(TestFixtures.DIMENSION)
                .dimensionalValueCount(TestFixtures.DIMENSIONAL_VALUE_COUNT)
                .build()).collect(Collectors.toList());

        final GetAnomalyMonitorsResponse mockResponse = GetAnomalyMonitorsResponse.builder()
                .anomalyMonitors(mockMonitors)
                .build();

        doReturn(mockResponse)
                .when(proxyClient.client()).getAnomalyMonitors(any(GetAnomalyMonitorsRequest.class));

        final ListTagsForResourceResponse listTagsResponse = ListTagsForResourceResponse.builder()
                .resourceTags(Arrays.asList(software.amazon.awssdk.services.costexplorer.model.ResourceTag.builder()
                    .key(TestFixtures.RESOURCE_TAG_KEY)
                    .value(TestFixtures.RESOURCE_TAG_VALUE)
                    .build()))
                .build();

        doReturn(listTagsResponse)
                .when(proxyClient.client()).listTagsForResource(any(ListTagsForResourceRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response
            = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(expectedModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_emptyResponse() {
        final ResourceModel model = ResourceModel.builder().monitorArn(TestFixtures.MONITOR_ARN).build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        final GetAnomalyMonitorsResponse mockResponse = GetAnomalyMonitorsResponse.builder()
                .anomalyMonitors(new ArrayList<>())
                .build();

        doReturn(mockResponse)
                .when(proxyClient.client()).getAnomalyMonitors(any(GetAnomalyMonitorsRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response
            = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }

    @Test
    public void handleRequest_unknownMonitorException() {
        final ResourceModel model = ResourceModel.builder().monitorArn(TestFixtures.MONITOR_ARN).build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        doThrow(UnknownMonitorException.class)
                .when(proxyClient.client()).getAnomalyMonitors(any(GetAnomalyMonitorsRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response
            = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }
}
