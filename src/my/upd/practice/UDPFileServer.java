package my.upd.practice;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class UDPFileServer {
  
  private String fileName = "";
  
  private int port = 0;
  
  private InetSocketAddress ip = null;
  
  private DatagramSocket socket = null;
  private DatagramPacket packet = null;
  
  private byte[] okSign = "Process Success".getBytes();
  private String finishSign = "Process Finish";
  
  private byte[] buffer = null;
  private int size = 2048;
  
  public UDPFileServer() {
    
    try {
      
      port = 32123;
      ip = new InetSocketAddress(InetAddress.getByName("localhost"), port);
      socket = new DatagramSocket(ip);
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }
  
  /*
  * 파일 전송 시작전
  * 클라이언트가 보내주는 파일 이름 체크
  * */
  public void fileNameCheck() {
    buffer = new byte[size];
    packet = new DatagramPacket(buffer, size);
    
    try {
      socket.receive(packet);

      fileName = new String(packet.getData(), 0, packet.getLength());
      System.out.println("Client send(File name) : " + fileName);
      
      packet = new DatagramPacket(fileName.getBytes(), fileName.getBytes().length, packet.getAddress(), packet.getPort());
      socket.send(packet);
      
      packet = new DatagramPacket(buffer, size);
      socket.receive(packet);
      
      System.out.print("NAME CHECK: ");
      
      if (new String(packet.getData(), 0, packet.getLength()).equals("ok")) {
        System.out.println("name check success, transfer start");
      } else {
        System.out.println("name check failed, process end");
        System.exit(1);
      }
      
      
      
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void startProcess() {

    File file = new File(fileName);
    
    int readSize = 0;
    int count = 0;
    
    try {
      packet = new DatagramPacket(buffer, 0, size, ip);
      socket.receive(packet);
      System.out.println(new String(packet.getData(), 0, packet.getLength()));
      
      BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
      
      
      while ((readSize = packet.getLength()) != 0) {
        String msg = new String(buffer, 0, readSize);
        System.out.println(msg);
        
        if (msg.equals(finishSign)) {
          System.out.println("finished");
          break;
        }
        
        bos.write(buffer, 0, readSize);
        bos.flush();
        
        packet.setData(okSign, 0, okSign.length);
        socket.send(packet);
        
        packet.setData(buffer, 0, size);
        socket.receive(packet);
      }
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public void start() {
    fileNameCheck();
    startProcess();
    
  }
  
  
  
  
  public static void main(String[] args) {
    
    System.out.println("<<UDP FILE SERVER STARTING...>>>");
    
    UDPFileServer server = new UDPFileServer();
    server.start();
  }
  
}
