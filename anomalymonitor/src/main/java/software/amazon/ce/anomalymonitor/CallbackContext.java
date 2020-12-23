package software.amazon.ce.anomalymonitor;

import lombok.Builder;
        import lombok.Data;
        import lombok.NoArgsConstructor;

/**
 * The CallbackContext will hold details about the current state of the execution.
 * When we return an IN_PROGRESS state and a CallbackContext,
 * CloudFormation will re-invoke the handler and pass the CallbackContext in with the request.
 * You can then make decisions based on what is included in the context.
 *
 * Handler have one minute timeout. All Cost Category API can be finished within one minute.
 * So we don't need the context for now. Keep it here for future usage.
 *
 * For example usage of the class, see https://docs.aws.amazon.com/cloudformation-cli/latest/userguide/resource-type-walkthrough.html#resource-type-walkthrough-implement
 */
@Data
@NoArgsConstructor
@Builder
public class CallbackContext {

}
