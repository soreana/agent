import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by sinakashipazha on 5/24/2017 AD.
 */
public class Agent implements Runnable{
    private Thread mainThread;
    private ServerSocket serverSocket;
    private ArrayList<SubAgent> subAgents;

    public Agent(int portNumber) throws IOException {
        mainThread = new Thread(this);
        serverSocket = new ServerSocket(portNumber);
        subAgents = new ArrayList<>();
        subAgents.add(new SubAgent());
        subAgents.add(new SubAgent());
        subAgents.add(new SubAgent());
        subAgents.add(new SubAgent());
        mainThread.start();
    }

    private class SubAgent implements Runnable{
        private Socket client;
        private Thread thread;
        private PrintWriter out;
        private BufferedReader in;

        SubAgent(){
            thread = new Thread(this);
        }

        void setClient( Socket socket){
            try {
                client = socket;
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(
                        new InputStreamReader(client.getInputStream()));
                thread.start();
            } catch (IOException ignored) {}
        }

        Thread.State getState(){
            return thread.getState();
        }

        public void run() {
            String input;
            try {
                while( (input = in.readLine() ) != null ){
                    System.out.println( input );
                    out.println(input);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        while (true) {
            try {
                Socket temp = serverSocket.accept();
                for(SubAgent current : subAgents){
                    if(current.getState() == Thread.State.NEW){
                        current.setClient(temp);
                        break;
                    }
                }
            } catch (IOException ignore) {
            }
        }
    }

    public static void main(String [] args) throws IOException, InterruptedException {
        new Agent(1234);
        while (true)
            Thread.sleep(100);
    }
}