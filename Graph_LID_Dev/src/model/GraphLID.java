package model;

import model.Node;
import java.util.ArrayList;
import model.Language;
import java.util.HashMap;
import dbmanagement.DBManager;
import java.util.Random;
import java.sql.SQLException;

public class GraphLID extends AbstractGraph{

	private ArrayList<Node> graphNodes = new ArrayList<>();
	private ArrayList<Edge> graphEdges = new ArrayList<>();
	
	private HashMap<Language, ArrayList<String>> trainedNames = new HashMap<>();
	
	private static final String[] maoriNameStarts = {"A", "E", "H", "I", "K", "M", "N", "Ng", "O", "P", "R", "T", "U", "W", "Wh"};
	private static final String[] englishNameStarts = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
	private static final String[] samoanNameStarts = {"A", "E", "F", "H", "I", "K", "L", "M", "N", "O", "P", "R", "S", "T", "U", "V"};
	private static final String[] genericNameStarts = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
	
	public GraphLID() {
		
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
						
						nameGraph.increaseLangVal(lang, langVal);
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
						nameGraph.increaseLangVal(lang, langVal);
					}
					break;
				}
			}
			
		}
		
		predictLanguage = nameGraph.getMostLikelyLanguage();
		
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
		
		NamesIterationLoop:
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
	
	public void trainAllLanguages() throws Exception{
		
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
		
		for (Language lang : Language.values()) this.trainLanguage(lang);
	}
	
	public int[] getNumberOfNamesToUse(Language lang, ArrayList<String> langNames) throws Exception{
		
		DBManager dbm = new DBManager();
		dbm.makeConnection();
		
		int numNameStarters = this.getNumNameStarters(lang);
		
		double[] nameFractions = new double[numNameStarters];
		
		int trainingSize = dbm.getTrainingSize();
		
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
	
	public void trainLanguage(Language lang) throws Exception{
		
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
		ArrayList<String> langNames = dbm.getNames(lang);
		
		int[] phonemeNumber = this.getNumberOfNamesToUse(lang, langNames);
		int numNameStarters = this.getNumNameStarters(lang);
		

//		String[] nameStart = new String[numNameStarters];
//		for (int i = 0; i < numNameStarters; i++) {
//			if (lang == Language.ENGLISH) nameStart[i] = GraphLID.englishNameStarts[i];
//			else if (lang == Language.MAORI) nameStart[i] = GraphLID.maoriNameStarts[i];
//			else if (lang == Language.SAMOAN) nameStart[i] = GraphLID.samoanNameStarts[i];
//		}
		String[] nameStart = GraphLID.getNameStarters(lang);
		
		ArrayList<ArrayList<String>> namesToUse = new ArrayList<>();
		namesToUse = GraphLID.selectTrainingNames(langNames, phonemeNumber, nameStart, lang);
		
		for (int i = 0; i < namesToUse.size(); i++) {
			ArrayList<String> extractedNames = namesToUse.get(i);
			
			for (int j = 0; j < extractedNames.size(); j++) {
				
//				System.out.println("The name " + extractedNames.get(j) + " will be parsed for the language " + lang);
				
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
	
	public void testAllLanguages() throws Exception{
		
		for (Language lang : Language.values()) {
			this.testLanguage(lang);
		}
		
	}
	
	public void testLanguage(Language lang) throws Exception{
		
		DBManager dbm = new DBManager();
		dbm.makeConnection();
		ArrayList<String> langNames = dbm.getNames(lang);
		
		int[] phonemeNumber = this.getNumberOfNamesToUse(lang, langNames);
		int numNameStarters = this.getNumNameStarters(lang);
		
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
		System.out.println("For the language " + lang + " the LID accuracy is " 
				+ accuracyScore + "%");
	}
	
	public static void main(String[] args) {
		
		DBManager dbm = new DBManager();
		GraphLID testGraph = new GraphLID();
		testGraph.initiateTrainedNamesHasMap();
		try {
			dbm.makeConnection();
		
			testGraph.trainAllLanguages();
			
			testGraph.testAllLanguages();
			
			dbm.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
//		int nodeListSize = testGraph.getNodeListSize();
//		int edgeListSize = testGraph.getEdgeListSize();
//		System.out.println("Number of nodes is " + nodeListSize);
//		System.out.println("Number of edges is " + edgeListSize + "\n");
//		
//		Language testLanguage = testGraph.predictLanguage("Mata");
//		System.out.println("The most likely language for 'Mata' is " + testLanguage + "\n");
//		testLanguage = testGraph.predictLanguage("Fetuao");
//		System.out.println("The most likely language for 'Fetuao' is " + testLanguage + "\n");
//		
//		testLanguage = testGraph.predictLanguage("Maxwell");
//		System.out.println("The most likely language for 'Maxwell' is " + testLanguage + "\n");
//		testLanguage = testGraph.predictLanguage("Aileen");
//		System.out.println("The most likely language for 'Aileen' is " + testLanguage + "\n");
		
		
		
	}

}
