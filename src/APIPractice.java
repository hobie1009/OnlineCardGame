package Practice;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class APIPractice {

    //Either make newDeck static or create APIPractice Object
    public static void main(String [] args) throws IOException {
        APIPractice apiPractice = new APIPractice();
        apiPractice.newDeck();
    }

    public void newDeck() throws IOException {
        URL address = new URL("https://www.deckofcardsapi.com/api/deck/new/shuffle/?deck_count=1");
        HttpsURLConnection connection = (HttpsURLConnection) address.openConnection();
        InputStream siteData = connection.getInputStream();

        JsonReader jsonReader = Json.createReader(siteData);
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();
        siteData.close();

        System.out.println();
    }
}
