import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
            ClientThread clientThread = new ClientThread("localhost", 8080);
            clientThread.start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            while (true){
                String msg = bufferedReader.readLine();
                clientThread.broadcast(msg);
            }
        }
    }
