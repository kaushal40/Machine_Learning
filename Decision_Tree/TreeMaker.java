import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class TreeMaker {
	
	public static int value = 1;

	public static void main(String[] args) throws IOException 
	{
		
		int L = Integer.parseInt(args[0]);
		
		int K = Integer.parseInt(args[1]);
		
		String trainingSet = args[2];
		
		String validationSet = args[3];
		
		String testingSet = args[4];
		
		String toPrint = args[5];
			
		String line = "";
		
		FileInputStream fileIn = new FileInputStream(trainingSet);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(fileIn));
		
		ArrayList<String[]> trainingData = new ArrayList<String[]>();
		
		ArrayList<String> usedAttributes = new ArrayList<String>();
	
		String[] attributes = in.readLine().split(",");
		
		while((line = in.readLine()) != null)
		{
			trainingData.add(line.split(","));
		}

		double posEg = 0, negEg = 0;
		
		for(String[] str : trainingData)
		{
			if(Integer.parseInt(str[str.length - 1]) == 1)
				posEg++;
			else
				negEg++;
		}
		
		double total = posEg + negEg;
		double entropy = -((posEg/total) * findLog(posEg/total)) - ((negEg/total) * findLog(negEg/total));

		TreeNode buildRoot = new TreeNode();
		
		buildRoot = makeTrees(trainingData, usedAttributes, entropy, attributes, 0, 0);
		
		System.out.println("Pre-pruning: Decision tree Before Pruning using Info Gain");
		
		if(toPrint.equalsIgnoreCase("yes"))
			printTree(buildRoot, 0);
			
		System.out.println("information gain heuristics Accuracy: " + findAccuracy(buildRoot, testingSet) * 100);

		TreeNode postPrunedRoot = PerformPostPruning(trainingData, buildRoot, attributes, K, L, validationSet);
		
		/*System.out.println("Post-pruning: Decision tree After Pruning using Info Gain");
				
		if(toPrint.equalsIgnoreCase("yes"))
			printTree(postPrunedRoot, 0);
		*/
		System.out.println("==================================================================");
		
		double k = total;
		double k0 = negEg;
		double k1 = posEg;
		
		double variance = (k0 * k1)/(k * k);
	
		TreeNode varRoot = new TreeNode();	
		varRoot = VarImpTree(trainingData, usedAttributes, variance, attributes, 0, 0);
		
		System.out.println("Pre-pruning: Decision tree Before Pruning using Info Gain");
		
		if(toPrint.equalsIgnoreCase("yes"))
			printTree(varRoot, 0);
		
		System.out.println("variance heuristic Accuracy : " + findAccuracy(varRoot, testingSet) * 100);
		TreeNode postPrunedVariance = PerformPostPruning(trainingData, varRoot, attributes, K, L, validationSet);
		
		/*System.out.println("Post-pruning: Decision tree After Pruning using Variance Impurity");
		
		if(toPrint.equalsIgnoreCase("yes"))
			printTree(postPrunedVariance, 0);
		*/
	}
	
	public static TreeNode VarImpTree(ArrayList<String[]> trainingData, ArrayList<String> usedAtts, double variance, String[] attributes, int maxAttr, int val) 
	{
		double posEg = 0, negEg = 0;
		
		ArrayList<String> newattributes = new ArrayList<String>(usedAtts);
		
		if(trainingData.isEmpty() || newattributes.size() == attributes.length - 1)
		{
			TreeNode leaf = new TreeNode();
			
			leaf.setValue(val);
			
			return leaf;
		}

		if(variance == 0)
		{
			TreeNode leaf = new TreeNode();
			
			leaf.setValue(val);
			
			leaf.setValue(Integer.parseInt((trainingData.get(0)[trainingData.get(0).length - 1])));
		}

		
		for(String[] str : trainingData)
		{
			int end = str.length - 1;
			
			if(Integer.parseInt(str[end]) == 1)
				posEg++;
			
			if(Integer.parseInt(str[end]) == 0)
				negEg++;
		}
		
		if(posEg == (posEg + negEg))
		{
			TreeNode leaf = new TreeNode();
			
			leaf.setAttributeName(attributes[maxAttr]);
			
			leaf.setTrainData(trainingData);
			
			leaf.setValue(1);
			
			leaf.setLChild(false);
			return leaf;
		}
		
		if(negEg == (negEg + posEg))
		{
			
			TreeNode leaf = new TreeNode();
			
			leaf.setAttributeName(attributes[maxAttr]);
			
			leaf.setTrainData(trainingData);
			
			leaf.setValue(0);
			
			leaf.setLChild(true);
			
			return leaf;				
		}
		
		else
		{
			Double max = Double.valueOf(Double.NEGATIVE_INFINITY);
			int i = 0, maxIndex = 0;
			double maxgain1 = 0, maxgain0 = 0;
			
			while(i != attributes.length - 1)
			{		
				double class0Neg = 0, class0Pos = 0, class1Neg = 0, class1Pos = 0, posEgs = 0, negEgs = 0, variance0, variance1, Gain = 0;

				if(!newattributes.contains(attributes[i]))
				{
					for(String[] rec : trainingData)
					{
						int end = rec.length - 1;
						
						if(Integer.parseInt(rec[i]) == 1)
							posEgs++;
						
						else
							negEgs++;
						
						if(Integer.parseInt(rec[end]) == 1 && Integer.parseInt(rec[i]) == 1)
							class1Pos++;
						
						else if(Integer.parseInt(rec[end]) == 1 && Integer.parseInt(rec[i]) == 0)
							class1Neg++;
						
						else if(Integer.parseInt(rec[end]) == 0 && Integer.parseInt(rec[i]) == 0)
							class0Neg++;
						
						else if(Integer.parseInt(rec[end]) == 0 && Integer.parseInt(rec[i]) == 1)							
							class0Pos++;
					}
					double sum = class0Neg + class0Pos + class1Neg + class1Pos;

					variance0 = -(((class1Neg + class0Neg)/sum) * ((class1Neg * class0Neg)/((class0Neg + class1Neg) * (class0Neg + class1Neg))));
											
					variance1 = -(((class1Pos + class0Pos)/sum) * ((class1Pos * class0Pos)/((class1Pos + class0Pos) * (class1Pos + class0Pos))));
						
					Gain = variance + variance1 + variance0;
					
					if(max < Gain)
					{
						max = Gain;
						maxIndex = i;
						
						maxgain1 = variance1;
						maxgain0 = variance0;
					}
				}
				i++;			
			}
			
			ArrayList<String[]> leftChild = new ArrayList<String[]>();
			ArrayList<String[]> rightChild = new ArrayList<String[]>();
			
			newattributes.add(attributes[maxIndex]);
			
			for(String[] addRecs : trainingData)
			{
				if(Integer.parseInt(addRecs[maxIndex]) == 0)
					leftChild.add(addRecs);

				else
					rightChild.add(addRecs);
			}
			
			TreeNode node = new TreeNode();
			node.setAttributeName(attributes[maxIndex]);
			node.setTrainData(trainingData);

			node.setLeftChild(VarImpTree(leftChild, newattributes, maxgain0, attributes, maxIndex, 0));
			node.setRightChild(VarImpTree(rightChild, newattributes, maxgain1, attributes, maxIndex, 1));

			return node;
		}
	}
	
	public static TreeNode copytree(TreeNode finroot, TreeNode DPtemp) 
	{
		if (finroot.getLeftChild() == null && finroot.getRightChild() == null) 
		{
			DPtemp = new TreeNode(finroot.getTrainData(), finroot.getAttributeName(), finroot.getValue(), null, null);

		} 
		else 
		{
			DPtemp = new TreeNode(finroot.getTrainData(), finroot.getAttributeName(), finroot.getValue(), finroot.getLeftChild(), finroot.getRightChild());
			
			DPtemp.setLeftChild(copytree(finroot.getLeftChild(), DPtemp.getLeftChild()));
			DPtemp.setRightChild(copytree(finroot.getRightChild(), DPtemp.getRightChild()));
		}

		return DPtemp;
	}
	
	public static ArrayList<TreeNode> findNodeToreplacedNode(ArrayList<TreeNode> nodeListt, TreeNode finroot) {

		if (finroot != null && finroot.getLeftChild() == null && finroot.getRightChild() == null) {
			return nodeListt;

		} else if (finroot != null) {
			nodeListt.add(finroot);
			findNodeToreplacedNode(nodeListt, finroot.getLeftChild());
			findNodeToreplacedNode(nodeListt, finroot.getRightChild());
		}
		return nodeListt;
	}



	public static TreeNode PerformPostPruning(ArrayList<String[]> trainData, TreeNode D, String[] attributes, int K, int L, String path) throws IOException
	{
		TreeNode Dbest = D;
		int countnodes = 0;
		double posEg = 0, negEg = 0;

		double accuracy = findAccuracy(D, path);
		double accuracyBest = 0;
		
		for (int i = 1; i < L; i++) 
		{
			TreeNode Dtemp = null;
			Dtemp = copytree(D, Dtemp);
			
			int M = (1 + (int) (Math.random() * K));

			for (int j = 1; j < M; j++) {
				int P = 0;
				
				ArrayList<TreeNode> nodeList = new ArrayList<>();
				nodeList = findNodeToreplacedNode(nodeList, Dtemp);
				countnodes = nodeList.size() - 2;

				P = (1 + (int) (Math.random() * countnodes));
			
				if (P != 0 && nodeList.size() >= 2) 
				{
					TreeNode replacedNode = nodeList.get(P);
					
					replacedNode.setLeftChild(null);
					replacedNode.setRightChild(null);

					for(String[] s : replacedNode.getTrainData())
					{
						if(Integer.parseInt(s[s.length -1]) == 0)
							negEg++;
						
						else
							posEg++;
					}
					
					if(posEg > negEg)
						replacedNode.setValue(1);
					else
						replacedNode.setValue(0);			
				}

			}

			accuracyBest = findAccuracy(Dtemp, path);
			if (accuracyBest > accuracy) {
				accuracy = accuracyBest;
				copytree(Dbest, Dtemp);
				// Dtemp = null;
			}
		}

		System.out.println("Accuracy after pruning is : " + accuracy * 100);
		return Dbest;
		
		
	}
			
	public static int findNonLeafNodes(TreeNode n1)
	{
		if(n1 == null)
			return 0;
		
		if(n1.getLeftChild() == null && n1.getRightChild() == null)
			return 1;
		
		else
			return findNonLeafNodes(n1.getLeftChild()) + findNonLeafNodes(n1.getRightChild());
	}
	
	public static TreeNode findNodeToreplacedNode(int p, TreeNode n1)
	{
		if(n1 != null)
		{
			if(n1.getValue() == p)
				return n1;
			
			else 
			{
				TreeNode foundNode = findNodeToreplacedNode(p, n1.getLeftChild());
				if(foundNode == null)
					foundNode = findNodeToreplacedNode(p, n1.getRightChild());
			
				return foundNode;
			}			
		}
		else
			return null;
		
	}
	
	public static int traverseToGetValue(String[] string1, TreeNode n1, String[] attributeList)
	{
		int val = 0;
		
		if(n1 == null)
			return 0;
		 
		if(n1.getLeftChild() == null && n1.getRightChild() == null)
			return n1.getValue();
		
		else
		{
			String attr = n1.getAttributeName();
		
			for(int i = 0; i< attributeList.length; i++)
			{
				if(attributeList[i].equalsIgnoreCase(attr))
				{
					val = Integer.parseInt(string1[i]);
					break;
				}
			}
			
			if(val == 0)
				return traverseToGetValue(string1, n1.getLeftChild(), attributeList);
			
			else
				return traverseToGetValue(string1, n1.getRightChild(), attributeList);
			
		}
	}
	
	public static double findAccuracy(TreeNode root, String path) throws IOException
	{
		String line = "";
		
		ArrayList<String[]> validationData = new ArrayList<String[]>();
		
		double posClass = 0, negClass = 0;
	
		FileInputStream fin = new FileInputStream(path);
		
		BufferedReader input = new BufferedReader(new InputStreamReader(fin));	
		
		String[] attributesList = input.readLine().split(",");
		
		while((line = input.readLine()) != null)
		{
			validationData.add(line.split(","));
		}
		
		for(int i = 0; i< validationData.size(); i++)
		{
			String[] str = validationData.get(i);
			
			int classifiedValue = traverseToGetValue(str, root, attributesList);
			
			if(classifiedValue == Integer.parseInt(str[str.length - 1]))
				posClass++;
			else
				negClass++;
		}

		double accuracy = posClass/(posClass + negClass);

		return accuracy;
	}
	
	public static TreeNode makeTrees(ArrayList<String[]> trainData, ArrayList<String> usedAttributes, double entropy, String[] attributes, int maxAttr, int val) 
	{
		double posEg = 0, negEg = 0;
		ArrayList<String> newattributes = new ArrayList<String>(usedAttributes);
		
		if(newattributes.size() == attributes.length - 1 || trainData.isEmpty())
		{
			TreeNode leaf = new TreeNode();
			
			leaf.setValue(val);
			return leaf;
		}

		if(entropy == 0)
		{
			TreeNode leaf = new TreeNode();
			leaf.setValue(val);
			leaf.setValue(Integer.parseInt((trainData.get(0)[trainData.get(0).length - 1])));
		}
		
		for(String[] str : trainData)
		{
			int end = str.length - 1;
			
			if(Integer.parseInt(str[end]) == 1)
				posEg++;
			
			if(Integer.parseInt(str[end]) == 0)
				negEg++;
		}
		if(posEg == (posEg + negEg))
		{
			TreeNode leaf = new TreeNode();
			
			leaf.setAttributeName(attributes[maxAttr]);
			
			leaf.setTrainData(trainData);
			
			leaf.setValue(1);
			
			leaf.setLChild(false);
			
			return leaf;
		}
		if(negEg == (negEg + posEg))
		{
			TreeNode leaf = new TreeNode();
			
			leaf.setAttributeName(attributes[maxAttr]);
			
			leaf.setTrainData(trainData);
			
			leaf.setValue(0);
			
			leaf.setLChild(true);
			
			return leaf;				
		}
		
		if(posEg == 0 && negEg == 0)
		{
			return null;
		}
		
		else
		{
			double entropy1 = 0, entropy0 = 0, Gain = 0, maxentropy1 = 0, maxentropy0 = 0;
			
			Double max = Double.valueOf(Double.NEGATIVE_INFINITY);
			
			String maxGain = "";
			
			int i = 0, maxIndex = 0;

			while(i != attributes.length - 1)
			{		
				double class0Neg = 0, class0Pos = 0, class1Neg = 0, class1Pos = 0, posEgs = 0, negEgs = 0;

				if(!newattributes.contains(attributes[i]))
				{
					for(String[] rec : trainData)
					{
						int end = rec.length - 1;
						
						if(Integer.parseInt(rec[i]) == 1)
							posEgs++;
						
						else
							negEgs++;
						
						if(Integer.parseInt(rec[end]) == 1 && Integer.parseInt(rec[i]) == 1)
							class1Pos++;
						
						else if(Integer.parseInt(rec[end]) == 1 && Integer.parseInt(rec[i]) == 0)
							class0Pos++;
						
						else if(Integer.parseInt(rec[end]) == 0 && Integer.parseInt(rec[i]) == 0)
							class0Neg++;
						
						else if(Integer.parseInt(rec[end]) == 0 && Integer.parseInt(rec[i]) == 1)							
							class1Neg++;
					}
					
					if(class1Pos == 0 || class1Neg == 0)
						entropy1 = 0;				
					else
						entropy1 = - ((class1Pos/(class1Pos + class1Neg)) * findLog((class1Pos/(class1Pos + class1Neg)))) - ((class1Neg/(class1Pos + class1Neg)) * findLog((class1Neg/(class1Neg + class1Pos))));                

					
					if(class0Pos == 0 || class0Neg == 0)
						entropy0 = 0;
					else
						entropy0 = -((class0Neg/(class0Neg + class0Pos)) * findLog((class0Neg/(class0Neg + class0Pos)))) - ((class0Pos/(class0Pos + class0Neg)) * findLog((class0Pos/(class0Pos + class0Neg))));

					Gain = entropy - (((posEgs/(posEgs + negEgs)) * entropy1)) - (((negEgs/(posEgs + negEgs)) * entropy0));

					if(max < Gain)
					{
						max = Gain;
						maxGain = attributes[i];
						maxIndex = i;
						
						maxentropy1 = entropy1;
						maxentropy0 = entropy0;
					}

				}
				i++;
			}
			
			ArrayList<String[]> leftChild = new ArrayList<String[]>();
			ArrayList<String[]> rightChild = new ArrayList<String[]>();
			
			newattributes.add(attributes[maxIndex]);
			
			for(String[] addRecs : trainData)
			{
				if(Integer.parseInt(addRecs[maxIndex]) == 0)
					leftChild.add(addRecs);

				else
					rightChild.add(addRecs);
			}
			
			TreeNode node = new TreeNode();
			node.setAttributeName(attributes[maxIndex]);
			node.setTrainData(trainData);
			
			
			node.setLeftChild(makeTrees(leftChild, newattributes, maxentropy0, attributes, maxIndex, 0));
			node.setRightChild(makeTrees(rightChild, newattributes, maxentropy1, attributes, maxIndex, 1));
			
			return node;
		}
	}


	public static void printTree(TreeNode root, int h1)
	{
		if(root == null)
			return;

		if(root.getLeftChild()!=null)
		{
			for(int i = 0; i< h1; i++)
				System.out.print("  |  ");
			
			String temp = "";
			
			if(root.getLeftChild().getLeftChild() == null)
				temp = " : " + root.getLeftChild().getValue();
			
			System.out.println(root.getAttributeName() + " = 0" + temp);
		}
			
		printTree(root.getLeftChild(), h1 + 1);

		if(root.getRightChild()!=null)
		{
			for(int i = 0; i< h1; i++)
				System.out.print("  |  ");
				
			String temp = "";
				
			if(root.getRightChild().getRightChild() == null)
				temp = " : " + root.getRightChild().getValue();
				
			System.out.println(root.getAttributeName() + " = 1" + temp);

			}
		printTree(root.getRightChild(), h1 + 1);
	}
	
	public static double findLog(double arg)
	{
		double temp = (Math.log(arg) / Math.log(2));
		
		return temp;
	}
}
