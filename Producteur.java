import java.util.ArrayList;

class Producteur extends Agent
{
    public ArrayList<Ressource> RList;
	Producteur (int id, int RI, int RD)
	{
		super(id);
        int i;
        RList = new ArrayList<Ressource> (RD);
        for (i=0; i< RD; i++)
            RList.add(i, new Ressource(RI));
        
        
	}
	
}
