package com.trainning.amazonaws;

import java.util.Scanner;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
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

/**
 * This sample demonstrates how to perform a few simple operations with the
 * Amazon DynamoDB service.
 */
public class AmazonSNSCore {

	private static AmazonSNSClient snsClient;

	private static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) throws Exception {
		init();
		String topicArn = null;
		int menuOpt = 0;
		while (menuOpt != 5) {
			System.out.println(
					"\nWhat do you want to do?\n1 - Create a Topic\n2 - Subscribe to a Topic\n3 - Publish Topic\n4 - Delete Topic\n5 - Leave");
			menuOpt = sc.nextInt();
			switch (menuOpt) {
			case 1: {
				System.out.println("Type your topicName: ");
				String topicName = null;
				while (topicName == null) {
					topicName = sc.next();
				}

				topicArn = createTopic(topicName);
				System.out.println("Topic Created. Arn: " + topicArn);
				break;
			}
			case 2: {
				if (topicArn == null) {
					System.out.println("No topic created");
					break;
				}
				System.out.println("Type your protocol: ");
				String protocol = null;
				while (protocol == null) {
					protocol = sc.next();
				}
				System.out.println("Type your contact: ");
				String contact = null;
				while (contact == null) {
					contact = sc.next();
				}
				subscribeToTopic(topicArn, protocol, contact);
				System.out.println("Subscribe created");
				break;
			}
			case 3: {
				if (topicArn == null) {
					System.out.println("No topic created");
					break;
				}
				System.out.println("Type the message to push into topic: ");
				String msg = null;
				while (msg == null) {
					msg = sc.nextLine();
				}
				publishTopic(topicArn, msg);
				System.out.println("Message published");
				break;
			}
			case 4: {
				System.out.println("Wanna delete topic by name: <y/n>");
				String op = null;
				while (op == null) {
					op = sc.next();
				}if(op.equals("y")) {
					System.out.println("Type the name:");
					String topicNameToDelete = null;
					while (topicNameToDelete == null) {
						topicNameToDelete = sc.next();
					}try {
					deleteTopicByArn(createTopic(topicNameToDelete));}
					catch(Exception e) {
						e.printStackTrace();
					}
				} else {
				if (topicArn == null) {
					System.out.println("No topic created");
					break;
				}
					System.out.println("Deleting the topic by Arn");
					deleteTopicByArn(topicArn);
				}
				System.out.println("Topic deleted");
				break;
			}
			}
		}
		System.out.println("Thanks, see ya!");

	}

	private static void init() throws Exception {
		snsClient = new AmazonSNSClient(new ProfileCredentialsProvider("default").getCredentials());
		snsClient.setRegion(Region.getRegion(Regions.US_EAST_1));
	}

	public static String createTopic(String topicName) {
		// create a new SNS topic
		CreateTopicRequest createTopicRequest = new CreateTopicRequest(topicName);
		CreateTopicResult createTopicResult = snsClient.createTopic(createTopicRequest);
		// print TopicArn
		return createTopicResult.getTopicArn();
	}

	public static void subscribeToTopic(String topicArn, String protocol, String contact) {
		// subscribe to an SNS topic
		SubscribeRequest subRequest = new SubscribeRequest(topicArn, protocol, contact);
		snsClient.subscribe(subRequest);
		// get request id for SubscribeRequest from SNS metadata
		System.out.println("SubscribeRequest - " + snsClient.getCachedResponseMetadata(subRequest));
		System.out.println("Check the " + protocol + " to confirm subscription.");
	}

	public static void publishTopic(String topicArn, String msg) {
		// publish to an SNS topic
		PublishRequest publishRequest = new PublishRequest(topicArn, msg);
		PublishResult publishResult = snsClient.publish(publishRequest);
		// print MessageId of message published to SNS topic
		System.out.println("MessageId - " + publishResult.getMessageId());
	}

	public static void deleteTopicByArn(String topicArn) {
		// delete an SNS topic
		DeleteTopicRequest deleteTopicRequest = new DeleteTopicRequest(topicArn);
		snsClient.deleteTopic(deleteTopicRequest);
		// get request id for DeleteTopicRequest from SNS metadata
		System.out.println("DeleteTopicRequest - " + snsClient.getCachedResponseMetadata(deleteTopicRequest));
	}

}
