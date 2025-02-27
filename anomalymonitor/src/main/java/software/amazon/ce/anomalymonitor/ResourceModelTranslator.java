package software.amazon.ce.anomalymonitor;

import lombok.experimental.UtilityClass;
import org.apache.commons.collections.MapUtils;
import software.amazon.awssdk.services.costexplorer.model.ResourceTag;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@UtilityClass
public class ResourceModelTranslator {
    public static List<ResourceTag> toSDKResourceTags(Map<String, String> resourceTags) {
        if (MapUtils.isEmpty(resourceTags)) {
            return Collections.emptyList();
        }

        return resourceTags.entrySet().stream().filter(Objects::nonNull).map(
                resourceTag -> ResourceTag.builder()
                        .key(resourceTag.getKey())
                        .value(resourceTag.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    public static List<software.amazon.ce.anomalymonitor.ResourceTag> toCFNResourceTags(List<ResourceTag> resourceTags) {
        if (resourceTags == null) {
            return Collections.emptyList();
        }

        return resourceTags.stream().filter(Objects::nonNull).map(
                resourceTag -> software.amazon.ce.anomalymonitor.ResourceTag.builder()
                        .key(resourceTag.key())
                        .value(resourceTag.value())
                        .build())
                .collect(Collectors.toList());
    }
}
