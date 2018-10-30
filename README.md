## rundeck-aws_sns-notification-plugin

### build

Run `gradle build`.

```sh
% cd AwsSnsNotificationPlugin 
% gradle build 
```

or use `gradle wrapper`

```sh
% cd AwsSnsNotificationPlugin
% ./gradlew build
```

### Install

Copy the jar file to the plugins directory: 

```
% sudo cp -i build/libs/AwsSnsNotificationPlugin-0.0.1.jar  /var/lib/rundeck/libext/
```

### Configure

![](https://raw.githubusercontent.com/inokappa/rundeck-aws_sns-notification-plugin/master/doc/images/2015102602.png)

- AWS Access Key
- AWS Secret Key
- AWS Region
- AWS SNS Topic ARN

When `AWS Access Key` or `AWS Secret Key` are empty,
the plugin use [DefaultAWSCredentialsProviderChain](https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/auth/DefaultAWSCredentialsProviderChain.html).


    AWS credentials provider chain that looks for credentials in this order:
    
    * Environment Variables - AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY (RECOMMENDED since they are recognized by all the AWS SDKs and CLI except for .NET), or AWS_ACCESS_KEY and AWS_SECRET_KEY (only recognized by Java SDK)
    * Java System Properties - aws.accessKeyId and aws.secretKey
    * Credential profiles file at the default location (~/.aws/credentials) shared by all AWS SDKs and the AWS CLI
    * Credentials delivered through the Amazon EC2 container service if AWS_CONTAINER_CREDENTIALS_RELATIVE_URI" environment variable is set and security manager has permission to access the variable,
    * Instance profile credentials delivered through the Amazon EC2 metadata service

