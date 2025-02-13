package software.amazon.ce.costcategory

import software.amazon.awssdk.services.costexplorer.CostExplorerClient
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy
import software.amazon.cloudformation.proxy.Credentials
import software.amazon.cloudformation.proxy.Logger
import software.amazon.cloudformation.proxy.ProxyClient
import software.amazon.cloudformation.proxy.ResourceHandlerRequest
import spock.lang.Specification

class HandlerSpecification extends Specification {

    def proxy = Mock(AmazonWebServicesClientProxy)

    def proxyClient = Mock(ProxyClient)

    def request = Mock(ResourceHandlerRequest)

    def callbackContext = Mock(CallbackContext)

    def logger = Mock(Logger)

    def ceClient = Mock(CostExplorerClient)

    static final MOCK_CREDENTIALS = new Credentials("accessKey", "secretKey", "sessionToken")

}
