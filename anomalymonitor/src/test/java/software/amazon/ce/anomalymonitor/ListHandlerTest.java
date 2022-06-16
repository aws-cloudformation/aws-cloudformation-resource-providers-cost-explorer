package software.amazon.ce.anomalymonitor;

import software.amazon.awssdk.services.costexplorer.model.*;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.doReturn;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ListHandlerTest {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private Logger logger;

    private final ListHandler handler = new ListHandler(TestUtils.generateTestClient());

    @BeforeEach
    public void setup() {
        proxy = mock(AmazonWebServicesClientProxy.class);
        logger = mock(Logger.class);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final ResourceModel model = ResourceModel.builder().build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .nextToken(TestFixtures.NEXT_TOKEN)
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
                .when(proxy).injectCredentialsAndInvokeV2(any(), any());

        final ProgressEvent<ResourceModel, CallbackContext> response =
            handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNotNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }
}
