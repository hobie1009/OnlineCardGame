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
    JFrame frame = new JFrame("BlackJack");
    JPanel panel;
    JButton drawCard;
    JButton stay;
    JButton playAgain;
    JLabel label;
    JLabel display;
    JLabel dealer;

    int cardCount = 1;
    int valueNum = 0;
    String address;
    public static Card initial;


    public static void main(String [] args) {
        APIPractice apiPractice = new APIPractice();
        try {
            apiPractice.startGame();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        //Init everything
        //frame = new JFrame("BlackJack");
        panel = new JPanel();
        drawCard = new JButton();
        stay = new JButton();
        playAgain = new JButton();
        label  = new JLabel();
        display = new JLabel();
        dealer = new JLabel();


        //Frame
        //frame = new JFrame("BlackJack");
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
        playAgain.setPreferredSize(new Dimension(frame.getWidth(), 50));
        playAgain.setText("Play Again");
        playAgain.addActionListener(this);

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
    public void checkAce(Card card){
        if (card.getValueAsString().contains("A")){
           if(valueNum + 11 <= 21){
               card.setValue(11);
           } else {
               card.setValue(1);
           }
        }
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
    public void startGame() throws IOException {
        deckID = newDeck();
        initial = getCard();
        checkAce(initial);
        System.out.println("Deck ID: " + deckID);
        System.out.println("Card URL: " + initial.getURL());
        initGUI();
    }
    public void resetGame() throws IOException {
        frame.remove(panel);
        cardCount=0;
        valueNum = 0;
        startGame();
    }

    //ACTION LISTENER
    //ADD play again, dealer cards, and ace value changes
    @Override
    public void actionPerformed(ActionEvent e) {
        //Draw Card
        if(e.getSource() == drawCard){
            try {
                Card card = getCard();
                checkAce(card);
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
                    stay.removeActionListener(this);
                    drawCard.removeActionListener(this);
                    playAgain.setPreferredSize(new Dimension(frame.getWidth(), 50));
                    panel.add(playAgain);
                    frame.setPreferredSize(new Dimension(frame.getWidth(), frame.getHeight()+50));
                    frame.pack();
                } else {
                    display.setText("Score: " + valueNum);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        //Stay (the hand)
        if(e.getSource() == stay){
            drawCard.removeActionListener(this);
            stay.removeActionListener(this);
            int dealerTarget = new Random().nextInt(7) + 14;

            /*Random rand = new Random();
            int dealerScore = rand.nextInt(25) + 1;
            if (dealerScore > 21){
                dealer.setText("Dealer score: " + dealerScore + " you win!");
            } else if (dealerScore < valueNum){
                dealer.setText("Dealer score: " + dealerScore + " you win!");
            } else { dealer.setText("Dealer score: " + dealerScore + " Dealer wins!"); }
            stay.removeActionListener(this);
            playAgain.setPreferredSize(new Dimension(frame.getWidth(), 50));
            panel.add(playAgain);
            frame.setPreferredSize(new Dimension(frame.getWidth(), frame.getHeight()+50));
            frame.pack();*/
        }
        //Play a new game of BlackJack
        if(e.getSource() == playAgain){
            try {
                resetGame();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
