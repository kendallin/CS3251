import java.net.*;  // for DatagramSocket, DatagramPacket, and InetAddress
import java.io.*;   // for IOException
import java.util.*;
import java.lang.Byte;

//HELPFUL FUNCTIONS
// Byte.toUnsignedInt(*byte b*) outputs the unsigned numbers
//

public class ringo {

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
    sender si = new sender(ri);
    Listener li = new Listener(localPort, ri, si);
    si.setListener(li);

    int t = -1;
    switch(type) {
      case "S":
        t = 0;
        break;
      case "R":
        t = 1;
        break;
      case "F":
        t = 2;
        break;
      default:
        System.out.println("Please enter a valid type string- S/R/F");
    }

    if (t == 0) {
      si.packetSender(true, pocName, pocPort, ri);
      li.listen(pocName, pocPort, t);
    } else {
      li.listen(pocName, pocPort, t);
    }
    //byte sf = (byte) 0x01;

  }

  public void addList(Integer p, InetAddress a, Double r) {
    boolean pb = ports.contains(p);
    boolean pa = addresses.contains(a);
    // System.out.println(pb + " : " + pa);
    if (!ports.contains(p) || !addresses.contains(a)) {
      System.out.println("ADDING PORT: " + p + " ON ADDRESS: " + a + " WITH RTT: " + r);
      ports.add(p);
      addresses.add(a);
      rttList.add(r);
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


  public byte[] dataHeader(byte t, int ack, int end, InetAddress rec, String data) {
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

  public byte[] keepAlive(int startup, InetAddress rec) {
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
