package software.amazon.ce.anomalysubscription;


import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationTest {
    @Test
    public void handleRequest_Success_ResourceDefinedTagsExist() {
        final Configuration configuration = new Configuration();

        final ResourceModel model = ResourceModel.builder()
                .subscriptionName(TestFixtures.SUBSCRIPTION_NAME)
                .threshold(TestFixtures.THRESHOLD)
                .subscribers(TestFixtures.CFN_MODEL_SUBSCRIBERS)
                .frequency(TestFixtures.FREQUENCY)
                .monitorArnList(TestFixtures.MONITOR_ARNS)
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
                .subscriptionName(TestFixtures.SUBSCRIPTION_NAME)
                .threshold(TestFixtures.THRESHOLD)
                .subscribers(TestFixtures.CFN_MODEL_SUBSCRIBERS)
                .frequency(TestFixtures.FREQUENCY)
                .monitorArnList(TestFixtures.MONITOR_ARNS)
                .build();

        final Map<String, String> resourceDefinedTags = configuration.resourceDefinedTags(model);

        assertThat(resourceDefinedTags).isNull();
    }
}
