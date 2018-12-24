package util;

import java.util.ArrayList;
import java.util.Hashtable;

public class FileData implements Comparable<FileData>{
	
	private String name;
	//private String domain;
	//private String text;
	//private String textLemmatized;
	//private String title;
	//private String titleLemmatized;
	private int qttyTerms = 0;
	private ArrayList<Token> tokens = new ArrayList<Token>();
	//public Hashtable<Token, ArrayList<Token>> tokensInferenceNet = new Hashtable<Token, ArrayList<Token>>();
	public Hashtable<String, Integer> tagsOriginalFile = new Hashtable<String, Integer>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	/*public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public String getTextLemmatized() {
		return textLemmatized;
	}
	public void setTextLemmatized(String textLemmatizer) {
		this.textLemmatized = textLemmatizer;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitleLemmatized() {
		return titleLemmatized;
	}
	public void setTitleLemmatized(String titleLemmatized) {
		this.titleLemmatized = titleLemmatized;
	}
	*/
	
	public int getQttyTerms() {
		return qttyTerms;
	}
	public void setQttyTerms(int qttyTerms) {
		this.qttyTerms = qttyTerms;
	}
	
	public ArrayList<Token> getTokens() {
		return tokens;
	}
	public void setTokens(ArrayList<Token> tokens) {
		this.tokens = tokens;
	}
	
	public ArrayList<Token> getTags() {
		ArrayList<Token> tags = new ArrayList<Token>();
		for (Token t : tokens) {
			if (t.getClassification().equals("Yes"))
				tags.add(t);
		}
		return tags;
	}
	
	public Token getTokenByLemma(String lemma) {
		for (Token token : getTokens()) {
			if (token.getLemma().equals(lemma))
				return token;
		}
		return null;
	}
	
	@Override
	public int compareTo(FileData f) {
		return this.name.compareToIgnoreCase(f.name);
	}
}