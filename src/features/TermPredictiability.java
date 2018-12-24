package features;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import features.Features.features;
import util.FileData;
import util.Token;

public class TermPredictiability {
	
	public static ArrayList<RuleAssociation> rulesAssociation = new ArrayList<TermPredictiability.RuleAssociation>();
	ArrayList<FileData> listFiles = null;
	public TermPredictiability (ArrayList<FileData> listFiles) {
		this.listFiles = listFiles;
	}
	
	/**
	 * Gets the terms frequents, ie, superior than minSuppport
	 * @author raquelsilveira
	 * @date 25/03/2015
	 * @param listTerm
	 * @param minSupport
	 * @return
	 */
	private Hashtable<String, Integer> getTokensSuperiorMinSup(Hashtable<String, Integer> listTerm, int minSupport) {
		Hashtable<String, Integer> tokensFrequents = new Hashtable<String, Integer>();
		for (String tagLemma : listTerm.keySet()) {
			if (listTerm.get(tagLemma) >= minSupport)
				tokensFrequents.put(tagLemma, listTerm.get(tagLemma));
		}
		return tokensFrequents;
	}
	
	public class RuleAssociation {
		ArrayList<String> antecedent = new ArrayList<String>();
		String consequent;
		int frequency;
		double confidence;
	}
	
	/**
	 * Gets the rules association with two terms: one tag as antecedent and one token as consequent
	 * @author raquelsilveira
	 * @date 25/03/2015
	 * @param tagsFrequents
	 * @param tokensFrequents
	 */
	private ArrayList<RuleAssociation> getFrequencyK2(Hashtable<String, Integer> tagsFrequents, Hashtable<String, Integer> tokensFrequents, int minSupport, Hashtable<String, Hashtable<String, String>> tokensFile) {
		ArrayList<RuleAssociation> rules = new ArrayList<TermPredictiability.RuleAssociation>();
		System.out.println("Qtde tags: " + tagsFrequents.size());
		int i = 0;
		for (String tag : tagsFrequents.keySet()) {
			System.out.println(i++ + " (" + tokensFrequents.size() + ")");
			int n = 0;
			for (String token : tokensFrequents.keySet()) {
				if (tag.equals(token)) continue;
				System.out.println(n++);
				int freq = 0;
				for (FileData file : listFiles) {
					if (tokensFile.get(file.getName()).containsKey(tag) && 
						tokensFile.get(file.getName()).get(tag).equals("Yes") && 
						tokensFile.get(file.getName()).containsKey(token))
						freq++;
				}
				
				if (freq >= minSupport) {
					RuleAssociation r = new RuleAssociation();
					ArrayList<String> ant = new ArrayList<String>();
					ant.add(tag);
					r.antecedent = ant;
					r.consequent = token;
					r.frequency = freq;
					r.confidence = (double) r.frequency / tagsFrequents.get(tag);
					rules.add(r);
				}
			}
		}
		return rules;
	}
	
	/**
	 * Gets the rules association with three terms: two tag as antecedent and one token as consequent
	 * @author raquelsilveira
	 * @date 25/03/2015
	 * @param tagsFrequents
	 * @param tokensFrequents
	 */
	private ArrayList<RuleAssociation> getFrequencyK3(Hashtable<String, HashSet<String>> consequents, int minSupport, Hashtable<String, Hashtable<String, String>> tokensFile) {
		
		ArrayList<RuleAssociation> rules = new ArrayList<TermPredictiability.RuleAssociation>();
		System.out.println("Qtde consequents: " + consequents.size());
		int n = 0;
		for (String token : consequents.keySet()) {
			System.out.println(n++);
			int indexTag1 = 0;
			for (String tag1 : consequents.get(token)) {
				for (int i = indexTag1+1; i < consequents.get(token).size(); i++) {
					String tag2 = (String)consequents.get(token).toArray()[i];
					if (tag1.equals(tag2)) continue;
					int freq = 0;
					for (FileData file : listFiles) {
						if (tokensFile.get(file.getName()).containsKey(tag1) && 
							tokensFile.get(file.getName()).get(tag1).equals("Yes") &&
							tokensFile.get(file.getName()).containsKey(tag2) && 
							tokensFile.get(file.getName()).get(tag2).equals("Yes") &&
							tokensFile.get(file.getName()).containsKey(token))
							freq++;
					}
					
					if (freq >= minSupport) {
						RuleAssociation r = new RuleAssociation();
						ArrayList<String> ant = new ArrayList<String>();
						ant.add(tag1);
						ant.add(tag2);
						r.antecedent = ant;
						r.consequent = token;
						r.frequency = freq;
						r.confidence = (double) r.frequency / frequencyTags(tokensFile, tag1, tag2, "");
						rules.add(r);
					}
				}
				indexTag1++;
			}
		}
		return rules;
	}
	
