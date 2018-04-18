import java.net.*;  // for DatagramSocket, DatagramPacket, and InetAddress
import java.io.*;   // for IOException
import java.util.*;

public class sender {

  private static final int TIMEOUT = 2000;   // Resend timeout (milliseconds)
  private static final int MAXTRIES = 5;     // Maximum retransmissions

  ringo ri;
  Listener li;

  public sender(ringo ri) {
    this.ri = ri;
  }

  public void setListener(Listener li) {
    this.li = li;
  }

  public void packetSender(int search, InetAddress pocName, int pocPort, ringo ri) throws IOException {
    System.out.println("Sending Packet to " + pocName + " on " + pocPort);
    DatagramSocket socket = new DatagramSocket();
    socket.setSoTimeout(TIMEOUT);  // Maximum receive blocking time (milliseconds)

    // get bytes parsed and then create send and receive packets
    byte[] bytesToSend;
    if (search == 0) {
      bytesToSend = ri.keepAlive(1, pocName);
    } else if (search == 1) {
      byte[] data = ri.generateRTTBytes();
      bytesToSend = ri.dataHeader((byte) 0x00, 1, 1, pocName, data);
    } else {
      System.out.println("Got some data");
      bytesToSend = ri.dataHeader((byte) 0x01, 1, 1, pocName, ri.getBitty());

    }

    DatagramPacket sendPacket = new DatagramPacket(bytesToSend, bytesToSend.length, pocName, pocPort);
    DatagramPacket receivePacket = new DatagramPacket(new byte[bytesToSend.length], bytesToSend.length);

    int tries = 0;      // Packets may be lost, so we have to keep trying
    boolean receivedResponse = false; //check to see if a response has been received
    long elapsed = 0;
    int rtt = 0;
    do {
      socket.send(sendPacket);          // Send the echo string

      long lStartTime = System.nanoTime();
      try {
        socket.receive(receivePacket);  // Attempt echo reply reception

        if (!receivePacket.getAddress().equals(pocName))  // Check source
          throw new IOException("Received packet from an unknown source");

        receivedResponse = true;
        long lEndTime = System.nanoTime();
        elapsed = lEndTime - lStartTime; // cant find RTT outside of do
        rtt = (int) (elapsed / 100000);

      } catch (InterruptedIOException e) {  // We did not get anything so start to increment the tries counter
        tries += 1;
        System.out.println("Timed out, " + (MAXTRIES-tries) + " more tries...");
      }
    } while ((!receivedResponse) && (tries < MAXTRIES));

    //handle outcomes of response
    if (receivedResponse) {
      String response = new String(receivePacket.getData());
      if (search == 0) {
        ri.addList(pocPort, pocName, rtt);
      }
    } else {
      System.out.println("No response -- giving up.");
    }
    // close socket
    socket.close();
  }
}
