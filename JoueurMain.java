import java.rmi.* ; 
import java.net.MalformedURLException ; 

public class JoueurMain
{
    static MessageControle M;
    static JoueurImpl J;
    static ConnexionImpl C;
    
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
            J = new JoueurImpl (T.x, T.y, T.z);
            Naming.rebind( "rmi://localhost:"+args[3] + "/Joueur", J);
            
            // initialise le serveur connexion pour que le controlleur puisse lui envoyer les nouveaux connectés
            C = new ConnexionImpl();
            Naming.rebind( "rmi://localhost:"+args[3] + "/Connexion", C);
            
            
            // Maintenant envoie ses "coordonnées" au Coordinateur
            M.addMachine( args[2], Integer.parseInt(args[3]) );
            
			
		}
        catch (RemoteException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
        catch (NotBoundException re) { System.out.println(re) ; }
        
        
    }
    
}
