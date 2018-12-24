package preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStream;
import java.text.Format;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import features.Features;
import features.Features.features;
import util.FileData;
import util.Relation;
import util.Token;
import weka.core.pmml.jaxbbindings.Application;

public class FormatFile {

	/**
	 * Write the files in format XML
	 * @author raquelsilveira
	 * @date 24/02/2015
	 * @param listFiles
	 */
	public void writeFormatFile(ArrayList<FileData> listFiles, String path) {
		
		try {
			for(FileData file : listFiles) {
				writeFormatFileByFile(file, path);
			}
		}
		catch(Exception e) { e.printStackTrace(); }
	}
	
	/**
	 * VWrite format file by fileData
	 * @author raquelsilveira
	 * @date 25/02/2015
	 * @param file
	 * @param path
	 */
	public void writeFormatFileByFile(FileData file, String path) {
		try {
			System.out.println(file.getName());
			
			Document doc = new Document();
			Element root = new Element("doc");
			root.setAttribute(new Attribute("name", file.getName()));
			root.addContent(new Element("text").setText(""));
			root.addContent(new Element("textLemma").setText(""));
			root.addContent(new Element("title").setText(""));
			root.addContent(new Element("titleLemma").setText(""));
			
			//root.addContent(new Element("qttyTerms").setText(Integer.toString(file.getQttyTerms())));
			
			Element originalTags = new Element("originalTags");
			for (String tag : file.tagsOriginalFile.keySet()) {
				if (!tag.isEmpty()) {
					Element tagNo = new Element("originalTag");
					tagNo.setAttribute("name", tag);
					tagNo.setAttribute("qtty", Integer.toString(file.tagsOriginalFile.get(tag)));
					originalTags.addContent(tagNo);
				}
			}
			root.addContent(originalTags);
			
			Element tokens = new Element("tokens");
			for (Token token : file.getTokens()) {
				Element tokenNo = new Element("token");
				//tokenNo.addContent(new Element("name").setText(token.getName()));
				//tokenNo.addContent(new Element("conceptName").setText(token.conceptWikipedia));
				//tokenNo.addContent(new Element("mentionName").setText(token.mentionWikipedia));
				tokenNo.addContent(new Element("lemma").setText(token.getLemma()));
				//tokenNo.addContent(new Element("beginIndex").setText(Integer.toString(token.getBeginIndex())));
				//tokenNo.addContent(new Element("endIndex").setText(Integer.toString(token.getEndIndex())));
				tokenNo.addContent(new Element("classification").setText(token.getClassification()));
				tokenNo.addContent(new Element("frequencyDoc").setText(Integer.toString(token.getFrequencyDoc())));
				//tokenNo.addContent(new Element("originalInf").setText(token.originalInferenceNet ? "true" : "false"));
				
				Element features = new Element("features");
				for (String feature : token.features.keySet()) {
					Element featureNo = new Element(feature);
					featureNo.setText(Double.toString(token.features.get(feature)));
					features.addContent(featureNo);
				}
				
				/*Element tokensInf = new Element("tokensInfered");
				for (Token t : token.tokenInfered.keySet()) {
					Element tokenInfNo = new Element("tokenInfered");
					tokenInfNo.setAttribute("name", t.getLemma());
					Element roles = new Element("rolesInfered");
					for (String r : token.tokenInfered.get(t)) {
						Element rAux = new Element("roleInfered");
						rAux.setText(r);
						roles.addContent(rAux);
					}
					tokenInfNo.addContent(roles);
					tokensInf.addContent(tokenInfNo);
				}*/
				
				Element tokensThatInf = new Element("tokensThatInfered");
				for (String t : token.tokenWhoInfered.keySet()) {
					Element tokenInfNo = new Element("tokenThatInfered");
					tokenInfNo.setAttribute("name", t);
					//tokenInfNo.setAttribute("similaritySemantic", String.valueOf(token.tokenWhoInfered.get(t).similaritySematic));
					Element roles = new Element("rolesThatInfered");
					for (String r : token.tokenWhoInfered.get(t).relation) {
						Element rAux = new Element("roleThatInfered");
						rAux.setText(r);
						roles.addContent(rAux);
					}
					tokenInfNo.addContent(roles);
					tokensThatInf.addContent(tokenInfNo);
				}
				
				tokenNo.addContent(features);
				//tokenNo.addContent(tokensInf);
				tokenNo.addContent(tokensThatInf);
				tokens.addContent(tokenNo);
			}
			root.addContent(tokens);
			doc.setRootElement(root);
			
			XMLOutputter xout = new XMLOutputter();
			OutputStream out = new FileOutputStream(new File(path + file.getName() + ".xml"));
			xout.output(doc , out);
			out.close();
		}
		catch(Exception e) { e.printStackTrace(); }
	}
	
