import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;


public class NaiveBayesFilter {
	
	static double totalWordsInHam = 0;
	
	static double totalWordsInSpam = 0;
	
	static double spamTotalAfterRemoval = 0;
	
	static double hamTotalAfterRemoval = 0;
	
	static HashMap<String, Integer> hamWordsAfterRemoval = new HashMap<String, Integer>();
	
	static HashMap<String, Integer> spamWordsAfterRemoval = new HashMap<String, Integer>();
	
	static HashMap<String, Integer> mergedList = new HashMap<String, Integer>();
	
	static HashMap<String, Integer> mergedListAfterRemoval = new HashMap<String, Integer>();
	
	static HashMap<String, Integer> stopWords = new HashMap<String, Integer>();
	
	static HashMap<String, Integer> hamWords = new HashMap<String, Integer>();
	
	static HashMap<String, Integer> spamWords = new HashMap<String, Integer>();

	public static void main(String[] args) throws IOException 
	{
		String filePathHam = args[0];
		
		String filePathSpam = args[1];
		
		String testPathHam = args[2];
		
		String testSpamPath = args[3];
		
		String stopWordsPath = args[4];
		
		stopWords = formStopWordsList(stopWords, stopWordsPath);
		
		hamWords = formDictionary(filePathHam, false);
		
		spamWords = formDictionary(filePathSpam, false);	
		
		hamWordsAfterRemoval = formDictionary(filePathHam, true);
		
		spamWordsAfterRemoval = formDictionary(filePathSpam, true);
		
		
		double hamDocNumber = 0, spamDocNumber = 0;
		
		double priorHam = 0, priorSpam = 0;
		
		File folder = new File(filePathHam);
		
		File[] listOfFiles = folder.listFiles();
		
		hamDocNumber = listOfFiles.length;

		folder = new File(filePathSpam);
		
		listOfFiles = folder.listFiles();
		
		spamDocNumber = listOfFiles.length;

		priorHam = hamDocNumber/(hamDocNumber + spamDocNumber);
		
		priorSpam = spamDocNumber/(hamDocNumber + spamDocNumber);
		
		HashMap<String, Double> conditionalHam = new HashMap<String, Double>();
		HashMap<String, Double> conditionalSpam = new HashMap<String, Double>();	
		HashMap<String, Double> conditionalHamAfter = new HashMap<String, Double>();
		HashMap<String, Double> conditionalSpamAfter = new HashMap<String, Double>();

		totalWordsInHam = findTotalWords(filePathHam, false);
		totalWordsInSpam = findTotalWords(filePathSpam, false);	
		hamTotalAfterRemoval = findTotalWords(filePathHam, true);
		spamTotalAfterRemoval = findTotalWords(filePathSpam, true);
		
		mergedList.putAll(hamWords);
		mergedList.putAll(spamWords);	
		mergedListAfterRemoval.putAll(hamWordsAfterRemoval);
		mergedListAfterRemoval.putAll(spamWordsAfterRemoval);
		
		conditionalHam = findConditional(hamWords, mergedList, totalWordsInHam);
		conditionalSpam = findConditional(spamWords, mergedList, totalWordsInSpam);
		conditionalHamAfter = findConditional(hamWordsAfterRemoval, mergedListAfterRemoval, hamTotalAfterRemoval);
		conditionalSpamAfter = findConditional(spamWordsAfterRemoval, mergedListAfterRemoval, spamTotalAfterRemoval);
		
		System.out.println("Without Removal of Stop Words: ");
		System.out.println("--------------------------------------------");
		System.out.println("In the ham testing folder: ");
		performTesting(conditionalHam, conditionalSpam, priorHam, priorSpam, false, testPathHam, totalWordsInHam, totalWordsInSpam, mergedList);
		
		System.out.println("\nIn the spam testing folder: ");
		performTesting(conditionalHam, conditionalSpam, priorHam, priorSpam, false, testSpamPath, totalWordsInSpam, totalWordsInSpam, mergedList);
		

		System.out.println("\nAfter Removal of Stop Words: ");
		System.out.println("--------------------------------------------");
		System.out.println("In the ham testing folder: ");
		performTesting(conditionalHamAfter, conditionalSpamAfter, priorHam, priorSpam, true, testPathHam, hamTotalAfterRemoval, spamTotalAfterRemoval, mergedListAfterRemoval);
		
		System.out.println("\nIn the spam testing folder: ");
		performTesting(conditionalHamAfter, conditionalSpamAfter, priorHam, priorSpam, true, testSpamPath, hamTotalAfterRemoval, spamTotalAfterRemoval, mergedListAfterRemoval);
		
	}
	
	public static HashMap<String, Double> findConditional(HashMap<String, Integer> list, HashMap<String, Integer> mergedList, double wordCount)
	{
		HashMap<String, Double> conditionalHam = new HashMap<String, Double>();
		
		Iterator iterate = list.keySet().iterator();

		while(iterate.hasNext())		
		{			
			String key = iterate.next().toString();
			double numerator = (double) list.get(key)  + 1;
			
			double denominator = wordCount + mergedList.size();
			conditionalHam.put(key, numerator/denominator);
		}
		
		return conditionalHam;
	}
	
