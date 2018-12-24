package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.omg.CosNaming.IstringHelper;

import classifier.RF;
import config.Config;
//import edu.stanford.nlp.ling.CoreAnnotations.PercentAnnotation;
import features.Features;
import features.Features.features;
import features.Keyphraseness;
import maui.stopwords.Stopwords;
import maui.stopwords.StopwordsEnglish;
import preprocessing.AddInferenceNet;
import preprocessing.Balance;
import preprocessing.FormatFile;
import util.FileData;
import util.Relation;
import util.StanfordLemmatizer;
import util.Token;
import util.WriteArff;
import weka.core.Instance;

public class MainInfNet {
	
	private static void removeStopWords(ArrayList<FileData> listFiles) {
		//Remove tokens stop words ou tamanho maior que 3 ou inicia ou termina com stop word
		Stopwords stopwords = new StopwordsEnglish();
		for (FileData file : listFiles) {
			ArrayList<Token> tokenRemoved = new ArrayList<Token>();
			for (Token t : file.getTokens()) {
				//if (t.features.get(features.ofText.getFeature()) == 1 && t.getClassification().equals("No")) {
				if (t.getClassification().equals("No")) {
					StringTokenizer token = new StringTokenizer(t.getLemma(), " ");
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
						if (stopwords.isStopword(buffer[0]) || stopwords.isStopword(buffer[pos-1]))
							tokenRemoved.add(t);
					}
				}
			}
			file.getTokens().removeAll(tokenRemoved);
		}
				
