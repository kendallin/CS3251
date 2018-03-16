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
  ArrayList<Integer> rttList = new ArrayList<>();
  Neighbors[] neighborList = new Neighbors[4];



  //RINGO INSTANTIATION
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

    //VERY BASIC BATH FOR STARTUP OF RINGOS
    if (t == 0) {
      si.packetSender(true, pocName, pocPort, ri);
      li.listen(pocName, pocPort, t);
      System.out.println("Done Waiting");
    } else {
      li.listen(pocName, pocPort, 1);
      System.out.println("Done Waiting");
    }

  }

  //FUNCTION USED BY LISTENER/SENDERS DURING STARTUP THAT ADD UNSEEN NODES TO NEIGHBORS LIST
  public void addList(Integer p, InetAddress a, int r) {
    boolean pb = ports.contains(p);
    boolean pa = addresses.contains(a);
    if (!ports.contains(p) || !addresses.contains(a)) {
      ports.add(p);
      addresses.add(a);
      rttList.add(r);
    }
    if (ports.size() == totalRingos - 1) {
      byte[] may = generateRTTBytes();
    }
  }

  //WILL BE USED JUST NOT LIKE THIS
  public Neighbors[] generateNeighbors() {
    Neighbors[] nlist = new Neighbors[totalRingos - 1];
    for (int i = 0; i < ports.size(); i++) {
      nlist[i] = new Neighbors(addresses.get(i), ports.get(i), rttList.get(i));
    }
    return nlist;
  }

  //ITERATES THROUGH RTT LOCAL THINGS AND PACKS THEM INTO A BITE ARRAY, 7 FOR EACH ITEM
  public byte[] generateRTTBytes() {
    byte[] b = new byte[ports.size() * 7];
    int count = 0;
    for (int i = 0; i < ports.size(); i++) {
      b[count++] = (byte)(ports.get(i) >> 7);
      int tryMe = ports.get(i);
      byte mayBe = (byte) tryMe;
      b[count++] = mayBe;
      byte[] bmx = addressMaker(addresses.get(i));
      for (byte bx: bmx) {
        b[count++] = bx;
      }
      int j = rttList.get(i);
      byte bit = ((Integer) j).byteValue();
      b[count++] = bit;
      // System.out.println(Byte.toUnsignedInt(rttList.get(i).byteValue()));
    }
    return b;

  }

  //makes the data headers for rtt vectors, needs to pass in the data array generated fromgenerateRTTBytes
  //[1x type and ack][1x end?][4x local address][4x target address][1x marker for number of elements][??x #elements * 7][1x checksum] = 12 + 7 * # elements
  public byte[] dataHeader(byte t, int ack, int end, InetAddress rec, byte[] data) {
    //create list used for pushing byte arrays on
    byte[] alist = new byte[12 + data.length];

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

    byte[] bmx = addressMaker(localHost);
    int i = 2;
    for (byte bx: bmx) {
      alist[i++] = bx;
    }

    byte[] byx = addressMaker(rec);
    for (byte by: bmx) {
      alist[i++] = by;
    }

    alist[i++] = (byte)ports.size();
    for (byte b : data) {
      alist[i++] = b;
    }

    alist[i++] = checksum(alist);


    return alist;
  }

  //[1x type and startup][4x localIP][2x local port info used in rttlist stuff][4x receiving ip] = 11
  public byte[] keepAlive(int startup, InetAddress rec) {
    byte[] alist = new byte[11];
    byte t = 0x02;
    byte mta = (byte)(t << 6);
    if (startup == 1) {
      mta = (byte) (mta ^ (0x01 << 0x05));
    }
    alist[0] = mta;
    byte[] bmx = addressMaker(localHost);
    int i = 1;
    for (byte bx: bmx) {
      alist[i++] = bx;
    }
    byte high = (byte)(port >> 7);
    byte low = (byte) port;
    alist[i++] = high;
    alist[i++] = low;
    byte[] brx = addressMaker(rec);
    for (byte br : brx) {
      alist[i++] = br;
    }
    return alist;
  }

  //makes a 4 byte array address
  private byte[] addressMaker(InetAddress add) {
    byte[] a = add.getAddress();
    return a;
  }

  //don't worry for now
  private byte checksum(byte[] alist) {
     byte cS = 0x00;
     for (byte b: alist) {
       cS ^= b;
     }
     return cS;
  }
}
