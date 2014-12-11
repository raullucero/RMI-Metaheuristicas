
import javax.swing.ImageIcon;

public interface Interface
        extends java.rmi.Remote {

    public ImageIcon transformImages(ImageIcon images, int op,int delay) 
            throws java.rmi.RemoteException;

    public ImageIcon[] GetImages(ImageIcon[] image, int op) 
            throws java.rmi.RemoteException;
    
    public void divideImages(ImageIcon[] image) 
            throws java.rmi.RemoteException;
    
    public double [] iPGsort(double iPG[]) 
            throws java.rmi.RemoteException;
    
    public int ConvertDecimal(String op)
            throws java.rmi.RemoteException;

    public String [] GenerationInitialPopulation(int size, int cromo) 
            throws java.rmi.RemoteException;
    
    public double [] Fitness (String [] iPP, int cromo)
            throws java.rmi.RemoteException;
    
    public int [] CountTasks(String bestSolution, int cromo)
            throws java.rmi.RemoteException;
    
    public String [] SortPhenotype(String [] iPP, double [] iPG, double [] iPGS)
            throws java.rmi.RemoteException;
    
    public String Elitismo(String [] iPPS)
            throws java.rmi.RemoteException;
    
    public String [] Selection (double iPGS[], String [] iPPS)
            throws java.rmi.RemoteException;
    
    public String [] CrossOver (String padre1, String padre2, int cromo) 
            throws java.rmi.RemoteException;
    
    public String Mutation (String cromosoma)
            throws java.rmi.RemoteException;
    
}
