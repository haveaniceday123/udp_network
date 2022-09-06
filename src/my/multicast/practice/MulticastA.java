package my.multicast.practice;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;

public class MulticastA {
  static void receiveMessage(MulticastSocket socket) {
    byte[] receiveData;
    DatagramPacket receivedPacket;
    
    receiveData = new byte[65508];
    receivedPacket = new DatagramPacket(receiveData, receiveData.length);
    
    try {
      socket.receive(receivedPacket);
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    System.out.println("remote addreses: " + receivedPacket.getSocketAddress());
    System.out.println("contents : " + new String(receivedPacket.getData()).trim());
  }
  
  public static void main(String[] args) {
    System.out.println("<<CLIENT A>> - TEXT");
  
    InetSocketAddress multicastAddress = null;
  
    multicastAddress = new InetSocketAddress("234.234.234.235", 10000);
  
    int multicastPort = 10000;
  
    MulticastSocket socket = null;
    
    try {
      socket = new MulticastSocket(multicastPort);
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    
    try {
      socket.joinGroup(multicastAddress, NetworkInterface.getByName("lo"));
    } catch (SocketException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    
    byte[] sendData = "안녕하세요(TEST1)".getBytes();
    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, multicastAddress);
    
    try {
      socket.send(sendPacket);
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    receiveMessage(socket);
    receiveMessage(socket);
    
    try {
      socket.leaveGroup(multicastAddress, NetworkInterface.getByName("lo"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    socket.close();
  }
}
