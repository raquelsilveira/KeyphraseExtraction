package test;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import br.com.informar.knowledgebase.KnowledgeBase;
import br.com.informar.knowledgebase.db.MongoDB;
import br.com.informar.knowledgebase.model.Mention;
import config.Config;
import classifier.RF;
import preprocessing.FormatFile;
import util.FileData;
import util.Token;
import features.Features;
import features.Keyphraseness;
import features.WikipediaKeyphraseness;
import features.Features.features;
import features.SemanticSimilarity;

public class Result {

	public static void main(String[] args) {
		
		String pathData = "data/citeULike";
		int percentNonText = 0; //0: Without nonText; -1: Non manipulated base; XX: Percentage of manipulation
		
		if (args.length > 0) {
			pathData = args[0];
			MainInfNet.modelFeatures = Integer.parseInt(args[1]); //0: KEA; 1: MAUI; 2: MAUI + ISR
			MainInfNet.modelBalance = Integer.parseInt(args[2]); //0: Without balance; 1: SMOTE; 2: Over-sampling majority class
			percentNonText = Integer.parseInt(args[3]);
		}
		
		percentNonText = 0; //0: Without nonText; -1: Non manipulated base; XX: Percentage of manipulation
		MainInfNet.modelFeatures = 0; //0: KEA; 1: MAUI; 2: MAUI + ISR                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     ; //0: KEA; 1: MAUI; 2: MAUI + ISR
		MainInfNet.modelBalance = 0; //0: Without balance; 1: SMOTE; 2: Over-sampling majority class; 3: SmoteAndEnn
		
		ArrayList<FileData> listFiles = new FormatFile().readFormatFiles(pathData);
		
		int countTokensNaoTexto = 0, countTagsNaoTexto = 0;
		int countTokensTexto = 0, countTagsTexto = 0;
		for (FileData file : listFiles) {
			for (Token token : file.getTokens()) {
				
				if (file.tagsOriginalFile.containsKey(token.getLemma()))
					token.setClassification("Yes");
				
				if (token.conceptWikipedia == null || token.conceptWikipedia.equals("")) {
					token.features.put(features.nodeDegree.getFeature(), 0.0D);
					token.features.put(features.inverseWikipediaLinkade.getFeature(), 0.0D);
					token.features.put(features.semanticSimilarity.getFeature(), 0.0D);
					token.features.put(features.wikipediaKeyphraseness.getFeature(), 0.0D);
				}
				
				if (token.features.get(features.ofText.getFeature()) == 0) { //Nao texto
					//token.originalInferenceNet = true;
					if (token.getClassification().equals("Yes"))
						countTagsNaoTexto++;
					else
						countTokensNaoTexto++;
				} else { //Texto
					if (token.getClassification().equals("Yes"))
						countTagsTexto++;
					else
						countTokensTexto++;
				}
			}
		}
		
		System.out.println("TAGS: " + (countTagsNaoTexto + countTagsTexto) + " (Texto: " + countTagsTexto + ", Nao texto: " + countTagsNaoTexto + ")");
		System.out.println("TOKENS: " + (countTokensNaoTexto + countTokensTexto) + " (Texto: " + countTokensTexto + ", Nao texto: " + countTokensNaoTexto + ")");
		
		//remove the terms not of text
		if (percentNonText == 0 || percentNonText == -1) {
			for (FileData file : listFiles) {
				ArrayList<Token> tokensRemoved = new ArrayList<Token>();
				for (Token token : file.getTokens()) {
					if (token.features.get(features.ofText.getFeature()) == 0 && 
					   (percentNonText != -1))// || (percentNonText == -1 && !token.originalInferenceNet)))
						tokensRemoved.add(token);
				}
				file.getTokens().removeAll(tokensRemoved);
			}
		}
		
		System.out.println("Verificando keyphraseness...");
		Hashtable<String, Integer> tags = new Hashtable<String, Integer>();
		for (FileData file : listFiles) {
			for (Token token : file.getTags()) {
				if (tags.containsKey(token.getLemma()))
					tags.put(token.getLemma(), (tags.get(token.getLemma())+1));
				else
					tags.put(token.getLemma(), 1);
			}
		}
		
		for (FileData file : listFiles) {
			for (Token token : file.getTokens()) {
				double keyphraseness = 0.0;
				if (tags.containsKey(token.getLemma()))
					keyphraseness = (double)tags.get(token.getLemma());
				
				token.features.put(features.keyphraseness.getFeature(), keyphraseness);///listFiles.size());
			}
		}
		System.out.println("Finalizando verificação de keyphraseness...");
		
		//Multiplica pela wikipediaKeyphraseness, conforme implementacao anterior da SS
		for (FileData file : listFiles) {
			for (Token token : file.getTokens()) {
				if (token.conceptWikipedia != null && !token.conceptWikipedia.equals("")) {
					double semanticSimilarity = token.features.get(features.semanticSimilarity.getFeature());
					token.features.put(features.semanticSimilarity.getFeature(), semanticSimilarity * token.features.get(features.wikipediaKeyphraseness.getFeature()));
				}
			}
		}
		
		//Set the feature ISR
		if (MainInfNet.modelFeatures == 2) {
			for (FileData file : listFiles) {
				for (Token token : file.getTokens()) {
					token.features.put(features.roleIsARelatedTo.getFeature(), Double.NaN);
				}
			}
				
			System.out.println("Calculando ISR...");
			int f = 0;
			for (FileData file : listFiles) {
				System.out.println(++f);
				for (Token token : file.getTokens()) {
					
					if (token.features.get(features.ofText.getFeature()) == 0) {
						Token maiorTfIdf = token.tokenWhoInfered.keySet().iterator().next();
						for (Token aux : token.tokenWhoInfered.keySet()) {
							if (aux.features.get(features.tf_idf.getFeature()) >= maiorTfIdf.features.get(features.tf_idf.getFeature()))
								maiorTfIdf = aux;
						}
					
						if (token.features.get(features.semanticSimilarity.getFeature()) >= maiorTfIdf.features.get(features.semanticSimilarity.getFeature())) {
							token.features.put(features.roleIsARelatedTo.getFeature(), 1.0);
							//if (token.getClassification().equals("No")) {
							//	Keyphraseness key = new Keyphraseness();
							//	key.calculateKeyphraseness(token, listFiles);
							//}
						}
						else
							token.features.put(features.roleIsARelatedTo.getFeature(), 0.0);
					} else {
						token.features.put(features.roleIsARelatedTo.getFeature(), Double.NaN);
					}
				}
			}
		}

		//obtem os valores das features sintaticas dos termos inferidos
		for(FileData file : listFiles) {
			for (Token t : file.getTokens()) {
				if (t.features.get(features.ofText.getFeature()) == 0) {
					t.features.put(features.firstOccurrence.getFeature(), 0.0);
					t.features.put(features.tf_idf.getFeature(), 0.0);
					t.features.put(features.spread.getFeature(), 0.0);
				}
			}
		}
		
		//Normaliza
		System.out.println("Normaling data");
		new Features().normalizeDataFeatures(listFiles);
		
		ArrayList<FileData> listFileNoBalance = (ArrayList<FileData>)listFiles.clone();
		
		if (percentNonText > 0)
			MainInfNet.reductionInferenceNet(listFiles, percentNonText);
		
		System.out.println("Classifing...");
		new RF().runRFCrossValidationDoc(listFiles, listFileNoBalance, false);
		System.out.println("Finished!!!");
	}
}
