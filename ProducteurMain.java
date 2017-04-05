import java.rmi.* ; 
import java.net.MalformedURLException ; 

public class ProducteurMain
{
    static MessageControle M;
    static ProducteurImpl P;
    static ConnexionImpl C;
    
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
            
            TripleImpl  T = M.getProducteurInitialInfo();
            
            
            // initialise le serveur producteur
            P = new ProducteurImpl(T.x,T.y, T.z);
            
            Naming.rebind( "rmi://localhost:"+args[3] + "/Producteur", P);
            
            
            // Maintenant envoie ses "coordonnées" au Coordinateur
            M.addProducteur( args[2], Integer.parseInt(args[3]) );
            System.out.println("Ajouté le producteur");
			
		}
        catch (RemoteException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
        catch (NotBoundException re) { System.out.println(re) ; }
        
        
    }
    
}
