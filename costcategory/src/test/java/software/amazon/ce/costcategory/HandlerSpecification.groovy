package software.amazon.ce.costcategory

import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy
import software.amazon.cloudformation.proxy.Logger
import software.amazon.cloudformation.proxy.ResourceHandlerRequest
import spock.lang.Specification

class HandlerSpecification extends Specification {

    def proxy = Mock(AmazonWebServicesClientProxy)

    def request = Mock(ResourceHandlerRequest)

    def callbackContext = Mock(CallbackContext)

    def logger = Mock(Logger)
}
