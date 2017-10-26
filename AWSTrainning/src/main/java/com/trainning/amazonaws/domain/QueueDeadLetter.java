package com.trainning.amazonaws.domain;

public class QueueDeadLetter {
	
	String name;
	
	String url;
	
	String redrivePolicy;

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

	public String getRedrivePolicy() {
		return redrivePolicy;
	}

	public void setRedrivePolicy(String redrivePolicy) {
		this.redrivePolicy = redrivePolicy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((redrivePolicy == null) ? 0 : redrivePolicy.hashCode());
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
		QueueDeadLetter other = (QueueDeadLetter) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (redrivePolicy == null) {
			if (other.redrivePolicy != null)
				return false;
		} else if (!redrivePolicy.equals(other.redrivePolicy))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
	
	

}
