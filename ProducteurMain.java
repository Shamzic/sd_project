import java.rmi.* ; 
import java.net.MalformedURLException ; 

public class ProducteurMain
{
    static MessageControle M;
    static ProducteurImpl P;
    
    
    public static void main (String [] args)
    {
        if ( args.length != 4)
        {
            System.err.println( "usage : <ControllerMachineName> <ControllerPort> <ProducterMachineName> <ProducterPort>");
            System.exit(1);
        }
        try
		{
            
            // débute la communication avec le controller
            M = (MessageControle) Naming.lookup("rmi://" + args[0] + ":" + args[1] + "/MessageControleGlobal");
            TripleImpl T =M.getProdInitialInfo();
            System.out.println("Le Producteur reçoit l'id : " + T.x + ", RI : " + T.y + ", RD : " + T.z);
            P = new ProducteurImpl ( T.x, T.y, T.z);
            
            // initialise la connexion
            Naming.rebind( "rmi://localhost:"+args[3] + "/Producteur"+ P.id, P);
            // Maintenant envoie ses "coordonnées" au Coordinateur
            M.addMachine( args[2], Integer.parseInt(args[3]) );
            
            je fais un objet dans lequel je mets toutes les connexions avec les Joueurs/Producteurs
            quand j ajoute un joueur/producteur j envoie un message au Controlleur depuis MessController
            et le controlleur envoie un message à tous les agents
            
			
		}
        catch (RemoteException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
        catch (NotBoundException re) { System.out.println(re) ; }
        
        
    }
    
}
