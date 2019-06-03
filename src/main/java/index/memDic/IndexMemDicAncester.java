package index.memDic;

import db.disk.dataHandler.DiskDataPosition;
import db.search.Operator;
import db.search.Predicate;
import db.structure.Column;
import db.structure.Index;
import db.structure.Table;
import index.indexTree.IndexException;

import java.io.IOException;
import java.util.Collection;

/**
 *  Pour avoir un objet commun pour le stockage des variables statiques entre IndexMemDic et IndexMemDicCh.
 *
 */
public class IndexMemDicAncester extends Index {
	// Pensé pour être mono-thread, pour l'instant
	
	protected Table table;
	protected int[] colIndexArray; // dans l'ordre
	protected Column[] indexOnThisColArray;
	
	//public static Table staticTable; désormais inutile
	//public static int[] staticColIndexArray;
	public static Column[] staticIndexOnThisColArray; // seulement utile pour le sort, utilisé depuis IndexMemDicTemporaryItem
	// et actualisé avant tous les sort (dans IndexMemDic.refreshIndexWithColumnsData())
	
	public static boolean enableVerboseDichotomy = false;
	public static boolean enableVerboseSort = false;
	
	public static final boolean useSafeSlowComparaisonsNotDichotomy = false;
	public static final boolean enableDoubleDichotomyVerif = false;

	@Override
	public Column[] getIndexedColumns() {
		return indexOnThisColArray;
	}

	@Override
	public boolean isOperatorCompatible(Operator op) {
		//TODO
		return false;
	}

	@Override
	public Collection<DiskDataPosition> getPositionsFromPredicate(Predicate predicate) throws IndexException {
		return null;
	}

	@Override
	public void addValue(Object value, DiskDataPosition position) throws IOException {

	}

	@Override
	public void flushOnDisk() throws IOException {

	}
}
