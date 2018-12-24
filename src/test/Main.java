package test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.management.relation.Role;

import knowledgeBase.conceptNet.ConceptNet;
import knowledgeBase.inferenceNet.InferenceNet;
import br.com.informar.knowledgebase.KnowledgeBase;
import br.com.informar.knowledgebase.RelatednessCache;
import br.com.informar.knowledgebase.db.MongoDB;
import br.com.informar.knowledgebase.model.Concept;
import br.com.informar.knowledgebase.model.Mention;
import config.Config;
import maui.filters.MauiPhraseFilter;
import maui.stopwords.Stopwords;
import maui.stopwords.StopwordsEnglish;
import classifier.RF;
//import edu.stanford.nlp.ling.CoreAnnotations.TokenEndAnnotation;
import features.Features;
import features.Features.features;
import features.Keyphraseness;
import features.SemanticSimilarity;
import features.SintaticFeatures;
import features.TermPredictiability;
import features.WikipediaKeyphraseness;
import preprocessing.AddInferenceNet;
import preprocessing.Balance;
import preprocessing.FormatFile;
import preprocessing.ReadData;
import util.FileData;
import util.Relation;
import util.Token;
import util.WriteArff;
import weka.core.Instance;

public class Main {

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
	
	public static void getTermsConceptNet(ArrayList<FileData> listFiles) {
		//obtem os termos da conceptNet
		for (FileData file : listFiles) {
			ArrayList<Token> removedTokens = new ArrayList<Token>();
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 0.0D)
					removedTokens.add(token);
				token.tokenInfered.clear();
				token.tokenWhoInfered.clear();
			}
			file.getTokens().removeAll(removedTokens);
		}
		
		new ConceptNet().getConcepts(listFiles);
		System.out.println("Finished ConceptNet...");
		
		System.out.println("Showing relations...");
		for (FileData file : listFiles) {
			for (Token t : file.getTokens()) {
				if (t.features.get(features.ofText.getFeature()) == 1) {
					for (Token aux : t.tokenInfered.keySet()) {
						for (String relation : t.tokenInfered.get(aux)) {
							System.out.println(t.getLemma() + " -> " + relation + " -> " + aux.getLemma());
						}
					}
				}
			}
		}
	}
	
	public static void countTags(ArrayList<FileData> listFiles) {
		int tags = 0, tagsText = 0, tagsNonText = 0;
		int tokens = 0, tokensText = 0, tokensNonText = 0;
		for (FileData file : listFiles) {
			tags += file.getTags().size();
			tokens += file.getTokens().size();
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 0.0D) {
					if (token.getClassification().equals("Yes"))
						tagsNonText++;
					else
						tokensNonText++;
				}
				else {
					if (token.getClassification().equals("Yes"))
						tagsText++;
					else
						tokensText++;
				}
			}
		}
		
		System.out.println("TAGS: " + tags + " (Text: " + tagsText + " Non Text: " + tagsNonText + ")");
		System.out.println("TOKENS: " + tokens + " (Text: " + tokensText + " Non Text: " + tokensNonText + ")");
	}
	
	public static void ajustaTagsNonText(ArrayList<FileData> listFiles) {
		for (FileData file : listFiles) {
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 0.0 &&
					(file.tagsOriginalFile.containsKey(token.getLemma()))) {
					token.setClassification("Yes");
				}
			}
		}
	}
	
	public static void checksRelations(ArrayList<FileData> listFiles) {
		
		Hashtable<String, Integer> relationsTags = new Hashtable<String, Integer>();
		Hashtable<String, Integer> relationsTokens = new Hashtable<String, Integer>();
		
		for (FileData file : listFiles) {
			//ArrayList<Token> removedToken = new ArrayList<Token>();
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 0) {
					Token maiorTfIdf = null;
					//ArrayList<Token> removedWho = new ArrayList<Token>();
					for (Token aux : token.tokenWhoInfered.keySet()) {
						
						if (maiorTfIdf == null) { maiorTfIdf = aux; continue; }
						if (aux.features.get(features.tf_idf.getFeature()) > maiorTfIdf.features.get(features.tf_idf.getFeature()))
							maiorTfIdf = aux;
						
						/*token.tokenWhoInfered.get(aux).remove("RelatedTo");
						if (token.tokenWhoInfered.get(aux).size() == 0)
							removedWho.add(aux);*/
						
						/*for (String relation : token.tokenWhoInfered.get(aux)) {
							
							if (token.getClassification().equals("Yes")) {
								if (relationsTags.containsKey(relation))
									relationsTags.put(relation, relationsTags.get(relation) + 1);
								else
									relationsTags.put(relation, 1);
							}
							else {
								if (relationsTokens.containsKey(relation))
									relationsTokens.put(relation, relationsTokens.get(relation) + 1);
								else
									relationsTokens.put(relation, 1);
							}							
						}*/				
					}
					//if (removedWho.size() > 0)
					//	for (Token aux1 : removedWho)
					//		token.tokenWhoInfered.remove(aux1);
					
					//if (token.tokenWhoInfered.size() == 0)
					//	removedToken.add(token);
					
					if (token.getClassification().equals("Yes")) {
						if (relationsTags.containsKey(token.tokenWhoInfered.get(maiorTfIdf).relation.get(0)))
							relationsTags.put(token.tokenWhoInfered.get(maiorTfIdf).relation.get(0), relationsTags.get(token.tokenWhoInfered.get(maiorTfIdf).relation.get(0)) + 1);
						else
							relationsTags.put(token.tokenWhoInfered.get(maiorTfIdf).relation.get(0), 1);
					}
					else {
						if (relationsTokens.containsKey(token.tokenWhoInfered.get(maiorTfIdf).relation.get(0)))
							relationsTokens.put(token.tokenWhoInfered.get(maiorTfIdf).relation.get(0), relationsTokens.get(token.tokenWhoInfered.get(maiorTfIdf).relation.get(0)) + 1);
						else
							relationsTokens.put(token.tokenWhoInfered.get(maiorTfIdf).relation.get(0), 1);
					}
				}
			}
			//file.getTokens().removeAll(removedToken);
		}
		
		//System.out.println(relationsTags);
		
		System.out.println("Tags: ");
		Object[] valores = relationsTags.values().toArray();
		Arrays.sort(valores);
		Object valorAntigo = -1;
		for (Object v : valores) {
			if (v == valorAntigo) continue;
			valorAntigo = v;
			for (String r : relationsTags.keySet()) {
				if (relationsTags.get(r) == v)
					System.out.println(r + ": " + v);
			}
		}
		
		System.out.println("Tokens: ");
		Object[] valoresTokens = relationsTokens.values().toArray();
		Arrays.sort(valoresTokens);
		for (Object v : valoresTokens) {
			for (String r : relationsTokens.keySet()) {
				if (relationsTokens.get(r) == v)
					System.out.println(r + ": " + v);
			}
		}
	}
	
	public static void main(String[] args) {
		
		//verificacaoFeatures();
		/*ArrayList<FileData> listFiles2 = new FormatFile().readFormatFiles("data/newTest/old/correct_features6/");
		
		//Remove tokens stop words ou tamanho maior que 3 ou inicia ou termina com stop word
		Stopwords stopwords = new StopwordsEnglish();
		int qttyTokens = 0;
		for (FileData file : listFiles2) {
			ArrayList<Token> tokenRemoved = new ArrayList<Token>();
			for (Token t : file.getTokens()) {
				//if (t.features.get(features.ofText.getFeature()) == 0 && t.getClassification().equals("No")) {
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
			qttyTokens += file.getTokens().size();
		}*/        
		
		SemanticSimilarity sem = new SemanticSimilarity();
		//ArrayList<FileData> aux = new FormatFile().readFormatFiles("data/newTest/old/conceptNet3FeaturesSintSem/");
		HashSet<String> filesWrited = new HashSet<String>();
		//for (FileData f : aux) 
		//	filesWrited.add(f.getName());
		
		ArrayList<FileData> listFiles3 = new FormatFile().readFormatFiles("data/newTest/old/teste/");
		
		/*int cont = 0;
		for (FileData file : listFiles3) {
			for (Token t : file.getTokens()) {
				if (t.features.containsKey(features.semanticSimilarity.getFeature()) && t.features.get(features.semanticSimilarity.getFeature()) > 0 &&
					t.features.get(features.ofText.getFeature())==1)
					cont++;
			}
		}
		System.out.println(cont);*/
		
		/*int qttyTokens = 0;
		Stopwords stopwords = new StopwordsEnglish();
		for (FileData file : listFiles3) {
			ArrayList<Token> tokenRemoved = new ArrayList<Token>();
			for (Token t : file.getTokens()) {
				//if (t.features.get(features.ofText.getFeature()) == 0 && t.getClassification().equals("No")) {
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
						try {
						if (stopwords.isStopword(buffer[0]) || (pos > 0 && stopwords.isStopword(buffer[pos-1])))
							tokenRemoved.add(t);
						} catch (Exception e) { System.out.println(pos); }
					}
				}
			}
			file.getTokens().removeAll(tokenRemoved);
			qttyTokens += file.getTokens().size();
		}
		System.out.println("Qtde de tokens: " + qttyTokens);*/
		
		//ajustaTagsNonText(listFiles3);
		//checksRelations(listFiles3);
		//countTags(listFiles3);
		
		
		//Keyphraseness key = new Keyphraseness();
		//SintaticFeatures sint = new SintaticFeatures();
		//WikipediaKeyphraseness wikip = new WikipediaKeyphraseness();
		
		/*Hashtable<String, Integer> frequencyTags = new Hashtable<String, Integer>();
		for (FileData file : listFiles3) {
			for (Token token : file.getTags()) {
				if (frequencyTags.containsKey(token.getLemma()))
					frequencyTags.put(token.getLemma(), frequencyTags.get(token.getLemma())+1);
				else
					frequencyTags.put(token.getLemma(), 1);
			}
		}*/
		
		System.out.println("Prepando para obter os conceitos dos tokens...");
		int i = filesWrited.size();
		for (FileData file : listFiles3) {
			if (filesWrited.contains(file.getName())) continue;
			System.out.println("File: " + ++i + " (" + file.getTokens().size() + ")");
			sem.getRelationsWikipediaLast(file);
			int j = 0;
			for (Token token : file.getTokens()) {
				System.out.println(++j);
				//if (token.features.get(features.ofText.getFeature()) == 0) {
					sem.calculateSimilaritySemanticLast(token);
					sem.calculateNodeDegreeLast(token);
					sem.calculatesInverseWikipediaLinkageLast(token);
					//token.features.put(features.keyphraseness.getFeature(), frequencyTags.containsKey(token.getLemma()) ? (double)frequencyTags.get(token.getLemma())/listFiles3.size() : 0.0);
					//token.features.put(features.phraseLenght.getFeature(), sint.calculatePhraseLenght(token));
					//sem.calculateWikipediaKeyphrasenessLast(token);
				//}
			}
			new FormatFile().writeFormatFileByFile(file, "data/newTest/old/conceptNet3FeaturesSintSem/");
		}
		
		for (FileData file : listFiles3) {
			for (Token t : file.getTokens()) {
				System.out.println(t.getLemma() + " -> " + t.features.get(features.ofText.getFeature()) + "; " + t.features.get(features.semanticSimilarity.getFeature()));
			}
		}
		
		
		/*for (FileData file : listFiles2) {
			ArrayList<Token> removedTokens = new ArrayList<Token>();
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 0 &&
					token.tokenWhoInfered.size() == 0)
					removedTokens.add(token);
				
				if (token.features.get(features.ofText.getFeature()) == 0 &&
					token.getClassification().equals("No")){
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
			file.getTokens().removeAll(removedTokens);
		}*/
		
		
		//checksRoles(listFiles2);
		
		/*ArrayList<FileData> listFilesAux = new FormatFile().readFormatFiles("data/newTest/old/correct_features6/");
		HashSet<String> reads = new HashSet<String>();
		for (FileData file : listFilesAux) {
			reads.add(file.getName());
		}*/
		
		//verifica relacoes inferenceNet
		/*int semRelacao = 0;
		for (FileData file : listFiles2) {
			for (Token t : file.getTokens()) {
				if (t.features.get(features.ofText.getFeature()) == 0) {
					for (Token aux : t.tokenWhoInfered.keySet()) {
						if (t.tokenWhoInfered.get(aux).equals("")) {
							semRelacao++;
							System.out.println(t.getLemma() + " + " + aux.getLemma() + ": " + t.tokenWhoInfered.get(aux));
						}
					}
				}
			}
		}
		System.out.println("Sem relação: " + semRelacao);*/
		
		//Gets role terms infered
		/*int encontrado = 0;
		InferenceNet inf = InferenceNet.getInstance();
		int n = reads.size();
		for (FileData file : listFiles2) {
			if (reads.contains(file.getName())) continue;
			System.out.println(++n);
			for (Token t : file.getTokens()) {
				if (t.tokenInfered != null && t.tokenInfered.size() > 0) {
					ArrayList<Token> tokensFound = inf.getTermsIsAInferenceNet(t);
					for (Token tokenFound : tokensFound) {
						for (Token tokenInf : t.tokenInfered.keySet()) {
							if (tokenFound.getLemma().replace("-", " ").equals(tokenInf.getLemma().replace("-", " "))) {
								encontrado++;
								t.tokenInfered.put(tokenInf, tokenFound.role);
								//System.out.println("Encontrado " + t.getLemma() + " + " + tokenFound.getLemma() + ": " + tokenFound.role);
								
								for (Token tWho : tokenInf.tokenWhoInfered.keySet()) {
									if (tWho.getLemma().replace("-", " ").equals(t.getLemma().replace("-", " "))) {
		
										tokenInf.tokenWhoInfered.put(tWho, tokenFound.role);
										break;
									}
								}
								break;
							}
						}
					}
				}
			}
			new FormatFile().writeFormatFileByFile(file, "data/newTest/old/correct_features6/");
		}
		
		System.out.println("Encontrou inferenceNet: " + encontrado);*/
		
		//Checks the tokens that exists in semantic and not exists in correct_features_2
		/*ArrayList<FileData> listFiles = new FormatFile().readFormatFiles("data/newTest/old/semantic/");
		SintaticFeatures sint = new SintaticFeatures();
		Keyphraseness keyp = new Keyphraseness();
		//SemanticSimilarity sem = new SemanticSimilarity();
		
		//List<Concept> concepts = sem.knowledgeBase.getConcepts().findAll();
		//Hashtable<String, Concept> listConcepts = new Hashtable<String, Concept>();
		//for (Concept con : concepts) {
		//	listConcepts.put(con.getName(), con);
		//}
		
		//System.out.println(concepts.size());
		
		int count = 0;
		int foundT = 0;
		for (FileData file2 :  listFiles2) {
			for (FileData file : listFiles) {
				if (file2.getName().equals(file.getName())) {
					for (Token token : file.getTokens()) {
						boolean found = false;
						for (Token token2 : file2.getTokens()) {
							if (token.getLemma().replace("-", " ").equals(token2.getLemma())) {
								found = true;
								break;
							}
						}
						if (!found) {
							token.setName(token.getName().replace("-", ""));
							token.setFrequencyDoc(token.getBeginIndex() != token.getEndIndex() ? 5 : 1);
							token.features.put(features.firstOccurrence.getFeature(), sint.calculateFirstOccurrence(token, file));
							token.features.put(features.tf_idf.getFeature(), sint.calculateTFIDF(token, listFiles2, file, null));
							token.features.put(features.spread.getFeature(), sint.calculateSpread(token));
							token.features.put(features.phraseLenght.getFeature(), sint.calculatePhraseLenght(token));
							token.features.put(features.keyphraseness.getFeature(), keyp.calculateKeyphraseness(token, listFiles2));
							
							/*Token tokenFound = null;
							for (FileData fileAux : listFiles2) {
								for (Token tokenAux : fileAux.getTokens()) {
									if (tokenAux.getLemma().equals(token.getLemma().replace("-", " "))) {
										tokenFound = tokenAux.clone();
										break;
									}
								}
								if (tokenFound != null) {
									token.conceptWikipedia = tokenFound.conceptWikipedia;
									token.features.put(features.inverseWikipediaLinkade.getFeature(), tokenFound.features.get(features.inverseWikipediaLinkade.getFeature()));
									token.features.put(features.wikipediaKeyphraseness.getFeature(), tokenFound.features.get(features.wikipediaKeyphraseness.getFeature()));
									if (token.conceptWikipedia != null && !token.conceptWikipedia.equals(""))
										sem.calculateNodeDegreeToken(file, tokenFound, listConcepts);
									foundT++;
									break;
								}*/
							//}
							
							//token.features.put(features.semanticSimilarity.getFeature(), 0.0);
							//if (tokenFound == null) {
								//token.features.put(features.inverseWikipediaLinkade.getFeature(), 0.0);
								//token.features.put(features.wikipediaKeyphraseness.getFeature(), 0.0);
								//token.features.put(features.nodeDegree.getFeature(), 0.0);
							//}
							
							//file2.getTokens().add(token);
							/*count++;
						}
					}
				}
			}
		}
		System.out.println("Qtde: " + count);
		System.out.println("FoundT: " + foundT);*/
		
		//obtem os valores das features sintaticas dos termos inferidos
		/*for(FileData file : listFiles2) {
			for (Token t : file.getTokens()) {
				if (t.features.get(features.ofText.getFeature()) == 0) {
					double firstOcc = 0, tf_idf = 0, spread = 0;
					for (Token aux : t.tokenWhoInfered.keySet()) {
						//firstOcc += aux.features.get(features.firstOccurrence.getFeature());
						//tf_idf += aux.features.get(features.tf_idf.getFeature());
						//spread += aux.features.get(features.spread.getFeature());
						
						//atribui o valor das features sintaticas do ultimo termo que inferiu 
						//t.features.put(features.firstOccurrence.getFeature(), aux.features.get(features.firstOccurrence.getFeature()));
						//t.features.put(features.tf_idf.getFeature(), aux.features.get(features.tf_idf.getFeature()));
						//t.features.put(features.spread.getFeature(), aux.features.get(features.spread.getFeature()));
						//break;
						//t.features.put(features.tf_idf.getFeature(), 1.0);
					}
					//t.features.put(features.firstOccurrence.getFeature(), Instance.missingValue());
					//t.features.put(features.spread.getFeature(), Instance.missingValue());
					t.features.put(features.firstOccurrence.getFeature(), firstOcc/t.tokenWhoInfered.size());
					t.features.put(features.tf_idf.getFeature(), tf_idf/t.tokenWhoInfered.size());
					t.features.put(features.spread.getFeature(), spread/t.tokenWhoInfered.size());
					//t.features.put(features.firstOccurrence.getFeature(), Instance.missingValue());
					//t.features.put(features.tf_idf.getFeature(), Instance.missingValue());
					//t.features.put(features.spread.getFeature(), Instance.missingValue());
				}
			}
		}
		
		//remove the terms of text
		/*for (FileData file : listFiles2) {
			ArrayList<Token> tokensRemoved = new ArrayList<Token>();
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 0)
					tokensRemoved.add(token);
			}
			file.getTokens().removeAll(tokensRemoved);
		}*/
		
		/*ArrayList<FileData> removedFiles = new ArrayList<FileData>();
		for (FileData file : listFiles2) {
			if (file.getTokens().size() == 0)
				removedFiles.add(file);
		}
		listFiles2.removeAll(removedFiles);*/
		
		
		//Adds the missing value to terms that non exists in the Wikipedia 
		/*for (FileData file : listFiles2) {
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
		
		//atribui peso para as relacoes inferidas
		/*int n = 0;
		InferenceNet inf = InferenceNet.getInstance();
		for (FileData file : listFiles2) {
			System.out.println(++n);
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 0) {
					double weightRole = 0.0D;
					ArrayList<String> roles = new ArrayList<String>();
					for (Token tInf : token.tokenWhoInfered) {
						roles.addAll(inf.getRelation(token, tInf));
					}
					for (String role : roles) {
						switch (role.toLowerCase()) {
						case "isa": 
						case "synonym":
						case "definedas":
						{
							weightRole += 1;
							break;
						}
						case "propertyof": 
						case "relatedto": {
							weightRole += 0.75;
							break;
						}
						case "partof": 
						{
							weightRole += 0.5;
							break;
						}
						default:
							weightRole += 0.1;
							break;
						}
					}
					weightRole /= roles.size();
					token.features.put(features.weightRole.getFeature(), weightRole);
				}
				else
					token.features.put(features.weightRole.getFeature(), Instance.missingValue());
			}
			new FormatFile().writeFormatFileByFile(file, "data/newTest/old/correct_features3/");
		}*/
		
		//remove the terms of the text
		/*System.out.println("Removing text terms...");
		for (FileData file : listFiles2) {
			ArrayList<Token> tokensRemoved = new ArrayList<Token>();
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 1.0D)
					tokensRemoved.add(token);
			}
			file.getTokens().removeAll(tokensRemoved);
		}
		
		ArrayList<FileData> filesRemoved = new ArrayList<FileData>();
		for (FileData file : listFiles2) {
			if (file.getTokens().size() == 0)
				filesRemoved.add(file);
		}
		listFiles2.removeAll(filesRemoved);*/
		
		/*System.out.println("Normaling data");
		new Features().normalizeDataFeatures(listFiles2);
		
		System.out.println("Writing arff...");
		try {
			new WriteArff().createFolders("citeULike");
			new WriteArff().writeFiles(listFiles2, "citeULike");
		} catch (Exception e) { e.printStackTrace(); }*/
		
		//TermPredictiability term = new TermPredictiability(listFiles2);
		//term.calculatePredictiability();
		
		//ArrayList<FileData> listFileNoBalance = (ArrayList<FileData>)listFiles2.clone();
		//System.out.println("Term predictiabily test...");
		//term.calculatePrectiabilityTest(listFileNoBalance);
		
		/*System.out.println("Classifing...");
		new RF().runRFCrossValidationDoc(listFiles2, listFileNoBalance);
		System.out.println("Finished!!!");*/
		
		//Gets list tokens by file in hastable
		/*Hashtable<String, HashSet<String>> listTokens = new Hashtable<String, HashSet<String>>();
		for(FileData file : listFiles2) {
			HashSet<String> tokens = new HashSet<String>();
			for (Token token : file.getTokens()) {
				tokens.add(token.getLemma());
			}
			listTokens.put(file.getName(), tokens);
		}
		
		//Recalculate TFxIDF according Maui
		SintaticFeatures sint = new SintaticFeatures();
		for (FileData file : listFiles2) {
			for (Token token : file.getTokens()) {
				token.features.put(features.tf_idf.getFeature(), sint.calculateTFIDF(token, listFiles2, file, listTokens));
			}
			
			/*for (Token t : file.getTokens()) {
				if (t.features.get(features.ofText.getFeature()) == 0) {
					double firstOcc = 0, tf_idf = 0, spread = 0;
					for (Token aux : t.tokenWhoInfered) {
						//firstOcc += aux.features.get(features.firstOccurrence.getFeature());
						tf_idf += aux.features.get(features.tf_idf.getFeature());
						//spread += aux.features.get(features.spread.getFeature());
					}
					//t.features.put(features.firstOccurrence.getFeature(), firstOcc/t.tokenWhoInfered.size());
					t.features.put(features.tf_idf.getFeature(), tf_idf/t.tokenWhoInfered.size());
					//t.features.put(features.spread.getFeature(), spread/t.tokenWhoInfered.size());
				}
			}*/
		//}
		
		//ArrayList<FileData> listFiles2 = new FormatFile().readFormatFiles("data/newTest/old/correct_features3/");
		//HashSet<String> filesRead = new HashSet<String>();
		//for (FileData file : listAux) {
		//	filesRead.add(file.getName());
		//}
		
		//calculates the similaritySemantic according Maui
		/*MongoDB.changeMongoParameters(Config.SERVIDOR_DB, Config.PORT_DB, Config.DB_NAME);
		KnowledgeBase knowledgeBase = new KnowledgeBase(new File(Config.PATH_MODEL_CONFIG), Config.SERVIDOR_DB, Config.PORT_DB, Config.DB_NAME);
		System.out.println("Geting concepts wikipedia...");
		List<Concept> allConcepts = knowledgeBase.getConcepts().findAll();
		Hashtable<String, Concept> mapConcepts = new Hashtable<String, Concept>();
		Iterator it = allConcepts.iterator();
		while(it.hasNext()) {
			Concept conc = (Concept)it.next();
			mapConcepts.put(conc.getName(), conc);
		}
		
		System.out.println("Calculing semantic similarity...");
		int n = 0;
		//SemanticSimilarity semantic = new SemanticSimilarity();
		for (FileData file : listFiles2) {
			//if (filesRead.contains(file.getName())) continue;
			System.out.println(++n);
			//ArrayList<Concept> conceptList = new ArrayList<Concept>();
			//for (Token token : file.getTokens()) {
			//	if (!token.conceptWikipedia.equals("") && token.features.get(features.ofText.getFeature()) == 1)
			//		conceptList.add(mapConcepts.get(token.conceptWikipedia));
			//}
			
			for (Token token : file.getTokens()) {
				if (!token.conceptWikipedia.equals("")) {
					//token.features.put(features.semanticSimilarity.getFeature(), semantic.calculateSimilaritySemanticMaui(mapConcepts.get(token.conceptWikipedia), conceptList));
					token.features.put(features.inverseWikipediaLinkade.getFeature(), - Math.log10((double) mapConcepts.get(token.conceptWikipedia).getLinksIn().size() / 2000000));
				}
				else {
					//token.features.put(features.semanticSimilarity.getFeature(), Instance.missingValue());
					token.features.put(features.inverseWikipediaLinkade.getFeature(), Instance.missingValue());
				}
			}
			//new FormatFile().writeFormatFileByFile(file, "data/newTest/old/correct_features3/");
		}*/
		
		//Checks the quantity of each tags
		/*Hashtable<String, Integer> tags = new Hashtable<String, Integer>();

		for (FileData file : listFiles2) {
			for (Token t : file.getTokens()) {
				if (t.getClassification().equals("Yes") && t.features.get(features.ofText.getFeature()) == 0) {
					if (tags.containsKey(t.getLemma())) 
						tags.put(t.getLemma(), tags.get(t.getLemma())+1);
					else
						tags.put(t.getLemma(), 1);
				}
			}
		}
		
		int qtty = 0;
		for (String key : tags.keySet()) {
			if (tags.get(key) == 1)
				qtty++;
		}
		System.out.println("Qtty: " + qtty);*/
		
		/*MongoDB.changeMongoParameters(Config.SERVIDOR_DB, Config.PORT_DB, Config.DB_NAME);
		KnowledgeBase knowledgeBase = new KnowledgeBase(new File(Config.PATH_MODEL_CONFIG), Config.SERVIDOR_DB, Config.PORT_DB, Config.DB_NAME);
		
		
		
		for (Token t : listFiles2.get(0).getTokens()) {
			
			System.out.println(t.getLemma() + " -> " + t.conceptWikipedia);
			Concept concept = knowledgeBase.getConcepts().getConceptByName(t.conceptWikipedia); 
			System.out.println("Concept: " + concept);
			if (concept != null) {
				System.out.println("Inv: " + -Math.log((double)concept.getLinksIn().size()/2000000));
			}
			else 
				System.out.println(Instance.missingValue());
		}*/
		
		
		//new Features().normalizeDataFeatures(listFiles2);
		
		//System.out.println("Reading files...");
		//ArrayList<FileData> listFiles = new ReadData().readFiles();
		//ArrayList<FileData> listFiles = new FormatFile().readFormatFiles("data/newTest/old/readCorpus/");
		//new FormatFile().writeFormaFile(listFiles, "data/newTest/without_order/readCorpus/");
		
		//aux();
		
		//System.out.println("Reading format files...");
		//ArrayList<FileData> listFiles2 = new FormatFile().readFormatFiles("data/newTest/old/semantic_infer_5_norm_tf_idf/");
		//Stopwords stopwords = new StopwordsEnglish();
		
		/*Hashtable<String, Integer> tags = new Hashtable<String, Integer>();
		int qtty = 0;
		for (FileData file : listFiles2) {
			for (Token token : file.getTags()) {
				if (token.features.get(features.ofText.getFeature()) == 0.0D) {
					if (tags.containsKey(token.getLemma()))
						tags.put(token.getLemma(), tags.get(token.getLemma())+1);
					else
						tags.put(token.getLemma(), 1);
				}
			}
		}
		for (String keys : tags.keySet()) {
			if (tags.get(keys) == 1)
				qtty++;
		}
		
		System.out.println("Qtde: " + qtty);
		System.out.println("Qtde de tags exclusivas: " + tags.size());*/
		
		
		//System.out.println("Qtty tokens: " + qttyTokens);
		//new Features().normalizeDataFeatures(listFiles2);
		
		/*Hashtable<String, HashSet<String>> fileTokens = new Hashtable<String, HashSet<String>>();
		for (FileData file : listFiles2) {
			HashSet<String> tokens = new HashSet<String>();
			for (Token t : file.getTokens()) {
				tokens.add(t.getLemma());
			}
			fileTokens.put(file.getName(), tokens);
		}
			
		//Gets the frequency term in document
		Hashtable<String, Hashtable<String, Integer>> tokensFreq = new Hashtable<String, Hashtable<String,Integer>>(); 
		for (FileData file : listFiles) {
			Hashtable<String, Integer> freq = new Hashtable<String, Integer>();
			for (Token t : file.getTokens()) {
				freq.put(t.getLemma(), t.getFrequencyDoc());
			}
			tokensFreq.put(file.getName(), freq);
		}
		
		int qtty =0;
		System.out.println("Size: " + listFiles2.size());
		for(FileData file : listFiles2) {
			ArrayList<Token> removed = new ArrayList<Token>();
			for (Token t : file.getTokens()) {
				if (t.features.get(features.ofText.getFeature()) == 1) {
					try {
						t.setFrequencyDoc(tokensFreq.get(file.getName()).get(t.getLemma().replace("-", " ")));
						//System.out.println("Token: " + t.getLemma() + " -> " + t.getFrequencyDoc());
					}
					catch(Exception e) { 
						qtty++; 
						System.out.println("Não encontrou: " + t.getLemma() + "( " + t.getBeginIndex() + " - " + t.getEndIndex() + ")");
						
						if (t.getClassification().equals("No"))
							removed.add(t);
						else {
							if (t.getBeginIndex() != t.getEndIndex())
								t.setFrequencyDoc(2);
							else
								t.setFrequencyDoc(1);
						}
						System.out.println(t.getFrequencyDoc());
					}
				}
			}
			file.getTokens().removeAll(removed);
		}
		System.out.println("Qtde não encontrada: " + qtty);*/
		
		//Recalculo do TFxIDF
		/*System.out.println("Recalculing TFxIDF...");
		int n = 0;
		for (FileData file : listFiles2) {
			System.out.println(n++);
			for (Token t : file.getTokens()) {
				if (t.features.get(features.ofText.getFeature()) == 1)
					t.features.put(features.tf_idf.getFeature(), new SintaticFeatures().calculateTFIDF(t, listFiles2, file, fileTokens));
			}
		}*/
		
		//remove the terms InfereceNet
		/*for (FileData file : listFiles2) {
			ArrayList<Token> tokensRemoved = new ArrayList<Token>();
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 0.0D)
					tokensRemoved.add(token);
			}
			file.getTokens().removeAll(tokensRemoved);
		}*/
		
		//Média dos quem inferiram
		/*for(FileData file : listFiles2) {
			for (Token t : file.getTokens()) {
				if (t.features.get(features.ofText.getFeature()) == 0) {
					double firstOcc = 0, tf_idf = 0, spread = 0;
					for (Token aux : t.tokenWhoInfered) {
						firstOcc += aux.features.get(features.firstOccurrence.getFeature());
						tf_idf += aux.features.get(features.tf_idf.getFeature());
						spread += aux.features.get(features.spread.getFeature());
					}
					t.features.put(features.firstOccurrence.getFeature(), firstOcc/t.tokenWhoInfered.size());
					t.features.put(features.tf_idf.getFeature(), tf_idf/t.tokenWhoInfered.size());
					t.features.put(features.spread.getFeature(), spread/t.tokenWhoInfered.size());
				}
			}
		}
		
		new FormatFile().writeFormatFile(listFiles2, "data/newTest/old/semantic_infer_5_norm_tf_idf/");*/
		
		//new FormatFile().writeFormatFile(listFiles2, "data/newTest/old/semantic_infer_5_norm/");
		
		/*for (FileData file1 : listFiles) {
			for (FileData file2 : listFiles2) {
				if (file1.getName().equals(file2.getName())) {
					System.out.println("File: " + file1.getName());
					for (Token t1 : file1.getTokens()) {
						for (Token t2 : file2.getTokens()) {
							if (t1.getLemma().replace("-", " ") .equals(t2.getLemma())) {
								for (features f : Config.getFeatures()) {
									if (!t1.features.get(f.getFeature()).equals(t2.features.get(f.getFeature()))) {
										System.out.println("OfText: " + t2.features.get(features.ofText.getFeature()));
										System.out.println(f + " t1: " + t1.features.get(f.getFeature()) + " t2: " + t2.features.get(f.getFeature()));
										t2.features.put(f.getFeature(), t1.features.get(f.getFeature()));
									}
								}
							}
						}
					}
				}
			}
		}
		
		new FormatFile().writeFormatFile(listFiles2, "data/newTest/old/semantic_infer_5/");*/
		
		//remove the terms InfereceNet
		/*for (FileData file : listFiles) {
			ArrayList<Token> tokensRemoved = new ArrayList<Token>();
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 0.0D)
					tokensRemoved.add(token);
			}
			file.getTokens().removeAll(tokensRemoved);
		}
		
		System.out.println("Adding terms inferenceNet...");
		listFiles = new AddInferenceNet(listFiles).checkTagNoText();*/
		
		/*int totalTokenInf = 0, totalWhoInf = 0;
		for (FileData file : listFiles) {
			for (Token t : file.getTokens()) {
				totalTokenInf += t.tokenInfered.size();
				totalWhoInf += t.tokenWhoInfered.size();
				//System.out.println("Infered: " + t.tokenInfered.size());
				//System.out.println("WhoInfered: " + t.tokenWhoInfered.size());
			}
		}
		
		System.out.println("Total inf: " + totalTokenInf);
		System.out.println("Total who inf: " + totalWhoInf);
		*/
		/*System.out.println("Writing arff...");
		try {
			new WriteArff().createFolders("citeULike");
			new WriteArff().writeFiles(listFiles2, "citeULike");
		} catch (Exception e) { e.printStackTrace(); }
		
		/*TermPredictiability term = new TermPredictiability(listFiles2);
		term.calculatePredictiability();*/
		
		//ArrayList<FileData> listFileNoBalance = (ArrayList<FileData>)listFiles2.clone();
		//System.out.println("Term predictiabily test...");
		//term.calculatePrectiabilityTest(listFileNoBalance);
		
		/*System.out.println("Classifing...");
		new RF().runRFCrossValidationDoc(listFiles2, listFileNoBalance);
		System.out.println("Finished!!!");
		
		/*
		int countTag = 0;
		int countToken = 0;
		int avgTag = 0, avgToken = 0;
		for (FileData file : listFiles) {
			for (Token t : file.getTokens()) {
				if (t.features.get(features.ofText.getFeature()) == 0) {
					
					//System.out.println("tokenWhoInfered: " + t.tokenWhoInfered.size());
					
					if (t.getClassification().equals("Yes")) {
						avgTag += t.tokenWhoInfered.size();
						if (t.tokenWhoInfered.size() == 0)
							avgTag++;
					}
					
					if (t.getClassification().equals("No")) { 
						avgToken += t.tokenWhoInfered.size();
						if (t.tokenWhoInfered.size() == 0)
							avgToken++;
					}
					
					if (t.tokenWhoInfered.size() > 1) {
						if (t.getClassification().equals("Yes"))
							countTag++;
						if (t.getClassification().equals("No"))
							countToken++;
					}
				}
			}
		}
		System.out.println("Maior que 1 (tag): " + countTag);
		System.out.println("Maior que 1 (token): " + countToken);
		System.out.println("Avg tag: " + avgTag/198.0);
		System.out.println("Avg token: " + avgToken/36594.0);*/
		
		/*
		for (FileData file : listFiles) {
			for (Token t : file.getTokens()) {
				System.out.println(t.getLemma() + ": " + t.tokenInfered.size());
				
				int countText = 0;
				for (Token tInf : t.tokenInfered) {
					if (tInf.features.get(features.ofText.getFeature()) == 1)
						countText++;
				}
				System.out.println(countText);
			}
		}*/
		
				
		//remove the terms InfereceNet
		/*for (FileData file : listFiles) {
			ArrayList<Token> tokensRemoved = new ArrayList<Token>();
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 0.0D)
					tokensRemoved.add(token);
			}
			file.getTokens().removeAll(tokensRemoved);
		}
		
		System.out.println("Adding terms inferenceNet...");
		listFiles = new AddInferenceNet(listFiles).checkTagNoText();*/
		
		/*int countTest = 0;
		//remove termos inferencenet > 2
		for (FileData file : listFiles) {
			ArrayList<Token> tokensRemoved = new ArrayList<Token>();
			for (Token token : file.getTokens()) {
				for (Token t : token.tokenInfered) {
					if (t.features.get(features.ofText.getFeature()) == 1)
						countTest++;
				}
				
				int count = 10;
				if (token.features.get(features.ofText.getFeature()) == 1 &&
						token.tokenInfered.size() >= count) {
					
					for (Token t : token.tokenInfered) {
						if (t.features.get(features.ofText.getFeature()) == 0 &&
								t.getClassification().equals("No")) {
							if (count > 0) {
								count--;
							} else {
								tokensRemoved.add(t);
							}
						}
					}
				}
			}
			file.getTokens().removeAll(tokensRemoved);
		}
		
		System.out.println("Count test: " + countTest);
		*/
		//System.out.println("Adding terms inferenceNet...");
		//listFiles = new AddInferenceNet(listFiles).checkTagNoText();
		
		//System.out.println("Calculing sintatic features...");
		//new Features().calculateSintaticFeatures(listFiles);
		
		//new FormatFile().writeFormaFile(listFiles, "data/newTest/without_order/sintatic/");
				
		//System.out.println("Adding terms inferenceNet...");
		//listFiles = new AddInferenceNet(listFiles).checkTagNoText();
		
		//System.out.println("Calculing semantic features...");
		//new Features().calculateSemanticFeatures(listFiles);
		
		/*
		int countFile = 0, countTokens = 0, countTags = 0, countTagsOriginal = 0;
		for (FileData file : listFiles) {
			countFile++;
			countTokens += file.getTokens().size();
			countTags += file.getTags().size();
			countTagsOriginal += file.tagsOriginalFile.size();
		}
		System.out.println("Count files: " + countFile);
		System.out.println("Count tokens: " + countTokens);
		System.out.println("Count tags: " + countTags);
		System.out.println("Count tags original: " + countTagsOriginal);
		
		//System.out.println("Normalizing data features...");
		//new Features().normalizeDataFeatures(listFiles);
		
		//System.out.println("Realizing balance...");
		//listFiles = new Balance().realizeBalance(listFiles);*/
				
		/*System.out.println("Writing arff...");
		try {
			new WriteArff().createFolders("citeULike");
			new WriteArff().writeFiles(listFiles2, "citeULike");
		} catch (Exception e) { e.printStackTrace(); }*/
		
		//System.out.println("Reading format files...");
		//ArrayList<FileData> listFiles = new FormatFile().readFormatFiles("data/corpus_full_inference_sem_norm/");
		
		/*for (FileData file : listFiles) {
			for (Token token : file.getTokens()) {
				//System.out.println(token.features.get(features.spread.getFeature()));
				
				if (token.features.get(features.spread.getFeature()) <= 0) {
					System.out.println("Spread: " + token.features.get(features.spread.getFeature()));
					System.out.println("Begin: " + token.getBeginIndex() + " End: " + token.getEndIndex());
				}
			}
		}*/
		
		//System.out.println("Finished!");
		
		
		//int percent = 20;
		//reductionInferenceNet(listFiles, percent);
		
		//remove the terms InfereceNet
		/*for (FileData file : listFiles) {
			ArrayList<Token> tokensRemoved = new ArrayList<Token>();
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 0.0D)
					tokensRemoved.add(token);
			}
			file.getTokens().removeAll(tokensRemoved);
		}*/
		
		
		//remove the sintatic features of terms obtained of InferenceNet
		/*for (FileData file : listFiles) {
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 0.0D) {
					//token.features.put(features.firstOccurrence.getFeature(), 0.0D);
					//token.features.put(features.spread.getFeature(), 0.0D);
					token.features.put(features.tf_idf.getFeature(), 0.0D);
				}
			}
		}*/
		
		//ArrayList<FileData> listFileNoBalance = (ArrayList<FileData>)listFiles2.clone();
		
		//System.out.println("Realizing balance...");
		//listFiles = new Balance().realizeBalance(listFiles);
		
		/*System.out.println("Classifing...");
		new RF().runRFCrossValidationDoc(listFiles2, listFileNoBalance);
		System.out.println("Finished!!!");*/
		
		/*System.out.println("Writing arff...");
		try {
			new WriteArff().createFolders("citeULike");
			new WriteArff().writeFiles(listFiles, "citeULike");
		} catch (Exception e) { e.printStackTrace(); }*/
		
		//InverseWikipediaLinkage inv = new InverseWikipediaLinkage();
		//for(FileData file : listFiles) {
		//	for (Token token : file.getTokens())
		//		token.features.put(features.inverseWikipediaLinkade.getFeature(), inv.calculatesInverseWikipediaLinkage(token));
		//}
		
		//System.out.println("Normalizing data features...");
		//new Features().normalizeDataFeatures(listFiles);
		
		//System.out.println("Writing format files...");
		//new FormatFile().writeFormaFile(listFiles, "data/corpus_full_semantic_inv/");
		
		//CALCULAR SIMILARIDADE SEMÂNTICA TERMOS INFERENCE_NET
		/*System.out.println("Reading format files...");
		ArrayList<FileData> listFiles = new FormatFile().readFormatFiles("data/corpus_full_inference_sem/");
		
		System.out.println("Calculing inverseWikipediaLinkage...");
		InverseWikipediaLinkage inv = new InverseWikipediaLinkage();
		SintaticFeatures sintaticFeatures = new SintaticFeatures();
		WikipediaKeyphraseness wikipediaKeyphraseness = new WikipediaKeyphraseness();
		Keyphraseness keyphraseness = new Keyphraseness();
		int n = 0;
		for(FileData file : listFiles) {
			System.out.println(n++);
			boolean notNodeDegree = false;
			for(Token token : file.getTokens()) {
				if (!token.features.containsKey(features.nodeDegree.getFeature())) {
					notNodeDegree = true;
					break;
				}
			}
			
			
			if (notNodeDegree) { 
				System.out.println("NodeDegre... " + file.getName() + " (" + file.getTokens().size() + ")" );
				SemanticSimilarity sem = new SemanticSimilarity(file); 
				sem.calculateNodeDegree(file);
				sem.calculateSimilaritySemantic(file);
			}
			
			for (Token token : file.getTokens()) {
				if (!token.features.containsKey(features.inverseWikipediaLinkade.getFeature())) {
					System.out.println("inv");
					token.features.put(features.inverseWikipediaLinkade.getFeature(), inv.calculatesInverseWikipediaLinkage(token));
				}
				
				if (!token.features.containsKey(features.wikipediaKeyphraseness.getFeature())) {
					System.out.println("wiki");
					token.features.put(features.wikipediaKeyphraseness.getFeature(), wikipediaKeyphraseness.calculatesWikipediaKeyphraseness(token));
				}
				
				if (notNodeDegree) {
					System.out.println("other");
					token.features.put(features.keyphraseness.getFeature(), keyphraseness.calculateKeyphraseness(token, listFiles));
					token.features.put(features.phraseLenght.getFeature(), sintaticFeatures.calculatePhraseLenght(token));
				}
			}
		}
		
		
		System.out.println("Normalizing data features...");
		new Features().normalizeDataFeatures(listFiles);
		
		System.out.println("Writing format files...");
		new FormatFile().writeFormaFile(listFiles, "data/corpus_full_inference_sem_norm/");
		
		//System.out.println("Writing format files...");
		//new FormatFile().writeFormaFile(listFiles, "data/corpus_full/");
		
		//System.out.println("Reading format files...");
		//ArrayList<FileData> listFiles = new FormatFile().readFormatFiles("data/corpus_full/");
		
		//System.out.println("Adding terms inferenceNet...");
		//listFiles = new AddInferenceNet(listFiles).checkTagNoText();

		//System.out.println("Realizing balance...");
		//listFiles = new Balance().realizeBalance(listFiles);
		
		//System.out.println("Writing format files...");
		//new FormatFile().writeFormaFile(listFiles, "data/corpus_full_inf/");
		
		//System.out.println("Normalizing data features...");
		//new Features().normalizeDataFeatures(listFiles);
		
		//System.out.println("Writing format files...");
		//new FormatFile().writeFormaFile(listFiles, "data/corpus_full_semantic/");
		
		//System.out.println("Reading format files...");
		//ArrayList<FileData> listFilesRead = new FormatFile().readFormatFiles();
		
		/*for (FileData file : listFilesRead) {
			
			System.out.println("FileName: " + file.getName());
			System.out.println("FileText: " + file.getText());
			System.out.println("FileTextLemmatizer" + file.getTextLemmatized());
			System.out.println("FileTitle: " + file.getTitle());
			System.out.println("FileTitleLemmatizer: " + file.getTitleLemmatized());
			System.out.println("Qtty terms: " + file.getQttyTerms());
			
			for (Token token : file.getTokens()) {
				System.out.println(token.getLemma() + " " + token.getClassification() + " " + token.features);
			}
		}*/
	}
	
	private static void reductionInferenceNet (ArrayList<FileData> listFile, int percent) {
		
		int count = 0;
		for (FileData file : listFile) {
			for (Token token : file.getTokens()) {
				if (token.features.containsKey(features.ofText.getFeature()) &&
					token.features.get(features.ofText.getFeature()) == 0.0D) {
					count++;
				}
			}
		}
		
		int qtty = count - (count * percent / 100);
		int qttyRemoved = 0;
		for (FileData file : listFile) {
			ArrayList<Token> tokensRemoved = new ArrayList<Token>();
			for (Token token : file.getTokens()) {
				if (token.features.containsKey(features.ofText.getFeature()) &&
					token.features.get(features.ofText.getFeature()) == 0.0D) {
					if (qttyRemoved < qtty) {
						tokensRemoved.add(token);
						qttyRemoved++;
					}
					else { break; }
				}
			}
			file.getTokens().removeAll(tokensRemoved);
			if (qttyRemoved == qtty) break;
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
					//t.features.put(features.roleIsARelatedTo.getFeature(), 0.0);
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
}