package software.amazon.ce.anomalymonitor;

import software.amazon.awssdk.services.costexplorer.model.*;

import java.util.Arrays;
import java.util.List;

public class TestFixtures {
    public static String MONITOR_TYPE_CUSTOM = "CUSTOM";
    public static String MONITOR_TYPE_DIMENSIONAL = "DIMENSIONAL";
    public static String MONITOR_NAME = "TestMonitorName";
    public static String TAGS_MONITOR_SPEC = "{\"Tags\":{\"Key\":\"aws:createdBy\",\"Values\":[\"TagValue1\",\"TagValue2\"],\"MatchOptions\":null}}";
    public static String CC_MONITOR_SPEC = "{\"Or\":null,\"And\":null,\"Not\":null,\"Dimensions\":null,\"Tags\":null,\"CostCategories\":{\"Key\":\"myResourceEC2Category\",\"Values\":[\"EC2\"]}}";
    public static String LINKEDACCOUNT_MONITOR_SPEC = "{\"Or\":null,\"And\":null,\"Not\":null,\"Dimensions\":{\"Key\":\"LINKED_ACCOUNT\",\"Values\":[\"123456789012\"],\"MatchOptions\":null},\"Tags\":null,\"CostCategories\":null}";
    public static String MONITOR_ARN = "arn:aws:ce::123456789012:anomalymonitor/monitorId";
    public static String NEXT_TOKEN = "NextToken";
    public static String DATE = "2020-11-15T00:00::00Z";
    public static String DIMENSION = "SERVICE";
    public static int DIMENSIONAL_VALUE_COUNT = 100;
}
