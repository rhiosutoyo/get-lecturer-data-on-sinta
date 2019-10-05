import java.net.*;
import java.util.*;
import java.io.*;

public class GetLecturerDataOnSINTA_v2 {
	
	public static void main(String[] args) throws Exception {
		// GET DATE FOR FILE NAME
		Date today = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(today);
		int dayOfMonth = 0, month = 0, year = 0;
		dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);	 	// 17
		month = cal.get(Calendar.MONTH) + 1; 				// 5
		year = cal.get(Calendar.YEAR); 						// 2016
		FileWriter fileWriter = new FileWriter("output_v2-"+ dayOfMonth + "-" + month + "-" + year  +".csv");

		// NECESSARY VARIABLES 
		int startIndex = 0, endIndex = 0, pageNumber = 0;
		String inputLine, finalResult = null, tempString = null;
		LecturerDataModel newLecturer = new LecturerDataModel();
		URL linkURL;
		
    	// FILL THIS
    	int universityID 	= 388;
    	int startPage 		= 1;
    	int endPage 		= getEndPage(universityID);

		// NAMA KOLOM
		String kolom = new String("ID SINTA;NIDN/NIP/NIDK;NAMA DOSEN;"
				+ "SCOPUS DOCUMENTS;SCOPUS CITATIONS;SCOPUS H-INDEX;SCOPUS i10-INDEX;"
				+ "BOOKS;IPR");
		System.out.println(kolom);
    	fileWriter.write(kolom+"\n");
			
