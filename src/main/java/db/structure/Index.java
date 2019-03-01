package db.structure;

import java.util.ArrayList;
import java.util.Map;
import db.data.Operator;


/**
 * Classe Index, permettant d'indexer une ou plusieurs colonnes
 * Exemple : Indexer selon le nom ou selon le nom + prénom ou nom + prénom + date de naissance ...
 * 
 */
public abstract class Index {
	
	protected static Operator[] compatibleOperatorsList; // Liste des opérateurs compatibles
	protected Column[] indexedColumnsList; // Liste des colonnes indexées dans cet Index
	
	/** Carte des valeurs indexées par cet objet Index - association clef valeur :
	 *  Key est l'identifiant (ex : nom -ou- nom + prénom + age par exemple),
	 *  Integer est l'index dans le fichier binaire issu des .CSV
	 *  Une seule correspondance est possible, pour que ça fonctionne, il faudrait donc remplacer Integer par ArrayList<Integer>
	 */
	protected Map<Key, ArrayList<Integer>> indexedValuesMap;
	
	
	/** Constructeur
	 * @param columnsToIndex la liste des colonnes à indexer
	 */
	public Index(Column[] columnsToIndex) {
		this.indexedColumnsList = columnsToIndex;
	}
	
	/**
	 * @return la liste des colonnes indexées dans cet Index
	 */
	public Column[] getColumnList() {
		return indexedColumnsList;
	}
	
	/**
	 * @return la carte de toutes les valeurs indexées dans cet Index
	 */
	public Map<Key, ArrayList<Integer>> getIndexedValuesMap() {
		return indexedValuesMap;
	}
	
	
}
