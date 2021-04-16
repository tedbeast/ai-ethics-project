
import java.io.*;
import java.util.*;
//import packages
public class languageSentiment{
	public static void main(String[] args){
		Scanner reader;
		try {
			reader = new Scanner(new File("tweetin.csv"));
		
		String in = "";
		long id = -1;
		String text = "";
		String countryCode = "";
		int sentiment = -1;
		while((in = reader.next())!=null){
			id = getIDFromLine(in);
			text = getTextFromID(id);
			if(textIsEnglish(text)){
				countryCode = getCountryFromLine(in);
				sentiment = generateSentiment(text);
				addToJDBC(id, text, countryCode, sentiment);
			}
		}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
static long getIDFromLine(String in){
	return -1;
}
static boolean textIsEnglish(String in) {
	return false;
}
static String getTextFromID(long id){
	return "";
}
static String getCountryFromLine(String in){
	return "";
}
static int generateSentiment(String text){
	return -1;
}
static boolean addToJDBC(long id, String text, String countryCode, int sentiment){
	return false;
}
}