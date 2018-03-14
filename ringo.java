import java.net.*;  // for DatagramSocket, DatagramPacket, and InetAddress
import java.io.*;   // for IOException
import java.util.*;

public class ringo {

  public static void main(String[] args) throws IOException {

    String type = args[0];
    int localPort = Integer.parseInt(args[1]);
    InetAddress pocName = InetAddress.getByName(args[2]);
    int pocPort = Integer.parseInt(args[3]);
    int n = Integer.parseInt(args[4]);

    switch(type) {
      case "S":
        sender s = new sender();
        break;
      case "R":
        receiver r = new receiver();
        break;
      case "F":
        forwarder f = new forwarder();
        break;
      default:
        System.out.println("Please enter a valid type string- S/R/F");
    }


    while (true) {  // Run forever, receiving and echoing datagrams
      //create socket and packets for the desired spots and create a large empty byte array to read into
      DatagramSocket socket = new DatagramSocket(localPort);
      DatagramPacket packet = new DatagramPacket(new byte[255], 255);

      // Receive packet from client
      socket.receive(packet);

      //get the packet into a string
      byte[] bytes = packet.getData();
      String output = new String(bytes);
      System.out.println(output);

      //check to make sure the string is not empty
      if (!output.equals("")) {

        // //create a new datagrampacket to send and send it
        // DatagramPacket sendPacket = new DatagramPacket(out, out.length, packet.getAddress(), packet.getPort());
        // socket.send(sendPacket);
        // System.out.println("Response: " + yep);
        // packet.setLength(out.length);
      }

      //close the socket after receiving and sending a response
      socket.close();
    }
  }
}
