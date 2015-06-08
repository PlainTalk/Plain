package com.toe.plain;

public class TribeDirectoryListItem {
	private String name;
	private String description;
	private int likes;
	private String timestamp;

	public TribeDirectoryListItem(String name, String description, int likes,
			String timestamp) {
		this.setName(name);
		this.setDescription(description);
		this.setLikes(likes);
		this.setTimestamp(timestamp);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

}