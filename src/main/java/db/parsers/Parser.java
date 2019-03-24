package db.parsers;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import com.dant.utils.Log;
import com.dant.utils.MemUsage;
import com.dant.utils.Timer;

import db.structure.Column;
import db.structure.Table;
import db.structure.recherches.SRuntimeIndexingEntry;
import db.structure.recherches.SRuntimeIndexingEntryList;

public abstract class Parser {
	protected Table schema;
	protected int lineByteSize; // number of bytes used to store information
	protected int totalEntryCount = 0;
	
	protected SRuntimeIndexingEntryList runtimeIndexingEntries = null;
	
	public Parser(Table schema) {
		this.schema = schema;
		this.lineByteSize = schema.getLineSize();
	}
	
	// Pour indexer au moment du parsing
	public void setRuntimeIndexing(SRuntimeIndexingEntryList argIndexingEntryList) {
		runtimeIndexingEntries = argIndexingEntryList;
	}
	
	public final void parse(InputStream input, boolean appendAtTheEndOfSave) {
		parse(input, -1, appendAtTheEndOfSave);
	}
	
	protected int showInfoEveryParsedLines = 100_000; // mettre -1 pour désactiver l'affichage
	
	/**
	 * Parse an input stream into an output stream according to a schema with a
	 * limit of lines (-1 : no limit)
	 * 
	 * @param input
	 * @param limit
	 */
	public final void parse(InputStream input, int limit, boolean appendAtTheEndOfSave) {
		int localReadEntryNb = 0;
		try (
				BufferedReader bRead = new BufferedReader(new InputStreamReader(input));
				
				DataOutputStream bWrite = new DataOutputStream(new BufferedOutputStream(schema.tableToOutputStream(appendAtTheEndOfSave)));
		) {
			String entryString;
			Timer timeTookTimer = new Timer("Temps écoulé");
			
			while ((entryString = processReader(bRead)) != null && totalEntryCount != limit) {
				
				// entryString : "entrée", ligne lue (d'un fichier CSV par exemple pour CSVParser)
				
				if (showInfoEveryParsedLines != -1 && localReadEntryNb % showInfoEveryParsedLines == 0) {
					Log.info("Parser : nombre de résultats (local) parsés = " + localReadEntryNb + "   temps écoulé = " + timeTookTimer.pretty());
					MemUsage.printMemUsage();
				}
				
				try {
					this.writeEntry(entryString, bWrite);
					localReadEntryNb++;
					totalEntryCount++;
				} catch (IncorrectEntryException e) {
					Log.error(e);
					// TODO: handle exception
				} catch (IOException e) {
					Log.error(e);
					// TODO: handle exception
				}
			}
		} catch (FileNotFoundException e) {
			Log.error(e);
			// TODO: handle exception
		}catch (IOException e) {
			Log.error(e);
			// TODO: handle exception
		}
	}
	
	
	
	/** Ecriture d'une entrée (ligne, donnée complète) sur un DataOutputStream (nécessaire pour avoir le fonction .size())
	 *  @param entryString
	 *  @param output
	 *  @throws IncorrectEntryException
	 *  @throws IOException
	 */
	protected final void writeEntry(String entryString, /*OutputStream*/DataOutputStream output) throws IncorrectEntryException, IOException {
		String[] valuesAsStringArray = processEntry(entryString);
		
		if (!isCorrectSize(valuesAsStringArray)) {
			throw new IncorrectEntryException(totalEntryCount, "incorrect size");
			// -> will be handled Nicolas' way ? yes
		}
		// the buffer used to store the line data as an array of bytes
		ByteBuffer entryBuffer = ByteBuffer.allocate(lineByteSize);
		Object[] entry = new Object[valuesAsStringArray.length];
		
		long entryBinIndex = output.size();
		try {
		// for each column, parse and write data into entryBuffer
			for (int columnIndex = 0; columnIndex < schema.getColumns().size(); columnIndex++) {
				Column currentColumn = schema.getColumns().get(columnIndex);
				
				// Converts the string value into an array of bytes representing the same data
				Object currentValue = currentColumn.writeToBuffer(valuesAsStringArray[columnIndex], entryBuffer);
				currentColumn.evaluateMinMax(currentValue); // <- Indispensable pour le IndexTreeCeption (non utile pour le IndexTreeDic)
				// Indexer au moment de parser (pour de meilleures performances)
				
				if (runtimeIndexingEntries != null) {
					SRuntimeIndexingEntry indexingEntry = runtimeIndexingEntries.getEntryAssociatedWithColumnIndex(columnIndex);
					if (indexingEntry != null) {
						// Indexer cette entrée
						indexingEntry.addIndexValue(currentValue, entryBinIndex);
						//Log.info("Indexer valeur = " + currentValue);
					}
					//Log.info("Indexer2 valeur = " + currentValue);
				}
				
				
				entry[columnIndex] = currentValue;
			}
		} catch (Exception e) {
			throw new IncorrectEntryException(totalEntryCount, "incorrect data");
		}
		// writes the line in the output stream associated with the current file
		output.write(entryBuffer.array());
	}
	
	/**
	 * Checks if the number of values in an array is the same as the number of columns in the schema.
	 *
	 * @param valuesArray
	 * @return
	 */
	private final boolean isCorrectSize(String[] valuesArray) {
		return valuesArray.length == schema.getColumns().size();
	}
	
	
	//////////////////////////////// Interface to implement ////////////////////////////////
	
	/**
	 * Reads a BufferedReader and return an entry String.
	 *
	 * @param input - the input as a buffered reader
	 * @return entryString or *null* if there is no more entries
	 */
	abstract protected String processReader(BufferedReader input) throws IOException;
	
	/** 
	 * Converts a string entry (such as a CSV line) to a byte array of raw data.
	 *
	 * @param entryString
	 * @return the data stored by the line as string array
	 */
	abstract protected String[] processEntry(String entryString);
}
