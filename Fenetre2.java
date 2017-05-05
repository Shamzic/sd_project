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

import javax.swing.JRadioButton;

import java.rmi.RemoteException ;
import java.rmi.* ; 

import java.net.MalformedURLException ; 

import com.panayotis.gnuplot.JavaPlot;

public class Fenetre2 extends JFrame{
	int id_joueur;
	int nb_ressources_vol;
	int nb_ressource_prendre;
	int id_joueur_vol;
	int id_prod_prendre;
	TYPE ressource_vol;
	TYPE ressource_prendre;
	MessageControleImpl MC;
    JoueurImpl J;
    
    
	ArrayList<String> tabJoueurs;

    public Fenetre2(int id_joueur, MessageControleImpl MC){
    
    //javac -cp ./JavaPlot-0.5.0/dist/JavaPlot.jar *.java
    
    
    	
    	this.id_joueur=id_joueur;
    	this.setTitle("Joueur humain (id="+id_joueur);
    	this.setSize(1050, 300);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
        this.MC = MC;
    	JPanel content = new JPanel();
		JRadioButton BoutonVoler    = new JRadioButton();
		BoutonVoler.setText("Voler : ");
	  	JRadioButton BoutonPrendre   = new JRadioButton();
	  	BoutonPrendre.setText("Prendre : ");
	  	JRadioButton BoutonObserver  = new JRadioButton();
	  	BoutonObserver.setText("Observer ... Zzzzz");
	  	
	  	NumberFormat format = NumberFormat.getInstance();
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Integer.class);
		formatter.setMinimum(1);
		formatter.setMaximum(Integer.MAX_VALUE);
		formatter.setAllowsInvalid(false);
		
		final JFormattedTextField FormatterNb_ressources_vol = new JFormattedTextField(formatter);
		FormatterNb_ressources_vol.setValue(1);
		
		final JFormattedTextField FormatterNb_ressources_prendre = new JFormattedTextField(formatter);
		FormatterNb_ressources_prendre.setValue(1);
		
		formatter.setMinimum(0);
		final JFormattedTextField FormatterId_joueur_vol = new JFormattedTextField(formatter);
		FormatterId_joueur_vol.setValue(0);
		
		final JFormattedTextField FormatterId_prod_prendre = new JFormattedTextField(formatter);
		FormatterId_prod_prendre.setValue(0);
		
		
		String[] types = {"OR", "ARGENT", "BOIS"};
		final JComboBox<String> comboxRessTypes= new JComboBox<>(types);
    	final JComboBox<String> comboxRessTypes2= new JComboBox<>(types);
    	
    	content.setLayout(new GridBagLayout());
    	GridBagConstraints gc = new GridBagConstraints();
		// marge 
		gc.weightx = 0.5;
		gc.weighty = 0.5;
		// Première ligne
		
		gc.gridx=0;
		gc.gridy=0;
		content.add(new JLabel("Actions du joueur humain n°"+id_joueur),gc);
		
		// Deuxième ligne
		
		gc.gridx=0;
		gc.gridy=1;
		
		gc.fill = GridBagConstraints.HORIZONTAL;
		content.add(BoutonVoler,gc);

		gc.gridx=1;
		gc.gridy=1;
		content.add(FormatterNb_ressources_vol,gc);
		
		gc.gridx=2;
		gc.gridy=1;
		content.add(new JLabel(" ressources de type "),gc);
		
		gc.gridx=3;
		gc.gridy=1;
		content.add(comboxRessTypes,gc);
		
		gc.gridx=4;
		gc.gridy=1;
		content.add(new JLabel(" au joueur n° "),gc);
		
		gc.gridx=5;
		gc.gridy=1;
		content.add(FormatterId_joueur_vol,gc);
		
		// Troisième ligne
		
		gc.gridx=0;
		gc.gridy=2;
		content.add(BoutonPrendre,gc);
		
		gc.gridx=1;
		gc.gridy=2;
		content.add(FormatterNb_ressources_prendre,gc);
		
