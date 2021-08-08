package net.md_5.bungee.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import net.md_5.bungee.BetterBungee;

public class RestAPI {

	private static RestAPI instance = new RestAPI();

	public static RestAPI getInstance() {
		return instance;
	}

	public RestAPIResponse get(String urlstring) {
		return get(urlstring,15000, null);
    }

	public RestAPIResponse get(String urlstring,Proxy proxy) {
		return get(urlstring,15000, proxy);
    }

	public RestAPIResponse get(String urlstring,int timeout,Proxy proxy) {
        String response = "";
		try {
	        URL url = new URL(urlstring.replaceAll("\n", ""));
	        URLConnection con = null;
	        if (proxy == null) {
	        	con = url.openConnection();
	        } else {
	        	con = url.openConnection(proxy);
	        }
	        con.setConnectTimeout(timeout);
	        con.setRequestProperty("User-Agent", "Mozilla/5.0");
	        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	        String inputLine;
	        while ((inputLine = in.readLine()) != null) {
	            response += inputLine + "\n";
	        }
	        in.close();
		} catch (IOException e) {
			if (BetterBungee.getInstance().isDevdebugmode()) {
				e.printStackTrace();
			}
			return new RestAPIResponse("Error", true, urlstring);
		}
        return new RestAPIResponse(response, false, urlstring);
    }
}