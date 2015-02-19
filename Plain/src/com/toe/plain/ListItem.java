package com.toe.plain;

public class ListItem {
	private String plain;
	private int likes;
	private String tag;

	public ListItem(String plain, int likes, String tag) {
		this.setPlain(plain);
		this.setLikes(likes);
		this.setTag(tag);
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

}