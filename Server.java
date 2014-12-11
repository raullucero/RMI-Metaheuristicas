
import java.rmi.Naming;		//Import naming classes to bind to rmiregistry

public class Server {

   //threadsserver constructor
    public Server() {

   	//Construct a new threadsimpl object and bind it to the local rmiregistry
        try {
            Interface c = new Methods();
            Naming.rebind("//200.1.3.2:1234/ThreadsService", c);
        } catch (Exception e) {
            System.out.println("Server Error: " + e);
        }
    }

    public static void main(String args[]) {

        //Create the new threads server
        new Server();
    }
}
