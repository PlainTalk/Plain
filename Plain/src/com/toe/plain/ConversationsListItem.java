package com.toe.plain;

public class ConversationsListItem {
	private String name;

	public ConversationsListItem(String name) {
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object object) {
		boolean same = false;

		if (object != null && object instanceof ConversationsListItem) {
			same= this.name == ((ConversationsListItem) object).name;
		}

		return same;
	}
}