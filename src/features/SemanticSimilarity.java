package features;

import java.io.File;
import java.security.Timestamp;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import preprocessing.ReadData;
import config.Config;
import features.Features.features;
import util.FileData;
import util.StanfordLemmatizer;
import util.Token;
import br.com.informar.knowledgebase.KnowledgeBase;
import br.com.informar.knowledgebase.RelatednessCache;
import br.com.informar.knowledgebase.ConceptComparer.LinkDirection;
import br.com.informar.knowledgebase.annotation.Context;
import br.com.informar.knowledgebase.collection.Concepts;
import br.com.informar.knowledgebase.collection.Mentions;
import br.com.informar.knowledgebase.db.MongoDB;
import br.com.informar.knowledgebase.model.Concept;
import br.com.informar.knowledgebase.model.Mention;

public class SemanticSimilarity {

	static KnowledgeBase knowledgeBase = null;
	static RelatednessCache relatednessCache = null;
	Context context = null;
	//Hashtable<String, Concept> disambiguateTerms = null;
	
	public static Hashtable<String, Mention> mentionsAll = null;
	//Hashtable<String, Concept> conceptsAll = null;
	//Hashtable<String, Set<Concept>> mentionConceptsAll = null;
	
	//Hashtable<String, Mention> mentionWiki = new Hashtable<String, Mention>();
	//Hashtable<String, Set<Concept>> conceptsMentionsAll = new Hashtable<String, Set<Concept>>();
	//Hashtable<String, Concept> conceptWiki = new Hashtable<String, Concept>();
	
	Hashtable<String, Concept> idCandidates = null;
	
	public static void getMentions() {
		
		MongoDB.changeMongoParameters(Config.SERVIDOR_DB, Config.PORT_DB, Config.DB_NAME);
		knowledgeBase = new KnowledgeBase(new File(Config.PATH_MODEL_CONFIG), Config.SERVIDOR_DB, Config.PORT_DB, Config.DB_NAME);
		relatednessCache = new RelatednessCache(knowledgeBase.getConceptComparer( ));
		
		System.out.println("Obtaining mentions");
		List<Mention> allMentions = knowledgeBase.getMentions().findAll();
		mentionsAll = new Hashtable<String, Mention>();
		for (Mention m : allMentions)
			mentionsAll.put(m.getName().toLowerCase(), m);
		System.out.println("Qtty mentions (KB): " + knowledgeBase.getMentions().count());
		System.out.println("Qtty mentions (mentionsAll): " + mentionsAll.size());
	}
	
	public SemanticSimilarity() {
		
		MongoDB.changeMongoParameters(Config.SERVIDOR_DB, Config.PORT_DB, Config.DB_NAME);
		knowledgeBase = new KnowledgeBase(new File(Config.PATH_MODEL_CONFIG), Config.SERVIDOR_DB, Config.PORT_DB, Config.DB_NAME);
		relatednessCache = new RelatednessCache(knowledgeBase.getConceptComparer( ));
		
		/*System.out.println("Obtaining concepts");
		List<Concept> allConcepts = knowledgeBase.getConcepts().findAll();
		conceptsAll = new Hashtable<String, Concept>();
		for (Concept c : allConcepts)
			conceptsAll.put(c.getName().toLowerCase(), c);*/
		
		/*System.out.println("Obtaining concepts/mentions");
		mentionConceptsAll = new Hashtable<String, Set<Concept>>();
		for (String conc : conceptsAll.keySet()) {
			
			System.out.println(conceptsAll.get(conc).mentionsName.size());
			
			for (String m : conceptsAll.get(conc).mentionsName) {
				if (mentionConceptsAll.containsKey(m.toLowerCase())) { 
					Set<Concept> listConc = mentionConceptsAll.get(m.toLowerCase());
					listConc.add(conceptsAll.get(conc));
					mentionConceptsAll.put(m.toLowerCase(), listConc);
				}
				else {
					Set<Concept> listConc = new HashSet<Concept>();
					listConc.add(conceptsAll.get(conc));
					mentionConceptsAll.put(m.toLowerCase(), listConc);
				}
			}
		}
		System.out.println(mentionConceptsAll.size());*/
	}
	
