The submitted code was implemented in Java, using JDK 1.8.

---------------------------------------------------------------------------------
The Naive Bayes implementation consists of a file titled NaiveBayesFilter.java

To compile and run the code use the following commands:

>javac NaiveBayesFilter.java
>java NaiveBayesFilter <ham_trainingFolder_path> <spam_trainingFolder_path> <ham_testFolder_path> <spam_testFolder_path> <stopWords_list_path>

Example: java NaiveBayesFilter C:/train/ham C:/train/spam C:/test/ham C:/test/spam C:/stopwords.txt

----------------------------------------------------------------------------------------------------------------------------------------------------

The logistic regression implementation is implemented in a file called LogisticRegression.java
To compile and run the code, use the following commands:

>javac LogisticRegression.java
>java LogisticRegression <ham_trainingFolder_path> <spam_trainingFolder_path> <ham_testFolder_path> <spam_testFolder_path> <stopWords_list_path> <no_of_iterations> <eta> <lambda>

Example: java LogisticRegression C:/train/ham C:/train/spam C:/test/ham C:/test/spam C:/stopwords.txt 5 0.01 0.01