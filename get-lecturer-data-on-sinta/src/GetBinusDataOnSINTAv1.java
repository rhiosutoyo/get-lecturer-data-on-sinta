import java.net.*;
import java.util.*;
import java.io.*;

public class GetBinusDataOnSINTAv1 {
	public static void main(String[] args) throws Exception {
		// GET DATE FOR FILE NAMEs
		Date today = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(today);		
		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH); // 17
		int month = cal.get(Calendar.MONTH) + 1; // 5
		int year = cal.get(Calendar.YEAR); // 2016
		FileWriter fw = new FileWriter("output_v1-"+ dayOfMonth + "-" + month + "-" + year  +".csv");

		// NECESSARY VARIABLES
		String inputLine;
		String result = null;
		int startIndex;
		int endIndex;
		int count = 0;
		boolean flag = true;
//    	int n = 0;								// helper

    	// FILL THIS
		int pageNumber = 1;
    	int univ_sinta_id = 388;
    	
		System.out.print("===================================================="
				+ "\nSTART CRAWLING\n"
						+ "====================================================\n");
		
		// NAMA KOLOM
//		String kolom = new String("NO;ID SINTA;NAMA DOSEN;NIDN/NIP/NIDK;SCOPUS H-INDEX;GOOGLE H-INDEX;OVERALL SCORE;");
		String kolom = new String("NO;ID SINTA;NAMA DOSEN;NIDN/NIP/NIDK;SCOPUS H-INDEX;GOOGLE H-INDEX");
		System.out.println(kolom);
    	fw.write(kolom+"\n");
		
		while(flag == true) {
			flag = false;
			URL oracle = new URL("http://sinta2.ristekdikti.go.id/affiliations/detail?page="+pageNumber+"&view=authors&id="+univ_sinta_id);
			BufferedReader in = new BufferedReader(
			new InputStreamReader(oracle.openStream()));

			// ONE WEBSITE, READ LINE BY LINE
			while ((inputLine = in.readLine()) != null) {
        		if(inputLine.contains("authors/detail")) {
	        		// SELAMA MASIH KETEMU AUTHORS MAKA AKAN JALAN TERUS
	        		flag = true;
	        		
	        		// NO
	           		result = ++count + ";";

	           		// ID SINTA
	           		startIndex = inputLine.indexOf("id=")+3;
	        		endIndex = inputLine.indexOf("&view");	
	        		result += inputLine.substring(startIndex, endIndex).trim() + ";";

	        		// NAMA DOSEN
	           		startIndex = inputLine.indexOf("blue") + 6;
	        		endIndex = inputLine.indexOf("</a>");
	        		result += inputLine.substring(startIndex, endIndex).trim() + ";";
//	        		System.out.println(inputLine.substring(startIndex, endIndex));	        	
	        	}
	        	if(inputLine.contains("<dd>NIDN <small>/NIP/NIDK</small>")) {
	        		// NIDN / NIP / NIDK
	        		startIndex = inputLine.indexOf("/small") + 10;
	        		endIndex = inputLine.indexOf("</dd>");
	        		result += inputLine.substring(startIndex, endIndex).trim() + ";";
//		       		System.out.println(inputLine.substring(startIndex, endIndex));
	        	}
	        	if(inputLine.contains("H-Index")) {
	        		// SCOPUS H-INDEX
		        	if(inputLine.contains("orange")) {
		        		startIndex = inputLine.indexOf("orange\">") + 8;
		        		endIndex = inputLine.indexOf("<", startIndex+1);
		        		result += inputLine.substring(startIndex, endIndex).trim() + ";";		        		
		        	}
	        		// GOOGLE H-INDEX
		        	if(inputLine.contains("green")) {
		        		startIndex = inputLine.indexOf("green\">") + 7;
		        		endIndex = inputLine.indexOf("<", startIndex+1);
		        		result += inputLine.substring(startIndex, endIndex).trim();// + ";";	        		
		        	}
	        	}
	        	
	        	if(inputLine.contains("/assets/img/scholar_logo.png")) {
		        	// PRINT RESULT
	        		System.out.println(result);
	            	fw.write(result+"\n");
	        	}
//	        	if(inputLine.contains("Overall Score")) {
//	        		// Overall Score
//	        		startIndex = 36;
//	        		endIndex = inputLine.indexOf(" ", startIndex+1);
//	        		result += inputLine.substring(startIndex, endIndex).trim() + ";";
//	        	}	        	       	
	        }
	        in.close();
	        if(flag == false) {
	    		System.out.print("====================================================\nEND OF AVAILABLE PAGE\n====================================================\n");	        	
	        	break;
	        }
	        pageNumber++;
		}
		System.out.print("====================================================\nEND CRAWLING\n====================================================\n");        
		fw.close();
	}
}