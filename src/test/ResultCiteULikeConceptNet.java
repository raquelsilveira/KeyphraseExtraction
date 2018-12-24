package test;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import config.Config;
import maui.stopwords.Stopwords;
import maui.stopwords.StopwordsEnglish;
import amie.rules.Rule;
import br.com.informar.knowledgebase.model.Concept;
import preprocessing.FormatFile;
import util.FileData;
import util.Token;
import classifier.RF;
import features.Features;
import features.Keyphraseness;
import features.SemanticSimilarity;
import features.SintaticFeatures;
import features.Features.features;

public class ResultCiteULikeConceptNet {

	public static void main_(String[] args) {
		
		/*String pathData = "data/citeULike/teste";
		ArrayList<FileData> listFiles = new FormatFile().readFormatFiles(pathData);
		
		for (FileData file : listFiles) {
			for (Token token : file.getTokens()) {
				token.setClassification("No");
				if (file.tagsOriginalFile.containsKey(token.getLemma())) {
					token.setClassification("Yes");
				}
			}
		}*/
				
		//int count = 0, countTagNonText = 0;
		//int countTags = 0, countTagsOrig = 0;
		
		/*for(FileData files : listFiles) {
			
			for (Token token : files.getTags()) {
				System.out.println(token.getLemma() + " -> " + token.toString());
			}
			
			
			/*if (files.getName().replace(".xml", "").equals("101") || 
					files.getName().replace(".xml", "").equals("112878") ||
					files.getName().replace(".xml", "").equals("114199") ||
					files.getName().replace(".xml", "").equals("1206611") ||
					files.getName().replace(".xml", "").equals("126997") ||
					files.getName().replace(".xml", "").equals("1272533") ||
					files.getName().replace(".xml", "").equals("1307464") ||
					files.getName().replace(".xml", "").equals("1320727") ||
					files.getName().replace(".xml", "").equals("1322799") ||
					files.getName().replace(".xml", "").equals("1336057") ||
					files.getName().replace(".xml", "").equals("136657") ||
					files.getName().replace(".xml", "").equals("1624776") ||
					files.getName().replace(".xml", "").equals("1632947") ||
					files.getName().replace(".xml", "").equals("2235507") ||
					files.getName().replace(".xml", "").equals("238188") ||
					files.getName().replace(".xml", "").equals("292") ||
					files.getName().replace(".xml", "").equals("375823") ||
					files.getName().replace(".xml", "").equals("407273") ||
					files.getName().replace(".xml", "").equals("438129") ||
					files.getName().replace(".xml", "").equals("44") ||
					files.getName().replace(".xml", "").equals("504894") ||
					files.getName().replace(".xml", "").equals("506455") ||
					files.getName().replace(".xml", "").equals("546157") ||
					files.getName().replace(".xml", "").equals("559064") ||
					files.getName().replace(".xml", "").equals("668933") ||
					files.getName().replace(".xml", "").equals("738207") ||
					files.getName().replace(".xml", "").equals("740681") ||
					files.getName().replace(".xml", "").equals("771168") ||
					files.getName().replace(".xml", "").equals("86487") ||							
					files.getName().replace(".xml", "").equals("880918")) {
				
					countTags += files.getTags().size();
					countTagsOrig += files.tagsOriginalFile.size();
					
				}	*/
			/*System.out.println("Tags não existentes...");
			for (String tagOrig : files.tagsOriginalFile.keySet()) {
				boolean found = false; 
				for (Token tag : files.getTags()) {
					if (tag.getLemma().equals(tagOrig)) {
						found = true;
						break;
					}
				}
				if (!found)
					System.out.println(tagOrig + " " + files.tagsOriginalFile.get(tagOrig));
			}*/
			
			/*System.out.println("Tokens text: ");
			for (Token token : files.getTokens()) {
				if (token.getClassification().equals("No") &&
					token.features.get(features.ofText.getFeature()) == 1) {
					System.out.print(token.getLemma() + ", ");
				}
			}
			
			System.out.println("Tokens no-text: ");
			for (Token token : files.getTokens()) {
				if (token.getClassification().equals("No") &&
					token.features.get(features.ofText.getFeature()) == 0) {
					System.out.print(token.getLemma() + ", ");
				}
			}*/
			
			//System.out.println("Tags: ");
			/*boolean tagNonText = false;
			for (Token token : files.getTags()  ) {
				if (token.features.get(features.ofText.getFeature()) == 0) {
					System.out.println(files.getName());
					count++;
					tagNonText = true;
					break;
				}
				//if (token.getClassification().equals("Yes")) {
					//System.out.println(token.getLemma() + " " + token.features.get(features.ofText.getFeature()));
				//}
			}
			if (tagNonText) {
				for (Token token : files.getTags()  ) {
					System.out.println(token.getLemma() + " " + token.features.get(features.ofText.getFeature()));
					if (token.features.get(features.ofText.getFeature()) == 0)
						countTagNonText++;
				}	
			}*/
				
		//}
		
		//System.out.println("Qtde de arquivos com tags não-texto: " + count);
		//System.out.println("Qtde de tags não-texto: " + countTagNonText);
		
		//System.out.println("Qtde de tags: " + countTags);
		//System.out.println("Qtde de tags originais: " + countTagsOrig);
			
		/*for (FileData file : listFiles) {
			System.out.println("Tags: ");
			for (Token token : file.getTags()  ) {
				//if (token.getClassification().equals("Yes")) {
					System.out.println(token.getLemma() + " " + token.features.get(features.ofText.getFeature()));
				//}
			}
		}*/
	}
	
	/**
	 * Obtem a similaridade semantica entre o termo e quem o inferiu
	 * @param args
	 */
	public static void main_3(String[] args) {
		
		/*System.out.println("Teste...");
		SemanticSimilarity sem = new SemanticSimilarity();
		Concept concept1 = sem.knowledgeBase.getConcepts().getConceptByName("panel");
		Concept concept2 = sem.knowledgeBase.getConcepts().getConceptByName("control panel");
		
		if (concept1 != null)
			System.out.println(concept1.getName());
		
		if (concept2 != null)
			System.out.println(concept2.getName());
		
		System.out.println("Acabou Teste...");*/
		
		/*String pathTeste = "data/citeULike/conceptNet_features_I_SS_infered";
		ArrayList<FileData> listRead = new FormatFile().readFormatFiles(pathTeste);
		for (FileData file : listRead) {
			for (Token token : file.getTokens()) {
				if (token.tokenWhoInfered != null &&
					token.tokenWhoInfered.size() > 0) {
					for (Token tokenWhoInfered : token.tokenWhoInfered.keySet()) {
						System.out.println(token.getLemma() + " <-> " + tokenWhoInfered.getLemma() + ": " + token.tokenWhoInfered.get(tokenWhoInfered).similaritySematic);
					}
				}
			}
		}*/
		
		
		String pathData = "data/citeULike/conceptNet_features_I";
		//ArrayList<FileData> listFiles = new FormatFile().readFormatFiles(pathData);
		
		ArrayList<String> filesWrite = new ArrayList<String>();
		//String pathOutput = "data/citeULike/teste/out/";
		String pathOutput = "data/citeULike/conceptNet_features_I_SS_infered_IN/";
		for (File file : new File(pathOutput).listFiles()) {
			filesWrite.add(file.getName().replace(".xml", ""));
		}
		
		//Calcula a similaridade semantica entre os termos inferidos
		SemanticSimilarity semantic = new SemanticSimilarity();
		int f = filesWrite.size();
		
		for (File fileDirectory : new File(pathData).listFiles()) {
		//for (FileData file : listFiles) {
			
			if (!fileDirectory.getAbsolutePath().endsWith(".xml")) continue;
			if (filesWrite.contains(fileDirectory.getName().replace(".xml", ""))) continue;
			
			FileData file = new FormatFile().readFormatFileByFile(fileDirectory.getAbsolutePath());
			
			//Associa as tags
			for (Token token : file.getTokens()) {
				if (file.tagsOriginalFile.containsKey(token.getLemma()))
					token.setClassification("Yes");
			}
			
			System.out.println(++f + " (" + file.getName() + ": " + file.getTokens().size() + ")");
			for (Token token : file.getTokens()) {
				if (token.tokenWhoInfered != null &&
					token.tokenWhoInfered.size() > 0) {
					for (Token tokenWhoInfered : token.tokenWhoInfered.keySet()) {
						semantic.calculateSimilaritySemantic(token, tokenWhoInfered);
						
						//System.out.println(token.getLemma() + " <-> " + tokenWhoInfered.getLemma() + ": " + token.tokenWhoInfered.get(tokenWhoInfered).similaritySematic);
					}
				}
			}
			new FormatFile().writeFormatFileByFile(file, pathOutput);
		}
	}
	
