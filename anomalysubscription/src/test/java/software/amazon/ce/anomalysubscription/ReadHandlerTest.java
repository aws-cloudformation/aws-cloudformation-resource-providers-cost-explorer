package software.amazon.ce.anomalysubscription;

import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.GetAnomalySubscriptionsRequest;
import software.amazon.awssdk.services.costexplorer.model.GetAnomalySubscriptionsResponse;
import software.amazon.awssdk.services.costexplorer.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.costexplorer.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.costexplorer.model.UnknownSubscriptionException;
import software.amazon.awssdk.services.costexplorer.model.Dimension;
import software.amazon.awssdk.services.costexplorer.model.DimensionValues;
import software.amazon.awssdk.services.costexplorer.model.Expression;
import software.amazon.awssdk.services.costexplorer.model.MatchOption;
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
import software.amazon.cloudformation.proxy.HandlerErrorCode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.time.Duration;
import java.util.ArrayList;

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
                .subscriptionArn(TestFixtures.SUBSCRIPTION_ARN)
                .subscriptionName(TestFixtures.SUBSCRIPTION_NAME)
                .monitorArnList(TestFixtures.MONITOR_ARNS)
                .thresholdExpression(Utils.toJson(Expression.builder()
                    .dimensions(DimensionValues.builder()
                            .key(Dimension.ANOMALY_TOTAL_IMPACT_PERCENTAGE)
                            .matchOptions(Collections.singletonList(MatchOption.GREATER_THAN_OR_EQUAL))
                            .values(Collections.singletonList("100"))
                            .build())
                    .build()))
                .subscribers(ResourceModelTranslator.toSubscribers(TestFixtures.SUBSCRIBERS))
                .frequency(TestFixtures.FREQUENCY)
                .resourceTags(TestFixtures.RESOURCE_TAGS)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(ResourceModel.builder().subscriptionArn(TestFixtures.SUBSCRIPTION_ARN).build())
                .build();

        final GetAnomalySubscriptionsResponse mockResponse = GetAnomalySubscriptionsResponse.builder()
                .anomalySubscriptions(TestFixtures.anomalySubscriptions)
                .build();

        doReturn(mockResponse)
                .when(proxyClient.client()).getAnomalySubscriptions(any(GetAnomalySubscriptionsRequest.class));

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
    public void handleRequest_Failure_Read() {
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(ResourceModel.builder().subscriptionArn(TestFixtures.SUBSCRIPTION_ARN).build())
                .build();

        doThrow(UnknownSubscriptionException.class)
                .when(proxyClient.client()).getAnomalySubscriptions(any(GetAnomalySubscriptionsRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertEquals(HandlerErrorCode.NotFound, response.getErrorCode());
    }

    @Test
    public void handleRequest_emptyResponse() {
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(ResourceModel.builder().subscriptionArn(TestFixtures.SUBSCRIPTION_ARN).build())
                .build();

        final GetAnomalySubscriptionsResponse mockResponse = GetAnomalySubscriptionsResponse.builder()
                .anomalySubscriptions(new ArrayList<>())
                .build();

        doReturn(mockResponse)
                .when(proxyClient.client()).getAnomalySubscriptions(any(GetAnomalySubscriptionsRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertEquals(HandlerErrorCode.NotFound, response.getErrorCode());
    }
}
