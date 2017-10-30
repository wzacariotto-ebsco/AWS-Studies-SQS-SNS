package com.trainning.amazonaws;

import java.util.Scanner;
import java.util.Set;

import com.trainning.amazonaws.domain.Queue;
import com.trainning.amazonaws.domain.QueueDeadLetter;
import com.trainning.amazonaws.domain.Topic;
import com.trainning.amazonaws.domain.enums.QueueType;
import com.trainning.amazonaws.service.QueueService;
import com.trainning.amazonaws.service.TopicService;
import com.trainning.amazonaws.service.TopicQueueService;

public class Main {
	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner(System.in);
		int menuOpt = 0;
		while (menuOpt != 10) {
			System.out.println("What do you want to do?\n1 - Topic Flow\n2 - Queue Stardard Flow\n3 - Queue FiFo Flow\n"
					+ "4 - Delete Queues, Queues Messages and Topics\n5 - Subscribe Queue To a Topic\n6 - Create Queue with "
					+ "Dead Letter\n7 - List Topics and Queues\n8 - Publish to Topic by arn\n9 - List Subscription\n10 - Leave");
			try {
				menuOpt = sc.nextInt();
			} catch (Exception e) {
			}
			switch (menuOpt) {
			case 1: {
				// Create topic
				System.out.println("Type your topicName: ");
				String topicName = null;
				while (topicName == null) {
					topicName = sc.next();
				}
				Topic topic = TopicService.getInstance().createTopic(topicName);
				// Subscribe
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
				TopicService.getInstance().subscribeToTopic(topic, protocol, contact);
				System.out.println("Subscribe created");
				// Publish
				topicPublisherCall(sc, topic);
				System.out.println("Message published");
				break;
			}
			case 2: {
				// Create standard queue
				Queue queue = new Queue();
				queue.setQueueType(QueueType.STANDARD);
				queueFlow(sc, queue);
				System.out.println();
				break;
			}
			case 3: {
				// Create fifo queue
				Queue queue = new Queue();
				queue.setQueueType(QueueType.FIFO);
				queueFlow(sc, queue);
				System.out.println();
				break;
			}
			case 4: {
				// Deleting queues messages and queues
				Set<Queue> queues = QueueService.getInstance().listQueues();
				queues.forEach(queue -> {
					QueueService.getInstance().deleteQueueMessage(queue);
					QueueService.getInstance().deleteQueue(queue);
				});
				TopicService.getInstance().listTopics().forEach(topic -> {
					TopicService.getInstance().deleteTopic(topic);

				});
				break;
			}
			case 5: {
				System.out.println("Type your topicName: ");
				String topicName = null;
				while (topicName == null) {
					topicName = sc.next();
				}
				Topic topic = TopicService.getInstance().createTopic(topicName);
				System.out.println("Type your Queue Name: ");
				String queueName = null;
				while (queueName == null) {
					queueName = sc.next();
				}
				Queue queue = new Queue();
				queue.setName(queueName);
				queue.setQueueType(QueueType.STANDARD);
				Queue standardQueue = QueueService.getInstance().createQueue(queue);
				TopicQueueService topicQueueService = new TopicQueueService();
				topicQueueService.subscribeQueueToTopic(topic, standardQueue);
				break;
			}
			case 6: {
				System.out.println("Type your Queue Name: ");
				String queueName = null;
				while (queueName == null) {
					queueName = sc.next();
				}
				Queue queue = new Queue();
				queue.setName(queueName);
				queue.setQueueType(QueueType.FIFO);
				System.out.println("Type your Dead Queue Name: ");
				String queueDeadName = null;
				while (queueDeadName == null) {
					queueDeadName = sc.next();
				}
				QueueDeadLetter queueDeadLetter = new QueueDeadLetter();
				queueDeadLetter.setName(queueDeadName);
				queue.setQueueDeadLetter(queueDeadLetter);
				QueueService.getInstance().createQueue(queue);
				break;
			}
			case 7: {
				TopicService.getInstance().listTopics();
				QueueService.getInstance().listQueues();
				break;
			}
			case 8: {
				System.out.println("Type your topic arn: ");
				String topicArn = null;
				while (topicArn == null) {
					topicArn = sc.next();
				}
				Topic topic = new Topic();
				topic.setArn(topicArn);
				topicPublisherCall(sc, topic);
				break;
			}
			case 9: {
				System.out.println("Type your Topic Arn: ");
				String topicArn = null;
				while (topicArn == null) {
					topicArn = sc.next();
				}
				TopicService.getInstance().listSubsciption(topicArn);
				System.out.println();
				break;
			}
			case 10: {
				sc.close();
				System.out.println("Thanks, see ya!");
				break;
			}
			}
		}

	}

	private static void topicPublisherCall(Scanner sc, Topic topic) {
		String msg = null;
		while (msg == null || msg == "") {
			msg = sc.nextLine();
		}
		System.out.println("Type the subject to push into topic: ");
		String subject = null;
		while (subject == null || msg == "") {
			subject = sc.nextLine();
		}
		System.out.println("Type the message to push into topic: ");
		msg = null;
		while (msg == null || msg == "") {
			msg = sc.nextLine();
		}
		TopicService.getInstance().publishTopic(topic, msg, subject);
	}

	private static void queueFlow(Scanner sc, Queue queue) {
		System.out.println("Type your Queue Name: ");
		String queueName = null;
		while (queueName == null) {
			queueName = sc.next();
		}
		queue.setName(queueName);
		Queue standardQueue = QueueService.getInstance().createQueue(queue);
		// Message
		System.out.println("Type your message: ");
		String msg = null;
		while (msg == null) {
			msg = sc.next();
		}
		QueueService.getInstance().sendMessageToQueue(standardQueue, msg);
		// Recieve
		QueueService.getInstance().recievedMessageFromQueue(standardQueue);
	}
}
