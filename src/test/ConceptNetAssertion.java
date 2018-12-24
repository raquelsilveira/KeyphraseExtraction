package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javatools.datatypes.ByteString;
import javatools.filehandlers.FileLines;
import javatools.parsers.Char17;

import javax.security.auth.Subject;

import features.Features.features;
import preprocessing.FormatFile;
import util.FileData;
import util.Relation;
import util.Token;
import knowledgeBase.conceptNet.Assertions;
import knowledgeBase.conceptNet.ConceptNet;

public class ConceptNetAssertion {

	public static void main(String[] args) {
	
		//ConceptNetAssertion.getAllRelationsConeptNet();
		//ConceptNetAssertion.ajustFileRelations();
		//ConceptNetAssertion.readFile();
		//ConceptNetAssertion.readRelations();
		//ConceptNetAssertion.checksNT(args);
		ConceptNetAssertion.checksFeatures(args);
		
		//String pathData = "data/citeULike/conceptNet_features_I_SS_infered_IN";
		/*String pathData = "data/citeULike/teste/in/";
		String pathOutput = "data/citeULike/teste/out/";
		//String pathOutput = "data/citeULike/conceptNet_features_I_SS_infered_IN_aux/";
		String pathConceptNet = "data/citeULike/conceptNet_relations_en.tsv";
		boolean removeNonText = true;
		
		if (args.length == 4) {
			pathData = args[0];
			pathOutput = args[1];
			pathConceptNet = args[2];
			removeNonText = Boolean.parseBoolean(args[3]);
		}
		
		System.out.println("pathData: " + pathData);
		System.out.println("pathOutput: " + pathOutput);
		System.out.println("pathConceptNet: " + pathConceptNet);
		System.out.println("removeNonText: " + removeNonText);
		
		ConceptNetAssertion.infersTerms(pathData, pathOutput, pathConceptNet, removeNonText);*/
		
		/*String pathOutput = "data/citeULike/teste/infers_conceptNet_AMIE_IN/";
		
		if (args.length == 1)
			pathOutput = args[0];
		
		ArrayList<FileData> listFiles = new FormatFile().readFormatFiles(pathOutput);
		ConceptNetAssertion.countTokens(listFiles);*/
		
		/*String path = "data/citeULike/teste/infers_conceptNet_AMIE2_IN";
		if (args.length == 1)
			path = args[0];
		ConceptNetAssertion.printTagNT(path);*/
		
		/*String pathConceptNet = "data/citeULike/teste/conceptNet_relations_en.tsv";
		String pathAmie = "data/citeULike/teste/relations_amie.txt";
		String pathOutputAmie = "data/citeULike/teste/conceptNet_relations_en_amie.tsv";
		if (args.length == 3) {
			pathConceptNet = args[0];
			pathAmie = args[1];
			pathOutputAmie = args[2];
		}
		
		System.out.println("Reading facts ConceptNet...");
		Hashtable<String, ArrayList<Fact>> factsConceptNet = readFactsConceptNet(pathConceptNet);
		
		System.out.println("Reading facts ConceptNet by subject...");
		Hashtable<String, Hashtable<String, ArrayList<Fact>>> factsConceptNetSubject = readFactsSubjectConceptNet(pathConceptNet);
		
		System.out.println("Reading relations AMIE...");
		ArrayList<Rule> rulesAmie = readRelationsAmie(pathAmie);
		
		System.out.println("Adding relations AMIE...");
		ConceptNetAssertion.addRelationsPredictedAmie(factsConceptNet, factsConceptNetSubject, rulesAmie, pathOutputAmie);
		
		/*String pathConceptNet = "data/citeULike/teste/conceptNet_relations_en.tsv";
		String pathAmie = "data/citeULike/teste/conceptNet_relations_en_amie.tsv";
		if (args.length == 2) {
			pathConceptNet = args[0];
			pathAmie = args[1];
		}
		ConceptNetAssertion.addRulesEspecifics(pathConceptNet, pathAmie);*/
		
		/*String path = "data/citeULike/teste/test/";
		//String path = "data/citeULike/teste/infers_conceptNet_AMIE_IN/";
		//String pathAmie = "data/citeULike/predicted_relations_amie_partial.txt";
		String pathAmie = "data/citeULike/teste/relations_amie.txt";
		String patConceptNet = "data/citeULike/teste/conceptNet_relations_en_amie.tsv";
		ConceptNetAssertion.checksRelationsTag(path, pathAmie, patConceptNet);*/
		
		/*String path = "data/citeULike/conceptNet_en_amie.tsv";
		String pathOutput = "data/citeULike/conceptNet_en_amie_new.tsv";
		if (args.length == 2) {
			path = args[0];
			pathOutput = args[1];
		}
		ConceptNetAssertion.checksRelationsRepetead(path, pathOutput);*/
		
		/*String pathAmie1 = "data/citeULike/teste/relations_amie.txt";
		String pathAmie2 = "data/citeULIke/teste/relations_amie2.txt";
		ConceptNetAssertion.readRulesAmies(pathAmie1, pathAmie2);*/
		
		/*String pathConceptNet = "data/citeULike/teste/conceptNet_relations_en_amie.tsv";
		String pathData = "data/citeULike/teste/infers_conceptNet_AMIE_IN/";
		ConceptNetAssertion.checksNTNotCapturedConceptNet(pathConceptNet, pathData);*/
		
		//String path = "data/citeULike/teste/non_text";
		//checksNonText(path);
		
		/*String pathConceptNet = "data/citeULike/teste/conceptNet_relations_en.tsv";
		String pathData = "data/citeULike/teste/infers_conceptNet_IN_2";
		//String pathData = "data/citeULike/teste/aux";
		String pathOutput = "data/citeULike/teste/conceptNet_relations_level2_en_correct_v2.tsv";
		addRelationLevel2(pathConceptNet, pathData, pathOutput);*/
		
		//String path = "data/citeULike/teste/conceptNet_relations_level2_en.tsv";
		//String path = "data/citeULike/teste/conceptNet_relations_level2_en_correct.tsv";
		//20730318 conceptNet_relations_level2_en_correct_v2.tsv
		//22798071 conceptNet_relations_level2_en_without_repeated.tsv
		//6554675 conceptNet_relations_en.tsv
		//String pathOutput = "data/citeULike/teste/conceptNet_relations_level2_en_correct_v5.tsv";
		//readTsv(path, pathOutput);
	}
	
	public static void checksFeatures(String[] args){
		String path = args[0];
		
		Hashtable<String, Double> minFeatures = new Hashtable<String, Double>();
		Hashtable<String, Double> maxFeatures = new Hashtable<String, Double>();
		for (File fileOrig : new File(path).listFiles()) {
			
			System.out.println("Read " + fileOrig.getName());
			FileData file = new FormatFile().readFormatFileByFile(fileOrig.getAbsolutePath());
			try {
				FileWriter writer = new FileWriter(new File("data/citeULike/output_tagsNT.txt"), true);
				
				for (Token token : file.getTags()) {
					if (token.features.get(features.ofText.getFeature()) == 1) continue;
				
					writer.write(token.getLemma() + "(" + token.conceptWikipedia + ")\n");
					
					for (String f : token.features.keySet()) {
						writer.write(f + " " + token.features.get(f) + "\n");
						if ((f.equals(features.semanticSimilarity.getFeature()) || f.equals(features.nodeDegree.getFeature()) ||
							f.equals(features.inverseWikipediaLinkade.getFeature()) || f.equals(features.wikipediaKeyphraseness.getFeature())) &&
							(token.conceptWikipedia == null || token.conceptWikipedia.equals(""))) 
							continue;
						
						if (!minFeatures.containsKey(f) || token.features.get(f) < minFeatures.get(f)) {
							minFeatures.put(f, token.features.get(f));
						}
						
						if (!maxFeatures.containsKey(f) || token.features.get(f) > maxFeatures.get(f)) {
							maxFeatures.put(f, token.features.get(f));
						}
					}
				
				if (!minFeatures.containsKey("QttyWhoInfered") || token.tokenWhoInfered.size() < minFeatures.get("QttyWhoInfered"))
					minFeatures.put("QttyWhoInfered", (double)token.tokenWhoInfered.size());
				
					writer.write("Infered by: \n");
					for (Token whoInfered : token.tokenWhoInfered.keySet()) {
						
						writer.write(whoInfered.getLemma() + "(" + whoInfered.conceptWikipedia + "): ");
						for (String f : whoInfered.features.keySet())
							writer.write(f + " -> " + whoInfered.features.get(f) + "; ");
						for (String rel : token.tokenWhoInfered.get(whoInfered).relation)
							writer.write(rel + " ");
						writer.write("SS: " + token.tokenWhoInfered.get(whoInfered).similaritySematic);
						writer.write("\n\n\n");
						
						if (token.conceptWikipedia == null || token.conceptWikipedia.equals("") ||
							whoInfered.conceptWikipedia == null || whoInfered.conceptWikipedia.equals("")) 
							continue;
						
						if (!minFeatures.containsKey("SSWhoInfered") || token.tokenWhoInfered.get(whoInfered).similaritySematic < minFeatures.get("SSWhoInfered"))
							minFeatures.put("SSWhoInfered", token.tokenWhoInfered.get(whoInfered).similaritySematic);
						
						if (!maxFeatures.containsKey("SSWhoInfered") || token.tokenWhoInfered.get(whoInfered).similaritySematic < maxFeatures.get("SSWhoInfered"))
							maxFeatures.put("SSWhoInfered", token.tokenWhoInfered.get(whoInfered).similaritySematic);
					}
				}
				writer.close();
			} catch(Exception e) { e.printStackTrace(); }
		}
		
		for (String key : minFeatures.keySet())
			System.out.println(key + " -> " + minFeatures.get(key));
		
		for (String key : maxFeatures.keySet())
			System.out.println(key + " -> " + maxFeatures.get(key));
	}
	
