package util;

import java.util.ArrayList;
import java.util.Hashtable;

import br.com.informar.knowledgebase.annotation.ConceptReference;
import br.com.informar.knowledgebase.model.Concept;

public class Token implements Comparable<Token>, Cloneable
{
	//private String name;
	public String fileName;
	private String lemma;
	//private String label;
	//private String tagName;
	private int beginIndex;
	private int endIndex;
	private int frequencyDoc = 0;
	
	//Used to LSA
	public int fileId;
	
	private String classification;
	public Double rankFunction = null;
	public Hashtable<String, Double> features = new Hashtable<String, Double>();
	//public Hashtable<String, Double> coefficients = new Hashtable<String, Double>();
	//public Hashtable<Token, ArrayList<String>> tokenWhoInfered = new Hashtable<Token, ArrayList<String>>();
	public Hashtable<String, Relation> tokenWhoInfered = new Hashtable<String, Relation>();
	//public Hashtable<Token, ArrayList<String>> tokenInfered = new Hashtable<Token, ArrayList<String>>();
	//public boolean originalInferenceNet = false;
	public String conceptWikipedia = null;
	public String mentionWikipedia = null;
	//public String role = null;
	
	/*public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}*/
	
	public String getLemma() {
		return lemma;
	}
	public void setLemma(String lemma) {
		this.lemma = lemma;
	}
	
	/*public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}*/
	
	/*public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}*/
	
	public int getBeginIndex() {
		return beginIndex;
	}
	public void setBeginIndex(int beginIndex) {
		this.beginIndex = beginIndex;
	}
	
	public int getEndIndex(){
		return endIndex;
	}
	public void setEndIndex(int endIndex){
		this.endIndex = endIndex;
	}
	
	public int getFrequencyDoc() {
		return frequencyDoc;
	}
	public void setFrequencyDoc(int frequencyDoc) {
		this.frequencyDoc = frequencyDoc;
	}
	
	public String getClassification() {
		return classification;
	}
	public void setClassification(String classification) {
		this.classification = classification;
	}
	
	
	public Double getRankFunction() {
		
		//This verification is to consider the classifiers with use the coefficients to calculate the rank function
		//In the Random Forest the rank function is calculated based in the average of the probabilities of the trees and seted by classifier 
		/*if (this.rankFunction == null && coefficients!= null && coefficients.size() > 0) {
			rankFunction = 0.0D;
			for (String f : features.keySet()) {
				rankFunction += coefficients.get(f) * features.get(f);
			}
		}*/
		return rankFunction;
	}
	
	//This attribute stores the quantity of term occurrence in documents as tags
	public int qttyOccurrence = 0;
	
	@Override
	public int compareTo(Token t) {
		//if (this.getRankFunction() == null){
			if (t.qttyOccurrence > this.qttyOccurrence)
				return -1;
			else if (t.qttyOccurrence < this.qttyOccurrence)
					return 1;
				 else return 0;
		/*}	
		else {
			if (this.getRankFunction() > t.getRankFunction())
				return -1;
			else if (this.getRankFunction() < t.getRankFunction())
					return 1;
				 else return 0;
		}*/
		/*if (this.getLemma().equals(t.getLemma()))
			return 1;
		return 0;*/
	}
	
	@Override
	public String toString() {
		//return features + " -> " + getRankFunction() + ", " + classification + ", " + name;
		return fileName + " " + features + " -> " + lemma + ", " + classification;
	}
	
	public Token clone() { 
		try { 
			// call clone in Object. 
			return (Token) super.clone(); 
		} 
		catch (CloneNotSupportedException e) { 
			System.out.println("Cloning not allowed."); 
			return this; 
		}
	}
}