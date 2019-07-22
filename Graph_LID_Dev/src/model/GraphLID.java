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
	
	private static final String[] maoriNameStarts = {"A", "E", "H", "I", "K", "M", "N", "Ng", "O", "P", "R", "T", "U", "W", "Wh"};
	private static final String[] englishNameStarts = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
	private static final String[] samoanNameStarts = {"A", "E", "F", "H", "I", "K", "L", "M", "N", "O", "P", "R", "S", "T", "U", "V"};
	private static final String[] genericNameStarts = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
	
	public GraphLID() {

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
					
					System.out.println("\nThe current trigram has been found in the graph");
					System.out.println("The current trigram is " + nodeI.getTrigram());
					
					for (Language lang : Language.values()) {
						int langVal = nodeJ.getLangVal(lang);
						if (langVal >= 1) System.out.println("This trigram has a " + lang + " value of " + langVal);
						
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
		//This function will take a name and a language as inputs, and add them to the graph
		
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
		
		ArrayList<String> returnList = new ArrayList<>();
		DBManager dbm = new DBManager();
		dbm.makeConnection();
		
		int numNameStart = dbm.getNumNamesStartString(lang, nameStart);
		
		for (int i = 0; i < numNamesExtract; i++) {
			int randIndex = new Random().nextInt(numNameStart);
			
			String extractedString = origList.get(randIndex);
			if (!GraphLID.stringInArrayList(returnList, extractedString)) 
				returnList.add(extractedString);
			
		}
		
		dbm.closeConnection();
		return returnList;
		
	}
	
	public static boolean stringInArrayList(ArrayList<String> inputList, String inputStr) {
		
		for (int i = 0; i < inputList.size(); i++) {
			if (inputList.get(i).equals(inputStr)) return true;
		}
		
		return false;
	}
	
	public boolean nameStartMatches(String curName, String curPhoneme) {
		
		if (curName.length() < curPhoneme.length()) return false;
		for (int i = 0; i < curPhoneme.length(); i++) {

			char nChar = curName.charAt(i);
			char pChar = curPhoneme.charAt(i);
			if (nChar != pChar) return false;
			
		}
		return true;
	}
	
	public double[] setNameFractions(Language lang, int numNameStarters, ArrayList<String> langNames, int trainingSize) {
		
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
//		
//		int[] phonemeNumber = new int[numNameStarters];
//		
//		for (int i = 0; i < numNameStarters; i++) {
//			double temp_double = nameFractions[i] * (double)trainingSize;
//			phonemeNumber[i] = (int)Math.round(temp_double);
//		}
		
	}
	
	public static ArrayList<ArrayList<String>> selectTrainingNames(ArrayList<String> langNames, int[] phonemeNumber, String[] langNameStarts, Language lang) throws Exception{

		ArrayList<ArrayList<String>> namesToUse = new ArrayList<>();
		
		int numPhonemeStarts = phonemeNumber.length;
		
		for (int i = 0; i < numPhonemeStarts; i++) {
			ArrayList<String> namesFromPhoneme = GraphLID.makeRandomNameList(langNames, langNameStarts[i], phonemeNumber[i], lang);
			namesToUse.add(namesFromPhoneme);
		}
		
		return namesToUse;
		
	}
	
	public void trainAllLanguages() throws Exception{
		for (Language lang : Language.values()) this.trainLanguage(lang);
	}
	
	public void trainLanguage(Language lang) throws Exception{
		
		DBManager dbm = new DBManager();
		dbm.makeConnection();
		ArrayList<String> langNames = dbm.getNames(lang);
		boolean isSmallestSampleLang = GraphLID.getLanguageLeastNames() == lang;
		
		int langNamesInDB = dbm.getNumNamesInLanguage(lang);
		int numLangToTrain = dbm.getNumNamesInLanguage(GraphLID.getLanguageLeastNames()) / 2;
		
		int numNameStarters = GraphLID.genericNameStarts.length;
		if (lang == Language.ENGLISH) numNameStarters = GraphLID.englishNameStarts.length;
		else if (lang == Language.MAORI) numNameStarters = GraphLID.maoriNameStarts.length;
		else if (lang == Language.SAMOAN) numNameStarters = GraphLID.samoanNameStarts.length;
		
		double[] nameFractions = new double[numNameStarters];
		
		int trainingSize = dbm.getTrainingSize();
		
		nameFractions = this.setNameFractions(lang, numNameStarters, langNames, trainingSize);
		
		int[] phonemeNumber = new int[numNameStarters];
		for (int i = 0; i < nameFractions.length; i++) {
			double temp_double = nameFractions[i] * trainingSize;
			phonemeNumber[i] = (int)Math.round(temp_double);
		}
		
		ArrayList<ArrayList<String>> namesToUse = new ArrayList<>();
		String[] nameStart = new String[numNameStarters];
		for (int i = 0; i < numNameStarters; i++) {
			if (lang == Language.ENGLISH) nameStart[i] = GraphLID.englishNameStarts[i];
			else if (lang == Language.MAORI) nameStart[i] = GraphLID.maoriNameStarts[i];
			else if (lang == Language.SAMOAN) nameStart[i] = GraphLID.samoanNameStarts[i];
		}
		
		namesToUse = GraphLID.selectTrainingNames(langNames, phonemeNumber, nameStart, lang);
		
		for (int i = 0; i < namesToUse.size(); i++) {
			ArrayList<String> extractedNames = namesToUse.get(i);
			for (int j = 0; j < extractedNames.size(); j++) {
				
				System.out.println("The name " + extractedNames.get(j) + " will be parsed for the language " + lang);
				
				this.parseName(extractedNames.get(j), lang);
			}
		}
		
		//TODO: finish this function
		
	}
	
	public static void main(String[] args) {
	
		//Pair<String, Language> data_element;
		
		DBManager dbm = new DBManager();
		GraphLID testGraph = new GraphLID();
		try {
			dbm.makeConnection();
		
			testGraph.trainAllLanguages();
			
			for (Language lang : Language.values()) {
				
				
				
//				ArrayList<String> langNames = dbm.getNames(lang);
//				
//				if (lang == Language.MAORI) {
//					
//					int maoriLangNamesInDB = langNames.size();
//					int numMaoriNameStartPhonemes = GraphLID.maoriNameStarts.length;
//					double[] nameFractions = new double[numMaoriNameStartPhonemes];
//					
//					for (int i = 0; i < numMaoriNameStartPhonemes; i++) 
//						nameFractions[i] = 0;
//					
//					NamesIterateLoop:
//					for (int i = 0; i < maoriLangNamesInDB; i++) {
//						
//						String curName = langNames.get(i);
//						//Iterate through the different possible phonemes at start of 
//						//names. 
//						NameStartIterateLoop:
//						for (int j = 0; j < numMaoriNameStartPhonemes; j++) {
//							
//							String curPhoneme = GraphLID.maoriNameStarts[j];
//							
//							if (curPhoneme.length() == 1) {
//								if ( ( curName.charAt(0) == 'W' && curName.charAt(1) == 'h' ) || ( curName.charAt(0) == 'N' && curName.charAt(1) == 'g' ) ) continue NameStartIterateLoop; 
//								if (curPhoneme.charAt(0) == curName.charAt(0)) {
//									nameFractions[j] += 1.0;
//									break NameStartIterateLoop;
//								}
//							}
//							
//						}
//						
//					}
//					
//					for (int i = 0; i < numMaoriNameStartPhonemes; i++)
//						nameFractions[i] /= (double)maoriLangNamesInDB;
//					
//					//Now get number of Samoan names in DB
//					int samoanNameSize = dbm.getNumNamesInLanguage(Language.SAMOAN);
//					int[] phonemeNumber = new int[numMaoriNameStartPhonemes]; //This is the number of Maori names that will be used for each phoneme used to start a name
//					
//					for (int i = 0; i < numMaoriNameStartPhonemes; i++) 
//						phonemeNumber[i] = (int)Math.round(nameFractions[i] * (double)samoanNameSize);
//					
//					//Now make sets of random names that start with particular phonemes
//					
//					ArrayList<ArrayList<String>> namesToUse = new ArrayList<>();
//					for (int i = 0; i < numMaoriNameStartPhonemes; i++) {
//						namesToUse.add(GraphLID.makeRandomNameList(langNames, GraphLID.maoriNameStarts[i], phonemeNumber[i], lang));
//					}
//					
//					for(int i = 0; i < namesToUse.size(); i++) {
//						ArrayList<String> extractedList = namesToUse.get(i);
//						for (int j = 0; j < extractedList.size(); j++) testGraph.parseName(extractedList.get(j), lang);
//					}
//					
//				} else if (lang == Language.SAMOAN) {
//					
//					for (int i = 0; i < langNames.size(); i++)
//						testGraph.parseName(langNames.get(i), lang);
//					
//				}
				
			}
			
			dbm.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		for (int i = 0; i < testGraph.getNodeListSize(); i++) {
//			Node dispNode = testGraph.getNode(i);
//			System.out.println("Node '" + dispNode.getTrigram() + "' has the following language values: ");
//			for (Language lang : Language.values()) {
//				System.out.println("\t" + lang + ": " + dispNode.getLanguageValue(lang));
//			}
//			System.out.println("\n");
//		}
		
//		for (int i = 0; i < testGraph.getEdgeListSize(); i++) {
//			Edge dispEdge = testGraph.getEdge(i);
//			System.out.println("Edge from '" + dispEdge.getPastNodeTrigram() + 
//					"' to '" + dispEdge.getNextNodeTrigram() + " has the following "
//							+ "language values: ");
//			for (Language lang : Language.values()) {
//				System.out.println("\t" + lang + ": " + dispEdge.getLanguageValue(lang));
//			}
//			System.out.println("\n");
//		}
		
//		GraphLID testGraph = new GraphLID();
//		testGraph.parseName("Yuuko", Language.JAPANESE);
//		testGraph.parseName("Yumika", Language.JAPANESE);
//		testGraph.parseName("Yuu", Language.JAPANESE);		
//		testGraph.parseName("Takayama", Language.JAPANESE);
//		testGraph.parseName("Takahashi", Language.JAPANESE);
//		testGraph.parseName("Takahara", Language.JAPANESE);
//		testGraph.parseName("Uehara", Language.JAPANESE);
//		testGraph.parseName("Matsuyama", Language.JAPANESE);
//		testGraph.parseName("Tretiakova", Language.RUSSIAN);
//		testGraph.parseName("Tchaikovsky", Language.RUSSIAN);
//		testGraph.parseName("Piotr", Language.RUSSIAN);
//		testGraph.parseName("Gagarin", Language.RUSSIAN);
//		testGraph.parseName("Junko", Language.JAPANESE);
//		testGraph.parseName("Ayaka", Language.JAPANESE);
//		testGraph.parseName("Taiki", Language.JAPANESE);
//		testGraph.parseName("Itadani", Language.JAPANESE);
//		testGraph.parseName("Risako", Language.JAPANESE);
//		testGraph.parseName("Tatsuya", Language.JAPANESE);
//		testGraph.parseName("Aiko", Language.JAPANESE);
//		testGraph.parseName("Akane", Language.JAPANESE);
//		testGraph.parseName("Kana", Language.JAPANESE);
//		testGraph.parseName("Aoi", Language.JAPANESE);
//		testGraph.parseName("Asuka", Language.JAPANESE);
//		testGraph.parseName("Chiaki", Language.JAPANESE);
//		testGraph.parseName("Hanako", Language.JAPANESE);
//		testGraph.parseName("Haruko", Language.JAPANESE);
//		testGraph.parseName("Hikari", Language.JAPANESE);
//		testGraph.parseName("Hikaru", Language.JAPANESE);
//		testGraph.parseName("Hitomi", Language.JAPANESE);
//		testGraph.parseName("Katsumi", Language.JAPANESE);
//		testGraph.parseName("Kotone", Language.JAPANESE);
//		testGraph.parseName("Kyoko", Language.JAPANESE);
//		testGraph.parseName("Midori", Language.JAPANESE);
//		testGraph.parseName("Miyako", Language.JAPANESE);
//		testGraph.parseName("Momoko", Language.JAPANESE);
//		testGraph.parseName("Nanapeko", Language.JAPANESE);
//		testGraph.parseName("Nanako", Language.JAPANESE);
//		testGraph.parseName("Naomi", Language.JAPANESE);
//		testGraph.parseName("Natsuko", Language.JAPANESE);
//		testGraph.parseName("Haruna", Language.JAPANESE);
//		testGraph.parseName("Ono", Language.JAPANESE);
//		testGraph.parseName("Tomomi", Language.JAPANESE);
//		testGraph.parseName("Ogawa", Language.JAPANESE);
//		testGraph.parseName("Rina", Language.JAPANESE);
//		testGraph.parseName("Suzuki", Language.JAPANESE);
//		testGraph.parseName("Mami", Language.JAPANESE);
//		testGraph.parseName("Sasazaki", Language.JAPANESE);
//		testGraph.parseName("Suzuhana", Language.JAPANESE);
//		
//		testGraph.parseName("Abakumov", Language.RUSSIAN);
//		testGraph.parseName("Abdulov", Language.RUSSIAN);
//		testGraph.parseName("Abramov", Language.RUSSIAN);
//		testGraph.parseName("Abramovich", Language.RUSSIAN);
//		testGraph.parseName("Astakhov", Language.RUSSIAN);
//		testGraph.parseName("Aslanov", Language.RUSSIAN);
//		testGraph.parseName("Baskin", Language.RUSSIAN);
//		testGraph.parseName("Bebchuk", Language.RUSSIAN);
//		testGraph.parseName("Bebchuk", Language.RUSSIAN);
//		testGraph.parseName("Bezrukov", Language.RUSSIAN);
//		testGraph.parseName("Bezrodny", Language.RUSSIAN);
//		testGraph.parseName("Belyakov", Language.RUSSIAN);
//		testGraph.parseName("Bogomalov", Language.RUSSIAN);
//		testGraph.parseName("Bryzgalov", Language.RUSSIAN);
//		testGraph.parseName("Vikhrov", Language.RUSSIAN);
//		testGraph.parseName("Vinogradov", Language.RUSSIAN);
//		testGraph.parseName("Vedenin", Language.RUSSIAN);
//		testGraph.parseName("Vavilov", Language.RUSSIAN);
//		testGraph.parseName("Vorontsov", Language.RUSSIAN);
//		testGraph.parseName("Romanova", Language.RUSSIAN);
//		testGraph.parseName("Vasilevsky", Language.RUSSIAN);
//		testGraph.parseName("Voskoboynikov", Language.RUSSIAN);
//		testGraph.parseName("Voskresensky", Language.RUSSIAN);
//		testGraph.parseName("Goncharov", Language.RUSSIAN);
//		testGraph.parseName("Glazkov", Language.RUSSIAN);
//		testGraph.parseName("Golubtsov", Language.RUSSIAN);
//		testGraph.parseName("Glukhov", Language.RUSSIAN);
//		testGraph.parseName("Gorbachyov", Language.RUSSIAN);
//		testGraph.parseName("Grebenshchikov", Language.RUSSIAN);
//		testGraph.parseName("Gurkovsky", Language.RUSSIAN);
//		testGraph.parseName("Gruzinsky", Language.RUSSIAN);
//		
//		testGraph.parseName("Abby", Language.ENGLISH);
//		testGraph.parseName("Alicia", Language.ENGLISH);
//		testGraph.parseName("Alex", Language.ENGLISH);
//		testGraph.parseName("Ashley", Language.ENGLISH);
//		testGraph.parseName("Ariel", Language.ENGLISH);
//		testGraph.parseName("Bailey", Language.ENGLISH);
//		testGraph.parseName("Blair", Language.ENGLISH);
//		testGraph.parseName("Bobby", Language.ENGLISH);
//		testGraph.parseName("Brett", Language.ENGLISH);
//		testGraph.parseName("Bronte", Language.ENGLISH);
//		testGraph.parseName("Brooklyn", Language.ENGLISH);
//		testGraph.parseName("Bryn", Language.ENGLISH);
//		testGraph.parseName("Cadence", Language.ENGLISH);
//		testGraph.parseName("Cameron", Language.ENGLISH);
//		testGraph.parseName("Cecil", Language.ENGLISH);
//		testGraph.parseName("Colby", Language.ENGLISH);
//		testGraph.parseName("Courtney", Language.ENGLISH);
//		testGraph.parseName("Cole", Language.ENGLISH);
//		testGraph.parseName("Dakota", Language.ENGLISH);
//		testGraph.parseName("Dana", Language.ENGLISH);
//		testGraph.parseName("Darian", Language.ENGLISH);
//		testGraph.parseName("Darnell", Language.ENGLISH);
//		testGraph.parseName("Darryl", Language.ENGLISH);
//		testGraph.parseName("Devin", Language.ENGLISH);
//		testGraph.parseName("Dustin", Language.ENGLISH);
//		testGraph.parseName("Elis", Language.ENGLISH);
//		testGraph.parseName("Elliot", Language.ENGLISH);
//		testGraph.parseName("Emerson", Language.ENGLISH);
//		testGraph.parseName("Emery", Language.ENGLISH);
//		testGraph.parseName("Evan", Language.ENGLISH);
//		testGraph.parseName("Evelyn", Language.ENGLISH);
//		testGraph.parseName("Fay", Language.ENGLISH);
//		testGraph.parseName("Finley", Language.ENGLISH);
//		testGraph.parseName("Finn", Language.ENGLISH);
//		testGraph.parseName("Florence", Language.ENGLISH);
//		testGraph.parseName("Garnet", Language.ENGLISH);
//		testGraph.parseName("Gay", Language.ENGLISH);
//		testGraph.parseName("Gayle", Language.ENGLISH);
//		testGraph.parseName("Hayden", Language.ENGLISH);
//		testGraph.parseName("Hayley", Language.ENGLISH);
//		testGraph.parseName("Heaven", Language.ENGLISH);
//		testGraph.parseName("Hilary", Language.ENGLISH);
//		testGraph.parseName("Hillary", Language.ENGLISH);
//		testGraph.parseName("Hope", Language.ENGLISH);
//		testGraph.parseName("Ivy", Language.ENGLISH);
//		
		int nodeListSize = testGraph.getNodeListSize();
		int edgeListSize = testGraph.getEdgeListSize();
		System.out.println("Number of nodes is " + nodeListSize);
		System.out.println("Number of edges is " + edgeListSize + "\n");
		
		Language testLanguage = testGraph.predictLanguage("Mata");
		System.out.println("The most likely language for 'Mata' is " + testLanguage + "\n");
		testLanguage = testGraph.predictLanguage("Fetuao");
		System.out.println("The most likely language for 'Fetuao' is " + testLanguage + "\n");
		
//		Language kanakoLanguage = testGraph.predictLanguage("Kanako");
//		
//		System.out.println("The language of origin for the name 'Kanako' was tested");
//		System.out.println("'Kanako' is a " + kanakoLanguage + " name");
//		
//		Language fedoseyev = testGraph.predictLanguage("Fedoseyev");
//		
//		System.out.println("The language of origin for the name 'Fedoseyev' was tested");
//		System.out.println("'Fedoseyev' is a " + fedoseyev + " name");
		
		
		
	}

}
