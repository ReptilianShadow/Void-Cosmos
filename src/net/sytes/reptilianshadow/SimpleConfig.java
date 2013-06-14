package net.sytes.reptilianshadow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JFileChooser;

public class SimpleConfig {
	
	public static HashMap<String, String> getHashMap(File file) throws FileNotFoundException{
		
		HashMap<String, String> config = new HashMap<String, String>();
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		String cLine = null;
		try {
			cLine = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while(cLine != null){
			
			//remove all spaces before first equals sign and
			//after until first non blank character
			String noComment = removeComments(cLine);
			
			if (noComment.contains("=")){ //must contain assignment
				
				String evalString = removeBadWhiteSpace(noComment);
				
				String key = evalString.substring(0, evalString.indexOf("="));
				String value = evalString.substring(evalString.indexOf("=") + 1);
				
				config.put(key, value);
			}
			
			try {
				cLine = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return config;
	}
	
	
	private static String removeBadWhiteSpace(String str){
		//assume comments are gone
		
		int indexOfEquals = str.indexOf("=");
		
		String beforeEquals = str.substring(0, indexOfEquals);
		beforeEquals = beforeEquals.replaceAll(" ", "");
		
		String equalsAfter = str.substring(indexOfEquals);
		
		
		return beforeEquals + equalsAfter;
	}
	
	private static String removeComments(String line){		
		boolean ignoreSig = false;
		
		for (int i = 0; i < line.length(); i++){
			
			if (line.charAt(i) == '#' && !ignoreSig){
				return line.substring(0, i);
			}
			
			if (line.charAt(i) == '\\'){
				//remove the backslash and go back one letter to compensate
				line = line.substring(0, i) + line.substring(i + 1);
				i--;
				
				ignoreSig = true;
			}else{
				ignoreSig = false;
			}
			
		}
		return line;
		
	}
	
	
	
	
	
}