	public static void main_7(String[] args) {
		String path = "data/citeULike/teste/in/";
		ArrayList<FileData> listFiles = new FormatFile().readFormatFiles(path);
		for (FileData file : listFiles) {
			for (Token token : file.getTokens()) {
				if (token.getLemma().equals("classification")) {
					for (Token whoInfered : token.tokenWhoInfered.keySet()) {
						System.out.println("Inferido por: " + whoInfered.getLemma());
						//if (whoInfered.getLemma().equals("discovery")) {
							
							System.out.println("Entrou aqui!");
							SemanticSimilarity semantic = new SemanticSimilarity();
							semantic.calculateSimilaritySemantic(token, whoInfered);
							
						//	break;
						//}
					}
				}
			}
		}
	}
	
	public static void filterSemanticSimilarity(String[] args) {
		
		//String path = "data/citeULike/conceptNet_features_I_SS_infered_IN/";
		String path = "data/citeULike/files_tags_NT_features/";
		String pathOut = "data/citeULike/distributionInference.txt";
		int tamanho = 50;
		String featuresComparation = features.tf_idf.getFeature();
		
		if (args.length > 0) {
			path = args[0];
			tamanho = Integer.parseInt(args[1]);
			switch(args[2]) {
				case "SS": {
					featuresComparation = features.semanticSimilarity.getFeature();
					break;
				}
				case "TFxIDF": {
					featuresComparation = features.tf_idf.getFeature();
					break;
				}
			}
		}
			
		ArrayList<FileData> listFiles = new FormatFile().readFormatFiles(path);
		int countTags = 0, countTokens = 0;
		int countTags2 = 0, countTokens2 = 0;
		int tokenInfered1 = 0, tagInfered1 = 0;
		try {
			for(FileData file : listFiles) {
				FileWriter writer = new FileWriter(new File(pathOut), true);
				int countText = 0;
				
				for (Token token : file.getTokens()) {
					if (token.features.get(features.ofText.getFeature()) == 0 &&
						token.tokenWhoInfered.size() > 1) {
						
						writer.write(token.getLemma() + "; " + token.tokenWhoInfered.size() + "; " + token.getClassification() + "\n");
						if (token.getClassification().equals("Yes"))
							countTags2++;
						else
							countTokens2++;
					}
				}
				writer.close();
				
				//Token[] tokenMaior = new Token[tamanho];
				
				/*for (Token token : file.getTokens()) {
					if (token.features.get(features.ofText.getFeature()) == 1) {
						countText++;
						token.features.put(features.semanticSimilarity.getFeature(), token.features.get(features.semanticSimilarity.getFeature())*token.features.get(features.wikipediaKeyphraseness.getFeature()));
					}
				}
				
				System.out.println("countText: " + countText);
				int tamanhoData = (int) (countText * tamanho/100);
				System.out.println("tamanho" + tamanhoData);
				double[] maiorFeature = new double[tamanhoData];
				maiorFeature[0] = 0;
				int i = 0;
				
				for (Token token : file.getTokens()) {
					if (token.features.get(features.ofText.getFeature()) == 1) {
						if (i < tamanhoData) {
							maiorFeature[i] = token.features.get(featuresComparation);
							//tokenMaior[i] = token;
							i++;
						} else {
							//System.out.println(Arrays.toString(maiorFeature));
							Arrays.sort(maiorFeature);
							if (token.features.get(featuresComparation) > maiorFeature[0]) {
								//tokenMaior[0] = token;
								maiorFeature[0] = token.features.get(featuresComparation);
							}
						}
					}
				}
				 
				System.out.println("Menor valor dos maiores: " + maiorFeature[0]);
				System.out.println("Maior valor dos maiores: " + maiorFeature[maiorFeature.length-1]);
				
				ArrayList<Token> maiores = new ArrayList<Token>();
				for (Token token : file.getTokens()) {
					if (token.features.get(features.ofText.getFeature()) == 1) {
						for (int k = 0; k < maiorFeature.length; k++) {
							if (token.features.get(featuresComparation) == 
								maiorFeature[k]) {
								maiores.add(token);
								break;
							}
						}
					}
				}
				//for (int j = 0; j < tokenMaior.length; j++)
				//	maiores.add(tokenMaior[j]);
				
				//System.out.println("Maiores: " + maiores);
				//System.out.println("Token text: " +countText);
				
				for (Token token : file.getTokens()) {
					if (token.features.get(features.ofText.getFeature()) == 0) {
						
						if (token.getClassification().equals("Yes"))
							countTags++;
						else
							countTokens++;
						
						if (token.tokenWhoInfered.size() > 1) {
							if (token.getClassification().equals("Yes"))
								countTags2++;
							else
								countTokens2++;
							continue;
						}
						
						for (Token whoInfered : token.tokenWhoInfered.keySet()) {
							if (maiores.contains(whoInfered)) {
								//System.out.println("File: " + file.getName() + " Tag: " + token.getLemma());
								if (token.getClassification().equals("Yes"))
									countTags2++;
								else
									countTokens2++;
									
								//found = true;
								break;
							}
						}
						
						//if (!found)
						//	System.out.println("File: " + file.getName() + " Tag: " + token.getLemma());
					}
				}*/
			}
		} catch(Exception e) { e.printStackTrace(); }
		
		System.out.println("Tags: " + countTags);
		System.out.println("Tokens: " + countTokens);
		System.out.println("Count last semantic similarity filter: ");
		System.out.println("Tags: " + countTags2);
		System.out.println("Tokens: " + countTokens2);
		System.out.println("Infered by 1: ");
		System.out.println("Tags: " + tagInfered1);
		System.out.println("Tokens: " + tokenInfered1);
	}
	
	
	/**
	 * This method filters the tokens according the similarity semantic feature.
	 * 50% of the tokens with bigger similarity semantic infers the non-text terms.
	 * @param listFiles
	 * @return
	 */
	public static void filterBySS(ArrayList<FileData> listFiles) {
		
		int tamanho = 0;
		String featuresComparation = features.semanticSimilarity.getFeature();
		
		for(FileData file : listFiles) {
			
			int countText = 0;
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 1)
					countText++;
			}
			
