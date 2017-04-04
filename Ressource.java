
/* TRES PROBABLEMENT PAS COMME CA QU'ON FERA CAR C'EST LA MÉTHODE DU PROD QUI EST APPELÉE
 * Sert à partager les ressources entre les agents 
 * 1 copie synchronisée avec les autres et une copie locale pouvant être consultée en permanence
 * 
*/
public class Ressource
{
    int nombre;
    Ressource (int nombreInit)
    {
        nombre = nombreInit;
    }
    
    int getRessource()
    {
        return nombre;
    }
    
    
}
