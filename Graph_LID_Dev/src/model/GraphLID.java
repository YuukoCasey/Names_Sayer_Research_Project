package model;

import model.Node;
import java.util.ArrayList;
import model.Language;
import java.util.HashMap;
import dbmanagement.DBManager;
import java.util.Random;
import java.util.Scanner;
import java.sql.SQLException;
import dbmanagement.TestResultManager;
import dbmanagement.GraphSaver;
import dbmanagement.GraphLoader;

public class GraphLID extends AbstractGraph{

//	private ArrayList<Node> graphNodes = new ArrayList<>();
//	private ArrayList<Edge> graphEdges = new ArrayList<>();
	
//	private boolean DEBUGGING_BOOL;
	
	private HashMap<Language, ArrayList<String>> trainedNames = new HashMap<>();
//	private HashMap<Language, ArrayList<String>> trainingNames = new HashMap<>();
	
//	private static final String[] maoriNameStarts = {"A", "E", "H", "I", "K", "M", "N", "Ng", "O", "P", "R", "T", "U", "W", "Wh"};
//	private static final String[] englishNameStarts = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
//	private static final String[] samoanNameStarts = {"A", "E", "F", "H", "I", "K", "L", "M", "N", "O", "P", "R", "S", "T", "U", "V"};
//	private static final String[] genericNameStarts = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

	private static final String[] maoriNameStarts = {"a", "e", "h", "i", "k", "m", "n", "ng", "o", "p", "r", "t", "u", "w", "wh"};
	private static final String[] englishNameStarts = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
	private static final String[] samoanNameStarts = {"a", "e", "f", "g", "h", "i", "k", "l", "m", "n", "o", "p", "r", "s", "t", "u", "v"};
	private static final String[] genericNameStarts = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
	
	public GraphLID() {
//		this.DEBUGGING_BOOL = false;
	}
	
	public GraphLID(ArrayList<Node> nodeList, ArrayList<Edge> edgeList) {
//		this.nodeList = nodeList;
//		this.edgeList = edgeList;
		for (Node n : nodeList)
			this.nodeList.add(n);
		for (Edge e : edgeList) 
			this.edgeList.add(e);
		
	}
	
	public void setNodeList(ArrayList<Node> nodeList) {
		for (Node n : nodeList)
			this.nodeList.add(n);
	}
	
	public void setEdgeList(ArrayList<Edge> edgeList) {
		for (Edge e : edgeList) 
			this.edgeList.add(e);
	}
	
	public void initiateTrainedNamesHasMap() {
		for (Language lang : Language.values()) {
			this.trainedNames.put(lang, new ArrayList<String>());
		}
	}
	
	public void addNameToTrainedData(Language lang, String name) {
		ArrayList<String> curNames = this.getTrainedNamesForLanguage(lang);
		
		//Double-check that the name you will enter is not present
		if (!curNames.contains(name)) {
			curNames.add(name);
		}
		this.trainedNames.put(lang, curNames);
	}
	
	public ArrayList<String> getTrainedNamesForLanguage(Language lang){
		return this.trainedNames.get(lang);
	}
	
	public HashMap<Language, ArrayList<String>> getAllTrainedNames(){
		return this.trainedNames;
	}
	
