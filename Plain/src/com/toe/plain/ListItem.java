package com.toe.plain;

public class ListItem {
	private String plain;
	private int likes;
	private String tag;
	private boolean admin;

	public ListItem(String plain, int likes, String tag, boolean admin) {
		this.setPlain(plain);
		this.setLikes(likes);
		this.setTag(tag);
		this.setAdmin(admin);
	}

	public String getPlain() {
		return plain;
	}

	public void setPlain(String plain) {
		this.plain = plain;
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