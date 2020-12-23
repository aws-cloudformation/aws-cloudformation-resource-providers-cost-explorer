# AWS::CE::AnomalySubscription

Schema for AnomalySubscription Resource

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::CE::AnomalySubscription",
    "Properties" : {
        "<a href="#subscriptionname" title="SubscriptionName">SubscriptionName</a>" : <i>String</i>,
        "<a href="#monitorarnlist" title="MonitorArnList">MonitorArnList</a>" : <i>[ String, ... ]</i>,
        "<a href="#subscribers" title="Subscribers">Subscribers</a>" : <i>[ <a href="subscriber.md">Subscriber</a>, ... ]</i>,
        "<a href="#threshold" title="Threshold">Threshold</a>" : <i>Double</i>,
        "<a href="#frequency" title="Frequency">Frequency</a>" : <i>String</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::CE::AnomalySubscription
Properties:
    <a href="#subscriptionname" title="SubscriptionName">SubscriptionName</a>: <i>String</i>
    <a href="#monitorarnlist" title="MonitorArnList">MonitorArnList</a>: <i>
      - String</i>
    <a href="#subscribers" title="Subscribers">Subscribers</a>: <i>
      - <a href="subscriber.md">Subscriber</a></i>
    <a href="#threshold" title="Threshold">Threshold</a>: <i>Double</i>
    <a href="#frequency" title="Frequency">Frequency</a>: <i>String</i>
</pre>

## Properties

#### SubscriptionName

The name of the monitor.

_Required_: Yes

_Type_: String

_Maximum_: <code>1024</code>

_Pattern_: <code>[\S\s]*</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### MonitorArnList

A list of cost anomaly monitors.

_Required_: Yes

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Subscribers

A list of subscriber

_Required_: Yes

_Type_: List of <a href="subscriber.md">Subscriber</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Threshold

The dollar value that triggers a notification if the threshold is exceeded. 

_Required_: Yes

_Type_: Double

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Frequency

The frequency at which anomaly reports are sent over email. 

_Required_: Yes

_Type_: String

_Allowed Values_: <code>DAILY</code> | <code>IMMEDIATE</code> | <code>WEEKLY</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the SubscriptionArn.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### SubscriptionArn

Subscription ARN

#### AccountId

The accountId

#### Status

Returns the <code>Status</code> value.
