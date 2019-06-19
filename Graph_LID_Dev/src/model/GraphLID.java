package model;

import model.Node;
import java.util.ArrayList;
import model.Language;
import java.util.HashMap;

public class GraphLID extends AbstractGraph{

	private ArrayList<Node> graphNodes = new ArrayList<>();
	private ArrayList<Edge> graphEdges = new ArrayList<>();
	
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
	
	public void parseName(String name, Language lang) {
		//This function will take a name and a language as inputs, and add them to the graph
		
		if (name.length() == 0) return;
		
		int numOfTrigrams = 0;
		if (name.length() <= 3) numOfTrigrams = 1; //Trigram is whole name
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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
//		GraphLID testGraph = new GraphLID();
//		testGraph.addNode(Language.ENGLISH, "the");
//		
//		System.out.println("Size of node list is " + testGraph.getNodeListSize());
//		
//		Node testNode = testGraph.getNode(testGraph.getNodeListSize()-1);
//		HashMap<Language, Integer> testHashMap = testNode.getAllLangValues();
//		
//		for (Language lang : Language.values()) {
//			int langVal = testHashMap.get(lang);
//			System.out.println("Trigram is 'the'. Lang val for " + lang + " is " + langVal);
//		}
//		
//		
		
//		
//		Node testNode = new Node(Language.RUSSIAN, "eto");
//		System.out.println("testNode has a trigram of " + testNode.getTrigram());
		
//		String name = "Yuuko";
//		String nameSubString = name.substring(0, 3);
//		System.out.println(nameSubString);

		
//		System.out.println("Input name is Yuuko. Yuuko has been input as a JAPANESE name");
		
//		int nodeListSize = testGraph.getNodeListSize();
//		for (int i = 0; i < nodeListSize; i++) {
//			
//			Node tempNode = testGraph.getNode(i);
//			System.out.println("\nNode is " + tempNode.getTrigram());
//			
//			for (Language lang : Language.values()) {
//				System.out.println("For this node, the value of " + lang + " is " + tempNode.getLangVal(lang));
//			}
//			
//		}
		
		
//		nodeListSize = testGraph.getNodeListSize();
//		System.out.println("NODE LIST SIZE IS " + nodeListSize);
		
//		System.out.println("\nThere is a node with the trigram 'Yuu': " + testGraph.nodeExists("Yuu") + "\n");
		
//		System.out.println("Input name is Yuuko. Yuuko has been input as an ENGLISH name");
//		
//		nodeListSize = testGraph.getNodeListSize();
//		for (int i = 0; i < nodeListSize; i++) {
//			
//			Node tempNode = testGraph.getNode(i);
//			System.out.println("\nNode is " + tempNode.getTrigram());
//			
//			for (Language lang : Language.values()) {
//				if (lang == Language.JAPANESE || lang == Language.ENGLISH) System.out.println("For this node, the value of " + lang + " is " + tempNode.getLangVal(lang));
//			}
//			
//		}
//		
////		System.out.println("\nNumber of nodes after parsing the name 'Yuuko' as both ENGLISH and JAPANESE is " + nodeListSize);
//		
		
		GraphLID testGraph = new GraphLID();
		testGraph.parseName("Yuuko", Language.JAPANESE);
//		testGraph.parseName("Yuuko", Language.ENGLISH);
		testGraph.parseName("Casey", Language.IRISH);
		testGraph.parseName("Yumika", Language.JAPANESE);
		testGraph.parseName("Yuu", Language.JAPANESE);		
		testGraph.parseName("Takayama", Language.JAPANESE);
		testGraph.parseName("Takahashi", Language.JAPANESE);
		testGraph.parseName("Takahara", Language.JAPANESE);
		testGraph.parseName("Uehara", Language.JAPANESE);
		testGraph.parseName("Matsuyama", Language.JAPANESE);
		testGraph.parseName("Tretiakova", Language.RUSSIAN);
		testGraph.parseName("Tchaikovsky", Language.RUSSIAN);
		testGraph.parseName("Piotr", Language.RUSSIAN);
		testGraph.parseName("Gagarin", Language.RUSSIAN);
		testGraph.parseName("Junko", Language.JAPANESE);
		testGraph.parseName("Ayaka", Language.JAPANESE);
		testGraph.parseName("Taiki", Language.JAPANESE);
		testGraph.parseName("Itadani", Language.JAPANESE);
		testGraph.parseName("Risako", Language.JAPANESE);
		testGraph.parseName("Tatsuya", Language.JAPANESE);
		testGraph.parseName("Aiko", Language.JAPANESE);
		testGraph.parseName("Akane", Language.JAPANESE);
		testGraph.parseName("Kana", Language.JAPANESE);
		testGraph.parseName("Aoi", Language.JAPANESE);
		testGraph.parseName("Asuka", Language.JAPANESE);
		testGraph.parseName("Chiaki", Language.JAPANESE);
		testGraph.parseName("Hanako", Language.JAPANESE);
		testGraph.parseName("Haruko", Language.JAPANESE);
		testGraph.parseName("Hikari", Language.JAPANESE);
		testGraph.parseName("Hikaru", Language.JAPANESE);
		testGraph.parseName("Hitomi", Language.JAPANESE);
		testGraph.parseName("Katsumi", Language.JAPANESE);
		testGraph.parseName("Kotone", Language.JAPANESE);
		testGraph.parseName("Kyoko", Language.JAPANESE);
		testGraph.parseName("Midori", Language.JAPANESE);
		testGraph.parseName("Miyako", Language.JAPANESE);
		testGraph.parseName("Momoko", Language.JAPANESE);
		testGraph.parseName("Nanapeko", Language.JAPANESE);
		testGraph.parseName("Nanako", Language.JAPANESE);
		testGraph.parseName("Naomi", Language.JAPANESE);
		testGraph.parseName("Natsuko", Language.JAPANESE);
		testGraph.parseName("Haruna", Language.JAPANESE);
		testGraph.parseName("Ono", Language.JAPANESE);
		testGraph.parseName("Tomomi", Language.JAPANESE);
		testGraph.parseName("Ogawa", Language.JAPANESE);
		testGraph.parseName("Rina", Language.JAPANESE);
		testGraph.parseName("Suzuki", Language.JAPANESE);
		testGraph.parseName("Mami", Language.JAPANESE);
		testGraph.parseName("Sasazaki", Language.JAPANESE);
		testGraph.parseName("Suzuhana", Language.JAPANESE);
		
		testGraph.parseName("Abakumov", Language.RUSSIAN);
		testGraph.parseName("Abdulov", Language.RUSSIAN);
		testGraph.parseName("Abramov", Language.RUSSIAN);
		testGraph.parseName("Abramovich", Language.RUSSIAN);
		testGraph.parseName("Astakhov", Language.RUSSIAN);
		testGraph.parseName("Aslanov", Language.RUSSIAN);
		testGraph.parseName("Baskin", Language.RUSSIAN);
		testGraph.parseName("Bebchuk", Language.RUSSIAN);
		testGraph.parseName("Bebchuk", Language.RUSSIAN);
		testGraph.parseName("Bezrukov", Language.RUSSIAN);
		testGraph.parseName("Bezrodny", Language.RUSSIAN);
		testGraph.parseName("Belyakov", Language.RUSSIAN);
		testGraph.parseName("Bogomalov", Language.RUSSIAN);
		testGraph.parseName("Bryzgalov", Language.RUSSIAN);
		testGraph.parseName("Vikhrov", Language.RUSSIAN);
		testGraph.parseName("Vinogradov", Language.RUSSIAN);
		testGraph.parseName("Vedenin", Language.RUSSIAN);
		testGraph.parseName("Vavilov", Language.RUSSIAN);
		testGraph.parseName("Vorontsov", Language.RUSSIAN);
		testGraph.parseName("Romanova", Language.RUSSIAN);
		testGraph.parseName("Vasilevsky", Language.RUSSIAN);
		testGraph.parseName("Voskoboynikov", Language.RUSSIAN);
		testGraph.parseName("Voskresensky", Language.RUSSIAN);
		testGraph.parseName("Goncharov", Language.RUSSIAN);
		testGraph.parseName("Glazkov", Language.RUSSIAN);
		testGraph.parseName("Golubtsov", Language.RUSSIAN);
		testGraph.parseName("Glukhov", Language.RUSSIAN);
		testGraph.parseName("Gorbachyov", Language.RUSSIAN);
		testGraph.parseName("Grebenshchikov", Language.RUSSIAN);
		testGraph.parseName("Gurkovsky", Language.RUSSIAN);
		testGraph.parseName("Gruzinsky", Language.RUSSIAN);
		
		testGraph.parseName("Abby", Language.ENGLISH);
		testGraph.parseName("Alicia", Language.ENGLISH);
		testGraph.parseName("Alex", Language.ENGLISH);
		testGraph.parseName("Ashley", Language.ENGLISH);
		testGraph.parseName("Ariel", Language.ENGLISH);
		testGraph.parseName("Bailey", Language.ENGLISH);
		testGraph.parseName("Blair", Language.ENGLISH);
		testGraph.parseName("Bobby", Language.ENGLISH);
		testGraph.parseName("Brett", Language.ENGLISH);
		testGraph.parseName("Bronte", Language.ENGLISH);
		testGraph.parseName("Brooklyn", Language.ENGLISH);
		testGraph.parseName("Bryn", Language.ENGLISH);
		testGraph.parseName("Cadence", Language.ENGLISH);
		testGraph.parseName("Cameron", Language.ENGLISH);
		testGraph.parseName("Cecil", Language.ENGLISH);
		testGraph.parseName("Colby", Language.ENGLISH);
		testGraph.parseName("Courtney", Language.ENGLISH);
		testGraph.parseName("Cole", Language.ENGLISH);
		testGraph.parseName("Dakota", Language.ENGLISH);
		testGraph.parseName("Dana", Language.ENGLISH);
		testGraph.parseName("Darian", Language.ENGLISH);
		testGraph.parseName("Darnell", Language.ENGLISH);
		testGraph.parseName("Darryl", Language.ENGLISH);
		testGraph.parseName("Devin", Language.ENGLISH);
		testGraph.parseName("Dustin", Language.ENGLISH);
		testGraph.parseName("Elis", Language.ENGLISH);
		testGraph.parseName("Elliot", Language.ENGLISH);
		testGraph.parseName("Emerson", Language.ENGLISH);
		testGraph.parseName("Emery", Language.ENGLISH);
		testGraph.parseName("Evan", Language.ENGLISH);
		testGraph.parseName("Evelyn", Language.ENGLISH);
		testGraph.parseName("Fay", Language.ENGLISH);
		testGraph.parseName("Finley", Language.ENGLISH);
		testGraph.parseName("Finn", Language.ENGLISH);
		testGraph.parseName("Florence", Language.ENGLISH);
		testGraph.parseName("Garnet", Language.ENGLISH);
		testGraph.parseName("Gay", Language.ENGLISH);
		testGraph.parseName("Gayle", Language.ENGLISH);
		testGraph.parseName("Hayden", Language.ENGLISH);
		testGraph.parseName("Hayley", Language.ENGLISH);
		testGraph.parseName("Heaven", Language.ENGLISH);
		testGraph.parseName("Hilary", Language.ENGLISH);
		testGraph.parseName("Hillary", Language.ENGLISH);
		testGraph.parseName("Hope", Language.ENGLISH);
		testGraph.parseName("Ivy", Language.ENGLISH);
		
		
//		testGraph.parseName("Hinako", Language.JAPANESE);
//		testGraph.parseName(name, lang);
		
		int nodeListSize = testGraph.getNodeListSize();
		int edgeListSize = testGraph.getEdgeListSize();
		System.out.println("Number of nodes is now " + nodeListSize);
		System.out.println("Number of edges is now " + edgeListSize + "\n");
		
		Language kanakoLanguage = testGraph.predictLanguage("Kanako");
		
		System.out.println("The language of origin for the name 'Kanako' was tested");
		System.out.println("'Kanako' is a " + kanakoLanguage + " name");
		
		Language fedoseyev = testGraph.predictLanguage("Fedoseyev");
		
		System.out.println("The language of origin for the name 'Fedoseyev' was tested");
		System.out.println("'Fedoseyev' is a " + fedoseyev + " name");
		
		//Language sayuriLanguage = testGraph.predictLanguage("Sayuri");
		
		//System.out.println("The language of origin for the name 'Sayuri' was tested");
		//System.out.println("'Sayuri' is a " + sayuriLanguage + " name");
		
		//Language yumikaLanguage = testGraph.predictLanguage("Yumika");
		
		//System.out.println("The language of origin for the name 'Yumika' was tested");
		//System.out.println("'Yumika' is a " + yumikaLanguage + " name");
		
		
		
		//Language vladimirLanguage = testGraph.predictLanguage("Vladimir");
		
		//System.out.println("The language of origin for the name 'Vladimir' was tested");
		//System.out.println("'Vladimir' is a " + vladimirLanguage + " name");
		
		
	}

}
