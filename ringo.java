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
  ArrayList<Integer> ports = new ArrayList<>();
  ArrayList<InetAddress> addresses = new ArrayList<>();
  ArrayList<Double> rttList = new ArrayList<>();
  Neighbors[] neighborList = new Neighbors[4];



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
      ri.packetSender(localPort, pocName, pocPort, ri);
      ri.listen(localPort, pocName, pocPort, ri, t);
    } else {
      ri.listen(localPort, pocName, pocPort, ri, t);
    }
    //byte sf = (byte) 0x01;

  }

  private void packetSender(int localPort, InetAddress pocName, int pocPort, ringo ri) throws IOException {
    DatagramSocket socket = new DatagramSocket();
    socket.setSoTimeout(TIMEOUT);  // Maximum receive blocking time (milliseconds)

    // get bytes parsed and then create send and receive packets
    byte[] bytesToSend = ri.keepAlive(1, pocName);
    // for (byte x : bytesToSend) {
    //   System.out.println(Byte.toUnsignedInt(x));
    // }


    DatagramPacket sendPacket = new DatagramPacket(bytesToSend, bytesToSend.length, pocName, pocPort);
    DatagramPacket receivePacket = new DatagramPacket(new byte[bytesToSend.length], bytesToSend.length);

    int tries = 0;      // Packets may be lost, so we have to keep trying
    boolean receivedResponse = false; //check to see if a response has been received
    long elapsed = 0;
    double rtt = 0;
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
        rtt = (double)elapsed / 1000000.0;

      } catch (InterruptedIOException e) {  // We did not get anything so start to increment the tries counter
        tries += 1;
        System.out.println("Timed out, " + (MAXTRIES-tries) + " more tries...");
      }
    } while ((!receivedResponse) && (tries < MAXTRIES));

    //handle outcomes of response
    if (receivedResponse) {
      String response = new String(receivePacket.getData());
      ports.add(pocPort);
      addresses.add(pocName);
      rttList.add(rtt);
      Neighbors neighbor = new Neighbors(pocName, pocPort, rtt);
      neighborList[0] = neighbor;
      //System.out.println("neighbor port: " + neighborList[0].getPort());
    } else {
      System.out.println("No response -- giving up.");
    }


    // for (int k = 0; k < ports.size(); k++) {
    //   System.out.println("port: " + ports.get(k));
    //   System.out.println("address: " + addresses.get(k));
    //   System.out.println("rtt: " + rttList.get(k));
    //
    // }

    // close socket
    socket.close();
  }

  private void listen(int localPort, InetAddress pocName, int pocPort, ringo ri, int t) throws IOException {
    int count = 0;
    while (true) {  // Run forever, receiving and echoing datagrams
      //create socket and packets for the desired spots and create a large empty byte array to read into
      DatagramSocket socket = new DatagramSocket(localPort);
      DatagramPacket packet = new DatagramPacket(new byte[255], 255);

      // Receive packet from client
      socket.receive(packet);

      int rPort = packet.getPort();
      System.out.println("Packet getPort = " + packet.getPort());
      System.out.println("Socket getPort = " + socket.getPort());
      System.out.println("Socket getLocalPort = " + socket.getLocalPort());
      InetAddress rAddress = packet.getAddress();
      boolean pExists = false;
      boolean aExists = false;
      if (ports.contains(rPort)) {
        pExists = true;
      }
      if (addresses.contains(rAddress)) {
        aExists = true;
      }
      System.out.println(pExists);
      System.out.println(aExists);
      if (pExists == false && aExists == false) {
        ports.add(rPort);
        System.out.println("new port: " + rPort);
        addresses.add(rAddress);
        System.out.println("new address: " + rAddress);
        rttList.add((double)(-1));
        System.out.println(-1);
      }

      //get the packet into a string
      byte[] bytes = packet.getData();
      byte[] output = ri.keepAlive(1, pocName);
      //for (byte y : bytes) {
        //System.out.println(Byte.toUnsignedInt(y));
      //}

      int tell = Byte.toUnsignedInt(bytes[0]);
      // System.out.println("HEADER TYPE: ");
      // if (tell >= 192) {
      //   System.out.println("ACK");
      // } else if (tell >= 128) {
      //   System.out.println("KEEP ALIVE");
      // } else if (tell >= 64) {
      //   System.out.println("RTT");
      // } else if (tell >= 0) {
      //   System.out.println("DATA");
      // }

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
        ri.packetSender(localPort, pocName, pocPort, ri);
        count++;
      }
      socket.close();
    }
  }


// ask for command input ie file transfer, offline, disconnect, etc
// read in "if input == "
// do the task requested
// milestone 1: show ring, show matrix

// create functions for ring and matrix just print arrays ri.showRing & ri.showMatrix

// Socket echoSocket = new Socket(hostName, portNumber);
// ObjectOutputStream out = new ObjectOutputStream(echoSocket.getOutputStream());
// out.writeObject(array of ports/addresses/etc);
// read in with inputstream put into own array or just stored as ring formation or rttmatrix


  private byte[] dataHeader(byte t, int ack, int end, InetAddress rec, String data) {
    //create list used for pushing byte arrays on
    byte[] alist = new byte[15];

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

    alist[10] = (byte)0x00;
    alist[11] = (byte)0x00;
    alist[12] = (byte)0x00;
    alist[13] = (byte)0x00;

    alist[14] = checksum(alist);


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

    byte[] bmx = addressMaker(localHost, alist);
    int i = 1;
    for (byte bx: bmx) {
      alist[i++] = bx;
    }

    byte[] brx = addressMaker(rec, alist);
    for (byte br : brx) {
      alist[i++] = br;
    }
    return alist;
  }

  private byte[] addressMaker(InetAddress add, byte[] alist) {
    byte[] a = add.getAddress();
    return a;
  }

  private byte checksum(byte[] alist) {
     byte cS = 0x00;
     for (byte b: alist) {
       cS ^= b;
     }
     return cS;
  }
}
