package com.trainning.amazonaws.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.amazonaws.services.sqs.model.SetQueueAttributesRequest;
import com.trainning.amazonaws.domain.Queue;
import com.trainning.amazonaws.domain.QueueDeadLetter;
import com.trainning.amazonaws.domain.enums.QueueType;

public class QueueService {

	private static QueueService instance;

	public AmazonSQSClient sqs;

	private QueueService() {
		sqs = new AmazonSQSClient();
		sqs.setRegion(Region.getRegion(Regions.US_EAST_1));
//		sqs.setEndpoint("http://localhost:9324");
	}

	public static QueueService getInstance() {
		if (instance == null) {
			instance = new QueueService();
		}
		return instance;
	}

	public Queue createQueue(Queue queue) {
		// Create a queue
		if (QueueType.STANDARD.equals(queue.getQueueType())) {
			System.out.println("Creating a new SQS queue called" + queue.getName() + " .\n");
			CreateQueueRequest createQueueRequest = new CreateQueueRequest().withQueueName(queue.getName());
			String queueURL = sqs.createQueue(createQueueRequest).getQueueUrl();
			queue.setUrl(queueURL);
		} else {
			// Create a FIFO queue
			System.out.println("Creating a new Amazon SQS FIFO queue called "+queue.getName()+".fifo.\n");
			Map<String, String> attributes = new HashMap<String, String>();
			// A FIFO queue must have the FifoQueue attribute set to True
			attributes.put("FifoQueue", "true");
			// Generate a MessageDeduplicationId based on the content, if the user doesn't
			// provide a MessageDeduplicationId
			attributes.put("ContentBasedDeduplication", "true");
			// The FIFO queue name must end with the .fifo suffix
			CreateQueueRequest createQueueRequest = new CreateQueueRequest(queue.getName() + ".fifo")
					.withAttributes(attributes);
			String queueURL = sqs.createQueue(createQueueRequest).getQueueUrl();
			queue.setUrl(queueURL);
		}
		if (queue.getQueueDeadLetter() != null)
			createDeadLetterQueue(queue.getQueueDeadLetter());
		return queue;
	}

	public void createDeadLetterQueue(QueueDeadLetter queue) {
		String queueLetterName = queue.getName();
		System.out.println("Creating Dead Letter Queue named :" + queueLetterName);
		queue.setUrl(sqs.createQueue(queueLetterName).getQueueUrl());
		GetQueueAttributesResult queue_attrs = sqs
				.getQueueAttributes(new GetQueueAttributesRequest(queue.getUrl()).withAttributeNames("QueueArn"));

		String dlQueueArn = queue_attrs.getAttributes().get("QueueArn");

		// Set dead letter queue with redrive policy on source queue.
		String srcQueueUrl = sqs.getQueueUrl(queueLetterName).getQueueUrl();

		SetQueueAttributesRequest request = new SetQueueAttributesRequest().withQueueUrl(srcQueueUrl)
				.addAttributesEntry("RedrivePolicy",
						"{\"maxReceiveCount\":\"5\", \"deadLetterTargetArn\":\"" + dlQueueArn + "\"}");

		sqs.setQueueAttributes(request);
	}

	public Set<Queue> listQueues() {
		System.out.println("Listing all queues in your account.\n");
		Set<Queue> queues = new HashSet<Queue>();
		for (String queueUrl : sqs.listQueues().getQueueUrls()) {
			System.out.println("  QueueUrl: " + queueUrl);
			Queue queue = new Queue();
			queue.setUrl(queueUrl);
			queues.add(queue);
		}
		System.out.println();
		return queues;
	}

	// Send a message
	public void sendMessageToQueue(Queue queue, String msg) {
		// Standard Queue
		if (queue.getQueueType().equals(QueueType.STANDARD)) {
			System.out.println("Sending a message to Queue.\n");
			sqs.sendMessage(new SendMessageRequest().withQueueUrl(queue.getUrl()).withMessageBody(msg));
			return;
		}
		// FIFO Queue
		System.out.println("Sending a message to Queue.\n");
		SendMessageRequest sendMessageRequest = new SendMessageRequest(queue.getUrl(), msg);
		// You must provide a non-empty MessageGroupId when sending messages to a FIFO
		// queue
		sendMessageRequest.setMessageGroupId("messageGroup1");
		// Uncomment the following to provide the MessageDeduplicationId
		// sendMessageRequest.setMessageDeduplicationId("1");
		SendMessageResult sendMessageResult = sqs.sendMessage(sendMessageRequest);
		String sequenceNumber = sendMessageResult.getSequenceNumber();
		String messageId = sendMessageResult.getMessageId();
		System.out.println(
				"SendMessage succeed with messageId " + messageId + ", sequence number " + sequenceNumber + "\n");
	}

	// Receive messages
	public List<Message> recievedMessageFromQueue(Queue queue) {
		System.out.println("Receiving messages from MyQueue.\n");
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queue.getUrl());
		List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
		for (Message message : messages) {
			System.out.println("  Message");
			System.out.println("    MessageId:     " + message.getMessageId());
			System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
			System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
			System.out.println("    Body:          " + message.getBody());
			for (Entry<String, String> entry : message.getAttributes().entrySet()) {
				System.out.println("  Attribute");
				System.out.println("    Name:  " + entry.getKey());
				System.out.println("    Value: " + entry.getValue());
			}
		}
		return messages;
	}

	public void deleteQueueMessage(Queue queue) {
		List<Message> messages = recievedMessageFromQueue(queue);
		System.out.println("Deleting a message from "+queue.getUrl());
		if (!messages.isEmpty()) {
			String messageReceiptHandle = messages.get(0).getReceiptHandle();
			sqs.deleteMessage(new DeleteMessageRequest(queue.getUrl(), messageReceiptHandle));
		}
	}

	public void deleteQueue(Queue queue) {
		// Delete a queue
		System.out.println("Deleting the test queue.\n");
		sqs.deleteQueue(new DeleteQueueRequest(queue.getUrl()));
	}

}