	public static void readTsv(String path, String pathOutput) {
		int count = 0;
		try {
			FileReader file = new FileReader(path);
			BufferedReader fileBuff = new BufferedReader(file);
		
			//BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
			
			FileWriter fileWriter = new FileWriter(new File(pathOutput), true);
			HashSet<String> facts = new HashSet<String>();
			
			//String line = "";
			
			String message = "";
			System.out.println("Reading");
			File f = new File(path);
			for (String line : new FileLines(f, "UTF-8", message)) {
				
				/*if (line.endsWith("."))
					line = Char17.cutLast(line);
				String[] split = line.split("\t");
				if (split.length == 3) {
					System.out.println(split[0].trim() + " -> " + split[1].trim()  + " -> " +  split[2].trim());
					add(split[0].trim(), split[1].trim(), split[2].trim());
				}
				else if (split.length == 4) {
					add(split[1].trim(), split[2].trim(), split[3].trim());
				}*/
				
				/*String fact = line.replace(" ", "\t");
				if (fact.length() > 6 && !facts.contains(fact)) {
					
					fileWriter.write((count > 0 ? "\n" : "") + fact);
					facts.add(fact);
					
					count++;
					if (count % 100 == 0)
						System.out.println(count);
					
					//if (count > 7000000)
					//	System.out.println(fact);
				}*/
				count++;
			}
			
			System.out.println(count);
			System.out.println("Message: " + message);
			/*while((line = fileBuff.readLine()) != null) {
				String fact = line.replace(" ", "\t");
				if (fact.length() > 6 && !facts.contains(fact)) {
					//fileWriter.write(fact);
					facts.add(fact);
					
					count++;
					if (count % 100 == 0)
						System.out.println(count);
					
					//if (count > 7000000)
					//	System.out.println(fact);
				}
			}
			System.out.println("Qtde de fatos: " + count);*/
			
			fileBuff.close();
			file.close();
			fileWriter.close();
		} catch(Exception e) { e.printStackTrace(); }
	}
	
	public static boolean add(CharSequence... fact) {
		if (fact.length == 3) {
			return (add(compress(fact[0]), compress(fact[1]), compress(fact[2])));
		} else if (fact.length == 4) {
			return (add(compress(fact[1]), compress(fact[2]), compress(fact[3])));
		} else {
			throw new IllegalArgumentException("Incorrect fact: " + Arrays.toString(fact));
		}
	}
	
	public static ByteString compress(CharSequence s) {
		if (s instanceof ByteString)
			return ((ByteString) s);
		String str = s.toString();
		int pos = str.indexOf("\"^^");
		if (pos != -1)
			str = str.substring(0, pos + 1);
		return (ByteString.of(str));
	}
	
	public static void addRelationLevel2(String pathConceptNet, String pathData, String pathOutput) {
		
		try {
			/*System.out.println("Reading conceptNet");
			Hashtable<String, ArrayList<Fact>> factsConceptNet = readFactsConceptNet(pathConceptNet);
			System.out.println("Finishing of to read the conceptNet");
			
			ArrayList<Fact> facts = new ArrayList<Fact>();
			for (String key : factsConceptNet.keySet())
				facts.addAll(factsConceptNet.get(key));
			System.out.println("Qtty facts: " + facts.size());*/
			
			File directory = new File(pathData);
			HashSet<String> factsAdded = new HashSet<String>();
			int i = 0;
			boolean firstLineNT = false;
			for(File fileXml : directory.listFiles()) {
				if (!fileXml.getName().endsWith(".xml")) continue;
				FileData file = new FormatFile().readFormatFileByFile(fileXml.getAbsolutePath());
				System.out.println("Reading file " + ++i + " " + fileXml.getName());
				
				FileWriter fileRelationsLevel2 = new FileWriter(new File(pathOutput), true);
				for (Token token : file.getTokens()) {
					if (token.features.get(features.ofText.getFeature()) == 0) {
						
						boolean level2 = true;
						for (Token whoInfered : token.tokenWhoInfered.keySet()) {
							if (whoInfered.features.get(features.ofText.getFeature()) == 1)
								level2 = false;
						}
						
						if (level2) {
							for (Token whoInfered1 : token.tokenWhoInfered.keySet()) {
								for (Token whoInfered2 : whoInfered1.tokenWhoInfered.keySet()) {
									Fact newFact = new Fact();
									newFact.subject = "<" + whoInfered2.getLemma().trim().replace(" ", "_") + ">";
									newFact.relation = "RelatedTo";
									newFact.object = "<" + token.getLemma().trim().replace(" ", "_") + ">";
									if (!factsAdded.contains(newFact.toString())) {
										factsAdded.add(newFact.toString());
										fileRelationsLevel2.write((firstLineNT ? "\n" : "") + newFact.toString());
										firstLineNT = false;
										//System.out.println(newFact.toString());
									}
								}
							}
						}
					}
				}
				fileRelationsLevel2.close();
				System.out.println("Writing file " + i + " " + fileXml.getName());
			}
		} catch(Exception e) { e.printStackTrace(); }
	}
	
	public static void checksNonText(String path) {
		ArrayList<FileData> listFiles = new FormatFile().readFormatFiles(path);
		
		for (FileData file : listFiles) {
			for (Token token : file.getTags()) {
				if (token.features.get(features.ofText.getFeature()) == 0) {
					System.out.println(token.getLemma());
					System.out.println("Inferido por: ");
					for (Token whoInfered : token.tokenWhoInfered.keySet()) {
						System.out.print(whoInfered.getLemma() + " -> ");
						for (String relation : token.tokenWhoInfered.get(whoInfered).relation) {
							System.out.print(relation);
						}
						
						System.out.println("");
						System.out.println("Inferido por: ");
						for (Token whoInfered2 : whoInfered.tokenWhoInfered.keySet()) {
							System.out.println("");
							System.out.print(whoInfered2.getLemma() + " -> ");
							for (String relation : whoInfered.tokenWhoInfered.get(whoInfered2).relation) {
								System.out.print(relation);
							}
						}
					}
				}
			}
		}
	}
	
