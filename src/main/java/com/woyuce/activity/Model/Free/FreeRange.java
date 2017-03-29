package com.woyuce.activity.Model.Free;

public class FreeRange {

	private String id;
	private String title;

	public FreeRange() {
		super();
	}

	public FreeRange(String id, String title) {
		super();
		this.id = id;
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "FreeRange [id=" + id + ", title=" + title + "]";
	}

}
