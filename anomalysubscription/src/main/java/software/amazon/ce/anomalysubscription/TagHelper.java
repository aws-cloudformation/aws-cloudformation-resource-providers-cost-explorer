package software.amazon.ce.anomalysubscription;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import software.amazon.awssdk.services.costexplorer.model.ResourceTag;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;


public class TagHelper {
    /**
     * convertToMap
     *
     * Converts a collection of Tag objects to a tag-name -> tag-value map.
     *
     * Note: Tag objects with null tag values will not be included in the output
     * map.
     *
     * @param tags Collection of tags to convert
     * @return Converted Map of tags
     */
    public static Map<String, String> convertToMap(final Collection<ResourceTag> tags) {
        if (CollectionUtils.isEmpty(tags)) {
            return Collections.emptyMap();
        }
        return tags.stream()
                .filter(tag -> tag.value() != null)
                .collect(Collectors.toMap(
                        ResourceTag::key,
                        ResourceTag::value,
                        (oldValue, newValue) -> newValue));
    }

    /**
     * generateTagsForCreate
     *
     * Generate tags to put into resource creation request.
     * This includes user defined tags and system tags as well.
     */
    public static Map<String, String> generateTagsForCreate(final ResourceModel resourceModel, final ResourceHandlerRequest<ResourceModel> handlerRequest) {
        final Map<String, String> tagMap = new HashMap<>();

        // merge system tags with desired resource tags if your service supports CloudFormation system tags
        if (handlerRequest.getSystemTags() != null) {
            tagMap.putAll(handlerRequest.getSystemTags());
        }

        if (handlerRequest.getDesiredResourceTags() != null) {
            tagMap.putAll(handlerRequest.getDesiredResourceTags());
        }

        // TODO: get tags from resource model based on your tag property name
        // TODO: tagMap.putAll(convertToMap(resourceModel.getTags()));
        return Collections.unmodifiableMap(tagMap);
    }

}
