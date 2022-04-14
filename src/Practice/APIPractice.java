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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class APIPractice implements ActionListener {
    public static String deckID = "";
    JFrame frame = new JFrame();
    JPanel panel = new JPanel();
    JButton drawCard = new JButton();
    JButton stay = new JButton();
    JLabel label  = new JLabel();
    JLabel display = new JLabel();
    JLabel dealer = new JLabel();

    int cardCount = 1;
    int valueNum = 0;
    String address;
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
        //Frame
        frame = new JFrame("BlackJack");
        //Change this preffered size
        frame.setPreferredSize(new Dimension(1000, 530));
        frame.setLocation(980, 220);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.add(panel);

        //Buttons
        drawCard.setPreferredSize(new Dimension(980, 50));
        drawCard.setText("Draw Card");
        drawCard.addActionListener(this);
        stay.setPreferredSize(new Dimension(980, 50));
        stay.setText("Stay");
        stay.setLocation(0, 400);
        stay.addActionListener(this);
        valueNum = initial.getValue();

        //Label
        display.setPreferredSize(new Dimension(200, 50));
        display.setText("Score: " + initial.getValue());
        dealer.setPreferredSize(new Dimension(200, 50));
        dealer.setText("Dealer Score: ");

        panel.add(display);
        panel.add(dealer);
        panel.add(drawCard);
        //panel.add(stay);

        label = new JLabel();
        address = initial.getURL();
        Image image = getImage(new URL(address));
        label.setIcon(new ImageIcon(image));
        panel.add(label);
        panel.add(stay);
        frame.pack();


    }

    public Image getImage(URL url)  {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("User-Agent", "");
            Image image = ImageIO.read(httpURLConnection.getInputStream());
            return image;
        } catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    //ACTION LISTENER
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == drawCard){
            try {
                Card card = getCard();
                cardCount++;
                if (cardCount > 4 && cardCount <= 8){
                    frame.setPreferredSize(new Dimension(frame.getWidth() + 250, 530));
                    drawCard.setPreferredSize(new Dimension(drawCard.getWidth() + 250, 50));
                    stay.setPreferredSize(new Dimension(stay.getWidth() + 250, 50));
                    frame.setLocation(frame.getX() - 100, frame.getY());
                }
                JLabel label = new JLabel();
                Image image = getImage(new URL(card.getURL()));
                label.setIcon(new ImageIcon(image));
                panel.remove(stay);
                panel.add(label);
                panel.add(stay);
                frame.pack();
                valueNum += card.getValue();
                if (valueNum > 21){
                    display.setText("Score: " + valueNum + " YOU LOST!");
                    dealer.setText("Dealer wins!");
                } else {
                    display.setText("Score: " + valueNum);
                }
                System.out.println("Total value: " + valueNum);
                System.out.println("Total Cards: " + cardCount);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if(e.getSource() == stay){
            drawCard.removeActionListener(this);
            Random rand = new Random();
            int dealerScore = rand.nextInt(25);
            if (dealerScore > 21){
                dealer.setText("Dealer score: " + dealerScore + " you win!");
            } else if (dealerScore < valueNum){
                dealer.setText("Dealer score: " + dealerScore + " you win!");
            } else { dealer.setText("Dealer score: " + dealerScore + " Dealer wins!"); }
            stay.removeActionListener(this);
        }
    }
}
