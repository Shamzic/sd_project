import java.util.ArrayList;
import java.rmi.server.UnicastRemoteObject ;
import java.rmi.RemoteException ;
import java.rmi.* ; 
import java.net.MalformedURLException ; 
import java.util.concurrent.TimeUnit;

class JoueurImpl extends UnicastRemoteObject implements Joueur
{
    public static int id, RD, RI;
    public ArrayList<Ressource> RList;
    static ConnexionImpl C;
    static MessageControle M;
    Thread T ;
    final static Object monitor = new Object();
    static JoueurImpl J;
    
    public static void main (String [] args)
    {
        int i;
        if ( args.length != 4)
        {
            System.err.println( "usage : <ControllerMachineName> <ControllerPort> <JoueurMachineName> <ProducterPort>");
            System.exit(1);
        }
        try
		{
            
            // débute la communication avec le controller
            M = (MessageControle) Naming.lookup("rmi://" + args[0] + ":" + args[1] + "/MessageControleGlobal");
            
            TripleImpl T =M.getPlayerInitialInfo();
            System.out.println("Le joueur reçoit l'id : " + T.x + ", RI : " + T.y + ", RD : " + T.z);
            
            // initialise le serveur joueur
            J = new JoueurImpl (T.x, T.y, T.z, args[3]);
            Naming.rebind( "rmi://localhost:"+args[3] + "/Joueur", J);
            
            // Maintenant envoie ses "coordonnées" au Coordinateur
            M.addMachine( args[2], Integer.parseInt(args[3]) );
        }
        catch (RemoteException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
        catch (NotBoundException re) { System.out.println(re) ; }
        
        start();
    }
    
    
	JoueurImpl(int id, int RI, int RD, String portSelf)
    throws RemoteException
	{
		this.id = id;
        this.RD = RD; // Ressources différentes ??
        this.RI = RI; // Ressources initiales du joueur, inutile ???
        RList = new ArrayList<Ressource> (RD);

        // Boucle pour init la liste des ressources du joueur
        // Ayant des quantité nulles pour chaque type
        for (TYPE t : TYPE.values())
        {
        	RList.add(new Ressource(0,t));
        }
        // initialise le serveur connexion pour que le controlleur puisse lui envoyer les nouveaux connectés
        C = new ConnexionImpl();
        try
        {
            Naming.rebind( "rmi://localhost:"+ portSelf + "/Connexion", C);
            System.out.println("pas ici");
        }
        catch (RemoteException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
                
    }
	
	// Incrémente de x la quantité de la ressource de type t
	public void increaseRessourceAmout(TYPE t, int x)
    throws RemoteException
    {
    	for(int i=0 ; i < RList.size() ; i++)
           if(RList.get(i).getStockType()==t)
           		RList.get(i).increaseRessource(x);
    }

    public static void start()
    {
        while(true)
                {
                    try
                    {
                        synchronized (monitor)
                        {
                            System.out.println("j'attends");
                            monitor.wait();
                        }
                        System.out.println("À mon tour.");
                        TimeUnit.SECONDS.sleep(1);
                        C.JList.get(id +1).receiveToken();
                    }
                    catch (InterruptedException re) { System.out.println(re) ; }
                    catch (RemoteException re) { System.out.println(re) ; }
                }
    }
	
    //~ public void start()
    //~ throws RemoteException
    //~ {
        //~ T = new Thread()
        //~ {
            //~ public void run()
            //~ {
                //~ while(true)
                //~ {
                    //~ try
                    //~ {
                        //~ synchronized (monitor)
                        //~ {
                            //~ System.out.println("j'attends");
                            //~ monitor.wait();
                        //~ }
                        //~ System.out.println("À mon tour.");
                        //~ TimeUnit.SECONDS.sleep(1);
                        //~ C.JList.get(id +1).receiveToken();
                    //~ }
                    //~ catch (InterruptedException re) { System.out.println(re) ; }
                    //~ catch (RemoteException re) { System.out.println(re) ; }
                //~ }
            //~ }
            //~ 
            //~ 
        //~ };
        //~ T.run();
    //~ }
    
    public void receiveToken()
    throws RemoteException
    {
        System.out.println("Mon tour =)");
        synchronized(monitor)
        {monitor.notify();}
    }
    
    public void salut()
    throws RemoteException
    {
        System.out.println("Salut on vient de t'ajouter");
    }
    
}