	/**
	 * Gets the concept of each term in wikipedia
	 * @author raquelsilveira
	 * @date 31/03/2015
	 * @param file
	 */
	public void getRelationsWikipedia (FileData file) {

		/*System.out.println("Obtendo relações wikipedia...");
		
		disambiguateTerms = new Hashtable<String, Concept>();
		Hashtable<String, Concept> disambiguateTermsText = new Hashtable<String, Concept>();
		Map<Mention, Concept> disambiguateMention = new HashMap<Mention, Concept>();
		Map<Mention, Concept> disambiguateMentionText = new HashMap<Mention, Concept>();
		
		int n = 0;
		for (Token token : file.getTokens()) {
			System.out.println(n++);
			Mention mention = knowledgeBase.getMentions().getByName(token.getLemma());
			
			if (mention != null) {
				
				//Obtains the concepts to each mention
				Set<Concept> conceptsMention = knowledgeBase.getConcepts().getConceptsByMention(mention);
				
				//Disambiguates mention
				Concept contextSense = getDisambigConcept(mention, conceptsMention);
				if (contextSense != null) {
					token.conceptWikipedia = contextSense.getName();
					disambiguateMention.put(mention, contextSense);
					disambiguateTerms.put(token.getLemma(), contextSense);
					
					if (token.features.get(features.ofText.getFeature()) == 1.0D) {
						disambiguateTermsText.put(token.getLemma(), contextSense);
						disambiguateMentionText.put(mention, contextSense);
					}
				}
			}
		}
		
		//Mentions disambiguated of the text terms (tags or no)
		context = new Context(disambiguateMentionText, relatednessCache, knowledgeBase.getMaxContextSize());
		
		//Gets the id of the concepts 
		idCandidates = new Hashtable<String, Concept>();
		for (String key : disambiguateTermsText.keySet()) {
			idCandidates.put(disambiguateTermsText.get(key).getId(), disambiguateTermsText.get(key));
		}*/
	}
	
	public void getConceptsWikipediaTokens (FileData file) {
		
		int j = 0;
		for (Token token : file.getTokens()) {
			System.out.println(++j);
			if (token.conceptWikipedia == null || token.conceptWikipedia.equals("")) {
				//Mention mention = mentionsAll.get(token.getLemma());
				Mention mention = null;
				if (mentionsAll.containsKey(token.getLemma()))
					mentionsAll.get(token.getLemma());
				else
					mention = knowledgeBase.getMentions().getByName(token.getLemma());
				
				if (mention != null) {
					//Set<Concept> conceptsMention = mentionConceptsAll.get(mention.getName().toLowerCase());
					//if (conceptsMention != null) {
					//	Concept contextSense = getDisambigConcept(mention, conceptsMention);
						
						//Obtains the concepts to each mention
						Set<Concept> conceptsMention = knowledgeBase.getConcepts().getConceptsByMention(mention);
						//Disambiguates mention
						Concept contextSense = getDisambigConcept(mention, conceptsMention);
						if (contextSense != null)
							token.conceptWikipedia = contextSense.getName();
					//}
				}
			}
		}
	}
	
	Map<Mention, Concept> disambiguateMentionText;
	
