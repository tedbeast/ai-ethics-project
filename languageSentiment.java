
import java.io.*;
import java.util.*;
//import packages
public class languageSentiment{
	public static void main(String[] args){
		HashMap<String, Long> countries = new HashMap<String, Long>();
		HashMap<String, Integer> countriesNumber = new HashMap<String, Integer>();
		HashMap<String, Long> countriesPositive = new HashMap<String, Long>();
		HashMap<String, Long> countriesNegative = new HashMap<String, Long>();
		HashMap<Integer, String> numberCountries = new HashMap<Integer, String>();
		List<HashMap<String , dictionaryEntry>> dictionary  = new ArrayList<HashMap<String,dictionaryEntry>>();

		Scanner reader;
		try {
			reader = new Scanner(new File("geo_2020-04-21.json"));
		
		String in = "";
		long id = -1;
		String text = "";
		String countryCode = "";
		int sentiment = -1;
		int countryIncrement = 0;
		boolean flag = true;
		String temp;
		double maxTime = 10000;
		double currentTime = System.currentTimeMillis();
		
		while(flag = true){
			in = "  ";
			while(!in.substring(in.length()-2,in.length()).equals("]}")) {
				temp = reader.next();
				if(!reader.hasNext()) {
					System.out.println("null");
					flag = false;
					break;
				}
				in = in + temp;
			}
			if(!flag || (System.currentTimeMillis()-currentTime>maxTime))
				break;
			if(isTagged(in)) {
				id = getIDFromLine(in);
				text = getTextFromID(id);
				
				
				
				if(textIsEnglish(text)){
					countryCode = getCountryFromLine(in);
					if(!countries.containsKey(countryCode)) {
						countries.put(countryCode, (long) 0);
						countriesNumber.put(countryCode,countryIncrement);
						numberCountries.put(countryIncrement, countryCode);
						dictionary.add(new HashMap<String,dictionaryEntry>());
						countryIncrement++;
					}
					countries.put(countryCode, countries.get(countryCode)+1);
					sentiment = generateSentiment(text);
					
					
										
					String[] splitText = text.split("\\W");
					for(int i = 0; i < splitText.length; i++) {
						if(splitText[i].length()>0) {
							if(dictionary.get(countriesNumber.get(countryCode)).get(splitText[i])==null){
								dictionary.get(countriesNumber.get(countryCode)).put(splitText[i], new dictionaryEntry(splitText[i]));
							}
							(dictionary.get(countriesNumber.get(countryCode))).get(splitText[i]).inc(sentiment==1);
						}
					}
				}
			}
			
		}
		//i will make it output to file
		System.out.println(Arrays.asList(countries));
		for(int i = 0; i < countryIncrement; i++) {
			HashMap thisMap = dictionary.get(i);
			List<HashMap> thisCountryDictionary = Arrays.asList(thisMap);
			for(int j = 0; j < thisCountryDictionary.size();  j++) {
				System.out.println(numberCountries.get(i) + " : " + thisCountryDictionary.get(j));
			}
		}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
static long getIDFromLine(String in){
	return Long.parseLong(in.substring(15, in.indexOf("\",\"created_at")));
}
static boolean textIsEnglish(String in) {
	//FILL IN
	return true;
}
static String getTextFromID(long id){
	//FILL IN
	return "good nice bad,awful, excellent";
}
static boolean isTagged(String in) {
	return in.contains("country_code");
}
static String getCountryFromLine(String in){
	return in.substring(in.indexOf("country_code\":\"")+15, in.indexOf("country_code\":\"")+17);
}
static int generateSentiment(String text){
	//FILL IN
	//1 if positive, 0 if negative
	return -1;
}

}
class dictionaryEntry {
	String word;
	double positive;
	double negative;
	double total;
	double percentage;
	public dictionaryEntry(String w){
		word = w;
		positive = 0;
		negative = 0;
		total = 0;
	}
	public void inc(boolean s) {
		if(s)
			positive++;
		else
			negative++;
		total++;
	}
	public String toString() {
		return "Word " + word + " has " + positive + " positive hits, " + negative + " negative hits, " + total + " total hits ";
	}
}
