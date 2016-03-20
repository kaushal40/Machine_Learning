import java.util.ArrayList;


public class TreeNode {
	ArrayList<String[]> trainingData;
	
	int value;
	
	String attributeName;
	
	boolean isLtChild;
	
	TreeNode leftChildren, rightChildren;
	
	public boolean getisLChild() {
		return isLtChild;
	}

	public void setLChild(boolean isLChild) {
		this.isLtChild = isLChild;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	TreeNode()
	{
		
	}
	
	TreeNode(ArrayList<String[]> Data, String attrName, int val, TreeNode leftCh, TreeNode rightCh)
	{
		this.trainingData = Data;
		this.attributeName = attrName;
		this.setLeftChild(leftCh);
		this.setRightChild(rightCh);
	}

	TreeNode(TreeNode node)
	{
		
	}
		
	public String getAttributeName() {
		return attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public ArrayList<String[]> getTrainData() {
		return trainingData;
	}
	public void setTrainData(ArrayList<String[]> trainData) {
		this.trainingData = trainData;
	}
	public TreeNode getLeftChild() {
		return leftChildren;
	}
	public void setLeftChild(TreeNode leftChild) {
		this.leftChildren = leftChild;
	}
	public TreeNode getRightChild() {
		return rightChildren;
	}
	public void setRightChild(TreeNode rightChild) {
		this.rightChildren = rightChild;
	}
}
