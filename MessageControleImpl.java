import java.rmi.server.UnicastRemoteObject ;
import java.rmi.RemoteException ;

public class MessageControleImpl extends UnicastRemoteObject implements MessageControle
{
	public int IdProducteur = 0, IdJoueur = 0;
    public int nbRessourcesInitiales, nbRessourcesDifferentes;
    public SerializableList SList = new SerializableList();
    
    public MessageControleImpl(int nbRessourcesInitiales, int nbRessourcesDifferentes)
    throws RemoteException
    {
        this.nbRessourcesInitiales = nbRessourcesInitiales;
        this.nbRessourcesDifferentes = nbRessourcesDifferentes;
    }
    
    public TripleImpl getProdInitialInfo()
    throws RemoteException
    {
        TripleImpl T = new TripleImpl(IdProducteur++, nbRessourcesInitiales, nbRessourcesDifferentes);
        System.out.println("je donne les infos initiales");
        return new TripleImpl(IdProducteur++, nbRessourcesInitiales, nbRessourcesDifferentes);
    }
    
    public void addMachine( String MachineName, int port)
    throws RemoteException
    {
        SList.add(MachineName,port);
        System.out.println("J'ai ajouté le producteur " + MachineName + " port : " + port);
    }
    
    
    
    
    
    
    
    
    public int getIdProducteur()
    throws RemoteException
    {
        System.out.println("Give id " + IdProducteur );
        return IdProducteur ++;
    }
    
    
}

