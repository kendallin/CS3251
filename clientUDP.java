import java.net.*;  // for DatagramSocket, DatagramPacket, and InetAddress
import java.io.*;   // for IOException
import java.util.*;

public class clientUDP {

  private static final int TIMEOUT = 2000;   // Resend timeout (milliseconds)
  private static final int MAXTRIES = 3;     // Maximum retransmissions

  public static void main(String[] args) throws IOException {

    if ((args.length < 2) || (args.length > 3))  // Test for correct # of args
      throw new IllegalArgumentException("Parameter(s): <Server> [<Port>] <Word>");



    InetAddress serverAddress = InetAddress.getByName(args[0]);  // Server address
    String bytes = (args.length == 3) ? args[2] : args[1]; //collect data
    String serv =  (args.length == 3) ? args[1] : "50900"; //set port

    //light argument validation
    if (bytes.toLowerCase().matches(".*[a-z].*")) {
        throw new IOException("Please only use numbers and operators in the expression.");
    }
    if (serv.toLowerCase().matches(".*[a-z].*")) {
        throw new IOException("Please only use numbers and operators in the expression.");
    }

    int servPort =  (args.length == 3) ? Integer.parseInt(args[1]) : 50900;

    //iterate throught the string in a char array and add them to a list by operational order
    char[] tokens = bytes.toCharArray();
    ArrayList<String> list = new ArrayList<String>();
    list.add("");
    for (char c: tokens) {
        //add the char to the proceeding character in a string
        String cha = "" + c;
        String str = list.get(list.size() - 1);
        str = str + cha;

        //reset the value
        list.set(list.size() - 1, str);

        //if this is the end of an operation start a new list element for the next one
        if (!cha.matches("^[0-9]+$") && !cha.equals(" ")) {
            list.add("");
        }
    }
    //get rid of the leftover list value
    list.remove(list.size() - 1);

    //start a variable to keep track of the server responses
    String response = "";


    for (int j = 0; j < list.size(); j++) {
      DatagramSocket socket = new DatagramSocket();
      socket.setSoTimeout(TIMEOUT);  // Maximum receive blocking time (milliseconds)

      // check to see if its the first case and if not add the old response to it
      String outty = "";
      if (j > 0) {
          outty = response + list.get(j);
      } else {
          outty = list.get(j);
      }

      // get bytes parsed and then create send and receive packets
      byte[] bytesToSend = outty.getBytes();
      DatagramPacket sendPacket = new DatagramPacket(bytesToSend, bytesToSend.length, serverAddress, servPort);
      DatagramPacket receivePacket = new DatagramPacket(new byte[bytesToSend.length], bytesToSend.length);

      int tries = 0;      // Packets may be lost, so we have to keep trying
      boolean receivedResponse = false; //check to see if a response has been received
      do {
        socket.send(sendPacket);          // Send the echo string
        try {
          socket.receive(receivePacket);  // Attempt echo reply reception

          if (!receivePacket.getAddress().equals(serverAddress))  // Check source
            throw new IOException("Received packet from an unknown source");

          receivedResponse = true;
        } catch (InterruptedIOException e) {  // We did not get anything so start to increment the tries counter
          tries += 1;
          System.out.println("Timed out, " + (MAXTRIES-tries) + " more tries...");
        }
      } while ((!receivedResponse) && (tries < MAXTRIES));

      //handle outcomes of response
      if (receivedResponse) {
        response = new String(receivePacket.getData());
      } else {
        System.out.println("No response -- giving up.");
      }

      // close socket
      socket.close();
    }
    if (response != null) {
      System.out.println(response);
    } else {
      System.out.println("Please enter valid arguments");
    }




  }
}
