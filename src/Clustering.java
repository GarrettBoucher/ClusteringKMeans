//Program:	Clustering.java
//Course:	COSC460
//Description:	This program implements the iterative k-means clustering algorithm. Input is a text file with comma 
//              delimited data where the first column is a record ID, and the following 8 columns represent the values 
//              in the 8-dimensional space to be clustered.
//Author:	Garrett Boucher 
//Revised:	10/28/15
//Language:	Java
//IDE:		NetBeans 8.0.2
//**********************************************************************************************************************
//**********************************************************************************************************************
//Class:        Clustering
//Description:	Takes as an input a user defined text file containing a set of 8-dimensional data records as well as a
//              record ID.  The class then provides the capability to run the iterative k-means clustering algorithm.
//              The output displays each data record under a heading of the appropriate cluster.  The algorithm 
//              continues to run until complete convergence, or until 50 iterations have been completed at which point
//              the user is prompted for further instructions (continue or halt).
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
public class Clustering {
    public static boolean fileRead = false;        //if a file has been read yet
    public static int[][] dataPoints;              //the array of data
    public static int iterationCount;
    public static boolean normalizedValues;        //if the user chose to normalize data
    public static double[][] normalizedDataPoints; //the array of normalized data
    public static int numberOfClusters;            //the number of clusters, 'k'
    public static double[][] means;                //the set of means; it will have 'k' rows; does not contain index
    public static double columnMaxValue;           //the max value of the current column
    public static double columnMinValue;           //the min value of the current column
    public static int[][] clusterAssignment;       //array consisting of data ID, the cluster number to which that 
                                                     //record belongs, and the cluster to which it belonged in the
                                                     //previous iteration
    //******************************************************************************************************************
    //Method:		main  
    //Description:	Provides an interface for the user to continuously choose to read a text file, execute k-means
    //                  clustering for the most recently read text file, or exit the program. A user cannot attempt
    //                  to execute clustering without reading in a text file. The method also prompts the user for the
    //                  number of clusters and whether values should be normalized.
    //Parameters:	none
    //Returns:		nothing
    //Throws:		nothing
    //Calls:		executeClustering
    public static void main(String[] args) {
        System.out.println("K-means Clustering: Garrett Boucher");

        KeyboardInputClass keyboardInput = new KeyboardInputClass();
        int userInput = 0;

        while (userInput != 3) {

            userInput = keyboardInput.getInteger(true, 2, 1, 3, "\n"
                    + "1. Retrieve a text file containing cluster data\n"
                    + "2. Execute K-means clustering\n"
                    + "3. Exit");

            if (userInput == 1) {
                TextFileClass textFile = new TextFileClass();
                textFile.getFileName("\nSpecify the text file to be read: ");
                textFile.getFileContents();
                fileRead = true;
                parseData(textFile.text, textFile.lineCount);
                System.out.println("");
                System.out.println("Data: ");
                printIntArray(dataPoints);

            }

            if (userInput == 2) {
                iterationCount = 0;
                normalizedValues = false;

                if (!fileRead) {
                    System.out.println("\nPlease retrieve data from a text file before  "
                            + "attempting to execute clustering\n");
                } else {
                    int userInput2;
                    userInput2 = keyboardInput.getInteger(true, 2, 1, 100, 
                            "\nSpecify the number of clusters (default = 2): ");
                    numberOfClusters = userInput2;

                    userInput2 = keyboardInput.getInteger(true, 1, 1, 2, "\nUse normalized values? Default = 1"
                            + "\n1. Yes"
                            + "\n2. No");

                    if (userInput2 == 1) {
                        normalizedValues = true;
                    }
                    executeClustering();
                }
            }
        }
    }
    //******************************************************************************************************************
    //Method:		executeClustering  
    //Description:	Provides a shell that begins the k-means clustering algorithm. Prompts the user for instructions
    //                  if the max iteration count (50) was reached without complete convergence.
    //Parameters:	none
    //Returns:		nothing
    //Throws:		nothing
    //Calls:		normalizeData, printDoubleArray, getRandomMeans, initializeClusterAssignment, 
    //                  performClusteringLoop, outputClusters
    public static void executeClustering() {
        if (normalizedValues) {
            normalizeData();
            System.out.println("Normalized Data:");
            printDoubleArray(normalizedDataPoints);
        }
        getRandomMeans();
        System.out.println("\nRandom Means:");
        printDoubleArray(means);
        System.out.println("\nWorking...");
        initializeClusterAssignment();

        boolean completelyConverged = false;
        completelyConverged = performClusteringLoop(completelyConverged);
        
        while(!completelyConverged){
            outputClusters();
            KeyboardInputClass keyboardInput = new KeyboardInputClass();
            char userInput = keyboardInput.getCharacter(true, 'Y', "YN", 1, 
                    "After 50 iterations, the algorithm has not completely converged, resulting"
                    + " in the clusters above."
                    + "\nWould you like to continue iterating? (Y/N): ");
            if(userInput == 'N'){
                break;
            }else{
                completelyConverged = performClusteringLoop(completelyConverged);
            }    
        }

        System.out.println("");
        outputClusters();
        System.out.println("");

    }
    //******************************************************************************************************************
    //Method:		performClusteringLoop  
    //Description:	Performs 50 iterations of the k-means clustering algorithm. As long as the last cluster 
    //                  assignment and the current cluster assignment fore ach point are not the same, or the  
    //                  iterationCount is below 50, points are assigned to clusters and means are recalculated.
    //Parameters:	completelyConverged - if the data have completely converged into stable clusters
    //Returns:		completelyConverged - if the data have completely converged into stable clusters
    //Throws:		nothing
    //Calls:		assignPointsToCluster, getMeans
    public static boolean performClusteringLoop(boolean completelyConverged){
        //as long as the last 
         while (!completelyConverged && iterationCount < 50) {
            iterationCount++; //increment iteration

            assignPointsToCluster();    //the important work is in these two method calls
            getMeans();

            int numberOfConvergedPoints = 0;
            for (int i = 0; i < clusterAssignment.length; i++) { //for every record in clusterAssignment
                if (clusterAssignment[i][1] == clusterAssignment[i][2]) { //if the current cluster equals last cluster
                    numberOfConvergedPoints++;
                }
            }
            if (numberOfConvergedPoints == clusterAssignment.length) {
                completelyConverged = true;
            }
        }
         return completelyConverged;
    }
    //******************************************************************************************************************
    //Method:		parseData  
    //Description:	Parses the data from the input text file and organizes it into the dataPoints array so that
    //                  it can be used by the k-means clustering algorithm.  Accounts for empty lines in the text file.
    //Parameters:	text        -   the String array representing a line of the input text file
    //                  lineCount   -   the lineCount from TextFileClass
    //Returns:		nothing
    //Throws:		nothing
    //Calls:		nothing
    public static void parseData(String[] text, int lineCount) {
        int numberOfCategories;
        int numberOfLines = 0;
        StringTokenizer prelimTokenizer = new StringTokenizer(text[0], ", ");//just to get the numberOfCategories
        numberOfCategories = prelimTokenizer.countTokens();

        for (int i = 0; i < lineCount; i++) {//set numberOfLines, ignore empty strings
            if (!text[i].isEmpty()) {
                numberOfLines++;
            }
        }

        dataPoints = new int[numberOfLines][numberOfCategories];

        for (int i = 0; i < numberOfLines; i++) {
            StringTokenizer tokenizer = new StringTokenizer(text[i], ", ");
            for (int j = 0; j < numberOfCategories; j++) {
                dataPoints[i][j] = Integer.parseInt(tokenizer.nextToken());
            }
        }
    }
    //******************************************************************************************************************
    //Method:		printIntArray  
    //Description:	Prints the values held in a 2 dimensional int array. Formats values with commas and spaces.
    //Parameters:	array   -   the array to be printed
    //Returns:		nothing
    //Throws:		nothing
    //Calls:		nothing
    public static void printIntArray(int[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                System.out.print(array[i][j]);
                if (j != array[0].length - 1) {
                    System.out.print(", ");
                }

            }
            System.out.println("");
        }
    }
    //******************************************************************************************************************
    //Method:		printDoubleArray 
    //Description:	Prints the values held in a 2 dimensional double array. Formats values with commas and spaces.
    //Parameters:	none
    //Returns:		nothing
    //Throws:		nothing
    //Calls:		nothing
    public static void printDoubleArray(double[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                System.out.print(array[i][j]);
                if (j != array[0].length - 1) {
                    System.out.print(", ");
                }

            }
            System.out.println("");
        }
    }
    //******************************************************************************************************************
    //Method:		initializeClusterAssignment  
    //Description:	Initializes the clusterAssignment array. The first column of this array gets assigned the ID
    //                  of the records from the input data.
    //Parameters:	none
    //Returns:		nothing
    //Throws:		nothing
    //Calls:		nothing
    public static void initializeClusterAssignment() {
        clusterAssignment = new int[dataPoints.length][3];
        if (normalizedValues) {
            for (int i = 0; i < normalizedDataPoints.length; i++) { //Set the ID
                clusterAssignment[i][0] = (int) normalizedDataPoints[i][0];
            }
        } else{
            for (int i = 0; i < dataPoints.length; i++) { //Set the ID
                clusterAssignment[i][0] = dataPoints[i][0];
            }
        }
    }
    //******************************************************************************************************************
    //Method:		getMeans  
    //Description:	Gets the means for each cluster and puts it in the means array. Uses a List to keep track of 
    //                  all the IDs of records in a given cluster. Nearly repetitive code is present to account for if 
    //                  the user opted to use normalized data.
    //Parameters:	none
    //Returns:		nothing
    //Throws:		nothing
    //Calls:		roundValue
    public static void getMeans() {//this method basically has repeat code to account for if the data was normalized

        for (int i = 0; i < numberOfClusters; i++) { //for each cluster
            List<Integer> pointsInCluster = new ArrayList<Integer>(); //the IDs of all points in a single cluster

            for (int j = 0; j < clusterAssignment.length; j++) { //iterate through the array
                if (clusterAssignment[j][1] == i + 1) {
                    pointsInCluster.add(clusterAssignment[j][0]); //grab the ID of points in that cluster
                }
            }

            if (normalizedValues) {
                for (int j = 1; j < dataPoints[0].length; j++) { //for each category
                    double[] meanData = new double[pointsInCluster.size()];
                    for (int k = 0; k < pointsInCluster.size(); k++) {  //for each ID in list
                        for (int l = 0; l < normalizedDataPoints.length; l++) {   //for each record
                            if (pointsInCluster.get(k) == normalizedDataPoints[l][0]) {
                                meanData[k] = normalizedDataPoints[l][j];
                            }
                        }
                    }
                    double sum = 0;
                    for (int k = 0; k < meanData.length; k++) {//add the elements in the array
                        sum = sum + meanData[k];
                    }
                    means[i][j - 1] = roundValue(sum / pointsInCluster.size()); //divide to get mean
                }
            } else {
                for (int j = 1; j < dataPoints[0].length; j++) { //for each category
                    int[] meanData = new int[pointsInCluster.size()];
                    for (int k = 0; k < pointsInCluster.size(); k++) {  //for each ID in list
                        for (int l = 0; l < dataPoints.length; l++) {   //for each record
                            if (pointsInCluster.get(k) == dataPoints[l][0]) {
                                meanData[k] = dataPoints[l][j];
                            }
                        }
                    }
                    double sum = 0;
                    for (int k = 0; k < meanData.length; k++) {//add the elements in the array
                        sum = sum + meanData[k];
                    }
                    means[i][j - 1] = roundValue(sum / pointsInCluster.size());
                }                
            }
        }
    }
     //******************************************************************************************************************
    //Method:		assignPointsToCluster  
    //Description:	Assigns points to the appropriate clusters based on the shortest distance to a mean. Keeps track 
    //                  of these assignments with the clusterAssignment array.  This array has 3 columns; the first is 
    //                  the ID of the record, the second is the current cluster assignment, and the third is the previous
    //                  cluster assignment.
    //Parameters:	none
    //Returns:		nothing
    //Throws:		nothing
    //Calls:		nothing
    public static void assignPointsToCluster() { //assign based on which cluster is closest

        for (int i = 0; i < clusterAssignment.length; i++) { 
            //make'previous' cluster assignment the 'current' cluster assignment 
            clusterAssignment[i][2] = clusterAssignment[i][1];
        }

        for (int i = 0; i < dataPoints.length; i++) { //for every row in normalizedDataPoints (or dataPoints)
            //find closest cluster
            //assign it to that cluster
            int closestCluster = 0;
            double shortestDistance = Double.MAX_VALUE;
            for (int j = 0; j < means.length; j++) {
                double distance = getDistance(i, j);
                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    closestCluster = j + 1;
                }
            }
            clusterAssignment[i][1] = closestCluster;
        }
    }
    //******************************************************************************************************************
    //Method:		getDistance  
    //Description:	Computes the distance between a given data record and a given mean. The real distance is not 
    //                  computed, just the relative distance. The square root would need to be taken of the summation to
    //                  give the real distance.
    //Parameters:	dataIndex   -   the index of the data row for which the distance is to be computed
    //                  meanIndex   -   the index of the mean row fow which the distance is to be computed
    //Returns:		distance    -   the relative value of the distance between the record and the mean
    //Throws:		nothing
    //Calls:		nothing
    public static double getDistance(int dataIndex, int meanIndex){
        double distance;
        
        if(normalizedValues){ //compute distance between normalizedDataPoints[dataIndex] and means[meanIndex]
            double sum = 0;
            for (int i = 0; i < means[0].length; i++) {
                sum = sum + Math.pow((normalizedDataPoints[dataIndex][i+1] - means[meanIndex][i]), 2);
            }
            distance = sum;
        }else{//compute distance between dataPoints[dataIndex] and means[meanIndex]
            double sum = 0;
            for (int i = 0; i < means[0].length; i++) {
                sum = sum + Math.pow((dataPoints[dataIndex][i+1] - means[meanIndex][i]), 2);
            }
            distance = sum;
        }
        return distance;
    }
    //******************************************************************************************************************
    //Method:		getRandomMeans  
    //Description:	Gets a set of random means and puts it in the means array. Means are randomly generated for each
    //                  category in a range between the minimum and maximum values of the category.
    //Parameters:	none
    //Returns:		nothing
    //Throws:		nothing
    //Calls:		getColumnMaxAndMin    
    public static void getRandomMeans() {
        iterationCount++;
        means = new double[numberOfClusters][dataPoints[0].length - 1];

        for (int i = 0; i < means.length; i++) { //for each row in means ('k' times)
            for (int j = 1; j < dataPoints[0].length; j++) {//call Math.random numberOfCategories times
                getColumnMaxAndMin(normalizedValues, j);
                double randomValue = columnMinValue + (Math.random() * (columnMaxValue - columnMinValue));
                means[i][j - 1] = roundValue(randomValue);
            }
        }
    }
    //******************************************************************************************************************
    //Method:		getColumnMaxAndMin  
    //Description:	Gets the global variable values for columnMaxValue and columnMinValue by iterating through a 
    //                  column
    //Parameters:	normalized  -   whether or not the data has been normalized already
    //                  i           -   the index of the column for which the values are to be found
    //Returns:		nothing
    //Throws:		nothing
    //Calls:		nothing
    public static void getColumnMaxAndMin(boolean normalized, int i) {

        double[] columnData = new double[dataPoints.length];

        for (int j = 0; j < dataPoints.length; j++) { //pull data by row to populate columnData
            if (normalized) {
                columnData[j] = normalizedDataPoints[j][i];
            } else {
                columnData[j] = dataPoints[j][i];
            }
        }

        columnMaxValue = Integer.MIN_VALUE; //dummy values before they are actually set
        columnMinValue = Integer.MAX_VALUE;

        for (int j = 0; j < columnData.length; j++) { //iterate through column to get max and min
            if (columnData[j] > columnMaxValue) {
                columnMaxValue = columnData[j];
            }
            if (columnData[j] < columnMinValue) {
                columnMinValue = columnData[j];
            }
        }
    }
    //******************************************************************************************************************
    //Method:		roundValue  
    //Description:	Rounds a given value to three decimal places.
    //Parameters:	value       -   the value to be rounded
    //Returns:		newValue    -   the rounded value
    //Throws:		nothing
    //Calls:		nothing
    public static double roundValue(double value) {
        double newValue = Math.round(value * 1000);
        newValue = newValue / 1000;
        return newValue;
    }
    //******************************************************************************************************************
    //Method:		normalizeData  
    //Description:	Takes the data from dataPoints and puts it in a new array, normalizedDataPoints, with normalized
    //                  values according to the formula: (current value-minimum value) / (maximum value-minimum value)
    //Parameters:	none
    //Returns:		nothing
    //Throws:		nothing
    //Calls:		getColumnMaxAndMin
    public static void normalizeData() {

        normalizedDataPoints = new double[dataPoints.length][dataPoints[0].length];

        for (int i = 0; i < dataPoints.length; i++) { //copy values for ID (first column)
            normalizedDataPoints[i][0] = dataPoints[i][0];
        }

        for (int i = 1; i < dataPoints[0].length; i++) { //for each column except first

            getColumnMaxAndMin(false, i);

            for (int j = 0; j < dataPoints.length; j++) { //actual normalization of values
                double currentValue;
                currentValue = dataPoints[j][i];
                double newValue = (currentValue - columnMinValue) / (columnMaxValue - columnMinValue); 

                normalizedDataPoints[j][i] = roundValue(newValue);
            }

        }

    }
    //******************************************************************************************************************
    //Method:		outputClusters  
    //Description:	Outputs the clusters with their categories and the data records in those clusters. Provides
    //                  error messages if a cluster is empty. Uses printf method and only works with 8 dimensional
    //                  data.
    //Parameters:	none
    //Returns:		nothing
    //Throws:		nothing
    //Calls:		padString
    public static void outputClusters() {
        boolean emptyClusterExits = false;
        for (int i = 0; i < numberOfClusters; i++) { //for each cluster
            boolean containsRecord = false;
            System.out.println("\nCluster " + (i + 1));
            System.out.println(padString("ID", true, 4, " ")
                    + padString("Height", true, 4, " ")
                    + padString("Weight", true, 4, " ")
                    + padString("Sex", true, 4, " ")
                    + padString("College Education", true, 4, " ")
                    + padString("Athleticism", true, 4, " ")
                    + padString("RAD Rating", true, 4, " ")
                    + padString("Age", true, 4, " ")
                    + padString("Income", true, 4, " "));

            for (int j = 0; j < dataPoints.length; j++) { //for each record in dataPoints
                for (int k = 0; k < clusterAssignment.length; k++) { //for each record in clusterAssignment
                    if (normalizedValues) {
                        if (normalizedDataPoints[j][0] == clusterAssignment[k][0] && clusterAssignment[k][1] == i + 1) { 
                            //if the IDs and the clusters match
                            System.out.printf("%6d %9d %9d %6d %20d %14d %13d %6d %9d ", dataPoints[j][0],
                                    dataPoints[j][1], dataPoints[j][2], dataPoints[j][3], dataPoints[j][4],
                                    dataPoints[j][5], dataPoints[j][6], dataPoints[j][7], dataPoints[j][8]);
                            System.out.println("");
                            containsRecord = true;
                            break;
                        }
                    } else {
                        if (dataPoints[j][0] == clusterAssignment[k][0] && clusterAssignment[k][1] == i + 1) {
                            //if the IDs and the clusters match
                            System.out.printf("%6d %9d %9d %6d %20d %14d %13d %6d %9d ", dataPoints[j][0],
                                    dataPoints[j][1], dataPoints[j][2], dataPoints[j][3], dataPoints[j][4],
                                    dataPoints[j][5], dataPoints[j][6], dataPoints[j][7], dataPoints[j][8]);
                            System.out.println("");
                            containsRecord = true;
                            break;
                        }
                    }
                }
            }
            if(containsRecord == false){
                emptyClusterExits = true;
            }
        }
        if (emptyClusterExits){
            System.out.println("\nAfter the algorithm's completion, there is at least one empty cluster."
                    + "\nThis may be due to bad initial random means."
                    + "\nAttempt algorithm again with normalized data for better results.");
        }
    }

