package com.trainning.amazonaws.domain;

import com.trainning.amazonaws.domain.enums.QueueType;

public class Queue {
	
	String name;
	
	String url;
	
	QueueType queueType;
	
	QueueDeadLetter queueDeadLetter;



	public void setQueueType(QueueType queueType) {
		this.queueType = queueType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public QueueType getQueueType() {
		return queueType;
	}

	public void setQueueType(String type) {
		this.queueType = QueueType.valueOf(type);
	}

	public QueueDeadLetter getQueueDeadLetter() {
		return queueDeadLetter;
	}

	public void setQueueDeadLetter(QueueDeadLetter queueDeadLetter) {
		this.queueDeadLetter = queueDeadLetter;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((queueDeadLetter == null) ? 0 : queueDeadLetter.hashCode());
		result = prime * result + ((queueType == null) ? 0 : queueType.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Queue other = (Queue) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (queueDeadLetter == null) {
			if (other.queueDeadLetter != null)
				return false;
		} else if (!queueDeadLetter.equals(other.queueDeadLetter))
			return false;
		if (queueType != other.queueType)
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
}
