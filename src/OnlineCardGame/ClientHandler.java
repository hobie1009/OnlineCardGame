package OnlineCardGame;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.HttpsURLConnection;

public class ClientHandler implements Runnable {
	Socket client;
	String deckID;
	ArrayList<Card> clientsCards = new ArrayList<Card>();

	public ClientHandler(Socket c) {
		client = c;
	}

	public void run() {

		try {
			InputStream in = client.getInputStream();
			String content = "";
			int letter = in.read();
			boolean ignore = false;
			boolean continuePlay = false;
			while (letter != -1) {
				content += (char) letter;
				if (content.equals("\r")) {
					break;
				}
				if (letter == '\n') {
					if (content.contains("GET")) {
						String[] words = content.split(" ");
						
						if (!words[1].equals("/")) {
							if (words[1].contains("favicon.ico")) {
								ignore = true;
							} else {
								continuePlay = true;
								System.out.println("words [1]" + words[1]);
							}
						}
					}
					content = "";
				}
				letter = in.read();
			}
			if (ignore) {
				client.getOutputStream().close();
				return;
			} else if (continuePlay) {
				System.out.println("Continuing a game");
			} else {
				deckID = getNewDeckID();
				System.out.println(deckID);
				Card c = getRandomCard();
				Card b = getRandomCard();
				// Get card image from API

				String sendContents = deckID + "&";
				sendContents += c.getValueAsString() + "&";
				sendContents += b.getValueAsString();

				String http = "<html><div id = \"divID\"><img src=\"" + c.getURL() + "\"></html>" + "<img src=\""
						+ b.getURL() + "\">" + "<br>" + "<h1>" + (c.getValue() + b.getValue()) + "</h1>"
						+ "<button onclick=\"buttonClicked()\">click me</button>" + "<script>"
						+ "function buttonClicked (){" 
						+ "var connection = new XMLHttpRequest();"
						+ "connection.open(\"GET\", \"http://localhost:8080/"+ sendContents +"\");" 
						+ "connection.send();"
						+ "connection.onreadystatechange = function (){" 
						+ "   if(connection.readyState == 4){"
						+ "     var d = document.getElementById (\"divID\");" 
						+ "d.innerHTML = connection.response"
						+ "    }" 
						+ "  }"
						+ "}" 
						+ "</script>" 
						+ "</div></html>";
				OutputStream out = client.getOutputStream();
				String output = "HTTP/1.1 200 \r\n";
				output += "Content-Type: text/html\r\n";
				output += "Content-Length: " + http.length();
				output += "\r\n\r\n";
				out.write(output.getBytes());
				out.write(http.getBytes());

				in.close();
				out.flush();
				out.close();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	Card getRandomCard() {
		URL cardAPIURl;
		try {
			cardAPIURl = new URL("https://deckofcardsapi.com/api/deck/" + deckID + "/draw");
			HttpsURLConnection connection = (HttpsURLConnection) cardAPIURl.openConnection();
			connection.setRequestProperty("User-Agent", "");
			InputStream cardResponse = connection.getInputStream();
			String fullResponse = "";
			int letter = cardResponse.read();

			while (letter != -1) {
				fullResponse += (char) letter;
				letter = cardResponse.read();
			}
			connection.disconnect();

			JsonObject jobj = getJsonObjectFromString(fullResponse);
			JsonArray jarr = jobj.getJsonArray("cards");
			String code = ((JsonObject) jarr.get(0)).getString("code");
			String pic = ((JsonObject) jarr.get(0)).getString("image");
			Card c = new Card(pic, code);

			return c;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private JsonObject getJsonObjectFromString(String s) {
		JsonReader jsonReader = Json.createReader(new StringReader(s));

		JsonObject obj = jsonReader.readObject();
		jsonReader.close();
		return obj;
	}

	String getNewDeckID() {
		URL cardAPIURl;
		String id = "";
		try {
			cardAPIURl = new URL("https://deckofcardsapi.com/api/deck/new/shuffle");
			HttpsURLConnection connection = (HttpsURLConnection) cardAPIURl.openConnection();
			connection.setRequestProperty("User-Agent", "");
			InputStream cardResponse = connection.getInputStream();
			String fullResponse = "";
			int letter = cardResponse.read();

			while (letter != -1) {
				fullResponse += (char) letter;
				letter = cardResponse.read();
			}
			connection.disconnect();
			JsonObject jobj = getJsonObjectFromString(fullResponse);
			id = jobj.getString("deck_id");

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return id;

	}

}
