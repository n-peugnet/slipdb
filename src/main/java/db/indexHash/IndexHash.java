package db.indexHash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import db.data.types.DataPositionList;
import db.disk.dataHandler.DiskDataPosition;
import db.search.Predicate;
import db.structure.Column;
import db.structure.Index;
import index.indexTree.IndexException;

import org.apache.commons.lang3.ArrayUtils;

import db.search.Operator;

public class IndexHash extends Index {
	
	
	// Hash map associant une clef à une liste de positions (index des lignes parsées des csv)
	protected HashMap<KeyHash, ArrayList<Integer>> indexMap;
	
	public IndexHash(Column column) {
		super(column);
		this.indexMap = new HashMap<>();
	}
	
	@Override
	public DataPositionList getPositionsFromPredicate(Predicate predicate) throws IndexException {
		// TODO make this func
		return null;
	}
	
	@Override
	public void addValue(Object value, DiskDataPosition position) throws IOException {
		// TODO
	}
	
	@Override
	public boolean isOperatorCompatible(Operator op) {
		if(!ArrayUtils.contains(new Operator[] {
				Operator.equals
			}, op)) {
			return false;
		}
		if (!indexedColumn.getDataType().isOperatorCompatible(op)) {
			return false;
		}
		return true;
	}
	
}
