package net.md_5.bungee.api;

import java.io.IOException;

public class RestAPIResponse {

	private String text;

	private boolean failed;
	
	public RestAPIResponse(String text,boolean failed) {
		this.text = text;
		this.failed = failed;
	}

	public RestAPIResponse(String string, boolean b, IOException ex) {
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