	/**
	 * Gets the concept of each term in wikipedia
	 * @author raquelsilveira
	 * @date 31/03/2015
	 * @param file
	 */
	public void getRelationsWikipediaLast (FileData file) {

		System.out.println("Obtendo relações wikipedia...");
		
		Hashtable<String, Concept> disambiguateTerms = new Hashtable<String, Concept>();
		Hashtable<String, Concept> disambiguateTermsText = new Hashtable<String, Concept>();
		Map<Mention, Concept> disambiguateMention = new HashMap<Mention, Concept>();
		disambiguateMentionText = new HashMap<Mention, Concept>();
		
		int j = 0;
		for (Token token : file.getTokens()) {
			
			//if (token.getClassification().equals("No") && token.features.get(features.ofText.getFeature()) == 0) continue;
			
			//System.out.println(++j);
			j++;
			if (j % 100 == 0) System.out.println(j + " de " + file.getTokens().size());
			
			Mention mention = null;
			if (mentionsAll != null && mentionsAll.containsKey(token.getLemma()))
				mention = mentionsAll.get(token.getLemma());
			else
				mention = knowledgeBase.getMentions().getByName(token.getLemma());
			
			if (mention != null) {
				token.mentionWikipedia = mention.getName();
				Concept contextSense = null;
				if (token.conceptWikipedia != null && !token.conceptWikipedia.equals("")) {
					//if (conceptsAll.containsKey(token.conceptWikipedia))
						//contextSense = conceptsAll.get(token.conceptWikipedia.toLowerCase());
					//else
						contextSense = knowledgeBase.getConcepts().getConceptByName(token.conceptWikipedia);
						
				} else {
					
					Set<Concept> conceptsMention = null;
					//Obtains the concepts to each mention
					//if (mentionConceptsAll.containsKey(mention.getName().toLowerCase()))
					//	conceptsMention = mentionConceptsAll.get(mention.getName().toLowerCase());
					//else { 
						conceptsMention = knowledgeBase.getConcepts().getConceptsByMention(mention);
						
					//	conceptsMentionsAll.put(mention.getName().toLowerCase(), conceptsMention);
					//}
					
					if (conceptsMention != null) {
						//Disambiguates mention
						contextSense = getDisambigConcept(mention, conceptsMention);
						if (contextSense != null)
							token.conceptWikipedia = contextSense.getName();
						else
							System.out.println("conceito sem nome: " + token.getLemma() + " -> " + contextSense);
					}
					else
						System.out.println("Mention sem conceitos -> " + mention.getName() + " (" + token.getLemma() + ")");
				}
			
				if (contextSense != null) {
					
					disambiguateMention.put(mention, contextSense);
					disambiguateTerms.put(token.getLemma(), contextSense);
					
					if (!token.features.containsKey(features.ofText.getFeature()))
    					token.features.put(features.ofText.getFeature(), 0.0);
					else if (token.features.get(features.ofText.getFeature()) == 1.0D) {
						disambiguateMentionText.put(mention, contextSense);
						disambiguateTermsText.put(token.getLemma(), contextSense);
					}
				}
			}
		}
		
		//Mentions disambiguated of the text terms (tags or no)
		//context = new Context(disambiguateMentionText, relatednessCache, knowledgeBase.getMaxContextSize());
		
		//Gets the id of the concepts 
		idCandidates = new Hashtable<String, Concept>();
		for (String key : disambiguateTermsText.keySet()) {
			idCandidates.put(disambiguateTermsText.get(key).getId(), disambiguateTermsText.get(key));
		}
	}
	
	
	/**
	 * Gets the concept disambiguation to a mention
	 * Obtained according Maui
	 * @author raquelsilveira
	 * @date 31/03/2015 
	 * @param mention
	 * @param concepts
	 * @return
	 */
	public Concept getDisambigConcept(Mention mention, Set<Concept> concepts) {
		
		//System.out.println(mention.getId() + " - " + mention.getName());
		Map<Concept, Integer> occurrencies = new Mentions().getOccurrencies(mention.getId());
		int occurrenciesTotalCount = 0;
		for (Integer occurrencyCount : occurrencies.values()) {
            occurrenciesTotalCount += occurrencyCount;
        }
		
		//for (Concept c : occurrencies.keySet())
		//	System.out.println(c != null ? c.getName() : " ");
		
		//System.out.println("occurrencies: " + occurrencies);
		//System.out.println("occurrenciesTotalCount: " + occurrenciesTotalCount);
		//System.out.println("concepts.size(): " + concepts.size());
		
		Iterator it = concepts.iterator();
		double maxPriorProbab = 0.0D;
		Concept conceptMax = null;
		while(it.hasNext()) {
			Concept concept = (Concept)it.next();
			if (occurrencies.containsKey(concept)) {
				//System.out.println("entrou aqui!");
				double prob = occurrenciesTotalCount != 0 ? (double) occurrencies.get(concept) / occurrenciesTotalCount : 0.0; //mention.getPriorProbability(concept);
				//System.out.println("prob:" + prob);
				if (prob >= maxPriorProbab) {
					maxPriorProbab = prob;
					conceptMax = concept;
				}
			} //else
				//System.out.println("occurrencies não contém o conceito: " + concept.getName());
		}
		
		//System.out.println("conceptMax: " + conceptMax);
		return conceptMax;
	}
	
