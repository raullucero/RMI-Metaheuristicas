
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.rmi.*;
import java.lang.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import javax.swing.ImageIcon;
import java.awt.Color;

public class Methods
        extends java.rmi.server.UnicastRemoteObject implements Interface {

    static final double pC = 0.7;
    static final double pM = 0.07;
    LinkedList colaR1 = new LinkedList();
    LinkedList colaR2 = new LinkedList();
    
    // Constructor
    public Methods()
            throws RemoteException {
        super();
    }

    public void divideImages(ImageIcon[] image) {
        int numeroImagenes = image.length;
        int cromo = image.length * 3;

        double fitnessIdeal = 1 / (((double)cromo/3) / 8 ); //variable con el fitness perfecto
        String [] iPP = new String[5]; //iPP = initial population phenotype
        String [] iPPS = new String[5]; //iPPS = initial population phenotype sort
        String [] nG = new String[5]; //nG = nueva generacion  
        String [] seleccion = new String[2];
        String [] hijos = new String[2];
        int [] numTasks =  new int[8];
        double [] iPG = new double[5]; //iPG = initial population genotype
        double [] iPGS = new double[5]; //iPG ordenado (fitness)
        int p = 0;
        int contRepeticion = 0;
        double fitness_actual;
        double repeticion = 0;
        int [] numTaskInTwo = new int[2];
        iPP = GenerationInitialPopulation(5, cromo);
        iPG = Fitness(iPP, cromo); 
        iPGS = iPGsort(iPG);
        iPPS = SortPhenotype(iPP, iPG, iPGS);
        for(int j=0;  j<10000; j++)
        {
            nG[0] = Elitismo(iPPS);
            System.out.println(iPGS[iPG.length-1]);
            fitness_actual = iPGS[iPG.length-1];
           // if(contRepeticion > 100) {
             //   break;
            //}
            if(iPGS[iPG.length-1] == fitnessIdeal)
            {
                p = j;
                break;
            }
            else
                p++;
            for(int i=1; i<4; i+=2)
            {
                seleccion = Selection(iPGS, iPPS);  
                hijos = CrossOver(seleccion[0], seleccion[1], cromo);
                hijos[0] = Mutation(hijos[0]);
                hijos[1] = Mutation(hijos[1]);
                nG[i] = hijos[0];
                nG[i+1] = hijos[1];
            }
            
            iPP = nG; //nueva poblacion
            iPG = Fitness(iPP, cromo); 
            iPGS = iPGsort(iPG);
            iPPS = SortPhenotype(iPP, iPG, iPGS);
            if(fitness_actual == repeticion) 
            {
                contRepeticion++;
            }
            else {
                repeticion = fitness_actual;
                contRepeticion = 0;
            }
        }
        numTasks = CountTasks(Elitismo(iPPS), cromo);
        numTaskInTwo[0] = numTasks[0] + numTasks [1] + numTasks[2] + numTasks[3];
        numTaskInTwo[1] = numTasks[4] + numTasks [5] + numTasks[6] + numTasks[7];
        
        System.out.println("Numero de poblaciones generadas " + p);
        //for (int i = 0; i < 8; i++)
        //{
         //   System.out.println("Replica " + (i+1) + ": " + numTasks[i] + " Imagenes");
        //}
        System.out.println("Replica 1: " + numTaskInTwo[0] + " Imagenes");
        System.out.println("Replica 2: " + numTaskInTwo[1] + " Imagenes");

        for (int i = 0; i < numeroImagenes; i++) {
            if (i < numTaskInTwo[0]) {
                colaR1.offer(image[i]);
            } else {
                colaR2.offer(image[i]);
            }
        }
        
    }

    
    public ImageIcon[] GetImages(ImageIcon[] image, int op) {
        int numeroImagenes = image.length;
        int option = op;

        divideImages(image);
//        //Determinamos que la replica 1 tendra como maximo 2 cargas de tareas
//        for (int i = 0; i < image.length; i++) {
//            if (i < 2) {
//                colaR1.offer(image[i]);
//            } //Determinamos que la replica 2 tendra como maximo 3 cargas
//            else if (i < 5) {
//                colaR2.offer(image[i]);
//            } else {
//                restantes.offer(image[i]);
//            }
//        }

        System.out.println(colaR1.size());
        System.out.println(colaR2.size());

        Threads parte1 = new Threads("1", colaR1, op);
        Threads parte2 = new Threads("2", colaR2, op);

        parte1.start();
        parte2.start();

        boolean bandera1 = true, bandera2 = true;

        do
        {

            if (parte1.isDone() && bandera1) {
                //LinkedList result = parte1.getResultado();
                //result.toArray(imagesPart1);
                //System.out.println("Obtuvo valor 1");
                bandera1 = false;
            }
            if (parte2.isDone() && bandera2) {
                //LinkedList result = parte2.getResultado();
                //result.toArray(imagesPart2);
                //System.out.println("Obtuvo valor 2");
                bandera2 = false;
            }
            boolean impresa = false;
            System.out.print("");
        }while (bandera1 || bandera2);

        LinkedList result1 = parte1.getResultado();
        LinkedList result2 = parte2.getResultado();

        System.out.println("Replica 1 " + result1.size());
        System.out.println("Replica 2 " + result2.size());

        ImageIcon[] imagesPart1 = (ImageIcon[]) result1.toArray(new ImageIcon[result1.size()]);
        ImageIcon[] imagesPart2 = (ImageIcon[]) result2.toArray(new ImageIcon[result2.size()]);
        ImageIcon[] vectorImagenes3 = new ImageIcon[result1.size() + result2.size()];

        System.arraycopy(imagesPart1, 0, vectorImagenes3, 0, imagesPart1.length);
        System.arraycopy(imagesPart2, 0, vectorImagenes3, imagesPart1.length, imagesPart2.length);
        System.out.println("Incorporacion de imagenes completa");

        return vectorImagenes3;
    }

    public ImageIcon transformImages(ImageIcon image, int op, int delay) {
        BufferedImage bi = null;
        int r, g, b;
        Color color;
        ImageIcon invertImage = new ImageIcon();

        //Convert ImageIcon to BufferedImage
        Image myimage = image.getImage();
        BufferedImage imgsel = new BufferedImage(myimage.getWidth(null), myimage.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = imgsel.createGraphics();
        g2.drawImage(myimage, 0, 0, null);
        g2.dispose();

        //Negativos
        if (imgsel != null && op == 1) {
            bi = new BufferedImage(imgsel.getWidth(), imgsel.getHeight(), imgsel.getType());
            for (int i = 0; i < imgsel.getWidth(); i++) {
                for (int j = 0; j < imgsel.getHeight(); j++) {
                    //Se obtiene el colo del pixel
                    color = new Color(imgsel.getRGB(i, j));
                    //se extraen los valores RGB
                    r = color.getRed();
                    g = color.getGreen();
                    b = color.getBlue();
                    //se coloca len la nueva imagen los valores invertidos
                    Color miColor = new Color(255 - r, 255 - g, 255 - b);
                    bi.setRGB(i, j, miColor.getRGB());
                }
            }
            invertImage = new ImageIcon(bi);
        } //Escala de Grises
        else if (imgsel != null && op == 2) {
            int mediaPixel, colorSRGB;
            Color colorAux;

            //Recorremos la imagen p�xel a p�xel
            for (int i = 0; i < imgsel.getWidth(); i++) {
                for (int j = 0; j < imgsel.getHeight(); j++) {
                    //Almacenamos el color del p�xel
                    colorAux = new Color(imgsel.getRGB(i, j));
                    //Calculamos la media de los tres canales (rojo, verde, azul)
                    mediaPixel = (int) ((colorAux.getRed() + colorAux.getGreen() + colorAux.getBlue()) / 3);
                    //Cambiamos a formato sRGB
                    colorSRGB = (mediaPixel << 16) | (mediaPixel << 8) | mediaPixel;
                    //Asignamos el nuevo valor al BufferedImage
                    imgsel.setRGB(i, j, colorSRGB);
                }
            }
            invertImage = new ImageIcon(imgsel);
        } else {
            System.out.println("No hay opcion");
            invertImage = null;
        }

        long TimeOne = System.currentTimeMillis();
        do {
        } while ((TimeOne + delay) > System.currentTimeMillis());
        System.out.println("Imagen procesada");
        return invertImage;
    }
    
    public double [] iPGsort(double iPG[]) {   
        double [] tempGeno = new double[iPG.length];
        tempGeno = iPG;
        Arrays.sort(tempGeno);
        return tempGeno;
    }
    
    public int ConvertDecimal(String op) {
        if(op.equals("000")) {
            return 1;
        }
        else if (op.equals("001")) {
            return 2;
        }
        else if (op.equals("010")) {
            return 3;
        }

        else if (op.equals("011")) {
            return 4;
        }
        else if (op.equals("100")) {
            return 5;
        }

        else if (op.equals("101")) {
            return 6;
        }

        else if (op.equals("110")) {
            return 7;
        }
        else 
        {
            return 8;
        }
    }
    
    public String [] GenerationInitialPopulation(int size, int cromo) 
    {
	String [] initial_population_phenotype = new String[size];
        Random r = new Random();
	int rn;
	for (int j = 0; j < size; j++)
		initial_population_phenotype[j] = "";
	for (int i = 0; i < size; i++)
	{
		for (int j = 0; j < cromo; j++)
		{
                    rn = r.nextInt(2);
                    initial_population_phenotype[i] += Integer.toString(rn);
		}
	}
	return initial_population_phenotype;
    }
    
    // funcion para obtener el fitnes de cada cromosoma 
    public double [] Fitness (String [] iPP, int cromo){
        int [] countReplies = new int [8];
        int [] tempRep = new int [8];
        for(int i = 0; i < 8; i++) {
            countReplies[i] = 0;
            tempRep[i] = 0;
        }
        double [] initial_population_genotype = new double[iPP.length];
        for (int i = 0; i < 5; i++)
            initial_population_genotype[i] = 0.0;
        for (int i = 0 ; i < iPP.length; i++)
        {
            for (int a = 0; a < cromo; a += 3)
            {
                int temp = ConvertDecimal(iPP[i].substring(a, a + 3));
                countReplies[temp-1]++;  //Numero de requerimientos en cada replica
            }
            tempRep = countReplies;
            Arrays.sort(tempRep);
            initial_population_genotype[i] = 1 / (double)(Math.abs(tempRep[7]-tempRep[0]) + Math.abs(tempRep[7])); //funcion objetivo
            for (int j = 0; j < 8; j++)
        	   countReplies[j] = 0;
        }     
        return initial_population_genotype;
    }
    
    public int [] CountTasks(String bestSolution, int cromo) {
        int [] count = new int[8];
        for (int a = 0; a < cromo; a += 3)
        {
            int temp = ConvertDecimal(bestSolution.substring(a, a+3));
            count[temp-1]++;  //Numero de requerimientos en cada replica
        }
        return count;
    }
    
    public String [] SortPhenotype(String [] iPP, double [] iPG, double [] iPGS)
    {
        String [] iPPS = new String[iPP.length];
        iPPS = iPP;
        for(int i=0; i< iPP.length; i++)
        {
            for (int j = 0; j < iPP.length; j++)
            {
                if(iPGS[i] == iPG[j]) {
                    iPPS[i] = iPP[j];
                    break;
                }
            }
        }
	return iPPS;
    }
    
    public String Elitismo(String [] iPPS)
    {
        String better = iPPS[iPPS.length - 1];
        return better;
    }

public String [] Selection (double iPGS[], String [] iPPS)
{
    double zum=0;
    boolean flag = false;
    Random r = new Random();
    String [] tempSelec = new String[2];
    //suma de los genotipos 
    for (int i = 0 ;i< (iPGS.length);i++)
    {
        zum+= iPGS[i];    
    }
    //seleccion
    for(int j=0 ;j<2;j++)
    {
        flag = false;
        for (int i = 0 ;i< (iPGS.length);i++)
        {
        	double aleatorio = (r.nextDouble());
        	double com = aleatorio * zum;
         	if(iPGS[i]>=com)//se selecciona 
                { 
                    tempSelec[j]=iPPS[j];   
                    flag = true;
                    break;
                }            
        }
        if(flag == false) {
            j=j-1;
        }
    }
    return tempSelec;
    }

public String [] CrossOver (String padre1, String padre2, int cromo) {
    String [] hijos = new String[2];
    String hijo1 = "";
    String hijo2 = "";
    int r2=0;
    Random r = new Random();
    double rn;
    rn = r.nextDouble();
    if(rn <= pC) {
        hijos[0] = padre1;
        hijos[1] = padre2;
    }
    else {
        r2 = r.nextInt(cromo);
        hijo1 = padre1.substring(0, r2);
        hijo1 += padre2.substring(r2);
        hijo2 = padre2.substring(0, r2);
        hijo2 += padre1.substring(r2);
        hijos[0] = hijo1;
        hijos[1] = hijo2;
    }
    return hijos;
}

public String Mutation (String cromosoma){
    Random r = new Random();
    double tempRand = r.nextDouble();
    String mutado = "";
    if (pM > tempRand) {
    double aleatorio = r.nextDouble();
    for (int i = 0; i < cromosoma.length(); i++)
    {
        if (pM > aleatorio) { 
            if (cromosoma.substring(i,i+1) == "0") 
            {
                mutado += '1';
            }
            else {
                 mutado += '0';
            }
        }
        else {
            mutado += cromosoma.substring(i, i+1);
        }
    }
    }
    else {
        mutado += cromosoma;
    }
    return mutado;
}

}