	/**
	 * Gets the rules association with four terms: three tag as antecedent and one token as consequent
	 * @author raquelsilveira
	 * @date 25/03/2015
	 * @param tagsFrequents
	 * @param tokensFrequents
	 */
	private ArrayList<RuleAssociation> getFrequencyK4(Hashtable<String, HashSet<String>> consequents, int minSupport, Hashtable<String, Hashtable<String, String>> tokensFile) {
		
		ArrayList<RuleAssociation> rules = new ArrayList<TermPredictiability.RuleAssociation>();
		System.out.println("Qtde consequents: " + consequents.size());
		int n = 0;
		for (String token : consequents.keySet()) {
			System.out.println(n++);
			int indexTag1 = 0;
			for (String tag1 : consequents.get(token)) {
				for (int i = indexTag1+1; i < consequents.get(token).size(); i++) {
					String tag2 = (String)consequents.get(token).toArray()[i];
					if (tag1.equals(tag2)) continue;
					for (int j = i+1; j < consequents.get(token).size(); j++) {
						String tag3 = (String)consequents.get(token).toArray()[j];
						if (tag1.equals(tag3)) continue;
						int freq = 0;
						for (FileData file : listFiles) {
							if (tokensFile.get(file.getName()).containsKey(tag1) && 
								tokensFile.get(file.getName()).get(tag1).equals("Yes") &&
								tokensFile.get(file.getName()).containsKey(tag2) && 
								tokensFile.get(file.getName()).get(tag2).equals("Yes") &&
								tokensFile.get(file.getName()).containsKey(tag3) && 
								tokensFile.get(file.getName()).get(tag3).equals("Yes") &&
								tokensFile.get(file.getName()).containsKey(token))
								freq++;
						}
						
						if (freq >= minSupport) {
							RuleAssociation r = new RuleAssociation();
							ArrayList<String> ant = new ArrayList<String>();
							ant.add(tag1);
							ant.add(tag2);
							ant.add(tag3);
							r.antecedent = ant;
							r.consequent = token;
							r.frequency = freq;
							r.confidence = (double) r.frequency / frequencyTags(tokensFile, tag1, tag2, tag3);
							rules.add(r);
						}
					}
				}
				indexTag1++;
			}
		}
		return rules;
	}
	
	private int frequencyTags(Hashtable<String, Hashtable<String, String>> tokensFile, String tag1, String tag2, String tag3) {
		int freq = 0;
		for (String file : tokensFile.keySet()) {
			if (tokensFile.get(file).containsKey(tag1) && 
				tokensFile.get(file).get(tag1).equals("Yes") &&
				tokensFile.get(file).containsKey(tag2) && 
				tokensFile.get(file).get(tag2).equals("Yes") &&
				(tag3.equals("") || (!tag3.equals("") && 
				tokensFile.get(file).containsKey(tag3) && 
				tokensFile.get(file).get(tag3).equals("Yes"))))
				freq++;
		}
		return freq;
	}
	
