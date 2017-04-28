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

public class Fenetre extends JFrame{
    int nbJoueurs = 0;
    int nbProducteurs = 0;

    public void afficherJoueurs(){
	 System.out.println("Nombre de joueurs : "+nbJoueurs);
    }

    public void setJoueurs(int nbJoueurs){
	 this.nbJoueurs=nbJoueurs;
    }

	public  class TraitementB1 implements ActionListener
	{
            public  void actionPerformed(ActionEvent e)
			{
				afficherJoueurs();
			    //System.out.println("Nombre de joueurs : "+Fenetre.nbjoueurs);
			}
	}
    public  class TraitementF1 implements ActionListener
	{
	    public void actionPerformed(ActionEvent actionEvent) 
		{
		    JFormattedTextField source = (JFormattedTextField) actionEvent.getSource();
		    Integer value = (Integer)source.getValue();
		    System.out.println("Value: " + value);
			setJoueurs((Integer)source.getValue());
			//System.out.println("nombre de joueurs dans le champ : "+FormatterJoueurs.toString());
			// MARCHE PAAAS		
		}
	}

  public Fenetre(){
	this.setTitle("Age of Agents");
    this.setSize(600, 100);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLocationRelativeTo(null);
	NumberFormat format = NumberFormat.getInstance();
    NumberFormatter formatter = new NumberFormatter(format);
    formatter.setValueClass(Integer.class);
    formatter.setMinimum(0);
    formatter.setMaximum(Integer.MAX_VALUE);
    formatter.setAllowsInvalid(false);
    JFormattedTextField FormatterJoueurs =new JFormattedTextField(formatter);
    JFormattedTextField FormatterProducteurs =new JFormattedTextField(formatter);


    
    JButton BoutonJoueurs = new JButton("OK");
    JButton BoutonProducteurs = new JButton("OK");
    JButton BoutonStart = new JButton("Lancer le jeu");

    this.setLayout(new GridLayout(3, 3));

    //On ajoute le bouton au content pane de la JFrame
    this.getContentPane().add(new JLabel("Nombre de joueurs"));
    this.getContentPane().add(FormatterJoueurs);
    this.getContentPane().add(BoutonJoueurs);

    this.getContentPane().add(new JLabel("Nombre de producteurs"));
    this.getContentPane().add(FormatterProducteurs);
    this.getContentPane().add(BoutonProducteurs);

    this.getContentPane().add(BoutonStart);

  FormatterJoueurs.addActionListener(new TraitementF1());
  //FormatterProducteurs.addActionListener(actionListenerForm);
  BoutonJoueurs.addActionListener(new TraitementB1());

    this.setVisible(true);

  }

	

}
