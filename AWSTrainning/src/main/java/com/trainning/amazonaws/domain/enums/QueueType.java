package com.trainning.amazonaws.domain.enums;

public enum QueueType {
	STANDARD("standard"), FIFO("fifo");
	
	private final String text;

    /**
     * @param text
     */
    private QueueType(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }

}
