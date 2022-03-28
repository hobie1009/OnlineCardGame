package Practice;

import OnlineCardGame.Card;

import javax.imageio.ImageIO;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class APIPractice implements ActionListener {
    public static String deckID = "";
    JFrame frame = new JFrame();
    JPanel panel = new JPanel();
    JButton drawCard = new JButton();
    JLabel label  = new JLabel();
    public static Card initial;

    //Either make newDeck static or create APIPractice Object
    public static void main(String [] args) throws IOException {
        APIPractice apiPractice = new APIPractice();
        deckID = apiPractice.newDeck();
        initial = apiPractice.getCard();
        System.out.println("Deck ID: " + apiPractice.deckID);
        System.out.println("Card URL: " + initial.getURL());

        apiPractice.initGUI();

    }

    //API METHODS
    public String newDeck() throws IOException {
        URL address = new URL("https://www.deckofcardsapi.com/api/deck/new/shuffle");
        String id = "";
        HttpsURLConnection connection = (HttpsURLConnection) address.openConnection();
        connection.setRequestProperty("User-Agent", "");
        InputStream siteData = connection.getInputStream();
        String response = getResponse(siteData);
        connection.disconnect();
        JsonObject jobj = getJsonObjectFromString(response);
        id = jobj.getString("deck_id");
        siteData.close();

        return id;
    }
    public Card getCard() throws IOException {
        Card card;
        URL address = new URL("https://deckofcardsapi.com/api/deck/" + deckID + "/draw");
        HttpsURLConnection connection = (HttpsURLConnection) address.openConnection();
        connection.setRequestProperty("User-Agent", "");
        InputStream siteData = connection.getInputStream();
        String response = getResponse(siteData);
        connection.disconnect();
        JsonObject jobj = getJsonObjectFromString(response);
        JsonArray jarr = jobj.getJsonArray("cards");
        String name = ((JsonObject) jarr.get(0)).getString("code");
        String image  = ((JsonObject) jarr.get(0)).getString("image");

        card = new Card(image, name);
        return card;

    }
    private String getResponse(InputStream inputStream) throws IOException {
        int letter = inputStream.read(); String response = "";
        while(letter != -1){
            response += (char) letter;
            letter = inputStream.read();
        }
        return response;
    }
    private JsonObject getJsonObjectFromString(String s) {
        JsonReader jsonReader = Json.createReader(new StringReader(s));

        JsonObject obj = jsonReader.readObject();
        jsonReader.close();
        return obj;
    }

    //OTHER METHODS
    public void initGUI() throws IOException {
        frame = new JFrame("BlackJack");
        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);



    }

    //ACTION LISTENER
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == drawCard){
            try {
                panel.remove(label);
                System.out.println(initial.getURL());
                label = new JLabel(new ImageIcon(new URL(initial.getURL())));
                //label.setIcon(new ImageIcon(new URL(initial.getURL())));
                label.setText("Not Label");
                panel.add(label);
                frame.pack();
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
