package OnlineCardGame;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class OnlineCardGame {
	//constructer
	public OnlineCardGame() {
		try {
			ServerSocket server = new ServerSocket(8080);
			System.out.println("Sever Started");
			while (true) {
				Socket client = server.accept();
				Thread thread = new Thread(new ClientHandler(client));
				thread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		new OnlineCardGame();
		
	}
}
