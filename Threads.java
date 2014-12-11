import static java.lang.Thread.currentThread;
import java.util.LinkedList;
import javax.swing.ImageIcon;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Raul
 */
public class Threads extends Thread {

    private boolean done;
    LinkedList cola;
    private LinkedList resultado;
    int option;

    public Threads(String str, LinkedList cola, int option){
        super(str);
        this.option = option;
        this.done = false;
        this.cola = cola;
        this.resultado = new LinkedList();
    }

    public void run() {

        Thread thActual = currentThread();

        if ("1".equals(this.getName()) && !done) {
            try {
                Interface server1 = (Interface) java.rmi.Naming.lookup("//200.1.1.2:1233/ThreadServer1");
                while (cola.peek() != null) {
                    ImageIcon image;
                    image = server1.transformImages((ImageIcon) cola.poll(), option, 500);
                    resultado.offer(image);
                    //System.out.println("obtuvo imagen Cola 1 tama�o: " + cola.size());
                }
                if (resultado.size() != 0) {
                    //System.out.println("Obtuvo valor con " + resultado.size() + " elementros");
                    done = true;
                }
            } catch (Exception e) {
                System.out.println("No se pudo realizar operacion");
            }

        }

        if ("2".equals(this.getName()) && !done) {
            try {
                Interface server2 = (Interface) java.rmi.Naming.lookup("//200.1.2.2:1099/ThreadServer2");
                while (cola.peek() != null) {
                    ImageIcon image;
                    image = server2.transformImages((ImageIcon) cola.poll(), option, 500);
                    resultado.offer(image);
                    //System.out.println("obtuvo imagen Cola 2 tama�o: " + cola.size());
                }
                if (resultado.size() != 0) {
                    // System.out.println("Obtuvo valor con " + resultado.size() + " elementros");
                    done = true;
                }
            } catch (Exception e) {
                System.out.println("No se pudo realizar operacion");
            }
        }

    }

    /**
     * @return the done
     */
    public boolean isDone() {
        return done;
    }

    /**
     * @return the resultado
     */
    public LinkedList getResultado() {
        return resultado;
    }

}
