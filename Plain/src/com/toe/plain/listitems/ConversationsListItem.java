package com.toe.plain.listitems;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ConversationsListItem implements Serializable{
	
	private String name;
	private int message_count;
	private String chat_state;

	public ConversationsListItem(String name, int message_count,
			String chat_state) {
		this.setName(name);
		this.setChatState(chat_state);
		this.setMessageCount(message_count);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

		if (object != null && object instanceof ConversationsListItem) {
			same = this.name == ((ConversationsListItem) object).name;
		}

		return same;
	}
}