package test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.StringTokenizer;

import maui.main.Examples;
import maui.stopwords.Stopwords;
import maui.stopwords.StopwordsEnglish;
import preprocessing.FormatFile;
import util.FileData;
import util.Token;
import classifier.RF;
import classifier.RFSony;
import config.Config;
import features.Features;
import features.SintaticFeatures;
import features.Features.features;

public class ResultSchutz {

	private static void removeStopWords(ArrayList<FileData> listFiles) {
		
		// Remove tokens stop words ou tamanho maior que 3 ou inicia ou termina com stop word
		Stopwords stopwords = new StopwordsEnglish();
		int i = 0;
		for (FileData file : listFiles) {
			System.out.println(++i);
			ArrayList<Token> tokenRemoved = new ArrayList<Token>();
			for (Token t : file.getTokens()) {
				// if (t.features.get(features.ofText.getFeature()) == 0) {// &&
				// t.getClassification().equals("No")) {
				if (t.getClassification().equals("No")) {
					StringTokenizer token = new StringTokenizer(t.getLemma(),
							" ");
					int size = token.countTokens();
					if (size > Config.MAX_LENGTH_NGRAM)
						tokenRemoved.add(t);
					else {
						String[] buffer = new String[size];
						int pos = 0;
						while (token.hasMoreTokens()) {
							buffer[pos] = token.nextToken();
							pos++;
						}
						if (stopwords.isStopword(buffer[0])
								|| stopwords.isStopword(buffer[pos - 1]))
							tokenRemoved.add(t);
					}
				}
			}
			//}
			file.getTokens().removeAll(tokenRemoved);
		}

		// Remove os termos nao do texto que nao sao inferidos por ninguem ou
		// que possuem as mesmas palavras (independente da ordem)
		for (FileData file : listFiles) {
			ArrayList<Token> removedTokens = new ArrayList<Token>();
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 0) {
					if (token.tokenWhoInfered.size() == 0)
						removedTokens.add(token);
				}

			}

			/*for (Token token : file.getTokens()) {
				ArrayList<Token> remove = new ArrayList<Token>();
				for (Token t : token.tokenInfered.keySet()) {
					if (removedTokens.contains(t)) {
						remove.add(t);
					}
				}

				for (Token t : remove)
					token.tokenInfered.remove(t);
			}*/

