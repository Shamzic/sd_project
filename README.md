D'abord aller dans le dossier où sont les programmes et lancer : 
rmiregistry 5000 &
rmiregistry 5001 &
rmiregistry 5002 &

ensuite lancer dans des terminaux différents :
java Mainexemple

java ProducteurMain localhost 5000 lalhost 5001

java JoueurMain localhost 5000 localst 5002




# sd_project

Corrigé des TPs + sujet projet : https://dpt-info.u-strasbg.fr/~g.frey/SD/

Diagramme UML draw.io : https://drive.google.com/file/d/0B0OIKQIdHBm8Qi16NDBucFFaakE/view?usp=sharing

# Petites infos précisées en TP :

Pour rappel c'est de la prog distribuée donc il faut faire des processus indépendants.
Si un agent demande un accès aux ressources d'un producteur, c'est la méthode du producteur qui est appelée.

Interface graphique possible à faire avec Eclipse par exemple.

On a un producteur de ressources avec 15 ressources

Un objectif peut etre d'atteindre 100 ressources.

Au cours du temps, on ajoute n ressources.

Il peut y avoir des vols entre joueurs.

Pour définir stratégies, les agents peuvent observer ce qu'il se passe.

Si il y a plus beaucoup de ressources, ça produit plus grand chose, si il y en a beaucoup ça en produit beaucoup.

Quel agent a finit très rapidement, ou quel est le temps pour que tout le monde finisse ?
Lorsque la partie est terminée, on a le nombre de ressources de chacun, avec le temps.

On définit si l'agent prendre des ressources, si il n'y en a pas, il attends.

On peut avoir plusieurs règles de fin, si un agent a fini, on regarde l'état des autres agents.

Accès exclusif aux producteurs de ressources.

Production possible pour un agent : la moitié des ressources +1 toutes les 50 ms.

Plusieurs personnalités pour les joueurs : individualiste ou coopératif (en binôme, il serait bien d'en faire d'autres)

Les différentes actions sont envoyées à un coordinateur (une sorte de log, pour avoir les infos sur ce qui est utilisé)

Ce coordinateur sert aussi à démarrer la partie. 

Un joueur peut soit observer des ressources, soit observer des Agents.

File d'attente pour les producteurs car accès exclusif, cela peut être géré au niveau de l'architecture, mais les demandes d'accès au producteur vont arriver les unes après les autres.

Exemple pour gagner partie : 100 ressource de la ressources 1 et 50 de la ressource 2.

# Rôles

-Coopératif
-Coopératif stratège
-Individualiste
-Individualiste voleur
-Voleur
-Traître
-Rancunier

# initialisation

Pour lancer une partie, on peut le faire ne ligne de commande, on stocke tout dans un fichier qu'on peut utiliser pour l'interface graphique.

Un jeu avec tant de joueurs, tant de ressources max, tant de producteurs qui vont produire tant de ressources.
Definir les objectifs des joueurs. Les ressources épuisables ou non.
On peut faire un fichier init pour stocker tous les paramètres dedans.


# Deroulement de la partie

Le coordinateur permet aux joueurs d'accéder aux ressources des producteurs. 
Il faut enregistrer CHAQUE opération dans un fichier texte par exemple dès qu'un joueur a finit son action.
Dans un premier temps on peut l'afficher dans le terminal pour commencer sans interface graphique.

# Fin de la partie

La visualisation de l'interface se fait à posteriori une fois que la partie est terminée on affiche les noeuds des joueurs.
Il existe des libraires de représentation des graphes (on fait simplement dix noeuds par exemple et ça les dispose automatiquement).
Il ne faut pas passer trop de temps sur la visualisation.

L'interface avec des graphes a l'air plus simple à réaliser affichant simplement un graphe du nombre de ressources des joueurs et des producteurs en fonction du temps.