	public Language predictLanguage(String name) {
		//This function should be able to iterate through an input string to see if there is
		//any relevant node for each trigram. If there is, that node's weight for each language
		//is added to the PointAccumulator. 
		
		Language predictLanguage = Language.ENGLISH;
		
		PathPointsAccumulator nameGraph = new PathPointsAccumulator(name);
		
		int sumV = this.getSumNodeLangVals();
		int sumE = this.getSumEdgeLangVals();
		
		//For every Node that is in the intersection of sets this.graphNodes and nameGraph.nodeList
		//add the language points of this.graphNodes.getNode(index) to the language values of
		//nameGraph
		
		int nameGraphNodeNum = nameGraph.getNodeListSize();
		
		for (int i = 0; i < nameGraphNodeNum; i++) {
			
			Node nodeI = nameGraph.getNode(i);
			
			for (int j = 0; j < this.getNodeListSize(); j++) {
				Node nodeJ = this.getNode(j);
				if (nodeI.hasSameName(nodeJ)) {
					
//					System.out.println("\nThe current trigram has been found in the graph");
//					System.out.println("The current trigram is " + nodeI.getTrigram());
					
					for (Language lang : Language.values()) {
						int langVal = nodeJ.getLangVal(lang);
//						if (langVal >= 1) System.out.println("This trigram has a " + lang + " value of " + langVal);
						
//						int sumV = this.getSumNodeLangVals(lang);
						
						nameGraph.increaseLangVal(lang, langVal, sumV);
					}
					break;
				}
			}
			
		}
		
		//Same with every Edge that is in the intersection of sets this.graphEdges and
		//nameGraph.edgeList
		
		int nameGraphEdgeNum = nameGraph.getEdgeListSize();
		for (int i = 0; i < nameGraphEdgeNum; i++) {
			Edge edgeI = nameGraph.getEdge(i);
			for (int j = 0; j < this.getEdgeListSize(); j++) {
				Edge edgeJ = this.getEdge(j);
				if (edgeI.hasSameNames(edgeJ)) {
					for (Language lang : Language.values()) {
						int langVal = edgeJ.getLangVal(lang);
						
//						int sumE = this.getSumEdgeLangVals(lang);
						nameGraph.increaseLangVal(lang, langVal, sumE);
					}
					break;
				}
			}
			
		}
		
		predictLanguage = nameGraph.getMostLikelyLanguage();
		
		nameGraph.derefLangValues();
		nameGraph.closeNodeAndEdgeHashMaps();
		nameGraph = null;
		
		return predictLanguage;
			
		
	}
	
	public static Language getLanguageLeastNames() throws SQLException, ClassNotFoundException{
		
		/****************************************************************
		 *                                                              *
		 * This function serves to find what language, among those that *
		 * have been trained, has the lowest number of names in the     *
		 * database.                                                    *
		 *                                                              *
		 * Inputs: none                                                 *
		 *                                                              *
		 * Output: Language that has the least names in the database    *
		 *                                                              *
		 ****************************************************************/
		
		Language returnLanguage = Language.ENGLISH;
		
		DBManager dbm = new DBManager();
		dbm.makeConnection();

		int lowestNum = dbm.getNumNamesInLanguage(returnLanguage);
		
		for (Language lang : Language.values()) {
			if (dbm.getNumNamesInLanguage(lang) < lowestNum) {
				lowestNum = dbm.getNumNamesInLanguage(lang);
				returnLanguage = lang;
			}
		}
		return returnLanguage;
	}
	
	public void parseName(String name, Language lang) {
		//This function will take a name and a language as inputs, 
		//and add them to the graph
		
		if (name.length() == 0) return;
		
		int numOfTrigrams = 0;
		if (name.length() <= 3) { 
			numOfTrigrams = 1; //Trigram is whole name
			while(name.length() < 3) name = name + " ";
		}
		else numOfTrigrams = name.length() - 2;
		
		String trigram = "";
		
		if (numOfTrigrams == 1) {
			trigram = name;
			this.addNode(lang, trigram);
		}
		
		else {
			
			String lastTrigram = name.substring(0, 3); //Set lastTrigram to the first trigram
			for (int i = 0; i < numOfTrigrams; i++) {
				trigram = name.substring(i, i+3);
				this.addNode(lang, trigram);
				if (i >= 1) {

					this.addEdge(lang, lastTrigram, trigram);
					lastTrigram = trigram;
				}
				
			}
			
		}
		
		
		
	}
	