		//Remove os termos nao do texto que nao sao inferidos por ninguem ou que possuem as mesmas palavras (independente da ordem)
		for (FileData file : listFiles) {
			ArrayList<Token> removedTokens = new ArrayList<Token>();
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 0) {
					if (token.tokenWhoInfered.size() == 0)
						removedTokens.add(token);
					
					if (token.getClassification().equals("No")){
						String[] words = token.getLemma().split(" ");
						if (words.length > 1) {
							Arrays.sort(words);
							
							for (Token aux : file.getTokens()) {
								if (!aux.getLemma().equals(token.getLemma())) {
									String[] wordsAux = aux.getLemma().split(" ");
									if (wordsAux.length == words.length) {
										boolean equal = true;
										for (int i = 0; i < wordsAux.length; i++) {
											if (!wordsAux[i].equals(words[i])) {
												equal = false;
												break;
											}
										}
										if (equal) {
											removedTokens.add(token);
											break;
										}
									}
								}
							}
						}
					}
				}
				
			}
			
			for (Token token : file.getTokens()) {
				ArrayList<Token> remove = new ArrayList<Token>();
				for (Token t : token.tokenInfered.keySet()) {
					if (removedTokens.contains(t)) {
						remove.add(t);
					}
				}
				
				for (Token t : remove)
					token.tokenInfered.remove(t);
			}
			
			file.getTokens().removeAll(removedTokens);
		}
	}
	
	public static int modelFeatures = 0;
	public static int modelBalance = 0;
	public static int percentageOverSampling = 0;
	public static String outPutExperiments = "data/output.txt";
	
	public static void main (String[] args) {
		
		String pathData = "data/schutz/features_I/";
		//String pathData = "data/newTest/old/correct_features6/";
		//String pathData = "data/newTest/old/correct_features6Inf/"; //Add 17/09/2015
		//String pathData = "data/newTest/old/correct_features6InfOrig/"; //Add 17/09/2015
		modelFeatures = 0; //0: KEA; 1: MAUI; 2: MAUI + ISR
		int percentNonText = 0; //0: Without nonText; -1: Non manipulated base; 1: 100% manipulated base; XX: Percentage of manipulation
		modelBalance = 0; //0: Without balance; 1: SMOTE; 2: Over-sampling majority class
		String knowledgeBase = "C"; //I: InferenceNet; C: ConceptNet
		if (args.length > 0) {
			pathData = args[0];
			modelFeatures = Integer.parseInt(args[1]);
			modelBalance = Integer.parseInt(args[2]);
			knowledgeBase = args[3];
		}
		
		//ArrayList<FileData> listFiles2 = new FormatFile().readFormatFiles("data/newTest/old/infOriginalFeatures/");
		//ArrayList<FileData> listFilesInf = new FormatFile().readFormatFiles("data/newTest/old/correct_features6InfOrig/");
		//ArrayList<FileData> listFilesInf = new FormatFile().readFormatFiles("data/newTest/old/infOriginalFeatures/");
		ArrayList<FileData> listFilesInf = new FormatFile().readFormatFiles(pathData);
		
		System.out.println("Terminou de ler");
		
		//Set original to all non text tokens
		if (args.length > 0) {
			for (FileData file : listFilesInf) {
				for (Token token : file.getTokens()) {
					if (token.features.get(features.ofText.getFeature()) == 0.0)
						token.originalInferenceNet = true;
				}
			}
		}
		
		System.out.println("1");
		//Voltar esse codigo qdo quiser fazer os testes da progressao da InferenceNet
		/*for (FileData file : listFilesInf) {
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 0)
					token.originalInferenceNet = true;
			}
		}
		
		for (FileData file : listFiles2) {
			for (FileData fileInf : listFilesInf) {
				if (file.getName().equals(fileInf.getName())) {
					for (Token token : file.getTokens()) {
						boolean encontrado = false;
						for (Token tokenInf : fileInf.getTokens()) {
							if (token.getLemma().equals(tokenInf.getLemma())) {
								encontrado = true;
								break;
							}
						}
						if (!encontrado)
							fileInf.getTokens().add(token);
					}
				}
			}
		}*/
		
		//Calcula a ISR
		if (modelFeatures == 2) {
			if (knowledgeBase.equals("I"))
				checksRoles(listFilesInf);
	
			for (FileData file : listFilesInf) {
				for (Token token : file.getTokens()) {
					token.features.put(features.roleIsARelatedTo.getFeature(), Double.NaN);
				}
			}
			
			int isATag = 0, isAToken = 0, nodeDegTag = 0, nodeDegToken = 0,
				wikipTag = 0, wikipTok = 0, KeyphTag = 0, keyphTok = 0;
			for (FileData file : listFilesInf) {
				for (Token token : file.getTokens()) {
					
					if (token.features.get(features.ofText.getFeature()) == 0) {
						Token maiorTfIdf = token.tokenWhoInfered.keySet().iterator().next();
						for (Token aux : token.tokenWhoInfered.keySet()) {
							if (aux.features.get(features.tf_idf.getFeature()) >= maiorTfIdf.features.get(features.tf_idf.getFeature()))
								maiorTfIdf = aux;
						}
					
						if (token.features.get(features.semanticSimilarity.getFeature()) >= maiorTfIdf.features.get(features.semanticSimilarity.getFeature())
							) {
							token.features.put(features.roleIsARelatedTo.getFeature(), 1.0);
							if (token.getClassification().equals("Yes")) {
								isATag++;
								if (token.features.get(features.keyphraseness.getFeature()) > 0)
									KeyphTag++;
								
								//System.out.println(maiorTfIdf.getLemma() + " -> " + token.tokenWhoInfered.get(maiorTfIdf).get(0) + " -> " + token.getLemma() + "(" + token.originalInferenceNet + ")");
								
							}
							else {
								isAToken++;
								//Keyphraseness key = new Keyphraseness();
								//key.calculateKeyphraseness(token, listFilesInf);
								//token.features.put(features.keyphraseness.getFeature(), 0.0);
								//if (token.features.get(features.keyphraseness.getFeature()) > 0)
								//	keyphTok++;
							}
						}
						else
							token.features.put(features.roleIsARelatedTo.getFeature(), 0.0);
					}
					
					//for (Token aux: token.tokenWhoInfered.keySet()) {
						/*for (String relacao : token.tokenWhoInfered.get(aux)) {
							Token maiorTfIdf = t.tokenWhoInfered.keySet().iterator().next();
							for (Token aux : t.tokenWhoInfered.keySet()) {
								if (aux.features.get(features.tf_idf.getFeature()) > maiorTfIdf.features.get(features.tf_idf.getFeature()))
									maiorTfIdf = aux;
							}
							
							/*if (relacao.toLowerCase().equals("capableofreceivingaction")) {
								if (token.getClassification().equals("Yes")) {
									isATag++;
								}
								else {
									isAToken++;
								}
							}*/
						//}
					//}
				}
			}
		}
		System.out.println("2");
		/*System.out.println("IsA tag: " + isATag);
		System.out.println("IsA token: " + isAToken);
		
		System.out.println("Keyp tag: " + KeyphTag);
		System.out.println("Keyp token: " + keyphTok);
		
		System.out.println("Keyp tag: " + nodeDegTag);
		System.out.println("Keyp token: " + nodeDegToken);
		
		System.out.println("Keyp tag: " + wikipTag);
		System.out.println("Keyp token: " + wikipTok);*/
		 
		removeStopWords(listFilesInf);
		System.out.println("3");
		//new FormatFile().writeFormatFile(listFiles2, "data/newTest/old/correct_features6InfOrig/");
		/*int countDif = 0;
		for (FileData file2 : listFiles2) {
			for (FileData fileAux : listFileAux) {
				if (file2.getName().equals(fileAux.getName())) {
					for (Token t : file2.getTokens()) {
						for (Token tAux : fileAux.getTokens()) {
							if (t.getLemma().equals(tAux.getLemma())) {
								
								for (String f : t.features.keySet()) {
									if (!t.features.get(f).equals(tAux.features.get(f))) {
										System.out.println("Diferente: " + f + "( " + t.features.get(f) + "; " + tAux.features.get(f) + ")");
										countDif++;
									}
								}
								
							}
						}
					}
				}
			}
		}
		System.out.println("Qtde de diferentes: " + countDif);*/
		
		
		//new FormatFile().writeFormatFile(listFiles2, "data/newTest/old/correct_features6Inf/");
		
		//ArrayList<FileData> listFilesInf = new FormatFile().readFormatFiles("data/newTest/old/infOriginal/");
		
		//removeStopWords(listFiles2);
		
		/*int tokensNaoText = 0, tokensNaoTextOrig = 0, tagsNaoText = 0, tagsNaoTextOrig = 0;
		for (FileData file : listFiles2) {
			for (Token t : file.getTokens()) {
				if (t.features.get(features.ofText.getFeature()) == 0) {
					if (t.getClassification().equals("Yes")) {
						tagsNaoText++;
						if (t.originalInferenceNet)
							tagsNaoTextOrig++;
					}
					else {
						tokensNaoText++;
						if (t.originalInferenceNet)
							tokensNaoTextOrig++;
					}
				}
			}
			//new FormatFile().writeFormatFileByFile(file, "data/newTest/old/correct_features6Inf/");
		}
		System.out.println("Tokens nao texto: " + tokensNaoText);
		System.out.println("Tokens nao texto original: " + tokensNaoTextOrig);
		System.out.println("Tags nao texto: " + tagsNaoText);
		System.out.println("Tags nao texto original: " + tagsNaoTextOrig);*/
		
		
		//Analise das features dos inferidos com quem inferiu
		/*int tag = 0, naoTag = 0, maiorTfIdfTag = 0, maiorTfIdfNaoTag = 0;
		int qtdeTagMais1Inf = 0, qtdeTokenMais1Inf = 0;
		int tagIgualMais1 = 0, naoTagIgualMais1 = 0;
		for (FileData file : listFiles2) {
			for (Token token : file.getTokens()) {
				if (token.tokenWhoInfered.size() > 1) {
					if (token.getClassification().equals("Yes")) {
						qtdeTagMais1Inf++;
					}
					else
						qtdeTokenMais1Inf++;
				}
				
				if (token.features.get(features.ofText.getFeature()) == 0) {
					boolean equalClassif = false;
					Token equalToken = null;
					for (Token whoInf : token.tokenWhoInfered.keySet()) {
						if (whoInf.getClassification().equals(token.getClassification())) {
							equalClassif = true;
							equalToken = whoInf;
						}
					}
					
					if (equalClassif) {
						if (token.getClassification().equals("Yes")) {
							tag++;
							if (token.tokenWhoInfered.size() > 1)
								tagIgualMais1++;
						}
						else { 
							naoTag++;
							if (token.tokenWhoInfered.size() > 1)
								naoTagIgualMais1++;
						}
								
						Token maiorTf = equalToken;
						for (Token whoInf : token.tokenWhoInfered.keySet()) {
							if (whoInf.features.get(features.semanticSimilarity.getFeature()) > maiorTf.features.get(features.semanticSimilarity.getFeature()))
								maiorTf = whoInf;	
						}
						if (maiorTf.getClassification().equals(token.getClassification())) {
							if (token.getClassification().equals("Yes"))
								maiorTfIdfTag++;
							else 
								maiorTfIdfNaoTag++;
						}
						else {
							if (token.getClassification().equals("Yes"))
								System.out.println(maiorTf.getClassification() + " -> " + maiorTf.features.get(features.keyphraseness.getFeature()));
						}
							
					}
				}
			}
		}
		
		System.out.println("Igual classif tag: " + tag);
		System.out.println("Igual classif nao tag: " + naoTag);*/
		
		//System.out.println("Tag mais 1 inf: " + qtdeTagMais1Inf);
		//System.out.println("Nao tag mais 1 inf: " + qtdeTokenMais1Inf);
		
		//System.out.println("An�lise feature tag: " + maiorTfIdfTag);
		//System.out.println("An�lise feature nao tag: " + maiorTfIdfNaoTag);
		
		//System.out.println("Tag igual mais 1: " + tagIgualMais1);
		//System.out.println("Nao tag igual mais 1: " + naoTagIgualMais1);
		
		System.out.println("4");
		//obtem os valores das features sintaticas dos termos inferidos
		for(FileData file : listFilesInf) {
			for (Token t : file.getTokens()) {
				if (t.features.get(features.ofText.getFeature()) == 0) {
					//double firstOcc = 0, tf_idf = 0, spread = 0;
					//Token maiorTfIdf = t.tokenWhoInfered.keySet().iterator().next();
					//Token maiorFirsOc = maiorTfIdf;
					//Token maiorSpread = maiorTfIdf;
					
					//for (Token aux : t.tokenWhoInfered.keySet()) {
						
						//if (aux.features.get(features.semanticSimilarity.getFeature()) >= maiorTfIdf.features.get(features.semanticSimilarity.getFeature()))
						//	maiorTfIdf = aux;
						
						//if (aux.features.get(features.firstOccurrence.getFeature()) < maiorFirsOc.features.get(features.firstOccurrence.getFeature()))
						//	maiorFirsOc = aux;
						
						//if (aux.features.get(features.spread.getFeature()) < maiorSpread.features.get(features.spread.getFeature()))
						//	maiorSpread = aux;
						
						//firstOcc += aux.features.get(features.firstOccurrence.getFeature());
						//tf_idf += aux.features.get(features.tf_idf.getFeature());
						//spread += aux.features.get(features.spread.getFeature());
						
						//atribui o valor das features sintaticas do ultimo termo que inferiu 
						//t.features.put(features.firstOccurrence.getFeature(), aux.features.get(features.firstOccurrence.getFeature()));
						//t.features.put(features.tf_idf.getFeature(), aux.features.get(features.tf_idf.getFeature()));
						//t.features.put(features.spread.getFeature(), aux.features.get(features.spread.getFeature()));
						//break;
						//t.features.put(features.tf_idf.getFeature(), 1.0);
					//}
					//t.features.put(features.firstOccurrence.getFeature(), Instance.missingValue());
					//t.features.put(features.spread.getFeature(), Instance.missingValue());
					
					//Maior TFxIDF
					//t.features.put(features.firstOccurrence.getFeature(), maiorTfIdf.features.get(features.firstOccurrence.getFeature()));
					//t.features.put(features.tf_idf.getFeature(), maiorTfIdf.features.get(features.tf_idf.getFeature()));
					//t.features.put(features.spread.getFeature(), maiorTfIdf.features.get(features.spread.getFeature()));
					
					//Media
					//t.features.put(features.firstOccurrence.getFeature(), firstOcc/t.tokenWhoInfered.size());
					//t.features.put(features.tf_idf.getFeature(), tf_idf/t.tokenWhoInfered.size());
					//t.features.put(features.spread.getFeature(), spread/t.tokenWhoInfered.size());
					
					//Zero
					t.features.put(features.firstOccurrence.getFeature(), 0.0);
					t.features.put(features.tf_idf.getFeature(), 0.0);
					t.features.put(features.spread.getFeature(), 0.0);
					
					//Sem valor
					//t.features.put(features.firstOccurrence.getFeature(), Instance.missingValue());
					//t.features.put(features.tf_idf.getFeature(), Instance.missingValue());
					//t.features.put(features.spread.getFeature(), Instance.missingValue());
				}
			}
		}
		
		System.out.println("5");
		//remove the terms not of text
		if (percentNonText == 0 || percentNonText == -1) {
			for (FileData file : listFilesInf) {
				ArrayList<Token> tokensRemoved = new ArrayList<Token>();
				for (Token token : file.getTokens()) {
					if (token.features.get(features.ofText.getFeature()) == 0 && 
					   (percentNonText != -1 || (percentNonText == -1 && !token.originalInferenceNet)))
						tokensRemoved.add(token);
				}
				file.getTokens().removeAll(tokensRemoved);
			}
		}
		
		//Get title file
		/*StanfordLemmatizer lemmatizer = new StanfordLemmatizer();
		Hashtable<String, String> titles = new MainInfNet().getTileFile();
		for (FileData file : listFilesInf) {
			file.setTitle(titles.get(file.getName()));
			if (file.getTitle() != null) { 
				file.setTitleLemmatized(lemmatizer.lemmatize(file.getTitle()).toString().replace("[", "").replace("]", "").replace("-", " ").toLowerCase());
				for (Token token : file.getTokens()) {
					if (file.getTitleLemmatized().contains(token.getLemma())) 
						token.features.put(features.titleOccurrence.getFeature(), 1.0);
					else
						token.features.put(features.titleOccurrence.getFeature(), 0.0);
				}
			}
			else
				System.out.println("Não encontrado titulo: " + file.getName());
		}*/
		
		
		//Adiciona os termos da inferenceNet
		//System.out.println("Adicionando os termos da InferenceNet...");
		//new AddInferenceNet(listFiles2).checkTagNoText();*/
		
		//Adds the missing value to terms that non exists in the Wikipedia 
		/*for (FileData file : listFilesInf) {
			for (Token token : file.getTokens()) {
				if (token.conceptWikipedia.equals("")) {
					token.features.put(features.semanticSimilarity.getFeature(), Instance.missingValue());
					token.features.put(features.inverseWikipediaLinkade.getFeature(), Instance.missingValue());
					token.features.put(features.nodeDegree.getFeature(), Instance.missingValue());
					token.features.put(features.wikipediaKeyphraseness.getFeature(), Instance.missingValue());
				}
			}
		}*/
		
		//Adiciona a inferência
		/*for (FileData file : listFiles2) {
		
			//Obtem maior frequencia de inferidos em cada arquivo
			int maxFrequency = 0;
			for (Token t : file.getTokens()) {
				if (t.features.get(features.ofText.getFeature()) == 0) {
					if (t.tokenWhoInfered.size() > maxFrequency)
						maxFrequency = t.tokenWhoInfered.size();
				}
			}
			
			for (Token t : file.getTokens()) {
				if (t.features.get(features.ofText.getFeature()) == 0) {
					int tf = t.tokenWhoInfered.size() / maxFrequency;
					int df = 0;
					for (FileData fileAux : listFiles2) {
						if (!fileAux.getName().equals(file.getName())) {
							for (Token tAux : file.getTokens()) {
								if (tAux.getLemma().equals(t.getLemma())) {
									df++;
									break;
								}
							}
						}
					}
					double idf = Math.log((double) listFiles2.size() / df);
					double tfxidf = (double) tf * idf;
					//t.features.put(features.tf_idf.getFeature(), t.features.get(features.tf_idf.getFeature()) * tfxidf);
					//t.features.put(features.firstOccurrence.getFeature(), t.features.get(features.firstOccurrence.getFeature()) * tfxidf);
					//t.features.put(features.spread.getFeature(), t.features.get(features.spread.getFeature()) * tfxidf);
					//System.out.print("Token: " + t.getLemma() + " TFxIDF (antigo): " + t.features.get(features.tf_idf.getFeature()) + "TFxIDF (inferência): " + tfxidf);
					t.features.put(features.tf_idf_inf.getFeature(), (double)idf);
					//t.features.put(features.tf_idf.getFeature(), t.features.get(features.tf_idf.getFeature()) * tfxidf);
					//System.out.println(" TFxIDF (novo): " + t.features.get(features.tf_idf.getFeature()));
				}
				//else {
				//	t.features.put(features.tf_idf_inf.getFeature(), Instance.missingValue());
				//}
			}
		}*/
		
		//System.out.println("Term predictiability...");
		//TermPredictiability term = new TermPredictiability(listFiles2);
		//term.calculatePredictiability();
		
		System.out.println("6");
		//Normaliza
		System.out.println("Normaling data");
		new Features().normalizeDataFeatures(listFilesInf);
		
		//checksRoles(listFilesInf);

		System.out.println("7");
		if (percentNonText > 0 && percentNonText < 1)
			reductionInferenceNet(listFilesInf, percentNonText);
		
		/*System.out.println("Writing arff...");
		try {
			new WriteArff().createFolders("citeULike");
			new WriteArff().writeFiles(listFilesInf, "citeULike");
		} catch (Exception e) { e.printStackTrace(); }*/
		
		//TermPredictiability term = new TermPredictiability(listFiles2);
		//term.calculatePredictiability();
		
		System.out.println("8");
		ArrayList<FileData> listFileNoBalance = (ArrayList<FileData>)listFilesInf.clone();
		//System.out.println("Term predictiabily test...");
		//term.calculatePrectiabilityTest(listFileNoBalance);
		
		//System.out.println("Tamanho antes do balanceamento: " + listFilesInf.size());
		//listFilesInf = new Balance().realizeBalance(listFilesInf);
		//System.out.println("Tamanho depois do balanceamento: " + listFilesInf.size());
		
		System.out.println("9");
		System.out.println("Classifing...");
		new RF().runRFCrossValidationDoc(listFilesInf, listFileNoBalance, false);
		System.out.println("Finished!!!");
	}
	
	public static void reductionInferenceNet (ArrayList<FileData> listFile, int percent) {
		
		int countTags = 0, countNonTags = 0;
		int countTagsOrig = 0, countNonTagsOrig = 0;
		for (FileData file : listFile) {
			for (Token token : file.getTokens()) {
				if (token.features.containsKey(features.ofText.getFeature()) &&
					token.features.get(features.ofText.getFeature()) == 0.0D) {
					if (token.getClassification().equals("Yes")) {
						countTags++;
						if (token.originalInferenceNet) 
							countTagsOrig++;
					}
					else {
						countNonTags++;
						if (token.originalInferenceNet)
							countNonTagsOrig++;
					}
				}
			}
		}
		
		System.out.println("Tags total: " + countTags);
		System.out.println("Tokens total: " + countNonTags);
		
		System.out.println("Tags total (orig): " + countTagsOrig);
		System.out.println("Tokens total (orig): " + countNonTagsOrig);
		
		int qttyTagsToRemove = (int)(((double)(100-percent)/100)*countTags);
		int qttyTokensToRemove = (int) (countNonTags-(countNonTagsOrig+((countNonTags-countNonTagsOrig)*(countTags-qttyTagsToRemove-countTagsOrig)/(countTags-countTagsOrig))));
		//int qttyTags = countTags - (countTags * percent / 100);
		//int qttyNonTags = countNonTags - (countNonTags * percent / 100);
		System.out.println("Remover: " + qttyTagsToRemove + " tags e " + qttyTokensToRemove + " não tags");
		int qttyRemovedTags = 0, qttyRemovedTokens = 0;
		
		for (FileData file : listFile) {
			if (qttyRemovedTags >= qttyTagsToRemove && qttyRemovedTokens >= qttyTokensToRemove) break;
			ArrayList<Token> tokensRemoved = new ArrayList<Token>();
			for (Token token : file.getTokens()) {
				if (token.features.containsKey(features.ofText.getFeature()) &&
					token.features.get(features.ofText.getFeature()) == 0.0D &&
					token.originalInferenceNet == false) {
					if (token.getClassification().equals("Yes")) {
						if (qttyRemovedTags < qttyTagsToRemove) {
							tokensRemoved.add(token);
							qttyRemovedTags++;
						}
					}
					else {
						if (qttyRemovedTokens < qttyTokensToRemove) {
							tokensRemoved.add(token);
							qttyRemovedTokens++;
						}
					}
					if (qttyRemovedTags >= qttyTagsToRemove && qttyRemovedTokens >= qttyTokensToRemove) break;
				}
			}
			file.getTokens().removeAll(tokensRemoved);
			if (qttyRemovedTags >= qttyTagsToRemove && qttyRemovedTokens >= qttyTokensToRemove) break;
		}
	}
	
	public static void checksRoles(ArrayList<FileData> listFiles2) {
		Hashtable<String, Integer> rolesNonTag = new Hashtable<String, Integer>();
		Hashtable<String, Integer> rolesTag = new Hashtable<String, Integer>();
		int countTagRelatedToIsA = 0, countTagCapableOfReceivingAction = 0;
		int countNonTagRelatedToIsA = 0, countNonTagCapableOfReceivingAction = 0;
		int totalTag = 0, totalNonTag = 0;
		//verifica tipos de relacoes
		int cont = 0;
		for (FileData file : listFiles2) {
			ArrayList<Token> removedToken = new ArrayList<Token>();
			for (Token t : file.getTokens()) {
				if (t.features.get(features.ofText.getFeature()) == 0) {
					
					HashSet<String> roles = new HashSet<String>();
					for (Token aux : t.tokenWhoInfered.keySet()) {
						String role = t.tokenWhoInfered.get(aux).relation.get(0).toLowerCase();
						roles.add(role);
						
						if (rolesTag.containsKey(role))
							rolesTag.put(role, rolesTag.get(role)+1);
						else
							rolesTag.put(role, 1);
					}
					
					if (t.getClassification().equals("No") && 
						roles.contains("capableofreceivingaction"))
						cont++;
						
					addRelationInf("human", "rna", "112878", "RelatedTo", file, t);
					addRelationInf("altruism", "behavior", "114199", "IsA", file, t);
					addRelationInf("leader", "represent science", "738207", "IsA", file, t);
					addRelationInf("ethic", "aspect behavioral", "546157", "IsA", file, t);
					addRelationInf("complex", "nonlinear", "44", "RelatedTo", file, t);	
					addRelationInf("system", "system biology", "407273", "IsA", file, t);
					addRelationInf("system biology", "system", "333353", "IsA", file, t);
					addRelationInf("system biology", "system", "833421", "IsA", file, t);
					addRelationInf("distribute", "action", "1632947", "IsA", file, t);
					addRelationInf("gene", "cerevisiae gene", "1624776", "IsA", file, t);
					addRelationInf("protein", "protein sci", "126997", "IsA", file, t);
					addRelationInf("cell", "structure", "1206611", "IsA", file, t);
					addRelationInf("social network", "phenomenon world", "99", "IsA", file, t);
					addRelationInf("social network", "dij", "459365", "RelatedTo", file, t);
					addRelationInf("social network", "community large", "1206611", "IsA", file, t);
					addRelationInf("social network", "network social", "303889", "IsA", file, t);
					addRelationInf("scale free", "law power scale", "90558", "IsA", file, t);
					addRelationInf("scale free", "different number", "833421", "IsA", file, t);
					addRelationInf("gene expression", "dna sequence", "904109", "IsA", file, t);
					addRelationInf("bioinformatic", "gene", "880918", "RelatedTo", file, t);
					addRelationInf("protein", "community protein", "833421", "relatedTo", file, t);
					addRelationInf("protein network", "cancer protein", "833421", "RelatedTo", file, t);
					addRelationInf("neuroanatomy", "cerebral", "80410", "RelatedTo", file, t);
					addRelationInf("socialnetwork", "community large", "1206611", "IsA", file, t);
					addRelationInf("sociophysic", "network social", "1206611", "RelatedTo", file, t);
					addRelationInf("social dynamics", "network social", "1206611", "RelatedTo", file, t);
					addRelationInf("neurophysiology", "neurophysiol", "1197981", "IsA", file, t);
					addRelationInf("sociology", "arrangement social", "114199", "RelatedTo", file, t);
					addRelationInf("economics", "gain", "114199", "RelatedTo", file, t);
					addRelationInf("gene expression", "expression gene", "1097231", "IsA", file, t);
					addRelationInf("bio inspire", "olfactory robot", "1067802", "RelatedTo", file, t);
					addRelationInf("apply math", "connection neuron", "101", "IsA", file, t);
					addRelationInf("sub graph pattern", "node set", "101", "IsA", file, t);
					addRelationInf("sociology", "community structure", "1206611", "IsA", file, t);
					addRelationInf("swarm", "community structure", "1206611", "RelatedTo", file, t);
					addRelationInf("cell", "composition", "1206611", "RelatedTo", file, t);
					addRelationInf("taggingpre", "information supplementary", "1206611", "IsA", file, t);
					addRelationInf("tag", "information supplementary", "1206611", "IsA", file, t);
					addRelationInf("neuroeconomic", "american association", "121661", "RelatedTo", file, t);
					addRelationInf("decision making", "procedure", "121661", "IsA", file, t);
					addRelationInf("reinforcement learning", "procedure", "121661", "IsA", file, t);
					addRelationInf("review", "reader", "1272533", "RelatedTo", file, t);
					addRelationInf("choicebehavior", "activity brain", "1322799", "RelatedTo", file, t);
					addRelationInf("incentive", "modality reward", "1322799", "IsA", file, t);
					addRelationInf("ncrna", "lrna", "1336057", "RelatedTo", file, t);
					addRelationInf("non coding", "lrna overlap", "1336057", "RelatedTo", file, t);
					addRelationInf("classification", "cluster", "1336057", "RelatedTo", file, t);
					addRelationInf("gene expression", "gene", "141524", "IsA", file, t);
					addRelationInf("genevariation", "gene propagation", "142488", "RelatedTo", file, t);
					addRelationInf("genecircuit", "downstream gene", "142488", "RelatedTo", file, t);
					addRelationInf("bio", "biology mining", "136657", "RelatedTo", file, t);
					addRelationInf("txtmine", "biology mining", "136657", "RelatedTo", file, t);
					addRelationInf("non mendelian inheritance", "inheritance mendelian", "137111", "IsA", file, t);
					removeRelation("non mendelian inheritance", "inheritance", file);
					addRelationInf("avano lab", "datum experimental", "142488", "RelatedTo", file, t);
					addRelationInf("reinforcement learning", "sensorimotor", "1532668", "RelatedTo", file, t);
					addRelationInf("eisen journal club", "online science", "1551105", "IsA", file, t);
					addRelationInf("theory of mind", "mind theory", "155900", "IsA", file, t);
					addRelationInf("social cognition", "perception rational", "1632947", "IsA", file, t);
					addRelationInf("modeling", "process", "1752368", "IsA", file, t);
					addRelationInf("language evolution", "evolution language", "211497", "IsA", file, t);
					addRelationInf("gene expression", "expression gene", "2235507", "IsA", file, t);
					addRelationInf("great ape", "kalimantan", "226992", "RelatedTo", file, t);
					addRelationInf("classic", "use widely", "238188", "IsA", file, t);
					addRelationInf("protein dynamics", "binding nucleotide", "239581", "RelatedTo", file, t);
					addRelationInf("systemsbiology", "biology system", "248", "IsA", file, t);
					addRelationInf("sysbio", "biology system", "248", "IsA", file, t);
					addRelationInf("reinforcement learning", "training", "261639", "IsA", file, t);
					addRelationInf("language evolution", "evolution language", "265789", "IsA", file, t);
					addRelationInf("graphcluster", "algorithm clustering", "302050", "RelatedTo", file, t);
					addRelationInf("nature publishing group", "group nature publishing", "303213", "IsA", file, t);
					addRelationInf("social software", "revolution social", "303213", "RelatedTo", file, t);
					addRelationInf("protein evolution", "acid amino", "329170", "RelatedTo", file, t);
					addRelationInf("correlate mutation", "artificial protein", "329170", "RelatedTo", file, t);
					addRelationInf("system biology", "biological network", "333353", "RelatedTo", file, t);
					addRelationInf("reinforcement learning", "learn reinforcement", "353537", "IsA", file, t);
					addRelationInf("reinforcement learning", "stage training", "355573", "IsA", file, t);
					addRelationInf("prefrontal cortex", "basal cortex", "355573", "RelatedTo", file, t);
					addRelationInf("sysbio", "biol", "3594", "IsA", file, t);
					addRelationInf("algorithm", "toolkit", "415502", "IsA", file, t);
					addRelationInf("collaboration", "researcher", "422950", "RelatedTo", file, t);
					addRelationInf("comunication", "wikipedian", "438129", "IsA", file, t);
					addRelationInf("credibility", "quality", "438129", "IsA", file, t);
					addRelationInf("survey", "different scientist see", "44", "IsA", file, t);
					addRelationInf("p2p", "core module", "477450", "IsA", file, t);
					addRelationInf("ppus", "core module", "477450", "IsA", file, t);
					addRelationInf("protein protein", "connect protein", "478707", "IsA", file, t);
					addRelationInf("dna binding", "binding dna", "482101", "IsA", file, t);
					addRelationInf("online behavior", "internet use", "499796", "RelatedTo", file, t);
					addRelationInf("classification", "cluster", "504894", "RelatedTo", file, t);
					addRelationInf("regulatory cascade", "activator system", "506455", "IsA", file, t);
					addRelationInf("molecular signaling", "expression gene", "506455", "IsA", file, t);
					addRelationInf("decisionmake", "postchoice", "507926", "IsA", file, t);
					addRelationInf("ncrna", "rna small", "523878", "RelatedTo", file, t);
					//addRelationInf("gene set analysis", "gene set", "523878", "RelatedTo", file, t);
					addRelationInf("gene expression", "expression gene", "553494", "IsA", file, t);
					addRelationInf("review", "view", "668933", "IsA", file, t);
					addRelationInf("footprinting", "method supplemental", "698675", "IsA", file, t);
					addRelationInf("reinforcement learning", "learn model", "702349", "IsA", file, t);
					addRelationInf("discourse analysis", "analysis discourse", "738207", "IsA", file, t);
					addRelationInf("social network analysis", "network reference", "738207", "RelatedTo", file, t);
					addRelationInf("impact factor", "citation use", "738207", "RelatedTo", file, t);
					addRelationInf("author impact", "citation use", "738207", "RelatedTo", file, t);
					addRelationInf("author influence", "citation use", "738207", "RelatedTo", file, t);
					addRelationInf("social navigation", "information journal", "740681", "RelatedTo", file, t);
					addRelationInf("statistics", "log probability", "778023", "RelatedTo", file, t);
					addRelationInf("optical imaging", "cortex visual", "80410", "RelatedTo", file, t);
					addRelationInf("protein interaction", "community protein", "833421", "RelatedTo", file, t);
					
					if (t.getLemma().equals("system biology")) {
						for (Token aux : file.getTokens()) {
							if (aux.features.get(features.ofText.getFeature()) == 1 && 
								aux.getLemma().equals("biology system")) {
								Relation rel = new Relation();
								rel.relation.add("IsA");
								t.tokenWhoInfered.put(aux, rel);
								aux.tokenInfered.put(t, rel.relation);
								break;
							}
						}
					}
					
					boolean removed = false;
					String[] words = t.getLemma().replace("-", " ").split(" ");
					if (words.length > 1 && t.getClassification().equals("No")) {
						Arrays.sort(words);
						
						for (Token token : file.getTokens()) {
							if (!token.getLemma().equals(t.getLemma())) {
								String[] wordsAux = token.getLemma().replace("-", " ").split(" ");
								if (wordsAux.length == words.length) {
									boolean equal = true;
									for (int i = 0; i < words.length; i ++) {
										if (!words[i].equals(wordsAux[i])) {
											equal = false;
											break;
										}
									}
									if (equal) {
										removedToken.add(t);
										removed = true;
									}
								}
							}
						}
					}
					
					if (removed) continue;
					
					//Obtem o tfxidf do primeiro
					if (t.tokenWhoInfered.size() == 0) { removedToken.add(t); continue; }
					Token maiorTfIdf = t.tokenWhoInfered.keySet().iterator().next();
					for (Token aux : t.tokenWhoInfered.keySet()) {
						if (aux.features.get(features.tf_idf.getFeature()) > maiorTfIdf.features.get(features.tf_idf.getFeature()))
							maiorTfIdf = aux;
					}
					
					//t.features.put(features.tf_idf.getFeature(), maiorTfIdf.features.get(features.tf_idf.getFeature()));
					//t.features.put(features.firstOccurrence.getFeature(), maiorTfIdf.features.get(features.firstOccurrence.getFeature()));
					//t.features.put(features.spread.getFeature(), maiorTfIdf.features.get(features.spread.getFeature()));
					
					/*if (t.features.get(features.semanticSimilarity.getFeature()) <
						maiorTfIdf.features.get(features.semanticSimilarity.getFeature())) {
						t.features.put(features.roleIsARelatedTo.getFeature(), 0.0);
						//removedToken.add(t);
						continue;
					}*/
						
					
					/*if (t.features.get(features.semanticSimilarity.getFeature()) <
						maiorTfIdf.features.get(features.semanticSimilarity.getFeature())) {//&&
						//t.getClassification().equals("No")) {
						t.features.put(features.roleIsARelatedTo.getFeature(), 0.0);
						continue;
					}*/
					
					if (t.features.get(features.semanticSimilarity.getFeature()) >=
						maiorTfIdf.features.get(features.semanticSimilarity.getFeature()) &&
						(t.tokenWhoInfered.get(maiorTfIdf).relation.get(0).toLowerCase().equals("isa") ||
						t.tokenWhoInfered.get(maiorTfIdf).relation.get(0).toLowerCase().equals("relatedto")))
						t.features.put(features.roleIsARelatedTo.getFeature(), 1.0);
					else
						t.features.put(features.roleIsARelatedTo.getFeature(), 0.0);
					
					if (t.getClassification().equals("Yes")) {
						if (t.features.get(features.roleIsARelatedTo.getFeature()) == 1)
							countTagRelatedToIsA++;
						else {
							
							/*if (t.getLemma().equals("protein interaction") && (file.getName().equals("833421"))) {
								System.out.println("File: " + file.getName());
								for (Token aux : file.getTokens()) {
									if (aux.features.get(features.ofText.getFeature()) == 1 && 
										aux.features.get(features.tf_idf.getFeature()) > maiorTfIdf.features.get(features.tf_idf.getFeature()) &&
										aux.features.get(features.semanticSimilarity.getFeature()) <= t.features.get(features.semanticSimilarity.getFeature()))
										System.out.println(aux.getLemma() + " " + aux.features.get(features.tf_idf.getFeature()) + " " + aux.features.get(features.semanticSimilarity.getFeature()));
								}*/
								/*System.out.println(t.getLemma() + "(tag) " + file.getName());
								for (String f : t.features.keySet()) {
									if (f.equals("Keyphraseness"))
										System.out.println(f + ": " + t.features.get(f)*180);	
									else
										System.out.println(f + ": " + t.features.get(f));
								}
								System.out.println("Escolhido: " + maiorTfIdf.getLemma());
								
								System.out.println("Quem inferiu: ");
								for (Token aux : t.tokenWhoInfered.keySet()) {
									System.out.println(aux.getLemma() + " " + t.tokenWhoInfered.get(aux) + " " + aux.getClassification() + " TFxIDF: " +
											aux.features.get(features.tf_idf.getFeature()) + " Semantic: " + aux.features.get(features.semanticSimilarity.getFeature()) +
											" Keyphraseness: " + aux.features.get(features.keyphraseness.getFeature()));
								}*/
							}
						//}
					}
					else {
						if (t.features.get(features.roleIsARelatedTo.getFeature()) == 1) {
							countNonTagRelatedToIsA++;
						}
					}
					/*if (t.getClassification().equals("Yes")) {
						//t.features.put(features.roleIsARelatedTo.getFeature(), 1.0);
						//totalTag++;
						HashSet<String> roles = new HashSet<String>();
						for (Token aux : t.tokenWhoInfered.keySet()) {
							String role = t.tokenWhoInfered.get(aux).toLowerCase();
							roles.add(role);
							
							if (rolesTag.containsKey(role))
								rolesTag.put(role, rolesTag.get(role)+1);
							else
								rolesTag.put(role, 1);
						}
						
						if ((roles.contains("relatedto") || roles.contains("isa")) && 
								!roles.contains("capableofreceivingaction") && !roles.contains("capableof") &&
								!roles.contains("usedfor") && !roles.contains("propertyof") &&
								!roles.contains("subeventof") && !roles.contains("locationof") &&
								!roles.contains("partof") && !roles.contains("definedas") &&
								(!roles.contains("atlocation") && !roles.contains("motivationof"))) {
							countTagRelatedToIsA++;
							t.features.put(features.roleIsARelatedTo.getFeature(), 1.0);
						}
						else {
							t.features.put(features.roleIsARelatedTo.getFeature(), 0.0);
							
							if (t.features.get(features.roleIsARelatedTo.getFeature()) == 0) {
								System.out.println(t.getLemma() + "(tag): " + roles);
								for (String f : t.features.keySet()) {
									if (f.equals("Keyphraseness"))
										System.out.println(f + ": " + t.features.get(f)*180);	
									else
										System.out.println(f + ": " + t.features.get(f));
								}
								System.out.println("Quem inferiu: ");
								for (Token aux : t.tokenWhoInfered.keySet()) {
									System.out.println(aux.getLemma() + " " + t.tokenWhoInfered.get(aux) + " " + aux.getClassification() + " TFxIDF: " +
											aux.features.get(features.tf_idf.getFeature()) + " Semantic: " + aux.features.get(features.semanticSimilarity.getFeature()) +
											" Keyphraseness: " + aux.features.get(features.keyphraseness.getFeature()));
								}
							}
						}
					}
					else {
						totalNonTag++;
						//t.features.put(features.roleIsARelatedTo.getFeature(), 0.0);
						HashSet<String> roles = new HashSet<String>();
						for (Token aux : t.tokenWhoInfered.keySet()) {
							String role = t.tokenWhoInfered.get(aux).toLowerCase();
							roles.add(role);
							
							if (rolesNonTag.containsKey(role))
								rolesNonTag.put(role, rolesNonTag.get(role)+1);
							else
								rolesNonTag.put(role, 1);
						}
						
						//if (roles.contains("capableofreceivingaction") || roles.contains("capableof") || 
						//	roles.contains("usedfor") || roles.contains("propertyof") || roles.contains("subeventof"))
						//	countNonTagCapableOfReceivingAction++;
						//if (!roles.contains("capableofreceivingaction") && !roles.contains("capableof") &&  
						//	(roles.contains("relatedto") || roles.contains("isa")))
						//	countNonTagRelatedToIsA++;
						if ((roles.contains("relatedto") || roles.contains("isa")) && 
								!roles.contains("capableofreceivingaction") && !roles.contains("capableof") &&
								!roles.contains("usedfor") && !roles.contains("propertyof") &&
								!roles.contains("subeventof") && !roles.contains("locationof") &&
								!roles.contains("partof") && !roles.contains("definedas") &&
								(!roles.contains("atlocation") && !roles.contains("motivationof"))) {
							countNonTagRelatedToIsA++;
							t.features.put(features.roleIsARelatedTo.getFeature(), 1.0);
							//System.out.println(t.getLemma() + "(nao tag): " + roles);
						}
						else {
							t.features.put(features.roleIsARelatedTo.getFeature(), 0.0);
						}
					}*/
				}
				else
					//t.features.put(features.roleIsARelatedTo.getFeature(), 0.5);
					t.features.put(features.roleIsARelatedTo.getFeature(), Double.NaN);
			}
			file.getTokens().removeAll(removedToken);
		}
		
		/*System.out.println("TAGS: ");
		for (String role : rolesTag.keySet()) {
			System.out.println(role + ": " + rolesTag.get(role));
		}
		
		System.out.println("NON TAGS: ");
		for (String role : rolesNonTag.keySet()) {
			System.out.println(role + ": " + rolesNonTag.get(role));
		}*/
		
		//System.out.println("Tags: " + totalTag);
		//System.out.println("Total non tags: " + totalNonTag);
		
		System.out.println("countTagRelatedToIsA: " + countTagRelatedToIsA);
		//System.out.println("countTagCapableOfReceivingAction: " + countTagCapableOfReceivingAction);
		System.out.println("countNonTagRelatedToIsA: " + countNonTagRelatedToIsA);
		//System.out.println("countNonTagCapableOfReceivingAction: " + countNonTagCapableOfReceivingAction);
		System.out.println("Contagem: " + cont);
	}
	
	private static void addRelationInf(String token, String tokenRelation, String fileName, String role, FileData file, Token t) {
		if (file.getName().equals(fileName) && t.getLemma().equals(token)) {
			for (Token aux : file.getTokens()) {
				if (aux.getLemma().equals(tokenRelation)) {
					Relation rel = new Relation();
					rel.relation.add(role);
					t.tokenWhoInfered.put(aux, rel);
					aux.tokenInfered.put(t, rel.relation);
				}
			}
		}
	}
	
	private static void removeRelation(String token, String tokenRelation, FileData file) {
		for (Token aux : file.getTokens()) {
			if (aux.getLemma().equals(token)) {
				Token removed = null;
				for (Token who : aux.tokenWhoInfered.keySet()) {
					if (who.getLemma().equals(tokenRelation)) {
						removed = who;
					}
				}
				if (removed != null) {
					removed.tokenInfered.remove(aux);
					aux.tokenWhoInfered.remove(removed);
				}
			}
		}
	}
	
	private Hashtable<String, String> getTileFile() {
		Hashtable<String, String> titleFile = new Hashtable<String, String>();
		titleFile.put("43", "Reverse Engineering of Biological Complexity");
		titleFile.put("44", "Exploring complex networks");
		titleFile.put("47", "Exploring complex networks");
		titleFile.put("99", "Collective dynamics of `small-world' networks");
		titleFile.put("101", "Network Motifs: Simple Building Blocks of Complex Networks");
		titleFile.put("229", "Initial sequencing and comparative analysis of the mouse genome");
		titleFile.put("248", "Systems Biology: A Brief Overview Hiroaki");
		titleFile.put("249", "Computational systems biology");
		titleFile.put("272", "Genomic analysis of regulatory network dynamics reveals large topological changes");
		titleFile.put("292", "Functional genomic hypothesis generation and experimentation by a robot scientist");
		titleFile.put("2100", "Integrated Genomic and Proteomic Analyses of a Systematically Perturbed Metabolic Network");
		titleFile.put("2107", "Transcriptional Regulatory Networks in Saccharomyces cerevisiae");
		titleFile.put("3594", "Network motifs in integrated cellular networks of transcription≠regulation and protein≠protein interaction");
		titleFile.put("80410", "Functional imaging with cellular resolution reveals precise microarchitecture in visual cortex");
		titleFile.put("81501", "Community structure in social and biological networks");
		titleFile.put("84176", "Self-similarity of complex networks");
		titleFile.put("84309", "The economics of ideas and intellectual property");
		titleFile.put("86487", "Microarray analysis shows that some microRNAs downregulate large numbers of target mRNAs");
		titleFile.put("86865", "Neural correlates of decision variables in parietal cortex");
		titleFile.put("90558", "Emergence of Scaling in Random Networks");
		titleFile.put("98486", "Competing interests statement");
		titleFile.put("99033", "Whole-Genome Patterns of Common DNA Variation in Three Human Populations");
		titleFile.put("100166", "Inferring Cellular Networks Using Probabilistic Graphical Models");
		titleFile.put("101973", "Different time courses of learning-related activity in the prefrontal cortex and striatum");
		titleFile.put("106364", "Systematic discovery of regulatory 0 motifs in human promoters and 3 UTRs by comparison of several mammals");
		titleFile.put("112878", "Ultraconserved Elements in the Human Genome");
		titleFile.put("114199", "What Shanl We Mam?");
		titleFile.put("118649", "Evidence that microRNA precursors, unlike other non-coding RNAs, have lower folding free energies than random sequences");
		titleFile.put("118727", "Vienna RNA secondary structure server");
		titleFile.put("118744", "Pfold: RNA secondary structure prediction using stochastic context-free grammars");
		titleFile.put("121661", "Adaptive Coding of Reward Value by Dopamine Neurons");
		titleFile.put("126997", "The protein structure prediction problem could be solved using the current PDB library");
		titleFile.put("136657", "Accomplishments and challenges in literature data mining for biology");
		titleFile.put("137111", "Genome-wide non-mendelian inheritance of extra-genomic information in Arabidopsis");
		titleFile.put("141524", "Gene Regulation at the Single-Cell Level Nitzan");
		titleFile.put("141840", "Cluster analysis and display of genome-wide expression patterns");
		titleFile.put("142323", "Statistical analysis of domains in interacting protein pairs");
		titleFile.put("142488", "Noise Propagation in Gene Networks");
		titleFile.put("150261", "Prediction and statistics of pseudoknots in RNA structures using exactly clustered stochastic simulations");
		titleFile.put("155900", "Do 15-Month-Old Infants Understand False Beliefs?");
		titleFile.put("211497", "The evolution of syntactic communication");
		titleFile.put("226992", "Lowland Forest Loss in Protected Areas of Indonesian");
		titleFile.put("227153", "Initial sequencing and analysis of the human genome");
		titleFile.put("227174", "Evidence for dynamically organized modularity in the yeast protein≠protein interaction network");
		titleFile.put("231294", "MrBayes 3: Bayesian phylogenetic inference under mixed models");
		titleFile.put("238188", "Gapped BLAST and PSI-BLAST: a new generation of protein database search programs");
		titleFile.put("239528", "Exploration, normalization, and summaries of high density oligonucleotide array probe level data");
		titleFile.put("239569", "Service-Oriented Science");
		titleFile.put("239581", "Probing the Local Dynamics of Nucleotide-Binding Pocket Coupled to the Global Dynamics: Myosin versus Kinesin");
		titleFile.put("241030", "Statistical significance for genomewide studies");
		titleFile.put("261639", "Dopamine Cells Respond to Predicted Events during Classical Conditioning: Evidence for Eligibility Traces in the Reward-Learning Network");
		titleFile.put("265789", "The Faculty of Language: What Is It, Who Has It, and How Did It Evolve?");
		titleFile.put("272363", "A survey of current work in biomedical text mining");
		titleFile.put("302050", "Hierarchical Organization of Modularity in Metabolic Networks");
		titleFile.put("303213", "Join a social revolution");
		titleFile.put("303889", "An Experimental Study of Search in Global Social Networks");
		titleFile.put("307461", "Network motifs in the transcriptional regulation network of Escherichia coli");
		titleFile.put("309778", "A network-based analysis of systemic inflammation in humans");
		titleFile.put("312119", "Rule Learning by Seven-Month-Old Infants");
		titleFile.put("312124", "Statistical Learning by 8-Month-Old Infants");
		titleFile.put("312476", "Unsupervised learning of natural languages");
		titleFile.put("329170", "Evolutionary information for specifying a protein fold");
		titleFile.put("332150", "The Transcriptional Landscape of the Mammalian Genome");
		titleFile.put("332173", "Evolutionarily Conserved Pathways of Energetic Connectivity in Protein Families");
		titleFile.put("333353", "Spontaneous evolution of modularity and network motifs");
		titleFile.put("334264", "Towards a proteome-scale map of the human protein≠protein interaction network");
		titleFile.put("341252", "Bayesian statistical analysis of protein side-chain rotamer preferences");
		titleFile.put("353537", "Computational roles for dopamine in behavioural control");
		titleFile.put("353538", "Cortical rewiring and information storage");
		titleFile.put("354027", "Interrogating protein interaction networks through structural biology");
		titleFile.put("355573", "Activity of striatal neurons reflects dynamic encoding and recoding of procedural memories");
		titleFile.put("355574", "Natural selection on protein-coding genes in the human genome");
		titleFile.put("363614", "Superfamilies of Evolved and Designed Networks");
		titleFile.put("375823", "Stabilization Wedges: Solving the Climate Problem for the Next 50 Years with Current Technologies");
		titleFile.put("400238", "Formation of a Motor Memory by Action Observation");
		titleFile.put("406519", "Design principles of a bacterial signalling network");
		titleFile.put("407273", "A data integration methodology for systems biology");
		titleFile.put("415502", "The Bioperl Toolkit: Perl Modules for the Life Sciences");
		titleFile.put("420465", "Evolutionarily conserved elements in vertebrate, insect, worm, and yeast genomes");
		titleFile.put("422950", "Let data speak to data");
		titleFile.put("430079", "Do We Know What the Early Visual System Does?");
		titleFile.put("437770", "RNAsoft: a suite of RNA secondary structure prediction and design software tools");
		titleFile.put("438129", "Internet encyclopaedias go head to head");
		titleFile.put("444860", "Genome sequence of the Brown Norway rat yields insights into mammalian evolution");
		titleFile.put("446839", "What does the photoblog want?");
		titleFile.put("454555", "Automated De Novo Identification of Repeat Sequence Families in Sequenced Genomes");
		titleFile.put("459365", "Empirical Analysis of an Evolving Social Network");
		titleFile.put("460153", "The Origins of Eukaryotic Gene Structure");
		titleFile.put("465989", "Neural Systems Responding to Degrees of Uncertainty in Human Decision-Making");
		titleFile.put("466050", "Evolutionary changes in cis and trans gene regulation");
		titleFile.put("466068", "Transcriptional regulatory code of a eukaryotic genome");
		titleFile.put("477450", "Proteome survey reveals modularity of the yeast cell machinery");
		titleFile.put("478707", "Lethality and centrality in protein networks");
		titleFile.put("482101", "Defining the sequence-recognition profile of DNA-binding molecules");
		titleFile.put("499796", "Introduction: The Internet in Everyday Life");
		titleFile.put("504894", "Gene expression proÆling predicts clinical outcome of breast cancer");
		titleFile.put("506455", "A bottom-up approach to gene regulation");
		titleFile.put("506468", "The primate amygdala represents the positive and negative value of visual stimuli during learning");
		titleFile.put("507525", "Causal Reasoning in Rats");
		titleFile.put("507926", "On Making the Right Choice: The Deliberation-Without-Attention Effect");
		titleFile.put("509425", "Detection of a direct carbon dioxide effect in continental river runoff records");
		titleFile.put("516580", "Fission-track ages of stone tools and fossils on the east Indonesian island of Flores");
		titleFile.put("523878", "Elucidation of the Small RNA Component of the Transcriptome");
		titleFile.put("525366", "Gene set enrichment analysis: A knowledge-based approach for interpreting genome-wide expression profiles");
		titleFile.put("528160", "A gene atlas of the mouse and human protein-encoding transcriptomes");
		titleFile.put("540889", "Expression profiling in primates reveals a rapid evolution of human transcription factors");
		titleFile.put("546157", "An fMRI Investigation of Emotional Engagement in Moral Judgment");
		titleFile.put("549806", "Parameter Estimation in Biochemical Pathways: A Comparison of Global Optimization Methods");
		titleFile.put("553494", "Stochastic protein expression in individual cells at the single molecule level");
		titleFile.put("553497", "Multiple rounds of speciation associated with reciprocal gene loss in polyploid yeasts");
		titleFile.put("559064", "Gene Regulatory Networks and the Evolution of Animal Body Plans");
		titleFile.put("560813", "Global landscape of protein complexes in the yeast Saccharomyces cerevisiae");
		titleFile.put("571538", "Genome-Wide Detection of Polymorphisms at Nucleotide Resolution with a Single DNA Microarray");
		titleFile.put("602903", "Startling starlings");
		titleFile.put("609199", "Numerical Cognition Without Words: Evidence from Amazonia");
		titleFile.put("612608", "Statistical and Bayesian approaches to RNA secondary structure prediction");
		titleFile.put("613191", "Integrated analysis of regulatory and metabolic networks reveals novel regulatory mechanisms in Saccharomyces cerevisiae");
		titleFile.put("620656", "Genetic Dissection of Transcriptional Regulation in Budding Yeast");
		titleFile.put("622483", "Predicting stochastic gene expression dynamics in single cells");
		titleFile.put("656280", "Apes Save Tools for Future Use");
		titleFile.put("668899", "A simple rule for the evolution of cooperation on graphs and social networks");
		titleFile.put("668933", "WHAT IS A GENE?");
		titleFile.put("698675", "Close sequence comparisons are sufficient to identify human cis -regulatory elements");
		titleFile.put("702349", "Cortical substrates for exploratory decisions in humans");
		titleFile.put("738207", "Citation Analysis and Discourse Analysis Revisited");
		titleFile.put("740681", "Usage patterns of collaborative tagging systems");
		titleFile.put("771131", "Agent-Specific Responses in the Cingulate Cortex During Economic Exchanges");
		titleFile.put("771144", "A New RNA Dimension to Genome Control");
		titleFile.put("771168", "Food-Caching Western Scrub-Jays Keep Track of Who Was Watching When");
		titleFile.put("778023", "Reducing the Dimensionality of Data with Neural Networks");
		titleFile.put("802975", "Reward-Related Cortical Inputs Define a Large Striatal Region in Primates That Interface with Associative Cortical Connections, Providing a Substrate for Incentive-Based Learning");
		titleFile.put("833421", "Global topological features of cancer proteins in the human interactome");
		titleFile.put("880918", "The Connectivity Map: Using Gene-Expression Signatures to Connect Small Molecules, Genes, and Disease");
		titleFile.put("904109", "Genetics of global gene expression");
		titleFile.put("937059", "A protein interaction network for pluripotency of embryonic stem cells");
		titleFile.put("957831", "Global variation in copy number in the human genome");
		titleFile.put("1016800", "Relating Three-Dimensional Structures to Protein Networks Provides Evolutionary Insights ");
		titleFile.put("1036681", "DNA shuffling by random fragmentation and reassembly: In vitro recombination for molecular evolution");
		titleFile.put("1067802", "Infotaxis as a strategy for searching without gradients");
		titleFile.put("1097231", "Relative Impact of Nucleotide and Copy Number Variation on Gene Expression Phenotypes ");
		titleFile.put("1116998", "How to infer gene networks from expression profiles");
		titleFile.put("1133633", "Multiple structural alignment and clustering of RNA sequences");
		titleFile.put("1137519", "How Web 2.0 is changing medicine");
		titleFile.put("1144477", "Small-World Anatomical Networks in the Human Brain Revealed by Cortical Thickness from MRI");
		titleFile.put("1159615", "Network-based prediction of protein function");
		titleFile.put("1197981", "Top-Down Versus Bottom-Up Control of Attention in the Prefrontal and Posterior Parietal Cortices");
		titleFile.put("1202726", "Emergent Biogeography of Microbial Communities in a Model Ocean");
		titleFile.put("1206611", "Quantifying social group evolution");
		titleFile.put("1226851", "Bayesian methods in bioinformatics and computational systems biology");
		titleFile.put("1272477", "Drought sensitivity shapes species distribution patterns in tropical forests");
		titleFile.put("1272533", "Error bars in experimental biology");
		titleFile.put("1307464", "Network motifs: theory and experimental approaches");
		titleFile.put("1320727", "The human disease network");
		titleFile.put("1322799", "Time Discounting for Primary Rewards");
		titleFile.put("1322886", "Global and regional drivers of accelerating CO2 emissions");
		titleFile.put("1336057", "RNA Maps Reveal New RNA Classes and a Possible Function for Pervasive Transcription ");
		titleFile.put("1362387", "Genome-Wide Mapping of in Vivo Protein-DNA Interactions");
		titleFile.put("1388250", "Identification and analysis of functional elements in 1% of the human genome by the ENCODE pilot project");
		titleFile.put("1391621", "Neural Mechanisms of Visual Attention: How Top-Down Feedback Highlights Relevant Locations ");
		titleFile.put("1392584", "Large-Scale Gamma-Band Phase Synchronization and Selective Attention");
		titleFile.put("1392792", "What is a gene, post-ENCODE? History and updated definition");
		titleFile.put("1418865", "Lateral Habenula Stimulation Inhibits Rat Midbrain Dopamine Neurons through a GABAA Receptor-Mediated Mechanism");
		titleFile.put("1453145", "The folksonomy tag cloud: when is it useful?");
		titleFile.put("1465869", "The effect of ancient population bottlenecks on human phenotypic variation");
		titleFile.put("1532668", "The Role of the Dorsal Striatum in Reward and Decision-Making");
		titleFile.put("1551105", "Divergence of Transcription Factor Binding Sites Across Related Yeast Species");
		titleFile.put("1568644", "Correlation between neural spike trains increases with firing rate");
		titleFile.put("1602005", "Tools for visually exploring biological networks");
		titleFile.put("1610049", "Widespread Lateral Gene Transfer from Intracellular Bacteria to Multicellular Eukaryotes");
		titleFile.put("1610369", "Localization of a Stable Neural Correlate of Associative Memory");
		titleFile.put("1624776", "Natural history and evolutionary principles of gene duplication in fungi");
		titleFile.put("1631613", "Impact of anthropogenic atmospheric nitrogen and sulfur deposition on ocean acidification and the inorganic carbon system");
		titleFile.put("1632947", "The Perception of Rational, Goal-Directed Action in Nonhuman Primates");
		titleFile.put("1752368", "Quantifying the evolutionary dynamics of language");
		titleFile.put("1794647", "High-resolution structure prediction and the crystallographic phase problem");
		titleFile.put("1880339", "Discovery of functional elements in 12 Drosophila genomes using evolutionary signatures");
		titleFile.put("1880603", "Evolution of genes and genomes on the Drosophila phylogeny");
		titleFile.put("1910555", "Programming gene expression with combinatorial promoters");
		titleFile.put("1926414", "Fast-Forward Playback of Recent Memory Sequences in Prefrontal Cortex During Sleep");
		titleFile.put("1989097", "Distinguishing protein-coding and noncoding genes in the human genome");
		titleFile.put("2163327", "Web 3.0 and medicine");
		titleFile.put("2235507", "On the relation between promoter divergence and gene expression evolution");
		titleFile.put("2288308", "Alignment Uncertainty and Genomic Analysis");
		return titleFile;
	}
}
