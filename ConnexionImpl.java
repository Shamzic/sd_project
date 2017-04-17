import java.util.ArrayList;
import java.rmi.server.UnicastRemoteObject ;
import java.rmi.RemoteException ;
import java.rmi.* ; 
import java.net.MalformedURLException ; 

// objet qui va contenir toutes les connexions (objets avec les autres agents)
// Une méthode de cette classe est appelée à chaque fois que le controlleur ajoute une machine
class ConnexionImpl extends UnicastRemoteObject implements Connexion
{
    public static final long serialVersionUID = 1L; // Utilie uniquement pour régler les warning de serial
    public ArrayList<Joueur> JList = new ArrayList<Joueur>();
    public ArrayList<Joueur> FinishedPlayerList = new ArrayList<Joueur>();
    
    public ArrayList<Producteur> PList = new ArrayList<Producteur>();

    
    public ConnexionImpl()
    throws RemoteException
    {
    }
    
    // Renvoie la quantité de la ressource ressourceNb du producteur producteurNb
    public int getStockAmount( int producteurNb, int ressourceNb)
    throws RemoteException
    {
        return PList.get(producteurNb).askRessourceAmount(ressourceNb);
    }
    
    // Établi la connexion avec les autres joueurs contenus dans la lsite L envoyée par le coordinateur
    public void initialSetPlayer( SerializableList<Tuple> L)
    throws RemoteException
    {
        int i;
        try
        {
            for( i = 0 ; i< L.size() ; i++)
            {
                System.out.println("Machine :" + L.get(i).MN + " :" + L.get(i).port);
                Joueur P = (Joueur) Naming.lookup("rmi://" +  L.get(i).MN + ":" + L.get(i).port + "/Joueur") ;
                JList.add( P );
            }
            
        }
        catch (NotBoundException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
    }
    
    
    // Établi la connexion avec les autres producteurs contenus dans la lsite L envoyée par le coordinateurs
    public void setProducteur ( SerializableList<Tuple> PCoordList)
    throws RemoteException
    {
        int i;
        int size = PCoordList.size();
        try
        {
            for(i=0; i < size ; i++)
            {
                System.out.println("Ajoute le producteur " + i + " au port " + PCoordList.get(i).port + " nom de machine : " + PCoordList.get(i).MN);
                Producteur P = (Producteur) Naming.lookup("rmi://" +  PCoordList.get(i).MN + ":"+PCoordList.get(i).port+"/Producteur");
                PList.add(P);
            }
        }
        catch (NotBoundException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
    }
    
    // Lorsqu'un joueur est ajouté on appel cette méthode pour le dire à tous les joueurs
    public void addConnexionPlayer(String MachineName, int port)
    throws RemoteException
    {
        try
        {
            System.out.println("J'ajoute le joueur de port " + port);
            Joueur J = (Joueur) Naming.lookup("rmi://" + MachineName + ":" + port + "/Joueur") ;
            JList.add( J );
        }
        catch (NotBoundException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
        
    }

    // Lorsqu'un joueur est ajouté on appel cette méthode pour le dire à tous les joueurs
    public void addConnexionProducteur(String MachineName, int port)
    throws RemoteException
    {
        System.out.println("J'ai ajouté le producteur " + MachineName + ":"+ port);
        try
        {
            Producteur P = (Producteur) Naming.lookup("rmi://" + MachineName + ":" + port + "/Producteur") ;
            PList.add( P );
        }
        catch (NotBoundException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
        
    }

    // Soustrait un nombre de ressource à un producteur
    // et l'ajoute à un joueur
    public void takeRessourceAmount( int producteurNb, int ressourceNb, int quantite)
    throws RemoteException
    {
        int quantite_stock = PList.get(producteurNb).askRessourceAmount(ressourceNb);
        if (quantite<=quantite_stock)
        {	
        	// décrémente de <quantite> la ressource N°ressourceNb du prod N°producteurNb
        	PList.get(producteurNb).decreaseRessourceAmount(ressourceNb,quantite);
       		//JList.get(producteurNb).increaseRessourceAmount(quantite);
       	}

    }
    
    // envoie un message à tous les agents pour qu'ils se terminent ( lorsqu'on est arrivé à la fin du jeu
    public void endAllAgents(int id)
    {
        int i;
        try
        {
            for(i=0 ; i < JList.size() ; i++)
            {
                System.out.println("j'arrête le joueur : "+ i);
                if( i != id ) // évite que l'agent s'arrête lui même avant d'avoir arrêté tout le monde
                JList.get(i).end();
            }
            endAllProducteurs();
            JList.get(id).end(); // s'arrête
        }
        catch (RemoteException re) { System.out.println(re) ; }

    }
    
    public void endAllProducteurs()
    {
        int i;
        try
        {
            for(i=0 ; i < PList.size() ; i++)
            {
                System.out.println("j'arrête le producteur : "+ i);
                PList.get(i).end();
            }
        }
        catch (RemoteException re) { System.out.println(re) ; }
    }
	
    // supprime le joueur d'id id
    public void deletePlayer(int id)
    {
        int i;
        //try
       // {
            FinishedPlayerList.add( JList.get(id));
            System.out.println("Le joueur " + id + " à gagné le jeu en " + FinishedPlayerList.size() + " place ");
            //~ 
            //~ for(i=0;i<JList.size();i++)
            //~ {
                //~ if( JList.get(i).getId() == id )
                //~ {    
                    //~ System.out.println("Je supprime le joueur d'id " + id);
                    //~ FinishedPlayerList.add( JList.get(i));
                    //~ JList.remove(i);
                    //~ break;
                //~ }
            //~ }
     //   }
     //   catch (RemoteException re) { System.out.println(re) ; }
        
    }
    
    // demande à tous les joueurs de supprimer le joueur d'id id
    public void deleteToAllPlayer(int id)
    {
        int i;
        System.out.println("Je donne l'ordre de supp de " +id + " taille de liste " + JList.size());
        try
        {
            for (i=0;i<JList.size();i++)
            {
                if(JList.get(i).getId( ) != id)
                {
                    JList.get(i).deletePlayer(id);
                }
            }
            System.out.println("Je dis à "+ id + " de supprimer " +id);
            deletePlayer(id);
        }
        catch (RemoteException re) { System.out.println(re) ; }
    }
}
