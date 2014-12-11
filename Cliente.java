
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.rmi.Naming;			// Import rmi naming - so you can lookup remote objects
import java.rmi.RemoteException;	// Import exceptions
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

public class Cliente {

    public static void main(String[] args) {

        try {

            // Create the reference to the remote object through the rmiregistry			
            Interface c = (Interface) Naming.lookup("//200.1.3.2:1234/ThreadsService");

            // Now use the reference c to call remote method
            BufferedImage imgsel;
            ImageIcon imgicsel, invertImage;
            int numImagenes = 8;
            ImageIcon[] vectorImagenes = new ImageIcon[numImagenes];
            ImageIcon[] imagenesNuevas = new ImageIcon[numImagenes];
            for (int i = 0; i < numImagenes; i++) {
                JFileChooser selector = new JFileChooser();
                int r = selector.showOpenDialog(null);
                if (r == JFileChooser.APPROVE_OPTION) {
                    try {
                        imgsel = ImageIO.read(selector.getSelectedFile());
                        imgicsel = new ImageIcon(imgsel);
                        vectorImagenes[i] = imgicsel;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            
            //Obtiene las imagenes de los servidores
            imagenesNuevas = c.GetImages(vectorImagenes, 1);
            
            
            for(int i = 0; i < numImagenes; i++) {
            invertImage = imagenesNuevas[i];
            Image myimage = invertImage.getImage();
            BufferedImage imgFinal = new BufferedImage(myimage.getWidth(null),myimage.getHeight(null),BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = imgFinal.createGraphics();
            g2.drawImage(myimage, 0, 0, null);
            g2.dispose();
            System.out.println("Obtuvo IMAGEN");
              try {
              ImageIO.write(imgFinal, "jpg", new File("foto"+i+".jpg"));
              } catch (IOException e) {
              System.out.println("Error de escritura");
            }
          }

        } // Catch the exceptions - rubbish URL, Remote exception or Not bound exception.
        catch (MalformedURLException murle) {
            System.out.println("MalformedURLException" + murle);
        } catch (RemoteException re) {
            System.out.println("RemoteException" + re);
        } catch (NotBoundException nbe) {
            System.out.println("NotBoundException" + nbe);
        }
    }
}
