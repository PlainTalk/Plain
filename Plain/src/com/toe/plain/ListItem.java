package com.toe.plain;

public class ListItem {
	private String story;
	private int likes;
	private String tag;
	private boolean admin;
	private String timestamp;

	public ListItem(String story, int likes, String tag, boolean admin,
			String timestamp) {
		this.setStory(story);
		this.setLikes(likes);
		this.setTag(tag);
		this.setAdmin(admin);
		this.setTimestamp(timestamp);
	}

	public String getStory() {
		return story;
	}

	public void setStory(String story) {
		this.story = story;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

}