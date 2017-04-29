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

public class Fenetre extends JFrame{
	int nbJoueurs = 0;
    int nbProducteurs = 1;
	ArrayList<String> tabJoueurs;

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
		//comboxJoueur.setSelectedIndex(4);

		final JFormattedTextField FormatterOr = new JFormattedTextField(formatter);

		final JFormattedTextField FormatterBois = new JFormattedTextField(formatter);
		final JFormattedTextField FormatterArgent = new JFormattedTextField(formatter);
		JButton BoutonAdd = new JButton("Ajouter joueur");
		JButton BoutonStart = new JButton("Lancer le jeu");
		JButton BoutonReset = new JButton("Reset valeurs");
		
		
		
		final JTextArea textArea = new JTextArea();

		//content.setPreferredSize(new Dimension(300, 120));
		
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
		
		content.add(new JLabel("Or initial : "),gc);
		gc.gridx=1;
		gc.gridy=2;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.fill = GridBagConstraints.HORIZONTAL;
		content.add(FormatterOr,gc);


		// Quatrième ligne
		gc.gridx=0;
		gc.gridy=3;
		gc.anchor = GridBagConstraints.LINE_START;
		content.add(new JLabel("Bois initial : "),gc);
		gc.gridx=1;
		gc.gridy=3;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.fill = GridBagConstraints.HORIZONTAL;
		content.add(FormatterBois,gc);

		// Cinquième ligne
		gc.gridx=0;
		gc.gridy=4;
		gc.anchor = GridBagConstraints.LINE_START;
		content.add(new JLabel("Argent initial : "),gc);
		gc.gridx=1;
		gc.gridy=4;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.fill = GridBagConstraints.HORIZONTAL;
		content.add(FormatterArgent,gc);


		//On ajoute le bouton au content pane de la JFrame
/*
		this.getContentPane().add(comboxJoueur);
		this.getContentPane().add(BoutonAdd);
		this.getContentPane().add(new JLabel("Nombre de producteurs"));
		this.getContentPane().add(FormatterProducteurs);
		this.getContentPane().add(new JLabel(""));

		this.getContentPane().add(new JLabel("Nombre de ressources d'or"));
		this.getContentPane().add(FormatterOr);
		this.getContentPane().add(new JLabel(""));
		this.getContentPane().add(new JLabel("Nombre de ressources de bois"));
		this.getContentPane().add(FormatterBois);
		this.getContentPane().add(new JLabel(""));
		this.getContentPane().add(new JLabel("Nombre de ressources d'argent"));
		this.getContentPane().add(FormatterArgent);
		this.getContentPane().add(new JLabel(""));
		this.getContentPane().add(BoutonStart);
		this.getContentPane().add(BoutonReset);
		this.getContentPane().add(new JLabel(""));
*/

		BoutonStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nbJoueurs = getJoueurs();
				nbProducteurs = (Integer) FormatterProducteurs.getValue();
				afficherJoueurs();
			}
		});

		BoutonReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setJoueurs(0);
				setProducteurs(1);
				FormatterProducteurs.setValue(1);
				tabJoueurs.clear();
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

    public void setJoueurs(int nbJoueurs){
	 	this.nbJoueurs=nbJoueurs;
    }

    public int getJoueurs(){
	 	return nbJoueurs;
    }

	public void setProducteurs(int nbProducteurs){
	 	this.nbProducteurs=nbProducteurs;
    }

	public int getProducteurs(){
	 	return nbProducteurs;
    }
}
