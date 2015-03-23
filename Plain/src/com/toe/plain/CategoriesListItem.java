package com.toe.plain;

public class CategoriesListItem {
	private String category;
	private String description;

	public CategoriesListItem(String category, String description) {
		this.setCategory(category);
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}