	/**
	 * Calculates the similarity semantic of the all files terms
	 * @author raquelsilveira
	 * @date 31/03/2015
	 * @param file
	 */
	public void calculateSimilaritySemantic(FileData file) {
		
		/*try {
			for (Token token : file.getTokens()) {
				//System.out.println("Calculing feature similaritySemantic...");
				double similaritySemantic = 0;
				
				Concept concept = disambiguateTerms.get(token.getLemma());
				if (concept != null)
					similaritySemantic = context.getRelatednessTo(concept);
				
				token.features.put(features.semanticSimilarity.getFeature(), similaritySemantic);
			}
		}
		catch(Exception e) { e.printStackTrace(); }*/
	}
	
	/**
	 * Calculates the similarity semantic of the all files terms
	 * @author raquelsilveira
	 * @date 31/03/2015
	 * @param file
	 */
	public void calculateSimilaritySemanticLast(Token token) {
		
		try {
			//System.out.println("Calculing feature similaritySemantic...");
			//TODO: Retirar este if (provisoriamente para calcular a similaridade semantica dos termos não do texto obtidos do ConceptNet)
			if (token.conceptWikipedia != null) {
				double similaritySemantic = 0;
				//Concept concept = conceptsAll.get(token.conceptWikipedia.toLowerCase());
				Concept concept = knowledgeBase.getConcepts().getConceptByName(token.conceptWikipedia);
				similaritySemantic = context.getRelatednessTo(concept);
				
				
				
				token.features.put(features.semanticSimilarity.getFeature(), similaritySemantic);
			} else
				token.features.put(features.semanticSimilarity.getFeature(), 0.0);
		}
		catch(Exception e) { System.out.println("Erro ao obter o conceito: " + token.conceptWikipedia); e.printStackTrace(); }
	}
	
	public void calculateSimilaritySemanticLast2(Token token) {
		
		try {
			//System.out.println(token.getLemma());
			if (token.conceptWikipedia != null && !token.conceptWikipedia.equals("")) {
				double similaritySemantic = 0;
				//Concept concept = conceptsAll.get(token.conceptWikipedia.toLowerCase());
				Concept concept = knowledgeBase.getConcepts().getConceptByName(token.conceptWikipedia);
				
				ArrayList<LinkDirection> linkDirections = new ArrayList<LinkDirection>();
				linkDirections.add(LinkDirection.IN);
				//linkDirections.add(LinkDirection.OUT);
				
				for (Concept conceptContext : this.disambiguateMentionText.values()) {
					if (conceptContext != null) {
						for (LinkDirection linkDirection : linkDirections) {
							similaritySemantic += getRelatedness(conceptContext, concept, linkDirection);
						}
					}
				}
				token.features.put(features.semanticSimilarity.getFeature(), similaritySemantic/disambiguateMentionText.size());
			} else
				token.features.put(features.semanticSimilarity.getFeature(), 0.0);
		}
		catch(Exception e) { e.printStackTrace(); }
	}
	
	public void calculateSimilaritySemantic(Token token1, Token token2) {
		
		//System.out.println("Qtde de conceitos: " + knowledgeBase.getConcepts().count());
		
		double similaritySemantic = 0.0;
		
		//System.out.println(token1.getLemma() + " (" + token1.conceptWikipedia + ") <-> " + token2.getLemma() + " (" + token2.conceptWikipedia + ")");
		
		if (token1.conceptWikipedia != null && !token1.conceptWikipedia.equals("") &&
			token2.conceptWikipedia != null && !token2.conceptWikipedia.equals("")) {

			Concept concept1 = knowledgeBase.getConcepts().getConceptByName(token1.conceptWikipedia);
			Concept concept2 = knowledgeBase.getConcepts().getConceptByName(token2.conceptWikipedia);
		
			//System.out.println(concept1.getName() + " <-> " + concept2.getName());
			
			if (concept1 != null && concept2 != null) {
				ArrayList<LinkDirection> linkDirections = new ArrayList<LinkDirection>();
				linkDirections.add(LinkDirection.IN);
				//linkDirections.add(LinkDirection.OUT);
				
				for (LinkDirection linkDirection : linkDirections) {
					similaritySemantic += getRelatedness(concept1, concept2, linkDirection);
				}
				
				//similaritySemantic /= linkDirections.size();
			}
		}
		
		token1.tokenWhoInfered.get(token2).similaritySematic = similaritySemantic;
	}
	