	/**
	 * VWrite format file by fileData
	 * @author raquelsilveira
	 * @date 25/02/2015
	 * @param file
	 * @param path
	 */
	public void writeFormatFileByFileResume(FileData file, String path) {
		try {
			System.out.println(file.getName());
			
			Document doc = new Document();
			Element root = new Element("doc");
			root.setAttribute(new Attribute("name", file.getName()));
			//root.addContent(new Element("text").setText(""));
			//root.addContent(new Element("textLemma").setText(""));
			//root.addContent(new Element("title").setText(""));
			//root.addContent(new Element("titleLemma").setText(""));
			
			root.addContent(new Element("qttyTerms").setText(Integer.toString(file.getQttyTerms())));
			
			Element originalTags = new Element("originalTags");
			for (String tag : file.tagsOriginalFile.keySet()) {
				if (!tag.isEmpty()) {
					Element tagNo = new Element("originalTag");
					tagNo.setAttribute("name", tag);
					tagNo.setAttribute("qtty", Integer.toString(file.tagsOriginalFile.get(tag)));
					originalTags.addContent(tagNo);
				}
			}
			root.addContent(originalTags);
			
			Element tokens = new Element("tokens");
			for (Token token : file.getTokens()) {
				Element tokenNo = new Element("token");
				//tokenNo.addContent(new Element("name").setText(token.getName()));
				tokenNo.addContent(new Element("conceptName").setText(token.conceptWikipedia));
				//tokenNo.addContent(new Element("mentionName").setText(token.mentionWikipedia));
				tokenNo.addContent(new Element("lemma").setText(token.getLemma()));
				tokenNo.addContent(new Element("beginIndex").setText(Integer.toString(token.getBeginIndex())));
				tokenNo.addContent(new Element("endIndex").setText(Integer.toString(token.getEndIndex())));
				tokenNo.addContent(new Element("classification").setText(token.getClassification()));
				tokenNo.addContent(new Element("frequencyDoc").setText(Integer.toString(token.getFrequencyDoc())));
				//tokenNo.addContent(new Element("originalInf").setText(token.originalInferenceNet ? "true" : "false"));
				
				Element featuresE = new Element("features");
				for (String feature : token.features.keySet()) {
					if (feature.equals(features.titleOccurrence.getFeature()) ||
						feature.equals(features.termPredictiability.getFeature()))
						continue;
					
					Element featureNo = new Element(feature);
					featureNo.setText(Double.toString(token.features.get(feature)));
					featuresE.addContent(featureNo);
				}
				
				/*Element tokensInf = new Element("tokensInfered");
				for (Token t : token.tokenInfered.keySet()) {
					Element tokenInfNo = new Element("tokenInfered");
					tokenInfNo.setAttribute("name", t.getLemma());
					Element roles = new Element("rolesInfered");
					for (String r : token.tokenInfered.get(t)) {
						Element rAux = new Element("roleInfered");
						rAux.setText(r);
						roles.addContent(rAux);
					}
					tokenInfNo.addContent(roles);
					tokensInf.addContent(tokenInfNo);
				}*/
				
				Element tokensThatInf = new Element("tokensThatInfered");
				for (Token t : token.tokenWhoInfered.keySet()) {
					Element tokenInfNo = new Element("tokenThatInfered");
					tokenInfNo.setAttribute("name", t.getLemma());
					//tokenInfNo.setAttribute("similaritySemantic", String.valueOf(token.tokenWhoInfered.get(t).similaritySematic));
					Element roles = new Element("rolesThatInfered");
					for (String r : token.tokenWhoInfered.get(t).relation) {
						Element rAux = new Element("roleThatInfered");
						rAux.setText(r);
						roles.addContent(rAux);
					}
					tokenInfNo.addContent(roles);
					tokensThatInf.addContent(tokenInfNo);
				}
				
				tokenNo.addContent(featuresE);
				//tokenNo.addContent(tokensInf);
				tokenNo.addContent(tokensThatInf);
				tokens.addContent(tokenNo);
			}
			root.addContent(tokens);
			doc.setRootElement(root);
			
			XMLOutputter xout = new XMLOutputter();
			OutputStream out = new FileOutputStream(new File(path + file.getName() + ".xml"));
			xout.output(doc , out);
			out.close();
		}
		catch(Exception e) { e.printStackTrace(); }
	}
	
