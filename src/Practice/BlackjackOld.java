package Practice;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/*OnlineCardGame's function is to provide a simple and easy way to 'call' our client handler and set up our site.
First, a constructor is created to call all of our code, which is surronded by a try catch because of the potential errors of connecting to the server.
In our try catch is our main code. We create our server using a server socket, while 'while true' basically ensures the code runs indefinetly.
The client accepts the server, and then a thread is created and started for our Card Game(Blackjack). The reason for the if statement is in case the server is null(not created or otherwise).
 */

public class BlackjackOld {
	public BlackjackOld() {
		try {
			//ServerSocket --> Socket where the server is listening for connection
			//Socket --> connection between the server socket and x computer(being a client) Socket will only have a value if the Server Socket has recieved a connection from the client.
			ServerSocket server = new ServerSocket(8080);
			//While true is true... continously running. This will wait forever until it accepts a connection from the 'socket'
			while (true) {
				Socket client = server.accept();
				//Why we do localhost:8080. It will wait until that is connected(typed into browser) and then it will connect.
				//Surronded by if statement because we only want this to run if client is set to a value 'not null'.
				if(client != null){
				//Thread thread = new Thread(new ClientHandler(client));
				//thread.start();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		new BlackjackOld();
		
	}
}
