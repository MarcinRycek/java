import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread{
    Socket socket;
    PrintWriter printWriter;
    BufferedReader bufferedReader;
    ClientThread(String host, int port) throws IOException {
        socket=new Socket(host, port);
    }
    public void run(){
        try {
            this.bufferedReader= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.printWriter=new PrintWriter(socket.getOutputStream());
            String msg;
            while ((msg=bufferedReader.readLine())!=null){
                System.out.println("received:" + msg);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public void broadcast(String msg){
        printWriter.println(msg);
    }
}