	public void calculateSimilaritySemanticMilneWitten(FileData file) 
	{
		try 
		{
			//Obtem os tokens/conceitos do texto
			Hashtable<Token, Concept> tokensConceptsText = new Hashtable<Token, Concept>();
			for (Token token : file.getTokens())
			{
				if (token.features.get(features.ofText.getFeature()) == 1 &&
					token.conceptWikipedia != null && !token.conceptWikipedia.equals(""))
					tokensConceptsText.put(token, knowledgeBase.getConcepts().getConceptByName(token.conceptWikipedia));
			}
			
			//Calcula o peso dos conceitos conforme Milne & Witten (2008)
			Hashtable<Concept, Double> weightConceptsText = new Hashtable<Concept, Double>();
			double totalWeight = 0.0;
			double weight = 0.0;
			for (Token token : tokensConceptsText.keySet()) 
			{
				Concept concept1 = tokensConceptsText.get(token);
				double relatedness = 0.0;
				for (Token token2 : tokensConceptsText.keySet())
				{
					Concept concept2 = tokensConceptsText.get(token2);
					if (concept1 != concept2)
						relatedness += getRelatedness(concept1, concept2, LinkDirection.IN);
				}
				relatedness /= tokensConceptsText.size();
				weight = (double) (token.features.get(features.wikipediaKeyphraseness.getFeature()) + relatedness)/2.0;
				totalWeight += weight;
				weightConceptsText.put(concept1, weight);
				System.out.println("Conceito: " + concept1.getName() + " Peso: " + weight);
			}
			
			//Calcula a similaridade semantica dos tokens com os termos do texto
			for (Token token : file.getTokens()) {
				
				if (token.conceptWikipedia != null && !token.conceptWikipedia.equals("")) {
					System.out.println("Wikipedia: " + token.conceptWikipedia);
					Concept concept = knowledgeBase.getConcepts().getConceptByName(token.conceptWikipedia);
					 
					if (concept != null) {
						double relatedness = 0.0;
						for (Token tokenText : tokensConceptsText.keySet()) {
							if (tokenText != token) {
								double pesoConceitoText = weightConceptsText.get(tokensConceptsText.get(tokenText));
								relatedness += pesoConceitoText * getRelatedness(concept, tokensConceptsText.get(tokenText), LinkDirection.IN);
							}
						}
						
						relatedness /= (double) totalWeight;
						
						token.features.put(Features.features.semanticSimilarity.getFeature(), relatedness);
					} else 
						token.features.put(Features.features.semanticSimilarity.getFeature(), 0.0);
					
					System.out.println(token.getLemma() + " " + token.features.get(Features.features.semanticSimilarity.getFeature()));
				}
			}
		}
		catch(Exception e) 
		{ e.printStackTrace(); }
	}
	