	public static void performTesting(HashMap<String, Double> conditionalHam, HashMap<String, Double> conditionalSpam, double priorHam, double priorSpam, boolean toTest, String testPath, double totalWordsInHam, double totalWordsInSpam, HashMap<String, Integer> mergeList) throws IOException
	{
		String currLine = "";
		int hamClass = 0, spamClass = 0;
		
		File folder = new File(testPath);
		File[] listOfFiles = folder.listFiles();

		for(int i = 0; i< listOfFiles.length; i++)
		{
			ArrayList<String> listOfWords = new ArrayList<String>();
			File file = listOfFiles[i];
			
			if(file.isFile() && file.getName().contains("txt"))
			{
				BufferedReader br = new BufferedReader(new FileReader(testPath + "/" + file.getName()));
				
				while((currLine = br.readLine()) != null)
				{
					String[] lineArray = currLine.split(" ");

					for(int j = 0; j< lineArray.length; j++)
					{
						if(lineArray[j].length() <= 1)
						{
							
						}
						
						if(stopWords.containsKey(lineArray[j]) && toTest == true)
						{
							
						}
						else
						{
							listOfWords.add(lineArray[j]);
						}
					}
				}
			}
			
			if(calculateProbability(listOfWords, conditionalHam, conditionalSpam, priorHam, priorSpam, totalWordsInHam, totalWordsInSpam, mergeList) == 1)
				hamClass++;
			
			else
				spamClass++;
		}
		
		System.out.println("No. of ham mails = " + hamClass + "\n" + "No. of spam mails = " + spamClass);
		
		if(hamClass > spamClass)
			System.out.println("Accuracy is: " + (double) hamClass/(double) listOfFiles.length * 100.0);
		
		else
			System.out.println("Accuracy is: " + (double)spamClass/(double)listOfFiles.length * 100.0);
	}
	
	public static int calculateProbability(ArrayList<String> listOfWords, HashMap<String, Double> conditionalHam, HashMap<String, Double> conditionalSpam, double priorHam, double priorSpam, double totalWordsInHam, double totalWordsInSpam, HashMap<String, Integer> mergeList)
	{
		double valHam = 0;
		
		for(int i = 0; i< listOfWords.size(); i++)
		{
			if(conditionalHam.get(listOfWords.get(i)) != null)
				valHam = valHam + Math.log(conditionalHam.get(listOfWords.get(i)));
			
			else
				valHam = valHam + Math.log(1/(totalWordsInHam + mergeList.size()));
		}
		valHam = valHam + Math.log(priorHam);
		
		double valSpam = 0;
		
		for(int i = 0; i< listOfWords.size(); i++)
		{
			if(conditionalSpam.get(listOfWords.get(i)) != null)
				valSpam = valSpam + Math.log(conditionalSpam.get(listOfWords.get(i)));
			
			else
			{
				valSpam = valSpam + Math.log(1/(totalWordsInSpam + mergeList.size()));
			}
		}

		valSpam = valSpam + Math.log(priorSpam);
		
		if((valHam) > (valSpam))
			return 1;
		
		else
			return -1;
		
	}
	
	public static double findTotalWords(String filepath, boolean toTest) throws IOException
	{
		String currLine;
		double wordCount = 0;

		File folder = new File(filepath);
		File[] listOfFiles = folder.listFiles();
		
		for(int i = 0; i< listOfFiles.length; i++)
		{
			File file = listOfFiles[i];
			if(file.isFile() && file.getName().contains("txt"))
			{
				BufferedReader br = new BufferedReader(new FileReader(filepath + "/" + file.getName()));
				
				while((currLine = br.readLine()) != null)
				{
					String[] lineArray = currLine.split(" ");
					
					for(int j = 0; j< lineArray.length; j++)
					{
						if(lineArray[j].length() <= 1)
						{
							
						}
						
						if(stopWords.containsKey(lineArray[j]) && toTest == true)
						{
							
						}
						else
						{
							wordCount++;
						}

					}
				}
			}
		}
		return wordCount;
	}
	
	public static HashMap<String, Integer> formDictionary(String filepath, boolean toTest) throws IOException
	{
		
		String currLine;
		HashMap<String, Integer> hamWords = new HashMap<String, Integer>();
		
		File folder = new File(filepath);
		File[] listOfFiles = folder.listFiles();
		
		for(int i = 0; i< listOfFiles.length; i++)
		{
			File file = listOfFiles[i];
			if(file.isFile() && file.getName().contains("txt"))
			{
				BufferedReader br = new BufferedReader(new FileReader(filepath + "/" + file.getName()));
				
				while((currLine = br.readLine()) != null)
				{
					String[] lineArray = currLine.split(" ");
					
					for(int j = 0; j< lineArray.length; j++)
					{
						if(lineArray[j].length() <= 1)
						{
							
						}
						if(stopWords.containsKey(lineArray[j]) && toTest == true)
						{
							
						}
						else
						{
							if(hamWords.containsKey(lineArray[j]))
								hamWords.put(lineArray[j], hamWords.get(lineArray[j]) + 1);
							
							else
								hamWords.put(lineArray[j], 1);
						}
					}		
				}
			}
		}
		
		return hamWords;
	}
	
	public static HashMap<String, Integer> formStopWordsList(HashMap<String, Integer> stopWords, String path) throws FileNotFoundException
	{	
		File file = new File(path);
		Scanner scanFile = new Scanner(new FileReader(file));
		
		while(scanFile.hasNext())
		{
			stopWords.put(scanFile.next(), 1);
		}
		
		scanFile.close();
	
		return stopWords;
	}
}
