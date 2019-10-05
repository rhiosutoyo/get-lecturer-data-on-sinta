import java.net.*;
import java.util.*;
import java.io.*;

public class GetLecturerDataOnSINTA {
	public static void main(String[] args) throws Exception {
		// GET DATE FOR FILE NAME
		Date today = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(today);
		int dayOfMonth = 0, month = 0, year = 0;
		dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);	 	// 17
		month = cal.get(Calendar.MONTH) + 1; 				// 5
		year = cal.get(Calendar.YEAR); 						// 2016
		FileWriter fw = new FileWriter("output_v1-"+ dayOfMonth + "-" + month + "-" + year  +".csv");

		// NECESSARY VARIABLES
		boolean isAvailable = true;
		int startIndex = 0, endIndex = 0, pageNumber = 1;
		String inputLine = null, finalResult = null;
		LecturerDataModel newLecturer = new LecturerDataModel();

    	// FILL THIS
    	int universityID = 388;
  		
		// NAMA KOLOM
		String columnName = new String("ID SINTA;NAMA DOSEN;NIDN/NIP/NIDK;SCOPUS H-INDEX;GOOGLE H-INDEX");
		System.out.println(columnName);
    	fw.write(columnName + "\n");
		
		while(isAvailable == true) {
			isAvailable = false;

			URL oracle = new URL("http://sinta2.ristekdikti.go.id/affiliations/detail?page="+pageNumber+"&view=authors&id="+universityID);
			BufferedReader in = new BufferedReader(
			new InputStreamReader(oracle.openStream()));

			// ONE PAGE, READ LINE BY LINE
			while ((inputLine = in.readLine()) != null) {
        		if(inputLine.contains("authors/detail")) {
	        		// SELAMA MASIH KETEMU AUTHORS MAKA AKAN JALAN TERUS
	        		isAvailable = true;
	        		finalResult = "";
	        		
	           		// ID SINTA
	           		startIndex = inputLine.indexOf("id=") + 3;
	        		endIndex = inputLine.indexOf("&view");
	        		newLecturer.setIdSinta(inputLine.substring(startIndex, endIndex).trim());

	        		// NAMA DOSEN
	           		startIndex = inputLine.indexOf("blue") + 6;
	        		endIndex = inputLine.indexOf("</a>");
	        		newLecturer.setNamaDosen(inputLine.substring(startIndex, endIndex).trim());
	        	}
	        	if(inputLine.contains("<dd>NIDN <small>/NIP/NIDK</small>")) {
	        		// NIDN / NIP / NIDK
	        		startIndex = inputLine.indexOf("/small") + 10;
	        		endIndex = inputLine.indexOf("</dd>");
	        		newLecturer.setNidnDosen(inputLine.substring(startIndex, endIndex).trim());	        		
	        	}
	        	if(inputLine.contains("H-Index")) {
	        		// SCOPUS H-INDEX
		        	if(inputLine.contains("orange")) {
		        		startIndex = inputLine.indexOf("orange\">") + 8;
		        		endIndex = inputLine.indexOf("<", startIndex+1);
		        		newLecturer.setScopusHIndex(inputLine.substring(startIndex, endIndex).trim());		        		
		        	}
	        		// GOOGLE H-INDEX
		        	if(inputLine.contains("green")) {
		        		startIndex = inputLine.indexOf("green\">") + 7;
		        		endIndex = inputLine.indexOf("<", startIndex+1);
		        		newLecturer.setGoogleHIndex(inputLine.substring(startIndex, endIndex).trim());		        		
		        	}
	        	}
	        	
	        	if(inputLine.contains("/assets/img/scholar_logo.png")) {
		        	// COMBINE AND STORE RESULT
	        		finalResult += newLecturer.getIdSinta() + ";";
	        		finalResult += newLecturer.getNamaDosen() + ";";
	        		finalResult += newLecturer.getNidnDosen() + ";";
	        		finalResult += newLecturer.getScopusHIndex() + ";";
	        		finalResult += newLecturer.getGoogleHIndex();
	        		System.out.println(finalResult);
	        		fw.write(finalResult + "\n");
	        	}	       	
	        }
	        in.close();
	        
	        if(isAvailable == false) {
	    		fw.close();
	        	break;
	        }
	        else {
		        pageNumber++;	        	
	        }
		}
	}
}