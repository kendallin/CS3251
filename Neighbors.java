import java.net.*;  // for DatagramSocket, DatagramPacket, and InetAddress
import java.io.*;   // for IOException
import java.util.*;

public class Neighbors {

  private InetAddress address;
  private int port;
  private int rtt;

  public Neighbors(InetAddress address, int port, int rtt) {
    this.address = address;
    this.port = port;
    this.rtt = rtt;
  }

  public InetAddress getAddress() {
    return address;
  }

  public int getPort() {
    return port;
  }

  public int getRTT() {
    return rtt;
  }

  public void setAddress(InetAddress newAddr) {
    address = newAddr;
  }

  public void setPort(int newPort) {
    port = newPort;
  }

  public void setRTT(int newRTT) {
    rtt = newRTT;
  }

}