		for(pageNumber = startPage; pageNumber <= endPage ; pageNumber++ ) {
			// ACCESS THE WEB PAGE
			linkURL = new URL("http://sinta2.ristekdikti.go.id/affiliations/detail?page="+pageNumber+"&view=authors&id="+universityID);
			BufferedReader pageResult = new BufferedReader(
			new InputStreamReader(linkURL.openStream()));

			// ONE PAGE, READ LINE BY LINE
			while ((inputLine = pageResult.readLine()) != null) {
	        	if(inputLine.contains("authors/detail")) {
	        		finalResult = "";
	        		
	           		// ID SINTA
	           		startIndex = inputLine.indexOf("id=")+3;
	        		endIndex = inputLine.indexOf("&view");
	        		newLecturer.setIdSinta(inputLine.substring(startIndex, endIndex).trim());

	        		// NAMA DOSEN
	           		startIndex = inputLine.indexOf("blue") + 6;
	        		endIndex = inputLine.indexOf("</a>");
	        		tempString = inputLine.substring(startIndex, endIndex).trim();
	        		// CLEAN BROKEN DATA
	        		if(tempString.contains(",")) {
	        			tempString = tempString.substring(0, tempString.indexOf(","));
	        		}
	        		newLecturer.setNamaDosen(tempString);
	        	}
	        	if(inputLine.contains("<dd>NIDN <small>/NIP/NIDK</small>")) {
	        		// NIDN / NIP / NIDK
	        		startIndex = inputLine.indexOf("/small") + 10;
	        		endIndex = inputLine.indexOf("</dd>");
	        		tempString = inputLine.substring(startIndex, endIndex).trim();
	        		// CLEAN BROKEN DATA
	        		if(tempString.contains(",")) {
	        			tempString = tempString.substring(0, tempString.indexOf(","));
	        		}
	        		newLecturer.setNidnDosen(tempString);
	        	}
	        	if(inputLine.contains("/assets/img/scholar_logo.png")) {	        		
	        		getProfileData(newLecturer);
	        		
		        	// COMBINE AND STORE RESULT
	        		finalResult += newLecturer.getIdSinta() + ";";
	        		finalResult += newLecturer.getNidnDosen() + ";";
	        		finalResult += newLecturer.getNamaDosen() + ";";

	        		finalResult += newLecturer.getScopusDocument() + ";";
	        		finalResult += newLecturer.getScopusCitation() + ";";
	        		finalResult += newLecturer.getScopusHIndex() + ";";
	        		finalResult += newLecturer.getScopusI10Index() + ";";

	        		finalResult += newLecturer.getBookTotal() + ";";
	        		finalResult += newLecturer.getIntellectualProperty();

	        		System.out.println(finalResult);
	        		fileWriter.write(finalResult + "\n");
	        	}
	        }
	        pageResult.close();
		}
		fileWriter.close();
	}
	
	private static int getEndPage(int universityID) throws Exception  {
		String inputLine = "";
		int endPage = 0;
		int startIndex = 0, endIndex = 0;
		
		URL linkURL = new URL("http://sinta2.ristekdikti.go.id/affiliations/detail?page=1&view=authors&id="+universityID);
		BufferedReader pageResult = new BufferedReader(
		new InputStreamReader(linkURL.openStream()));
		
		// ONE PAGE, READ LINE BY LINE
		while ((inputLine = pageResult.readLine()) != null) {
			if(inputLine.contains("<caption>Page")) {
				startIndex = inputLine.indexOf("of") + 3;
				endIndex = inputLine.indexOf("|", startIndex + 1);
				endPage = Integer.parseInt(inputLine.substring(startIndex, endIndex).trim());
			}
		}
        pageResult.close();

        return endPage;	
	}
	
	private static void getProfileData(LecturerDataModel newLecturer) throws IOException {
		int startIndex = 0, endIndex = 0;
		String profileLine = null, previousLine = null;
		
		URL profileURL = new URL("http://sinta2.ristekdikti.go.id/authors/detail?id=" + newLecturer.getIdSinta() + "&view=overview");
		BufferedReader inProfile = new BufferedReader(new InputStreamReader(profileURL.openStream()));
		
		while((profileLine = inProfile.readLine()) != null) {
			// GET TOTAL BOOKS
			if(profileLine.contains("<div class=\"stat2-lbl\">Books</div>")) {
				startIndex = previousLine.indexOf(">") + 1;
				endIndex = previousLine.indexOf("<", startIndex + 1);
				newLecturer.setBookTotal(previousLine.substring(startIndex, endIndex).trim());
			}
			// GET TOTAL IPR
			if(profileLine.contains("<div class=\"stat2-lbl\">IPR</div>")) {
				startIndex = previousLine.indexOf(">") + 1;
				endIndex = previousLine.indexOf("<", startIndex + 1);
				newLecturer.setIntellectualProperty(previousLine.substring(startIndex, endIndex).trim());				
			}
			// SCOPUS DATA
			if(profileLine.contains("<div class=\"uk-width-1-6 uk-row-first stat-lbl-pub\"><img class=\"stat-logo\" src=\"/assets/img/scopus_logo.png\"/></div>")){
				// SCOPUS DOCUMENT
				profileLine = inProfile.readLine();
				startIndex = profileLine.indexOf(">") + 1;
        		endIndex = profileLine.indexOf("<", startIndex + 1);
        		newLecturer.setScopusDocument(profileLine.substring(startIndex, endIndex).trim());

				// SCOPUS CITATION
				profileLine = inProfile.readLine();
				startIndex = profileLine.indexOf(">") + 1;
        		endIndex = profileLine.indexOf("<", startIndex + 1);
        		newLecturer.setScopusCitation(profileLine.substring(startIndex, endIndex).trim());

        		// SCOPUS H-Index
				profileLine = inProfile.readLine();
				startIndex = profileLine.indexOf(">") + 1;
        		endIndex = profileLine.indexOf("<", startIndex + 1);
        		newLecturer.setScopusHIndex(profileLine.substring(startIndex, endIndex).trim());
        		
				// SCOPUS i10-Index
				profileLine = inProfile.readLine();
				startIndex = profileLine.indexOf(">") + 1;
        		endIndex = profileLine.indexOf("<", startIndex + 1);
        		newLecturer.setScopusI10Index(profileLine.substring(startIndex, endIndex).trim());
			}
			// GET PREVIOUS LINE FOR BOOKS AND IPR DATA
			previousLine = profileLine;
		}
	}
}