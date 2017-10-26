package com.trainning.amazonaws.service;

import java.util.HashSet;
import java.util.Set;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.DeleteTopicRequest;
import com.amazonaws.services.sns.model.ListTopicsResult;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.trainning.amazonaws.domain.Topic;

public class TopicService {

	private static TopicService instance;

	public AmazonSNSClient snsClient;

	private TopicService() {
		snsClient = new AmazonSNSClient();
		snsClient.setRegion(Region.getRegion(Regions.US_WEST_2));

	}

	public static TopicService getInstance() {
		if (instance == null) {
			instance = new TopicService();
		}
		return instance;
	}

	public Topic createTopic(String topicName) {
		System.out.println("Creating a new Topic called " + topicName);
		// create a new SNS topic
		CreateTopicRequest createTopicRequest = new CreateTopicRequest(topicName);
		CreateTopicResult createTopicResult = snsClient.createTopic(createTopicRequest);
		String topicArn = createTopicResult.getTopicArn();
		Topic topic = new Topic();
		topic.setArn(topicArn);
		topic.setName(topicName);
		System.out.println("Topic Created. Arn: " + topicArn);
		return topic;
	}

	public void subscribeToTopic(Topic topic, String protocol, String contact) {
		System.out.println("Subscribing to topic" + topic.getArn());
		// subscribe to an SNS topic
		SubscribeRequest subRequest = new SubscribeRequest(topic.getArn(), protocol, contact);
		snsClient.subscribe(subRequest);
		// get request id for SubscribeRequest from SNS metadata
		System.out.println("SubscribeRequest - " + snsClient.getCachedResponseMetadata(subRequest));
		System.out.println("Check the " + protocol + " to confirm subscription.");
	}

	public void publishTopic(Topic topic, String msg, String subject) {
		System.out.println("Publishin into topic" + topic.getArn());
		// publish to an SNS topic
		PublishRequest publishRequest = new PublishRequest(topic.getArn(), msg,subject);
		PublishResult publishResult = snsClient.publish(publishRequest);
		// print MessageId of message published to SNS topic
		System.out.println("MessageId - " + publishResult.getMessageId());
	}

	public void deleteTopic(Topic topic) {
		System.out.println("Deleting topic " + topic.getArn());
		// delete an SNS topic
		DeleteTopicRequest deleteTopicRequest = new DeleteTopicRequest(topic.getArn());
		snsClient.deleteTopic(deleteTopicRequest);
		// get request id for DeleteTopicRequest from SNS metadata
		System.out.println("DeleteTopicRequest - " + snsClient.getCachedResponseMetadata(deleteTopicRequest));
	}
	
	public Set<Topic> listTopics() {
		System.out.println("Listing Topics\n");
		// list topics
		ListTopicsResult listTopics = snsClient.listTopics();
		Set<Topic> topics = new HashSet<Topic>();
		listTopics.getTopics().forEach(topicAWS ->{
			System.out.println("  Topic Arn: " + topicAWS.getTopicArn());
			Topic topic = new Topic();
			topic.setArn(topicAWS.getTopicArn());
			topics.add(topic);
		});
		System.out.println();
		return topics;
	}
	
}
