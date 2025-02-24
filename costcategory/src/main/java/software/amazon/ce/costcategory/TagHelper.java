package software.amazon.ce.costcategory;


import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
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
                .filter(tag -> tag.getValue() != null)
                .collect(Collectors.toMap(
                        ResourceTag::getKey,
                        ResourceTag::getValue,
                        (oldValue, newValue) -> newValue));
    }

    /**
     * generateTagsForCreate
     *
     * Generate tags to put into resource creation request.
     * This includes user defined tags and system tags as well.
     */
    public static Map<String, String> generateTagsForCreate(final ResourceHandlerRequest<ResourceModel> handlerRequest) {
        final Map<String, String> tagMap = new HashMap<>();

        // System tags
        if (handlerRequest.getSystemTags() != null) {
            tagMap.putAll(handlerRequest.getSystemTags());
        }

        // Stack tags
        if (handlerRequest.getDesiredResourceTags() != null) {
            tagMap.putAll(handlerRequest.getDesiredResourceTags());
        }

        // Resource tags
        tagMap.putAll(convertToMap(handlerRequest.getDesiredResourceState().getTags()));

        return Collections.unmodifiableMap(tagMap);
    }

    /**
     * shouldUpdateTags
     *
     * Determines whether user defined tags have been changed during update.
     */
    public static boolean shouldUpdateTags(final ResourceModel resourceModel, final ResourceHandlerRequest<ResourceModel> handlerRequest) {
        final Map<String, String> previousTags = getPreviouslyAttachedTags(handlerRequest);
        final Map<String, String> desiredTags = getNewDesiredTags(resourceModel, handlerRequest);
        return ObjectUtils.notEqual(previousTags, desiredTags);
    }

    /**
     * getPreviouslyAttachedTags
     *
     * If stack tags and resource tags are not merged together in Configuration class,
     * we will get previous attached user defined tags from both handlerRequest.getPreviousResourceTags (stack tags)
     * and handlerRequest.getPreviousResourceState (resource tags).
     */
    public static Map<String, String> getPreviouslyAttachedTags(final ResourceHandlerRequest<ResourceModel> handlerRequest) {
        final Map<String, String> previousTags = handlerRequest.getPreviousResourceTags() != null ?
                handlerRequest.getPreviousResourceTags() : new HashMap<>();

        previousTags.putAll(convertToMap(handlerRequest.getPreviousResourceState().getTags()));
        return previousTags;
    }

    /**
     * getNewDesiredTags
     *
     * If stack tags and resource tags are not merged together in Configuration class,
     * we will get new user defined tags from both resource model and previous stack tags.
     */
    public static Map<String, String> getNewDesiredTags(final ResourceModel resourceModel, final ResourceHandlerRequest<ResourceModel> handlerRequest) {
        final Map<String, String> desiredTags = handlerRequest.getDesiredResourceTags() != null ?
                handlerRequest.getDesiredResourceTags() : new HashMap<>();

        desiredTags.putAll(convertToMap(resourceModel.getTags()));
        return desiredTags;
    }

    /**
     * generateTagsToAdd
     *
     * Determines the tags the customer desired to define or redefine.
     */
    public static Map<String, String> generateTagsToAdd(final Map<String, String> previousTags, final Map<String, String> desiredTags) {
        return desiredTags.entrySet().stream()
                .filter(e -> !previousTags.containsKey(e.getKey()) || !Objects.equals(previousTags.get(e.getKey()), e.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue));
    }

    /**
     * getTagsToRemove
     *
     * Determines the tags the customer desired to remove from the function.
     */
    public static Set<String> generateTagsToRemove(final Map<String, String> previousTags, final Map<String, String> desiredTags) {
        final Set<String> desiredTagNames = desiredTags.keySet();

        return previousTags.keySet().stream()
                .filter(tagName -> !desiredTagNames.contains(tagName))
                .collect(Collectors.toSet());
    }
}