		gc.gridx=2;
		gc.gridy=2;
		content.add(new JLabel(" ressources de type "),gc);
		
		gc.gridx=3;
		gc.gridy=2;
		content.add(comboxRessTypes2,gc);
		
		gc.gridx=4;
		gc.gridy=2;
		content.add(new JLabel(" au producteur n° "),gc);
		
		gc.gridx=5;
		gc.gridy=2;
		content.add(FormatterId_prod_prendre,gc);
		
		// quatrième ligne
	
		gc.gridx=0;
		gc.gridy=3;
		content.add(BoutonObserver,gc);
	
    
        
		JButton BoutonPlay = new JButton("Jouer");
		gc.gridx=0;
        gc.gridy=4;
        content.add(BoutonPlay,gc);
	    
        // Créé l'instance du joueur
        try
        {
            InitialInfoImpl I = MC.getPlayerInitialInfo();
            // initialise le serveur joueur
            J = new JoueurImpl( I, String.valueOf(5000 + this.id_joueur+1), "humain");
            J.M = MC;
            Naming.rebind( "rmi://localhost:"+ String.valueOf(5000 + this.id_joueur+1) + "/Joueur", J);
            
            // Maintenant envoie ses "coordonnées" au Coordinateur
        //    System.out.println("je vais ajouter " + args[2] + "    "  + Integer.parseInt(args[3]));
            MC.addMachine( "localhost", Integer.parseInt(String.valueOf(5000 + this.id_joueur+1)) );
            
            
            Runnable r1 = new Runnable() 
            {
                public void run() 
                {
                    try{
                    J.start();
                    }catch (RemoteException re) { System.out.println(re) ; }
                }
            };
            
            
        } catch (RemoteException re) { System.out.println(re) ; }
            catch (MalformedURLException e) { System.out.println(e) ; }
        
        
        J.id = id_joueur; // met la bonne id au joueur
        
        // Joue le tour
		BoutonPlay.addActionListener(new ActionListener()  {
			public void actionPerformed(ActionEvent e)  {
                int nb, id;
                String type;
                if( J.have_token == false ) // pas à notre tour
                    return ;
                if( BoutonPrendre.isSelected() ) // Veut prendre des ressources
                {
                    String comport = (String) comboxRessTypes2.getSelectedItem();
                    System.out.println("Choix de Prendre "+ FormatterNb_ressources_prendre.getValue() +"des ressources de " + comport + " au producteur " + FormatterId_joueur_vol.getValue());
                    nb = (Integer)FormatterNb_ressources_prendre.getValue();
                    id = (Integer) FormatterId_prod_prendre.getValue();
                    type =(String)comport;
                    J.PrendRessources( nb, id,type);
                }
                else if( BoutonVoler.isSelected() ) // Veut prendre des ressources
                {
                    String comport = (String) comboxRessTypes.getSelectedItem();
                    System.out.println("Choix de Voler "+ FormatterNb_ressources_vol.getValue() +"des ressources de " + comport + " au joueur " + FormatterId_joueur_vol.getValue());
                    nb = (Integer)FormatterNb_ressources_vol.getValue();
                    id = (Integer) FormatterId_joueur_vol.getValue();
                    type =(String)comport;
                    J.VolRessources( nb, id,type);
                }
                else if ( BoutonObserver.isSelected())
                {
                    System.out.println("Choix de Observer");
                    
                    J.Observer();                    
                }
                
                
                System.out.println("ça fonctionne comme ça ");
                System.out.println("Il faut prendre " + FormatterNb_ressources_prendre.getValue());
                System.out.println("Il est checked " + BoutonVoler.isSelected());
            
            
            
            }
        });
        
        // Radio bouton pour chaque action
	    
	    // action voler
	    // suivit du nombre de ressources
	    // du type
	    // de l'id du joueur à voler
	    
	    // action prendre
	     // suivit du nombre de ressources
	    // du type
	    // de l'id du producteur à aller puiser
	    
	    // action observer
	    
	    
        
        
	    
    	
    	this.setContentPane(content);
    	this.setVisible(true);
    }
    
    
    
}
