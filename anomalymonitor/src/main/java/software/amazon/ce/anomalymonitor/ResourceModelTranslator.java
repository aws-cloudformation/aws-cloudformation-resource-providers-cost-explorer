package software.amazon.ce.anomalymonitor;

import lombok.experimental.UtilityClass;
import org.apache.commons.collections.CollectionUtils;
import software.amazon.awssdk.services.costexplorer.model.ResourceTag;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@UtilityClass
public class ResourceModelTranslator {
    public static List<ResourceTag> toSDKResourceTags(List<software.amazon.ce.anomalymonitor.ResourceTag> resourceTags) {
        if (CollectionUtils.isEmpty(resourceTags)) {
            return null;
        }

        return resourceTags.stream().filter(Objects::nonNull).map(
                resourceTag -> ResourceTag.builder()
                        .key(resourceTag.getKey())
                        .value(resourceTag.getValue())
                        .build())
                .collect(Collectors.toList());
    }
}
