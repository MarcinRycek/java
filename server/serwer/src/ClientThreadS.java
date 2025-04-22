import java.io.*;
import java.net.Socket;

public class ClientThreadS extends Thread{
    Socket socket;
    Server server;
    PrintWriter printWriter;
    ClientThreadS(Socket socket, Server server){
        this.socket=socket;
        this.server=server;
    }
    @Override
    public void run(){
        try {
            BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            String msg;
            while ((msg= bufferedReader.readLine())!=null){
                System.out.println("received: "+msg);
                server.broadcast(msg);
            }
            System.out.println("client closed");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    void send(String msg){
        this.printWriter.println(msg);
    }
}
