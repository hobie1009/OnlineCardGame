package Practice;

import java.net.InetAddress;
import java.net.Socket;

public class SocketPractice {
   public static void main(String[] args) {
      try {
         InetAddress addr;
         Socket sock = new Socket("www.javatutorial.com", 80);
         addr = sock.getInetAddress();
         System.out.println("Connected to " + addr);
         sock.close();
      } catch (java.io.IOException e) {
         System.out.println("Can't connect to " + args[0]);
         System.out.println(e);
      }
   }
}