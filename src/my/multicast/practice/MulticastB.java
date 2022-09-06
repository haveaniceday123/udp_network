package my.multicast.practice;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;

public class MulticastB {
  public static void main(String[] args) {
    System.out.println("<<CLIENT B>> - TEXT");
  
    InetSocketAddress multicastAddress = null;
    NetworkInterface nif = null;
    
    try {
      multicastAddress  = new InetSocketAddress("234.234.234.235", 10000);
      nif = NetworkInterface.getByName("lo");
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    int multicastPort = 10000;
  
    MulticastSocket socket = null;
    
    try {
      socket = new MulticastSocket(multicastPort);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    try {
      socket.joinGroup(multicastAddress, nif);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    receiveMessage(socket);
    
    byte[] sendData = "반갑습니다.(CLIENTB)".getBytes();
    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, multicastAddress);
    
    try {
      socket.send(sendPacket);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    receiveMessage(socket);
    
    try {
      socket.leaveGroup(multicastAddress, nif);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    socket.close();
  }
  
  static void receiveMessage(MulticastSocket socket) {
    byte[] receiveData;
    DatagramPacket receivePacket;
    
    receiveData = new byte[65508];
    receivePacket = new DatagramPacket(receiveData, receiveData.length);
    
    try {
      socket.receive(receivePacket);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    System.out.println("remote address: " + receivePacket.getSocketAddress());
    System.out.println("contents: " + new String(receivePacket.getData()).trim());
  }
}
