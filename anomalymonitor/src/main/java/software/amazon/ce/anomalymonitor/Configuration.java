package software.amazon.ce.anomalymonitor;

import java.util.Map;
import java.util.stream.Collectors;

class Configuration extends BaseConfiguration {

    public Configuration() {
        super("aws-ce-anomalymonitor.json");
    }

    /*
    Overrides the base method to retrieve both stack and resource tags when the getDesiredResourceTags function is called.
     */
    @Override
    public Map<String, String> resourceDefinedTags(final ResourceModel resourceModel) {
        if (resourceModel.getResourceTags() == null) {
            return null;
        } else {
            return resourceModel.getResourceTags().stream().collect(Collectors.toMap(ResourceTag::getKey, ResourceTag::getValue));
        }
    }
}
