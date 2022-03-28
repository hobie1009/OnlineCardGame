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

//Implements Runnable to multi-thread. Don't *need* it for this, but it makes our program more convenient/easy to write
public class ClientHandler implements Runnable {
    Socket client;
    String deckID;
    String imageURL = "https://deckofcardsapi.com/static/img/";
    ArrayList<Card> clientsCards = new ArrayList<Card>();
    Card firstCard;
    Card secondCard;

    //create constructor so we can access it in OnlineCardGame
   /* public ClientHandler(Socket socket){
        client = socket;
    }*/
    public static void main(String [] args){
        new ClientHandler();
    }
    public ClientHandler() {
        deckID = getNewDeckID();
        System.out.println(deckID);
        JsonObject jsonObject = getContents();
        System.out.println(jsonObject);
        System.out.println(getRandomCard());
    }

    @Override
    public void run() {
    }




    //Creates a jsonObject from a string(converts it bc its already a 'json object' just not in the right form
    private JsonObject getJsonObjectFromString(String s) {
        JsonReader jsonReader = Json.createReader(new StringReader(s));

        JsonObject obj = jsonReader.readObject();
        jsonReader.close();
        return obj;
    }
    JsonObject getContents(){
        JsonObject jsonObject = null;
        URL cardAPIURl;
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
            jsonObject = getJsonObjectFromString(fullResponse);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }



    //GAME RELATED
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
    Card getRandomCard(){
        JsonObject jsonObject = null;
        Card card = null;
        try {
            //change the count with the URL
            URL url = new URL("https://www.deckofcardsapi.com/api/deck/" + deckID + "/draw/?count=1");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "");
            InputStream cardResponse = connection.getInputStream();
            String fullResponse = "";
            int letter = cardResponse.read();

            while (letter != -1) {
                fullResponse += (char) letter;
                letter = cardResponse.read();
            }
            connection.disconnect();
           jsonObject = getJsonObjectFromString(fullResponse);
           JsonArray jsonArray = jsonObject.getJsonArray("cards");
           String code = ((JsonObject) jsonArray.get(0)).getString("code");
           String image = ((JsonObject) jsonArray.get(0)).getString("image");
           card = new Card(code, image);

        } catch (IOException e){
            e.printStackTrace();
        }
        //System.out.println("\n ran once");
        return card;
    }

}
