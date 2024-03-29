import java.awt.GridLayout; 
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

 import javax.swing.JTextField;
import javax.swing.JFormattedTextField;
import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import java.util.ArrayList;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.Dimension;
import javax.swing.JPanel;

import javax.swing.JTextArea;

import java.awt.Insets;

import java.util.ArrayList;
import java.rmi.server.UnicastRemoteObject ;
import java.rmi.RemoteException ;
import java.rmi.* ; 
import java.net.MalformedURLException ; 
import java.lang.*;
import java.io.IOException;
import java.io.*;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;

public class Fenetre extends JFrame{
	int nbJoueurs = 0;
    int nbProducteurs = 1;
	ArrayList<String> tabJoueurs;
	boolean start =false;
	int orWin=5;
	int argentWin=5;
	int boisWin=5;
	int Tps_max=20;
	int ModeDeJeu=0; // 0 = tour par tour, 1 = temps réel
    MessageControleImpl MC;

    public Fenetre() throws IOException, InterruptedException {
		JPanel content = new JPanel();
		tabJoueurs = new ArrayList<String>();

		this.setTitle("Age of Agents");
		this.setSize(1050, 300);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		NumberFormat format = NumberFormat.getInstance();
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Integer.class);
		formatter.setMinimum(0);
		formatter.setMaximum(Integer.MAX_VALUE);
		formatter.setAllowsInvalid(false);
		
		final JFormattedTextField FormatterProducteurs = new JFormattedTextField(formatter);
		FormatterProducteurs.setValue(1);

		String[] comportements = {"cooperatif", "individualiste", "voleur","attentionnel","brute","humain"};
		final JComboBox<String> comboxJoueur= new JComboBox<>(comportements);

		NumberFormatter formatterRessources = new NumberFormatter(format);
		formatterRessources.setMinimum(5);
		formatterRessources.setMaximum(Integer.MAX_VALUE);
		formatterRessources.setAllowsInvalid(false);
		
		final JFormattedTextField FormatterOr = new JFormattedTextField(formatterRessources);
		FormatterOr.setValue(5);
		final JFormattedTextField FormatterBois = new JFormattedTextField(formatterRessources);
		FormatterBois.setValue(5);
		final JFormattedTextField FormatterArgent = new JFormattedTextField(formatterRessources);
		FormatterArgent.setValue(5);
		NumberFormatter formatterTemps = new NumberFormatter(format);
		formatterTemps.setMinimum(20);
		formatterTemps.setMaximum(Integer.MAX_VALUE);
		formatterTemps.setAllowsInvalid(false);
		
		final JFormattedTextField FormatterTempsMax = new JFormattedTextField(formatterTemps);
		FormatterTempsMax.setValue(20);
		JButton BoutonAdd = new JButton("Ajouter joueur");
		JButton BoutonStart = new JButton("Lancer le jeu");
		JButton BoutonReset = new JButton("Reset valeurs");
		JButton BoutonGraph = new JButton("Afficher les graphiques");
		
		
		String[] modes = {"Tour par tour","Temps réel"};
		final JComboBox<String> comboxMode= new JComboBox<>(modes);
		
		
		final JTextArea textArea = new JTextArea();

		
		content.setLayout(new GridBagLayout());
		

		GridBagConstraints gc = new GridBagConstraints();
		// marge 
		gc.weightx = 0.5;
		gc.weighty = 0.5;
		// Première ligne
		
		gc.gridx=0;
		gc.gridy=0;
		gc.anchor = GridBagConstraints.LINE_START;
		content.add(new JLabel("Ajouter un joueur : "),gc);
		gc.gridx=1;
		gc.gridy=0;
		gc.anchor = GridBagConstraints.CENTER;
		content.add(comboxJoueur,gc);
		gc.gridx=2;
		gc.gridy=0;
		content.add(BoutonAdd,gc);
		gc.gridx=3;
		gc.gridy=0;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		content.add(textArea,gc);

		// Deuxième ligne
		gc.gridx=0;
		gc.gridy=1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		content.add(new JLabel("Nombre producteurs:"),gc);
		gc.gridx=1;
		gc.gridy=1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		content.add(FormatterProducteurs,gc);
		// Troisième ligne
		gc.gridx=0;
		gc.gridy=2;
		gc.anchor = GridBagConstraints.LINE_START;
		
		content.add(new JLabel("Or pour la victoire : "),gc);
		gc.gridx=1;
		gc.gridy=2;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.fill = GridBagConstraints.HORIZONTAL;
		content.add(FormatterOr,gc);


		// Quatrième ligne
		gc.gridx=0;
		gc.gridy=3;
		gc.anchor = GridBagConstraints.LINE_START;
		content.add(new JLabel("Bois pour la victoire : "),gc);
		gc.gridx=1;
		gc.gridy=3;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.fill = GridBagConstraints.HORIZONTAL;
		content.add(FormatterBois,gc);

