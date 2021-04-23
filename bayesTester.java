package cs1555;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class bayesTester {

	public static void main(String[] args) throws IOException {
		HashMap<String, Integer> countriesNumber = new HashMap<String, Integer>();
		List<Integer> countriesPositive = new ArrayList<Integer>();
		List<Integer> countriesNegative = new ArrayList<Integer>();
		HashMap<Integer, String> numberCountries = new HashMap<Integer, String>();
		List<HashMap<String , dictionaryEntry>> dictionary  = new ArrayList<HashMap<String,dictionaryEntry>>();
		HashMap<String, dictionaryEntry> master = new HashMap<String, dictionaryEntry>();
		long masterPos = 0;
		long masterNeg = 0;
		
		BufferedReader masterReader = new BufferedReader(new FileReader("master_output.txt"));
		BufferedReader testReader = new BufferedReader(new FileReader("test_set.txt"));
		BufferedReader trainReader = new BufferedReader(new FileReader("global_output.txt"));
		
		String in = "";
		in = masterReader.readLine();
		String[] splitLine = in.split(":");
		masterPos = Long.parseLong(splitLine[1]);
		masterNeg = Long.parseLong(splitLine[2]);
		List<String> wordlist = new ArrayList<String>(master.keySet());
		List<dictionaryEntry> dictlist = new ArrayList<dictionaryEntry>(master.values());
		while((in = masterReader.readLine()) != null) {
			splitLine = in.split(":");

			master.put(splitLine[0], new dictionaryEntry(Long.parseLong(splitLine[1]), Long.parseLong(splitLine[2])));
		}
		
		int countryIncrement = -1;
		while((in = trainReader.readLine()) != null) {
			splitLine = in.split(":");
			if(splitLine[0].charAt(0)=='#') {
				countryIncrement++;
				String countryCode = splitLine[1];
				countriesPositive.add(countryIncrement,Integer.parseInt(splitLine[2]) );
				countriesNegative.add(countryIncrement,Integer.parseInt(splitLine[3]) );
				countriesNumber.put(countryCode,countryIncrement);
				numberCountries.put(countryIncrement, countryCode);
				dictionary.add(countryIncrement, new HashMap<String,dictionaryEntry>());
				
			}else {
				dictionary.get(countryIncrement)
				.put(splitLine[0], new dictionaryEntry(Long.parseLong(splitLine[1]), Long.parseLong(splitLine[2])));
			}
		}
		wordlist = new ArrayList<String>(master.keySet());
		dictlist = new ArrayList<dictionaryEntry>(master.values());
		System.out.println(masterPos+":"+masterNeg);
		for(int i = 0; i < wordlist.size(); i++) {
			//System.out.println(wordlist.get(i)+":"+dictlist.get(i));
			
		}
		/*
		for(int i = 0; i < countryIncrement; i++) {
			//System.out.print(i+":"+numberCountries.get(i)+":"+(countriesPositive.get(i)+countriesNegative.get(i))+" ");
			if((countriesPositive.get(i)+countriesNegative.get(i))>1)  {
			System.out.println("#"+i+":"+numberCountries.get(i)+":"+countriesPositive.get(i)+":"+countriesNegative.get(i));
			HashMap thisMap = dictionary.get(i);
			wordlist = new ArrayList<String>(thisMap.keySet());
			dictlist = new ArrayList<dictionaryEntry>(thisMap.values());
			for(int j = 0; j < wordlist.size();  j++) {
				
				System.out.println(wordlist.get(j) + ":" + dictlist.get(j));
			}
			}
		}*/
		int correct = 0;
		int total  = 0;
		while((in=testReader.readLine())!=null) {
			splitLine = in.split(":");
			String countryCode = splitLine[0];
			String text = splitLine[1];
			
			if(countriesNumber.containsKey(countryCode)){
				int myNum = countriesNumber.get(countryCode);
				int a = getSentiment(master, text.split(" "), masterPos, masterNeg);
				int b = getSentiment(dictionary.get(myNum), text.split(" "), countriesPositive.get(myNum), countriesNegative.get(myNum));
				
				if(a==b) {
					correct++;
				}
				total++;
			}
		}
		System.out.println(correct+"/"+total);
	}
	
	

static int getSentiment(HashMap<String, dictionaryEntry> dict, String[] in, long p, long n) {
	Math.log(p/((1.0*p)+n));
	double poschance = Math.log(p/((1.0*n)+p));
	double negchance = Math.log(n/((1.0*n)+p));
	double minchance = Math.log(1/(p+n));
	double dictPos;
	double dictNeg;
	double dictTotal;
	for(int i = 0; i < in.length; i++) {
		if(!dict.containsKey(in[i])) {
			dictPos = 1;
			dictNeg = 1;
			dictTotal = p+n;
		}else {
			dictPos = dict.get(in[i]).getPos();
			dictNeg = dict.get(in[i]).getNeg();
			dictTotal = dict.get(in[i]).getNeg();
			if(dictPos == 0) {
				dictPos = 1;
			}
			if(dictNeg == 0) {
				dictNeg = 1;
			}
		}
		poschance = poschance + Math.log(dictPos/dictTotal);
		negchance = negchance + Math.log(dictNeg/dictTotal);
	}
	//System.out.println(poschance);
	//System.out.println(negchance);
	if(poschance>negchance)
		return 1;
	return 0;
}

}
