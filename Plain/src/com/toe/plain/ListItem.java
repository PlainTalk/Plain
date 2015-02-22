package com.toe.plain;

public class ListItem {
	private String story;
	private int likes;
	private String tag;
	private boolean admin;

	public ListItem(String story, int likes, String tag, boolean admin) {
		this.setStory(story);
		this.setLikes(likes);
		this.setTag(tag);
		this.setAdmin(admin);
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

}