	public double getRelatedness(Concept concept1, Concept concept2, LinkDirection linkDirection) {
        //System.out.println(concept1.getName() + " " + concept2.getName() + " " + linkDirection);
		
		if (concept1.equals(concept2)) {
            return 0.0;
        }
        
        Set<Concept> links1 = (linkDirection == LinkDirection.IN) ? concept1.linksIn  : concept1.linksOut;
        Set<Concept> links2 = (linkDirection == LinkDirection.IN) ? concept2.linksIn : concept2.linksOut;
        
        if (links1.isEmpty() || links2.isEmpty()) {       
            return 0.0;
        }
        
        Set<Concept> intersectionSet = new HashSet<Concept>(links1);
        intersectionSet.retainAll(links2);
        int intersection = intersectionSet.size();
 
        if (links1.contains(concept2)) {
            intersection++;
        }
        
        if (links2.contains(concept1)) {
            intersection++;
        }
        
        /*if (intersection > 0)
        	System.out.println("Intersection set: " + intersectionSet.iterator().next().getName());
        
        System.out.println("Intersection: " + intersection);*/
        
        double relatedness = 0.0;
        if (intersection != 0) {            
            double a = links1.size();
            double b = links2.size();
            double ab = intersection;
            double w = knowledgeBase.getConcepts().count();
            
            relatedness = 1 - (Math.log(Math.max(a,b)) - Math.log(ab)) / (Math.log(w) - Math.log(Math.min(a, b)));
            
            /*System.out.println("Links1.size: " + links1.size());
            System.out.println("Links2.size: " + links2.size());
            System.out.println("Intersection: " + intersection);
            System.out.println("Count concepts: " + w);
            System.out.println("Relatedness: " + relatedness);*/
        }
        return relatedness;
    }
	
	/**
	 * Calculates the similarity semantic according Maui
	 * @auhtor raquelsilveira
	 * @date 04/04/2015
	 * @param concept
	 * @param listConcepts
	 * @return
	 */
	public double calculateSimilaritySemanticMaui(Concept concept, ArrayList<Concept> listConcepts) {
	
		if (listConcepts.isEmpty())
			return 0.0D;
		
		double relatedness = 0.0D;
		for (Concept contextConcept : listConcepts) {
			double r = this.relatednessCache.getRelatedness(concept, contextConcept);
			relatedness += r;
		}
		return relatedness / (listConcepts.size() - 1);
	}
	
	/**
	 * Calculates the node degree, according Maui
	 * @author raquelsilveira
	 * @date 31/03/2015
	 * @param file
	 */
	public void calculateNodeDegree(FileData file) {
		
		//System.out.println("Obtencao dos candidatos...");
		//Gets the id of the concepts 
		/*Hashtable<String, Concept> idCandidates = new Hashtable<String, Concept>();
		for (String key : disambiguateTerms.keySet()) {
			idCandidates.put(disambiguateTerms.get(key).getId(), disambiguateTerms.get(key));
		}
		
		for (Token token : file.getTokens()) {
			//TODO: Retirar este if (provisoriamente para calcular o node degree dos termos não do texto obtidos do ConceptNet)
			if (token.features.get(features.ofText.getFeature()) == 0.0) {
				double nodeDegree = 0;
				if (disambiguateTerms.containsKey(token.getLemma())) {
					Concept concept = disambiguateTerms.get(token.getLemma());
					
					for (Concept relatedID : concept.getLinksIn()) {
						
						if (idCandidates.containsKey(relatedID.getId())) {
							nodeDegree++;
						}
					}
					
					for (Concept relatedID : concept.getLinksOut()) {
						if (idCandidates.containsKey(relatedID.getId())) {
							nodeDegree++;
						}
					}
				}
				token.features.put(features.nodeDegree.getFeature(), nodeDegree/(idCandidates.size()*2));
			}
		}*/
	}
	
	public void calculateNodeDegreeLast(Token token) {
		
		if (token.conceptWikipedia != null && !token.conceptWikipedia.equals("")) {
			Concept concept = null;
			//if (conceptsAll.containsKey(token.conceptWikipedia))
				//concept = conceptsAll.get(token.conceptWikipedia.toLowerCase());
			//else {
				concept = knowledgeBase.getConcepts().getConceptByName(token.conceptWikipedia); //conceptsAll.get(token.conceptWikipedia.toLowerCase());
			//	conceptWiki.put(token.conceptWikipedia, concept);
			//}
			double nodeDegree = 0;
			
			if (concept != null) {
				if (concept.getLinksIn() != null) {
					for (Concept relatedID : concept.getLinksIn()) {
						if (!concept.getId().equals(relatedID.getId()) && 
							idCandidates.containsKey(relatedID.getId())) {
							nodeDegree++;
						}
					}
				} else
					System.out.println(token.conceptWikipedia + " -> sem linksIn");
			
				if (concept.getLinksOut() != null) {
					for (Concept relatedID : concept.getLinksOut()) {
						if (!concept.getId().equals(relatedID.getId()) &&
							idCandidates.containsKey(relatedID.getId())) {
							nodeDegree++;
						}
					}
				} else
					System.out.println(token.conceptWikipedia + " -> sem linksOut");
			} else
				System.out.println(token.conceptWikipedia + " -> Concept null");
			
			token.features.put(features.nodeDegree.getFeature(), nodeDegree/(idCandidates.size()*2));
		} else
			token.features.put(features.nodeDegree.getFeature(), 0.0);
	}
	
