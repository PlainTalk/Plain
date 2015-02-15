package com.toe.plain;

public class ListItem {
	private String plain;
	private int likes;

	public ListItem(String plain, int likes) {
		this.setPlain(plain);
		this.setLikes(likes);
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

}