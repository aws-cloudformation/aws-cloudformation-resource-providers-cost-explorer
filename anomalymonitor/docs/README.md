# AWS::CE::AnomalyMonitor

AWS Cost Anomaly Detection leverages advanced Machine Learning technologies to identify anomalous spend and root causes, so you can quickly take action. You can use Cost Anomaly Detection by creating monitor.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::CE::AnomalyMonitor",
    "Properties" : {
        "<a href="#monitortype" title="MonitorType">MonitorType</a>" : <i>String</i>,
        "<a href="#monitorname" title="MonitorName">MonitorName</a>" : <i>String</i>,
        "<a href="#monitordimension" title="MonitorDimension">MonitorDimension</a>" : <i>String</i>,
        "<a href="#monitorspecification" title="MonitorSpecification">MonitorSpecification</a>" : <i>String</i>,
    }
}
</pre>

### YAML

<pre>
Type: AWS::CE::AnomalyMonitor
Properties:
    <a href="#monitortype" title="MonitorType">MonitorType</a>: <i>String</i>
    <a href="#monitorname" title="MonitorName">MonitorName</a>: <i>String</i>
    <a href="#monitordimension" title="MonitorDimension">MonitorDimension</a>: <i>String</i>
    <a href="#monitorspecification" title="MonitorSpecification">MonitorSpecification</a>: <i>String</i>
</pre>

## Properties

#### MonitorType

_Required_: Yes

_Type_: String

_Allowed Values_: <code>DIMENSIONAL</code> | <code>CUSTOM</code>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### MonitorName

The name of the monitor.

_Required_: Yes

_Type_: String

_Maximum_: <code>1024</code>

_Pattern_: <code>[\S\s]*</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### MonitorDimension

The dimensions to evaluate

_Required_: No

_Type_: String

_Allowed Values_: <code>SERVICE</code>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### MonitorSpecification

_Required_: No

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the MonitorArn.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### MonitorArn

Monitor ARN

#### CreationDate

The date when the monitor was created. 

#### DimensionValueCount

Returns the <code>DimensionValueCount</code> value.

#### LastEvaluatedDate

The date when the monitor last evaluated for anomalies.

#### LastUpdatedDate

The date when the monitor was last updated.

#### DimensionalValueCount

The value for evaluated dimensions.

