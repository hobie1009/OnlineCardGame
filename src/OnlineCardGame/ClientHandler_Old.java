package OnlineCardGame;

import Blackjack.Card;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.HttpsURLConnection;

public class ClientHandler_Old implements Runnable {
	Socket client;
	String deckID;
	String imageURL = "https://deckofcardsapi.com/static/img/";
	ArrayList<Card> clientsCards = new ArrayList<Card>();
	Card c;
	Card b;

	//Constructor Method to access it in OnlineCardGame
	public ClientHandler_Old(Socket c) {
		client = c;
	}

	public void run() {

		try {
			InputStream in = client.getInputStream();
			String content = "";
			int letter = in.read();
			boolean ignore = false;
			boolean continuePlay = false;
			String deckInfo = "";
			while (letter != -1) {
				content += (char) letter;
				if (content.equals("\r")) {
					break;
				}
				if (letter == '\n') {
					if (content.contains("GET")) {
						//This splits the 'stuff' we get back. and the stuff we want is at index 1, hence why we are accessing it
						String[] words = content.split(" ");
						if (!words[1].equals("/")) {
							if (words[1].contains("favicon.ico")) {
								ignore = true;
							} else {
								System.out.println("\n\n");
								System.out.println("words [1]" + words[1]);
								deckInfo = words[1].substring(1);
								continuePlay = true;

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
				String[] info = deckInfo.split("&");
				deckID = info[0];

				int totalValue = 0;
				String sendContents = deckID + "&";
				String html = "<html><div id = \"divID\">";
				for(int i = 1; i < info.length; i++){
					char answer = info[i].substring(0, 1).charAt(0); int value = 0;
					System.out.println("this is what the value is: " + answer);
					//if ((!Character.isDigit(answer) || answer == 0) && answer != 'A'){ value = 10; }
					//else {value = answer - 48;}
					totalValue += value;
					html += "<img src=\"" + imageURL + info[i] + ".png\">";
					sendContents += info[i] + "&";
				}
				Card newCard = getRandomCard();
				sendContents += newCard.getValueAsString();
				totalValue += (newCard.getValue() + c.getValue() + b.getValue());
				html += "<img src=\"" + newCard.getURL() + "\">";
				html += "<h1>" + totalValue + "</h1>"
						+ "<button onclick=\"buttonClicked()\">draw</button>" + "<script>"
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
						+ "</script>";
				html += "</div></html>";
				OutputStream out = client.getOutputStream();
				String output = "HTTP/1.1 200 \r\n";
				output += "Content-Type: text/html\r\n";
				output += "Content-Length: " + html.length();
				output += "\r\n\r\n";
				out.write(output.getBytes());
				out.write(html.getBytes());

				in.close();
				out.flush();
				out.close();
			} else {
				deckID = getNewDeckID();
				//System.out.println(deckID);
				c = getRandomCard();
				b = getRandomCard();
				// Get card image from API

				String sendContents = deckID + "&";
				sendContents += c.getValueAsString() + "&";
				sendContents += b.getValueAsString();
				//System.out.println(sendContents);
				String html = "<html><div id = \"divID\"><img src=\"" + c.getURL() + "\"></html>" + "<img src=\""
						+ b.getURL() + "\">" + "<br>" + "<h1>" + (c.getValue() + b.getValue()) + "</h1>"
						+ "<button onclick=\"buttonClicked()\">draw</button>" + "<script>"
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
				output += "Content-Length: " + html.length();
				output += "\r\n\r\n";
				out.write(output.getBytes());
				out.write(html.getBytes());

				in.close();
				out.flush();
				out.close();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//Organize a lot of repeated code into methods. Either organize alphabetically or frequency.
	//Comments

	//Gets a random card from the API
	Card getRandomCard() {
		//This is the URL for our card
		URL cardAPIURl;
		try {
			/*
			Sets the url to a url. We insert our deck to draw from said deck
			HttpsConnection --> ties client to customer(pathway to server). Uses the URL(address/location of thing you are connecting to) to do so.
			InputStream is the "stuff" coming from the connection to analyze and use.
			URL: house address(location)  HTTPconnection: Driveway(path) InputStream: vehicle driving down driveway(data)
			fullresponse + while loop --> unloading the car to get the data
			 */
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
			System.out.println(jobj);
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
