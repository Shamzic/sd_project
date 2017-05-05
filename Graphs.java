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
	
	ressource1.setTitle("Evolution des ressources des joueurs en fonction des tours");

	ressource1.set("xlabel","'Tour ou millisecondes selon votre mode de jeu'");
	ressource1.set("ylabel","'Nombre de ressources'");
	ressource1.set("key","on outside left bmargin box title 'Légende'");
	
    ressource1.addPlot("sin(x)");
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
