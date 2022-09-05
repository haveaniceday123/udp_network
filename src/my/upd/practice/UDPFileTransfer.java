package my.upd.practice;


import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.*;

public class UDPFileTransfer {
  
  private String fileName = "sample.mp4";
  private int serverPort = 32123;
  private InetAddress serverIp = null;
  
  private DatagramSocket socket = null;
  private DatagramPacket packet = null;
  
  private String okSign = "Process Success";
  private byte[] finishSign = "Process Finish".getBytes();
  
  private byte[] buffer = null;
  private int bufferSize = 2048;
  
  UDPFileTransfer() {
    try {
      serverIp = InetAddress.getByName("localhost");
      socket = new DatagramSocket();
    } catch (UnknownHostException | SocketException e) {
      e.printStackTrace();
    }
  }
  
  /*
  * 파일을 보내기전에 서버에 파일 이름을 전송하고
  * 수신확인을 위해 서버에서 다시 파일 이름을 리턴
  * 해당 리턴값을 비교하여 파일 이름이 같을경우에 전송 시작
  * */
  public boolean initConnection() {
    System.out.println("File name to transfer: " + fileName);
    buffer = new byte[bufferSize];
    
    try {
      packet = new DatagramPacket(fileName.getBytes(), fileName.getBytes().length, serverIp, serverPort);
      socket.send(packet);
      
      packet = new DatagramPacket(buffer, buffer.length, serverIp, serverPort);
      socket.receive(packet);
      
      String checkFileName = new String(packet.getData(), 0, packet.getLength());
      
      if (checkFileName.equals(fileName)) {
        packet = new DatagramPacket("ok".getBytes(), "ok".getBytes().length, serverIp, serverPort);
        socket.send(packet);
      } else {
        packet = new DatagramPacket("fail".getBytes(), "fail".getBytes().length, serverIp, serverPort);
        socket.send(packet);
      }
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    return true;
  }
  
  
  public boolean checkData(String msg) {
    
    if (msg.equals(okSign)) {
      return true;
    }
    
    return false;
    
  }
  
  public void sendFile() {
    int read = 0;
    int count = 0;
    byte[] tmpBuf = new byte[bufferSize];
    
    try {
      RandomAccessFile file = new RandomAccessFile("src/" + fileName, "r");
      
      packet = new DatagramPacket(buffer, bufferSize, serverIp, serverPort);
      while(( read = file.read(buffer, 0, bufferSize)) != -1) {
        System.out.println("ReadSize : "+ read);
        packet.setData(buffer, 0, read);
        socket.send(packet);
        count++;
        
        while (true) {
          packet.setData(tmpBuf, 0, bufferSize);
          socket.receive(packet);
          
          String msg = new String(packet.getData(), 0, packet.getLength());
          if (checkData(msg)) {
            break;
          } else {
            packet.setData(buffer, 0, bufferSize);
            socket.send(packet);
          }
        }
      }
      
      packet.setData("Process Finish".getBytes(), 0, "Process Finish".getBytes().length);
      socket.send(packet);
      
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public static void main(String[] args) {
    UDPFileTransfer client = new UDPFileTransfer();
    System.out.println("String UDP file transper...");
    
    client.initConnection();
    client.sendFile();
  }
  
  

 
}
