import java.rmi.* ; 
import java.net.MalformedURLException ; 

public class ProducteurMain
{
    static MessageControle M;
    static Producteur P;
    
    
    public static void main (String [] args)
    {
        try
		{
            
            // débute la communication avec le controller
            M = (MessageControle) Naming.lookup("rmi://" + args[0] + ":" + args[1] + "/MessageControleGlobal");
            TripleImpl T =M.getProdInitialInfo();
            System.out.println("Le Producteur reçoit l'id : " + T.x + ", RI : " + T.y + ", RD : " + T.z);
            P = new Producteur ( T.x, T.y, T.z);
            
            
            
            
			
		}
        catch (RemoteException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
        catch (NotBoundException re) { System.out.println(re) ; }
        
        
    }
    
}
