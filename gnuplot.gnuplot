set terminal jpeg
set output "Graphique_fin.jpg"
set title ' Evolution des ressources des joueurs en fonction des tours : '
set xlabel ' Tour '
set ylabel ' Nombre de ressources '
set key on outside left bmargin box title 'Legende'
plot "actionLog.dat" using 1:3 every $0::2 with line lc rgb"green" title "Ressource 1 joueur 2",\
     "actionLog.dat" using 1:4 every $0::2 with line lc rgb"blue" title "Ressource 2 joueur 2",\
     "actionLog.dat" using 1:5 every $0::2 with line lc rgb"red" title "Ressource 3 joueur 2"
		
# affiche graphique de la ressource de la 4e colonne du joueur 2
# soit la ressource 2 car première colonne = numéro tour
# et deuxième colonne = numéro joueur
# every 3 car il y a trois joueurs. Comment paramétrer pour X joueurs ?




# tuto lancer avec argument = nb joueurs :

# >gnuplot 
# gnuplot> call "gnuplot.gnuplot" 3 