	/**
	 * Calculates the inverse wikipedia linkage feature
	 * @author raquelsilveira
	 * @date 24/02/2015
	 * @param token
	 * @return
	 */
	public void calculatesInverseWikipediaLinkage(FileData file) {
		
		for (Token token : file.getTokens()) {
			Mention mention = knowledgeBase.getMentions().getByName(token.getLemma());
			
			if (mention != null) {
				
				//Obtains the concepts to each mention
				Set<Concept> conceptsMention = knowledgeBase.getConcepts().getConceptsByMention(mention);
				
				//Disambiguates mention
				Concept concept = getDisambigConcept(mention, conceptsMention);
				
				//TODO: 12.853.780 obtained adhoc in mongodob (represents the number links in wikipedia en)
				if (concept != null) {
					token.conceptWikipedia = concept.getName();
					double inv = (double) (concept.getLinksIn().size() / 81815863.0);
					if (inv > 81815863) {
						System.out.println("Error! Token: " + token.getLemma() + " Concept: " + concept.getName());
					}
					if (inv != 0) {
						//System.out.println("Token: " + token.getLemma() + " Concept: " + concept.getName() + " LinkIn: " + concept.getLinksIn().size() + " Inv: " +  -Math.log10(inv));
						token.features.put(features.inverseWikipediaLinkade.getFeature(), - Math.log(inv)/Math.log(2));
					} 
					else
						token.features.put(features.inverseWikipediaLinkade.getFeature(), 0.0D);
				}
				else
					token.features.put(features.inverseWikipediaLinkade.getFeature(), 0.0D);
			}
		}
	}
	
	/**
	 * Calculates the inverse wikipedia linkage feature
	 * @author raquelsilveira
	 * @date 24/02/2015
	 * @param token
	 * @return
	 */
	public void calculatesInverseWikipediaLinkageLast(Token token) {
		
		//System.out.println("Calculing inverseWikipediaLinkage...");
		if (token.conceptWikipedia != null && !token.conceptWikipedia.equals("")) {
			Concept concept = null;
			//if (conceptWiki.containsKey(token.conceptWikipedia))
				//concept = conceptsAll.get(token.conceptWikipedia.toLowerCase());
			//else {
				concept = knowledgeBase.getConcepts().getConceptByName(token.conceptWikipedia); //conceptsAll.get(token.conceptWikipedia.toLowerCase());
			//	conceptWiki.put(token.conceptWikipedia, concept);
			//}
			
			//TODO: 12.853.780 obtained adhoc in mongodob (represents the number links in wikipedia en)
			if (concept != null) {
				double inv = (double) concept.getLinksIn().size() / 12853780;//81815863.0);
				if (inv > 12853780) {
					System.out.println("Error! Token: " + token.getLemma() + " Concept: " + concept.getName());
				}
				if (inv != 0) {
					//System.out.println("Token: " + token.getLemma() + " Concept: " + concept.getName() + " LinkIn: " + concept.getLinksIn().size() + " Inv: " +  -Math.log10(inv));
					token.features.put(features.inverseWikipediaLinkade.getFeature(), - (Math.log(inv)/Math.log(2)));
				} 
				else
					token.features.put(features.inverseWikipediaLinkade.getFeature(), 0.0D);
			}
			else
				token.features.put(features.inverseWikipediaLinkade.getFeature(), 0.0D);
		} else
			token.features.put(features.inverseWikipediaLinkade.getFeature(), 0.0D);
	}
	
