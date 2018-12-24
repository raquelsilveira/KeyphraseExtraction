package wordNet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import javax.print.attribute.IntegerSyntax;

import preprocessing.FormatFile;
import util.FileData;
import util.Relation;
import util.StanfordLemmatizer;
import util.Token;

public class ReadDatabase {

	public static Hashtable<String, ItemSynset> itensSynset = new Hashtable<String, ItemSynset>();
	public static HashSet<String> terms = new HashSet<String>(); 
	
	public static void readFiles() {
		String path = "data/wordNet3_1/";
		int i = 0;
		for (File file : new File(path).listFiles()) {
			System.out.println(file.getName());
			if (file.getName().startsWith("index"))
				readIndexFile(file.getAbsolutePath());
		}
		
		for (File file : new File(path).listFiles()) {
			System.out.println(file.getName());
			if (file.getName().startsWith("data"))
				readDataFile(file.getAbsolutePath());
		}
		
		/*System.out.println("Lemmas com simple");
		for (String synsetOff : itensSynset.keySet()) {
			if (itensSynset.get(synsetOff).lemmas.contains("simple")) {
				System.out.println(itensSynset.get(synsetOff).lemmas);
				System.out.println(itensSynset.get(synsetOff).relations);
			}
		}*/
	}
	
	static StanfordLemmatizer lemmatizer = new StanfordLemmatizer();
	
	public static void readIndexFile(String path) {
		try {
			
			BufferedReader buffer = new BufferedReader(new FileReader(path));
			
			while(buffer.ready()){
				String linha = buffer.readLine();
				//System.out.println(linha);
				if (linha.startsWith(" ")) continue;
				
				String[] valores = linha.split(" ");
				
				int qttySynsetOff = Integer.parseInt(valores[Integer.parseInt(valores[3])+3+1]);
				for (int i = 0, index = 6 + Integer.parseInt(valores[3]); i < qttySynsetOff; i++, index++) {
					ItemSynset item = new ItemSynset();
					item.setSynsetOff(valores[index]);
					String lemma = lemmatizer.lemmatize(valores[0].replace("_", " ").replace("-", " ")).toString().replace("[", "").replace("]", "").replace(",", "");
					
					terms.add(lemma);
					//System.out.println(lemma + " -> " + valores[0].replace("_", " ").replace("-", " "));
					//System.out.println(new StanfordLemmatizer().lemmatize(valores[0].replace("_", " ")));
					
					ArrayList<String> lemmas = new ArrayList<String>();
					if (itensSynset.containsKey(item.getSynsetOff()))
						lemmas = itensSynset.get(item.getSynsetOff()).lemmas;
					lemmas.add(lemma);
					item.lemmas = lemmas;
					itensSynset.put(item.getSynsetOff(), item);
				}
			}
			buffer.close();
		} catch (Exception e) { e.printStackTrace();}	
	}
	
	public static void readDataFile(String path) {
		String linha = null;
		try {
			BufferedReader buffer = new BufferedReader(new FileReader(path));
			
			while(buffer.ready()){
				linha = buffer.readLine();
				if (linha.startsWith(" ")) continue;
				
				String[] valores = linha.split(" ");
				//ArrayList<ItemSynset> itens = getItemSynset(valores[0]);
				ItemSynset itemSynset = itensSynset.get(valores[0]);
				
				//if (valores[0].equals("12233207"))
				//	System.out.println(linha);
				
				int qttyRelations = Integer.parseInt(valores[3 + Integer.decode("#" + valores[3])*2 + 1]);
				int beginRelations = 3 + Integer.decode("#" + valores[3])*2+1+1;
				
				for (int i = 0, j = beginRelations; i < qttyRelations; i++, j+=4) {
					String synsetRelation = valores[j+1];
					int posLexical = Integer.decode("#" + valores[j+3].substring(2, 4));
					
					HashSet<String> lemmasRelations = new HashSet<String>();
					if (itemSynset.relations.containsKey(valores[j]))
						lemmasRelations = itemSynset.relations.get(valores[j]);
					
					if (posLexical == 0)
						lemmasRelations.addAll(itensSynset.get(synsetRelation).lemmas);
					//else
					//	lemmasRelations.add(itensSynset.get(synsetRelation).lemmas.get(posLexical-1));
						
					itemSynset.relations.put(valores[j], lemmasRelations);
				}
			}
			buffer.close();
		} catch (Exception e) { System.out.println(linha); e.printStackTrace();}
	}
	