	/**
	 * VWrite format file by fileData
	 * @author raquelsilveira
	 * @date 25/02/2015
	 * @param file
	 * @param path
	 */
	public void writeFormatFileByFileResume2(FileData file, String path) {
		try {
			System.out.println(file.getName());
			
			Document doc = new Document();
			Element root = new Element("doc");
			root.setAttribute(new Attribute("name", file.getName()));
			//root.addContent(new Element("text").setText(""));
			//root.addContent(new Element("textLemma").setText(""));
			//root.addContent(new Element("title").setText(""));
			//root.addContent(new Element("titleLemma").setText(""));
			
			//root.addContent(new Element("qttyTerms").setText(Integer.toString(file.getQttyTerms())));
			
			/*Element originalTags = new Element("originalTags");
			for (String tag : file.tagsOriginalFile.keySet()) {
				if (!tag.isEmpty()) {
					Element tagNo = new Element("originalTag");
					tagNo.setAttribute("name", tag);
					tagNo.setAttribute("qtty", Integer.toString(file.tagsOriginalFile.get(tag)));
					originalTags.addContent(tagNo);
				}
			}
			root.addContent(originalTags);*/
			
			Element tokens = new Element("tokens");
			for (Token token : file.getTokens()) {
				Element tokenNo = new Element("token");
				//tokenNo.addContent(new Element("name").setText(token.getName()));
				//tokenNo.addContent(new Element("conceptName").setText(token.conceptWikipedia));
				//tokenNo.addContent(new Element("mentionName").setText(token.mentionWikipedia));
				tokenNo.addContent(new Element("lemma").setText(token.getLemma()));
				//tokenNo.addContent(new Element("beginIndex").setText(Integer.toString(token.getBeginIndex())));
				//tokenNo.addContent(new Element("endIndex").setText(Integer.toString(token.getEndIndex())));
				tokenNo.addContent(new Element("classification").setText(token.getClassification()));
				//tokenNo.addContent(new Element("frequencyDoc").setText(Integer.toString(token.getFrequencyDoc())));
				//tokenNo.addContent(new Element("originalInf").setText(token.originalInferenceNet ? "true" : "false"));
				
				Element featuresE = new Element("features");
				for (String feature : token.features.keySet()) {
					if (feature.equals(features.titleOccurrence.getFeature()) ||
						feature.equals(features.termPredictiability.getFeature()))
						continue;
					
					Element featureNo = new Element(feature);
					featureNo.setText(Double.toString(token.features.get(feature)));
					featuresE.addContent(featureNo);
				}
				
				/*Element tokensInf = new Element("tokensInfered");
				for (Token t : token.tokenInfered.keySet()) {
					Element tokenInfNo = new Element("tokenInfered");
					tokenInfNo.setAttribute("name", t.getLemma());
					Element roles = new Element("rolesInfered");
					for (String r : token.tokenInfered.get(t)) {
						Element rAux = new Element("roleInfered");
						rAux.setText(r);
						roles.addContent(rAux);
					}
					tokenInfNo.addContent(roles);
					tokensInf.addContent(tokenInfNo);
				}*/
				
				Element tokensThatInf = new Element("tokensThatInfered");
				for (Token t : token.tokenWhoInfered.keySet()) {
					Element tokenInfNo = new Element("tokenThatInfered");
					tokenInfNo.setAttribute("name", t.getLemma());
					//tokenInfNo.setAttribute("similaritySemantic", String.valueOf(token.tokenWhoInfered.get(t).similaritySematic));
					Element roles = new Element("rolesThatInfered");
					for (String r : token.tokenWhoInfered.get(t).relation) {
						Element rAux = new Element("roleThatInfered");
						rAux.setText(r);
						roles.addContent(rAux);
					}
					tokenInfNo.addContent(roles);
					tokensThatInf.addContent(tokenInfNo);
				}
				
				tokenNo.addContent(featuresE);
				//tokenNo.addContent(tokensInf);
				tokenNo.addContent(tokensThatInf);
				tokens.addContent(tokenNo);
			}
			root.addContent(tokens);
			doc.setRootElement(root);
			
			XMLOutputter xout = new XMLOutputter();
			OutputStream out = new FileOutputStream(new File(path + file.getName() + ".xml"));
			xout.output(doc , out);
			out.close();
		}
		catch(Exception e) { e.printStackTrace(); }
	}
	