	/**
	 * Gets the tokens by file
	 * @author raquelsilveira
	 * @date 25/03/2015
	 * @return
	 */
	private Hashtable<String, Hashtable<String, String>> getTokensFile() {
		Hashtable<String, Hashtable<String, String>> tokensFile = new Hashtable<String, Hashtable<String, String>>();
		for (FileData file : listFiles) {
			Hashtable<String, String> tokens = new Hashtable<String, String>();
			for (Token token : file.getTokens()) {
				tokens.put(token.getLemma(), token.getClassification());
			}
			tokensFile.put(file.getName(), tokens);
		}
		return tokensFile;
	}
	
	/**
	 * Gets the tags by file
	 * @author raquelsilveira
	 * @date 25/03/2015
	 * @return
	 */
	private Hashtable<String, Hashtable<String, String>> getTagsFile() {
		Hashtable<String, Hashtable<String, String>> tokensFile = new Hashtable<String, Hashtable<String, String>>();
		for (FileData file : listFiles) {
			Hashtable<String, String> tokens = new Hashtable<String, String>();
			for (Token token : file.getTags()) {
				tokens.put(token.getLemma(), token.getClassification());
			}
			tokensFile.put(file.getName(), tokens);
		}
		return tokensFile;
	}
	
	/**
	 * Get all antecedents of each antecedent
	 * @author raquelsilveira
	 * @date 25/03/2015
	 * @param rules
	 * @return
	 */
	private Hashtable<String, HashSet<String>> divideRules(ArrayList<RuleAssociation> rules) {
		Hashtable<String, HashSet<String>> conseq = new Hashtable<String, HashSet<String>>();
		
		for (RuleAssociation rule : rules) {
			for (String ant : rule.antecedent) {
				if (conseq.containsKey(rule.consequent))
					conseq.get(rule.consequent).add(ant);
				else {
					HashSet<String> ants = new HashSet<String>();
					ants.add(ant);
					conseq.put(rule.consequent, ants);
				}
			}
		}
		return conseq;
	}
	
	/**
	 * Calculates the entropy
	 * @author raquelsilveira
	 * @date 25/03/2015
	 * @param rulesAssociation
	 * @return
	 */
	private Hashtable<String, Double> calculatesEntropy(ArrayList<RuleAssociation> rulesAssociation) {
		Hashtable<String, Double> entropy = new Hashtable<String, Double>();
		Hashtable<String, Integer> freq = new Hashtable<String, Integer>();
		
		for (RuleAssociation r : rulesAssociation) {
			//for (String ant : r.antecedent) {
				if (entropy.containsKey(r.consequent)) {
					entropy.put(r.consequent, entropy.get(r.consequent) + (r.confidence * Math.log(r.confidence)));
					freq.put(r.consequent, freq.get(r.consequent)+1);
				}
				else {
					entropy.put(r.consequent, r.confidence * Math.log(r.confidence));
					freq.put(r.consequent, 1);
				}	
			//}
		}
		
		for (String ant : entropy.keySet()) {
			entropy.put(ant, Math.abs((double) entropy.get(ant) / rulesAssociation.size()));// / freq.get(conseq)));
		}
		return entropy;
	}
	
	public void calculatePrectiabilityTest(ArrayList<FileData> listFilesTest) {
		System.out.println("Calculing term predictiability (test) ...");
		for (FileData file : listFilesTest) {
			for (Token t : file.getTokens()) {
				double sumConfid = 0; boolean found = false;
				for (RuleAssociation rule : TermPredictiability.rulesAssociation) {
					//for (String ant : rule.antecedent) {
						if (rule.consequent.equals(t.getLemma())) {
							sumConfid += rule.confidence * Math.log(rule.confidence);
							found = true;
						}
					//}
				}
				if (found)
					t.features.put(features.termPredictiability.getFeature(), Math.abs(sumConfid / TermPredictiability.rulesAssociation.size()));
				else
					t.features.put(features.termPredictiability.getFeature(), 1.0D);
			}
		}
	}
	
