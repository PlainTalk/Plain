package com.toe.plain.listitems;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ChatListItem implements Serializable {

	private String message;
	private int message_count;
	private String chat_state;
	private int direction;

	public ChatListItem(String message, int message_count, String chat_state,
			int direction) {
		this.setMessage(message);
		this.setChatState(chat_state);
		this.setMessageCount(message_count);
		this.setDirection(direction);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getChatState() {
		return chat_state;
	}

	public void setChatState(String chat_state) {
		this.chat_state = chat_state;
	}

	public int getMessageCount() {
		return message_count;
	}

	public void setMessageCount(int message_count) {
		this.message_count = message_count;
	}

	@Override
	public boolean equals(Object object) {
		boolean same = false;

		if (object != null && object instanceof ChatListItem) {
			same = this.message == ((ChatListItem) object).message;
		}

		return same;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}
}