	public static ArrayList<String> makeRandomNameList(ArrayList<String> origList, String nameStart, int numNamesExtract, Language lang) throws Exception {
		
		/*******************************************************************************
		 *                                                                             *
		 * This function serves to make an ArrayList of names from the database, where *
		 * a certain number of names are extracted and those names all start with a    *
		 * certain string.                                                             *
		 *                                                                             *
		 * Inputs:                                                                     *
		 * 	- An ArrayList from which names will be selected                           *
		 * 	- A string that the start of names to use will start with                  *
		 * 	- The number of names you want in the return ArrayList                     *
		 * 	- The Language of the names you will return                                *
		 *                                                                             *
		 * Output:                                                                     *
		 * 	A random ArrayList of names that start with a certain string for a certain *
		 * 	language                                                                   *
		 *                                                                             *
		 *******************************************************************************/

		ArrayList<String> returnList = new ArrayList<>();
		DBManager dbm = new DBManager();
		dbm.makeConnection();
		
		ArrayList<String> nameStartList = dbm.getNamesStartString(nameStart, lang);
		
		for (int i = 0; i < numNamesExtract; i++) {
			
			//TODO: explain what this [articular line of code does
			int randIndex = new Random().nextInt(nameStartList.size());
			
			String extractedName = nameStartList.get(randIndex);
			boolean nameIsAlreadyUsed = GraphLID.stringInArrayList(returnList, extractedName);

			if (!nameIsAlreadyUsed) {
				returnList.add(extractedName);
			} else i--;
			
		}
		
		dbm.closeConnection();
		return returnList;
		
	}
	
	public static ArrayList<String> makeRandomNameList(ArrayList<String> origList, String nameStart, int numNamesExtract, Language lang, ArrayList<String> usedNames) throws Exception{
		
		ArrayList<String> returnList = new ArrayList<>();
		DBManager dbm = new DBManager();
		dbm.makeConnection();
		
		ArrayList<String> nameStartList = dbm.getNamesStartString(nameStart, lang);
		
		for (int i = 0; i < numNamesExtract; i++) {
			
			//TODO: explain what this [articular line of code does
			int randIndex = new Random().nextInt(nameStartList.size());
			
			String extractedName = nameStartList.get(randIndex);
			boolean nameIsAlreadyUsed = GraphLID.stringInArrayList(returnList, extractedName);

			if (!nameIsAlreadyUsed) {
				nameIsAlreadyUsed = usedNames.contains(extractedName);
				if (!nameIsAlreadyUsed) {
					returnList.add(extractedName);
				}
			} else i--;
			
		}
		
		dbm.closeConnection();
		return returnList;
		
	}
	
	public static boolean stringInArrayList(ArrayList<String> inputList, String inputStr) {
		
		/*******************************************************************************
		 *                                                                             *
		 * This function serves to see if a string is present in an ArrayList of       *
		 * strings                                                                     *
		 *                                                                             *
		 * Inputs:                                                                     *
		 * 	- The ArrayList that will be analysed                                      *
		 * 	- The String to which ArrayList Strings will be compared                   *
		 *                                                                             *
		 * Output:                                                                     *
		 * 	A boolean saying whether or not the ArrayList contains the searched String *
		 *                                                                             *
		 *******************************************************************************/
		
		for (int i = 0; i < inputList.size(); i++) {
			if (inputList.get(i).equals(inputStr)) return true;
		}
		
		return false;
	}
	
	public boolean nameStartMatches(String curName, String curPhoneme) {
		
		/********************************************************************
		 *                                                                  *
		 * This function serves to see whether or not a string starts with  *
		 * a given sequence of letters                                      *
		 *                                                                  *
		 * Inputs:                                                          *
		 * 	- The name (or string in general) that will be analysed         *
		 * 	- The sequence of letters that will be searched for in the name *
		 *                                                                  *
		 * Output:                                                          *
		 * 	A boolean of the answer                                         *
		 *                                                                  *
		 ********************************************************************/
		
		if (curName.length() < curPhoneme.length()) return false;
		for (int i = 0; i < curPhoneme.length(); i++) {

			char nChar = curName.charAt(i);
			char pChar = curPhoneme.charAt(i);
			if (nChar != pChar) return false;
			
		}
		return true;
	}
	