//**********************************************************************************************************************
//Method:���� padString
//Description:������ Pads a string with a specified number of leading or trailing characters. The character (or block of
//����������� characters) to be used for padding is user definable. Example: if stringToBePadded = "test" and
//����������� padLeft = true and numberOfSpacesToPad = 4 and paddingCharacters = " " (i.e., a single blank space),
//����������� the returned string would be "��� test" (i.e., the input string preceded by 4 blank spaces). However,
//����������� if paddingCharacters = "123" the returned string would be "123123123123test". Example use:
//������������variableAsString = Integer.toString(variable);
//����������� variableAsString = padString(variableAsString, true, totalSizeOfPrintField-variableAsString.length(), " ");
//������������� System.out.print(variableAsString + " ");
//Parameters: stringToBePadded���� - the string to be modified
//����������� padLeft��������������- true=place padding characters on left of the string to be padded; false=pad right
//����������� numberOfSpacesToPad� - the # of leading or trailing positions to be padded
//����������� paddingCharacters��� - the character(s) to be used for padding
//Returns:��� paddedString�������� - the modified string
//Calls:����� nothing
    public static String padString(String stringToBePadded, boolean padLeft, int numberOfSpacesToPad, 
            String paddingCharacters) {
        String paddedString = stringToBePadded;
        if (padLeft) {
            for (int i = 1; i <= numberOfSpacesToPad; i++) {
                paddedString = paddingCharacters + paddedString;
            }
        } else {
            for (int i = 1; i <= numberOfSpacesToPad; i++) {
                paddedString = paddedString + paddingCharacters;
            }
        }
        return paddedString;
    }
//**********************************************************************************************************************
}
//**********************************************************************************************************************
//**********************************************************************************************************************
