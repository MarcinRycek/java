import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    ServerSocket serverSocket;
    List<ClientThreadS> clientThreadSList= new ArrayList<>();
    Server(int port) throws IOException {
        try {
            serverSocket = new ServerSocket(port);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    void listen(){
        System.out.println("listenin.........");
        while (true){
            Socket socket= null;
            try {
                socket=serverSocket.accept();
                System.out.println("New client connected");
                ClientThreadS clientThreadS=new ClientThreadS(socket, this);
                clientThreadSList.add(clientThreadS);
                clientThreadS.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void broadcast(String msg){
        for (ClientThreadS client:clientThreadSList ){
            client.send(msg);
        }
    }

}