	public static void checksNTNotCapturedConceptNet(String pathConceptNet, String pathData) {
		
		try {
			
			System.out.println("Reading ConceptNet's data...");
			FileReader file = new FileReader(pathConceptNet);
			BufferedReader fileBuff = new BufferedReader(file);
			Hashtable<String, ArrayList<Fact>> listObjectConceptNet = new Hashtable<String, ArrayList<Fact>>();
			String line = "";
			while((line = fileBuff.readLine()) != null) {
				
				Fact fact = new Fact();
				fact.subject = line.substring(0, line.indexOf("\t")).replace("<", "").replace(">", "").replace("_", " ");
				fact.relation = line.substring(line.indexOf("\t")+1, line.lastIndexOf("\t")).replace("<", "").replace(">", "").replace("_", " ");
				fact.object = line.substring(line.lastIndexOf("\t")+1, line.length()).replace("<", "").replace(">", "").replace("_", " ");
				
				ArrayList<Fact> facts = new ArrayList<Fact>();
				if (listObjectConceptNet.containsKey(fact.subject))
					facts = listObjectConceptNet.get(fact.subject);
				facts.add(fact);
				listObjectConceptNet.put(fact.subject, facts);
			}
			fileBuff.close();
			file.close();
			System.out.println("Finished of to read the ConceptNet's data...");
			
			int count = 0;
			ArrayList<FileData> listFiles = new FormatFile().readFormatFiles(pathData);
			for (FileData fileData : listFiles) {
				
				HashSet<String> tagText = new HashSet<String>();
				HashSet<String> tagNT = new HashSet<String>();
				for (Token tag : fileData.getTags()) {
					if (tag.features.get(features.ofText.getFeature()) == 1)
						tagText.add(tag.getLemma());
					else
						tagNT.add(tag.getLemma());
				}
				
				for (String tagOrig : fileData.tagsOriginalFile.keySet()) {
					if (!tagText.contains(tagOrig) && listObjectConceptNet.containsKey(tagOrig) ) {
						count++;
						System.out.println("File: " + fileData.getName());
						System.out.println("Tag: " + tagOrig);
						System.out.println(listObjectConceptNet.get(tagOrig));
					}
				}
			}
			System.out.println("Qtde de tags na ConceptNet: " + count);
			
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	public static void readRulesAmies(String amie1, String amie2) {
		
		ArrayList<Rule> rulesAmie1 = readRelationsAmie(amie1);
		HashSet<String> rulesStringAmie1 = new HashSet<String>();
		for (Rule r : rulesAmie1) 
			rulesStringAmie1.add(r.toString());
		
		ArrayList<Rule> rulesAmie2 = readRelationsAmie(amie2);
		HashSet<String> rulesStringAmie2 = new HashSet<String>();
		for (Rule r : rulesAmie2)
			rulesStringAmie2.add(r.toString());
		
		HashSet<String> intersect = new HashSet<String>(rulesStringAmie1);
		intersect.retainAll(rulesStringAmie2);
		
		System.out.println("Size Amie1: " + rulesAmie1.size());
		System.out.println("Size Amie2: " + rulesAmie2.size());
		System.out.println("Intersect: " + intersect.size());
		
		System.out.println("Diferentes: ");
		for (String r : rulesStringAmie2) {
			if (!intersect.contains(r))
				System.out.println(r);
		}
	}
	
	public static Hashtable<String, Integer> rulesAmieTag = new Hashtable<String, Integer>();
	
	public static void addRulesEspecifics(String pathConceptNet, String pathAmie) {
	
		try {
			System.out.println("Reading facts ConceptNet...");
			Hashtable<String, ArrayList<Fact>> factsConceptNet = readFactsConceptNet(pathConceptNet);
			Hashtable<String, Hashtable<String, ArrayList<Fact>>> factsSubjectConceptNet = readFactsSubjectConceptNet(pathConceptNet);
			System.out.println("Finished of to read the facts ConceptNet...");
			
			FileWriter fileWriter = new FileWriter(pathAmie, true);
			int count = 0;
			
			//?b  <IsA>  ?a  ?a  <RelatedTo>  ?b   => ?a  <Antonym>  ?b
			/*ArrayList<Fact> factsIsA = factsConceptNet.get("IsA");
			count = 0;
			System.out.println("Rule: ?b  <IsA>  ?a  ?a  <RelatedTo>  ?b   => ?a  <Antonym>  ?b");
			//ArrayList<Fact> factsRelatedTo = factsConceptNet.get("RelatedTo");
			for (Fact factAtIsA : factsIsA) {
				if (count % 100 == 0)
					System.out.println(count + " de " + factsIsA.size());
				++count;
				
				ArrayList<Fact> factsRelatedTo = factsSubjectConceptNet.get("RelatedTo").get(factAtIsA.object);
				if (factsRelatedTo != null && factsRelatedTo.size() > 0) {
					for (Fact factRelatedTo : factsRelatedTo) {
						if (factAtIsA.subject.equals(factRelatedTo.object) &&
							factAtIsA.object.equals(factRelatedTo.subject)) {
							String newRule = "<" + factRelatedTo.subject.replace(" ", "_") + ">\t<" + "Antonym" + ">\t<" + factRelatedTo.object.replace(" ", "_") + ">";
							fileWriter.write(newRule);
						}
					}
				}
			}
			
			fileWriter.close();
			fileWriter = new FileWriter(pathAmie, true);*/
			count = 0;
			//?b  <AtLocation>  ?a  ?b  <IsA>  ?a   => ?a  <InheritsFrom>  ?b
			//?b  <AtLocation>  ?a  ?a  <IsA>  ?b   => ?a  <MadeOf>  ?b
			System.out.println("Rule: ?b  <AtLocation>  ?a  ?b  <IsA>  ?a   => ?a  <InheritsFrom>  ?b");
			ArrayList<Fact> factsAtLocation = factsConceptNet.get("AtLocation");
			for (Fact factAtLocation : factsAtLocation) {
				if (count % 100 == 0)
					System.out.println(count + " de " + factsAtLocation.size());
				++count;
				
				ArrayList<Fact> factsIsA = factsSubjectConceptNet.get("IsA").get(factAtLocation.subject);
				if (factsIsA == null)
					factsIsA = new ArrayList<Fact>();
				
				ArrayList<Fact> factsIsAAux = factsSubjectConceptNet.get("IsA").get(factAtLocation.object);
				if (factsIsAAux != null)
					factsIsA.addAll(factsIsAAux);
				if (factsIsA != null && factsIsA.size() > 0) {
					for (Fact factIsA : factsIsA) {
						if (factAtLocation.subject.equals(factIsA.subject) &&
							factAtLocation.object.equals(factIsA.object)) {
							String newRule = "<" + factIsA.object.replace(" ", "_") + ">\t<" + "InheritsFrom" + ">\t<" + factIsA.subject.replace(" ", "_") + ">";
							fileWriter.write(newRule);
						}
						
						if (factAtLocation.subject.equals(factIsA.object) &&
							factAtLocation.object.equals(factIsA.subject)) {
								String newRule = "<" + factIsA.subject.replace(" ", "_") + ">\t<" + "MadeOf" + ">\t<" + factIsA.object.replace(" ", "_") + ">";
								fileWriter.write(newRule);
						}
					}
				}
			}
			
			count = 0;
			System.out.println("?b  <AtLocation>  ?a  ?b  <PartOf>  ?a   => ?a  <MadeOf>  ?b");
			for (Fact factAtLocation : factsAtLocation) {
				if (count % 100 == 0)
					System.out.println(count + " de " + factsAtLocation.size());
				++count;
				
				ArrayList<Fact> factsPartOf = factsSubjectConceptNet.get("PartOf").get(factAtLocation.subject);
				if (factsPartOf != null && factsPartOf.size() > 0) {
					for (Fact factPartOf : factsPartOf) {
						if (factAtLocation.subject.equals(factPartOf.subject) &&
							factAtLocation.object.equals(factPartOf.object)) {
							String newRule = "<" + factPartOf.object.replace(" ", "_") + ">\t<" + "MadeOf" + ">\t<" + factPartOf.subject.replace(" ", "_") + ">";
							fileWriter.write(newRule);
						}
					}
				}
			}
			
			count = 0;
			System.out.println("?b  <AtLocation>  ?a  ?b  <RelatedTo>  ?a   => ?a  <HasA>  ?b)");
			for (Fact factAtLocation : factsAtLocation) {
				if (count % 100 == 0)
					System.out.println(count + " de " + factsAtLocation.size());
				++count;
				
				ArrayList<Fact> factsRelatedTo = factsSubjectConceptNet.get("RelatedTo").get(factAtLocation.subject);
				if (factsRelatedTo != null && factsRelatedTo.size() > 0) {
					for (Fact factRelatedTo : factsRelatedTo) {
						if (factAtLocation.subject.equals(factRelatedTo.subject) &&
							factAtLocation.object.equals(factRelatedTo.object)) {
							String newRule = "<" + factRelatedTo.object.replace(" ", "_") + ">\t<" + "HasA" + ">\t<" + factRelatedTo.subject.replace(" ", "_") + ">";
							fileWriter.write(newRule);
						}
					}
				}
			}
			
			/*fileWriter.close();
			fileWriter = new FileWriter(pathAmie, true);
			count = 0;
			//?b  <AtLocation>  ?a  ?a  <IsA>  ?b   => ?a  <MadeOf>  ?b
			System.out.println("Rule: ?b  <AtLocation>  ?a  ?a  <IsA>  ?b   => ?a  <MadeOf>  ?b");
			for (Fact factAtLocation : factsAtLocation) {
				if (count % 5000 == 0)
					System.out.println(count + " de " + factsAtLocation.size());
				++count;
				for (Fact factIsA : factsIsA) {
					if (factAtLocation.subject.equals(factIsA.object) &&
						factAtLocation.object.equals(factIsA.subject)) {
						String newRule = "<" + factIsA.subject.replace(" ", "_") + ">\t<" + "MadeOf" + ">\t<" + factIsA.object.replace(" ", "_") + ">";
						fileWriter.write(newRule);
					}
				}
			}*/
			
			/*fileWriter.close();
			fileWriter = new FileWriter(pathAmie, true);
			count = 0;
			//?b  <AtLocation>  ?a  ?b  <PartOf>  ?a   => ?a  <MadeOf>  ?b
			System.out.println("Rule: ?b  <AtLocation>  ?a  ?b  <PartOf>  ?a   => ?a  <MadeOf>  ?b");
			ArrayList<Fact> factsPartOf = factsConceptNet.get("PartOf");
			for (Fact factAtLocation : factsAtLocation) {
				if (count % 5000 == 0)
					System.out.println(count + " de " + factsAtLocation.size());
				++count;
				for (Fact factPartOf : factsPartOf) {
					if (factAtLocation.subject.equals(factPartOf.subject) &&
						factAtLocation.object.equals(factPartOf.object)) {
						String newRule = "<" + factPartOf.object.replace(" ", "_") + ">\t<" + "MadeOf" + ">\t<" + factPartOf.subject.replace(" ", "_") + ">";
						fileWriter.write(newRule);
					}
				}
			}*/
			
			/*fileWriter.close();
			fileWriter = new FileWriter(pathAmie, true);
			count = 0;
			//?b  <AtLocation>  ?a  ?b  <RelatedTo>  ?a   => ?a  <HasA>  ?b
			System.out.println("Rule: ?b  <AtLocation>  ?a  ?b  <RelatedTo>  ?a   => ?a  <HasA>  ?b");
			for (Fact factAtLocation : factsAtLocation) {
				if (count % 5000 == 0)
					System.out.println(count + " de " + factsAtLocation.size());
				++count;
				for (Fact factRelatedTo : factsRelatedTo) {
					if (factAtLocation.subject.equals(factRelatedTo.subject) &&
						factAtLocation.object.equals(factRelatedTo.object)) {
						String newRule = "<" + factRelatedTo.object.replace(" ", "_") + ">\t<" + "HasA" + ">\t<" + factRelatedTo.subject.replace(" ", "_") + ">";
						fileWriter.write(newRule);
					}
				}
			}*/
			
			fileWriter.close();
		} catch(Exception e) { e.printStackTrace(); }
	}
	
	public static void checksRelationsRepetead(String path, String pathOutput) {
		try {
			HashSet<String> checksRules = new HashSet<String>();
			
			FileReader file = new FileReader(path);
			BufferedReader fileBuff = new BufferedReader(file);
			
			FileWriter fileWriter = new FileWriter(pathOutput, true);
			String line = "";
			while((line = fileBuff.readLine()) != null) {
				if (!checksRules.contains(line)){
					fileWriter.write(line + "\n");
					checksRules.add(line);
				}
			}
			fileBuff.close();
			file.close();
			fileWriter.close();
			
		} catch(Exception e) { e.printStackTrace(); }
	}
	
	public static void checksRelationsTag(String path, String pathAmie, String pathConceptNet) {
		
		System.out.println("Reading the rules/relations of the conceptNet");
		Hashtable<String, ArrayList<Fact>> factsConceptNet = readFactsConceptNet(pathConceptNet);
		
		/*ArrayList<Fact> aux = factsConceptNet.get("Antonym");
		Fact auxFact = new Fact();
		auxFact.subject = "nature";
		auxFact.relation = "Antonym";
		auxFact.object = "original";
		aux.add(auxFact);*/
		
		
		System.out.println("Finished of to read the rules/relations of the conceptNet");
		
		ArrayList<Rule> rulesAmie = ConceptNetAssertion.readRelationsAmie(pathAmie);
		
		ArrayList<FileData> listFiles = new FormatFile().readFormatFiles(path);
		
		int countTagRuleAmie = 0;
		for (FileData file : listFiles) {
			
			System.out.println(file.getName());
			for (Token token : file.getTags()) {
				boolean check = true;
				if (token.features.get(features.ofText.getFeature()) == 0) {
					//System.out.println(file.getName());
					System.out.println(token.getLemma());
					System.out.println("Inferido por: ");
					for (Token whoInfered : token.tokenWhoInfered.keySet()) {
						System.out.println(whoInfered.getLemma() + token.tokenWhoInfered.get(whoInfered).relation);
						
						for (String relation : token.tokenWhoInfered.get(whoInfered).relation) {
							//System.out.println("Finding: " + whoInfered.getLemma() + " -> " + relation + " -> " + token.getLemma());
							for (Rule ruleAmie : rulesAmie) {
								if (ruleAmie.head.relation.equals(relation)) {
									//System.out.println("Amie: " + ruleAmie);
									if (captureRulesAmieHead(ruleAmie, factsConceptNet, whoInfered.getLemma(), token.getLemma()) &&
										check) {
										countTagRuleAmie++;
										check = false;
									}
								}
							}
						}
					}
				}
			}
		}
		
		System.out.println("Rules AMIE tags: ");
		for(String key : rulesAmieTag.keySet()) {
			System.out.println(key + "\t" + rulesAmieTag.get(key));
		}
		
		System.out.println("Qtde de tags com regra AMIE: " + countTagRuleAmie);
	}
	
	public static boolean captureRulesAmieHead(Rule rule, Hashtable<String, ArrayList<Fact>> factsConceptNet, String tokenSubjectHead, String tokenObjectHead) {
		
		//System.out.println(rule);
		
		ArrayList<Rule> rulesAnalysed = new ArrayList<Rule>();
		
		int indexFactBody = 0;
		for(Fact fact : rule.body) {
			
			//System.out.println(fact);
			ArrayList<Rule> rulesRemoved = new ArrayList<Rule>();

			if (factsConceptNet.containsKey(fact.relation)) {
				
				//System.out.println("Qtde relations (" + fact.relation + "): " + factsConceptNet.get(fact.relation).size());
				
				ArrayList<Rule> newRules = new ArrayList<Rule>();
				for (Fact factConceptNet : factsConceptNet.get(fact.relation)) {
					
					if ((fact.subject.equals(rule.head.subject) && factConceptNet.subject.equals(tokenSubjectHead) && 
						(!fact.object.equals(rule.head.object) || 
						(fact.object.equals(rule.head.object) && factConceptNet.object.equals(tokenObjectHead))))
						||
						(fact.object.equals(rule.head.object) && factConceptNet.object.equals(tokenObjectHead) && 
						(!fact.subject.equals(rule.head.subject) || 
						(fact.subject.equals(rule.head.subject) && factConceptNet.subject.equals(tokenSubjectHead))))
						||
						(fact.subject.equals(rule.head.object) && factConceptNet.subject.equals(tokenObjectHead) && 
						(!fact.object.equals(rule.head.subject) || 
						(fact.object.equals(rule.head.subject) && factConceptNet.object.equals(tokenSubjectHead))))
						||
						(fact.object.equals(rule.head.subject) && factConceptNet.object.equals(tokenSubjectHead) && 
						(!fact.subject.equals(rule.head.object) || 
						(fact.subject.equals(rule.head.object) && factConceptNet.subject.equals(tokenObjectHead))))
						||
						(!fact.subject.equals(rule.head.subject) &&
						!fact.subject.equals(rule.head.object) &&
						!fact.object.equals(rule.head.subject) &&
						!fact.object.equals(rule.head.object))) {
						
						//System.out.println("Fato da vez: " + factConceptNet);
						boolean existsVariable = false;
						
						for (int i = 0; i < indexFactBody; i++) {
							if (rule.body.get(i).subject.equals(fact.subject) ||
								rule.body.get(i).subject.equals(fact.object) ||
								rule.body.get(i).object.equals(fact.subject) ||
								rule.body.get(i).object.equals(fact.object)) {
								
								existsVariable = true;
								
								for(Rule analyse : rulesAnalysed) {
								
									if (
										(rule.body.get(i).subject.equals(fact.subject) && analyse.body.get(i).subject.equals(factConceptNet.subject) &&
										(!rule.body.get(i).object.equals(fact.object) || 
										(rule.body.get(i).object.equals(fact.object) && analyse.body.get(i).object.equals(factConceptNet.object)))) 
										||
										(rule.body.get(i).subject.equals(fact.object) && analyse.body.get(i).subject.equals(factConceptNet.object) &&
										(!rule.body.get(i).object.equals(fact.subject) || 
										(rule.body.get(i).object.equals(fact.subject) && analyse.body.get(i).object.equals(factConceptNet.subject)))) 
										||
										(rule.body.get(i).object.equals(fact.subject) && analyse.body.get(i).object.equals(factConceptNet.subject) &&
										(!rule.body.get(i).subject.equals(fact.object) || 
										(rule.body.get(i).subject.equals(fact.object) && analyse.body.get(i).subject.equals(factConceptNet.object)))) 
										||	
										(rule.body.get(i).object.equals(fact.object) && analyse.body.get(i).object.equals(factConceptNet.object) &&
										(!rule.body.get(i).subject.equals(fact.subject) || 
										(rule.body.get(i).subject.equals(fact.subject) && analyse.body.get(i).subject.equals(factConceptNet.subject))))
									   ) {  
											
										if (analyse.body.size() == indexFactBody) 
											analyse.body.add(factConceptNet);
										else {
											Rule newRule = new Rule();
											newRule.body = (ArrayList<Fact>)analyse.body.clone();
											newRule.body.get(indexFactBody).subject = factConceptNet.subject;
											newRule.body.get(indexFactBody).relation = factConceptNet.relation;
											newRule.body.get(indexFactBody).object = factConceptNet.object;
											newRules.add(newRule);
										} 
									}
								}
							}
						}
						
						if (!existsVariable) {
							Rule ruleAdd = new Rule();
							ruleAdd.body.add(factConceptNet);
							rulesAnalysed.add(ruleAdd);
						}
					}
				}
				if (newRules.size() > 0)
					rulesAnalysed.addAll(newRules);
			}
			
			indexFactBody++;
			
			for(Rule aux : rulesAnalysed) {
				if (aux.body.size() < indexFactBody)
					rulesRemoved.add(aux);
			}
			
			rulesAnalysed.removeAll(rulesRemoved);
		}
		boolean retorno = false;
		if (rulesAnalysed != null && rulesAnalysed.size() > 0) {
			retorno = true;
			System.out.println("AMIE: " + rule);
			
			int qtde = 1;
			if (ConceptNetAssertion.rulesAmieTag.containsKey(rule.toString())) 
				qtde = ConceptNetAssertion.rulesAmieTag.get(rule.toString()) + 1;
			
			ConceptNetAssertion.rulesAmieTag.put(rule.toString(), qtde);
		}
		
		for (Rule rulesPredicted : rulesAnalysed) {
		
			rulesPredicted.head.subject = tokenSubjectHead;
			rulesPredicted.head.object = tokenObjectHead;
			rulesPredicted.head.relation = rule.head.relation;
			//String ruleFound = "<" + rulesPredicted.head.subject.replace(" ", "_") + ">\t<" + rulesPredicted.head.relation + ">\t<" + rulesPredicted.head.object.replace(" ", "_") + ">";
			System.out.println(rulesPredicted);
		}
		return retorno;
	}
	
	public static void printTagNT(String path) {
		
		ArrayList<FileData> listFiles = new FormatFile().readFormatFiles(path);
		int f = 0;
		for (FileData file : listFiles) {
			System.out.println(++f + " File: " + file.getName() + " (" + file.getTags().size() + ")");
			int t = 0;
			for (Token token : file.getTags()) {
				System.out.println(++t);
				if (token.features.get(features.ofText.getFeature()) == 0)
					System.out.println(token.getLemma());
			}
		}
	}
	
	public static void checksNT(String [] args) {
		
		//String path = "data/citeULike/conceptNet_features_I_SS_infered_IN/";
		String path = "data/citeULike/files_tags_NT/";
		if (args.length > 0)
			path = args[0];
		//ArrayList<FileData> listFiles = new FormatFile().readFormatFiles(path);
		int countInfersTag = 0, countInfersToken = 0, countTagNT = 0, countTokenNT = 0;
		int tagInferedMore1 = 0, tokenInferedMore1 = 0;
		for (File fileOrig : new File(path).listFiles()) {
			
			FileData file = new FormatFile().readFormatFileByFile(fileOrig.getAbsolutePath());
			int countTagNTFile = 0, countTagTFile = 0;
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 0) {
					if (token.getClassification().equals("Yes")) {
						countInfersTag += token.tokenWhoInfered.size();
						countTagNT++;
						countTagNTFile++;
						if (token.tokenWhoInfered.size() > 1)
							tagInferedMore1++;
					}
					else {
						countInfersToken += token.tokenWhoInfered.size();
						countTokenNT++;
						if (token.tokenWhoInfered.size() > 1)
							tokenInferedMore1++;
					}
				} else
					if (token.getClassification().equals("Yes"))
						countTagTFile++;
			}
			System.out.println("File: " + file.getName());
			System.out.println("Tag: " + (countTagNTFile + countTagTFile) + " (T: " + countTagTFile + "; NT: " + countTagNTFile + ")");
		}
		
		System.out.println("TOKENS NT: [Qtde: " + countTokenNT + "; Qtde inferidos: " + countInfersToken + "; Média: " + (double)(countInfersToken/(double)countTokenNT) + "]");
		System.out.println("Infered more than 1 token: " + tokenInferedMore1);
		System.out.println("TAGS NT: [Qtde: " + countTagNT + "; Qtde inferidos: " + countInfersTag + "; Média: " + (double)(countInfersTag/(double)countTagNT) + "]");
		System.out.println("Infered more than 1 token: " + tagInferedMore1);
	}
	
	
	public static void teste() {
/*Hashtable<String, ArrayList<Fact>> factsConceptNet = new Hashtable<String, ArrayList<Fact>>();
		
		ArrayList<Fact> facts = new ArrayList<Fact>();
		
		Fact fact1 = new Fact();
		fact1.subject = "gato";
		fact1.relation = "NotIsA";
		fact1.object = "galinha";
		facts.add(fact1);
		
		Fact fact2 = new Fact();
		fact2.subject = "gato";
		fact2.relation = "NotIsA";
		fact2.object = "cachorro";
		facts.add(fact2);
		
		fact1 = new Fact();
		fact1.subject = "cachorro";
		fact1.relation = "NotIsA";
		fact1.object = "pássaro";
		facts.add(fact1);
		factsConceptNet.put("NotIsA", facts);
		
		facts = new ArrayList<Fact>();
		fact1 = new Fact();
		fact1.subject = "gato";
		fact1.relation = "IsA";
		fact1.object = "felino";
		facts.add(fact1);
		factsConceptNet.put("IsA", facts);
		
		facts = new ArrayList<Fact>();
		fact1 = new Fact();
		fact1.subject = "bem";
		fact1.relation = "Antonym";
		fact1.object = "mal";
		facts.add(fact1);
		
		fact1 = new Fact();
		fact1.subject = "bem";
		fact1.relation = "Antonym";
		fact1.object = "coisa ruim";
		facts.add(fact1);
		
		fact1 = new Fact();
		fact1.subject = "dia";
		fact1.relation = "Antonym";
		fact1.object = "noite";
		facts.add(fact1);
		factsConceptNet.put("Antonym", facts);
		
		facts = new ArrayList<Fact>();
		fact1 = new Fact();
		fact1.subject = "mal";
		fact1.relation = "InheritsFrom";
		fact1.object = "ruim";
		facts.add(fact1);
		
		fact1 = new Fact();
		fact1.subject = "noite";
		fact1.relation = "InheritsFrom";
		fact1.object = "lua";
		facts.add(fact1);
		factsConceptNet.put("InheritsFrom", facts);
		
		facts = new ArrayList<Fact>();
		fact1 = new Fact();
		fact1.subject = "homem";
		fact1.relation = "RelatedTo";
		fact1.object = "mulher";
		facts.add(fact1);
		
		fact1 = new Fact();
		fact1.subject = "Brasil";
		fact1.relation = "RelatedTo";
		fact1.object = "país";
		facts.add(fact1);
		
		fact1 = new Fact();
		fact1.subject = "homem";
		fact1.relation = "RelatedTo";
		fact1.object = "comida";
		facts.add(fact1);
		factsConceptNet.put("RelatedTo", facts);
		
		facts = new ArrayList<Fact>();
		fact1 = new Fact();
		fact1.subject = "mulher";
		fact1.relation = "Synonym";
		fact1.object = "dama";
		facts.add(fact1);
		
		fact1 = new Fact();
		fact1.subject = "mulher";
		fact1.relation = "Synonym";
		fact1.object = "garota";
		facts.add(fact1);
		
		fact1 = new Fact();
		fact1.subject = "país";
		fact1.relation = "Synonym";
		fact1.object = "pátria";
		facts.add(fact1);
		
		fact1 = new Fact();
		fact1.subject = "comida";
		fact1.relation = "Synonym";
		fact1.object = "refeição";
		facts.add(fact1);
		factsConceptNet.put("Synonym", facts);
		
		ArrayList<Rule> rulesAmie = new ArrayList<Rule>();
		Rule rule = new Rule();
		rule.body = new ArrayList<Fact>();
		Fact body1 = new Fact();
		body1.subject = "?b";
		body1.relation = "NotIsA";
		body1.object = "?a";
		rule.body.add(body1);
		
		rule.head = new Fact();
		rule.head.subject = "?a";
		rule.head.relation = "NotIsA";
		rule.head.object = "?b";
		
		rulesAmie.add(rule);
		
		rule = new Rule();
		rule.body = new ArrayList<Fact>();
		body1 = new Fact();
		body1.subject = "?b";
		body1.relation = "Antonym";
		body1.object = "?f";
		rule.body.add(body1);
		
		body1 = new Fact();
		body1.subject = "?f";
		body1.relation = "InheritsFrom";
		body1.object = "?a";
		rule.body.add(body1);
		
		rule.head = new Fact();
		rule.head.subject = "?a";
		rule.head.relation = "InheritsFrom";
		rule.head.object = "?b";
		
		rulesAmie.add(rule);
		
		rule = new Rule();
		rule.body = new ArrayList<Fact>();
		body1 = new Fact();
		body1.subject = "?a";
		body1.relation = "RelatedTo";
		body1.object = "?b";
		rule.body.add(body1);
		
		body1 = new Fact();
		body1.subject = "?b";
		body1.relation = "Synonym";
		body1.object = "?a";
		rule.body.add(body1);
		
		rule.head = new Fact();
		rule.head.subject = "?a";
		rule.head.relation = "InheritsFrom";
		rule.head.object = "?b";
		
		rulesAmie.add(rule);*/
	}
	
