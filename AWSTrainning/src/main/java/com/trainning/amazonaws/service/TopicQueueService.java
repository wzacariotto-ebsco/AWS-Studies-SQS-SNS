package com.trainning.amazonaws.service;

import com.amazonaws.services.sns.util.Topics;
import com.trainning.amazonaws.domain.Queue;
import com.trainning.amazonaws.domain.Topic;

public class TopicQueueService {

	public void subscribeQueueToTopic(Topic topic, Queue queue) throws Exception {
		
		Topics.subscribeQueue(TopicService.getInstance().snsClient,QueueService.getInstance().sqs,topic.getArn(),queue.getUrl());
		System.out.println("Queue subscribed to Topic");
	}
}