	/**
	 * Calculate the term predictiability
	 * @author raquelsilveira
	 * @date 08/01/2015
	 */
	public void calculatePredictiability() {
		
		//Assigns default value of termPredictiability to all tokens
		for (FileData file : listFiles) {
			for (Token t : file.getTokens())
				t.features.put(features.termPredictiability.getFeature(), 1.0D);
		}
		
		//Gets minSupport, obtained by frequency total of tags divided by quantity of distinct tags
		double minSupport = getMinSupport();
		
		//Gets the tags and tokens distinct and your frequency in files list
		Hashtable<String, Integer> tagsFrequency = getDistinctTags();
		//Hashtable<String, Integer> tokensFrequency = getDistinctTokens();
		
		//Gets the tokens whose frequency as tag is superior than minSupport
		//Hashtable<String, Integer> tokensFrequents = getTokensSuperiorMinSup(tokensFrequency, (int)minSupport);
		Hashtable<String, Integer> tagsFrequents = getTokensSuperiorMinSup(tagsFrequency, (int)minSupport);
		
		//Hashtable<String, Hashtable<String, String>> tokensFile = getTokensFile();
		Hashtable<String, Hashtable<String, String>> tokensFile = getTagsFile();
		
		//Gets the association rules with only one antecedent (tag) and one consequent (term - tag or no)
		ArrayList<RuleAssociation> rulesAssociation1 = getFrequencyK2(tagsFrequents, tagsFrequents, (int)minSupport, tokensFile);
		
		//Gets the association rules with three terms: two antecedents (tags) and one consequent (term - tag or no)
		//Hashtable<String, HashSet<String>> divideConseq = divideRules(rulesAssociation1);
		//ArrayList<RuleAssociation> rulesAssociation2 = getFrequencyK3(divideConseq, (int)minSupport, tokensFile);
		
		//Gets the association rules with four terms: three antecedents (tags) and one consequent (term - tag or no)
		//Hashtable<String, HashSet<String>> divideConseq3 = divideRules(rulesAssociation2);
		//ArrayList<RuleAssociation> rulesAssociation3 = getFrequencyK4(divideConseq3, (int)minSupport, tokensFile);
		
		TermPredictiability.rulesAssociation.addAll(rulesAssociation1);
		//TermPredictiability.rulesAssociation.addAll(rulesAssociation2);
		//TermPredictiability.rulesAssociation.addAll(rulesAssociation3);
		
		try {
			FileWriter write = new FileWriter(new File("data/newTest/old/associationRules/associationRules.txt"));
			for (RuleAssociation r : rulesAssociation) {
				String print = r.antecedent + " -> " + r.consequent + " = " + r.confidence + " (" + r.frequency + ")";
				System.out.println(print);
				write.write(print + "/n");
			}
			write.close();
		}
		catch(Exception e) {e.printStackTrace(); }
		
		Hashtable<String, Double> entropy = calculatesEntropy(rulesAssociation);
		
		System.out.println("TERM PREDICTIABILITY...");
		for (FileData file : listFiles) {
			for (Token t : file.getTokens()) {
			//for (Token t : file.getTags()) {
				if (entropy.containsKey(t.getLemma()))
					t.features.put(features.termPredictiability.getFeature(), entropy.get(t.getLemma()));
				else
					t.features.put(features.termPredictiability.getFeature(), 1.0D);
				
				System.out.println(t.getLemma() + " " + t.features.get(features.termPredictiability.getFeature()));
			}
		}
		
		
		/*Hashtable<ArrayList<Token>, Double> itemSet2 = getFrequencyItemSet2(tokensFrequents, minSupport);
		for (Token t : tokensFrequents) {
			double predictiab = 0.0D;
			int qttyrules = 0;
			for (ArrayList<Token> t2 : itemSet2.keySet()) {
				if (t.getName().equals(t2.get(0).getName())) {
					qttyrules++;
					predictiab += itemSet2.get(t2) * Math.log(itemSet2.get(t2));
				}
			}
			if (qttyrules > 0) {
				for (FileData file : listFiles) {
					for (Token token : file.getTokens()) {
						if (t.getName().equals(token.getName())) {
							token.features.put(features.termPredictiability.getFeature(), (Math.abs(predictiab)/qttyrules));
						}
					}
				}
			}
		}*/
	}
	
