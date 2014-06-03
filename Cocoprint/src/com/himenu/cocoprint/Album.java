package com.himenu.cocoprint;

import java.util.List;

public class Album {
	private long id;
	private String name;
	private long created;
	private long updated;
    private List<ImageMedia> images; 
    
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	public long getUpdated() {
		return updated;
	}

	public void setUpdated(long updated) {
		this.updated = updated;
	} 
	
	public String toString() {
		return name + " " + updated; 
	}

	public List<ImageMedia> getImages() {
		return images;
	}

	public void setImages(List<ImageMedia> images) {
		this.images = images;
	}
}
