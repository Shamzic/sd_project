import java.io.Serializable;


// classe dans laquelle sont mises toutes les informations initiales que les joueurs/producteurs nécessitent

public class InitialInfoImpl implements Serializable
{
    public int nbProducteurs, nbJoueurs; // nombre de joueurs et producteurs qu'on veut 
    public int victory_condition = 0; // victory condition == 0 : premier vainqueur arrêt du jeu ; 1 : enlève les joueurs les uns après les autres
    public int nbRessourcesInitiales, nbRessourcesDifferentes; 
    public String Name; // nom de la machine du contrôleur
    public int port; // port du rmiregistry du contrôleur
    public int IdJoueur = -1, IdProducteur = -1;
    public int playMode ; // 0 : tour par tour, 1 : dès qu'on peut
    SerializableList<Ressource> VLC ;
    
    public InitialInfoImpl( int nbRessourcesInitiales, int nbRessourcesDifferentes, String Name, int port, int nbProducteurs, int nbJoueurs, int victory_condition, SerializableList<Ressource> VLC, int playMode)
    {
        this.nbRessourcesInitiales = nbRessourcesInitiales;
        this.nbRessourcesDifferentes = nbRessourcesDifferentes;
        this.Name = Name;
        this.port = port;
        this.nbProducteurs = nbProducteurs;
        this.nbJoueurs = nbJoueurs;
        this.victory_condition = victory_condition;
        this.VLC = VLC;
        this.playMode = playMode;
    }
    
    public int getStockQuantity(TYPE T)
    {
        int i;
        for(i = 0 ; i< VLC.size() ;i++)
        {
            if( VLC.get(i).getStockType() == T)
                return VLC.get(i).getStock();
        }
        return 0;
    }
}
