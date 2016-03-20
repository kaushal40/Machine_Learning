import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class LogisticRegression {
	
	static HashMap<String, Integer> indexMap = new HashMap<String, Integer>();
	
	static HashMap<String, Integer> indexMapPost = new HashMap<String, Integer>();
	
	static HashMap<String, Integer> stopWords = new HashMap<String, Integer>();
	
	static String filePathHam;
	
	static String filePathSpam;
	
	static int hamDocNumber = 0, spamDocNumber = 0;


	public static void main(String[] args) throws IOException {

		String currLine;
		
		filePathHam = args[0];
		filePathSpam = args[1];
		
		stopWords = formStopWordsList(args[4]);
		
		File folder = new File(filePathHam);
		File[] listOfFilesHam = folder.listFiles();		
		hamDocNumber = listOfFilesHam.length;

		folder = new File(filePathSpam);
		File[] listOfFilesSpam = folder.listFiles();
		spamDocNumber = listOfFilesSpam.length;
		
		constructIndexMap(indexMap, false);
		constructIndexMap(indexMapPost, true);
		
		double matrix[][] = new double[hamDocNumber + spamDocNumber][indexMap.size()];
		double matrixPost[][] = new double[hamDocNumber + spamDocNumber][indexMapPost.size()];

		matrix = constructMainMatrix(indexMap, matrix);
		matrixPost = constructMainMatrix(indexMapPost, matrixPost);
		
		double[] weights = new double[indexMap.size() + 1];
		double[] weightsPost = new double[indexMapPost.size() + 1];
		
		Random rand = new Random();
		
		for(int i = 0; i < weights.length; i++)
			weights[i] = -1 + (1 - (-1)) * rand.nextDouble();
		
		for(int i = 0; i < weightsPost.length; i++)
			weightsPost[i] = -1 + (1 - (-1)) * rand.nextDouble();
		
		int noOfIterations = Integer.parseInt(args[5]), k;
		double lambda = Double.parseDouble(args[7]);
		double eta = Double.parseDouble(args[6]);
		
		System.out.println("For - Iteration:" + noOfIterations + " ETA:" + eta + " Lambda:"+ lambda);
		System.out.println("----------------------------------------------------------------------");
		System.out.println("");
		
		double z = 0, sum = 0, zPost = 0, sumPost = 0;
		double[] probability = new double[hamDocNumber + spamDocNumber];
		double[] probabilityPost = new double[hamDocNumber + spamDocNumber];
		
		for(int i = 0; i< noOfIterations; i++)
		{
			sum = 0;
			sumPost = 0;
					
			for(k = 0; k < listOfFilesHam.length; k++)
			{
				z = 0;
				zPost = 0;
				File file = listOfFilesHam[k];
				
				if(file.isFile() && file.getName().contains("txt"))
				{
					BufferedReader br = new BufferedReader(new FileReader(filePathHam + "/" + file.getName()));
					while((currLine = br.readLine()) != null)
					{
						String[] lineArray = currLine.split(" ");
						
						for(int j = 0; j< lineArray.length; j++)
						{
							if(lineArray[j].equals(":") || lineArray[j].equals(",") || lineArray[j].equals("-") || lineArray[j].equals("(") || lineArray[j].equals(")") 
									 || lineArray[j].equals("'") || lineArray[j].equals(".") || lineArray[j].equals("/") || lineArray[j].equals("*") || lineArray[j].equals("$")
									 || lineArray[j].equals("#") || lineArray[j].equals("&") || lineArray[j].equals("%") || lineArray[j].equals("!") || lineArray[j].equals(";")
									 || lineArray[j].equals("?") || lineArray[j].equals("<") || lineArray[j].equals(">") || lineArray[j].equals("=") || lineArray[j].equals("@"))
							{
								
							}
							else if(indexMapPost.get(lineArray[j]) == null)
							{
								
							}

							else
							{
								z = z + (weights[indexMap.get(lineArray[j])] * (double)matrix[k][indexMap.get(lineArray[j])]);
								zPost = zPost + (weightsPost[indexMapPost.get(lineArray[j])] * (double)matrixPost[k][indexMapPost.get(lineArray[j])]);
							}
						}
					}
				}
				
				z = z + weights[0];
				zPost = zPost + weightsPost[0];
				
				if(z > 100.0)
					probability[k] = 1.0;
				
				else if(z < -100.0)
					probability[k] = 0.0;
				
				else
					probability[k] = (Math.exp(z))/(1 + Math.exp(z));
				
				
				if(zPost > 100.0)
					probabilityPost[k] = 1.0;
				
				else if(zPost < -100.0)
					probabilityPost[k] = 0.0;
				
				else
					probabilityPost[k] = (Math.exp(zPost))/(1 + Math.exp(zPost));
			}

			for(int l = 0; l < listOfFilesSpam.length; l++)
			{
				z = 0;
				zPost = 0;
				File file = listOfFilesSpam[l];
				
				if(file.isFile() && file.getName().contains("txt"))
				{
					BufferedReader br = new BufferedReader(new FileReader(filePathSpam + "/" + file.getName()));
					while((currLine = br.readLine()) != null)
					{
						String[] lineArray = currLine.split(" ");
						
						for(int j = 1; j< lineArray.length; j++)
						{
							if(lineArray[j].equals(":") || lineArray[j].equals(",") || lineArray[j].equals("-") || lineArray[j].equals("(") || lineArray[j].equals(")") 
									 || lineArray[j].equals("'") || lineArray[j].equals(".") || lineArray[j].equals("/") || lineArray[j].equals("*") || lineArray[j].equals("$")
									 || lineArray[j].equals("#") || lineArray[j].equals("&") || lineArray[j].equals("%") || lineArray[j].equals("!") || lineArray[j].equals(";")
									 || lineArray[j].equals("?") || lineArray[j].equals("<") || lineArray[j].equals(">") || lineArray[j].equals("=") || lineArray[j].equals("@"))
							{
								
							}
							else if(indexMapPost.get(lineArray[j]) == null)
							{
								
							}

							else
							{
								z = z + (weights[indexMap.get(lineArray[j])] * matrix[k][indexMap.get(lineArray[j])]);
								zPost = zPost + (weightsPost[indexMapPost.get(lineArray[j])] * (double)matrixPost[k][indexMapPost.get(lineArray[j])]);
							}
						}
					}
				}
				z = z + weights[0];
				zPost = zPost + weightsPost[0];

				if( z > 100.0)
					probability[k] = 1.0;
				
				else if(z < -100.0)
					probability[k] = 0.0;
				
				else
					probability[k] = (Math.exp(z))/(1 + Math.exp(z));
				
				
				if(zPost > 100.0)
					probabilityPost[k] = 1.0;
				
				else if(zPost < -100.0)
					probabilityPost[k] = 0.0;
				
				else
					probabilityPost[k] = (Math.exp(zPost))/(1 + Math.exp(zPost));

				k++;	
			}
			
			int k1 = 0;
			int i1;
			
			for(i1 = 1; i1 < indexMap.size(); i1++)
			{			
				for(k1 = 0; k1 < hamDocNumber + spamDocNumber; k1++)
				{
					sum = sum + ((double)matrix[k1][i1] * (1 - probability[k1]));

				}
				
				weights[i1] = weights[i1] + (eta * sum) - (lambda * eta * weights[i1]);
			}
			
			for(i1 = 1; i1< indexMapPost.size(); i1++)
			{
				for(k1 = 0; k1 < hamDocNumber + spamDocNumber; k1++)
					sumPost = sumPost + ((double)matrixPost[k1][i1] * (1 - probabilityPost[k1]));
				
				weightsPost[i1] = weightsPost[i1] + (eta * sumPost) - (lambda * eta * weightsPost[i1]);
			}
		}	

		System.out.println("Output before removal of stop words: ");
		performTesting(weights, matrix, indexMap, args[2], args[3]);
		System.out.println("-------------------------------------------");
		System.out.println("Output after removal of stop words: ");
		performTesting(weightsPost, matrixPost, indexMapPost, args[2], args[3]);
	}
	
	public static void performTesting(double weights[], double matrix[][], HashMap<String, Integer> indexMap, String hamPath, String spamPath) throws IOException
	{
		String testHamPath = hamPath;
		String testSpamPath = spamPath;
		String currLine;
		int hamFound = 0;
		int spamFound = 0;
		double z = 0;

		File folder = new File(testHamPath);
		File[] listOfFiles = folder.listFiles();

		double[] foundHamProbability = new double[listOfFiles.length];
		
		
		for(int i = 0; i< listOfFiles.length; i++)
		{
			File file = listOfFiles[i];
			z = 0;
			
			if(file.isFile() && file.getName().contains("txt"))
			{
				BufferedReader br = new BufferedReader(new FileReader(testHamPath + "/" + file.getName()));

				while((currLine = br.readLine()) != null)
				{
					String[] lineArray = currLine.split(" ");
					
					for(int j = 1; j< lineArray.length; j++)
					{
						if(lineArray[j].equals(":") || lineArray[j].equals(",") || lineArray[j].equals("-") || lineArray[j].equals("(") || lineArray[j].equals(")") 
								 || lineArray[j].equals("'") || lineArray[j].equals(".") || lineArray[j].equals("/") || lineArray[j].equals("*") || lineArray[j].equals("$")
								 || lineArray[j].equals("#") || lineArray[j].equals("&") || lineArray[j].equals("%") || lineArray[j].equals("!") || lineArray[j].equals(";")
								 || lineArray[j].equals("?") || lineArray[j].equals("<") || lineArray[j].equals(">") || lineArray[j].equals("=") || lineArray[j].equals("@"))
						{
							
						}
						else
						{
							if(indexMap.get(lineArray[j]) == null)
							{
								
							}
								
							else
								z = z + (weights[indexMap.get(lineArray[j])] * matrix[i][indexMap.get(lineArray[j])]);
						}
					}
				}	
			}
			
			z = z + weights[0];

			foundHamProbability[i] = z;
			
			if(foundHamProbability[i] > (1 - foundHamProbability[i]))
				hamFound++;
		}
		
		System.out.println("Total ham test files = " + listOfFiles.length);
		System.out.println("Total files classified = " + hamFound);
		System.out.println("Accuracy = " + (double) hamFound/(double) listOfFiles.length * 100.0);
		
		z = 0;

		folder = new File(testSpamPath);
		listOfFiles = folder.listFiles();

		double[] foundSpamProbability = new double[listOfFiles.length];
		
		
		for(int i = 0; i< listOfFiles.length; i++)
		{
			File file = listOfFiles[i];
			z = 0;
			
			if(file.isFile() && file.getName().contains("txt"))
			{
				BufferedReader br = new BufferedReader(new FileReader(testSpamPath + "/" + file.getName()));

				while((currLine = br.readLine()) != null)
				{
					String[] lineArray = currLine.split(" ");
					
					for(int j = 1; j< lineArray.length; j++)
					{
						if(lineArray[j].equals(":") || lineArray[j].equals(",") || lineArray[j].equals("-") || lineArray[j].equals("(") || lineArray[j].equals(")") 
								 || lineArray[j].equals("'") || lineArray[j].equals(".") || lineArray[j].equals("/") || lineArray[j].equals("*") || lineArray[j].equals("$")
								 || lineArray[j].equals("#") || lineArray[j].equals("&") || lineArray[j].equals("%") || lineArray[j].equals("!") || lineArray[j].equals(";")
								 || lineArray[j].equals("?") || lineArray[j].equals("<") || lineArray[j].equals(">") || lineArray[j].equals("=") || lineArray[j].equals("@"))
						{
							
						}
						else
						{
							if(indexMap.get(lineArray[j]) == null)
							{
								
							}
								
							else
								z = z + (weights[indexMap.get(lineArray[j])] * matrix[i][indexMap.get(lineArray[j])]);
						}
					}
				}	
			}
			
			z = z + weights[0];

			foundSpamProbability[i] = z;
			
			if(foundSpamProbability[i] > (1 - foundSpamProbability[i]))
				spamFound++;
		}
		
		System.out.println("Total spam test files is = " + listOfFiles.length);
		System.out.println("Total files classified = " + spamFound);
		System.out.println("Accuracy = " + (double) spamFound/(double) listOfFiles.length * 100.0);
	}
	
	public static void constructIndexMap(HashMap<String, Integer> indexMap, boolean toTest) throws IOException
	{
		String currLine;
		File folder = new File(filePathHam);
		File[] listOfFiles = folder.listFiles();
		int index = 0;
		
		for(int i = 0; i< listOfFiles.length; i++)
		{
			File file = listOfFiles[i];
			
			
			if(file.isFile() && file.getName().contains("txt"))
			{
				BufferedReader br = new BufferedReader(new FileReader(filePathHam + "/" + file.getName()));

				while((currLine = br.readLine()) != null)
				{
					String[] lineArray = currLine.split(" ");
					
					for(int j = 0; j< lineArray.length; j++)
					{
						if(lineArray[j].equals(":") || lineArray[j].equals(",") || lineArray[j].equals("-") || lineArray[j].equals("(") || lineArray[j].equals(")") 
								 || lineArray[j].equals("'") || lineArray[j].equals(".") || lineArray[j].equals("/") || lineArray[j].equals("*") || lineArray[j].equals("$")
								 || lineArray[j].equals("#") || lineArray[j].equals("&") || lineArray[j].equals("%") || lineArray[j].equals("!") || lineArray[j].equals(";")
								 || lineArray[j].equals("?") || lineArray[j].equals("<") || lineArray[j].equals(">") || lineArray[j].equals("=") || lineArray[j].equals("@"))
						{
							
						}
						else if(stopWords.get(lineArray[j]) != null && toTest == true)
						{
							
						}
						
						else
						{
							if(indexMap.containsKey(lineArray[j]))
							{
								
							}
							else
							{
								indexMap.put(lineArray[j], index);
								index++;
							}
						}
					}
				}
			}
		}
		
		folder = new File(filePathSpam);
		listOfFiles = folder.listFiles();

		for(int i = 0; i< listOfFiles.length; i++)
		{
			File file = listOfFiles[i];
			
			
			if(file.isFile() && file.getName().contains("txt"))
			{
				BufferedReader br = new BufferedReader(new FileReader(filePathSpam + "/" + file.getName()));

				while((currLine = br.readLine()) != null)
				{
					String[] lineArray = currLine.split(" ");
					
					for(int j = 0; j< lineArray.length; j++)
					{
						if(lineArray[j].equals(":") || lineArray[j].equals(",") || lineArray[j].equals("-") || lineArray[j].equals("(") || lineArray[j].equals(")") 
								 || lineArray[j].equals("'") || lineArray[j].equals(".") || lineArray[j].equals("/") || lineArray[j].equals("*") || lineArray[j].equals("$")
								 || lineArray[j].equals("#") || lineArray[j].equals("&") || lineArray[j].equals("%") || lineArray[j].equals("!") || lineArray[j].equals(";")
								 || lineArray[j].equals("?") || lineArray[j].equals("<") || lineArray[j].equals(">") || lineArray[j].equals("=") || lineArray[j].equals("@"))
						{
							
						}
						else
						{
							if(indexMap.containsKey(lineArray[j]))
							{
								
							}
							else
							{
								indexMap.put(lineArray[j], index);
								index++;
							}
						}
					}
				}
			}
		}
	}
	
	public static double[][] constructMainMatrix(HashMap<String, Integer> indexMap, double matrix[][]) throws IOException
	{
		String currLine;
		File folder = new File(filePathHam);
		File[] listOfFiles = folder.listFiles();
		int index = 0;
		
		for(int i = 0; i< listOfFiles.length; i++)
		{
			File file = listOfFiles[i];
			
			if(file.isFile() && file.getName().contains("txt"))
			{
				BufferedReader br = new BufferedReader(new FileReader(filePathHam + "/" + file.getName()));

				while((currLine = br.readLine()) != null)
				{
					String[] lineArray = currLine.split(" ");
					
					for(int j = 0; j< lineArray.length; j++)
					{
						if(lineArray[j].equals(":") || lineArray[j].equals(",") || lineArray[j].equals("-") || lineArray[j].equals("(") || lineArray[j].equals(")") 
								 || lineArray[j].equals("'") || lineArray[j].equals(".") || lineArray[j].equals("/") || lineArray[j].equals("*") || lineArray[j].equals("$")
								 || lineArray[j].equals("#") || lineArray[j].equals("&") || lineArray[j].equals("%") || lineArray[j].equals("!") || lineArray[j].equals(";")
								 || lineArray[j].equals("?") || lineArray[j].equals("<") || lineArray[j].equals(">") || lineArray[j].equals("=") || lineArray[j].equals("@"))
						{
							
						}
						else if(indexMap.containsKey(lineArray[j]))
						{
				
							matrix[index][indexMap.get(lineArray[j]).intValue()] = matrix[index][indexMap.get(lineArray[j]).intValue()] +  1;
						}
					}
				}
			}
			index++;
		}
		
		folder = new File(filePathSpam);
		listOfFiles = folder.listFiles();
		
		for(int i = 0; i< listOfFiles.length; i++)
		{
			File file = listOfFiles[i];
			
			if(file.isFile() && file.getName().contains("txt"))
			{
				BufferedReader br = new BufferedReader(new FileReader(filePathSpam + "/" + file.getName()));

				while((currLine = br.readLine()) != null)
				{
					String[] lineArray = currLine.split(" ");
					
					for(int j = 0; j< lineArray.length; j++)
					{
						if(lineArray[j].equals(":") || lineArray[j].equals(",") || lineArray[j].equals("-") || lineArray[j].equals("(") || lineArray[j].equals(")") 
								 || lineArray[j].equals("'") || lineArray[j].equals(".") || lineArray[j].equals("/") || lineArray[j].equals("*") || lineArray[j].equals("$")
								 || lineArray[j].equals("#") || lineArray[j].equals("&") || lineArray[j].equals("%") || lineArray[j].equals("!") || lineArray[j].equals(";")
								 || lineArray[j].equals("?") || lineArray[j].equals("<") || lineArray[j].equals(">") || lineArray[j].equals("=") || lineArray[j].equals("@"))
						{
							
						}
						else if(indexMap.containsKey(lineArray[j]))
						{			
							matrix[index][indexMap.get(lineArray[j]).intValue()] = matrix[index][indexMap.get(lineArray[j]).intValue()] +  1;
						}
					}
				}
			}
			index++;
		}
		
		return matrix;
	}
	
	public static HashMap<String, Integer> formStopWordsList(String path) throws FileNotFoundException
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
