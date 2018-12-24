package test;

import java.awt.List;
import java.util.ArrayList;
import java.util.Hashtable;

import knowledgeBase.inferenceNet.InferenceNet;
import features.Features.features;
import preprocessing.FormatFile;
import util.FileData;
import util.Token;

public class ChecksNonTextSchutz {

	public static void main(String [] args) {
	
		//String path = "data/schutz/conceptNet_features_I";
		String path = "/Users/raquelsilveira/Desktop/Máquina Unifor/conceptNet_I2";
		ArrayList<FileData> listFiles = new FormatFile().readFormatFiles(path);
		
		//checksTagsNTRepetidas(listFiles);
		//checksTermoInfere(listFiles);
		//checksRelation(listFiles);
		contaTagsNonText(listFiles);
	}
	
	public static void contaTagsNonText(ArrayList<FileData> listFiles) {
		int countTagsNonText = 0, countTagsText = 0, countTags = 0, countTagsOriginais = 0;
		int countTokensNonText = 0, countTokensText = 0;
		for (FileData file : listFiles) {
			countTags += file.getTags().size();
			countTagsOriginais += file.tagsOriginalFile.size();
			for (Token t : file.getTags()) {
				if (t.features.get(features.ofText.getFeature()) == 0)
					countTagsNonText++;
				else
					countTagsText++;
			}
			
			for (Token t : file.getTokens()) {
				if (t.getClassification().equals("No")) {
					if (t.features.get(features.ofText.getFeature()) == 0)
						countTokensNonText++;
					else
						countTokensText++; 
				}
			}
		}
		System.out.println("Tags originais: " + countTagsOriginais);
		System.out.println("Tags: " + countTags);
		System.out.println("Tags text: " + countTagsText);
		System.out.println("Tags non-text: " + countTagsNonText);
		
		System.out.println("Tokens text: " + countTokensText);
		System.out.println("Tokens non-text: " + countTokensNonText);
	}
	