	//TODO: Retirar este metodo (provisoriamente para calcular a wikipediaKeyphraseness dos termos não do texto obtidos do ConceptNet)
	public void calculateWikipediaKeyphrasenessLast(Token t) {
	
		//if (mentionsAll.containsKey(t.getLemma())) {
		//	Mention mention = mentionsAll.get(t.getLemma());
		if (t.mentionWikipedia != null && !t.mentionWikipedia.equals("")) {
			Mention mention = knowledgeBase.getMentions().getByName(t.mentionWikipedia);
			if (mention != null) {
				double keyp = (double) mention.getTopicDocumentCount() / mention.getTextDocumentCount();
				if (keyp <= 1)
					t.features.put(features.wikipediaKeyphraseness.getFeature(), keyp);
				else
					t.features.put(features.wikipediaKeyphraseness.getFeature(), 1.0D);
			}
			else 
				t.features.put(features.wikipediaKeyphraseness.getFeature(), 0.0D);
		} else
			t.features.put(features.wikipediaKeyphraseness.getFeature(), 0.0D);
	}
		
	public static void main(String [] args) {
		
		MongoDB.changeMongoParameters(Config.SERVIDOR_DB, Config.PORT_DB, Config.DB_NAME);
		KnowledgeBase knowledgeBase = new KnowledgeBase(new File(Config.PATH_MODEL_CONFIG), Config.SERVIDOR_DB, Config.PORT_DB, Config.DB_NAME);
		Calendar begin = Calendar.getInstance();
		
		Concept concept = knowledgeBase.getConcepts().getConceptByName("Animalia (book)");
		System.out.println("Tamanho: " + concept.getMentions().size());
		
		Calendar end = Calendar.getInstance();
		System.out.println("Execution time: " + (end.getTimeInMillis() - begin.getTimeInMillis()));
		
		Mention mention = knowledgeBase.getMentions().getByName("Lett");
		System.out.println(mention.getId());
		System.out.println(mention.getConcepts());
		System.out.println("Keyphraseness: " + mention.getKeyphraseness());
		Set<Concept> conceptsMention = knowledgeBase.getConcepts().getConceptsByMention(mention);
		for(Concept c : conceptsMention) {
			System.out.println(c.getName());
		}
		
		/*System.out.println("Capturando os conceitos...");
		List<Concept> allConcepts = knowledgeBase.getConcepts().findAll();
		for(Concept c : allConcepts) 
			System.out.println("Size mention: " + c.getMentions().size());*/
		
		/*StanfordLemmatizer lemmatizer = new StanfordLemmatizer();
		FileData file = new FileData();
		file.setName("teste");
		file.setText("the study of networks pervades all of science, from neurobiology to statistical physics the study of networks pervades all of science, from neurobiology to statistical physics");
		file.setTextLemmatized(lemmatizer.lemmatize(file.getText()).toString().replace("[", "").replace("]", "").replace(",", ""));
		Hashtable<String, Integer> tags = new Hashtable<String, Integer>();
		tags.put("network", 2);
		tags.put("science", 2);
		file.setTokens(new ReadData().generateNGram(file.getTextLemmatized(), tags, file));
		for (Token t : file.getTokens())
			t.features.put(features.ofText.getFeature(), 1.0D);
		Token aux = new Token();
		aux.setLemma("Jaguar");
		aux.features.put(features.ofText.getFeature(), 0.0D);
		file.getTokens().add(aux);
		
		SemanticSimilarity semantic = new SemanticSimilarity(); 
		semantic.getRelationsWikipedia(file);
		semantic.calculateSimilaritySemantic(file);
		semantic.calculateNodeDegree(file);
		
		System.out.println("Tokens: ");
		for (Token t : file.getTokens()) {
			System.out.println(t.getLemma() + " [" + t.getBeginIndex() + " - " + t.getEndIndex() + "]: " + t.features.get(features.semanticSimilarity.getFeature()) + " <==> " + t.features.get(features.nodeDegree.getFeature()));
		}*/
		
	}
}
