
		try
		{
            // Fait une liste de ressource qu'il faut pour gagner
            SerializableList<Ressource> L = new SerializableList<Ressource>();
            L.add(new Ressource(0,0));
            L.add(new Ressource(50,1));
            L.add(new Ressource(0,2));

	    // Commence par faire l'objet grâce auquel le Controlleur communique avec les agents
            MessageControleImpl MC = new MessageControleImpl(5,3, nbProducteurs,nbJoueurs,"localhost",5000,0,L);
            Naming.rebind( "rmi://localhost:"+5000 +"/MessageControleGlobal", MC); 

            
		}
        catch (RemoteException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
	}

}
