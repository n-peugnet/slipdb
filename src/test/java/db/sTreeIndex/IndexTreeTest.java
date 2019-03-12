package db.sTreeIndex;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.dant.utils.Log;
import com.dant.utils.MemUsage;
import com.dant.utils.Timer;
import com.dant.utils.Utils;

import db.data.ByteType;
import db.data.DateType;
import db.data.DoubleType;
import db.data.FloatType;
import db.data.IntegerArrayList;
import db.data.StringType;
import db.parsers.CsvParser;
import db.structure.Column;
import db.structure.Table;
import db.structure.indexTree.IndexTreeV3;

public class IndexTreeTest {

	protected static CsvParser parser;
	protected static Table table;

	@BeforeAll
	static void setUpBeforeAll() throws Exception {
		Log.info("setUpBeforeAll");
		Log.start("target/slipdb_indexingTreeTest.log", 3);
		//if (true) return;
		ArrayList<Column> columns = new ArrayList<Column>();
		try {
			columns.add(new Column("VendorID", new ByteType()));
			columns.add(new Column("tpep_pickup_datetime", new DateType()));
			columns.add(new Column("tpep_dropoff_datetime", new DateType()));
			columns.add(new Column("passenger_count", new ByteType()));
			columns.add(new Column("trip_distance", new FloatType()));
			columns.add(new Column("pickup_longitude", new DoubleType()));
			columns.add(new Column("pickup_latitude", new DoubleType()));
			columns.add(new Column("RateCodeID", new ByteType()));
			columns.add(new Column("store_and_fwd_flag", new StringType(1)));
			columns.add(new Column("dropoff_longitude", new DoubleType()));
			columns.add(new Column("dropoff_latitude", new DoubleType()));
			columns.add(new Column("payment_type",  new ByteType()));
			columns.add(new Column("fare_amount", new FloatType()));
			columns.add(new Column("extra", new FloatType()));
			columns.add(new Column("mta_tax", new FloatType()));
			columns.add(new Column("tip_amount", new FloatType()));
			columns.add(new Column("tolls_amount", new FloatType()));
			columns.add(new Column("improvement_surcharge", new FloatType()));
			columns.add(new Column("total_amount", new FloatType()));
		} catch (Exception e) {
			Log.error(e);
		}
		table = new Table("test", columns);
		parser = new CsvParser(table);
		
		FileInputStream is = new FileInputStream("testdata/SMALL_100_000_yellow_tripdata_2015-04.csv"); // "../SMALL_1_000_000_yellow_tripdata_2015-04.csv"
		//FileInputStream is = new FileInputStream("../SMALL_1_000_000_yellow_tripdata_2015-04.csv");
		
		Timer parseTimer = new Timer("Temps pris par le parsing");
		parser.parse(is);
		parseTimer.printms();
		
		Log.info("setUpBeforeAll OK");
	}
	
	/*
	@Test
	void testStorageLimit() {
		
		int arraySize = 1_000_000;
		MemUsage.printMemUsage();
		/*float[] floatArray = new float[arraySize];
		for (float i = 0; i < arraySize; i++) {
			floatArray[(int)i] = i;
		}* /
		Timer time = new Timer("Time");
		Float[] floatArray = new Float[arraySize];
		float nb = 0;
		for (float i = 0; i < arraySize; i++) {
			floatArray[(int)i] = nb;
			nb++;
		}
		time.printms();
		MemUsage.printMemUsage();
		
	}*/
	
	@Test
	void testIndexingTreeInt() throws IOException {
		//if (true) return;
		/**
		 * Note : c'est super le bordel ici, je vais ranger ça ^^'
		 */
		IndexTreeV3 indexingObject = new IndexTreeV3();
		//SIndexingTreeFloat indexingFoat = new SIndexingTreeFloat();
		Log.info("Lancé");
		
		// Index the column on index 4
		//int indexingColumnIndex = 3; // passanger count
		//int indexingColumnIndex = 4; // trip distance
		//int indexingColumnIndex = 5; // latitude
		int indexingColumnIndex = 1; // date pickup
		
		// Index the column from the disk
		// -> reading fron the disk is quite slow
		// --> a very cool optimization will be to index a bunch of columns at the same time
		Timer loadFromDiskTimer = new Timer("Time took to index this column, from disk");
		MemUsage.printMemUsage();
		indexingObject.indexColumnFromDisk(table, indexingColumnIndex);
		MemUsage.printMemUsage();
		loadFromDiskTimer.printms();
		
		Timer searchQueryTimer = new Timer("Time took to return the matching elements");
		Timer searchQueryFullTimer = new Timer("Time took to return the matching elements + size evaluation");
		Log.info("Fini");
		Log.info("OBJECT RESULT :");
		
		// Get the query result
		Collection<IntegerArrayList> result;
		MemUsage.printMemUsage();
		//result = indexingObject.findMatchingBinIndexes(new Float(0), new Float(10000), true); // new Float(20), new Float(21)
		//result = indexingObject.findMatchingBinIndexes(new Integer(-1000), new Integer(1000), true);

		Date dateFrom = Utils.dateFromString("2015-04-15 23:59:00");
		Date dateTo = Utils.dateFromString("2015-04-16 00:06:30");
		int intDateFrom = Utils.dateToSecInt(dateFrom);
		int intDateTo = Utils.dateToSecInt(dateTo);
		result = indexingObject.findMatchingBinIndexes(intDateFrom, intDateTo, true);
		//result = indexingObject.findMatchingBinIndexes(new Byte((byte)0), new Byte((byte)100), true);
		
		//indexingObject.findMatchingBinIndexesFromDisk(intDateFrom, intDateTo, true);
		
		MemUsage.printMemUsage();
		searchQueryTimer.printms();
		
		// Iterates over all the results
		int numberOfResults = 0, numberOfLines = 0;
		for (IntegerArrayList list : result) {
			//Log.info("list size = " + list.size());
			numberOfResults += list.size();
			numberOfLines++;
			for (Integer index : list) {
				// un-comment those lines if you want to get the full info on lines : List<Object> objList = table.getValuesOfLineById(index);
				/*Log.info("  index = " + index);
				List<Object> objList = table.getValuesOfLineById(index);
				Object indexedValue = objList.get(indexingColumnIndex);
				
				//Log.info("  valeur indexée = " + indexedValue);
				Log.info("  objList = " + objList);*/
				
			}
		}
		searchQueryFullTimer.printms();
		Log.info("Number of results = " + numberOfResults);
		Log.info("Number of lines = " + numberOfLines);

		Timer writeIndexToDiskTimer = new Timer("Temps pris pour l'écriture sur disque");
		indexingObject.saveOnDisk();
		writeIndexToDiskTimer.printms();
		
		
	}
	
	@AfterAll
	static void tearDown() {
		
	}
}