	/**
	 * VWrite format file by fileData
	 * @author raquelsilveira
	 * @date 25/02/2015
	 * @param file
	 * @param path
	 */
	public void writeFormatFileByFileResume3(FileData file, String path) {
		try {
			System.out.println(file.getName());
			
			FileWriter writer = new FileWriter(path + file.getName() + ".txt");
			writer.write(file.getName() + "\n");
			
			for (Token token : file.getTokens()) {
				writer.write(token.getLemma() + "\n");
				writer.write(token.features.get(Features.features.firstOccurrence.getFeature()) + ", ");
				writer.write(token.features.get(Features.features.inverseWikipediaLinkade.getFeature()) + ", ");
				writer.write(token.features.get(Features.features.keyphraseness.getFeature()) + ", ");
				writer.write(token.features.get(Features.features.nodeDegree.getFeature()) + ", ");
				writer.write(token.features.get(Features.features.ofText.getFeature()) + ", ");
				writer.write(token.features.get(Features.features.phraseLenght.getFeature()) + ", ");
				writer.write(token.features.get(Features.features.roleIsARelatedTo.getFeature()) + ", ");
				writer.write(token.features.get(Features.features.semanticSimilarity.getFeature()) + ", ");
				writer.write(token.features.get(Features.features.spread.getFeature()) + ", ");
				writer.write(token.features.get(Features.features.tf_idf.getFeature()) + ", ");
				writer.write(token.features.get(Features.features.wikipediaKeyphraseness.getFeature()) + ", ");
				writer.write(token.getClassification() + ";\n");
				
				for (Token t : token.tokenWhoInfered.keySet()) {
					writer.write("{" + t.getLemma() + ": ");
					
					String relations = "";
					for (String r : token.tokenWhoInfered.get(t).relation)
						relations += r + ", ";
					
					relations = relations.substring(0, relations.length()-2);
					writer.write(relations + "}\n");
				}
			}
			
			writer.close();
		}
		catch(Exception e) { e.printStackTrace(); }
	}
	
	public static void main (String [] args) {
		
		String path = "data/citeULike/infers_conceptNet_AMIE3_5_Filter_Inferece_features_resume3";
		
		long inicio = System.currentTimeMillis();
		
		File directory = new File(path);
		ArrayList<FileData> listFiles = new ArrayList<FileData>();
		int f = 0;
		for (File file : directory.listFiles()) {
			System.out.println(++f);
			listFiles.add(new FormatFile().readFormatFileByFileTxt(file.getAbsolutePath()));
		}
		
		System.out.println((System.currentTimeMillis() - inicio));
	}
	
