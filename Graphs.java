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
	
	String cmd_plot1 = "";
	String cmd_plot2 = "";
	String cmd_plot3 = "";
	
	ressource1.setTitle("Evolution de la ressource ARGENT des joueurs en fonction des tours ou du temps selon le mode de jeu");
	ressource1.set("xlabel","'Tour ou millisecondes selon votre mode de jeu'");
	ressource1.set("ylabel","'Nombre de ressources'");
	
	ressource2.setTitle("Evolution de la ressource OR des joueurs en fonction des tours ou du temps selon le mode de jeu");
	ressource2.set("xlabel","'Tour ou millisecondes selon votre mode de jeu'");
	ressource2.set("ylabel","'Nombre de ressources'");
	
	ressource3.setTitle("Evolution de la ressource BOIS des joueurs en fonction des tours ou du temps selon le mode de jeu");
	ressource3.set("xlabel","'Tour ou millisecondes selon votre mode de jeu'");
	ressource3.set("ylabel","'Nombre de ressources'");
	
	//cmd_plot1="actionLog.dat" using 1:3 every $0::2 with line lc rgb"green" title "Ressource 1 joueur 2",\ "actionLog.dat" using 1:4 every $0::2 with line lc rgb"blue" title "Ressource 2 joueur 2",\ "actionLog.dat" using 1:5 every $0::2 with line lc rgb"red" title "Ressource 3 joueur 2";
	
	int i;
	for(i=0;i<nbJoueurs-1;i++)
	{
		String is = String.valueOf(i);
		cmd_plot1+="\"actionLog.dat\" using 1:3 every 3::"+is+" title \"joueur "+is+"\" with linespoints, ";
	}
	String is = String.valueOf(i);
	cmd_plot1+="\"actionLog.dat\" using 1:3 every 3::"+is+" title \"joueur "+is+"\" with linespoints\n";
	System.out.println(cmd_plot1);
	
	for(i=0;i<nbJoueurs-1;i++)
	{
		String is2 = String.valueOf(i);
		cmd_plot2+="\"actionLog.dat\" using 1:4 every 3::"+is2+" title \"joueur "+is2+"\" with linespoints, ";
	}
	String is2 = String.valueOf(i);
	cmd_plot2+="\"actionLog.dat\" using 1:4 every 3::"+is2+" title \"joueur "+is2+"\" with linespoints\n";
	System.out.println(cmd_plot2);
	
	for(i=0;i<nbJoueurs-1;i++)
	{
		String is3 = String.valueOf(i);
		cmd_plot3+="\"actionLog.dat\" using 1:5 every 3::"+is3+" title \"joueur "+is3+"\" with linespoints, ";
	}
	String is3 = String.valueOf(i);
	cmd_plot3+="\"actionLog.dat\" using 1:5 every 3::"+is3+" title \"joueur "+is3+"\" with linespoints\n";
	System.out.println(cmd_plot3);
	
	
    ressource1.addPlot(cmd_plot1);
    ressource2.addPlot(cmd_plot2);
    ressource3.addPlot(cmd_plot3);
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
