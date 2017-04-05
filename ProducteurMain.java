import java.rmi.* ; 
import java.net.MalformedURLException ; 

public class ProducteurMain
{
    static MessageControle M; // Objet grâce auquel le producteur communique avec le contrôleur
    static ProducteurImpl P; // Objet qui instancie le producteur
    static ConnexionImpl C; // Objet qui instancie la connexion grâce auquel le producteur communiquera avec les autres
    
    public static void main (String [] args)
    {
        int i;
        if ( args.length != 4)
        {
            System.err.println( "usage : <ControllerMachineName> <ControllerPort> <JoueurMachineName> <ProducteurPort>");
            System.exit(1);
        }
        try
		{
            
            // débute la communication avec le controller
            M = (MessageControle) Naming.lookup("rmi://" + args[0] + ":" + args[1] + "/MessageControleGlobal");
            
            TripleImpl  T = M.getProducteurInitialInfo(); // demande les informations initiales au contrôleur
            
            
            // initialise le serveur producteur
            P = new ProducteurImpl(T.x,T.y, T.z); // Fait le producteur
            
            Naming.rebind( "rmi://localhost:"+args[3] + "/Producteur", P);
            
            M.addProducteur( args[2], Integer.parseInt(args[3]) ); // Maintenant envoie ses "coordonnées" au Coordinateur
            System.out.println("Le producteur a été ajouté");
			
		}
        catch (RemoteException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
        catch (NotBoundException re) { System.out.println(re) ; }
        
        
    }
    
}