			tamanho = countText / 2;
			double[] maiorFeature = new double[tamanho];
			maiorFeature[0] = 0;
			int i = 0;
			
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 1) {
					if (i < tamanho) {
						maiorFeature[i] = token.features.get(featuresComparation);
						i++;
					} else {
						Arrays.sort(maiorFeature);
						if (token.features.get(featuresComparation) > maiorFeature[0])
							maiorFeature[0] = token.features.get(featuresComparation);
					}
				}
			}
			 
			ArrayList<Token> maiores = new ArrayList<Token>();
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 1) {
					for (int k = 0; k < maiorFeature.length; k++) {
						if (token.features.get(featuresComparation) == 
							maiorFeature[k]) {
							maiores.add(token);
							break;
						}
					}
				}
			}
			
			ArrayList<Token> tokenRemoved = new ArrayList<Token>();
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 0) {
					ArrayList<Token> whoInferedRemoved = new ArrayList<Token>();
					for (Token whoInfered : token.tokenWhoInfered.keySet()) {
						if (!maiores.contains(whoInfered))
							whoInferedRemoved.add(whoInfered);
					}
					
					for (Token whoInfered : whoInferedRemoved)
						token.tokenWhoInfered.remove(whoInfered);
					
					if (token.tokenWhoInfered.size() == 0)
						tokenRemoved.add(token);
				}
			}
			file.getTokens().removeAll(tokenRemoved);
		}
	}
	
	/**
	 * Faz a analise dos termos inferidos com quem inferiu
	 * @param args
	 */
	public static void main_9(String[] args) {
		String path = "data/citeULike/conceptNet_features_I_SS_infered_IN/";
		//String path = "data/citeULike/teste/";
		ArrayList<FileData> listFiles = new FormatFile().readFormatFiles(path);
		
		//Associa as tags
		for (FileData file : listFiles) {
			for (Token token : file.getTokens()) {
				if (file.tagsOriginalFile.containsKey(token.getLemma()))
					token.setClassification("Yes");
			}
		}
		
		int tagSemanticZero = 0, tokenSemanticZero = 0;
		int countTagNT = 0, countTokenNT = 0;
		Token maiorToken = null, maiorTokenWhoInfered = null, menorTag = null, menorTagWhoInfered = null;
		FileData fileMaiorToken = null, fileMenorTag = null;
		
		int tagInferedByTag = 0, tokenInferedByTag = 0;
		
		double menorSSTag = 10, maiorSSToken = 0;
		for (FileData file : listFiles) {
			for (Token token : file.getTokens()) {
				boolean found = false;
				if (token.features.get(features.ofText.getFeature())==0) {
					
					if (token.getClassification().equals("Yes"))
						countTagNT++;
					else
						countTokenNT++;	
					
					boolean allZero = true;
					
					double menorSS = 10;
					for (Token tokenWhoInfered : token.tokenWhoInfered.keySet()) {
						
						if (!found && tokenWhoInfered.getClassification().equals("Yes")) {
							found = true;
							if (token.getClassification().equals("Yes"))
								tagInferedByTag++;
							else
								tokenInferedByTag++;
						}
						
						if (token.getClassification().equals("Yes")) {
							if (token.tokenWhoInfered.get(tokenWhoInfered).similaritySematic != 0 &&
								token.tokenWhoInfered.get(tokenWhoInfered).similaritySematic < menorSS) {
								menorSS = token.tokenWhoInfered.get(tokenWhoInfered).similaritySematic;
							}
						} else
							if (token.tokenWhoInfered.get(tokenWhoInfered).similaritySematic > maiorSSToken) {
								maiorSSToken = token.tokenWhoInfered.get(tokenWhoInfered).similaritySematic;
								maiorToken = token;
								maiorTokenWhoInfered = tokenWhoInfered;
								fileMaiorToken = file;
							}
						
						if (token.tokenWhoInfered.get(tokenWhoInfered).similaritySematic > 0) {
							allZero = false;
						}
					}
					
					if (menorSS != 0 && menorSS < menorSSTag) {
						menorSSTag = menorSS;
						fileMenorTag = file;
						menorTag = token;
					}
					
					if (allZero) {
						if (token.getClassification().equals("Yes"))
							tagSemanticZero++;
						else
							tokenSemanticZero++;
					}
				}
			}
		}
		
		System.out.println("Tags NT: " + countTagNT);
		System.out.println("Tag SS 0: " + tagSemanticZero);
		System.out.println("Menor SS tag: " + menorSSTag);
		System.out.println("Menor tag: " + fileMenorTag.getName() + " Tag: " + menorTag.getLemma());
		System.out.println("Tag infered by tag: " + tagInferedByTag);
		
		
		System.out.println("Tokens NT: " + countTokenNT);
		System.out.println("Tokens SS 0: " + tokenSemanticZero);
		System.out.println("Maior SS token: " + maiorSSToken);
		System.out.println("Maior token: " + fileMaiorToken.getName() + " Token: " + maiorToken.getLemma() + 
					       " WhoInfered: " + maiorTokenWhoInfered.getLemma());
		System.out.println("Token infered by tag: " + tokenInferedByTag);
	}
	
	/**
	 * Exibe as tags não texto e os termos que a inferiram
	 * @param args
	 */
	public static void main_8(String [] args) {
		String path = "data/citeULike/conceptNet_features_I_SS_infered_IN/";
		ArrayList<FileData> listFiles = new FormatFile().readFormatFiles(path);
		
		//Associa as tags
		for (FileData file : listFiles) {
			for (Token token : file.getTokens()) {
				if (file.tagsOriginalFile.containsKey(token.getLemma()))
					token.setClassification("Yes");
			}
		}
				
		for (FileData file : listFiles) {
			for (Token token : file.getTags()) {
				if (token.features.get(features.ofText.getFeature()) == 0) {
					System.out.println(file.getName());
			
					System.out.println(token.getLemma() + " " + token.getClassification());
					System.out.println("Inferido por: ");
					for (Token tokenWhoInfered : token.tokenWhoInfered.keySet()) {
						System.out.println(tokenWhoInfered.getLemma() + ": " + token.tokenWhoInfered.get(tokenWhoInfered).similaritySematic + " " + token.tokenWhoInfered.get(tokenWhoInfered).relation);
						System.out.println(tokenWhoInfered);
					}
				}
			}
		}
	}
	
	
	public static void filterInference(String[] args) {
		String path = "data/citeULike/infers_conceptNet_AMIE3_5_IN/";
		String pathOut = "data/citeULike/infers_conceptNet_AMIE3_5_Filter_Inferece/";
		
		HashSet<String> filesWrote = new HashSet<String>();
		for (File file : new File(pathOut).listFiles()) 
			filesWrote.add(file.getName());
		
		int i = filesWrote.size();
		for (File file : new File(path).listFiles()) {
			if (!file.getName().endsWith(".xml") || filesWrote.contains(file.getName())) continue;
			
			System.out.println(++i);
			FileData fileData = new FormatFile().readFormatFileByFile(file.getAbsolutePath());
			ArrayList<Token> tokenRemoved = new ArrayList<Token>();
			for (Token token : fileData.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 0) {
					if (token.tokenWhoInfered.size() <= 1)
						tokenRemoved.add(token);
				}
			}
			System.out.print(fileData.getName() + ": " + fileData.getTokens().size());
			fileData.getTokens().removeAll(tokenRemoved);
			System.out.println(" para " + fileData.getTokens().size());
			new FormatFile().writeFormatFileByFile(fileData, pathOut);
		}
	}
	
	public static void countTokens(String[] args) {
		String path = "data/citeULike/infers_conceptNet_AMIE3_5_Filter_Inferece_features/";
		
		int countTagsT = 0, countTagsNT = 0;
		int countTokensT = 0, countTokensNT = 0;
		
		int i = 0;
		for (File file : new File(path).listFiles()) {
			if (!file.getName().endsWith(".xml")) continue;
			
			System.out.println(++i);
			FileData fileData = new FormatFile().readFormatFileByFile(file.getAbsolutePath());
			for(Token token : fileData.getTokens()) {
				
				System.out.println("SS: " + token.features.get(features.semanticSimilarity.getFeature()));
				System.out.println("Node Degree: " + token.features.get(features.nodeDegree.getFeature()));
				System.out.println("IWL: " + token.features.get(features.inverseWikipediaLinkade.getFeature()));
				System.out.println("Wikipedia Keyphraseness: " + token.features.get(features.wikipediaKeyphraseness.getFeature()));
				
				
				if (token.features.get(features.ofText.getFeature()) == 0) {
					if (token.getClassification().equals("Yes"))
						countTagsNT++;
					else
						countTokensNT++;
				} else {
					if (token.getClassification().equals("Yes"))
						countTagsT++;
					else
						countTokensT++;
				}
			}
		}
		
		System.out.println("Tags: " + (countTagsT + countTagsNT) + " (T: " + countTagsT + "; " + countTagsNT + ")");
		System.out.println("Tokens: " + (countTokensT + countTokensNT) + " (T: " + countTokensT + "; " + countTokensNT + ")");
	}
	
	public static void main(String[] args) {
		
		/*FileData file = new FormatFile().readFormatFileByFile("data/citeULike/422950.xml");
		int count = 0;
		for (Token token : file.getTokens()) {
			if (token.features.get(features.ofText.getFeature()) == 0 &&
				token.conceptWikipedia != null &&
				//token.features.get(features.semanticSimilarity.getFeature()) == Double.NaN)// &&
				token.features.get(features.semanticSimilarity.getFeature()) != 0) {
				count++;
				System.out.println(token.features.get(features.semanticSimilarity.getFeature()));
			}
		}
		System.out.println("Qtde: " + count);*/
		
		//ResultCiteULikeConceptNet.analiseNonText();
		//ResultCiteULikeConceptNet.checkNT_DS(args);
		ResultCiteULikeConceptNet.runExperiments(args);
		//ResultCiteULikeConceptNet.readCiteULike(args);
		//ResultCiteULikeConceptNet.calculateFeatures(args);
		//ResultCiteULikeConceptNet.filterInference(args);
		//ResultCiteULikeConceptNet.filterSemanticSimilarity(args);
		//ResultCiteULikeConceptNet.countTokens(args);
		
		/*FileData file = new FileData();
		Token t1 = new Token();
		t1.setLemma("Girl");
		t1.features.put(features.ofText.getFeature(), 1.0);
		file.getTokens().add(t1);
		
		Token t2 = new Token();
		t2.setLemma("Man"
				+ "");
		t2.features.put(features.ofText.getFeature(), 0.0);
		file.getTokens().add(t2);
		
		Token t3 = new Token();
		t3.setLemma("Woman");
		t3.features.put(features.ofText.getFeature(), 1.0);
		file.getTokens().add(t3);
		
		//String teste = "Girl";
		SemanticSimilarity semantic = new SemanticSimilarity();
		//Concept concept = semantic.knowledgeBase.getConcepts().getConceptByName(teste);
		
		semantic.getRelationsWikipediaLast(file);
		
		semantic.calculateNodeDegreeLast(t1);
		semantic.calculateNodeDegreeLast(t2);
		semantic.calculateNodeDegreeLast(t3);
		
		System.out.println("Node degree: ");
		System.out.println(t1.features.get(features.nodeDegree.getFeature()));
		System.out.println(t2.features.get(features.nodeDegree.getFeature()));
		System.out.println(t3.features.get(features.nodeDegree.getFeature()));
		
		semantic.calculateSimilaritySemanticLast2(t1);
		semantic.calculateSimilaritySemanticLast2(t2);
		semantic.calculateSimilaritySemanticLast2(t3);
		
		System.out.println("Semantic Similarity: ");
		System.out.println(t1.features.get(features.semanticSimilarity.getFeature()));
		System.out.println(t2.features.get(features.semanticSimilarity.getFeature()));
		System.out.println(t3.features.get(features.semanticSimilarity.getFeature()));*/
	}
	
	public static void checkNT_DS(String[] args) {
		String pathData = "data/citeULike/citeULike";
		ArrayList<FileData> listFiles = new FormatFile().readFormatFiles(pathData);
		Hashtable<String, HashSet<String>> tagsNTAll = new Hashtable<String, HashSet<String>>();
		Hashtable<String, HashSet<String>> tokensTAll = new Hashtable<String, HashSet<String>>();
		int countTotalTags = 0, countTagsT = 0, countTagsNTInitial = 0, countTagNTSize1 = 0;
		for (FileData file : listFiles) {
			//System.out.println(file.tagsOriginalFile);
			
			HashSet<String> tokenT = new HashSet<String>();
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 1)
					tokenT.add(token.getLemma());
			}
			
			countTotalTags += file.tagsOriginalFile.size();
			
			HashSet<String> tagNT = new HashSet<String>();
			for (String tagOrig : file.tagsOriginalFile.keySet()) {
				if (!tokenT.contains(tagOrig)) {
					tagNT.add(tagOrig);
					if (tagOrig.length() == 1) countTagNTSize1++;
					System.out.println(tagOrig + " (" + file.tagsOriginalFile.get(tagOrig) + ") -> " + file.getName());
					countTagsNTInitial++;
				}
				else
					countTagsT++;
			}
			
			tagsNTAll.put(file.getName(), tagNT);
			tokensTAll.put(file.getName(), tokenT);
		}
		
		int countTagNT = 0, exist = 0;
		for (String file : tagsNTAll.keySet()) {
			
			countTagNT += tagsNTAll.get(file).size();
			for (String tagNT : tagsNTAll.get(file)) {
				for (String fileTokens : tokensTAll.keySet()) {
					if (!file.equals(fileTokens)) {
						if (tokensTAll.get(fileTokens).contains(tagNT)) {
							//System.out.println(tagNT + " -> " + fileTokens);
							exist++;
							break;
						}
					}
				}
			}
		}
		
		System.out.println("Total tags: " + countTotalTags + " (T: " + countTagsT + " NT: " + countTagsNTInitial + ")");
		System.out.println("Tag de tamanho 1: " + countTagNTSize1);
		
		System.out.println("Tags NT: " + countTagNT);
		System.out.println("Exist nos outros textos: " + exist);
	}
	
	public static void readCiteULike(String[] args) {
		
		String pathIn = args[0];
		String pathOut = args[1];
		
		ArrayList<FileData> listFiles = new FormatFile().readFormatFiles(pathIn);
		for (FileData file : listFiles) {
			
			ArrayList<Token> tokensNT = new ArrayList<Token>();
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 0)
					tokensNT.add(token);
			}
			
			file.getTokens().removeAll(tokensNT);
			
			new FormatFile().writeFormatFileByFile(file, pathOut);
		}
	}
	
	public static void calculateFeatures(String[] args) {
		try {
			
			double time1 = (double) System.currentTimeMillis();
			
			String pathIn = "data/hulth/wordNet";//args[0];
			String pathOut = "data/hulth/wordNet_features";//args[1];
			String pathText = "data/hulth/preprocessing_features1";//args[2];
			
			System.out.println("Qtty files pathIn: " + new File(pathIn).listFiles().length);
			System.out.println("Qtty files pathOut: " + new File(pathOut).listFiles().length);
			System.out.println("Qtty files pathText: " + new File(pathText).listFiles().length);
			
			File filesIn = new File(pathIn);
			File filesOut = new File(pathOut);
			ArrayList<String> listFilesOut = new ArrayList<String>();
			for(File file : filesOut.listFiles())
				listFilesOut.add(file.getName());
			
			ArrayList<FileData> listFilesText = new FormatFile().readFormatFiles(pathText);
			Hashtable<String, HashSet<String>> tokensFiles = new Hashtable<String, HashSet<String>>();
			for (FileData file : listFilesText) {
				HashSet<String> listTokens = new HashSet<String>();
				for (Token token : file.getTokens()) {
					listTokens.add(token.getLemma());
				}
				tokensFiles.put(file.getName().replace(".xml", ""), listTokens);
			}
						
			SintaticFeatures sintatic = new SintaticFeatures();
			Keyphraseness keyphraseness = new Keyphraseness();
			SemanticSimilarity.getMentions();
			
			System.out.println("Iniatializing the calcule of the features...");
				
			int nThreads = Runtime.getRuntime().availableProcessors();
			nThreads -= 1;
			//nThreads = Integer.parseInt(args[3]);
			//nThreads = (nThreads/2 == 0 ? 1 : nThreads/2);
			//int nThreads = 1;
			
			System.out.println("Qtty files: " + filesIn.listFiles().length);
			System.out.println("nThreads: " + nThreads);
			
			ArrayList<File> files = new ArrayList<File>();
			for (int i = 0; i < filesIn.listFiles().length; i++) {
				if (listFilesOut.contains(filesIn.listFiles()[i].getName()) || !filesIn.listFiles()[i].getName().endsWith(".xml")) continue;
				files.add(filesIn.listFiles()[i]);		
			}
			
			System.out.println("Starting threads...");
			ArrayList<Thread> currentJobs = new ArrayList<>();
	        ArrayList<CalculateFeatures> jobObjects = new ArrayList<>();
			for (int i = 0; i < nThreads; ++i) {
	            CalculateFeatures jobObject = new CalculateFeatures(files, sintatic, keyphraseness, listFilesText, tokensFiles, pathOut) ;
	            Thread job = new Thread(jobObject);
	            currentJobs.add(job);
	            jobObjects.add(jobObject);
	        }
	
	        for (Thread job : currentJobs) {
	            job.start();
	        }
	
	        for (Thread job : currentJobs) {
	            job.join();
	        }
	        
	        double time2 = (double) System.currentTimeMillis();
	        
	        System.out.println("Total duration: " + (time2 - time1));
	        
		} catch(Exception e) { e.printStackTrace(); }
	}
	
	public static void removeTermsBeginEndStopWord(ArrayList<FileData> listFiles) {
		
		Stopwords stopwords = new StopwordsEnglish();
		
		for(FileData file : listFiles) {
			ArrayList<Token> removedTokens = new ArrayList<Token>();
			for (Token token : file.getTokens()) {
				StringTokenizer stringTokenizer = new StringTokenizer(token.getLemma(), " ");
				if (stringTokenizer.countTokens() > 2) {
					String firstToken = stringTokenizer.nextToken();
					String lastToken = "";
					while (stringTokenizer.hasMoreTokens())
						lastToken = stringTokenizer.nextToken();
					
					if (stopwords.isStopword(firstToken) || stopwords.isStopword(lastToken)) {
						removedTokens.add(token);
						//System.out.println("Removido: " + token.getLemma() + " (" + token.features.get(features.ofText.getFeature()) + ")");
					}
				}
			}
			file.getTokens().removeAll(removedTokens);
		}
	}
	
	public static void analiseNonText() {
		
		String pathData = "data/citeULike/teste/in/";
		ArrayList<FileData> listFiles = new FormatFile().readFormatFiles(pathData);
		
		ResultCiteULikeConceptNet.removeTermsBeginEndStopWord(listFiles);
		
		Hashtable<String, Double> semantic = new Hashtable<String, Double>();
		
		int countTextWikipedia = 0;
		
		for(FileData file : listFiles) {
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 1 &&
					token.conceptWikipedia != null && !token.conceptWikipedia.equals(""))
					countTextWikipedia++;
			}
		}
		
		for(FileData file : listFiles) {
			for (Token token : file.getTokens()) {
				
				if (token.conceptWikipedia != null && !token.conceptWikipedia.equals("")) {
					double similaritySemantic = (countTextWikipedia - token.features.get(features.semanticSimilarity.getFeature())) 
							/ (double)countTextWikipedia; //* token.features.get(features.wikipediaKeyphraseness.getFeature());
					
					if (token.getLemma().equals("you turn")) {
						System.out.println(token.mentionWikipedia + " - " + token.conceptWikipedia);
						System.out.println("SS: " + token.features.get(features.semanticSimilarity.getFeature()));
						System.out.println("countTextWikipedia: " + countTextWikipedia);
						System.out.println("wikipediaKeyphraseness: " + token.features.get(features.wikipediaKeyphraseness.getFeature()));
						System.out.println("SS final: " + similaritySemantic);
					}
					
					semantic.put(token.getLemma(), similaritySemantic);
				}
			}
		}
		
		/*
		//Put keys and values in to an arraylist using entryset
		ArrayList myArrayList=new ArrayList(semantic.entrySet());

		//Sort the values based on values first and then keys.
		Collections.sort(myArrayList, new MyComparator());

		//Show sorted results
		Iterator itr=myArrayList.iterator();
		String key="";
		double value=0;
		while(itr.hasNext()){
			Map.Entry e=(Map.Entry)itr.next();
	
			key = (String)e.getKey();
			value = ((Double)e.getValue()).doubleValue();
	
			System.out.println(key + " -> " + value);
		}	
		
		System.out.println("Size context: " + countTextWikipedia);
		*/
	}
	
	/**
	 * Realiza os experimentos
	 * @param args
	 */
	public static void runExperiments(String[] args) {
		//String pathData = "data/citeULike/citeULikeLsa_features1";
		String pathData = "data/hulth/lsa_takelab_50_features1/";
		//String pathData = "data/citeULike/citeULike_without_NT";
		//String pathData = "data/citeULike/teste/in/";
		//String pathData = "data/citeULike/conceptNet_features_I_SS_infered_IN";
		//String pathData = "data/citeULike/infers_conceptNet_AMIE3_5_Filter_Inferece_features_resume3";
		//String pathCorpus = "/Users/raquelsilveira/Documents/Unifor/Aprendizagem de Máquina/Extração de Tópicos/Corpus/SemEval-2010/";
		int percentNonText = 0;
		boolean arquivosGerados = false;
		
		if (args.length > 0) {
			pathData = args[0];
			MainInfNet.modelFeatures = Integer.parseInt(args[1]); //0: KEA; 1: MAUI; 2: MAUI + ISR
			MainInfNet.modelBalance = Integer.parseInt(args[2]); //0: Without balance; 1: SMOTE; 2: Over-sampling majority class
			percentNonText = Integer.parseInt(args[3]); //0: Without nonText; -1: Non manipulated base; XX: Percentage of manipulation
			MainInfNet.percentageOverSampling = Integer.parseInt(args[4]); //Percentage to over-sampling
			arquivosGerados = Boolean.parseBoolean(args[5]);
		}
		
		MainInfNet.modelFeatures = 2; //0: KEA; 1: MAUI; 2: MAUI + ISR; 3: MAUI + ImportanceSemantic; 4: MAUI + ISR + Importance; 5: MAIU + QttyInference
		MainInfNet.modelBalance = 2; //0: Without balance; 1: SMOTE; 2: Over-sampling majority class; 3: SmoteAndEnn; 4: SmoteAndTomekLinks; 5: CNNAndTomekLink; 6: TomekLink; 7: CNN; 8: SMOTEBoost; 9: RUSBoost
		percentNonText = -1; //0: Without nonText; -1: Non manipulated base; XX: Percentage of manipulation
		MainInfNet.percentageOverSampling = 200;
		MainInfNet.outPutExperiments = "data/hulth/output.txt";
		arquivosGerados = false;
		
		/*ArrayList<FileData> listFiles = new ArrayList<FileData>();
		int f = 0;
		long inicio = System.currentTimeMillis();
		File directory = new File(pathData);
		for (File file : directory.listFiles()) {
			if (!file.getAbsolutePath().endsWith(".txt")) continue;
			System.out.println(++f + ": " + file.getName());
			listFiles.add(new FormatFile().readFormatFileByFileTxt(file.getAbsolutePath()));
			if (f % 50 == 0) 
				System.out.println("Duration: " + (System.currentTimeMillis() - inicio)/1000 + "s");
			//new FormatFile().readFormatFileByFileTxt(file.getAbsolutePath());
		}
		
		System.out.println("Duration: " + (System.currentTimeMillis() - inicio)/1000 + "s");
		*/
		
		ArrayList<FileData> listFiles = new FormatFile().readFormatFiles(pathData);
		
		for(FileData file : listFiles) {
			ArrayList<Token> removed = new ArrayList<Token>();
			for (Token token : file.getTokens()) {
				
				if (token.features.get(features.ofText.getFeature()) == 0) {
					if (token.tokenWhoInfered.keySet().size() < 2)
						removed.add(token);
					//System.out.println(token.tokenWhoInfered.size());
				}
			}
			file.getTokens().removeAll(removed);
		}
		
		//Ajusta similaridade semantica
		/*for (FileData file : listFiles) {
			int countConceptWikipedia = 0;
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 1 &&
					token.conceptWikipedia != null && !token.conceptWikipedia.equals(""))
					countConceptWikipedia++;
			}
			
			for (Token token : file.getTokens()) {
				if (token.conceptWikipedia != null && !token.conceptWikipedia.equals("")) {
					double similaritySemantic = token.features.get(features.semanticSimilarity.getFeature());
					//System.out.println(token.getLemma() + " -> " + similaritySemantic);
					
					similaritySemantic = (countConceptWikipedia - similaritySemantic) / countConceptWikipedia * token.features.get(features.wikipediaKeyphraseness.getFeature());
					//System.out.println(token.getLemma() + " -> " + similaritySemantic);
					token.features.put(features.semanticSimilarity.getFeature(), similaritySemantic);
				}
			}
		}*/
		
		//Multiplica pela wikipediaKeyphraseness, conforme implementacao anterior da SS
		/*for (FileData file : listFiles) {
			for (Token token : file.getTokens()) {
				if (token.conceptWikipedia != null && !token.conceptWikipedia.equals("")) {
					double semanticSimilarity = token.features.get(features.semanticSimilarity.getFeature());
					token.features.put(features.semanticSimilarity.getFeature(), semanticSimilarity * token.features.get(features.wikipediaKeyphraseness.getFeature()));
				}
			}
		}*/
		
		//ResultCiteULikeConceptNet.filterBySS(listFiles);
		
		int countTokensNaoTexto = 0, countTagsNaoTexto = 0;
		int countTokensTexto = 0, countTagsTexto = 0;
		for (FileData file : listFiles) {
			for (Token token : file.getTokens()) {
				
				if (token.features.get(features.ofText.getFeature()) == 0)
					token.features.put(features.phraseLenght.getFeature(), new SintaticFeatures().calculatePhraseLenght(token));
				
				if (file.tagsOriginalFile.containsKey(token.getLemma()))
					token.setClassification("Yes");
				
				/*if (token.conceptWikipedia == null || token.conceptWikipedia.equals("")) {
					token.features.put(features.nodeDegree.getFeature(), 0.0D);
					token.features.put(features.inverseWikipediaLinkade.getFeature(), 0.0D);
					token.features.put(features.semanticSimilarity.getFeature(), 0.0D);
					token.features.put(features.wikipediaKeyphraseness.getFeature(), 0.0D);
				}*/
				
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
		if (percentNonText == 0) { // || percentNonText == -1) {
			for (FileData file : listFiles) {
				ArrayList<Token> tokensRemoved = new ArrayList<Token>();
				for (Token token : file.getTokens()) {
					if (token.features.get(features.ofText.getFeature()) == 0) //&& 
					   //(percentNonText != -1 || (percentNonText == -1 && !token.originalInferenceNet)))
						tokensRemoved.add(token);
				}
				file.getTokens().removeAll(tokensRemoved);
			}
		}
		
		//Atribui a qtde de termos que inferiram os nao-texto
		if (MainInfNet.modelFeatures == 5) {
			for (FileData file : listFiles) {
				for (Token token : file.getTokens()) {
					if (token.features.get(features.ofText.getFeature()) == 0) 
						token.features.put(features.inferenceNetOccurrence.getFeature(), (double)token.tokenWhoInfered.size());
					else
						token.features.put(features.inferenceNetOccurrence.getFeature(), 0.0);
				}
			}
		}
		
		System.out.println("Verificando keyphraseness...");
		Hashtable<String, Integer> tags = new Hashtable<String, Integer>();
		for (FileData file : listFiles) {
			for (String tagOrig : file.tagsOriginalFile.keySet()) {
				if (tags.containsKey(tagOrig))
					tags.put(tagOrig, (tags.get(tagOrig)+1));
				else
					tags.put(tagOrig, 1);
			}
		}
		
		for (FileData file : listFiles) {
			for (Token token : file.getTokens()) {
				double keyphraseness = 0.0;
				if (tags.containsKey(token.getLemma()))
					keyphraseness = (double)tags.get(token.getLemma());
				
				if (token.getClassification().equals("Yes") && keyphraseness > 0.0)
					keyphraseness -= 1; //retira a contagem no arquivo
					
				token.features.put(features.keyphraseness.getFeature(), keyphraseness);///listFiles.size());
			}
		}
		System.out.println("Finalizando verificação de keyphraseness...");
		
		//Set the feature ISR
		if (MainInfNet.modelFeatures == 2 || MainInfNet.modelFeatures == 4) {
			for (FileData file : listFiles) {
				for (Token token : file.getTokens()) {
					token.features.put(features.roleIsARelatedTo.getFeature(), Double.NaN);
				}
			}
				
			System.out.println("Calculando ISR...");
			//int f = 0;
			for (FileData file : listFiles) {
				//System.out.println(++f);
				for (Token token : file.getTokens()) {
					
					if (token.features.get(features.ofText.getFeature()) == 0) {
						Token maiorTfIdf = file.getTokenByLemma(token.tokenWhoInfered.keySet().iterator().next());
						for (String aux : token.tokenWhoInfered.keySet()) {
							Token tokenAux = file.getTokenByLemma(aux);
							if (tokenAux.features.get(features.tf_idf.getFeature()) > maiorTfIdf.features.get(features.tf_idf.getFeature()))
								maiorTfIdf = tokenAux;
						}
					
						if (token.features.get(features.semanticSimilarity.getFeature()) >= maiorTfIdf.features.get(features.semanticSimilarity.getFeature())) {
							token.features.put(features.roleIsARelatedTo.getFeature(), 1.0);
						}
						else
							token.features.put(features.roleIsARelatedTo.getFeature(), 0.0);
					} else {
						token.features.put(features.roleIsARelatedTo.getFeature(), Double.NaN);
					}
				}
			}
		}
		
		//Set the feature "Importance Semantic"
		/*if (MainInfNet.modelFeatures == 3 || MainInfNet.modelFeatures == 4) {
			System.out.println("Calculando Importance semantic...");
			int f = 0;
			for (FileData file : listFiles) {
				System.out.println(++f);
				for (Token token : file.getTokens()) {
					
					if (token.features.get(features.ofText.getFeature()) == 0) {
						Token maiorTfIdf = file.getTokenByLemma(token.tokenWhoInfered.keySet().iterator().next());
						for (String aux : token.tokenWhoInfered.keySet()) {
							Token tokenAux = file.getTokenByLemma(aux);
							if (tokenAux.features.get(features.tf_idf.getFeature()) > maiorTfIdf.features.get(features.tf_idf.getFeature()))
								maiorTfIdf = tokenAux;
						}
					
						double importanceSemantic = 0.0;
						if (token.tokenWhoInfered.get(maiorTfIdf).similaritySematic > 0 && 
							maiorTfIdf.features.get(features.semanticSimilarity.getFeature()) > 0) { 
							importanceSemantic = token.tokenWhoInfered.get(maiorTfIdf).similaritySematic / maiorTfIdf.features.get(features.semanticSimilarity.getFeature());
							
							if (importanceSemantic > 0)
								importanceSemantic = Math.log(importanceSemantic);
						}
							System.out.println(token.getLemma() + " -> " + importanceSemantic);
						token.features.put(features.importanceSemantic.getFeature(), importanceSemantic);
						
						
					} else {
						token.features.put(features.importanceSemantic.getFeature(), Double.NaN);
					}
				}
			}
		}*/
		
		//obtem os valores das features sintaticas dos termos inferidos
		/*for(FileData file : listFiles) {
			for (Token t : file.getTokens()) {
				if (t.features.get(features.ofText.getFeature()) == 0) {
					t.features.put(features.firstOccurrence.getFeature(), 0.0);
					t.features.put(features.tf_idf.getFeature(), 0.0);
					t.features.put(features.spread.getFeature(), 0.0);
				}
			}
		}*/
		
		//Filtro pela ISR (remove os termos nao texto cuja ISR = 0)
		/*int countTokenNT = 0, countTokenT = 0, countTagNT = 0, countTagT = 0;
		for (FileData file : listFiles) {
			ArrayList<Token> removed = new ArrayList<Token>();
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 0 &&
					token.features.get(features.roleIsARelatedTo.getFeature()) == 0) {
					removed.add(token);
				}
			}
			file.getTokens().removeAll(removed);
			
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 0) { //NT
					if (token.getClassification().equals("Yes")) //tag
						countTagNT++;
					else
						countTokenNT++;
				} else {
					if (token.getClassification().equals("Yes")) //tag
						countTagT++;
					else
						countTokenT++;
				}
			}
		}
		
		System.out.println("Após o filtro ISR...");
		System.out.println("Tags: " + (countTagNT + countTagT) + " (Text: " + countTagT + ", Non-text: " + countTagNT + ")");
		System.out.println("Tokens: " + (countTokenNT + countTokenT) + " (Text: " + countTokenT + ", Non-text: " + countTokenNT + ")");
		*/
		//Normaliza
		System.out.println("Normaling data");
		new Features().normalizeDataFeatures(listFiles);
		
		//Mostra feature importanceSemantic
		/*for (FileData file : listFiles) {
			for (Token token : file.getTags()) {
				if (token.features.get(features.ofText.getFeature()) == 0)
					System.out.println(token.getLemma() + " -> " + token.features.get(features.importanceSemantic.getFeature()));
			}
		}*/
		
		ArrayList<FileData> listFileNoBalance = (ArrayList<FileData>)listFiles.clone();
		
		if (percentNonText > 0)
			MainInfNet.reductionInferenceNet(listFiles, percentNonText);
		
		System.out.println("Classifing...");
		new RF().runRFCrossValidationDoc(listFiles, listFileNoBalance, arquivosGerados);
		//new RF_ConceptNet().runRFCrossValidationDoc(listFiles, listFileNoBalance);
		System.out.println("Finished!!!");
	}
}

class CalculateFeatures implements Runnable {

	private ArrayList<File> listFiles;
	private Boolean done;
	private SemanticSimilarity semantic;
	private SintaticFeatures sintatic;
	private Keyphraseness keyphraseness;
	ArrayList<FileData> listFilesText; 
	Hashtable<String, HashSet<String>> tokensFiles;
	String pathOutput;
	
	public CalculateFeatures(ArrayList<File> listFiles, SintaticFeatures sintatic, Keyphraseness keyphraseness,
							ArrayList<FileData> listFilesText, Hashtable<String, HashSet<String>> tokensFiles, String pathOutput) {
		this.listFiles = listFiles;
		this.sintatic = sintatic;
		this.keyphraseness = keyphraseness;
		this.listFilesText = listFilesText;
		this.tokensFiles = tokensFiles;
		this.pathOutput = pathOutput;
		
		this.done = false;
	}
	
	private FileData getFileData() {
		FileData file = null;
		if (!listFiles.isEmpty()) {
			Iterator<File> iterator = listFiles.iterator();
			File filePath = iterator.next();
			iterator.remove();
			file = new FormatFile().readFormatFileByFile(filePath.getPath());
		}
		return file;
	}
	
	@Override
	public void run() {
        synchronized (this.done) {
            this.done = false;
        }
                
        while (true) {
            FileData currentFile = null;

            synchronized (listFiles) {
                currentFile = getFileData();
            }
	
            if (currentFile != null) {
            	
            	CalculateFeatureToken.indexToken = 0;
            	
        		semantic = new SemanticSimilarity();
            	System.out.println("Calculing features " + currentFile.getName());
            	//semantic.getConceptsWikipediaTokens(currentFile);
            	semantic.getRelationsWikipediaLast(currentFile);
            	System.out.println("Finishing reltions Wikipedia!");
            	
            	/*try {
	            	int nThreads = Runtime.getRuntime().availableProcessors() - 1;
	            	ArrayList<Thread> currentJobs = new ArrayList<>();
			        ArrayList<CalculateFeatureToken> jobObjects = new ArrayList<>();
					for (int i = 0; i < nThreads; ++i) {
			            CalculateFeatureToken jobObject = new CalculateFeatureToken(sintatic, keyphraseness, semantic, listFilesText, tokensFiles, currentFile.getTokens(), currentFile);
			            Thread job = new Thread(jobObject);
			            currentJobs.add(job);
			            jobObjects.add(jobObject);
			
			        }
			
			        for (Thread job : currentJobs) {
			            job.start();
			        }
			
			        for (Thread job : currentJobs) {
			            job.join();
			        }
            	} catch (Exception e) { e.printStackTrace(); }*/
            	
            	int ti = 0;
    			for (Token token : currentFile.getTokens()) {
    				
    				if (ti % 100 == 0) System.out.println(ti + " de " + currentFile.getTokens().size());
    				ti++;
    				
    				if (!token.features.containsKey(features.ofText.getFeature()))
    					token.features.put(features.ofText.getFeature(), 0.0);
    				
    				if (token.features.get(features.ofText.getFeature()) == 1) {
    					token.features.put(features.firstOccurrence.getFeature(), sintatic.calculateFirstOccurrence(token, currentFile));
    					token.features.put(features.spread.getFeature(), sintatic.calculateSpread(token));
    					token.features.put(features.tf_idf.getFeature(), sintatic.calculateTFIDF(token, listFilesText, currentFile, tokensFiles));
    				} else {
    					token.features.put(features.firstOccurrence.getFeature(), 0.0);
    					token.features.put(features.spread.getFeature(), 0.0);
    					token.features.put(features.tf_idf.getFeature(), 0.0);	
    				}
    				
    				//token.features.put(features.phraseLenght.getFeature(), sintatic.calculatePhraseLenght(token));
    				token.features.put(features.keyphraseness.getFeature(), keyphraseness.calculateKeyphraseness(token, listFilesText));
    				//TODO: obter a mention do token na wikipedia antes de calcular wikipediaKeyphraseness
    				semantic.calculateWikipediaKeyphrasenessLast(token);
    				//TODO: obter o concept do token na wikipedia antes de calcular wikipediaKeyphraseness
    				semantic.calculatesInverseWikipediaLinkageLast(token);
    				//TODO: obter o concept do token na wikipedia antes de calcular wikipediaKeyphraseness
    				//TODO: obter o idcandidate - o id do concept dos tokens do texto 
    				semantic.calculateNodeDegreeLast(token);
    				//TODO: obter o context dos tokens do texto
    				semantic.calculateSimilaritySemanticLast2(token);
    				
    				//for (Token token2 : token.tokenWhoInfered.keySet())
    				//	semantic.calculateSimilaritySemantic(token, token2);
    			}
    			
    			new FormatFile().writeFormatFileByFile(currentFile, pathOutput + "/");
    			System.out.println("Salvo arquivo em: " + pathOutput + "/" + currentFile.getName());
            } else
            	break;
        }

        synchronized (this.done) {
            this.done = true;
        }
    }
}

class CalculateFeatureToken implements Runnable {

	static int indexToken = 0;
	
	private ArrayList<Token> listTokens;
	Iterator<Token> iterator = null;
	
	private SemanticSimilarity semantic;
	private SintaticFeatures sintatic;
	private Keyphraseness keyphraseness;
	ArrayList<FileData> listFilesText; 
	Hashtable<String, HashSet<String>> tokensFiles;
	FileData currentFile;
	Boolean done = false;
	
	public CalculateFeatureToken(SintaticFeatures sintatic, Keyphraseness keyphraseness, SemanticSimilarity semantic, ArrayList<FileData> listFilesText,
			Hashtable<String, HashSet<String>> tokensFiles, ArrayList<Token> listTokens, FileData currentFile) {
		this.sintatic = sintatic;
		this.keyphraseness = keyphraseness;
		this.semantic = semantic;
		this.listFilesText = listFilesText;
		this.tokensFiles = tokensFiles;
		this.listTokens = listTokens;
		this.currentFile = currentFile;
		iterator = listTokens.iterator();
	}
	
	private Token getToken() {
		Token token = null;
		if (iterator.hasNext()) {
			token = iterator.next();
			if (indexToken++ % 100 == 0)
				System.out.println(indexToken + " de " + currentFile.getTokens().size());
		}
		return token;
	}
	
	@Override
	public void run() {
        synchronized (this.done) {
            this.done = false;
        }
		
		while (true) {
            Token currentToken = null;

            synchronized (listTokens) {
            	currentToken = getToken();
            }
	
            if (currentToken != null) {
            	if (currentToken.features.get(features.ofText.getFeature()) == 1) {
            		currentToken.features.put(features.firstOccurrence.getFeature(), sintatic.calculateFirstOccurrence(currentToken, currentFile));
            		currentToken.features.put(features.spread.getFeature(), sintatic.calculateSpread(currentToken));
            		currentToken.features.put(features.tf_idf.getFeature(), sintatic.calculateTFIDF(currentToken, listFilesText, currentFile, tokensFiles));
				} else {
					currentToken.features.put(features.firstOccurrence.getFeature(), 0.0);
					currentToken.features.put(features.spread.getFeature(), 0.0);
					currentToken.features.put(features.tf_idf.getFeature(), 0.0);	
				}
				
				//token.features.put(features.phraseLenght.getFeature(), sintatic.calculatePhraseLenght(token));
            	currentToken.features.put(features.keyphraseness.getFeature(), keyphraseness.calculateKeyphraseness(currentToken, listFilesText));
				//TODO: obter a mention do token na wikipedia antes de calcular wikipediaKeyphraseness
				semantic.calculateWikipediaKeyphrasenessLast(currentToken);
				//TODO: obter o concept do token na wikipedia antes de calcular wikipediaKeyphraseness
				semantic.calculatesInverseWikipediaLinkageLast(currentToken);
				//TODO: obter o concept do token na wikipedia antes de calcular wikipediaKeyphraseness
				//TODO: obter o idcandidate - o id do concept dos tokens do texto 
				semantic.calculateNodeDegreeLast(currentToken);
				//TODO: obter o context dos tokens do texto
				semantic.calculateSimilaritySemanticLast2(currentToken);
				
				for (Token token2 : currentToken.tokenWhoInfered.keySet())
					semantic.calculateSimilaritySemantic(currentToken, token2);
            } else
            	break;
		}
		
        synchronized (this.done) {
            this.done = true;
        }
	}
	
}