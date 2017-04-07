import java.rmi.* ; 
import java.net.MalformedURLException ; 

public class JoueurMain
{
    static MessageControle M;
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
            

            //~ // ------- TESTER ---------
            //~ System.out.println("TEST : Le joueur prend DEUX ressources N°0 au producteur N°0 : ");
         	//~ C.takeRessourceAmount(0,0,2);
			//~ // On réaffiche pour tester si la ressource a bien diminuée de 2 : 
			//~ System.out.println("TEST : La ressource du producteur 0 et ressource 0 : " + C.getStockAmount(0,0));
			//~ // ---- FIN TESTER --------- 
            
		}
        catch (RemoteException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
        catch (NotBoundException re) { System.out.println(re) ; }
        
        
    }
    
}