	/**
	 * Gets the frequency of each tag occurs together with another tag
	 * @author raquelsilveira
	 * @date 17/02/2015 
	 * @param tokensFrequents
	 * @param minSup
	 * @return
	 */
	private Hashtable<ArrayList<Token>, Double> getFrequencyItemSet2(ArrayList<Token> tokensFrequents, double minSup) {
		
		Hashtable<ArrayList<Token>, Double> predictTerms = new Hashtable<ArrayList<Token>, Double>();
		for (Token tokenT1 : tokensFrequents) {
			for (Token tokenT2 : tokensFrequents) {
				int count = 0;
				ArrayList<Token> freq = new ArrayList<Token>();
				freq.add(tokenT1);
				freq.add(tokenT2);
				if (!tokenT1.getName().equals(tokenT2.getName()) && !predictTerms.contains(freq)) {
					for (FileData file : listFiles) {
						boolean foundT1 = false, foundT2 = false;
						for (Token tokenCheck : file.getTags()) {
							if (!foundT1 && tokenCheck.getName().equals(tokenT1.getName())) foundT1 = true;
							if (!foundT2 && tokenCheck.getName().equals(tokenT2.getName())) foundT2 = true;
						}
						if (foundT1 && foundT2) count++;
					}
					if (count >= minSup)
						predictTerms.put(freq, (count/(double)getFrequencyTag(tokenT1)));
				}
			}
		}
		return predictTerms;
	}
	
	/**
	 * Get the minimum support
	 * @author raquelsilveira
	 * @date 09/01/2015
	 * @return
	 */
	private double getMinSupport() {
		
		int freqTags = 0;
		for (FileData file : listFiles)
			freqTags += file.getTags().size();
		
		return freqTags / (double)getDistinctTags().size();
	}
	
	/**
	 * Gets the distinct tags in the files
	 * @return list distinct tags
	 * @author raquelsilveira
	 * @date 17/02/2015
	 */
	private Hashtable<String, Integer> getDistinctTags() {
		//Key: Lemma token; Value: frequency
		Hashtable<String, Integer> tags = new Hashtable<String, Integer>();
		for(FileData file : listFiles) {
			for (Token t : file.getTags()) {
				if (tags.containsKey(t.getLemma()))
					tags.put(t.getLemma(), tags.get(t.getLemma())+1);
				else
					tags.put(t.getLemma(), 1);
			}
		}
		return tags;
	}
	
	/**
	 * Gets the distinct tokens in the files
	 * @author raquelsilveira
	 * @date 25/03/2015
	 * @return list of the distinct tokens and your frequency in list of the files
	 */
	private Hashtable<String, Integer> getDistinctTokens() {
		//Key: Lemma token; Value: frequency
		Hashtable<String, Integer> tokens = new Hashtable<String, Integer>();
		for(FileData file : listFiles) {
			for (Token t : file.getTokens()) {
				if (tokens.containsKey(t.getLemma()))
					tokens.put(t.getLemma(), tokens.get(t.getLemma())+1);
				else
					tokens.put(t.getLemma(), 1);
			}
		}
		return tokens;
	}
	
	/**
	 * Get the frequency term as tag in docs
	 * @author raquelsilveira
	 * @date 08/01/2015
	 * @param token
	 * @return
	 */
	private int getFrequencyTag(Token tag) {
		int freq = 0;
		for(FileData file : listFiles) {
			for (Token t : file.getTags()) {
				if (t.getLemma().equals(tag.getLemma()))
					freq++;
			}
		}
		return freq;
	}
}