	public static void getNT (String args[]) {
		int tagNT = 0, tokenNT = 0, tagT = 0, tokenT = 0;
		String pathData = "/Users/raquelsilveira/Documents/workspace/TesteBalance/data/citeULike/citeULikeOriginal";
		String pathOutput = "data/citeULike/citeULike_wordNet/";
		
		util.Stemmer stem = new util.Stemmer();
		
		if (args.length > 0) {
			pathData = args[0];
			pathOutput = args[1];
		}
		int f = 0;
		for (File file : new File(pathData).listFiles()) {
			if (!file.getName().endsWith(".xml")) continue;
			System.out.println(++f + " " + file.getName());
			FileData fileData = new FormatFile().readFormatFileByFile(file.getAbsolutePath());
			
			HashSet<String> tokensFile = new HashSet<String>();
			for(Token token : fileData.getTokens())
				tokensFile.add(token.getLemma());
			
			Hashtable<String, Token> tokensAdded = new Hashtable<String, Token>();
			
			for (Token token : fileData.getTokens()) {
				if (token.getClassification().equals("Yes")) tagT++;
				else tokenT++;
				
				Hashtable<String, HashSet<String>> relations = getTermsRelation(token.getLemma());
				for (String rel : relations.keySet()) {
					for (String tokenInfered : relations.get(rel)) {
						if (tokensFile.contains(tokenInfered)) continue;
						
						Token t = new Token();
						t.setLemma(tokenInfered);
						if (tokensAdded.containsKey(tokenInfered)) 
							t.tokenWhoInfered = tokensAdded.get(tokenInfered).tokenWhoInfered;
						else {
							if (fileData.tagsOriginalFile.containsKey(stem.stemmer(t.getLemma()))) {
								t.setClassification("Yes"); tagNT++;
								System.out.println("identificado: " +  t.getLemma());
							}
							else { t.setClassification("No"); tokenNT++; }
						}
						
						Relation rels = new Relation();
						if (t.tokenWhoInfered.containsKey(rel))
							rels = t.tokenWhoInfered.get(rel);
						rels.relation.add(token.getLemma());
						t.tokenWhoInfered.put(rel, rels);
							
						tokensAdded.put(t.getLemma(), t);
					}
				}
			}
			
			for (String key : tokensAdded.keySet())
				fileData.getTokens().add(tokensAdded.get(key));
			
			System.out.println("Tokens: " + (tokenNT + tokenT) + " (NT: " + tokenNT + "; T: " + tokenT + ")");
			System.out.println("Tags: " + (tagNT + tagT) + " (NT: " + tagNT + "; T: " + tagT + ")");
			
			new FormatFile().writeFormatFileByFile(fileData, pathOutput);
		}
		
		System.out.println("Tokens: " + (tokenNT + tokenT) + " (NT: " + tokenNT + "; T: " + tokenT + ")");
		System.out.println("Tags: " + (tagNT + tagT) + " (NT: " + tagNT + "; T: " + tagT + ")");
	}
	
	private static Hashtable<String, HashSet<String>> getTermsRelation(String term) {
		Hashtable<String, HashSet<String>> relations = new Hashtable<String, HashSet<String>>();
		
		for (String synsetOff : itensSynset.keySet()) {
			if (itensSynset.get(synsetOff).lemmas.contains(term)) {
				
				for (String rel : itensSynset.get(synsetOff).relations.keySet()) {
					HashSet<String> relExistents = new HashSet<String>();
					if (relations.containsKey(rel))
						relExistents = relations.get(rel);
					relExistents.addAll(itensSynset.get(synsetOff).relations.get(rel));
					relations.put(rel, relExistents);
				}
				
				HashSet<String> termsSynset = new HashSet<String>();
				if (relations.containsKey("synset"))
					termsSynset = relations.get("synset");
				termsSynset.addAll(itensSynset.get(synsetOff).lemmas);
				termsSynset.remove(term);
				relations.put("synset", termsSynset);
				
				/*if (term.equals("simple")) {
					System.out.println("relations WordNet simple: ");
					//System.out.println(itensSynset.get(synsetOff).relations);
					System.out.println(relations);
					System.out.println(termsSynset);
				}*/
			}
		}
		return relations;
	}
	
	public static void checkExistsTagNTWordNet() {
		String pathData = "/Users/raquelsilveira/Documents/workspace/TesteBalance/data/citeULike/citeULikeOriginal";
		int f = 0, qtdeTagNT = 0, existentWordNet = 0;
		readFiles();
		
		for (File file : new File(pathData).listFiles()) {
			if (!file.getName().endsWith(".xml")) continue;
			System.out.println(++f + " " + file.getName());
			FileData fileData = new FormatFile().readFormatFileByFile(file.getAbsolutePath());
			
			HashSet<String> tagsT = new HashSet<String>();
			for (Token t : fileData.getTags())
				tagsT.add(t.getLemma());
			
			//System.out.println("Terms texto: ");
			//for (Token t : fileData.getTokens()) 
			//	System.out.println(t.getLemma());
			
			for (String tag : fileData.tagsOriginalFile.keySet()) {
				if (!tagsT.contains(tag)) {
					//System.out.println(tag);
					qtdeTagNT++;
					if (terms.contains(tag)) {
						existentWordNet++;
						System.out.println(tag);
					}
				}
			}
		}
		System.out.println("Qtde de tags NT: " + qtdeTagNT);
		System.out.println("Qtde de tags NT na WordNet: " + existentWordNet);
	}
	
	public static void main(String[] args) {
		
		readFiles();
		//System.out.println("Itens WordNet: " + itensSynset.size());
		
		getNT(args);
		
		//checkExistsTagNTWordNet();
		
		/*ItemSynset item = itensSynset.get("00072888");
		System.out.println(item.getSynsetOff());
		System.out.println(item.lemmas);
		for (String relation : item.relations.keySet()) {
			System.out.println(relation + " -> " + item.relations.get(relation));
		}
		
		System.out.println("Itens WordNet: " + itensSynset.size());*/
	}
}