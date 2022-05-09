package Blackjack;

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
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class Blackjack implements ActionListener {
    public static String PlayerdeckID = "";
    public static String DealerdeckID = "";
    JFrame playerFrame = new JFrame("Blackjack - Player"); JPanel playerPanel;
    JButton drawCard; JButton stay; JButton playAgain;
    JLabel playerCard; JLabel playerScore; JLabel dealerScore;

    JFrame dealerFrame = new JFrame("Blackjack - dealerScore"); JPanel dealerPanel;
    JLabel dealerCard;

    int cardCount = 1;
    int playerTotal = 0;
    int dealerTotal = 0;
    String address;
    public static Card initial;


    public static void main(String [] args) {
        Blackjack apiPractice = new Blackjack();
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
    public Card getCard(String deckID) throws IOException {
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
        playerPanel = new JPanel();
        drawCard = new JButton();
        stay = new JButton();
        playAgain = new JButton();
        playerCard  = new JLabel();
        playerScore = new JLabel();
        dealerScore = new JLabel();

       //Frame
       playerFrame.setPreferredSize(new Dimension(1000, 530));
       playerFrame.setLocation(300, 220);
       playerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       playerFrame.setVisible(true);
       playerFrame.add(playerPanel);

        //Buttons
        drawCard.setPreferredSize(new Dimension(980, 50));
        drawCard.setText("Draw Card");
        drawCard.addActionListener(this);
        stay.setPreferredSize(new Dimension(980, 50));
        stay.setText("Stay");
        stay.setLocation(0, 400);
        stay.addActionListener(this);
        playerTotal = initial.getValue();
        playAgain.setPreferredSize(new Dimension(playerFrame.getWidth(), 50));
        playAgain.setText("Play Again");
        playAgain.addActionListener(this);

        //Label
        playerScore.setPreferredSize(new Dimension(200, 50));

        dealerScore.setPreferredSize(new Dimension(200, 50));
        dealerScore.setText("Dealer Score: ");

        playerPanel.add(playerScore);
        playerPanel.add(dealerScore);
        playerPanel.add(drawCard);
        //panel.add(stay);

        playerCard = new JLabel();
        address = initial.getURL();
        Image image = getImage(new URL(address));
        playerCard.setIcon(new ImageIcon(image));
        playerPanel.add(playerCard);
        playerPanel.add(stay);

        Card secondCard = getCard(PlayerdeckID);
        checkAce(secondCard, playerTotal);
        addCard(secondCard);
        playerScore.setText("Score: " + playerTotal);
       playerFrame.pack();


    }
    public void initDealer() throws IOException {
        dealerPanel = new JPanel();

        dealerFrame.setPreferredSize(new Dimension(500, 700));
        dealerFrame.setLocation(1400, 220);
        dealerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dealerFrame.setVisible(true);
        dealerFrame.add(dealerPanel);

        Card card = getCard(DealerdeckID);
        checkAce(card, dealerTotal);

        dealerCard = new JLabel();
        address = card.getURL();
        Image image = getImage(new URL(address));
        dealerCard.setIcon(new ImageIcon(image));
        dealerPanel.add(dealerCard);
        dealerTotal += card.getValue();
        dealerFrame.pack();
    }
    public void checkAce(Card card, int total){
        if (card.getValueAsString().contains("A")){
           if(total + 11 <= 21){
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
        PlayerdeckID = newDeck();
        DealerdeckID = newDeck();
        initial = getCard(PlayerdeckID);
        checkAce(initial, playerTotal);
        System.out.println("Deck ID: " + PlayerdeckID);
        System.out.println("Card URL: " + initial.getURL());
        initDealer();
        initGUI();

    }
    public void resetGame() throws IOException {
       playerFrame.remove(playerPanel);
       dealerFrame.remove(dealerPanel);
        cardCount=0;
        playerTotal = 0;
        dealerTotal = 0;
        startGame();
    }
    public void addCard(Card card) throws MalformedURLException {
        JLabel playerCard = new JLabel();
        Image image = getImage(new URL(card.getURL()));
        playerCard.setIcon(new ImageIcon(image));
        playerPanel.remove(stay);
        playerPanel.add(playerCard);
        playerPanel.add(stay);
        playerFrame.pack();
        playerTotal += card.getValue();
    }

    //ACTION LISTENER
    //ADD play again, dealerScore cards, and ace value changes
    @Override
    public void actionPerformed(ActionEvent e) {
        //Draw Card
        if(e.getSource() == drawCard){
            try {
                Card card = getCard(PlayerdeckID);
                checkAce(card, playerTotal);
                cardCount++;
                if (cardCount > 4 && cardCount <= 8){
                   playerFrame.setPreferredSize(new Dimension(playerFrame.getWidth() + 250, 530));
                    drawCard.setPreferredSize(new Dimension(drawCard.getWidth() + 250, 50));
                    stay.setPreferredSize(new Dimension(stay.getWidth() + 250, 50));
                   playerFrame.setLocation(playerFrame.getX() - 100,playerFrame.getY());
                }
                addCard(card);
                if (playerTotal > 21){
                    playerScore.setText("Score: " + playerTotal + " YOU LOST!");
                    dealerScore.setText("Dealer wins!");
                    stay.removeActionListener(this);
                    drawCard.removeActionListener(this);
                    playAgain.setPreferredSize(new Dimension(playerFrame.getWidth(), 50));
                    playerPanel.add(playAgain);
                   playerFrame.setPreferredSize(new Dimension(playerFrame.getWidth(),playerFrame.getHeight()+50));
                   playerFrame.pack();
                } else {
                    playerScore.setText("Score: " + playerTotal);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        //Stay (the hand)
        if(e.getSource() == stay){
            try {
                drawCard.removeActionListener(this);
                stay.removeActionListener(this);

                int dealerScoreTarget = (new Random().nextInt(5) + (16));
                System.out.println("Dealer Target: " + dealerScoreTarget);
                while (dealerTotal <= dealerScoreTarget) {
                    Card card = getCard(DealerdeckID);
                    JLabel dCard = new JLabel();
                    Image image = getImage(new URL(card.getURL()));
                    dCard.setIcon(new ImageIcon(image));
                    dealerPanel.add(dCard);
                    dealerFrame.pack();
                    dealerTotal += card.getValue();
                    System.out.println("Dealer Score: " + dealerTotal);
                }
            } catch (IOException e1){
                e1.printStackTrace();
            }
            if (dealerTotal > 21){
                dealerScore.setText("Dealer score: " + dealerTotal + " you win!");
            } else if (dealerTotal < playerTotal){
                dealerScore.setText("Dealer score: " + dealerTotal + " you win!");
            } else if (dealerTotal == playerTotal){
                dealerScore.setText("Dealer score: " + dealerTotal + " its as tie!");
            }
            else { dealerScore.setText("Dealer score: " + dealerTotal + " dealer wins!"); }
            playAgain.setPreferredSize(new Dimension(playerFrame.getWidth(), 50));
            playerPanel.add(playAgain);
            playerFrame.setPreferredSize(new Dimension(playerFrame.getWidth(),playerFrame.getHeight()+50));
            playerFrame.pack();
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