		// Cinquième ligne
		gc.gridx=0;
		gc.gridy=4;
		gc.anchor = GridBagConstraints.LINE_START;
		content.add(new JLabel("Argent pour la victoire : "),gc);
		gc.gridx=1;
		gc.gridy=4;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.fill = GridBagConstraints.HORIZONTAL;
		content.add(FormatterArgent,gc);
		// Sixième ligne
		gc.gridx=0;
		gc.gridy=5;
		gc.anchor = GridBagConstraints.LINE_START;
		content.add(new JLabel("Temps max : "),gc);
		gc.gridx=1;
		gc.gridy=5;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.fill = GridBagConstraints.HORIZONTAL;
		content.add(FormatterTempsMax,gc);
		
		// Septième ligne
		gc.gridx=0;
		gc.gridy=6;
		gc.anchor = GridBagConstraints.LINE_START;
		content.add(new JLabel("Mode de jeu : "),gc);
		gc.gridx=1;
		gc.gridy=6;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.fill = GridBagConstraints.HORIZONTAL;
		content.add(comboxMode,gc);
		
		// Huitième ligne
		gc.insets = new Insets(10,10,10,10);
		gc.gridwidth=2; 
		gc.gridx=0;
		gc.gridy=7;
		gc.anchor = GridBagConstraints.LINE_START;
		content.add(BoutonStart,gc);
		gc.gridwidth=1; 
		gc.gridx=2;
		gc.gridy=7;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		content.add(BoutonReset,gc);
		
		gc.gridwidth=4; 
		gc.gridx=0;
		gc.gridy=8;
		gc.anchor = GridBagConstraints.LINE_START;
		content.add(BoutonGraph,gc);

