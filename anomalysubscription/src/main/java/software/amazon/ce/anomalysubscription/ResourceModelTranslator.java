package software.amazon.ce.anomalysubscription;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.google.common.collect.ImmutableMap;
import lombok.experimental.UtilityClass;
import software.amazon.awssdk.services.costexplorer.model.*;
import software.amazon.awssdk.services.costexplorer.model.Subscriber;
import software.amazon.awssdk.services.costexplorer.model.SubscriberType;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import org.apache.commons.collections.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@UtilityClass
public class ResourceModelTranslator {
    public static List<software.amazon.ce.anomalysubscription.Subscriber> toSubscribers(List<Subscriber> subscribers) {
        if (CollectionUtils.isEmpty(subscribers)) {
            return null;
        }

        return subscribers.stream().map(
                subscriber -> software.amazon.ce.anomalysubscription.Subscriber.builder()
                    .address(subscriber.address())
                    .status(subscriber.status() != null ? subscriber.status().toString() : null)
                    .type(subscriber.type().toString())
                    .build())
                .collect(Collectors.toList());
    }

    public static List<Subscriber> toSDKSubscribers(List<software.amazon.ce.anomalysubscription.Subscriber> subscribers) {
        if (CollectionUtils.isEmpty(subscribers)) {
            return null;
        }

        return subscribers.stream().filter(Objects::nonNull).map(
                subscriber -> Subscriber.builder()
                    .address(subscriber.getAddress())
                    .status(subscriber.getStatus() != null ? subscriber.getStatus().toString() : null)
                    .type(subscriber.getType() != null ? subscriber.getType().toString() : null)
                    .build())
                .collect(Collectors.toList());
    }
}
