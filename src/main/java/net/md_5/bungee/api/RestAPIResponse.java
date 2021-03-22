package net.md_5.bungee.api;

public class RestAPIResponse {

	String text;
	
	boolean failed;
	
	public RestAPIResponse(String text,boolean failed) {
		this.text = text;
		this.failed = failed;
	}

	public String getText() {
		return text;
	}

	public boolean getFailed() {
		return failed;
	}
}