	public double[] setNameFractions(Language lang, int numNameStarters, ArrayList<String> langNames, int trainingSize) {
		
		/******************************************************************************
		 *                                                                            *
		 * This function serves to find what percentage of names start with each      *
		 * phoneme for a given language.                                              *
		 *                                                                            *
		 * Inputs:                                                                    *
		 * 	- The language being used                                                 *
		 * 	- The number of unique phonemes used to start names in the given language *
		 * 	- An ArrayList of the names in the given language                         *
		 * 	- The number of names that will be used for training each individual      *
		 * 	  language                                                                *
		 *                                                                            *
		 * Outputs:                                                                   *
		 * 	- An array of doubles, representing what percentage of names to be used   *
		 * 	  will start with which phoneme                                           *
		 *                                                                            *
		 ******************************************************************************/
		
		double[] nameFractions = new double[numNameStarters];
		for (int i = 0; i < nameFractions.length; i++) nameFractions[i] = 0;
		
		int numNamesInLang = langNames.size();
		
//		NamesIterationLoop:
		//Worst-case scenario: O(n) = numLanesInLang * numNameStarters = m * n
		for (int i = 0; i < numNamesInLang; i++) {
			
			String curName = langNames.get(i);
			
			NameStartIterationLoop:
			for (int j = 0; j < numNameStarters; j++) {
				
				String curPhoneme = "";
				if (lang == Language.ENGLISH) curPhoneme = GraphLID.englishNameStarts[j];
				else if (lang == Language.MAORI) curPhoneme = GraphLID.maoriNameStarts[j];
				else if (lang == Language.SAMOAN) curPhoneme = GraphLID.samoanNameStarts[j];
				else curPhoneme = GraphLID.genericNameStarts[j];
				
				boolean nameStartMatches = this.nameStartMatches(curName, curPhoneme);
				if (nameStartMatches) {
					nameFractions[j] += 1.0;
					break NameStartIterationLoop;
				}
				
			}
			
		}
		
		for (int i = 0; i < nameFractions.length; i++) 
			nameFractions[i] /= (double) langNames.size();
		
		return nameFractions;
		
	}
	
	public static ArrayList<ArrayList<String>> selectTrainingNames(ArrayList<String> langNames, int[] phonemeNumber, String[] langNameStarts, Language lang) throws Exception{

		/*******************************************************************************
		 *                                                                             *
		 * This function serves to produce an ArrayList of ArrayLists,                 *
		 * which each individually contain all the names that start with a certain     *
		 * phoneme that will be used for training the Language Identification AI       *
		 *                                                                             *
		 * Inputs:                                                                     *
		 * 	- An ArrayList of all the names from a language in a database              *
		 * 	- An Array of the number of names that will need to start with each        *
		 * 	  particular phoneme                                                       *
		 * 	- An Array of all the phonemes with which names can start with for a given *
		 * 	  language                                                                 *
		 * 	- The Language for which the AI is being trained to recognise              *
		 *                                                                             *
		 * Output:                                                                     *
		 * 	An ArrayList of ArrayLists, overall containing all the names that will be  *
		 * 	used to train the AI                                                       *
		 *                                                                             *
		 *******************************************************************************/
		
		ArrayList<ArrayList<String>> namesToUse = new ArrayList<>();
		
		int numPhonemeStarts = phonemeNumber.length;
		
		for (int i = 0; i < numPhonemeStarts; i++) {
			ArrayList<String> namesFromPhoneme = GraphLID.makeRandomNameList(langNames, langNameStarts[i], phonemeNumber[i], lang);
			namesToUse.add(namesFromPhoneme);
		}
		
		return namesToUse;
		
	}
	