	public static void infersTerms(String pathData, String pathOutput, String pathConceptNet, boolean removeNonText) {
	
		System.out.println("Reading the rules/relations of the conceptNet");
		Hashtable<String, Hashtable<String,Relation>> rules = readRelations(pathConceptNet);
		//System.out.println(rules);
		System.out.println("Finished of to read the rules/relations of the conceptNet");
		
		ArrayList<String> fileOutput = new ArrayList<String>();
		for (File out : new File(pathOutput).listFiles())
			fileOutput.add(out.getName());
		
		int f = 0;
		File directory = new File(pathData);
		for (File fileSource : directory.listFiles()) {
		
			if (fileOutput.contains(fileSource.getName())) continue;
			
			System.out.println(++f + " (" + fileSource.getName() + ")");
			FileData file = new FormatFile().readFormatFileByFile(fileSource.getAbsolutePath());
			
			if (removeNonText) {
				System.out.println("Removing non-text terms...");
				ArrayList<Token> tokenRemoved = new ArrayList<Token>();
				for (Token token : file.getTokens()) {
					if (token.features.get(features.ofText.getFeature()) == 0)
						tokenRemoved.add(token);
				}
				file.getTokens().removeAll(tokenRemoved);
			}
			
			HashSet<String> tokensText = new HashSet<String>();
			for (Token token : file.getTokens())
				tokensText.add(token.getLemma());
			
			//System.out.println("Tags: " + file.getTags());
			//System.out.println("Tags originais: " + file.tagsOriginalFile);
			
			System.out.println("Finding non-text terms...");
			Hashtable<String, Token> tokenNT = new Hashtable<String, Token>();
			ArrayList<Token> tokensAdded = new ArrayList<Token>();
			for (Token token : file.getTokens()) {
				
				//if (!removeNonText && token.features.get(features.ofText.getFeature()) == 1)
				//	continue;
				
				if (rules.containsKey(token.getLemma())) {
					for (String object : rules.get(token.getLemma()).keySet()) {
						
						if (tokensText.contains(object)) continue;
						
						Token tokenAdd = new Token();
						if (tokenNT.containsKey(object)) {
							tokenAdd = tokenNT.get(object);
							tokenAdd.tokenWhoInfered.put(token, rules.get(token.getLemma()).get(object));
						}
						else {
							tokenAdd.setLemma(object);
							tokenAdd.features.put(features.ofText.getFeature(), 0.0);
							if (file.tagsOriginalFile.containsKey(object))
								tokenAdd.setClassification("Yes");
							else 
								tokenAdd.setClassification("No");
							tokenAdd.tokenWhoInfered.put(token, rules.get(token.getLemma()).get(object));
							tokensAdded.add(tokenAdd);
						}
						tokenNT.put(object, tokenAdd);
					}
				}
			}
			file.getTokens().addAll(tokensAdded);
			
			System.out.println("File: " + file.getName() + " (Tokens: " + file.getTokens().size() + ")");
			new FormatFile().writeFormatFileByFile(file, pathOutput);
		}
		
		String path = pathOutput;
		ArrayList<FileData> listFiles = new FormatFile().readFormatFiles(path);
		countTokens(listFiles);
	}
	
