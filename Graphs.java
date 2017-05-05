import com.panayotis.gnuplot.JavaPlot;


/* 
Compilation : javac -cp ./JavaPlot-0.5.0/dist/JavaPlot.jar *.java
Execution  : java -cp ./JavaPlot-0.5.0/dist/JavaPlot.jar:. Test 
*/
 
public class Graphs {

	int nbJoueurs;

	public Graphs (int nbJoueurs){
	
	System.out.println("Il y avait ... "+nbJoueurs+" joueurs...");
	
	
	this.nbJoueurs=nbJoueurs;
	
	JavaPlot ressource1 = new JavaPlot();
	JavaPlot ressource2 = new JavaPlot();
	JavaPlot ressource3 = new JavaPlot();
	
	String cmd_plot1 = "sin(x)";
	
	ressource1.setTitle("Evolution de l or des joueurs en fonction des tours");

	ressource1.set("xlabel","'Tour ou millisecondes selon votre mode de jeu'");
	ressource1.set("ylabel","'Nombre de ressources'");
	ressource1.set("key","on outside left bmargin box title 'Légende'");
	
	//cmd_plot1="actionLog.dat" using 1:3 every $0::2 with line lc rgb"green" title "Ressource 1 joueur 2",\ "actionLog.dat" using 1:4 every $0::2 with line lc rgb"blue" title "Ressource 2 joueur 2",\ "actionLog.dat" using 1:5 every $0::2 with line lc rgb"red" title "Ressource 3 joueur 2";
	
    ressource1.addPlot(cmd_plot1);
    ressource2.addPlot("");
    ressource3.addPlot("");
    ressource1.plot();
    ressource2.plot();
    ressource3.plot();
    
	}
	
	
	
	
	/*
    public static void main(String[] args) {
        JavaPlot p = new JavaPlot();
        p.addPlot("sin(x)");
        p.plot();
    }*/
}