		// Lance le jeu depuis le bouton start
		BoutonStart.addActionListener(new ActionListener()  {
			public void actionPerformed(ActionEvent e)  {
				nbJoueurs = getJoueurs();
				nbProducteurs = (Integer) FormatterProducteurs.getValue();
				afficherJoueurs();
				
				int nbj = 0;
				for(nbj =0; nbj < nbJoueurs ; nbj++)
				{
					if(tabJoueurs.get(nbj)=="humain")
						System.out.println("ON A UN HUMAIN !!!");
				}
				if(nbj<1) 
				{
					System.out.println("Il faut au moins un joueur pour lancer la partie !");
					JOptionPane.showMessageDialog(null, "Il faut ajouter un joueur minimum pour lancer la partie !","Erreur lancement" , JOptionPane.ERROR_MESSAGE);
				}	
				else
				{
				
					/* Lancement des ports rmiregistry */
				
					try
						{
							System.out.println("pkill ? ");			
							Runtime runtime = Runtime.getRuntime();
							runtime.exec(new String[] {"pkill","rmiregistry"});
							try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException re) { System.out.println(re) ; }
							runtime.exec(new String[] {"rmiregistry","5000"} );
							System.out.println("pkill ... ");
							for(int i = 0 ; i < nbProducteurs ; i++)
							{
								int port_producteur = 5021+i;
								runtime.exec(new String[] { "rmiregistry",""+port_producteur,"&"} );
							}
							for(int i = 0 ; i < nbProducteurs ; i++)
							{
								int port_producteur = 5021+i;
								runtime.exec(new String[] { "rmiregistry",""+port_producteur,"&"} );
							}
							for(int i = 0 ; i < nbJoueurs ; i++)
							{
								int port_joueur = 5001+i;
								runtime.exec(new String[] { "rmiregistry",""+port_joueur,"&"} );
							}
						}
					catch(IOException exc){ System.out.println(exc) ; }
				
					try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException re) { System.out.println(re) ; }
					/* Lancement controlleur */
				
						try
						{
							// Fait une liste de ressource qu'il faut pour gagner
							
							orWin = (Integer) FormatterOr.getValue();
							argentWin = (Integer) FormatterArgent.getValue();
							boisWin = (Integer) FormatterBois.getValue();
													
							SerializableList<Ressource> L = new SerializableList<Ressource>();
							L.add(new Ressource(argentWin,0)); // Argent
							L.add(new Ressource(orWin,1)); // Or 
							L.add(new Ressource(boisWin,2)); // Bois

							// Commence par faire l'objet grâce auquel le Controlleur communique avec les agents
                            System.out.println("Mode de jeu "+ ModeDeJeu + " .Il faut " + argentWin + " et " + orWin+"  et " + boisWin);

							String mj = (String) comboxMode.getSelectedItem();
							if(mj=="Tour par tour")
								ModeDeJeu=0;
							else 
								ModeDeJeu=1;
								
							//System.out.println("Mode de jeu activé : "+ModeDeJeu);
							//System.out.println("OR VICTOIRE : "+orWin);
							  
							Tps_max =(Integer) FormatterTempsMax.getValue();
							MC = new MessageControleImpl(5,3,nbProducteurs,nbJoueurs,"localhost",5000,0,L,ModeDeJeu, Tps_max ); 
							Naming.rebind( "rmi://localhost:"+5000 +"/MessageControleGlobal", MC); 
						}
						catch (RemoteException re) { System.out.println(re) ; }
						catch (MalformedURLException excep) { System.out.println(excep) ; }
					
						 try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException re) { System.out.println(re) ; }
						 
						/* Lancement des producteurs */
				
						try
							{
								for(int i = 0 ; i < nbProducteurs ; i++)
								{
									Runtime runtime = Runtime.getRuntime();
									String titre_terminal = "\"Producteur n°"+i+"\"";
									int port_producteur = 5021+i;
									String commande_lancement_producteur = "java ProducteurImpl localhost 5000 localhost "+port_producteur+"; $SHELL"; 
									runtime.exec(new String[] { "xterm", "-T", titre_terminal,"-e",commande_lancement_producteur} );
								}
							}
						catch(IOException exc){
		  					System.out.println(exc) ;
						}
						 try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException re) { System.out.println(re) ; }
						/* Lancement des joueurs */
						try
							{
								for(int i = 0 ; i < nbJoueurs ; i++)
								{
		                            try{Thread.sleep(1000); } catch   (InterruptedException re) { System.out.println(re) ; }
		                            if(tabJoueurs.get(i)=="humain")
		                            {
										Fenetre2 f2 = new Fenetre2(i,MC);
										f2.setVisible(true);
		                                continue;
									}
		                            Runtime runtime = Runtime.getRuntime();
									String titre_terminal = "\"Joueur n°"+i+"\"";
									int port_joueur = 5001+i;
		                            System.out.println(tabJoueurs.get(i));
									String commande_lancement_joueur = "java JoueurImpl localhost 5000 localhost "+port_joueur+" "+tabJoueurs.get(i)+"; $SHELL"; 
									runtime.exec(new String[] { "xterm", "-T", titre_terminal,"-e",commande_lancement_joueur} );

								}
							}
						catch(IOException exc){
		  					System.out.println(exc) ;
					}
				}
			}
		});
		
		// Remet les paramètre de jeu à 0
		BoutonReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setJoueurs(0);
				setProducteurs(1);
				FormatterProducteurs.setValue(1);
				tabJoueurs.clear();
				textArea.setText("");
				comboxMode.setEnabled(true);
			}
		});

		BoutonAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String comport = (String) comboxJoueur.getSelectedItem();
				tabJoueurs.add(comport);
				setJoueurs(getJoueurs()+1);
				textArea.setText("");
				textArea.append("Nombre de joueurs : "+nbJoueurs+"\n");
				int i=0;
				while(i<tabJoueurs.size())
				{
					textArea.append("Joueur "+i+" "+tabJoueurs.get(i)+"\n");
					i++;
				}
				
				if(comboxJoueur.getSelectedItem()=="Humain")
				{
					comboxMode.setSelectedItem("Tour par tour");
					ModeDeJeu=0;
					comboxMode.setEnabled(false);
				}
			}
		});
		
		BoutonGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Graphs(getJoueurs());
				
				
			}
		});


		this.setContentPane(content);
    	this.setVisible(true);
  	}

	public void afficherJoueurs(){
	 	System.out.println("Nombre de joueurs : "+nbJoueurs);
		int i=0;
		while(i<tabJoueurs.size())
		{
			System.out.println("Joueur "+i+" "+tabJoueurs.get(i));
			i++;
		}
    }

	public void afficherProducteurs(){
	 	System.out.println("Nombre de joueurs : "+nbJoueurs);
    }


	// Setteurs
	
    public void setJoueurs(int nbJoueurs){
	 	this.nbJoueurs=nbJoueurs;
    }


	public void setProducteurs(int nbProducteurs){
	 	this.nbProducteurs=nbProducteurs;
    }
    
	public void setOrWin(int orWin){
	 	this.orWin=orWin;
    }
    
    public void setArgentWin(int argentWin){
	 	this.argentWin=argentWin;
    }
    public void setBoisWin(int boisWin){
	 	this.boisWin=boisWin;
    }    
    
    // Getteurs
    
    public int getJoueurs(){
	 	return this.nbJoueurs;
    }

	public int getProducteurs(){
	 	return this.nbProducteurs;
    }

	public int getOrWin(){
	 	return this.orWin;
    }
    
    public int getArgentWin(){
	 	return this.argentWin;
    }
    public int getBoisWin(){
	 	return this.boisWin;
    } 
    
}