	private static void checksRelation(ArrayList<FileData> listFiles) {
		int isATag = 0, isAToken = 0;
		int taginferidoByTag = 0, tokenInferidoByToken = 0;
		for (FileData file : listFiles) {
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 0) {
					
					//Obtem o termo que inferiu de maior TFxIDF
					Token maiorTfIdf = null;
					for (Token aux : token.tokenWhoInfered.keySet()) {
						if (maiorTfIdf == null)
							maiorTfIdf = aux;
						else {
							if (aux.features.get(features.tf_idf.getFeature()) >= maiorTfIdf.features.get(features.tf_idf.getFeature()))
								maiorTfIdf = aux;
						}
					}
					
					if (token.getClassification().equals("Yes")) {
						if (token.tokenWhoInfered.get(maiorTfIdf).relation.contains("IsA"))
							isATag++;
						
						System.out.println(token.tokenWhoInfered.get(maiorTfIdf));
						
						if (maiorTfIdf.getClassification().equals("Yes"))
							taginferidoByTag++;
						
					} else {
						if (token.tokenWhoInfered.get(maiorTfIdf).relation.contains("IsA"))
							isAToken++;
						
						if (maiorTfIdf.getClassification().equals("No"))
							tokenInferidoByToken++;
					}
				}
			}
		}
		
		System.out.println("---TAG---");
		System.out.println("IsA: " + isATag);
		System.out.println("Inferido por tag: " + taginferidoByTag);
		
		System.out.println("---TOKEN---");
		System.out.println("IsA: " + isAToken);
		System.out.println("Inferido por token: " + tokenInferidoByToken);
	}
	
	public static void checksTermoInfere(ArrayList<FileData> listFiles) {
		int sumInfereTag = 0, sumInfereToken = 0;
		int maiorInfereTag = 0, maiorInfereToken = 0;
		int mais1Tag = 0, mais1Token = 0;
		int qtdeTagNT = 0, qtdeTokenNT = 0;
		for (FileData file : listFiles) {
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 0) {
					if (token.getClassification().equals("Yes")) {
						qtdeTagNT++;
						sumInfereTag += token.tokenWhoInfered.size();
						if (token.tokenWhoInfered.size() > 1)
							mais1Tag++;
						if (token.tokenWhoInfered.size() > maiorInfereTag)
							maiorInfereTag = token.tokenWhoInfered.size();
					} else {
						qtdeTokenNT++;
						sumInfereToken += token.tokenWhoInfered.size();
						if (token.tokenWhoInfered.size() > 1)
							mais1Token++;
						if (token.tokenWhoInfered.size() > maiorInfereToken)
							maiorInfereToken = token.tokenWhoInfered.size();
					}
				}
			}
		}
		System.out.println("---TAG---");
		System.out.println("Qtde: " + qtdeTagNT);
		System.out.println("Soma de inferidos: " + sumInfereTag);
		System.out.println("Média de inferidos: " + sumInfereTag/qtdeTagNT);
		System.out.println("Inferido por mais de 1: " + mais1Tag); 
		System.out.println("Maior qtde de inferidos: " + maiorInfereTag);
		
		System.out.println("---TOKEN---");
		System.out.println("Qtde: " + qtdeTokenNT);
		System.out.println("Soma de inferidos: " + sumInfereToken);
		System.out.println("Média de inferidos: " + sumInfereToken/qtdeTokenNT);
		System.out.println("Inferido por mais de 1: " + mais1Token);
		System.out.println("Maior qtde de inferidos: " + maiorInfereToken);
	}
	
	public static void checksTagsNTRepetidas(ArrayList<FileData> listFiles) {
		
		Hashtable<String, Integer> tagsNT = new Hashtable<String, Integer>();
		Hashtable<String, Integer> tokensNT = new Hashtable<String, Integer>();
		for (FileData file : listFiles) {
			for (Token t : file.getTokens()) {
				if (t.features.get(features.ofText.getFeature()) == 0) {
					if (t.getClassification().equals("Yes")) {
						if (tagsNT.containsKey(t.getLemma()))
							tagsNT.put(t.getLemma(), tagsNT.get(t.getLemma()) + 1);
						else
							tagsNT.put(t.getLemma(), 1);
					} else {
						if (tokensNT.containsKey(t.getLemma()))
							tokensNT.put(t.getLemma(), tokensNT.get(t.getLemma()) + 1);
						else
							tokensNT.put(t.getLemma(), 1);
					}
				}
			}
		}
		
		int repet1Tag = 0, repet1Token = 0, sumTag = 0, sumToken = 0;
		int maiorTag = 0, maiorToken = 0;
		for (String key : tagsNT.keySet()) {
			sumTag += tagsNT.get(key);
			if (tagsNT.get(key) > maiorTag)
				maiorTag = tagsNT.get(key);
			
			if (tagsNT.get(key) > 1)
				repet1Tag++;
		}
		
		for (String key : tokensNT.keySet()) {
			sumToken += tokensNT.get(key);
			
			if (tokensNT.get(key) > maiorToken)
				maiorToken = tokensNT.get(key);
			
			if (tokensNT.get(key) > 1)
				repet1Token++;
		}
		
		System.out.println("--- TAG ---");
		System.out.println("Total: " + sumTag);
		System.out.println("Qtde de tags NT: " + tagsNT.size());
		System.out.println("Mais que 1: " + repet1Tag);
		System.out.println("Média: " + sumTag/tagsNT.size());
		System.out.println("Maior qtde de inferidos: " + maiorTag);
		
		System.out.println("--- TOKEN ---");
		System.out.println("Total: " + sumToken);
		System.out.println("Qtde de tokens NT: " + tokensNT.size());
		System.out.println("Mais que 1: " + repet1Token);
		System.out.println("Média: " + sumToken/tokensNT.size());
		System.out.println("Maior qtde de inferidos: " + maiorToken);
	}
}
