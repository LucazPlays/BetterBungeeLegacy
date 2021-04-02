package net.md_5.bungee.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestAPI {

	private static RestAPI instance = new RestAPI();

	public static RestAPI getInstance() {
		return instance;
	}

	public RestAPIResponse info(String urlstring) {
        String response = "";
		try {
	        URL url = new URL(urlstring);
	        HttpURLConnection con = (HttpURLConnection) url.openConnection();
	        con.setConnectTimeout(15000);
	        con.setRequestProperty("User-Agent", "Mozilla/5.0");
	        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	        String inputLine;
	        while ((inputLine = in.readLine()) != null) {
	            response += inputLine + "\n";
	        }
	        in.close();
		} catch (IOException e) {
			return new RestAPIResponse("Error", true);
		}
        return new RestAPIResponse(response, false);
    }
}