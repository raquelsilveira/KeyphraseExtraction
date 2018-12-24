package wordNet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

public class ItemSynset {
	
	private String synsetOff;
	ArrayList<String> lemmas;
	public Hashtable<String, HashSet<String>> relations = new Hashtable<String, HashSet<String>>();
	
	public String getSynsetOff() {
		return synsetOff;
	}
	public void setSynsetOff(String synsetOff) {
		this.synsetOff = synsetOff;
	}
}
