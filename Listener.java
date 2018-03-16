import java.net.*;  // for DatagramSocket, DatagramPacket, and InetAddress
import java.io.*;   // for IOException
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Listener {

  int port;
  ringo ri;
  sender si;
  int increment = 0;

  public Listener(int port, ringo ri, sender si) {
    this.port = port;
    this.ri = ri;
    this.si = si;
  }

  //current issue is how to do startup, nodes need to spend time waiting to be pinged, but one may also never get pinged so therefore WILL
  //have to be able to activate itself to ping its poc
  //look at setSoTimeout


  public void listen(InetAddress pocName, int pocPort, int t) throws IOException {
    System.out.println("Listening for sockets");
    int count = 0;
    while (true) {  // Run forever, receiving and echoing datagrams
      //create socket and packets for the desired spots and create a large empty byte array to read into
      DatagramSocket socket = new DatagramSocket(port);
      boolean beMe = false;
      if (t == 0) {
        socket.setSoTimeout(12000);
      } else if (t == 1) {
        socket.setSoTimeout(10000);
      } else if (t ==3) {
        beMe = true;
      } else if (t ==4) {
        socket.setSoTimeout(1000);
      }
      DatagramPacket packet = new DatagramPacket(new byte[255], 255);
      try {
        // Receive packet from client
        socket.receive(packet);
      } catch (java.net.SocketTimeoutException e) {
        socket.close();
        break;
      }

      byte[] bytes = packet.getData();
      byte[] output = ri.keepAlive(1, pocName);

      int tell = Byte.toUnsignedInt(bytes[0]);
      boolean time = false;
      boolean ka = false;
      if (tell >= 192) {
      } else if (tell >= 128) {
        ka = true;
      } else if (tell >= 64) {
        time = true;
      } else if (tell >= 0) {
        time = true;
      }

      int rPort = 0;
      if (ka) {
        rPort = ((bytes[5] & 0xff) << 7) | (bytes[6] & 0xFF);
        System.out.println("Received KA from: " + packet.getAddress() + " on port: " + rPort);
        ri.addList(rPort, packet.getAddress(), -1);
      }
      if (time) {
        byte[] addR = {bytes[2], bytes[3], bytes[4], bytes[5]};
        rPort = ((bytes[6] & 0xff) << 7) | (bytes[7] & 0xFF);
        System.out.println("Received RTT from: " + InetAddress.getByAddress(addR) + " on port: " + rPort);
      }

      //check to make sure the string is not empty
      if (!output.equals("")) {

        //create a new datagrampacket to send and send it
        DatagramPacket sendPacket = new DatagramPacket(bytes, output.length, packet.getAddress(), packet.getPort());
        socket.send(sendPacket);
        packet.setLength(output.length);
      }
      if (t != 0) {
        if (socket.getSoTimeout() != 0) {
          si.packetSender(true, pocName, pocPort, ri);
          count++;
        }
      }
      socket.close();
      if (!ri.isTrialStored(rPort) && !ka) {
        ri.storeTrial(rPort);
        ri.storeNeighbors(bytes);

        if (!beMe) {
          break;
        }
      }
    }
  }
}
