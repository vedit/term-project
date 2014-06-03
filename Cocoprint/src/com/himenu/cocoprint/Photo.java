package com.himenu.cocoprint;

public class Photo extends ImageMedia {
	private long id;
	private String uri;
	private long albumId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public long getAlbum_id() {
		return albumId;
	}

	public void setAlbumId(long albumId) {
		this.albumId = albumId;
	}
	
	public String toString(){
		return uri + " " + albumId;
	}
}
