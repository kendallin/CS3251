import java.net.*;  // for DatagramSocket, DatagramPacket, and InetAddress
import java.io.*;   // for IOException
import java.util.*;
import java.lang.Byte;

//HELPFUL FUNCTIONS
// Byte.toUnsignedInt(*byte b*) outputs the unsigned numbers
//
private static final int TIMEOUT = 2000;   // Resend timeout (milliseconds)
private static final int MAXTRIES = 5;     // Maximum retransmissions

public class ringo {
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

    int type = -1;
    switch(type) {
      case "S":
        sender s = new sender();
        type = 0;
        break;
      case "R":
        receiver r = new receiver();
        type = 1;
        break;
      case "F":
        forwarder f = new forwarder();
        type = 2;
        break;
      default:
        System.out.println("Please enter a valid type string- S/R/F");
    }

    if (type == 0) {
      DatagramSocket socket = new DatagramSocket();
      socket.setSoTimeout(TIMEOUT);  // Maximum receive blocking time (milliseconds)

      // get bytes parsed and then create send and receive packets
      Byte[] bytesToSend = keepAlive(1, pocName)
      DatagramPacket sendPacket = new DatagramPacket(bytesToSend, bytesToSend.length, pocName, pocPort);
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
    } else {
      while (true) {  // Run forever, receiving and echoing datagrams
        //create socket and packets for the desired spots and create a large empty byte array to read into
        DatagramSocket socket = new DatagramSocket(localPort);
        DatagramPacket packet = new DatagramPacket(new byte[255], 255);

        // Receive packet from client
        socket.receive(packet);

        //get the packet into a string
        Byte[] bytes = packet.getData();
        String output = new String(bytes);
        System.out.println(output);

        //check to make sure the string is not empty
        if (!output.equals("")) {

          //create a new datagrampacket to send and send it
          DatagramPacket sendPacket = new DatagramPacket(out, out.length, packet.getAddress(), packet.getPort());
          socket.send(sendPacket);
          System.out.println("Response: " + yep);
          packet.setLength(out.length);
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

    ringo ri = new ringo(localPort, n);

    Byte[] maybe = ri.dataHeader(sf, 1, 1, pocName, m);
  }

  private Byte[] dataHeader(byte type, int ack, int end, InetAddress rec, String data) {
    //create list used for pushing byte arrays on
    ArrayList<Byte> alist = new ArrayList<>();

    byte mta = (byte)(type << 6);
    if (ack == 1) {
      mta = (byte) (mta ^ (0x01 << 0x05));
    }
    alist.add(new Byte(mta));

    byte zeroEnd = 0x00;
    if (end == 1) {
      zeroEnd = (byte) (zeroEnd ^ (0x01));
    }
    alist.add(new Byte(zeroEnd));

    alist = addressMaker(localHost, alist);
    alist = addressMaker(rec, alist);

    alist.add(new Byte((byte)0x00));
    alist.add(new Byte((byte)0x00));
    alist.add(new Byte((byte)0x00));
    alist.add(new Byte((byte)0x00));

    alist.add(checksum(alist));

    Byte[] ret = alist.toArray(new Byte[alist.size()]);

    return ret;
  }

  private Byte[] keepAlive(int startup, InetAddress rec) {
    ArrayList<Byte> alist = new ArrayList<>();
    byte type = 0x02;
    byte mta = (byte)(type << 6);
    if (startup == 1) {
      mta = (byte) (mta ^ (0x01 << 0x05));
    }
    alist.add(new Byte(mta));

    alist = addressMaker(localHost, alist);
    alist = addressMaker(rec, alist);

    Byte[] ret = alist.toArray(new Byte[alist.size()]);

    return ret;
  }

  private ArrayList<Byte> addressMaker(InetAddress add, ArrayList<Byte> alist) {
    byte[] a = add.getAddress();
    for(byte b: a) {
      Byte h = new Byte((byte) (b & 0xFF));
      alist.add(h);
    }
    return alist;
  }

  private Byte checksum(ArrayList<Byte> alist) {
     byte cS = 0x00;
     for (Byte b: alist) {
       cS ^= b;
     }
     return new Byte(cS);
  }
}