	public ArrayList<ArrayList<String>> selectTestingNames(ArrayList<String> langNames, int[] phonemeNumber, String[] langNameStarts, Language lang) throws Exception{
		
		ArrayList<String> usedNames = this.getTrainedNamesForLanguage(lang);
		
		ArrayList<ArrayList<String>> namesToUse = new ArrayList<>();
		
		int numPhonemeStarts = phonemeNumber.length;
		
		for (int i = 0; i < numPhonemeStarts; i++) {
			ArrayList<String> namesFromPhoneme = GraphLID.makeRandomNameList(langNames, langNameStarts[i], phonemeNumber[i], lang, usedNames);
			namesToUse.add(namesFromPhoneme);
		}
		
		return namesToUse;
	}
	
	public void trainAllLanguages(double trainingPercent) throws Exception{
		
		/******************************************************************
		 *                                                                *
		 * This function serves to train the AI to identify the language  *
		 * of all languages for which there is data on words and/or names *
		 * in a database                                                  *
		 *                                                                *
		 * Inputs: None                                                   *
		 *                                                                *
		 * Output: None                                                   *
		 *                                                                *
		 ******************************************************************/
		
		for (Language lang : Language.values()) this.trainLanguage(lang, trainingPercent);
	}
	
	public int[] getNumberOfNamesToUse(Language lang, ArrayList<String> langNames, double trainingPercent) throws Exception{
		
		DBManager dbm = new DBManager();
		dbm.makeConnection();
		
		int numNameStarters = this.getNumNameStarters(lang);
		
		double[] nameFractions = new double[numNameStarters];
		
		int trainingSize = dbm.getTrainingSize(trainingPercent, lang); //O(n) = 1
		
		nameFractions = this.setNameFractions(lang, numNameStarters, langNames, trainingSize);
		
		int[] phonemeNumber = new int[numNameStarters];
		for (int i = 0; i < nameFractions.length; i++) {
			double temp_double = nameFractions[i] * trainingSize;
			phonemeNumber[i] = (int)Math.round(temp_double);
			
		}
		dbm.closeConnection();
		return phonemeNumber;
	}
	
	public int getNumNameStarters(Language lang) {
		
		int numNameStarters = GraphLID.genericNameStarts.length;
		if (lang == Language.ENGLISH) numNameStarters = GraphLID.englishNameStarts.length;
		else if (lang == Language.MAORI) numNameStarters = GraphLID.maoriNameStarts.length;
		else if (lang == Language.SAMOAN) numNameStarters = GraphLID.samoanNameStarts.length;

		return numNameStarters;
	}
	
	public static String[] getNameStarters(Language lang) {
		
		if (lang.equals(Language.MAORI)) {
			return GraphLID.maoriNameStarts;
		} else if (lang.equals(Language.SAMOAN)) {
			return GraphLID.samoanNameStarts;
		}
		return GraphLID.englishNameStarts;
		
	}
	
	public void trainLanguage(Language lang, double trainingPercentage) throws Exception{
		
		/*********************************************************
		 *                                                       *
		 * This function serves to train the AI to be able to    *
		 * recognise if a name is from this certain language     *
		 *                                                       *
		 * Input:                                                *
		 * 	- The language you wish to train the AI to recognise *
		 *                                                       *
		 * Output: None                                          *
		 *                                                       *
		 *********************************************************/
		
		DBManager dbm = new DBManager();
		dbm.makeConnection();
		ArrayList<String> langNames = dbm.getNames(lang); //O(n) = n
		
		
		int[] phonemeNumber = this.getNumberOfNamesToUse(lang, langNames, trainingPercentage);

		
		String[] nameStart = GraphLID.getNameStarters(lang);
		
		
		ArrayList<ArrayList<String>> namesToUse = new ArrayList<>();
		
		namesToUse = GraphLID.selectTrainingNames(langNames, phonemeNumber, nameStart, lang);
		
		for (int i = 0; i < namesToUse.size(); i++) { //O(n) = 
			ArrayList<String> extractedNames = namesToUse.get(i);
			
			for (int j = 0; j < extractedNames.size(); j++) {
				
				String currentName = extractedNames.get(j);
				this.addNameToTrainedData(lang, currentName);
				
				this.parseName(currentName, lang);
			}
		}
		
	}
	
