package software.amazon.ce.anomalymonitor;

import lombok.experimental.UtilityClass;
import org.apache.commons.collections.MapUtils;
import software.amazon.awssdk.services.costexplorer.model.ResourceTag;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@UtilityClass
public class ResourceModelTranslator {
    public static List<ResourceTag> toSDKResourceTags(Map<String, String> resourceTags) {
        if (MapUtils.isEmpty(resourceTags)) {
            return null;
        }

        return resourceTags.entrySet().stream().filter(Objects::nonNull).map(
                resourceTag -> ResourceTag.builder()
                        .key(resourceTag.getKey())
                        .value(resourceTag.getValue())
                        .build())
                .collect(Collectors.toList());
    }
}
