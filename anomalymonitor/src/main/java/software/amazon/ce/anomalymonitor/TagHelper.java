package software.amazon.ce.anomalymonitor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import software.amazon.cloudformation.proxy.ResourceHandlerRequest;


public class TagHelper {
    /**
     * generateTagsForCreate
     *
     * Generate tags to put into resource creation request.
     * This includes user defined tags and system tags as well.
     */
    public static Map<String, String> generateTagsForCreate(final ResourceHandlerRequest<ResourceModel> handlerRequest) {
        final Map<String, String> tagMap = new HashMap<>();

        if (handlerRequest.getSystemTags() != null) {
            tagMap.putAll(handlerRequest.getSystemTags());
        }

        if (handlerRequest.getDesiredResourceTags() != null) {
            tagMap.putAll(handlerRequest.getDesiredResourceTags());
        }

        return Collections.unmodifiableMap(tagMap);
    }

}
