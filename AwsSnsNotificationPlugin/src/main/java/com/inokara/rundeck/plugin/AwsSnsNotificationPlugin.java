package com.inokara.rundeck.plugin;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.core.plugins.configuration.PropertyScope;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty;
import com.dtolabs.rundeck.plugins.notification.NotificationPlugin;

import java.sql.Timestamp;
import java.util.Map;

@Plugin(service = "Notification", name = "AWS_SNS_Notification")
@PluginDescription(title = "AWS SNS Notification Plugin", description = "AWS SNS Notification Plugin.")
public class AwsSnsNotificationPlugin implements NotificationPlugin {

  @PluginProperty(title = "AWS Access Key", description = "AWS Access Key")
  private String aws_access_key;

  @PluginProperty(title = "AWS Secret Key", description = "AWS Secret Key")
  private String aws_secret_access_key;

  @PluginProperty(
    title = "AWS Region",
    description = "AWS Region to use. You can use one of the supported region names",
    required = true,
    defaultValue = "ap-northeast-1")
  private String aws_region;

  @PluginProperty(
    title = "AWS SNS Topic ARN",
    description = "AWS SNS Topic ARN.\n" +
      "Set empty if using system properties(`framework.properties` or `project.properties`).\n" +
      "See `Plugin List(/menu/plugins)` page for detail.",
    scope = PropertyScope.Instance)
  private String aws_sns_topic_arn;

  private String generateMessage(String trigger, Map executionData) {
    String jobExecutionId = executionData.get("id").toString();
    Timestamp startedAt = (Timestamp) executionData.get("dateStarted");
    String user = (String) executionData.get("user");
    String jobUrl = (String) executionData.get("href");

    Map jobData = (Map) executionData.get("job");
    String jobName = (String) jobData.get("name");
    String project = (String) jobData.get("project");

    return String.format("Job `%s`(#%s) is %s.\n", jobName, jobExecutionId, trigger)
      + String.format("Project is %s.\n", project)
      + String.format("Started at %s by %s.\n\n", startedAt, user)
      + "Job Execution URL: " + jobUrl;
  }

  public boolean postNotification(String trigger, Map executionData, Map config) {
    AmazonSNS snsClient;
    if (isNullOrEmpty(aws_access_key) || isNullOrEmpty(aws_secret_access_key)) {
      snsClient = AmazonSNSClientBuilder.standard()
        .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
        .withRegion(Regions.fromName(aws_region))
        .build();
    } else {
      BasicAWSCredentials awsCreds = new BasicAWSCredentials(aws_access_key, aws_secret_access_key);
      snsClient = AmazonSNSClientBuilder.standard()
        .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
        .withRegion(Regions.fromName(aws_region))
        .build();
    }
    //
    String subject = "Rundeck job execution is " + trigger;
    String message = generateMessage(trigger, executionData);
    PublishRequest publishRequest = new PublishRequest(aws_sns_topic_arn, message, subject);
    try {
      PublishResult publishResult = snsClient.publish(publishRequest);
      return true;
    } catch (Exception ex) {
      ex.printStackTrace(System.err);
      return false;
    }
  }

  private boolean isNullOrEmpty(String string) {
    return string == null || string.isEmpty();
  }
}
