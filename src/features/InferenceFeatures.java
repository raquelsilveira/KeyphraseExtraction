package features;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import preprocessing.FormatFile;
import features.Features.features;
import test.Calculate_TfIdf;
import util.*;

public class InferenceFeatures {

	/**
	 * Calculte the IFxIIF
	 * @author raquelsilveira
	 * @date 07/08/2016
	 * @param token
	 * @param frequencyTotal
	 * @param listTokensFiles
	 * @return
	 */
	public void calculateIf_IIf (Token token, int frequencyTotal, Hashtable<String, ArrayList<String>> listTokensFiles) {
		token.features.put(features.if_iif.getFeature(), calculateIf(token, frequencyTotal) * calculateIIF(listTokensFiles, token));
	}
	
	/**
	 * Calculate the Inference Frequency (IF)
	 * IF = The relation of the frequency inference of a non text term (ie quantity of text terms that infere it) by total frequency infere (ie IF of all non text terms)
	 * @author raquelsilveira
	 * @date 07/08/2016
	 * @param token
	 * @param frequencyTotal
	 * @return
	 */
	private double calculateIf(Token token, int frequencyTotal) {
		token.features.put(features.IF.getFeature(), (double) token.tokenWhoInfered.size() / frequencyTotal);
		return token.features.get(features.IF.getFeature());
	}
	
	/**
	 * Calculate the Inverse Inference Frequency (IIF)
	 * IIF = The relation of the frequency of a non text term appears in the documents of the corpus and number documents of the corpus
	 * @author raquelsilveira
	 * @date 07/08/2016  
	 * @param listTokensFiles
	 * @param token
	 * @return
	 */
	private double calculateIIF(Hashtable<String, ArrayList<String>> listTokensFiles, Token token){
		int frequencyCorpus = 0;
		for (String file : listTokensFiles.keySet()) {
			if (listTokensFiles.get(file).contains(token.getLemma())) {
				frequencyCorpus++;
				break;
			}
		}
		token.features.put(features.iif.getFeature(), (double) frequencyCorpus / listTokensFiles.size());
		return token.features.get(features.iif.getFeature());
	}
	
	private Hashtable<String, Double> calculateIIS (ArrayList<FileData> listFiles) {
		Hashtable<String, Double> relationsAis = new Hashtable<String, Double>(); 
		for (FileData file : listFiles) {
			Hashtable<String, ArrayList<Token>> relationsTs = new Hashtable<String, ArrayList<Token>>();
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 0) {
					
					HashSet<String> relations = new HashSet<String>();
					for(String whoInfers : token.tokenWhoInfered.keySet()) {
						relations.addAll(token.tokenWhoInfered.get(whoInfers).relation);
					}
					
					token.features.put(features.ts.getFeature(), (double)relations.size());
					
					for (String r : relations) {
						ArrayList<Token> listTokens = new ArrayList<Token>();
						if (relationsTs.containsKey(r))
							listTokens = relationsTs.get(r);
						listTokens.add(token);
						relationsTs.put(r, listTokens);
					}
				}
			}
			
			for (String r : relationsTs.keySet()) {
				Double iis = 0.0;
				for (Token t : relationsTs.get(r)) {
					iis += t.features.get(features.ts.getFeature());
				}
				
				iis /= relationsTs.get(r).size();
				
				if (relationsAis.containsKey(r))
					iis += relationsAis.get(r);
				
				relationsAis.put(r, iis);
			}
		}
		return relationsAis;
	}
	
	/**
	 * Calculate the features wTs, wIf, wIFt, SPI, SPIT
	 * @author raquelsilveira
	 * @date 07/08/2016
	 * @param listFiles
	 */
	public void calculateWeightedFeatures(ArrayList<FileData> listFiles) {
		Hashtable<String, Double> relationsAis = calculateIIS(listFiles);
		System.out.println(relationsAis.size());
		
		for (FileData file : listFiles) {
			
			double ssTotal = 0;
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 1)
					ssTotal += token.features.get(features.semanticSimilarity.getFeature());
			}
			
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 0) {
					Hashtable<String, Integer> relations = new Hashtable<String, Integer>();
					Hashtable<String, Double> relationsSemanticInfered = new Hashtable<String, Double>();
					for(String whoInfers : token.tokenWhoInfered.keySet()) {
						Token tokenWhoInfer = file.getTokenByLemma(whoInfers);
						for (String r : token.tokenWhoInfered.get(whoInfers).relation) {
							double ss = 0;
							if (relationsSemanticInfered.containsKey(r))
								ss = tokenWhoInfer.features.get(features.semanticSimilarity.getFeature());
							relationsSemanticInfered.put(r, ss);
							
							int qttyInference = 0;
							if (relations.containsKey(r))
								qttyInference = relations.get(r);
							qttyInference++;
							relations.put(r, qttyInference);
						}
					}
					
					double wTs = 0, wIft = 0, spit = 0;
					for (String r : relations.keySet()) {
						wTs += (double) relationsAis.get(r) / listFiles.size();
						double wIf = (double) relations.get(r) * relationsAis.get(r) / listFiles.size();
						wIft += wIf;
						token.features.put(features.wif.getFeature() + "_" + r, wIf);
						double spi = relationsSemanticInfered.get(r) / ssTotal; 
						spit += spi;
						token.features.put(features.spi.getFeature() + "_" + r, spi);
					}
					token.features.put(features.wts.getFeature(), wTs);
					token.features.put(features.wift.getFeature(), wIft);
					token.features.put(features.spiT.getFeature(), spit);
				}
			}
		}
	}
	
	public static void main (String args[]) {
		String path = "data/citeULike/teste/teste";
		ArrayList<FileData> listFiles = new FormatFile().readFormatFiles(path);
		InferenceFeatures inf = new InferenceFeatures();
		int textTerms = 0;
		Hashtable<String, ArrayList<String>> listTokensFiles = new Hashtable<String, ArrayList<String>>();
		for (FileData file : listFiles) {
			ArrayList<String> lemmaTokens = new ArrayList<String>();
			for (Token token : file.getTokens()) {
				lemmaTokens.add(token.getLemma());
				if (token.features.get(features.ofText.getFeature()) == 1)
					textTerms++;
			}
			listTokensFiles.put(file.getName(), lemmaTokens);
		}
		
		for (FileData file : listFiles) {
			for (Token token : file.getTokens()) {
				if (token.features.get(features.ofText.getFeature()) == 0) {
					System.out.println(token.getLemma());
					inf.calculateIf_IIf(token, textTerms, listTokensFiles);
					inf.calculateWeightedFeatures(listFiles);
					
					for (String f : token.features.keySet()) {
						System.out.println(f + ": " + token.features.get(f));
					}
				}
			}
		}
	}
}