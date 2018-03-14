import java.net.*;  // for DatagramSocket, DatagramPacket, and InetAddress
import java.io.*;   // for IOException
import java.util.*;
import java.lang.Byte;

//HELPFUL FUNCTIONS
// Byte.toUnsignedInt(*byte b*) outputs the unsigned numbers
//

public class ringo {
  private static final int TIMEOUT = 2000;   // Resend timeout (milliseconds)
  private static final int MAXTRIES = 5;     // Maximum retransmissions

  int port;
  int totalRingos;
  InetAddress localHost;


  public ringo(int localPort, int totalRingos) {
    this.port = localPort;
    this.totalRingos = totalRingos;
    try {
      this.localHost = InetAddress.getLocalHost();
    } catch(UnknownHostException e) {
      System.out.println(e);
    }

  }

  public static void main(String[] args) throws IOException {

    String type = args[0];
    int localPort = Integer.parseInt(args[1]);
    InetAddress pocName = InetAddress.getByName(args[2]);
    int pocPort = Integer.parseInt(args[3]);
    int n = Integer.parseInt(args[4]);

    ringo ri = new ringo(localPort, n);

    int t = -1;
    switch(type) {
      case "S":
        sender s = new sender();
        t = 0;
        break;
      case "R":
        receiver r = new receiver();
        t = 1;
        break;
      case "F":
        forwarder f = new forwarder();
        t = 2;
        break;
      default:
        System.out.println("Please enter a valid type string- S/R/F");
    }

    if (t == 0) {
      DatagramSocket socket = new DatagramSocket();
      socket.setSoTimeout(TIMEOUT);  // Maximum receive blocking time (milliseconds)

      // get bytes parsed and then create send and receive packets
      byte[] bytesToSend = ri.keepAlive(1, pocName);

      DatagramPacket sendPacket = new DatagramPacket(bytesToSend, bytesToSend.length, pocName, pocPort);
      DatagramPacket receivePacket = new DatagramPacket(new byte[bytesToSend.length], bytesToSend.length);

      int tries = 0;      // Packets may be lost, so we have to keep trying
      boolean receivedResponse = false; //check to see if a response has been received
      do {
        socket.send(sendPacket);          // Send the echo string
        try {
          socket.receive(receivePacket);  // Attempt echo reply reception

          if (!receivePacket.getAddress().equals(pocName))  // Check source
            throw new IOException("Received packet from an unknown source");

          receivedResponse = true;
        } catch (InterruptedIOException e) {  // We did not get anything so start to increment the tries counter
          tries += 1;
          System.out.println("Timed out, " + (MAXTRIES-tries) + " more tries...");
        }
      } while ((!receivedResponse) && (tries < MAXTRIES));

      //handle outcomes of response
      if (receivedResponse) {
        String response = new String(receivePacket.getData());
      } else {
        System.out.println("No response -- giving up.");
      }

      // close socket
      socket.close();
    } else {
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

          //create a new datagrampacket to send and send it
          DatagramPacket sendPacket = new DatagramPacket(bytes, output.length(), packet.getAddress(), packet.getPort());
          socket.send(sendPacket);
          packet.setLength(output.length());
        }

        //close the socket after receiving and sending a response
        socket.close();
      }
    }

    // int var = 1;
    // var = (var << 4);
    // System.out.println(var);
    // System.out.println(Integer.toBinaryString(var));

    String m = "m";
    byte sf = (byte) 0x01;



    byte[] maybe = ri.dataHeader(sf, 1, 1, pocName, m);
  }

  private byte[] dataHeader(byte t, int ack, int end, InetAddress rec, String data) {
    //create list used for pushing byte arrays on
    byte[] alist = new byte[14];

    byte mta = (byte)(t << 6);
    if (ack == 1) {
      mta = (byte) (mta ^ (0x01 << 0x05));
    }
    alist[0] = mta;

    byte zeroEnd = 0x00;
    if (end == 1) {
      zeroEnd = (byte) (zeroEnd ^ (0x01));
    }
    alist[1] = zeroEnd;

    alist = addressMaker(localHost, alist);
    alist = addressMaker(rec, alist);

    alist[2] = (byte)0x00;
    alist[3] = (byte)0x00;
    alist[4] = (byte)0x00;
    alist[5] = (byte)0x00;

    alist[6] = checksum(alist);


    return alist;
  }

  private byte[] keepAlive(int startup, InetAddress rec) {
    byte[] alist = new byte[9];
    byte t = 0x02;
    byte mta = (byte)(t << 6);
    if (startup == 1) {
      mta = (byte) (mta ^ (0x01 << 0x05));
    }
    alist[0] = mta;

    alist = addressMaker(localHost, alist);
    alist = addressMaker(rec, alist);


    return alist;
  }

  private byte[] addressMaker(InetAddress add, byte[] alist) {
    byte[] a = add.getAddress();
    int count = 0;
    for(byte b: a) {
      byte h = new Byte((byte) (b & 0xFF));
      alist[count] = h;
      count++;
    }
    return alist;
  }

  private byte checksum(byte[] alist) {
     byte cS = 0x00;
     for (byte b: alist) {
       cS ^= b;
     }
     return cS;
  }
}
