package sj.simpleDB.arrayList;

public class AL_Main {
	
	public static void main(String[] args) {
		
		AL_GlobalTest globalTest = new AL_GlobalTest();
		//globalTest.globalTestOffline();
		if (args.length >= 1)
			globalTest.globalTestWithCSV(args[0]);
		else {
			System.out.println("AL_Main.main() : pas assez de paramètres en entrée.");
		}
		
		
	}
	
	
	
}
