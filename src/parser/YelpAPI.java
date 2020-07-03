package parser;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.Vector;

import javax.xml.ws.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import austinhu_CSCI201L_Assignment3.ClientHandler;
import austinhu_CSCI201L_Assignment3.Restaurant;
import austinhu_CSCI201L_Assignment3.Schedule.Task;

public class YelpAPI {
	public static Vector<Restaurant> yelpvector;
	
	public static Restaurant call(String term, double lat, double lng) throws IOException {
		 // Sending get request
//		term = term.replaceAll("��", "");
//		term = term.replaceAll(" ", "-");
//		term = term.toLowerCase();
		//System.err.println(term);
        String inline = "";
        BufferedReader br;
        InputStream in;
        Restaurant restaurant = null;
      
		try
		{
			String urlString = "https://api.yelp.com/v3/businesses/search?term="+ term + "&latitude=" + lat + "&longitude=" + lng + "&limit=3";
			if(urlString.contains(" "))
	            urlString = urlString.replace(" ", "%20");
			URL url = new URL(urlString);
			//Parse URL into HttpURLConnection in order to open the connection in order to get the JSON data
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			//Set the request to GET or POST as per the requirements
			conn.addRequestProperty("Authorization","Bearer " + "key");
			conn.setRequestMethod("GET");
			//Use the connect method to create the connection bridge
			conn.connect();
			//Get the response status of the Rest API
			int responsecode = conn.getResponseCode();			
			//if response code is not 200 then throw a runtime exception
			//else continue the actual process of getting the JSON data
			if(responsecode != 200)
				throw new RuntimeException("HttpResponseCode: " +responsecode);
			else
			{		
				in = new BufferedInputStream(conn.getInputStream());
				Scanner scanner = new Scanner(in).useDelimiter("\\A");
				inline = scanner.hasNext() ? scanner.next() : "";
				scanner.close();
			}
			
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(inline);

			JSONArray businesses = (JSONArray) jsonObject.get("businesses");
			JSONObject firstBusiness = (JSONObject) businesses.get(0);
			String firstBusinessID = firstBusiness.get("name").toString();
			JSONObject coord = (JSONObject) firstBusiness.get("coordinates");
			Double newlat = Double.parseDouble(coord.get("latitude").toString());
			Double newlong = Double.parseDouble(coord.get("longitude").toString());
//			System.err.println("Business name:" + firstBusinessID);
//			System.err.println("latitude:" + newlat);
//			System.err.println("longitude:" + newlong);
			//Disconnect the HttpURLConnection stream
			conn.disconnect();
			restaurant = new Restaurant(firstBusinessID, newlat, newlong);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return restaurant;
	}
	
}
