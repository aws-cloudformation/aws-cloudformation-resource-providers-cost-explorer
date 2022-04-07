package software.amazon.ce.anomalymonitor;


import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

public class ConfigurationTest {
    @Test
    public void handleRequest_Success_ResourceDefinedTagsExist() {
        final Configuration configuration = new Configuration();

        final ResourceModel model = ResourceModel.builder()
                .monitorName(TestFixtures.MONITOR_NAME)
                .monitorType(TestFixtures.MONITOR_TYPE_DIMENSIONAL)
                .monitorArn(TestFixtures.MONITOR_ARN)
                .resourceTags(TestFixtures.RESOURCE_TAGS)
                .build();

        final Map<String, String> resourceDefinedTags = configuration.resourceDefinedTags(model);

        assertThat(resourceDefinedTags).isNotNull();
        assertThat(resourceDefinedTags).hasSameSizeAs(TestFixtures.RESOURCE_TAGS);

        assert resourceDefinedTags.containsKey(TestFixtures.RESOURCE_TAG_KEY);
        assert resourceDefinedTags.get(TestFixtures.RESOURCE_TAG_KEY).equals(TestFixtures.RESOURCE_TAG_VALUE);
    }

    @Test
    public void handleRequest_Success_ResourceDefinedTagsEmpty() {
        final Configuration configuration = new Configuration();

        final ResourceModel model = ResourceModel.builder()
                .monitorName(TestFixtures.MONITOR_NAME)
                .monitorType(TestFixtures.MONITOR_TYPE_DIMENSIONAL)
                .monitorArn(TestFixtures.MONITOR_ARN)
                .build();

        final Map<String, String> resourceDefinedTags = configuration.resourceDefinedTags(model);

        assertThat(resourceDefinedTags).isNull();
    }
}
