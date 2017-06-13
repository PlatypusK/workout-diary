package com.workout.diary.main;

import java.util.Scanner;



/**
 * @author Ketil
 *Singleton to make a {@linkplain java.util.Scanner} item globally accessible, but only having one instance. 
 *Scanner(system.in) will create some odd behaviour when other Scanner instances close, so it is best to only have one object of this type globally
 */
public class ScannerSingleton {
	
	/**
	 *Singleton to make a {@linkplain java.util.Scanner} item globally accessible, but only having one instance. 
	 *Scanner(system.in) will create some odd behaviour when other Scanner instances close, so it is best to only have one object of this type globally
	 */
	private static Scanner SCANNER=null;
	
	
	private ScannerSingleton(){
	}
	public static final Scanner getScanner(){
		if(SCANNER==null){
			SCANNER=new Scanner(System.in);
		}
		return SCANNER;
	}

}