			file.getTokens().removeAll(removedTokens);
		}
	}
	
	private static void filterISR(ArrayList<FileData> listFiles) {
		
		for (FileData file : listFiles) {
			for (Token token : file.getTokens()) {
				token.features.put(features.roleIsARelatedTo.getFeature(),
						Double.NaN);
			}
		}

		int isATag = 0, isAToken = 0, nodeDegTag = 0, nodeDegToken = 0, wikipTag = 0, wikipTok = 0, KeyphTag = 0, keyphTok = 0;
		for (FileData file : listFiles) {
			for (Token token : file.getTokens()) {

				if (token.features.get(features.ofText.getFeature()) == 0) {
					Token maiorTfIdf = token.tokenWhoInfered.keySet().iterator().next();
					for (Token aux : token.tokenWhoInfered.keySet()) {
						if (aux.features.get(features.tf_idf.getFeature()) >= maiorTfIdf.features.get(features.tf_idf.getFeature()))
							maiorTfIdf = aux;
					}

					if (token.features.get(features.semanticSimilarity.getFeature()) >= maiorTfIdf.features.get(features.semanticSimilarity.getFeature())) {
						token.features.put(features.roleIsARelatedTo.getFeature(), 1.0);
						if (token.getClassification().equals("Yes")) {
							isATag++;
							if (token.features.get(features.keyphraseness.getFeature()) > 0)
								KeyphTag++;

						} else {
							isAToken++;
						}
					} else
						token.features.put(features.roleIsARelatedTo.getFeature(), 0.0);
				}
			}
		}
		
		for (FileData file : listFiles) {
			ArrayList<Token> removedTokens = new ArrayList<Token>();
			for (Token t : file.getTokens()) {
				if (t.features.get(features.ofText.getFeature()) == 0 && 
					t.features.get(features.roleIsARelatedTo.getFeature()) == 0) {
					removedTokens.add(t);
				}
			}
			file.getTokens().removeAll(removedTokens);
		}
		
		System.out.println("Tag com ISR = 1: " + isATag);
		System.out.println("Token com ISR = 0: " + isAToken);
	}

	public static int modelFeatures = 0;
	public static int modelBalance = 0;
	public static int interactionSmoteBoost = 5;
	public static int interactionRF = 0;
	public static int nearestNeighbors = 5;

	public static void main_(String[] args) {
		String pathData = "data/schutz/schutz_features_I_unifor/";
		ArrayList<FileData> listFiles = new FormatFile().readFormatFiles(pathData);
		for (FileData file : listFiles) {
			for (Token t : file.getTokens()) {
				
				System.out.println("Lemma: " + t.getLemma() + " M: " + t.mentionWikipedia + " T: " + t.conceptWikipedia);
				
				if (t.features.get(features.ofText.getFeature()) == 1 &&
					(t.conceptWikipedia != null && !t.conceptWikipedia.equals("") &&
					 (t.features.get(features.nodeDegree.getFeature()) == null ||
					 t.features.get(features.semanticSimilarity.getFeature()) == null ||
					 t.features.get(features.wikipediaKeyphraseness.getFeature()) == null ||
					 t.features.get(features.inverseWikipediaLinkade.getFeature()) == null)) &&
					(t.features.get(features.ofText.getFeature()) == null ||
					t.features.get(features.tf_idf.getFeature()) == null ||
					t.features.get(features.phraseLenght.getFeature()) == null ||
					t.features.get(features.firstOccurrence.getFeature()) == null ||
					t.features.get(features.spread.getFeature()) == null ||
					t.features.get(features.keyphraseness.getFeature()) == null
					)) {
						
					System.out.println("Oftext: " + t.features.get(features.ofText.getFeature()));
					System.out.println("TFxIDF: " + t.features.get(features.tf_idf.getFeature()));
					System.out.println("PhraseLenght: " + t.features.get(features.phraseLenght.getFeature()));
					System.out.println("FirstOccurrence: " + t.features.get(features.firstOccurrence.getFeature()));
					System.out.println("Spread: " + t.features.get(features.spread.getFeature()));
					System.out.println("Keyphraseness: " + t.features.get(features.keyphraseness.getFeature()));
					System.out.println("NodeDegree: " + t.features.get(features.nodeDegree.getFeature()));
					System.out.println("SemanticSimilarity: " + t.features.get(features.semanticSimilarity.getFeature()));
					System.out.println("WikipediaKeyphraseness: " + t.features.get(features.wikipediaKeyphraseness.getFeature()));
					System.out.println("InverseLinkage: " + t.features.get(features.inverseWikipediaLinkade.getFeature()));
				}
			}
		}
	}
	
	public static void main(String[] args) {

		try {
			//String pathData = "data/schutz/conceptNet_features_I/";
			//String pathData = "data/teseUsp/teste/";
			//String pathData = "data/teseUsp/features_inferenceNet_v2_teste/";
			//String pathData = "data/estadao/sports/inferenceNet/";
			//String pathData = "data/hulth/wordNet_features/";
			//String pathData = "data/hulth/lsa_takelab_50_features1/";
			String pathData = "data/citeULike/citeULike_wordNet_features/";
			modelFeatures = 2; // 0: KEA; 1: MAUI; 2: MAUI + ISR
			int percentNonText = -1; // 0: Without nonText; -1: Non manipulated base; // 1: 100% manipulated base; XX: Percentage of manipulation
			modelBalance = 2; // 0: Without balance; 1: SMOTE; 2: Over-sampling majority class; 3: SmoteAndEnn; 4: SmoteAndTomekLinks; 5: CNN+TomekLinks; 6: TomekLink; 7:CNN; 8: SmoteBoost; 9: RUSBoost 
			String knowledgeBase = "C"; // I: InferenceNet; C: ConceptNet
			int percentageOverSampling = 300;
			boolean arquivosGerados = false;
			MainInfNet.outPutExperiments = "data/estadao/sports/output_s_" + modelFeatures + "_" + percentNonText + "_" + modelBalance + ".txt";
			//MainInfNet.outPutExperiments = "data/schutz/output_RUSBoost.txt";
			//MainInfNet.outPutExperiments = "data/teseUsp/v2/output_" + modelFeatures + "_" + percentNonText + "_" + modelBalance + ".txt";
			interactionRF = 0;
			interactionSmoteBoost = 8;
	
			if (args.length > 0) {
				pathData = args[0];
				modelFeatures = Integer.parseInt(args[1]);
				percentNonText = Integer.parseInt(args[2]);
				modelBalance = Integer.parseInt(args[3]);
				knowledgeBase = args[4];
				percentageOverSampling = Integer.parseInt(args[5]);
				MainInfNet.outPutExperiments = args[6];
				interactionSmoteBoost = Integer.parseInt(args[7]);
				interactionRF = Integer.parseInt(args[8]);
				nearestNeighbors = Integer.parseInt(args[9]); 
				arquivosGerados = Boolean.parseBoolean(args[10]);
			}
			
			MainInfNet.modelFeatures = modelFeatures;
			MainInfNet.modelBalance = modelBalance;
			MainInfNet.percentageOverSampling = percentageOverSampling;
			
			if (!arquivosGerados) { 
				
				/*int i = 0;
				for (File f : new File(pathData).listFiles()) {
					System.out.println(++i);
					if (f.getName().contains("778023")) {
						FileData file = new FormatFile().readFormatFileByFile(f.getAbsolutePath());
						for (Token t : file.getTags()) {
							System.out.println(t);
							//if (t.features.get(features.ofText.getFeature()) == 1.0)
							//	System.out.println(t);
								//System.out.println(t.tokenWhoInfered);
							/*if (t.getLemma().equals("geologia")) {
								System.out.print(t.getLemma() + " -> {");
								for (String s : t.tokenWhoInfered.keySet()) {
									System.out.print(s + " " + t.tokenWhoInfered.get(s).relation + " ");
								}
								System.out.println("}");
							}
						}
					}
					if (f.getName().contains("Stevaux  José Candido - O Rio Paraná: geomorfogênese  sedimentação e evolução qu..") ||
						f.getName().contains("Yamaguishi  Sergio Hideo - Gestão da inovação na indústria farmacêutica no Brasi..")) { 
						FileData file = new FormatFile().readFormatFileByFile(f.getAbsolutePath());
						System.out.println(++i);
						for (Token t : file.getTags()) {
							System.out.println(t);
							/*if (t.features.get(features.ofText.getFeature()) == 0 && t.getClassification().equals("Yes") &&
								t.features.containsKey(features.nodeDegree.getFeature()) && t.features.get(features.nodeDegree.getFeature()) > 0.14 &&
							    (t.getLemma().equals("geologia") || //&& t.features.get(features.nodeDegree.getFeature()).equals(0.14872575123621148)) || 
								t.getLemma().equals("brasil")))// && t.features.get(features.nodeDegree.getFeature()).equals(0.23872875092387288))))
								System.out.println(t + " -> " + file.getName());*/
						/*}
					}
				}*/
				
				//System.out.println("Terminou!");
				
				ArrayList<FileData> listFilesInf = new FormatFile().readFormatFiles(pathData);
				Collections.sort(listFilesInf);
				
				/*for (FileData file : listFilesInf) {
					if (file.getName().equals("Universidad do Chile pegará São Paulo na Sul-Americana")) {
						for (Token t : file.getTokens()) {
							if (t.getLemma().equals("paulo miranda") || t.getLemma().equals("são paulo fc") ||
								t.getLemma().equals("futebol")) {
								System.out.print(t.getLemma() + " -> {");
								for (String s : t.tokenWhoInfered.keySet()) {
									System.out.print(s + " " + t.tokenWhoInfered.get(s).relation + " ");
								}
								System.out.println("}");
							}
						}
					}
				}*/
				
				/*int countTag = 0, countToken = 0;
				for (FileData file : listFilesInf) {
					for (Token t : file.getTokens()) {
						if (t.features.get(features.phraseLenght.getFeature()) > 3) {
							System.out.println(" Lemma: " + t.getLemma() + 
											   " OfText: " + t.features.get(features.ofText.getFeature()) +
											   " Classification: " + t.getClassification());
							
							if (t.getClassification().equals("Yes"))
								countTag++;
							else
								countToken++;
						}
					}
				}
				System.out.println("Maior que 3: ");
				System.out.println("Tags: " + countTag);
				System.out.println("Tokens: " + countToken);*/
				
				//remove the terms with lenght more than 5
				for (FileData file : listFilesInf) {
					ArrayList<Token> removed = new ArrayList<Token>();
					for (Token t : file.getTokens()) {
						//if (t.features.get(features.ofText.getFeature()) == 0)
						//	System.out.println(t);
						if (!t.features.containsKey(features.phraseLenght.getFeature()))
							t.features.put(features.phraseLenght.getFeature(), new SintaticFeatures().calculatePhraseLenght(t));
						
						if (t.features.get(features.phraseLenght.getFeature()) > 5) 
							removed.add(t);
						if (t.features.get(features.ofText.getFeature()) == 0 && t.tokenWhoInfered.size() == 0)
							removed.add(t);
					}
					file.getTokens().removeAll(removed);
				}
				
				// remove the terms not of text
				if (percentNonText == 0 || percentNonText == -1) {
					for (FileData file : listFilesInf) {
						ArrayList<Token> tokensRemoved = new ArrayList<Token>();
						for (Token token : file.getTokens()) {
							if (token.features.get(features.ofText.getFeature()) == 0 && 
							   (percentNonText != -1))// || (percentNonText == -1 && !token.originalInferenceNet)))
								tokensRemoved.add(token);
						}
						file.getTokens().removeAll(tokensRemoved);
					}
				}
				
				int tagT = 0, tagNT = 0, tokenT = 0, tokenNT = 0, tagsOriginais = 0;;
				for (FileData file : listFilesInf) {
					tagsOriginais += file.tagsOriginalFile.size();
					for (Token t : file.getTokens()) {
						
						if (t.getEndIndex() < t.getBeginIndex()) {
							t.setEndIndex(t.getBeginIndex());
							t.features.put(features.spread.getFeature(), (double) (t.getEndIndex() - t.getBeginIndex()));
						}
						
						if (t.getClassification().equals("Yes")) {
							if (t.features.get(features.ofText.getFeature()) == 0) {
								//if (t.originalInferenceNet)
									tagNT++;
							} else
								tagT++;
						} else {
							if (t.features.get(features.ofText.getFeature()) == 0) {
								//if (t.originalInferenceNet)
									tokenNT++;
							} else
								tokenT++;
						}
					}
				}
				System.out.println("Tags originais: " + tagsOriginais);
				System.out.println("Tags: " + (tagT+tagNT) + " (T: " + tagT + " NT: " + tagNT + ")");
				System.out.println("Tokens: " + (tokenT+tokenNT) + " (T: " + tokenT + " NT: " + tokenNT + ")");
				
			System.out.println("Terminou de ler");
			
			//Atribui valor da features da Wikipedia para os termos nao presentes na Wikipedia
			for (FileData file : listFilesInf) {
				for (Token t : file.getTokens()) {
					if (!t.features.containsKey(features.semanticSimilarity.getFeature()))
						t.features.put(features.semanticSimilarity.getFeature(), 0.0);
					if (!t.features.containsKey(features.nodeDegree.getFeature()))
						t.features.put(features.nodeDegree.getFeature(), 0.0);
					if (!t.features.containsKey(features.inverseWikipediaLinkade.getFeature()))
						t.features.put(features.inverseWikipediaLinkade.getFeature(), 0.0);
					if (!t.features.containsKey(features.wikipediaKeyphraseness.getFeature()))
						t.features.put(features.wikipediaKeyphraseness.getFeature(), 0.0);
				}
			}
			
			//ajusta a similaridade semantica
			/*for (FileData file : listFilesInf) {
				int conceitosWikipedia = 0;
				for (Token t : file.getTokens()) {
					if (t.features.get(features.ofText.getFeature()) == 1 && 
						t.conceptWikipedia != null && !t.conceptWikipedia.equals(""))
						conceitosWikipedia++;
				}
				
				for (Token t : file.getTokens()) 
					if (t.features.containsKey(features.semanticSimilarity.getFeature()))
						t.features.put(features.semanticSimilarity.getFeature(), 
									   (double) t.features.get(features.semanticSimilarity.getFeature())/conceitosWikipedia);
			}*/
			
			/*for(FileData file : listFilesInf) {
				for (Token t : file.getTokens()) {
					if (t.conceptWikipedia == null || t.conceptWikipedia.equals("")) {
						t.features.put(features.semanticSimilarity.getFeature(), 0.0);
						t.features.put(features.nodeDegree.getFeature(), 0.0);
						t.features.put(features.inverseWikipediaLinkade.getFeature(), 0.0);
						t.features.put(features.wikipediaKeyphraseness.getFeature(), 0.0);
					}
				}
			}*/	
			
			//filterISR(listFilesInf);
			//removeStopWords(listFilesInf);
	
			// Set original to all non text tokens
			//if (args.length > 0) {
				/*for (FileData file : listFilesInf) {
					for (Token token : file.getTokens()) {
						if (token.features.get(features.ofText.getFeature()) == 0.0)
							token.originalInferenceNet = true;
					}
				}*/
			//}
	
			System.out.println("1");
			
			// Calcula a ISR
			if (modelFeatures == 2) {
				if (knowledgeBase.equals("I"))
					checksRoles(listFilesInf);
	
				/*for (FileData file : listFilesInf) {
					for (Token token : file.getTokens()) {
						token.features.put(features.roleIsARelatedTo.getFeature(),
								Double.NaN);
					}
				}*/
	
				int isATag = 0, isAToken = 0, nodeDegTag = 0, nodeDegToken = 0, wikipTag = 0, wikipTok = 0, KeyphTag = 0, keyphTok = 0;
				for (FileData file : listFilesInf) {
					for (Token token : file.getTokens()) {
	
						if (token.features.get(features.ofText.getFeature()) == 0 && 
							token.tokenWhoInfered.size() > 0) {
							Token maiorTfIdf = findToken(token.tokenWhoInfered.keys().nextElement(), file.getTokens());
							for (String auxLema : token.tokenWhoInfered.keySet()) {
								Token aux = findToken(auxLema, file.getTokens());
								//System.out.println("aux: " + aux);
								//System.out.println("aux.features: " + aux.features);
								//System.out.println("aux.features.get(features.tf_idf.getFeature()): " + aux.features.get(features.tf_idf.getFeature()));
								//System.out.println("maiorTfIdf: " + maiorTfIdf);
								//System.out.println("maiorTfIdf.features: " + maiorTfIdf.features);
								//System.out.println("maiorTfIdf.features.get(features.tf_idf.getFeature()): " + maiorTfIdf.features.get(features.tf_idf.getFeature()));
								
								//if (aux == null || aux.features == null || aux.features.get(features.tf_idf.getFeature()) == null ||
								//	aux == null || aux.features == null || aux.features.get(features.tf_idf.getFeature()) == null)
								//	System.out.println("Arquivo com erro: " + file.getName());
								try {
									if (aux.features.get(features.tf_idf.getFeature()) >= maiorTfIdf.features.get(features.tf_idf.getFeature()))
										maiorTfIdf = aux;
								} catch (Exception e)
								{
									System.out.println("File: " + file.getName());
									System.out.println("aux: " + aux);
									System.out.println("aux.features: " + aux.features);
									System.out.println("aux.features.get(features.tf_idf.getFeature()): " + aux.features.get(features.tf_idf.getFeature()));
									System.out.println("maiorTfIdf: " + maiorTfIdf);
									System.out.println("maiorTfIdf.features: " + maiorTfIdf.features);
									System.out.println("maiorTfIdf.features.get(features.tf_idf.getFeature()): " + maiorTfIdf.features.get(features.tf_idf.getFeature()));
									
									e.printStackTrace();
								}
							}
	
							if (token.features.get(features.semanticSimilarity.getFeature()) >= maiorTfIdf.features.get(features.semanticSimilarity.getFeature())) {
								token.features.put(features.roleIsARelatedTo.getFeature(), 1.0);
								if (token.getClassification().equals("Yes")) {
									isATag++;
									if (token.features.get(features.keyphraseness.getFeature()) > 0)
										KeyphTag++;
	
									// System.out.println(maiorTfIdf.getLemma() + " -> " + token.tokenWhoInfered.get(maiorTfIdf).get(0)
									// + " -> " + token.getLemma() + "(" + token.originalInferenceNet + ")");
	
								} else {
									isAToken++;
								}
							} else
								token.features.put(features.roleIsARelatedTo.getFeature(), 0.0);
						} else
							token.features.put(features.roleIsARelatedTo.getFeature(), Double.NaN);
	
						// for (Token aux: token.tokenWhoInfered.keySet()) {
						/*
						 * for (String relacao : token.tokenWhoInfered.get(aux)) {
						 * Token maiorTfIdf =
						 * t.tokenWhoInfered.keySet().iterator().next(); for (Token
						 * aux : t.tokenWhoInfered.keySet()) { if
						 * (aux.features.get(features.tf_idf.getFeature()) >
						 * maiorTfIdf.features.get(features.tf_idf.getFeature()))
						 * maiorTfIdf = aux; }
						 * 
						 * /*if
						 * (relacao.toLowerCase().equals("capableofreceivingaction"
						 * )) { if (token.getClassification().equals("Yes")) {
						 * isATag++; } else { isAToken++; } }
						 */
						// }
						// }
					}
				}
			}
			System.out.println("2");
			/*
			 * System.out.println("IsA tag: " + isATag);
			 * System.out.println("IsA token: " + isAToken);
			 * 
			 * System.out.println("Keyp tag: " + KeyphTag);
			 * System.out.println("Keyp token: " + keyphTok);
			 * 
			 * System.out.println("Keyp tag: " + nodeDegTag);
			 * System.out.println("Keyp token: " + nodeDegToken);
			 * 
			 * System.out.println("Keyp tag: " + wikipTag);
			 * System.out.println("Keyp token: " + wikipTok);
			 */
	
			//removeStopWords(listFilesInf);
			System.out.println("3");
			// new FormatFile().writeFormatFile(listFiles2,
			// "data/newTest/old/correct_features6InfOrig/");
			/*
			 * int countDif = 0; for (FileData file2 : listFiles2) { for (FileData
			 * fileAux : listFileAux) { if
			 * (file2.getName().equals(fileAux.getName())) { for (Token t :
			 * file2.getTokens()) { for (Token tAux : fileAux.getTokens()) { if
			 * (t.getLemma().equals(tAux.getLemma())) {
			 * 
			 * for (String f : t.features.keySet()) { if
			 * (!t.features.get(f).equals(tAux.features.get(f))) {
			 * System.out.println("Diferente: " + f + "( " + t.features.get(f) +
			 * "; " + tAux.features.get(f) + ")"); countDif++; } }
			 * 
			 * } } } } } } System.out.println("Qtde de diferentes: " + countDif);
			 */
	
			// new FormatFile().writeFormatFile(listFiles2,
			// "data/newTest/old/correct_features6Inf/");
	
			// ArrayList<FileData> listFilesInf = new
			// FormatFile().readFormatFiles("data/newTest/old/infOriginal/");
	
			// removeStopWords(listFiles2);
	
			/*
			 * int tokensNaoText = 0, tokensNaoTextOrig = 0, tagsNaoText = 0,
			 * tagsNaoTextOrig = 0; for (FileData file : listFiles2) { for (Token t
			 * : file.getTokens()) { if
			 * (t.features.get(features.ofText.getFeature()) == 0) { if
			 * (t.getClassification().equals("Yes")) { tagsNaoText++; if
			 * (t.originalInferenceNet) tagsNaoTextOrig++; } else { tokensNaoText++;
			 * if (t.originalInferenceNet) tokensNaoTextOrig++; } } } //new
			 * FormatFile().writeFormatFileByFile(file,
			 * "data/newTest/old/correct_features6Inf/"); }
			 * System.out.println("Tokens nao texto: " + tokensNaoText);
			 * System.out.println("Tokens nao texto original: " +
			 * tokensNaoTextOrig); System.out.println("Tags nao texto: " +
			 * tagsNaoText); System.out.println("Tags nao texto original: " +
			 * tagsNaoTextOrig);
			 */
	
			// Analise das features dos inferidos com quem inferiu
			/*
			 * int tag = 0, naoTag = 0, maiorTfIdfTag = 0, maiorTfIdfNaoTag = 0; int
			 * qtdeTagMais1Inf = 0, qtdeTokenMais1Inf = 0; int tagIgualMais1 = 0,
			 * naoTagIgualMais1 = 0; for (FileData file : listFiles2) { for (Token
			 * token : file.getTokens()) { if (token.tokenWhoInfered.size() > 1) {
			 * if (token.getClassification().equals("Yes")) { qtdeTagMais1Inf++; }
			 * else qtdeTokenMais1Inf++; }
			 * 
			 * if (token.features.get(features.ofText.getFeature()) == 0) { boolean
			 * equalClassif = false; Token equalToken = null; for (Token whoInf :
			 * token.tokenWhoInfered.keySet()) { if
			 * (whoInf.getClassification().equals(token.getClassification())) {
			 * equalClassif = true; equalToken = whoInf; } }
			 * 
			 * if (equalClassif) { if (token.getClassification().equals("Yes")) {
			 * tag++; if (token.tokenWhoInfered.size() > 1) tagIgualMais1++; } else
			 * { naoTag++; if (token.tokenWhoInfered.size() > 1) naoTagIgualMais1++;
			 * }
			 * 
			 * Token maiorTf = equalToken; for (Token whoInf :
			 * token.tokenWhoInfered.keySet()) { if
			 * (whoInf.features.get(features.semanticSimilarity.getFeature()) >
			 * maiorTf.features.get(features.semanticSimilarity.getFeature()))
			 * maiorTf = whoInf; } if
			 * (maiorTf.getClassification().equals(token.getClassification())) { if
			 * (token.getClassification().equals("Yes")) maiorTfIdfTag++; else
			 * maiorTfIdfNaoTag++; } else { if
			 * (token.getClassification().equals("Yes"))
			 * System.out.println(maiorTf.getClassification() + " -> " +
			 * maiorTf.features.get(features.keyphraseness.getFeature())); }
			 * 
			 * } } } }
			 * 
			 * System.out.println("Igual classif tag: " + tag);
			 * System.out.println("Igual classif nao tag: " + naoTag);
			 */
	
			// System.out.println("Tag mais 1 inf: " + qtdeTagMais1Inf);
			// System.out.println("Nao tag mais 1 inf: " + qtdeTokenMais1Inf);
	
			// System.out.println("An�lise feature tag: " + maiorTfIdfTag);
			// System.out.println("An�lise feature nao tag: " + maiorTfIdfNaoTag);
	
			// System.out.println("Tag igual mais 1: " + tagIgualMais1);
			// System.out.println("Nao tag igual mais 1: " + naoTagIgualMais1);
	
			System.out.println("4");
			// obtem os valores das features sintaticas dos termos inferidos
			for (FileData file : listFilesInf) {
				for (Token t : file.getTokens()) {
					if (t.features.get(features.ofText.getFeature()) == 0) {
						//double firstOcc = 0, tf_idf = 0, spread = 0;
						//Token maiorTfIdf = t.tokenWhoInfered.keySet().iterator().next();
						//Token maiorFirsOc = maiorTfIdf;
						// Token maiorSpread = maiorTfIdf;
	
						//for (Token aux : t.tokenWhoInfered.keySet()) {
	
						//	if (aux.features.get(features.tf_idf.getFeature()) >= maiorTfIdf.features.get(features.tf_idf.getFeature()))
						//		maiorTfIdf = aux;
	
						// if
						// (aux.features.get(features.firstOccurrence.getFeature())
						// <
						// maiorFirsOc.features.get(features.firstOccurrence.getFeature()))
						// maiorFirsOc = aux;
	
						// if (aux.features.get(features.spread.getFeature()) <
						// maiorSpread.features.get(features.spread.getFeature()))
						// maiorSpread = aux;
	
						// firstOcc +=
						// aux.features.get(features.firstOccurrence.getFeature());
						// tf_idf += aux.features.get(features.tf_idf.getFeature());
						// spread += aux.features.get(features.spread.getFeature());
	
						// atribui o valor das features sintaticas do ultimo termo
						// que inferiu
						// t.features.put(features.firstOccurrence.getFeature(),
						// aux.features.get(features.firstOccurrence.getFeature()));
						// t.features.put(features.tf_idf.getFeature(),
						// aux.features.get(features.tf_idf.getFeature()));
						// t.features.put(features.spread.getFeature(),
						// aux.features.get(features.spread.getFeature()));
						// break;
						// t.features.put(features.tf_idf.getFeature(), 1.0);
						//}
						// t.features.put(features.firstOccurrence.getFeature(),
						// Instance.missingValue());
						// t.features.put(features.spread.getFeature(),
						// Instance.missingValue());
	
						// Maior TFxIDF
						//t.features.put(features.firstOccurrence.getFeature(), maiorTfIdf.features.get(features.firstOccurrence.getFeature()));
						//t.features.put(features.tf_idf.getFeature(), maiorTfIdf.features.get(features.tf_idf.getFeature()));
						//t.features.put(features.spread.getFeature(), maiorTfIdf.features.get(features.spread.getFeature()));
	
						// Media
						// t.features.put(features.firstOccurrence.getFeature(),
						// firstOcc/t.tokenWhoInfered.size());
						// t.features.put(features.tf_idf.getFeature(),
						// tf_idf/t.tokenWhoInfered.size());
						// t.features.put(features.spread.getFeature(),
						// spread/t.tokenWhoInfered.size());
	
						// Zero
						t.features.put(features.firstOccurrence.getFeature(), 0.0);
						t.features.put(features.tf_idf.getFeature(), 0.0);
						t.features.put(features.spread.getFeature(), 0.0);
						
	
						// Sem valor
						 //t.features.put(features.firstOccurrence.getFeature(), Double.NaN);
						 //t.features.put(features.tf_idf.getFeature(), Double.NaN);
						 //t.features.put(features.spread.getFeature(), Double.NaN);
					}
				}
			}
	
			System.out.println("5");
	
			// Adiciona os termos da inferenceNet
			// System.out.println("Adicionando os termos da InferenceNet...");
			// new AddInferenceNet(listFiles2).checkTagNoText();*/
	
			// Adds the missing value to terms that non exists in the Wikipedia
			/*
			 * for (FileData file : listFilesInf) { for (Token token :
			 * file.getTokens()) { if (token.conceptWikipedia.equals("")) {
			 * token.features.put(features.semanticSimilarity.getFeature(),
			 * Instance.missingValue());
			 * token.features.put(features.inverseWikipediaLinkade.getFeature(),
			 * Instance.missingValue());
			 * token.features.put(features.nodeDegree.getFeature(),
			 * Instance.missingValue());
			 * token.features.put(features.wikipediaKeyphraseness.getFeature(),
			 * Instance.missingValue()); } } }
			 */
	
			// Adiciona a inferência
			/*
			 * for (FileData file : listFiles2) {
			 * 
			 * //Obtem maior frequencia de inferidos em cada arquivo int
			 * maxFrequency = 0; for (Token t : file.getTokens()) { if
			 * (t.features.get(features.ofText.getFeature()) == 0) { if
			 * (t.tokenWhoInfered.size() > maxFrequency) maxFrequency =
			 * t.tokenWhoInfered.size(); } }
			 * 
			 * for (Token t : file.getTokens()) { if
			 * (t.features.get(features.ofText.getFeature()) == 0) { int tf =
			 * t.tokenWhoInfered.size() / maxFrequency; int df = 0; for (FileData
			 * fileAux : listFiles2) { if
			 * (!fileAux.getName().equals(file.getName())) { for (Token tAux :
			 * file.getTokens()) { if (tAux.getLemma().equals(t.getLemma())) { df++;
			 * break; } } } } double idf = Math.log((double) listFiles2.size() /
			 * df); double tfxidf = (double) tf * idf;
			 * //t.features.put(features.tf_idf.getFeature(),
			 * t.features.get(features.tf_idf.getFeature()) * tfxidf);
			 * //t.features.put(features.firstOccurrence.getFeature(),
			 * t.features.get(features.firstOccurrence.getFeature()) * tfxidf);
			 * //t.features.put(features.spread.getFeature(),
			 * t.features.get(features.spread.getFeature()) * tfxidf);
			 * //System.out.print("Token: " + t.getLemma() + " TFxIDF (antigo): " +
			 * t.features.get(features.tf_idf.getFeature()) +
			 * "TFxIDF (inferência): " + tfxidf);
			 * t.features.put(features.tf_idf_inf.getFeature(), (double)idf);
			 * //t.features.put(features.tf_idf.getFeature(),
			 * t.features.get(features.tf_idf.getFeature()) * tfxidf);
			 * //System.out.println(" TFxIDF (novo): " +
			 * t.features.get(features.tf_idf.getFeature())); } //else { //
			 * t.features.put(features.tf_idf_inf.getFeature(),
			 * Instance.missingValue()); //} } }
			 */
	
			// System.out.println("Term predictiability...");
			// TermPredictiability term = new TermPredictiability(listFiles2);
			// term.calculatePredictiability();
	
			System.out.println("6");
			// Normaliza
			System.out.println("Normaling data");
			new Features().normalizeDataFeatures(listFilesInf);
			
			
			/*int i = 0;
			for (FileData file : listFilesInf) {
				System.out.println(++i);
				for (Token t : file.getTokens()) {
					if (t.features.get(features.ofText.getFeature()) == 0 && t.getClassification().equals("Yes") &&
						t.features.containsKey(features.nodeDegree.getFeature()) && t.features.get(features.nodeDegree.getFeature()) > 0.14 &&
					    (t.getLemma().equals("geologia") || //&& t.features.get(features.nodeDegree.getFeature()).equals(0.14872575123621148)) || 
						t.getLemma().equals("brasil")))// && t.features.get(features.nodeDegree.getFeature()).equals(0.23872875092387288))))
						System.out.println(t + " -> " + file.getName());
				}
			}*/
			
	
			// checksRoles(listFilesInf);
	
			System.out.println("7");
			if (percentNonText > 0 && percentNonText < 1)
				reductionInferenceNet(listFilesInf, percentNonText);
			
			/*
			 * System.out.println("Writing arff..."); try { new
			 * WriteArff().createFolders("citeULike"); new
			 * WriteArff().writeFiles(listFilesInf, "citeULike"); } catch (Exception
			 * e) { e.printStackTrace(); }
			 */
	
			// TermPredictiability term = new TermPredictiability(listFiles2);
			// term.calculatePredictiability();
	
			System.out.println("8");
			ArrayList<FileData> listFileNoBalance = (ArrayList<FileData>) listFilesInf.clone();
			// System.out.println("Term predictiabily test...");
			// term.calculatePrectiabilityTest(listFileNoBalance);
	
			// System.out.println("Tamanho antes do balanceamento: " +
			// listFilesInf.size());
			// listFilesInf = new Balance().realizeBalance(listFilesInf);
			// System.out.println("Tamanho depois do balanceamento: " +
			// listFilesInf.size());
			
			System.out.println("9");
			System.out.println("Classifing...");
			//new RFSony().runRFCrossValidationDoc(listFilesInf, listFileNoBalance);
			new RF().runRFCrossValidationDoc(listFilesInf, listFileNoBalance, arquivosGerados);
			System.out.println("Finished!!!");
			} else {
				System.out.println("Classifing...");
				//new RFSony().runRFCrossValidationDoc(listFilesInf, listFileNoBalance);
				new RF().runRFCrossValidationDoc(null, null, arquivosGerados);
				System.out.println("Finished!!!");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static Token findToken(String lema, ArrayList<Token> listTokens) {
		for (Token t : listTokens) {
			if (t.features.get(features.ofText.getFeature()) == 1 && t.getLemma().equals(lema))
				return t;
		}return null;
	}
	
	public static void reductionInferenceNet(ArrayList<FileData> listFile, int percent) {

		int countTags = 0, countNonTags = 0;
		int countTagsOrig = 0, countNonTagsOrig = 0;
		for (FileData file : listFile) {
			for (Token token : file.getTokens()) {
				if (token.features.containsKey(features.ofText.getFeature())
						&& token.features.get(features.ofText.getFeature()) == 0.0D) {
					if (token.getClassification().equals("Yes")) {
						countTags++;
						//if (token.originalInferenceNet)
						//	countTagsOrig++;
					} else {
						countNonTags++;
						//if (token.originalInferenceNet)
						//	countNonTagsOrig++;
					}
				}
			}
		}

		System.out.println("Tags total: " + countTags);
		System.out.println("Tokens total: " + countNonTags);

		System.out.println("Tags total (orig): " + countTagsOrig);
		System.out.println("Tokens total (orig): " + countNonTagsOrig);

		int qttyTagsToRemove = (int) (((double) (100 - percent) / 100) * countTags);
		int qttyTokensToRemove = (int) (countNonTags - (countNonTagsOrig + ((countNonTags - countNonTagsOrig)
				* (countTags - qttyTagsToRemove - countTagsOrig) / (countTags - countTagsOrig))));
		// int qttyTags = countTags - (countTags * percent / 100);
		// int qttyNonTags = countNonTags - (countNonTags * percent / 100);
		System.out.println("Remover: " + qttyTagsToRemove + " tags e "
				+ qttyTokensToRemove + " não tags");
		int qttyRemovedTags = 0, qttyRemovedTokens = 0;

		for (FileData file : listFile) {
			if (qttyRemovedTags >= qttyTagsToRemove
					&& qttyRemovedTokens >= qttyTokensToRemove)
				break;
			ArrayList<Token> tokensRemoved = new ArrayList<Token>();
			for (Token token : file.getTokens()) {
				if (token.features.containsKey(features.ofText.getFeature())
						&& token.features.get(features.ofText.getFeature()) == 0.0D) {
						//&& token.originalInferenceNet == false) {
					if (token.getClassification().equals("Yes")) {
						if (qttyRemovedTags < qttyTagsToRemove) {
							tokensRemoved.add(token);
							qttyRemovedTags++;
						}
					} else {
						if (qttyRemovedTokens < qttyTokensToRemove) {
							tokensRemoved.add(token);
							qttyRemovedTokens++;
						}
					}
					if (qttyRemovedTags >= qttyTagsToRemove
							&& qttyRemovedTokens >= qttyTokensToRemove)
						break;
				}
			}
			file.getTokens().removeAll(tokensRemoved);
			if (qttyRemovedTags >= qttyTagsToRemove
					&& qttyRemovedTokens >= qttyTokensToRemove)
				break;
		}
	}

	public static void checksRoles(ArrayList<FileData> listFiles2) {
		
		Hashtable<String, Integer> rolesNonTag = new Hashtable<String, Integer>();
		Hashtable<String, Integer> rolesTag = new Hashtable<String, Integer>();
		int countTagRelatedToIsA = 0, countTagCapableOfReceivingAction = 0;
		int countNonTagRelatedToIsA = 0, countNonTagCapableOfReceivingAction = 0;
		int totalTag = 0, totalNonTag = 0;
		
		// verifica tipos de relacoes
		int cont = 0;
		for (FileData file : listFiles2) {
			ArrayList<Token> removedToken = new ArrayList<Token>();
			for (Token t : file.getTokens()) {
				if (t.features.get(features.ofText.getFeature()) == 0) {

					/*HashSet<String> roles = new HashSet<String>();
					for (String auxLema : t.tokenWhoInfered.keySet()) {
					
						Token aux = findToken(auxLema, file.getTokens());
						String role = t.tokenWhoInfered.get(aux).relation.get(0).toLowerCase();
						roles.add(role);

						if (rolesTag.containsKey(role))
							rolesTag.put(role, rolesTag.get(role) + 1);
						else
							rolesTag.put(role, 1);
					}*/

					if (t.getClassification().equals("No"))// && roles.contains("capableofreceivingaction"))
						cont++;

					// Obtem o tfxidf do primeiro
					if (t.tokenWhoInfered.size() > 0) {
						Token maiorTfIdf = findToken(t.tokenWhoInfered.keys().nextElement(), file.getTokens());
						for (String auxLema : t.tokenWhoInfered.keySet()) {
							Token aux  = findToken(auxLema, file.getTokens());
							System.out.println(aux);
							if (aux != null && maiorTfIdf != null &&
								aux.features.get(features.tf_idf.getFeature()) > maiorTfIdf.features.get(features.tf_idf.getFeature()))
								maiorTfIdf = aux;
						}
					

					// t.features.put(features.tf_idf.getFeature(),
					// maiorTfIdf.features.get(features.tf_idf.getFeature()));
					// t.features.put(features.firstOccurrence.getFeature(),
					// maiorTfIdf.features.get(features.firstOccurrence.getFeature()));
					// t.features.put(features.spread.getFeature(),
					// maiorTfIdf.features.get(features.spread.getFeature()));

					/*
					 * if
					 * (t.features.get(features.semanticSimilarity.getFeature())
					 * < maiorTfIdf.features.get(features.semanticSimilarity.
					 * getFeature())) {
					 * t.features.put(features.roleIsARelatedTo.getFeature(),
					 * 0.0); //removedToken.add(t); continue; }
					 */

					/*
					 * if
					 * (t.features.get(features.semanticSimilarity.getFeature())
					 * < maiorTfIdf.features.get(features.semanticSimilarity.
					 * getFeature())) {//&&
					 * //t.getClassification().equals("No")) {
					 * t.features.put(features.roleIsARelatedTo.getFeature(),
					 * 0.0); continue; }
					 */

					if (maiorTfIdf != null && 
						t.features.get(features.semanticSimilarity.getFeature()) >= maiorTfIdf.features.get(features.semanticSimilarity.getFeature()))
						//&& (t.tokenWhoInfered.get(maiorTfIdf).relation.get(0).toLowerCase().equals("isa") 
						//	|| t.tokenWhoInfered.get(maiorTfIdf).relation.get(0).toLowerCase().equals("relatedto")))
						t.features.put(features.roleIsARelatedTo.getFeature(), 1.0);
					else
						t.features.put(features.roleIsARelatedTo.getFeature(), 0.0);

					if (t.getClassification().equals("Yes")) {
						if (t.features.get(features.roleIsARelatedTo.getFeature()) == 1)
							countTagRelatedToIsA++;
						else {

							/*
							 * if (t.getLemma().equals("protein interaction") &&
							 * (file.getName().equals("833421"))) {
							 * System.out.println("File: " + file.getName());
							 * for (Token aux : file.getTokens()) { if
							 * (aux.features.get(features.ofText.getFeature())
							 * == 1 &&
							 * aux.features.get(features.tf_idf.getFeature()) >
							 * maiorTfIdf
							 * .features.get(features.tf_idf.getFeature()) &&
							 * aux
							 * .features.get(features.semanticSimilarity.getFeature
							 * ()) <=
							 * t.features.get(features.semanticSimilarity.
							 * getFeature())) System.out.println(aux.getLemma()
							 * + " " +
							 * aux.features.get(features.tf_idf.getFeature()) +
							 * " " +
							 * aux.features.get(features.semanticSimilarity
							 * .getFeature())); }
							 */
							/*
							 * System.out.println(t.getLemma() + "(tag) " +
							 * file.getName()); for (String f :
							 * t.features.keySet()) { if
							 * (f.equals("Keyphraseness")) System.out.println(f
							 * + ": " + t.features.get(f)*180); else
							 * System.out.println(f + ": " + t.features.get(f));
							 * } System.out.println("Escolhido: " +
							 * maiorTfIdf.getLemma());
							 * 
							 * System.out.println("Quem inferiu: "); for (Token
							 * aux : t.tokenWhoInfered.keySet()) {
							 * System.out.println(aux.getLemma() + " " +
							 * t.tokenWhoInfered.get(aux) + " " +
							 * aux.getClassification() + " TFxIDF: " +
							 * aux.features.get(features.tf_idf.getFeature()) +
							 * " Semantic: " +
							 * aux.features.get(features.semanticSimilarity
							 * .getFeature()) + " Keyphraseness: " +
							 * aux.features
							 * .get(features.keyphraseness.getFeature())); }
							 */
						}
						}
					} else {
						if (t.features.containsKey(features.roleIsARelatedTo.getFeature()) &&
								t.features.get(features.roleIsARelatedTo.getFeature()) == 1) {
							countNonTagRelatedToIsA++;
						}
					}
					/*
					 * if (t.getClassification().equals("Yes")) {
					 * //t.features.put(features.roleIsARelatedTo.getFeature(),
					 * 1.0); //totalTag++; HashSet<String> roles = new
					 * HashSet<String>(); for (Token aux :
					 * t.tokenWhoInfered.keySet()) { String role =
					 * t.tokenWhoInfered.get(aux).toLowerCase();
					 * roles.add(role);
					 * 
					 * if (rolesTag.containsKey(role)) rolesTag.put(role,
					 * rolesTag.get(role)+1); else rolesTag.put(role, 1); }
					 * 
					 * if ((roles.contains("relatedto") ||
					 * roles.contains("isa")) &&
					 * !roles.contains("capableofreceivingaction") &&
					 * !roles.contains("capableof") &&
					 * !roles.contains("usedfor") &&
					 * !roles.contains("propertyof") &&
					 * !roles.contains("subeventof") &&
					 * !roles.contains("locationof") &&
					 * !roles.contains("partof") && !roles.contains("definedas")
					 * && (!roles.contains("atlocation") &&
					 * !roles.contains("motivationof"))) {
					 * countTagRelatedToIsA++;
					 * t.features.put(features.roleIsARelatedTo.getFeature(),
					 * 1.0); } else {
					 * t.features.put(features.roleIsARelatedTo.getFeature(),
					 * 0.0);
					 * 
					 * if
					 * (t.features.get(features.roleIsARelatedTo.getFeature())
					 * == 0) { System.out.println(t.getLemma() + "(tag): " +
					 * roles); for (String f : t.features.keySet()) { if
					 * (f.equals("Keyphraseness")) System.out.println(f + ": " +
					 * t.features.get(f)*180); else System.out.println(f + ": "
					 * + t.features.get(f)); }
					 * System.out.println("Quem inferiu: "); for (Token aux :
					 * t.tokenWhoInfered.keySet()) {
					 * System.out.println(aux.getLemma() + " " +
					 * t.tokenWhoInfered.get(aux) + " " +
					 * aux.getClassification() + " TFxIDF: " +
					 * aux.features.get(features.tf_idf.getFeature()) +
					 * " Semantic: " +
					 * aux.features.get(features.semanticSimilarity
					 * .getFeature()) + " Keyphraseness: " +
					 * aux.features.get(features.keyphraseness.getFeature())); }
					 * } } } else { totalNonTag++;
					 * //t.features.put(features.roleIsARelatedTo.getFeature(),
					 * 0.0); HashSet<String> roles = new HashSet<String>(); for
					 * (Token aux : t.tokenWhoInfered.keySet()) { String role =
					 * t.tokenWhoInfered.get(aux).toLowerCase();
					 * roles.add(role);
					 * 
					 * if (rolesNonTag.containsKey(role)) rolesNonTag.put(role,
					 * rolesNonTag.get(role)+1); else rolesNonTag.put(role, 1);
					 * }
					 * 
					 * //if (roles.contains("capableofreceivingaction") ||
					 * roles.contains("capableof") || //
					 * roles.contains("usedfor") || roles.contains("propertyof")
					 * || roles.contains("subeventof")) //
					 * countNonTagCapableOfReceivingAction++; //if
					 * (!roles.contains("capableofreceivingaction") &&
					 * !roles.contains("capableof") && //
					 * (roles.contains("relatedto") || roles.contains("isa")))
					 * // countNonTagRelatedToIsA++; if
					 * ((roles.contains("relatedto") || roles.contains("isa"))
					 * && !roles.contains("capableofreceivingaction") &&
					 * !roles.contains("capableof") &&
					 * !roles.contains("usedfor") &&
					 * !roles.contains("propertyof") &&
					 * !roles.contains("subeventof") &&
					 * !roles.contains("locationof") &&
					 * !roles.contains("partof") && !roles.contains("definedas")
					 * && (!roles.contains("atlocation") &&
					 * !roles.contains("motivationof"))) {
					 * countNonTagRelatedToIsA++;
					 * t.features.put(features.roleIsARelatedTo.getFeature(),
					 * 1.0); //System.out.println(t.getLemma() + "(nao tag): " +
					 * roles); } else {
					 * t.features.put(features.roleIsARelatedTo.getFeature(),
					 * 0.0); } }
					 */
				} else
					// t.features.put(features.roleIsARelatedTo.getFeature(),
					// 0.5);
					t.features.put(features.roleIsARelatedTo.getFeature(), Double.NaN);
			}
			file.getTokens().removeAll(removedToken);
		}

		/*
		 * System.out.println("TAGS: "); for (String role : rolesTag.keySet()) {
		 * System.out.println(role + ": " + rolesTag.get(role)); }
		 * 
		 * System.out.println("NON TAGS: "); for (String role :
		 * rolesNonTag.keySet()) { System.out.println(role + ": " +
		 * rolesNonTag.get(role)); }
		 */

		// System.out.println("Tags: " + totalTag);
		// System.out.println("Total non tags: " + totalNonTag);

		System.out.println("countTagRelatedToIsA: " + countTagRelatedToIsA);
		// System.out.println("countTagCapableOfReceivingAction: " +
		// countTagCapableOfReceivingAction);
		System.out.println("countNonTagRelatedToIsA: " + countNonTagRelatedToIsA);
		// System.out.println("countNonTagCapableOfReceivingAction: " +
		// countNonTagCapableOfReceivingAction);
		System.out.println("Contagem: " + cont);
	}
}