	public FileData readFormatFileByFileTxt(String path) {
		FileData fileData = null;
		
		try {
			FileReader reader = new FileReader(path);
			BufferedReader buffer = new BufferedReader(reader);
			
			fileData = new FileData();
			
			String line = buffer.readLine(); //First Line = file name
			fileData.setName(line);
			
			//Hashtable<String, Token> tokenText = new Hashtable<String, Token>();
			
			line = buffer.readLine();
			while(line != null) {
				Token token = new Token();
				token.setLemma(line);
				
				line = buffer.readLine();
				StringTokenizer t = new StringTokenizer(line, ",");
				
				token.features.put(Features.features.firstOccurrence.getFeature(), Double.parseDouble(t.nextToken()));
				token.features.put(Features.features.inverseWikipediaLinkade.getFeature(), Double.parseDouble(t.nextToken()));
				token.features.put(Features.features.keyphraseness.getFeature(), Double.parseDouble(t.nextToken()));
				token.features.put(Features.features.nodeDegree.getFeature(), Double.parseDouble(t.nextToken()));
				token.features.put(Features.features.ofText.getFeature(), Double.parseDouble(t.nextToken()));
				token.features.put(Features.features.phraseLenght.getFeature(), Double.parseDouble(t.nextToken()));
				String isr = t.nextToken();
				Double isrValue = Double.NaN;
				if (!isr.trim().equals("null")) isrValue = Double.parseDouble(isr);
				token.features.put(Features.features.roleIsARelatedTo.getFeature(), isrValue);
				token.features.put(Features.features.semanticSimilarity.getFeature(), Double.parseDouble(t.nextToken()));
				token.features.put(Features.features.spread.getFeature(), Double.parseDouble(t.nextToken()));
				token.features.put(Features.features.tf_idf.getFeature(), Double.parseDouble(t.nextToken()));
				token.features.put(Features.features.wikipediaKeyphraseness.getFeature(), Double.parseDouble(t.nextToken()));
				token.setClassification(t.nextToken().replace(";", "").trim());
				//System.out.println(token.getClassification());			
				
				token.tokenWhoInfered = new Hashtable<String, Relation>();
				line = buffer.readLine();
				while (line != null && line.startsWith("{")) {
					
					String lemaTokenWhoInfered = line.substring(1, line.indexOf(":"));
					//Token tokenWhoInfered = tokenText.get(lemaTokenWhoInfered);
					
					/*for(Token tokenFile : fileData.getTokens()) {
						if (tokenFile.getLemma().equals(lemaTokenWhoInfered)) {
							tokenWhoInfered = tokenFile;
							break;
						}
					}*/
					
					Relation rel = new Relation();
					rel.relation = new ArrayList<String>();
					StringTokenizer relToken = new StringTokenizer(line.substring(line.indexOf(":")+1, line.length()-1), ", ");
					while(relToken.hasMoreTokens())
						rel.relation.add(relToken.nextToken());
					
					token.tokenWhoInfered.put(lemaTokenWhoInfered, rel);
					
					line = buffer.readLine();
				}
				
				//if (token.features.get(features.ofText.getFeature()) == 1)
				//	tokenText.put(token.getLemma(), token);
				fileData.getTokens().add(token);
			}
			
			buffer.close();
			reader.close();
			/*
			System.out.println("*****" + fileData.getName() + "*****");
			for (Token token : fileData.getTokens()) {
				System.out.println(token.getLemma() + " -> " + token.getClassification());
				System.out.println("Features:");
				for(String f : token.features.keySet())
					System.out.println(f + ": " + token.features.get(f));
				if (token.tokenWhoInfered.size() > 0) {
					for (Token tWhoInfered : token.tokenWhoInfered.keySet()) {
						System.out.print(tWhoInfered.getLemma() + ": ");
						for (String r : token.tokenWhoInfered.get(tWhoInfered).relation)
							System.out.print(r + ", ");
						System.out.println("");
					}
				}
			}*/
			
			
		} catch(Exception e) { e.printStackTrace(); }
		
		return fileData;
	}
	
