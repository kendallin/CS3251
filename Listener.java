import java.net.*;  // for DatagramSocket, DatagramPacket, and InetAddress
import java.io.*;   // for IOException
import java.util.*;

public class Listener {

  int port;
  ringo ri;
  Sender si;

  public Listener(int port, ringo ri, Sender si) {
    this.port = port;
    this.ri = ri;
    this.si = si;
  }


  public void listen(InetAddress pocName, int pocPort, int t) throws IOException {
    System.out.println("Listening for sockets");
    int count = 0;
    while (true) {  // Run forever, receiving and echoing datagrams
      //create socket and packets for the desired spots and create a large empty byte array to read into
      DatagramSocket socket = new DatagramSocket(port);
      DatagramPacket packet = new DatagramPacket(new byte[255], 255);

      // Receive packet from client
      socket.receive(packet);

      int rPort = packet.getPort();
      InetAddress rAddress = packet.getAddress();
      ri.addList(rPort, rAddress, (double)-1);

      //get the packet into a string
      byte[] bytes = packet.getData();
      byte[] output = ri.keepAlive(1, pocName);
      //for (byte y : bytes) {
        //System.out.println(Byte.toUnsignedInt(y));
      //}

      int tell = Byte.toUnsignedInt(bytes[0]);
      if (tell >= 192) {
      } else if (tell >= 128) {
      } else if (tell >= 64) {
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
