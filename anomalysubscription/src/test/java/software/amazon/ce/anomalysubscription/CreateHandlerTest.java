package software.amazon.ce.anomalysubscription;

import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.CreateAnomalySubscriptionResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.doReturn;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private Logger logger;

    private final CreateHandler handler = new CreateHandler(mock(CostExplorerClient.class));

    @BeforeEach
    public void setup() {
        proxy = mock(AmazonWebServicesClientProxy.class);
        logger = mock(Logger.class);
    }

    @Test
    public void handleRequest_Success() {
        final ResourceModel model = ResourceModel.builder()
                .subscriptionName(TestFixtures.SUBSCRIPTION_NAME)
                .threshold(TestFixtures.THRESHOLD)
                .subscribers(TestFixtures.CFN_MODEL_SUBSCRIBERS)
                .frequency(TestFixtures.FREQUENCY)
                .monitorArnList(TestFixtures.MONITOR_ARNS)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final CreateAnomalySubscriptionResponse mockResponse = CreateAnomalySubscriptionResponse.builder()
                .subscriptionArn(TestFixtures.SUBSCRIPTION_ARN)
                .build();

        doReturn(mockResponse)
                .when(proxy).injectCredentialsAndInvokeV2(any(), any());

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_Success_thresholdExpression() {
        final ResourceModel model = ResourceModel.builder()
                .subscriptionName(TestFixtures.SUBSCRIPTION_NAME)
                .thresholdExpression(TestFixtures.THRESHOLD_EXPRESSION)
                .subscribers(TestFixtures.CFN_MODEL_SUBSCRIBERS)
                .frequency(TestFixtures.FREQUENCY)
                .monitorArnList(TestFixtures.MONITOR_ARNS)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final CreateAnomalySubscriptionResponse mockResponse = CreateAnomalySubscriptionResponse.builder()
                .subscriptionArn(TestFixtures.SUBSCRIPTION_ARN)
                .build();

        doReturn(mockResponse)
                .when(proxy).injectCredentialsAndInvokeV2(any(), any());

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_Success_tagsSubscription() {
        final ResourceModel model = ResourceModel.builder()
                .subscriptionName(TestFixtures.SUBSCRIPTION_NAME)
                .threshold(TestFixtures.THRESHOLD)
                .subscribers(TestFixtures.CFN_MODEL_SUBSCRIBERS)
                .frequency(TestFixtures.FREQUENCY)
                .monitorArnList(TestFixtures.MONITOR_ARNS)
                .resourceTags(TestFixtures.RESOURCE_TAGS)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final CreateAnomalySubscriptionResponse mockResponse = CreateAnomalySubscriptionResponse.builder()
                .subscriptionArn(TestFixtures.SUBSCRIPTION_ARN)
                .build();

        doReturn(mockResponse)
                .when(proxy).injectCredentialsAndInvokeV2(any(), any());

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_Fail_UserAssignValuesToReadOnlyProperties() {
        final ResourceModel model = ResourceModel.builder()
                .subscriptionName(TestFixtures.SUBSCRIPTION_NAME)
                .threshold(TestFixtures.THRESHOLD)
                .subscribers(TestFixtures.CFN_MODEL_SUBSCRIBERS)
                .frequency(TestFixtures.FREQUENCY)
                .monitorArnList(TestFixtures.MONITOR_ARNS)
                .subscriptionArn(TestFixtures.SUBSCRIPTION_ARN)
                .resourceTags(TestFixtures.RESOURCE_TAGS)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getMessage()).isNotNull();
        assertThat(response.getErrorCode()).isNotNull();
    }

    @Test
    public void handleRequest_Fail_SubscriptionAlreadyExists() {
        final ResourceModel model = ResourceModel.builder()
                .subscriptionName(TestFixtures.SUBSCRIPTION_NAME)
                .thresholdExpression(TestFixtures.THRESHOLD_EXPRESSION)
                .subscribers(TestFixtures.CFN_MODEL_SUBSCRIBERS)
                .frequency(TestFixtures.FREQUENCY)
                .monitorArnList(TestFixtures.MONITOR_ARNS)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final AwsServiceException exception = AwsServiceException.builder()
                .awsErrorDetails(AwsErrorDetails.builder()
                        .errorMessage(Utils.SUBSCRIPTION_ALREADY_EXISTS)
                        .build())
                .build();

        doThrow(exception)
                .when(proxy).injectCredentialsAndInvokeV2(any(), any());

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.AlreadyExists);
    }

    @Test
    public void handleRequest_Fail_AwsServiceException() {
        final ResourceModel model = ResourceModel.builder()
                .subscriptionName(TestFixtures.SUBSCRIPTION_NAME)
                .thresholdExpression(TestFixtures.THRESHOLD_EXPRESSION)
                .subscribers(TestFixtures.CFN_MODEL_SUBSCRIBERS)
                .frequency(TestFixtures.FREQUENCY)
                .monitorArnList(TestFixtures.MONITOR_ARNS)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final AwsServiceException exception = AwsServiceException.builder()
                .awsErrorDetails(AwsErrorDetails.builder()
                        .errorMessage("Some other error message")
                        .build())
                .build();

        doThrow(exception)
                .when(proxy).injectCredentialsAndInvokeV2(any(), any());

        assertThatThrownBy(() -> handler.handleRequest(proxy, request, null, logger))
                .isInstanceOf(AwsServiceException.class)
                .hasMessageContaining("Some other error message");
    }

    @Test
    public void handleRequest_Fail_RuntimeException() {
        final ResourceModel model = ResourceModel.builder()
                .subscriptionName(TestFixtures.SUBSCRIPTION_NAME)
                .thresholdExpression(TestFixtures.THRESHOLD_EXPRESSION)
                .subscribers(TestFixtures.CFN_MODEL_SUBSCRIBERS)
                .frequency(TestFixtures.FREQUENCY)
                .monitorArnList(TestFixtures.MONITOR_ARNS)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final RuntimeException exception = new RuntimeException("error");

        doThrow(exception)
                .when(proxy).injectCredentialsAndInvokeV2(any(), any());

        assertThatThrownBy(() -> handler.handleRequest(proxy, request, null, logger))
                .isInstanceOf(RuntimeException.class);
    }
}
