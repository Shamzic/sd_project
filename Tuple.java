import java.io.Serializable;

public class Tuple implements Serializable
    {
        public static final long serialVersionUID = 1L; // Utilie uniquement pour régler les warning de serial
        String MN ;
        int port;
        //~ 
        //~ int nbRessourcesInitiales;
        //~ int nbRessourcesDifferentes;
        Tuple(String MachineName, int port)
        {
            this.MN = MachineName;
            this.port = port;
        }
        //~ 
        //~ Tuple(int nbRessourcesInitiales, int nbRessourcesDifferentes)
        //~ {
            //~ this.nbRessourcesInitiales = nbRessourcesInitiales;
            //~ this.nbRessourcesDifferentes = nbRessourcesDifferentes;
        //~ }
    }
