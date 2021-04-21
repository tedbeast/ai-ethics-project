package cs1555;

import java.io.*;
import java.math.*;
import java.util.*;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class globalBayes {

	public static void main(String[] args) throws IOException {
		
		HashMap<String, Long> countries = new HashMap<String, Long>();
		HashMap<String, Integer> countriesNumber = new HashMap<String, Integer>();
		List<Integer> countriesPositive = new ArrayList<Integer>();
		List<Integer> countriesNegative = new ArrayList<Integer>();
		HashMap<Integer, String> numberCountries = new HashMap<Integer, String>();
		List<HashMap<String , dictionaryEntry>> dictionary  = new ArrayList<HashMap<String,dictionaryEntry>>();
		HashMap<String, dictionaryEntry> master = new HashMap<String, dictionaryEntry>();
		long masterPos = 0;
		long masterNeg = 0;
		long totalWords = 0;
		
		BufferedReader sentimentReader = new BufferedReader(new FileReader("training.1600000.processed.noemoticon.csv"));
		BufferedReader geoReader = new BufferedReader(new FileReader("all_annotated.tsv"));
		
		BufferedWriter masterWriter = new BufferedWriter(new FileWriter("master_output.txt"));
		BufferedWriter globalWriter = new BufferedWriter(new FileWriter("global_output.txt"));
		
		BufferedWriter testWriter = new BufferedWriter(new FileWriter("test_set.txt"));
		
		String in = "";
		geoReader.readLine();
		int num = 500;
		int count = 0;
		String countryCode= "";
		int countryIncrement = 0;
		int sentiment = -1;
		String text = "";
		int skip = 0;
		boolean flag = false;
		int testSize=5;
		
		while((in = sentimentReader.readLine())!=null) {
			String[] s = in.split(",");
			sentiment = Integer.parseInt(s[0].substring(1,2));
			for(int i = 0; i < skip; i++) {
				if(sentimentReader.readLine()==null) {
					flag = true;
					i = 9999;
				}
			}
			if(flag)
				break;
			if(sentiment <2) {
				sentiment = 0;
				masterNeg++;
			}else if(sentiment >2) {
				sentiment = 1;
				masterPos++;
			}else {
				sentiment = -1;
				
			}
			if(sentiment!=-1) {
				text = cleanText(s[5]);
				//System.out.println(text);
				
				String[] splitText = text.split("\\W");
				for(int i = 0; i < splitText.length; i++) {
					//System.out.println(splitText[i]);
					if(splitText[i].length()>0&&!splitText[i].contains("http")) {
					if(master.get(splitText[i])==null){
						master.put(splitText[i], new dictionaryEntry(splitText[i]));
					}
					totalWords++;
					master.get(splitText[i]).inc(sentiment==1);
					}
				}
			}
		}
		masterWriter.append("master:"+masterPos+":"+masterNeg+"\n");
		List<String> wordlist = new ArrayList<String>(master.keySet());
		List<dictionaryEntry> dictlist = new ArrayList<dictionaryEntry>(master.values());
		for(int i = 0; i < wordlist.size(); i++) {
			masterWriter.append(wordlist.get(i)+":"+dictlist.get(i)+"/");
			
		}
		/*
		String testString1 = "im feeling so mad";
		String testString2 = "im feeling great";
		String testString3 = "i dont know how im feeling";
		String testString4 = "i love it";
		
		System.out.println(master.get("bad"));
		System.out.println(master.get("good"));
		System.out.println(getSentiment(master, testString1.split("\\W"), masterPos, masterNeg));
		System.out.println(getSentiment(master, testString2.split("\\W"), masterPos, masterNeg));
		System.out.println(getSentiment(master, testString4.split("\\W"), masterPos, masterNeg));
		*/
		//System.out.println(master);
		skip = 0;
		int countryNum = -1;
		while((in = geoReader.readLine())!=null) {
			String[] s = in.split("\t");
			
			if(Integer.parseInt(s[4])==1) {
				
				text = cleanText(s[3]);
				countryCode = s[1];
				skip++;
				if(!countries.containsKey(countryCode)) {
					countries.put(countryCode, 0L);
					countriesPositive.add(countryIncrement,0 );
					countriesNegative.add(countryIncrement,0);
					countriesNumber.put(countryCode,countryIncrement);
					numberCountries.put(countryIncrement, countryCode);
					dictionary.add(countryIncrement, new HashMap<String,dictionaryEntry>());
					countryIncrement++;
				}
				
				if(skip%testSize==0) {
					testWriter.append(countryCode+":"+text+"/");
				}
				else {
				countryNum = countriesNumber.get(countryCode);
				countries.put(countryCode, countries.get(countryCode)+1);
				sentiment = getSentiment(master, text.split("\\W"), masterPos, masterNeg);
				//System.out.println(sentiment);
				if(sentiment == 1) {
					countriesPositive.set(countryNum, (countriesPositive.get(countryNum))+1);
				}else {
					countriesNegative.set(countryNum, (countriesNegative.get(countryNum))+1);
				}
				String[] splitText = text.split("\\W");
				for(int i = 0; i < splitText.length; i++) {
					if(splitText[i].length()>0&&!splitText[i].contains("http")) {
						if(dictionary.get(countriesNumber.get(countryCode)).get(splitText[i])==null){
							dictionary.get(countriesNumber.get(countryCode)).put(splitText[i], new dictionaryEntry(splitText[i]));
						}
						(dictionary.get(countriesNumber.get(countryCode))).get(splitText[i]).inc(sentiment==1);
					}
				}
				}
			}
		}

		
		for(int i = 0; i < countryIncrement; i++) {
			//System.out.print(i+":"+numberCountries.get(i)+":"+(countriesPositive.get(i)+countriesNegative.get(i))+" ");
			if((countriesPositive.get(i)+countriesNegative.get(i))>1)  {
			globalWriter.append("#"+i+":"+numberCountries.get(i)+":"+countriesPositive.get(i)+":"+countriesNegative.get(i)+"\n");
			HashMap thisMap = dictionary.get(i);
			wordlist = new ArrayList<String>(thisMap.keySet());
			dictlist = new ArrayList<dictionaryEntry>(thisMap.values());
			for(int j = 0; j < wordlist.size();  j++) {
				
				globalWriter.append(wordlist.get(j) + ":" + dictlist.get(j)+"/");
			}
			}
		}
	}
	

static String cleanText(String in) {
	in = in.trim().toLowerCase();
	in = in.replaceAll("@\\p{L}+", "");
	in = in.replaceAll("[^a-zA-Z0-9\\s]", "");
	return in;
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

class dictionaryEntry {
	long positive;
	long negative;
	public dictionaryEntry(String w){
		positive = 0;
		negative = 0;
	}
	public void inc(boolean s) {
		if(s)
			positive++;
		else
			negative++;
	}
	public double getPos() {
		return 1.0*positive;
	}
	public double getNeg() {
		return 1.0*negative;
	}
	public double getTotal() {
		return 1.0*positive+negative;
	}
	public String toString() {
		return ""+positive + ":" + negative;
	}
}