	public static int returnLongestPhonemeIndex(Language lang) {
		
		/**********************************************************
		 *                                                        *
		 * This function serves to find the longest name starting *
		 * phoneme for a language and return it's index           *
		 *                                                        *
		 *********************************************************/
		
		int returnValue = 0;
		
		if (lang == Language.ENGLISH)
			returnValue = GraphLID.returnLongestPhonemeIndex(englishNameStarts);
		else if (lang == Language.MAORI)
			returnValue = GraphLID.returnLongestPhonemeIndex(maoriNameStarts);
		else if (lang == Language.SAMOAN)
			returnValue = GraphLID.returnLongestPhonemeIndex(samoanNameStarts);
		
		return returnValue;
		
	}
	
	public static int returnLongestPhonemeIndex(String[] str_array) {
		
		/****************************************************************
		 *                                                              *
		 * This function serves to find the longest (in terms of        *
		 * ASCII characters) phoneme used to start names in a given     *
		 * language. This function effectively finds the longest String *
		 * int an Array of Strings                                      *
		 *                                                              *
		 * Input:                                                       *
		 * 	An Array of strings used to start names                     *
		 *                                                              *
		 * Output:                                                      *
		 * 	An Integer representing the index of the longest string in  *
		 * 	the input Array                                             *
		 *                                                              *
		 ****************************************************************/
		
		int returnValue = 0;
		
		for (int i = 1; i < str_array.length; i++) {
			if (str_array[i].length() > str_array[returnValue].length())
				returnValue = i;
		}
		
		return returnValue;
		
	}
	
	public static int getLongestPhonemeLength(Language lang) {
		
		/*************************************************************
		 *                                                           *
		 * This function serves to return the length of the longest  *
		 * phoneme used to start names in a language.                *
		 *                                                           *
		 * Inputs:                                                   *
		 * 	The language for which you wish to find the longest      *
		 *  name-starting phoneme.                                   *
		 *                                                           *
		 * Output:                                                   *
		 *  An Integer that is the length of the longest string used *
		 *  to represent phonemes used at the start of names         *
		 *                                                           *
		 *************************************************************/
		
		int returnValue = 0;
		
		int returnIndex = GraphLID.returnLongestPhonemeIndex(lang);
		if (lang == Language.ENGLISH)
			returnValue = GraphLID.englishNameStarts[returnIndex].length();
		else if (lang == Language.MAORI)
			returnValue = GraphLID.maoriNameStarts[returnIndex].length();
		else if (lang == Language.SAMOAN)
			returnValue = GraphLID.samoanNameStarts[returnIndex].length();
		
		return returnValue;
		
	}
	
	public void testAllLanguages(int testNum, double testingPercent, double trainingPercent) throws Exception{
		
		for (Language lang : Language.values()) {
			this.testLanguage(lang, testNum, testingPercent, trainingPercent);
		}
		
	}
	
	public HashMap<Language, Double> retTestAllLanguages(int testNum, double testingPercent, double trainingPercent) throws Exception{
		
		HashMap<Language, Double> accuracies = new HashMap<>();
		
		for (Language lang : Language.values()) {
			accuracies.put(lang, this.retTestLanguage(lang, testNum, testingPercent, trainingPercent));
		}
		
		return accuracies;
	}
	
