package software.amazon.ce.anomalymonitor;

import software.amazon.awssdk.services.costexplorer.model.*;

import java.util.Arrays;
import java.util.List;

public class TestFixtures {
    public static final String MONITOR_TYPE_CUSTOM = "CUSTOM";
    public static final String MONITOR_TYPE_DIMENSIONAL = "DIMENSIONAL";
    public static final String MONITOR_NAME = "TestMonitorName";
    public static final String TAGS_MONITOR_SPEC = "{\"Tags\":{\"Key\":\"aws:createdBy\",\"Values\":[\"TagValue1\",\"TagValue2\"],\"MatchOptions\":null}}";
    public static final String CC_MONITOR_SPEC = "{\"Or\":null,\"And\":null,\"Not\":null,\"Dimensions\":null,\"Tags\":null,\"CostCategories\":{\"Key\":\"myResourceEC2Category\",\"Values\":[\"EC2\"]}}";
    public static final String LINKEDACCOUNT_MONITOR_SPEC = "{\"Or\":null,\"And\":null,\"Not\":null,\"Dimensions\":{\"Key\":\"LINKED_ACCOUNT\",\"Values\":[\"123456789012\"],\"MatchOptions\":null},\"Tags\":null,\"CostCategories\":null}";
    public static final String INVALID_TYPE_MONITOR_SPEC = "Invalid";
    public static final String INVALID_VALUE_MONITOR_SPEC = "{\"Invalid\":{}}";;
    public static final String MONITOR_ARN = "arn:aws:ce::123456789012:anomalymonitor/monitorId";
    public static final String NEXT_TOKEN = "NextToken";
    public static final String DATE = "2020-11-15T00:00::00Z";
    public static final String DIMENSION = "SERVICE";
    public static final int DIMENSIONAL_VALUE_COUNT = 100;
    public static final String RESOURCE_TAG_KEY = "TestResourceTagKey";
    public static final String RESOURCE_TAG_VALUE = "TestResourceTagValue";
    static final List<ResourceTag> RESOURCE_TAGS = Arrays.asList(ResourceTag.builder()
            .key(RESOURCE_TAG_KEY)
            .value(RESOURCE_TAG_VALUE)
            .build()
        );
}
