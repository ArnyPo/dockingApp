# DOCKING APP
Arnošt Polák  
Java - Pokročilá 2023/24


## Uživatelská příručka

### Potřebný software
Pro správné fungování apliakce je nutné mít nainstalovaný [AutoDock Vina](https://vina.scripps.edu/) a 
[OpenBabel](https://openbabel.org/index.html). Distribuce [P2Rank](http://siret.ms.mff.cuni.cz/p2rank) 
je součástí programu.

### JMol
V aplikaci je vždy napravo přítomný panel s vizualiazcí vybrané struktury. Pod ní je prostor na vložení 
Jmol příkazů (skriptovací jazyk Jmol je podobný skriptovacímu jazyku, který používá PyMol). Tlačítko 
Execute pak vyhodnotí příkaz. JMol automaticky veškeré chybové hlášky posílá na stderr.  

Skrze horní lištu je možné vizualizovat výsledky předpovědí vyzebných míst programu P2Rank. Buď lze vložit 
informace o složce,
ale zde je nutné, aby P2Rank vygeneroval i vizualizaci, jinak program nenajde soubor se strukturou a pokud
P2Rank pracuje s více jak jedním souborem se strukturami, tak vybere první soubor, na který narazí.
Jinak lze vybrat verzi, kde se vkládá soubor se strukturou a se předpovědmi vazebných míst. 

### P2Rank
Program P2Rank se hlavně ovládá skrze graficky zpracované možnosti, které se pak vloží do jednoho příkazu,
který je následně vyhodnocen. Nejdříve je nutné vybrat funkci. Pro tuto apliakci je hlavní funkce *predict*, 
která vytváří předpovědi vazebných míst. Dále je nutné vybrat jeden nebo více souborů, pro které je 
předpověď vytvořena a vybrat složku, do které se výsledky uloží.  
Pokud stiknete tlačítko *Extras*, tak se zobrazí další možnosti, které se pak propíší do konečného příkau.  
Pro více informací můžete spustit `prank help`.

### AutoDock Vina
AutoDock Vina je používán zejména skrze graficky zpracované možnosti. Ty lze všechny vyplňovat ručně, ale
v horní liště pod záložkou *AutoDock Vina* naleznete několik funkcí, které spuštění programu zjednodušší.  
Zejména jde o *Load from P2Rank*. Tyto funkce buď vezmou celou složku nebo jen soubor s předpověďmi. Ten 
analyzují a dají vám na výběr z nalezených vazebných míst. Podle výběru vazebného místa je spočítá
střed a velikost vazebného místa. Zároveň, pokud se podaří načtení složky, tak se automaticky spustí 
konverze souboru se strukturou do formátu PDBQT, který je nutný pro zpracování tímto programem.
Dále je na uživateli, aby vložil svůj ligand (také v formátu PDBQT) a jméno souboru, do kterého 
se zapíší výsledky.

### OpenBabel
Skrze OpenBabel v liště lze konvertovat soubory do jincýh typů. OpenBabel je zde mimo jiné využíván při 
automatickém načítání souborů struktur. 

## Jednoduchý tutorial
Ve složce "sample" najdete soubory "7rdy-struc.*", což jsou strukturní soubory ve dvou formátech - 
PDB formát analyzuje P2Rank a PDBQT formát analyzuje AutoDock Vina. Soubor "SHY-ligand.pdbqt" je sobor 
ligandu, který můžete použít při spouštění AutoDock Vina. Tyto soubory tu jsou připravené, protože
konverze do PDBQT může trvat dlouho a tímto se proces značně zrychlí.