	public void testLanguage(Language lang, int testNum, double testingPercent, double trainingPercent) throws Exception{
		
		DBManager dbm = new DBManager();
		dbm.makeConnection();
		ArrayList<String> langNames = dbm.getNames(lang);
		
		int[] phonemeNumber = this.getNumberOfNamesToUse(lang, langNames, testingPercent);
		
		String[] nameStart = GraphLID.getNameStarters(lang);
		
		ArrayList<ArrayList<String>> namesToUse = this.selectTestingNames(langNames, phonemeNumber, nameStart, lang);
		
		ArrayList<String> testNames = new ArrayList<>();
		for (int i = 0; i < namesToUse.size(); i++) {
			for (int j = 0; j < namesToUse.get(i).size(); j++) {
				testNames.add(namesToUse.get(i).get(j));
			}
		}
		
		//Now for the actual testing of the LID method
		double accuracyScore = 0.0;
		for (int i = 0; i < testNames.size(); i++) {
			String curName = testNames.get(i);
			Language testLang = this.predictLanguage(curName);
			if (testLang.equals(lang)) {
				accuracyScore += 1.0;
			}
		}
		accuracyScore /= (double)testNames.size();
		accuracyScore *= 100.0;

		TestResultManager trm = new TestResultManager();
		trm.makeConnection();
		trm.insertEntryToDB(testNum, lang, accuracyScore, testingPercent, trainingPercent);
		
		trm.closeConnection();
		trm = null;
		
	}
	
	public double retTestLanguage(Language lang, int testNum, double testingPercent, double trainingPercent) throws Exception{
		
		DBManager dbm = new DBManager();
		dbm.makeConnection();
		ArrayList<String> langNames = dbm.getNames(lang);
		
		int[] phonemeNumber = this.getNumberOfNamesToUse(lang, langNames, testingPercent);
		
		String[] nameStart = GraphLID.getNameStarters(lang);
		
		ArrayList<ArrayList<String>> namesToUse = this.selectTestingNames(langNames, phonemeNumber, nameStart, lang);
		
		ArrayList<String> testNames = new ArrayList<>();
		for (int i = 0; i < namesToUse.size(); i++) {
			for (int j = 0; j < namesToUse.get(i).size(); j++) {
				testNames.add(namesToUse.get(i).get(j));
			}
		}
		
		//Now for the actual testing of the LID method
		double accuracyScore = 0.0;
		for (int i = 0; i < testNames.size(); i++) {
			String curName = testNames.get(i);
			Language testLang = this.predictLanguage(curName);
			if (testLang.equals(lang)) {
				accuracyScore += 1.0;
			}
		}
		accuracyScore /= (double)testNames.size();
		accuracyScore *= 100.0;
		
		return accuracyScore;
		
	}
	
	public void derefAllVars() {
		
//		this.graphNodes = null;
//		this.graphEdges = null;
		
		this.closeNodeAndEdgeHashMaps();
		
		for (Language lang : Language.values()) {
			ArrayList<String> arr = this.trainedNames.remove(lang);
			for (int i = 0; i < arr.size(); i++) {
				arr.set(i, null);
			}
			arr.clear();
		}
		
	}
	
	public int getSumNodeLangVals() {
		
		/*******************************************************
		 *                                                     *
		 * This function serves the purpose of calculating the *
		 * sum of the language values stored in every node for *
		 * every language trained                              *
		 *                                                     *
		 *******************************************************/
		
		int retVal = 0;
		
		for (Node n : this.nodeList) {
			for (Language lang : Language.values()) {
				retVal += n.getLangVal(lang);
			}
		}
		
		return retVal;
		
	}
	
	public int getSumNodeLangVals(Language lang) {
		
		int retVal = 0;
		
		for (Node v : this.nodeList) {
			retVal += v.getLanguageValue(lang);
		}
		return retVal;
	}
	
	public int getSumEdgeLangVals() {
		
		/*************************************************
		 *                                               *
		 * Same as 'getSumNodeLangVals()' but with edges *
		 *                                               *
		 *************************************************/
		
		int retVal = 0;
		
		for (Edge e : this.edgeList) {
			for (Language lang : Language.values()) {
				retVal += e.getLanguageValue(lang);
			}
		}
		
		return retVal;
		
	}
	
	public int getSumEdgeLangVals(Language lang) {
		
		int retVal = 0;
		
		for (Edge e : this.edgeList) {
			retVal += e.getLanguageValue(lang);
		}
		return retVal;
	}
	
	public static double getAverage(ArrayList<Double> languageValues) {
		
		double sum = 0.0;
		for (Double d : languageValues) {
			sum += d;
		}
		sum /= languageValues.size();
		return sum;
		
	}
	
