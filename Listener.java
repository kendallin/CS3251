import java.net.*;  // for DatagramSocket, DatagramPacket, and InetAddress
import java.io.*;   // for IOException
import java.util.*;

public class Listener {

  int port;
  ringo ri;
  sender si;

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
      if (t == 0) {
        socket.setSoTimeout(4000);
      } else if (t == 1) {
        socket.setSoTimeout(6000);
      }
      DatagramPacket packet = new DatagramPacket(new byte[255], 255);
      try {
        // Receive packet from client
        socket.receive(packet);
      } catch (java.net.SocketTimeoutException e) {
        System.out.println(e);
        break;
      }


      //get the packet into a string
      byte[] bytes = packet.getData();
      byte[] output = ri.keepAlive(1, pocName);


      int rPort = (bytes[5]<< 7) + bytes[6];
      InetAddress rAddress = packet.getAddress();
      ri.addList(rPort, rAddress, -1);

      int tell = Byte.toUnsignedInt(bytes[0]);
      boolean time = false;
      if (tell >= 192) {
      } else if (tell >= 128) {
      } else if (tell >= 64) {
        time = true;
      } else if (tell >= 0) {
      }

      //check to make sure the string is not empty
      if (!output.equals("")) {

        //create a new datagrampacket to send and send it
        DatagramPacket sendPacket = new DatagramPacket(bytes, output.length, packet.getAddress(), packet.getPort());
        socket.send(sendPacket);
        packet.setLength(output.length);
      }
      if (t != 0) {
        if (count < 3) {
          // start rtt matrix process
        }
        si.packetSender(true, pocName, pocPort, ri);
        count++;
      }
      socket.close();
    }
  }
}
