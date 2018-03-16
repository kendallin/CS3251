import java.net.*;  // for DatagramSocket, DatagramPacket, and InetAddress
import java.io.*;   // for IOException
import java.util.*;

public class Neighbors {

  private InetAddress address;
  private int port;
  private double rtt;

  public Neighbors(InetAddress address, int port, double rtt) {
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

  public double getRTT() {
    return rtt;
  }

  public void setAddress(InetAddress newAddr) {
    address = newAddr;
  }

  public void setPort(int newPort) {
    port = newPort;
  }

  public void setRTT(double newRTT) {
    rtt = newRTT;
  }

}
