package com.toe.plain.listitems;

public class TribeDirectoryListItem {
	private String name;
	private String description;
	private int likes;
	private String timestamp;
	private boolean newTribePlains;

	public TribeDirectoryListItem(String name, String description, int likes,
			String timestamp, boolean newTribePlains) {
		this.setName(name);
		this.setDescription(description);
		this.setLikes(likes);
		this.setTimestamp(timestamp);
		this.setNewTribePlains(newTribePlains);
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

	public boolean hasNewTribePlains() {
		return newTribePlains;
	}

	public void setNewTribePlains(boolean newTribePlains) {
		this.newTribePlains = newTribePlains;
	}

}