package sj.simpleDB.treeIndexing;

import java.util.Collection;
import java.util.List;

import db.data.DataType;
import db.structure.Column;
import db.structure.Table;

/**
 * -> Pour l'instant, c'est dans mon package, mais je vais faire moi-même un peu plus tard le refactoring quand tout marchera !
 * 
 * Modèle d'arbre (générique) pour l'indexation.
 * SIndexingTree est la super-classe dont héritent les arbres spécialisés comme SIndexingTreeInt, String, Float...
 * Le code des classes filles est spécialisé, pour optimiser un maximum le code en fonction du type de la donnée traîtée.
 * -> Le but est de charger le plus de données, le plus rapidement possible, et de pouvoir faire des recherches rapides.
 * 
 * Indexation d'une seule colonne par arbre.
 * Tous les index sont stockés en mémoire vive dans cette version.
 * 
 * Optimisation à réaliser : grouper les valeurs semblables, selon ce que j'ai écrit sur feuille volante.
 * Exemple : diviser la valeur d'un int par 2 pour avoir moins de ramifications, et plus d'éléments dans les listes (feuilles)
 
	Pour pouvoir stocker (beaucoup) plus de données, il faudra que les listes
	des binIndex (feuilles) associées à chaque valeur (de feuille) soient écrites sur le disque.
	  -> Un fichier par arbre.
	  -> les données doivent être aussi simples à lire que possible, et aussi rapides à charger du disque que possible.
	  
	  Fractionner les listes de chaque feuille semble être une mauvaise idée : il faudrait que chaque feuille ait sa liste complète écrite
	  quelque part sur le disque. Mais utiliser un fichier par feuille serait horrible (beaucoup trop long à charger, trop de place perdue sur le disque...)
	  
	  S'il y a suffisament de mémoire pour indexer toute une colonne, j'indexe toute la colonne, puis j'écris les données sur le disque.
	  Les données seraient de la forme, pour chaque feuille : nombre d'éléments + binIndex pour chaque élément.
	  Une fois que tout a été écrit sur le disque, je libère la mémoire vive et je ne garde que la position (dans le fichier) de chaque liste (feuille).
	  Ainsi, en mémoire, il n'y a plus que la position sur le disque de la liste, et non plus la liste de tous les binIndex.
	  
	
	V1 : indexer une colonne à partir du disque
		Chargement :
		à partir d'un objet Table, lire du disque tous les champs concernés,
		les ajouter à l'index.
		
		Recherche :
		via findMatchingBinIndexes
	
 * 
 */
public abstract class SIndexingTree {
	protected SIndexingTreeType treeType; // servira pour l'utilisation de méthodes génériques, pour utiliser le bon type d'arbre et faire les bons cast

	/**
	 * 
	 * @param associatedValue
	 * @return
	 */
	public abstract IntegerArrayList findBinIndexArrayFromValue(Object associatedValue);
	/**
	 * 
	 * @param minValue
	 * @param maxValue
	 * @param isInclusive
	 * @return la collection contenant tous les binIndex correspondants
	 */
	public abstract Collection<IntegerArrayList> findMatchingBinIndexes(Integer minValue, Integer maxValue, boolean isInclusive);
	
	/** Ajouter une valeur et un binIndex associé
	 *  @param associatedValue valeur indexée, ATTENTION : doit être du type du SIndexingTree utilisé (Integer pour un SIndexingTreeInt par exemple, Float pour un TreeFloat)
	 *  @param binIndex position (dans le fichier binaire global) de l'objet stocké dans la table
	 */
	public abstract void addValue(Object associatedValue, Integer binIndex);
	
	/** 
	 *  @param inTable
	 *  @param columnIndex
	 */
	public void indexColumnFromDisk(Table inTable, int columnIndex) {
		List<Column> columnsList = inTable.getColumns();
		int columnsNumber = columnsList.size();
		if (columnsNumber <= columnIndex) { // columnIndex invalide
			return;
		}
		Column indexThisColumn = columnsList.get(columnIndex);
		DataType columnType = indexThisColumn.getDataType();
		
	}
	
	
}
