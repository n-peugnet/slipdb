package db.structure.recherches;

import db.structure.Column;
import db.structure.Table;
import db.structure.indexTree.IndexTreeDic;

public class SRuntimeIndexingEntry implements Comparable<SRuntimeIndexingEntry> {
	
	// Support des index mono-colonne uniquement, à ce jour
	
	public int columnIndex;
	public IndexTreeDic associatedIndex;
	public Table associatedTable;
	public Column associatedColumn;
	
	
	@Override
	public int compareTo(SRuntimeIndexingEntry o) {
		if (columnIndex > o.columnIndex) return 1;
		if (columnIndex == o.columnIndex) return 0;
		return -1;
	}
	
	
	
}
