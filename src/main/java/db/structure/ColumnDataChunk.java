package db.structure;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;

import db.data.types.ByteType;
import db.data.types.DataType;
import db.data.types.DateType;
import db.data.types.DoubleType;
import db.data.types.FloatType;
import db.data.types.IntegerType;
import db.data.types.LongType;
import db.data.types.StringType;

// Version simplifiée

public class ColumnDataChunk implements Serializable {
	private static final long serialVersionUID = -3035163829243851789L;
	
	// Stockage de la donnée à garder en mémoire ici
	// -> Il n'est pas possible d'utiliser l'héritage ici, il faut un truc qui prenne le moins de mémoire possible, donc pas des objets.
	
	public byte[] valuesArrayByte = null;
	public int[] valuesArrayInteger = null; // date, aussi
	public long[] valuesArrayLong = null;
	public float[] valuesArrayFloat = null;
	public double[] valuesArrayDouble = null;
	public String[] valuesArrayString = null; // stocker des objets ralentir grandement le GC (garbage collector)
	// Un seul tableau stockant toutes les chaînes de caractères, pour ne pas avoir trop d'objets créés
	// et ne pas surcharger le GC
	public byte[] valuesByteArray = null;
	
	

	private int currentItemPosition = 0;
	private int currentPositionInByteArray = 0;
	private final int maxNumberOfItems;
	private final int byteArrayLength;
	
	protected final DataType dataType;
	protected final int dataTypeSize; // taille en octets
	
	/** 
	 *  @param argDataType     type de la donnée sauvegardée
	 *  @param maxNumberOfItems  taille de l'allocation (ne sera pas ré-alloué, pour des raisons de performance)
	 */
	public ColumnDataChunk(DataType argDataType, int argMaxNumberOfItems) {
		dataType = argDataType;
		dataTypeSize = dataType.getSize();
		maxNumberOfItems = argMaxNumberOfItems;
		byteArrayLength = maxNumberOfItems * dataTypeSize;
		valuesByteArray = new byte[byteArrayLength]; // données stockées sous forme d'un tableau d'octets
		currentItemPosition = 0;
		currentPositionInByteArray = 0;
		ByteBuffer b = ByteBuffer.allocateDirect(10);
		//b.getDouble()
	}
	
	/** 
	 *  @return true si un nouveau chunk est nécessaire
	 */
	private boolean incPosition() {
		currentItemPosition++;
		currentPositionInByteArray += dataTypeSize;
		if (currentItemPosition >= maxNumberOfItems) return true;
		return false;
	}
	
	public boolean writeInt(int data) {
		valuesArrayInteger[currentItemPosition] = data;
		return incPosition();
	}
	
	public boolean writeDate(int data) {
		valuesArrayInteger[currentItemPosition] = data;
		return incPosition();
	}
	
	public boolean writeByte(byte data) {
		valuesArrayByte[currentItemPosition] = data;
		return incPosition();
	}
	
	public boolean writeLong(long data) {
		valuesArrayLong[currentItemPosition] = data;
		return incPosition();
	}
	
	public boolean writeDouble(double data) {
		valuesArrayDouble[currentItemPosition] = data;
		return incPosition();
	}
	
	public boolean writeFloat(float data) {
		valuesArrayFloat[currentItemPosition] = data;
		return incPosition();
	}
	
	// En entrée : le String de taille ajustée
	public boolean writeString(String data) {
		
		/*if (useByteStringStorage) { // ne pas utisier d'objets, juste des octets
			byte[] strAsBytes = data.getBytes();
			System.arraycopy(strAsBytes, 0, valuesArrayStringAsBytes, currentItemPosition * dataTypeSize, dataTypeSize);
		} else // utiliser des objets (pas opti)
			valuesArrayString[currentItemPosition] = data;*/
		return incPosition();
	}
	
	
	public int getCurrentItemPosition() {
		return currentItemPosition;
	}
	
	/** Sans vérification sur la validité de l'index demandé
	 *  @param indexInChunk
	 */
	public String getString(int indexInChunk) {
		/*if (useByteStringStorage) { // ne pas utisier d'objets, juste des octets (beaucoup plus optimisé)
			byte[] strAsBytes = new byte[dataTypeSize];
			System.arraycopy(valuesArrayStringAsBytes, indexInChunk * dataTypeSize, strAsBytes, 0, dataTypeSize);
			return new String(strAsBytes);
		} else {
			return valuesArrayString[indexInChunk];
		}*/
		return null;
	}
	
	//public 
	
	/* peut-être supporté un jour
	public boolean writeByteArrayData(byte[] data) {
		???
		return incPosition();
	}*/
	
	
	
}
