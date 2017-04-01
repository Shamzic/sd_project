import java.io.IOException;
import java.io.FileInputStream;

public class Parser {

	String nom;
	String nbjoueurs;
	String nbproducteurs;

	public Parser(String nom){
		this.nom=nom;
	}

	public int getNbProd(){
		return Integer.parseInt(this.nbproducteurs);
	}

	// Lecture d'un fichier octet par octet
	 public static void lecture_fichier(String nom){
	   	int lu = 0; // octet lu dans le fichier
	   	int i = 0;
	   	int j = 0;
	 	try {
			FileInputStream fis = new FileInputStream(nom);
			String nomvariable="";
			String valeurvariable;
			Boolean lecture_variable = false;
	    	while (lu != -1){
		    	lu = fis.read();
		    	if (lu != -1) 
		    	{
		    		System.out.print((char)lu);

		    		// On cherche les champs situé avant ':'
		    		if(((char)lu!=':')&&((char)lu!='\n')&&(lecture_variable==false))
						nomvariable+=(char)lu;

					if(((char)lu!=':')&&((char)lu!='\n')&&(lecture_variable==true)&&(nomvariable=="nbjoueurs"))
						this.nbjoueurs+=(char)lu;
					if(((char)lu!=':')&&((char)lu!='\n')&&(lecture_variable==true)&&(nomvariable=="nbproducteurs"))
						this.nbproducteurs+=(char)lu;
					if((char)lu==':')
					{
						lecture_variable=true;
					}
					if((char)lu=='\n')
					{
						nomvariable="";
						valeurvariable="";
					}
				}
			}
		    fis.close();
	   	} catch (IOException e) {
	   		// TODO Auto-generated catch block
	   		e.printStackTrace();
	  	}
	 
	}

}