import java.io.IOException;
import java.io.FileInputStream;
/*
	Cette classe sert à parser le fichier configuration
	Les nombres de joueurs et de producteurs sont à indiquer 
	dans le fichier configuration après le signe "="
*/

public class Parser {

	private String nom;
	static String nbjoueurs="";
	static String nbproducteurs="";

	public Parser(){
		;
	}

	public String getNom(){
		return this.nom;
	}
	
	public String getNbProducteurs(){
		return nbproducteurs;
	}

	public String getNbJoueurs(){
		return nbjoueurs;
	}

	public void concat2string(String a,String b){
		a=a+b;
	}

	// Lecture d'un fichier octet par octet
	 public void lecture_fichier(String nom){
	   	int lu = 0; // octet lu dans le fichier
	 	try {
			FileInputStream fis = new FileInputStream(nom);
			String nomvariable="";
			//String valeurvariable;
			Boolean lecture_variable = false;
	    	while (lu != -1){
		    	lu = fis.read();
		    	if (lu != -1) 
		    	{
		    		//System.out.print((char)lu);

		    		if(((char)lu!='=')&&(lecture_variable==false)&&(char)lu!='\n')
						nomvariable+=(char)lu;
					if(((char)lu!='=')&&(lecture_variable==true)&&(nomvariable.equals("nbjoueurs"))&&(char)lu!='\n')
					{
						nbjoueurs+=(char)lu;
					}

					if(((char)lu!='=')&&(lecture_variable==true)&&(nomvariable.equals("nbproducteurs"))&&(char)lu!='\n')
					{
						nbproducteurs+=(char)lu;
					}

					if((char)lu=='=')
					{
						lecture_variable=true;
					}
					if((char)lu=='\n')
					{
						nomvariable="";
						lecture_variable=false;
					}
					
					//System.out.println(" nomvariable : "+nomvariable);
					//System.out.println("nbjoueurs : "+nbjoueurs1);
					//System.out.println("nbproducteurs : "+nbproducteurs1);
				}
			}
		    fis.close();
	   	} catch (IOException e) {
	   		// TODO Auto-generated catch block
	   		e.printStackTrace();
	  	}
	 
	}

}
