package com.dant.utils;

import java.text.NumberFormat;

public class MemUsage {
	

	public static void printMemUsage() {
		printMemUsage("", 4);
		
	}

	public static void printMemUsage(String message) {
		printMemUsage(message, 4);
	}
	
	public static void printMemUsage(String message, int logLevel) {
		Runtime runtime = Runtime.getRuntime();

		NumberFormat format = NumberFormat.getInstance();

		//StringBuilder sb = new StringBuilder();
		//long maxMemory = runtime.maxMemory();
		long allocatedMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();
		
		
		long usedMemory = allocatedMemory - freeMemory;
		if (message == null) message = "";
		if (message.equals("") == false) message += " ";
		
		Log.logInfoMessage(message + format.format(usedMemory / 1024), "MEMORY", logLevel);
		
		/*
		Log.info("free memory: " + format.format(freeMemory / 1024));
		Log.info("allocated memory: " + format.format(allocatedMemory / 1024));
		Log.info("max memory: " + format.format(maxMemory / 1024));
		Log.info("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024));*/
		
	}
}
