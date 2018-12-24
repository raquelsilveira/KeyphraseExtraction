package knowledgeBase.conceptNet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import preprocessing.FormatFile;
import util.FileData;
import util.Relation;
import util.StanfordLemmatizer;
import util.Token;
import br.com.informar.knowledgebase.db.MongoDB;
import config.Config;
import features.Features.features;

public class ConceptNet {
	
	StanfordLemmatizer lemmatizer = null;
	public ConceptNet() {
		MongoDB.changeMongoParameters(Config.SERVIDOR_DB, Config.PORT_DB, Config.DB_NAME_CONCEPTNET);
		lemmatizer = new StanfordLemmatizer();
	}

	private Hashtable<String, Token> getTokensFile(FileData file) {
		Hashtable<String, Token> tokensFile = new Hashtable<String, Token>();
		for (Token token : file.getTokens()) {
			tokensFile.put(token.getLemma(), token);
		}
		return tokensFile;
	}
	
	/**
	 * Gets all assertions of the conceptNet5
	 * @author raquelsilveira
	 * @date 21/04/2015
	 * @return
	 */
	public Hashtable<String, List<Assertions>> getAllAssertions() {
		List<Assertions> allAssertions = new AssertionsList().findAll();
		System.out.println("Size all: " + allAssertions.size());
		
		int i = 0;
		Hashtable<String, List<Assertions>> assertions = new Hashtable<String, List<Assertions>>();
		for (Assertions assertion : allAssertions) {
			System.out.println(++i);
			if (assertion != null) {
				String name = lemmatizer.lemmatize(assertion.getNameStart()).toString().replace("[", "").replace("]", "").replace(",", "").replace("-", " ");
				if (assertions.containsKey(name))
					assertions.get(name).add(assertion);
				else {
					List<Assertions> list = new ArrayList<Assertions>();
					list.add(assertion);
					assertions.put(name, list);
				}
				
				/*if (assertions.containsKey(assertion.getNameEnd()))
					assertions.get(assertion.getNameEnd()).add(assertion);
				else {
					List<Assertions> list = new ArrayList<Assertions>();
					list.add(assertion);
					assertions.put(assertion.getNameEnd(), list);
				}*/
			}
		}
		return assertions;
	}
	
	/**
	 * Gets the concepts of the conceptNet
	 * @author raquelsilveira
	 * @date 20/04/2015
	 * @param listFiles
	 */
	public void getConcepts(ArrayList<FileData> listFiles) {
		
		Hashtable<String, List<Assertions>> assertions = getAllAssertions();
		
		/*ArrayList<FileData> fileWrited = new FormatFile().readFormatFiles("data/newTest/old/conceptNet1/");
		HashSet<String> writed = new HashSet<String>();
		for (FileData file : fileWrited) 
			writed.add(file.getName());*/
		
		int n = 0;
		for (FileData file : listFiles) {
			System.out.println("File: " + ++n + " (" + file.getTokens().size() + ")");
			//if (writed.contains(file.getName())) continue;
			
			Hashtable<String, Token> tokensFile = getTokensFile(file);
			
			ArrayList<Token> tokensAdded = new ArrayList<Token>();
			int t = 0;
			for (Token token : file.getTokens()) {
				System.out.println(++t);
				
				List<Assertions> assertionsList = assertions.get(token.getLemma());
				if (assertionsList != null && assertionsList.size() > 0) {
					//HashSet<String> assertionsAdded = new HashSet<String>();
					for (Assertions assertion : assertionsList) {
						/*if (!assertionsAdded.contains(assertion.getNameStart())) {
							checksExistentToken(assertion.getNameStart(), assertion.getRelation(), token, tokensAdded, file, tokensFile);
							assertionsAdded.add(assertion.getNameStart());
						}*/
						
						String end = lemmatizer.lemmatize(assertion.getNameEnd()).toString().replace("[", "").replace("]", "").replace(",", "").replace("-", " ");
						//if (!assertionsAdded.contains(end)) {
						checksExistentToken(end, assertion.getRelation(), token, tokensAdded, file, tokensFile);
							//assertionsAdded.add(end);
						//}
					}
				}
			}
			
			System.out.println("Tokens adicionados: " + tokensAdded.size());
			if (tokensAdded.size() > 0)
				file.getTokens().addAll(tokensAdded);
			
			new FormatFile().writeFormatFileByFile(file, "data/newTest/old/conceptNet5/");
		}
	}
	
	/**
	 * Checks if the found token already exists in list token of the file
	 * @author raquelsilveira
	 * @date 20/04/2015
	 * @param name
	 * @param relation
	 * @param tokenWhoInf
	 * @param file
	 * @param tokensFile
	 */
	public void checksExistentToken(String name, String relation, Token tokenWhoInf, ArrayList<Token> tokensAdded, FileData file, Hashtable<String, Token> tokensFile) {
		
		System.out.println("Tags original: " + file.tagsOriginalFile);
		
		if (!tokensFile.containsKey(name)) {
			Token tokenFound = new Token();
			tokenFound.setLemma(name);
			tokenFound.features.put(features.ofText.getFeature(), 0.0);
			Relation rel = new Relation();
			rel.relation.add(relation);
			tokenFound.tokenWhoInfered.put(tokenWhoInf, rel);
			if (file.tagsOriginalFile.containsKey(name)) tokenFound.setClassification("Yes");
			else tokenFound.setClassification("No");
			tokensAdded.add(tokenFound);
			
			/*if (tokenWhoInf.tokenInfered.contains(tokenFound)) {
				tokenWhoInf.tokenInfered.get(tokenFound).add(relation);
			} else {
				ArrayList<String> relationsWho = new ArrayList<String>();
				relationsWho.add(relation);
				tokenWhoInf.tokenInfered.put(tokenFound, relationsWho);
			}*/
			tokensFile.put(tokenFound.getLemma(), tokenFound);
		}
		else { 
			if (tokensFile.get(name).features.get(features.ofText.getFeature()) == 0) {
				
				if (tokensFile.get(name).tokenWhoInfered.containsKey(tokenWhoInf))
					tokensFile.get(name).tokenWhoInfered.get(tokenWhoInf).relation.add(relation);
				else {
					Relation rel = new Relation();
					rel.relation.add(relation);
					tokensFile.get(name).tokenWhoInfered.put(tokenWhoInf, rel);
				}
				
				/*if (tokenWhoInf.tokenInfered.containsKey(tokensFile.get(name))) {
					tokenWhoInf.tokenInfered.get(tokensFile.get(name)).add(relation);
				}
				else {
					ArrayList<String> relations = new ArrayList<String>();
					relations.add(relation);
					tokenWhoInf.tokenInfered.put(tokensFile.get(name), relations);
				}*/
			}
		}
	}
}