	public static void countTokens(ArrayList<FileData> listFiles) {
		
		int tokenNT = 0, tokenT = 0, tagT = 0, tagNT = 0;
		for (FileData file : listFiles) {
			for (Token token : file.getTokens()) {
				if (token.getClassification().equals("Yes")) {//tags
					if (token.features.get(features.ofText.getFeature()) == 0)
						tagNT++;
					else
						tagT++;
				} else {
					if (token.features.get(features.ofText.getFeature()) == 0)
						tokenNT++;
					else
						tokenT++;
				}
			}
		}
		
		System.out.println("Tags: " + (tagNT+tagT) + " (Text: " + tagT + "; Non-text: " + tagNT + ")");
		System.out.println("Tokens: " + (tokenNT+tokenT) + " (Text: " + tokenT + "; Non-text: " + tokenNT + ")");
	}
	
	public static Hashtable<String, Hashtable<String,Relation>> readRelations(String path) {
		
		Hashtable<String, Hashtable<String,Relation>> rules = new Hashtable<String, Hashtable<String,Relation>>();
		
		try {
			
			FileReader file = new FileReader(path);
			BufferedReader fileBuff = new BufferedReader(file);
		
			String line = "";
			while((line = fileBuff.readLine()) != null) {
				
				String subject = line.substring(0, line.indexOf("\t")).replace("<", "").replace(">", "").replace("_", " ");
				String relation = line.substring(line.indexOf("\t")+1, line.lastIndexOf("\t")).replace("<", "").replace(">", "").replace("_", " ");
				String object = line.substring(line.lastIndexOf("\t")+1, line.length()).replace("<", "").replace(">", "").replace("_", " ");
				
				//Subject
				Hashtable<String, Relation> relations = new Hashtable<String, Relation>();
				Relation rel = new Relation();
				if (rules.containsKey(subject)) {
					relations = rules.get(subject);
					if (relations.containsKey(object))
						rel = relations.get(object);
				}
				rel.relation.add(relation);
				relations.put(object, rel);
				rules.put(subject, relations);
				
				//Object
				/*Hashtable<String, Relation> relationsObject = new Hashtable<String, Relation>();
				Relation relObject = new Relation();
				if (rules.containsKey(object)) {
					relationsObject = rules.get(object);
					if (relationsObject.containsKey(subject))
						relObject = relationsObject.get(subject);
				}
				relObject.relation.add(relation);
				relationsObject.put(subject, relObject);
				rules.put(object, relationsObject);*/
				
			}
			file.close();
			fileBuff.close();
			
		} catch(Exception e) { e.printStackTrace(); }
		
		return rules;
	}
	
