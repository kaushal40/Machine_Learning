consists of 2 Java files (1) "TreeMaker.java" (Main Method) (2) "TreeNode.java"

For code compilation, use the command: javac TreeMaker.java TreeNode.java

run using command: java TreeMaker L K training_set validation_set testing_set yes/no

e.g. java TreeMaker 10 3 data_sets2/test_set.csv data_sets1/training_set.csv data_sets1/validation_set.csv no

Terminology:

L = parameter value of L used in post pruning

K = parameter value of K used in post pruning

training_set = path of the training set to be used

validation_set = path of the validation set

testing_set = path of the testing set

yes/no = the words "yes" or "no" to print the decision tree or not