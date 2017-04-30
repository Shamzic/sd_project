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

    public Fenetre(){
		JPanel content = new JPanel();
		tabJoueurs = new ArrayList<String>();

		this.setTitle("Age of Agents");
		this.setSize(700, 200);
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

		String[] comportements = {"Coopératif", "Individualiste", "Voleur", "Humain"};
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


		BoutonStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nbJoueurs = getJoueurs();
				nbProducteurs = (Integer) FormatterProducteurs.getValue();
				afficherJoueurs();

				/* Lancement controlleur */
				
					try
					{
						// Fait une liste de ressource qu'il faut pour gagner
						
						SerializableList<Ressource> L = new SerializableList<Ressource>();
						L.add(new Ressource(argentWin/10,0)); // Argent
						L.add(new Ressource(orWin/10,1)); // Or 
						L.add(new Ressource(boisWin/10,2)); // Bois

					// Commence par faire l'objet grâce auquel le Controlleur communique avec les agents
						MessageControleImpl MC = new MessageControleImpl(5,3,nbProducteurs,nbJoueurs,"localhost",5000,0,L,ModeDeJeu);
						Naming.rebind( "rmi://localhost:"+5000 +"/MessageControleGlobal", MC); 
					}
					catch (RemoteException re) { System.out.println(re) ; }
					catch (MalformedURLException excep) { System.out.println(excep) ; }
				}

			
			
		});
		

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