	public static void addRelationsPredictedAmie(Hashtable<String, ArrayList<Fact>> factsConceptNet, Hashtable<String, Hashtable<String, ArrayList<Fact>>> factsConceptNetSubject, ArrayList<Rule> rulesAmie, String path) {

		try {
			
			//HashSet<String> relationsAmie = new HashSet<String>();
			for(Rule rule : rulesAmie) {			
				System.out.println(rule);
				FileWriter fileWriter = new FileWriter(path, true);
				
				ArrayList<Rule> rulesAnalysed = new ArrayList<Rule>();
				
				int indexFactBody = 0;
				for(Fact fact : rule.body) {
					
					//System.out.println(fact);
					ArrayList<Rule> rulesRemoved = new ArrayList<Rule>();

					if (factsConceptNet.containsKey(fact.relation)) {
						
						System.out.println("Qtde relations (" + fact.relation + "): " + factsConceptNet.get(fact.relation).size());
						
						ArrayList<Rule> newRules = new ArrayList<Rule>();
						//for (Fact factConceptNet : factsConceptNet.get(fact.relation)) {
						//	++r;
						//	if (r % 5000 == 0)
						//		System.out.println(r + " (" + fact.relation +  " - " + factsConceptNet.get(fact.relation).size() + ") " + rule);
							//System.out.println("Fato da vez: " + factConceptNet);
							boolean existsVariable = false;
							
							for (int i = 0; i < indexFactBody; i++) {
								if (rule.body.get(i).subject.equals(fact.subject) ||
									rule.body.get(i).subject.equals(fact.object) ||
									rule.body.get(i).object.equals(fact.subject) ||
									rule.body.get(i).object.equals(fact.object)) {
									
									existsVariable = true;
									
									System.out.println("Count rulesAnalysed: " + rulesAnalysed.size());
									//int x = 0;
									for(Rule analyse : rulesAnalysed) {
										//++x;
										//if (x % 1000 == 0)
										//	System.out.println(x);
										
										ArrayList<Fact> factsEspecifics = factsConceptNet.get(fact.relation);
										if (rule.body.get(i).subject.equals(fact.subject))
											factsEspecifics = factsConceptNetSubject.get(fact.relation).get(analyse.body.get(i).subject);
										if (rule.body.get(i).object.equals(fact.subject))
											factsEspecifics = factsConceptNetSubject.get(fact.relation).get(analyse.body.get(i).object);
										
										if (factsEspecifics == null || factsEspecifics.size() == 0) continue;
										
										for (Fact factConceptNet : factsEspecifics) {
										if (
											(rule.body.get(i).subject.equals(fact.subject) && analyse.body.get(i).subject.equals(factConceptNet.subject) &&
											(!rule.body.get(i).object.equals(fact.object) || 
											(rule.body.get(i).object.equals(fact.object) && analyse.body.get(i).object.equals(factConceptNet.object)))) 
											||
											(rule.body.get(i).subject.equals(fact.object) && analyse.body.get(i).subject.equals(factConceptNet.object) &&
											(!rule.body.get(i).object.equals(fact.subject) || 
											(rule.body.get(i).object.equals(fact.subject) && analyse.body.get(i).object.equals(factConceptNet.subject)))) 
											||
											(rule.body.get(i).object.equals(fact.subject) && analyse.body.get(i).object.equals(factConceptNet.subject) &&
											(!rule.body.get(i).subject.equals(fact.object) || 
											(rule.body.get(i).subject.equals(fact.object) && analyse.body.get(i).subject.equals(factConceptNet.object)))) 
											||	
											(rule.body.get(i).object.equals(fact.object) && analyse.body.get(i).object.equals(factConceptNet.object) &&
											(!rule.body.get(i).subject.equals(fact.subject) || 
											(rule.body.get(i).subject.equals(fact.subject) && analyse.body.get(i).subject.equals(factConceptNet.subject))))
											){
											
											//System.out.println("Analisando: " + analyse);
											//System.out.println(analyse.body.size() + " " + indexFactBody);
											
											if (analyse.body.size() == indexFactBody) 
												analyse.body.add(factConceptNet);
											else {
												Rule newRule = new Rule();
												newRule.body = (ArrayList<Fact>)analyse.body.clone();
												newRule.body.get(indexFactBody).subject = factConceptNet.subject;
												newRule.body.get(indexFactBody).relation = factConceptNet.relation;
												newRule.body.get(indexFactBody).object = factConceptNet.object;
												newRules.add(newRule);
											} 
											//System.out.println("Add: " + analyse);
										}
										
										if (rule.body.size() < indexFactBody)
											rulesRemoved.add(rule);
										}
									}
								}
							}
							
							if (!existsVariable) {
								for (Fact factConceptNet : factsConceptNet.get(fact.relation)) {
									Rule ruleAdd = new Rule();
									ruleAdd.body.add(factConceptNet);
									rulesAnalysed.add(ruleAdd);
								}
								//System.out.println("Add: " + ruleAdd);
							}
						//}
						if (newRules.size() > 0)
							rulesAnalysed.addAll(newRules);
					}
					
					indexFactBody++;
					
					//for(Rule aux : rulesAnalysed) {
					//	if (aux.body.size() < indexFactBody)
					//		rulesRemoved.add(aux);
					//}
					
					if (rulesRemoved != null && rulesRemoved.size() > 0)
						rulesAnalysed.removeAll(rulesRemoved);
				}
				
				//int j = 0;
				System.out.println("Qtde de regras produzidas: " + rulesAnalysed.size());
				for (Rule rulesPredicted : rulesAnalysed) {
				
					if (rulesPredicted.body.size() < rule.body.size()) continue;
					
					//j++;
					//if (j%1000 == 0)
					//	System.out.println(j);
					
					for (int i = 0; i < rule.body.size(); i++) {
						
						if (rule.body.get(i).subject.equals(rule.head.subject)) rulesPredicted.head.subject = rulesPredicted.body.get(i).subject;
						if (rule.body.get(i).object.equals(rule.head.subject)) rulesPredicted.head.subject = rulesPredicted.body.get(i).object;
						if (rule.body.get(i).subject.equals(rule.head.object)) rulesPredicted.head.object = rulesPredicted.body.get(i).subject;
						if (rule.body.get(i).object.equals(rule.head.object)) rulesPredicted.head.object = rulesPredicted.body.get(i).object;
						
						if (rulesPredicted.head.subject != null && rulesPredicted.head.object != null) 
							break;
					}
					rulesPredicted.head.relation = rule.head.relation;
					
					//System.out.println("Rules predicted: " + rulesPredicted);
					String newRule = "<" + rulesPredicted.head.subject.replace(" ", "_") + ">\t<" + rulesPredicted.head.relation + ">\t<" + rulesPredicted.head.object.replace(" ", "_") + ">";
					
					//if (!relationsAmie.contains(newRule)) {
						//System.out.println(newRule);
						fileWriter.write(newRule + "\n");
					//	relationsAmie.add(newRule);
					//}
				}
				fileWriter.close();
				
				//System.out.println("Rules analysed: " + rulesAnalysed);
			}
			
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	public static Hashtable<String, ArrayList<Fact>> readFactsConceptNet(String path) {
		
		Hashtable<String, ArrayList<Fact>> facts = new Hashtable<String, ArrayList<Fact>>();
		try {
			//String path = "data/citeULike/conceptNet_relations_en.tsv";
			FileReader file = new FileReader(path);
			BufferedReader fileBuff = new BufferedReader(file);
		
			String line = "";
			while((line = fileBuff.readLine()) != null) {
				
				Fact fact = new Fact();
				fact.subject = line.substring(0, line.indexOf("\t")).replace("<", "").replace(">", "").replace("_", " ");
				fact.relation = line.substring(line.indexOf("\t")+1, line.lastIndexOf("\t")).replace("<", "").replace(">", "").replace("_", " ");
				fact.object = line.substring(line.lastIndexOf("\t")+1, line.length()).replace("<", "").replace(">", "").replace("_", " ");
				
				ArrayList<Fact> factsRelation = new ArrayList<Fact>();
				if (facts.containsKey(fact.relation))
					factsRelation = facts.get(fact.relation);
				factsRelation.add(fact);
				facts.put(fact.relation, factsRelation);
			}
			
			fileBuff.close();
			file.close();
			
		} catch(Exception e) { e.printStackTrace(); }
		return facts;
	}
	
public static Hashtable<String, Hashtable<String,ArrayList<Fact>>> readFactsSubjectConceptNet(String path) {
		
		Hashtable<String, Hashtable<String,ArrayList<Fact>>> facts = new Hashtable<String, Hashtable<String,ArrayList<Fact>>>();
		try {
			//String path = "data/citeULike/conceptNet_relations_en.tsv";
			FileReader file = new FileReader(path);
			BufferedReader fileBuff = new BufferedReader(file);
		
			String line = "";
			while((line = fileBuff.readLine()) != null) {
				
				Fact fact = new Fact();
				fact.subject = line.substring(0, line.indexOf("\t")).replace("<", "").replace(">", "").replace("_", " ");
				fact.relation = line.substring(line.indexOf("\t")+1, line.lastIndexOf("\t")).replace("<", "").replace(">", "").replace("_", " ");
				fact.object = line.substring(line.lastIndexOf("\t")+1, line.length()).replace("<", "").replace(">", "").replace("_", " ");
				
				Hashtable<String, ArrayList<Fact>> factsSubject = new Hashtable<String, ArrayList<Fact>>();
				ArrayList<Fact> factsRelation = new ArrayList<Fact>();
				if (facts.containsKey(fact.relation)) {
					factsSubject = facts.get(fact.relation);
					if (factsSubject.containsKey(fact.subject))
						factsRelation = factsSubject.get(fact.subject);
				}
				factsRelation.add(fact);
				factsSubject.put(fact.subject, factsRelation);
				facts.put(fact.relation, factsSubject);
			}
			
			fileBuff.close();
			file.close();
			
		} catch(Exception e) { e.printStackTrace(); }
		return facts;
	}
	
	public static ArrayList<Rule> readRelationsAmie(String path) {
		
		ArrayList<Rule> rules = new ArrayList<Rule>();
		try {
			
			FileReader file = new FileReader(path);
			BufferedReader fileBuff = new BufferedReader(file);
			
			String line = "";
			while((line = fileBuff.readLine()) != null) {
				
				if (!line.contains("=>")) continue;
				
				StringTokenizer tokens = new StringTokenizer(line, " ");
				Rule rule = new Rule();
				boolean body = true;
				
				//System.out.println("Tokens: " + tokens);
				while (tokens.hasMoreTokens()) {
					
					Fact fact = new Fact();
					fact.subject = tokens.nextToken();
					
					if (fact.subject.equals("=>")) {
						body = false;
						continue;
					}
					
					//System.out.println(line);
					
					fact.relation = tokens.nextToken().replace("<", "").replace(">", "");
					String object = tokens.nextToken();
					fact.object = object.contains("\t") ? object.substring(0, object.indexOf("\t")) : object;
					
					if (body)
						rule.body.add(fact);
					else
						rule.head = fact;
					
					if (!body)
						break;
				}
				rules.add(rule);
			}
			
			file.close();
			fileBuff.close();
		
		} catch(Exception e) { e.printStackTrace(); }
		return rules;
	}
	
	public static void readFile() {
		try {
			String pathOutput = "data/citeULike/conceptNet_relations_en.tsv";
			
			int count = 0;
			Scanner scanner = new Scanner(new FileReader(pathOutput)).useDelimiter("\n");
			while (scanner.hasNext()) {
				System.out.println(scanner.next());
				count++;
			}
			System.out.println(count);

			
			/*FileReader file = new FileReader(pathOutput);
			BufferedReader fileBuff = new BufferedReader(file);
		
			String line = "";
			while((line = fileBuff.readLine()) != null) {
				line = fileBuff.readLine();
				System.out.println(line);
			}*/
		}
		catch(Exception e) { e.printStackTrace(); }
	}
	
	public static void ajustFileRelations() {
		
		try {
			String path = "data/citeULike/conceptNet_relations.tsv";
			FileReader file = new FileReader(path);
			BufferedReader fileBuff = new BufferedReader(file);
			
			String pathOutput = "data/citeULike/conceptNet_relations_en.tsv";
			FileWriter fileWriter = new FileWriter(pathOutput, false);
			
			int countLine = 0;
			String line = "";
			while(line != null) {
				
				line = fileBuff.readLine();
				countLine++;
				
				//Ajusts the text to replace empty space by underscore and add the caracters < > before and after the subject, relation and object
				String subject = "<" + line.substring(0, line.indexOf("\t")).trim() + ">";
				String relation = "<" + line.substring(line.indexOf("\t") + 1, line.lastIndexOf("\t")).trim() + ">";
				String object = "<" + line.substring(line.lastIndexOf("\t") + 1, line.length()).trim() + ">";
				line = line.replace(" ", "_");
				
				if (!subject.equals("<>") && !relation.equals("<>") && !object.equals("<>")) {
					String rule = subject + "\t" + relation + "\t" + object;
					fileWriter.write(rule + "\n");
					System.out.println(rule);
				}
			}
			System.out.println("Qtde de linhas: " + countLine);
			fileBuff.close();
			file.close();
			fileWriter.close();
		}
		catch(Exception e) { e.printStackTrace(); }
	}
	
	public static void getAllRelationsConeptNet() {
		try {
			ConceptNet conceptNet = new ConceptNet();
			System.out.println("Initializing...");
			Hashtable<String, List<Assertions>> listAssertions = conceptNet.getAllAssertions();
			System.out.println("Finished of the obtaining the concepts");
			
			String pathOutput = "data/citeULike/conceptNet_relations.tsv";
			FileWriter fileWriter = new FileWriter(pathOutput, false);
			
			System.out.println("Writing the relations: " + listAssertions.size());
			int i = 0, j = 0;
			for(String key : listAssertions.keySet()) {
				System.out.println("Assertion: " + ++i + " (" + listAssertions.get(key).size() + ")");
				j = 0;
				for (Assertions assertion : listAssertions.get(key)) {
					System.out.println("Relation: " + ++j);
					fileWriter.write(assertion.getNameStart() + "\t" + assertion.getRelation() + "\t" + assertion.getNameEnd() + "\n");
				}
			}
			fileWriter.close();
		} 
		catch(Exception e) { e.printStackTrace(); }
	}
}

class Rule
{
	ArrayList<Fact> body = new ArrayList<Fact>();
	Fact head = new Fact();
	
	@Override
	public String toString() {
		String rule = "";
		
		for (Fact fact : body) 
			rule += fact.toString() + " ";
		
		rule += "=> " + head.toString();
		
		return rule;
	}
}

class Fact
{
	String subject;
	String object;
	String relation;
	
	@Override
	public String toString() {
		return subject + "\t<" + relation + ">\t" + object;
	}
}