	public static void main(String[] args) {
		
		try {
			
//			double trainingPercent = 70.0;
//			double testingPercent = 100.0 - trainingPercent;

//			long startTime = System.nanoTime();
			GraphLID demoGraph = new GraphLID();
//			demoGraph.initiateTrainedNamesHasMap();
			System.out.println("Making graph");
//			System.out.println("About to train the graph!!!");
			
			GraphLoader gl = new GraphLoader();
			gl.makeConnection();
			demoGraph.setNodeList(gl.loadNodes());
			demoGraph.setEdgeList(gl.loadEdges());
			gl.closeConnection();
			gl = null;
			
			Scanner userIn = new Scanner(System.in);
			while(true) {
				System.out.println("Enter a name you wish to test, or enter q to exit:\n\t");
				String testName = userIn.nextLine();
				
				String lowerName = testName.toLowerCase();
				if (lowerName.equals("q")) 
					break;
				
				System.out.println(testName + " is a " + demoGraph.predictLanguage(lowerName) + " name");
				
				
			}
			userIn.close();
			System.out.println("Closing application...");
			
			/* START OF GRAPH TESTING AND SAVING SECTION */
//			GraphSaver gs = new GraphSaver();
//			gs.makeConnection();
//			
//			for (int i = 1; i <= 50; i++) {
//				
//				GraphLID testGraph = new GraphLID();
//				testGraph.initiateTrainedNamesHasMap();
//				testGraph.trainAllLanguages(trainingPercent);
//				System.out.println("Out of curiosity, num of Nodes is " + testGraph.getNodeListSize());
//				System.out.println("Out of curiosity, num of Edges is " + testGraph.getEdgeListSize());
//				HashMap<Language, Double> testRes = testGraph.retTestAllLanguages(i, testingPercent, trainingPercent);
//				
//				ArrayList<Double> results = new ArrayList<>();
//				for (Language lang : Language.values()) 
//					results.add(testRes.get(lang));
//				
//				double magnitude = GraphLID.getAverage(results);
//							
//				if (magnitude > gs.getOverallAccuracy()) {
//					System.out.println("About to update saved graph");
//					ArrayList<Double> accuracies = new ArrayList<>();
//					accuracies.add(magnitude);
//					for (double d : results) accuracies.add(d);
//					gs.saveGraph(testGraph, accuracies);
//					
//				}
//				
//				testGraph.derefAllVars();
//				
//				testGraph = null;
//				
//				System.out.println("Test " + i + " complete\n");	
//
//			}
//			System.out.println("FINISHED!!!");
//			gs.closeConnection();
			
			/* END OF GRAPH TESTING AND SAVING SECTION */
			
			
			/* START OF GRAPH LOADING SECTION */
			
//			GraphLoader gl = new GraphLoader();
//			gl.makeConnection();
//			long startTime = System.nanoTime();
//			ArrayList<Node> nodeList = gl.loadNodes();
//			ArrayList<Edge> edgeList = gl.loadEdges();
//			GraphLID loadedGraph = new GraphLID(nodeList, edgeList);
//			long nanoTimeTaken = System.nanoTime() - startTime;
//			nanoTimeTaken /= 1000000;
//			System.out.println("Loading the graph took " + nanoTimeTaken + " milliseconds");
//			System.out.println("This graph has " + nodeList.size() + " nodes and " + edgeList.size() + " edges");
//			
//			Scanner userIn = new Scanner(System.in);
//			while(true) {
//				System.out.println("Enter a name you wish to test, or enter q to exit:\n\t");
//				String testName = userIn.nextLine();
//				
//				String lowerName = testName.toLowerCase();
//				if (lowerName.equals("q")) 
//					break;
//				
//				System.out.println(testName + " is a " + loadedGraph.predictLanguage(lowerName) + " name");
//				
//				
//			}
//			userIn.close();
//			System.out.println("Closing application...");			
//			gl.closeConnection();
			
			/* END OF GRAPH LOADING SECTION */
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}