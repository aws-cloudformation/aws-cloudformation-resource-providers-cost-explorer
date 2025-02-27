package software.amazon.ce.costcategory

import software.amazon.awssdk.services.costexplorer.CostExplorerClient
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy
import software.amazon.cloudformation.proxy.Credentials
import software.amazon.cloudformation.proxy.Logger
import software.amazon.cloudformation.proxy.LoggerProxy
import software.amazon.cloudformation.proxy.ProxyClient
import software.amazon.cloudformation.proxy.ResourceHandlerRequest
import spock.lang.Specification

import java.time.Duration

class HandlerSpecification extends Specification {

    def proxy

    def proxyClient = Mock(ProxyClient)

    def request = Mock(ResourceHandlerRequest)

    def callbackContext

    def logger = Mock(Logger)

    def ceClient = Mock(CostExplorerClient)

    static final MOCK_CREDENTIALS = new Credentials("accessKey", "secretKey", "sessionToken")

    void setup() {
        callbackContext = Spy(new CallbackContext())
        proxy = Spy(new AmazonWebServicesClientProxy(
                new LoggerProxy(),
                MOCK_CREDENTIALS,
                () -> Duration.ofSeconds(600).toMillis()))

        proxyClient.injectCredentialsAndInvokeV2(*_) >> { newRequest, requestFunction ->
            proxy.injectCredentialsAndInvokeV2(newRequest, requestFunction)
        }
        proxyClient.client() >> ceClient
    }
}
