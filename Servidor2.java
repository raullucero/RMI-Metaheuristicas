
import java.rmi.Naming;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Raul
 */
public class Servidor2 {
    
     public Servidor2() {

   	//Construct a new threadsimpl object and bind it to the local rmiregistry
        try {
            Interface c = new Methods();
            Naming.rebind("//200.1.2.2:1099/ThreadServer2", c);
        } catch (Exception e) {
            System.out.println("Server Error: " + e);
        }
    }
    
    public static void main(String args[]) {

        //Create the new threads server
        new Servidor2();
    }
    
}