	public FileData readFormatFileByFile(String path) {
		
		FileData fileData = new FileData();
		
		try {
			Hashtable<String, Token> tokensAllFile = new Hashtable<String, Token>();
			
			Hashtable<String, Hashtable<String, ArrayList<String>>> listTokensInfered = new Hashtable<String, Hashtable<String, ArrayList<String>>>();
			Hashtable<String, Hashtable<String, Relation>> listTokensThatInfered = new Hashtable<String, Hashtable<String, Relation>>();
			
			File file = new File(path);
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(file);
			
			Element root = (Element) doc.getRootElement();
			fileData.setName(root.getAttributeValue("name"));
			/*try { fileData.setTitle(root.getChildText("title")); } catch (Exception e) {}
			try { fileData.setTitleLemmatized(root.getChildText("titleLemma")); } catch (Exception e) {}
			try { fileData.setText(root.getChildText("text")); } catch(Exception e) {}
			try { fileData.setTextLemmatized(root.getChildText("textLemma")); } catch(Exception e) {}*/
			try { fileData.setQttyTerms(Integer.parseInt(root.getChildText("qttyTerms"))); } catch(Exception e) {}
			
			try {
				fileData.tagsOriginalFile = new Hashtable<String, Integer>();
				Element originalTagNode = root.getChild("originalTags");
				Iterator k = originalTagNode.getChildren().iterator();
				while(k.hasNext()) {
					Element originalTag = (Element) k.next();
					int qtty = 1;
					try { qtty = Integer.parseInt(originalTag.getAttributeValue("qtty")); }
					catch(Exception e) {}
					fileData.tagsOriginalFile.put(originalTag.getAttributeValue("name"), qtty);
				}
			} catch(Exception e) {}
			
			Element tokens = root.getChild("tokens");
			Iterator i = tokens.getChildren().iterator();
			ArrayList<Token> tokensFile = new ArrayList<Token>();
			while (i.hasNext()) {
				Token token = new Token();
				token.fileName = fileData.getName();
				Element tokenNode = (Element) i.next();
				//try { token.setName(tokenNode.getChildText("name")); } catch(Exception e) {}
				try { token.conceptWikipedia = tokenNode.getChildText("conceptName"); } catch(Exception e) {}
				//try { token.mentionWikipedia = tokenNode.getChildText("mentionName"); } catch(Exception e) {}
				try { token.setLemma(tokenNode.getChildText("lemma").toLowerCase()); }
				catch (Exception e) { }//token.setLemma(token.getName()); }
				try { if (token.getLemma() == null) token.setLemma(tokenNode.getChildText("name").toLowerCase()); }
				catch (Exception e) { }//token.setLemma(token.getName()); }
				try { token.setBeginIndex(Integer.parseInt(tokenNode.getChildText("beginIndex"))); } catch(Exception e) {}
				try { token.setEndIndex(Integer.parseInt(tokenNode.getChildText("endIndex"))); } catch(Exception e) {}
				token.setClassification(tokenNode.getChildText("classification"));
				try {
					//token.originalInferenceNet = tokenNode.getChildText("originalInf").toString().equals("true") ? true : false;
					token.setFrequencyDoc(Integer.parseInt(tokenNode.getChildText("frequencyDoc"))); 
				}
				catch(Exception e) {}
				
				Element featuresNode = tokenNode.getChild("features");
				Iterator j = featuresNode.getChildren().iterator();
				token.features = new Hashtable<String, Double>();
				while(j.hasNext()) {
					Element featureNode = (Element) j.next();
					String featureNo = featureNode.getName();
					if (featureNode.getName().equals("PHRASELENGHT")) featureNo = features.phraseLenght.getFeature();
					if (featureNode.getName().equals("OFTEXT")) featureNo = features.ofText.getFeature();
					if (featureNode.getName().equals("TF-IDF")) featureNo = features.tf_idf.getFeature();
					if (featureNode.getName().equals("KEYPHRASENESS")) featureNo = features.keyphraseness.getFeature();
					if (featureNode.getName().equals("WIKIPEDIAKEYPHRASENESS")) featureNo = features.wikipediaKeyphraseness.getFeature();
					if (featureNode.getName().equals("ISRVALUE")) featureNo = features.roleIsARelatedTo.getFeature();
					if (featureNode.getName().equals("SPREAD")) featureNo = features.spread.getFeature();
					if (featureNode.getName().equals("FIRSTOCCURRENCE")) featureNo = features.firstOccurrence.getFeature();
					if (featureNode.getName().equals("INVERSEWIKIPEDIALINKADE")) featureNo = features.inverseWikipediaLinkade.getFeature();
					if (featureNode.getName().equals("NODEDEGREE")) featureNo = features.nodeDegree.getFeature();
					if (featureNode.getName().equals("SEMANTICSIMILARITY")) featureNo = features.semanticSimilarity.getFeature();
					
					token.features.put(featureNo, Double.parseDouble(featureNode.getText()));
				}
				
				Element tokensInfered = tokenNode.getChild("tokensInfered");
				if (tokensInfered != null) {
					Iterator ti = tokensInfered.getChildren().iterator();
					Hashtable<String, ArrayList<String>> aux = new Hashtable<String, ArrayList<String>>();
					while(ti.hasNext()) {
						Element tokenInfered = (Element) ti.next();
						ArrayList<String> relations = new ArrayList<String>();
						
						if (tokenInfered.getChild("rolesInfered") != null) {
							Iterator tir = tokenInfered.getChild("rolesInfered").getChildren().iterator();
							while(tir.hasNext()) {
								Element tokenInferedAux = (Element) tir.next();
								relations.add(tokenInferedAux.getText());
							}
						}
						else
							relations.add(tokenInfered.getAttributeValue("role"));
						
						aux.put(tokenInfered.getAttributeValue("name").toLowerCase(), relations);
					}
					listTokensInfered.put(token.getLemma().toLowerCase(), aux);
				}
				
				//WordNet
				/*Element tokensThatInfered = tokenNode.getChild("tokensThatInfered");
				if (tokensThatInfered != null) {
					Iterator ti2 = tokensThatInfered.getChildren().iterator();
					HashSet<String> aux2 = new HashSet<String>();
					while(ti2.hasNext()) {
						Element tokenThatInfered = (Element) ti2.next();
						String tokenWordNet = tokenThatInfered.getAttributeValue("name").toLowerCase();
						
						if (tokenThatInfered.getChild("rolesThatInfered") != null) {
							Iterator tir = tokenThatInfered.getChild("rolesThatInfered").getChildren().iterator();
							ArrayList<String> relations = new ArrayList<String>();
							while(tir.hasNext()) {
								Element tokenThatInferedAux = (Element) tir.next();
								String relation = tokenThatInferedAux.getText();
								
								if (token.tokenWhoInfered.containsKey(relation))
									relations = token.tokenWhoInfered.get(relation).relation;
								relations.add(relation);
							}
							Relation r = new Relation();
							r.relation = relations;
							//System.out.println("Aqui: " + tokenWordNet);
							token.tokenWhoInfered.put(tokenWordNet, r); 
						}
					}
				}*/
				//FinalWordNet
				
				/*Element tokensThatInfered = tokenNode.getChild("tokensThatInfered");
				if (tokensThatInfered != null) {
					Iterator ti2 = tokensThatInfered.getChildren().iterator();
					Hashtable<String, Relation> aux2 = new Hashtable<String, Relation>();
					while(ti2.hasNext()) {
						Element tokenThatInfered = (Element) ti2.next();
						Relation rel = new Relation();
						if (tokenThatInfered.getChild("rolesThatInfered") != null) {
							Iterator tir = tokenThatInfered.getChild("rolesThatInfered").getChildren().iterator();
							while(tir.hasNext()) {
								Element tokenThatInferedAux = (Element) tir.next();
								rel.relation.add(tokenThatInferedAux.getText());
							}
						}
						else
							rel.relation.add(tokenThatInfered.getAttributeValue("role"));
						
						//try { rel.similaritySematic = Double.parseDouble(tokenThatInfered.getAttributeValue("similaritySemantic")); }
						//catch (Exception e) { }
						
						aux2.put(tokenThatInfered.getAttributeValue("name").toLowerCase(), rel);
					}
					listTokensThatInfered.put(token.getLemma().toLowerCase(), aux2);
				}*/
	
				tokensFile.add(token);
				//Sem WordNet
				tokensAllFile.put(token.getLemma().toLowerCase(), token);
			}
			
			//Sem WordNet
			for (Token t : tokensFile) {
				//System.out.println(t.getLemma());
				/*if (listTokensInfered.size() > 0) {
					for (String key : listTokensInfered.get(t.getLemma()).keySet()) {
						if (tokensAllFile.containsKey(key))
							t.tokenInfered.put(tokensAllFile.get(key), listTokensInfered.get(t.getLemma()).get(key));
					}
				}*/
				
				//Sem WorNet
				if (listTokensThatInfered.size() > 0) {
					for (String key : listTokensThatInfered.get(t.getLemma()).keySet()) {
						if (tokensAllFile.containsKey(key)) {
							t.tokenWhoInfered.put(tokensAllFile.get(key).getLemma(), listTokensThatInfered.get(t.getLemma()).get(key));
							System.out.println(key);
						}
					}
				}
			}
			
			fileData.setTokens(tokensFile);
			
		}
		catch(Exception e) { e.printStackTrace(); }
		return fileData;
	}
	
	/**
	 * Reads the formated file
	 * @return file list
	 */
	public ArrayList<FileData> readFormatFiles(String path) {
		ArrayList<FileData> listFiles = new ArrayList<FileData>();
		
		try {
			File directory = new File(path);
			int n = 0;
			for(File file : directory.listFiles()) {
				
				if (!file.getName().contains(".xml") && !file.getName().contains(".txt") ) continue;
				
				System.out.println(++n + ": " + file.getName());
				
				FileData fileData = null;
				if (file.getName().endsWith(".xml"))
					fileData = readFormatFileByFile(file.getAbsolutePath());
				else if (file.getName().endsWith(".txt"))
					fileData = readFormatFileByFileTxt(file.getAbsolutePath());
					
				listFiles.add(fileData);
			}
			
			directory = null;
		}
		catch(Exception e) { e.printStackTrace(); }
		return listFiles;
	}
}
