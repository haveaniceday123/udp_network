package my.multicast.practice;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

public class MulticastSocketObject {
  public static void main(String[] args) {
  
    MulticastSocket mcs1 = null;
    MulticastSocket mcs2 = null;
    MulticastSocket mcs3 = null;
  
    Enumeration<NetworkInterface> networks = null;
    
    try {
      networks = NetworkInterface.getNetworkInterfaces();
      
      while(networks.hasMoreElements()) {
        NetworkInterface ni = (NetworkInterface) networks.nextElement();
        System.out.println(ni.getName() + " " + ni.getDisplayName());
        
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    try {
      mcs1 = new MulticastSocket();
      mcs2 = new MulticastSocket(10000);
      mcs3 = new MulticastSocket(
        new InetSocketAddress(InetAddress.getByName("0.0.0.0"), 10000)
      );
      
      System.out.println(new InetSocketAddress(InetAddress.getByName("localhost"), 10000) + ">>>>>>>>>>>>>>>>>>>>>>>");
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    System.out.println(mcs1.getLocalSocketAddress());
    System.out.println(mcs2.getLocalSocketAddress());
    System.out.println(mcs3.getLocalSocketAddress());
    System.out.println();
    
    try {
      System.out.println("TimeToLive: " + mcs1.getTimeToLive());
      mcs1.setTimeToLive(50);
      System.out.println("TimeToLive: " + mcs1.getTimeToLive());
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    System.out.println();
    
    
    try {
      
      System.out.println(NetworkInterface.getByName("lo"));
  
      mcs1.joinGroup(new InetSocketAddress("234.234.234.234", 10000), NetworkInterface.getByName("lo"));
      mcs2.joinGroup(new InetSocketAddress("234.234.234.234", 10000), NetworkInterface.getByName("234.234.234.234"));
      mcs3.joinGroup(new InetSocketAddress("234.234.234.234", 10000), NetworkInterface.getByName("234.234.234.234"));
      
      byte[] sendData = "안녕하세요".getBytes();
      
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("234.234.234.234"), 10000);
      mcs1.send(sendPacket);
      
      byte[] receivedData;
      DatagramPacket receivedPacket;
      
      receivedData = new byte[65508];
      receivedPacket = new DatagramPacket(receivedData, receivedData.length);
      mcs2.receive(receivedPacket);
      
      System.out.println("mcs2 received Data:  " + new String(receivedPacket.getData()).trim());
      
      System.out.println(" remote address : " + receivedPacket.getSocketAddress());
      
      receivedData = new byte[65508];
      
      receivedPacket = new DatagramPacket(receivedData, receivedData.length);
      
      mcs3.receive(receivedPacket);
      System.out.println("mcs3가 수신한 데이터: " + new String(receivedPacket.getData()).trim());
  
      System.out.println(" remote address : " + receivedPacket.getSocketAddress());
      
    }  catch (UnknownHostException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
