package db.structure.recherches;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dant.utils.Log;

import db.data.DataType;
import db.parsers.CsvParser;
import db.structure.Column;
import db.structure.Table;
import db.structure.indexTree.IndexTreeDic;

public class STableHandler {
	
	protected String tableName;
	protected ArrayList<Column> columnsList = new ArrayList<Column>();
	protected Table associatedTable;
	protected CsvParser csvParser = null;
	// Possibilité de parser de plusieurs manières différentes (un jour...)
	protected boolean firstTimeParsingData = true;
	
	protected ArrayList<IndexTreeDic> indexTreeList = new ArrayList<IndexTreeDic>(); // Liste des IndexTree associés à cette table
	
	public String getTableName() {
		return tableName;
	}
	
	public STableHandler(String argTableName) {
		tableName = argTableName;
	}
	
	/**
	 *  Ajouter une colonne, suppose qu'il n'y a pas encore de données dans la table
	 *  @param argColumnName
	 *  @param argColumnDataType
	 * @throws Exception 
	 */
	public void addColumn(String argColumnName, DataType argColumnDataType) throws Exception {
		for (Column col : columnsList) {
			if (col.getName().equals(argColumnName)) throw new Exception("Ajout de la colonne impossibe, il en exie déjà une du même nom : " + argColumnName);
		}
		Column newColumn = new Column(argColumnName, argColumnDataType);
		columnsList.add(newColumn);
	}
	
	public Table createTable() throws IOException {
		associatedTable = new Table(tableName, columnsList);
		return associatedTable;
	}
	
	public void parseCsvData(String csvPath) throws Exception {
		if (associatedTable == null) throw new Exception("La table associée est null, elle doit être crée via createTable avant tout parsing.");
		if (csvParser == null)
			csvParser = new CsvParser(associatedTable);
		
		InputStream is = new FileInputStream(csvPath);
		csvParser.parse(is, !firstTimeParsingData);
		is.close();
		
		firstTimeParsingData = false;
		
	}
	
	protected int getColumnIndex(String columnName) throws Exception {
		if (associatedTable == null) throw new Exception("Aucune table crée, indexation impossible.");
		List<Column> columnList = associatedTable.getColumns();
		for (int colIndex = 0; colIndex < columnList.size(); colIndex++) {
			Column currentColumn = columnList.get(colIndex);
			if (currentColumn.getName().equals(columnName)) {
				return colIndex;
			}
		}
		return -1;
	}
	
	public void indexColumnWithTreeFromDisk(String columnName) throws Exception {
		int colIndex = getColumnIndex(columnName);
		if (colIndex == -1) throw new Exception("Colonne introuvable, impossible de l'indexer.");
		indexColumnWithTreeFromDisk(colIndex);
	}

	public void indexColumnWithTreeFromDisk(int columnIndex) throws Exception {
		if (associatedTable == null) throw new Exception("Aucune table crée, indexation impossible.");
		List<Column> columnList = associatedTable.getColumns();
		if (columnIndex < 0 || columnIndex >= columnList.size()) throw new Exception("Index de la colonne invalide. (columnIndex=" + columnIndex + " non compris entre 0 et columnList.size()=" + columnList.size());
		
		IndexTreeDic indexingObject = new IndexTreeDic();
		indexTreeList.add(indexingObject);
		indexingObject.indexColumnFromDisk(associatedTable, columnIndex);
	}
	
	
	// ---- RuntimeIndexing : servant à stocker les champs à indexer ----
	
	// Pour l'instant, il n'y a que le spport des index mono-colonne.
	// Faire une recherche sur une colonne équivaut à trouver l'index qui traîte de la colonne, et à faire la recherche dessus.
	
	
	// indexColumnList est la liste des colonnes à indexer
	public ArrayList<SRuntimeIndexingEntry> runtimeIndexingList = new ArrayList<SRuntimeIndexingEntry>();
	
	public void sortRuntimeIndexingList() { // très important lors de la lecture des colonnes (parsing)
		Collections.sort(runtimeIndexingList); // (List<SInitialIndexingIndex>)
	}
	
	public void createRuntimeIndexingColumn(int columnIndex) throws Exception { // addInitialColumnAndCreateAssociatedIndex
		if (associatedTable == null) throw new Exception("Aucune table crée, indexation impossible.");
		List<Column> columnList = associatedTable.getColumns();
		if (columnIndex < 0 || columnIndex >= columnList.size()) throw new Exception("Index de la colonne invalide. (columnIndex=" + columnIndex + " non compris entre 0 et columnList.size()=" + columnList.size());
		
		SRuntimeIndexingEntry indexEntry = new SRuntimeIndexingEntry();
		indexEntry.associatedIndex = new IndexTreeDic();
		indexEntry.associatedColumn = columnList.get(columnIndex);
		indexEntry.associatedTable = associatedTable;
		indexEntry.columnIndex = columnIndex;
		runtimeIndexingList.add(indexEntry);
		
	}
	
	
	
	
	
	//public boolean makeQuery(STableQuery query) {
		
	//}
	
	/*
	public ArrayList<Integer> findIndexedResultsOfColumn(String comumnName, Object minValue, Object maxValue, boolean inclusive) {
		int colIndex = getColumnIndex(comumnName);
		if (colIndex == -1) throw new Exception("Colonne introuvable, impossible de faire une recherche sur ses index.");
		//return findIndexedResultsOfColumn();
		
		
	}*/
	
	
	
	
}
