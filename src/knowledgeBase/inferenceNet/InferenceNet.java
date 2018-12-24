package knowledgeBase.inferenceNet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import preprocessing.ReadData;
import util.StanfordLemmatizer;
import util.Token;
import config.Config;
import config.Config.selection;

public class InferenceNet {
	
	private static Connection connection = null;
	StanfordLemmatizer lemmatizer = null;
	
	private static InferenceNet instance = null;
	private InferenceNet() {
		lemmatizer = new StanfordLemmatizer();	
	}
	public static InferenceNet getInstance() {
		if (instance == null)
			instance = new InferenceNet();
		return instance;
	}
	
	/**
	 * Get the connection with the InferenceNet database
	 * @author raquelsilveira
	 * @date 06/01/2015
	 * @return connection
	 */
	public Connection getConnectionInferenceNet() {
		try 
		{
			if (connection == null) {			
				connection = DriverManager.getConnection(Config.URL_MYSQL, Config.USER_MYSQL, Config.PASSWORD_MYSQL);
			}
		}
		catch (SQLException e) { e.printStackTrace(); }
		return connection;
	}
	
	/**
	 * Get the terms with relation 'is a' in InferenceNet
	 * @param token
	 * @return tokens with relation 'is a'
	 */
	public ArrayList<Token> getTermsIsAInferenceNet(Token token) {
		ArrayList<Token> tokenList = null;
		try {
			String query = getQuerySelection();
						   			   
			Connection connection = getConnectionInferenceNet();
			PreparedStatement pstmt = connection.prepareStatement(query);
			pstmt.setString(1, token.getLemma().trim());
			pstmt.setString(2, token.getLemma().trim());
			if (Config.MODE_SELECTION_INF_NET == selection.transitivy) {
				pstmt.setString(3, token.getLemma().trim());
				pstmt.setString(4, token.getLemma().trim());
			}
			
			ResultSet result = null;
			result = pstmt.executeQuery();
				
			tokenList = new ArrayList<Token>();
			while(result.next()){
				int idPre = Integer.parseInt(result.getString("id_pre")); 
				int idPos = Integer.parseInt(result.getString("id_pos"));
				String namePtPre = result.getString("name_en_pre");
				String namePtPos = result.getString("name_en_pos");
				String role = result.getString("role");
				String uri_yago_pre = result.getString("uri_yago_pre");
				//Checks if the term was added in InferenceNet 
				boolean added = (uri_yago_pre == null && (idPre > 254492 || idPos > 254492));
					
				addToken(namePtPre, tokenList, added, role);
				addToken(namePtPos, tokenList, added, role);
			}
			result.close();
			pstmt.close();
		} 
		catch (SQLException e) { e.printStackTrace(); }
		return tokenList;
	}
	
	/**
	 * Gets the relation in the InferenceNet between two tokens
	 * @author raquelsilveira
	 * @date 07/04/2015
	 * @param t1
	 * @param t2
	 * @return list of roles
	 */
	public ArrayList<String> getRelation(Token t1, Token t2) {
		
		ArrayList<String> roles = null;
		try {
			String query = "select role from opt_relation where (name_en_pre = ? and name_en_pos = ?) or "
					+ "(name_en_pre = ? and name_en_pos = ?)";
			
			Connection connection = getConnectionInferenceNet();
			PreparedStatement pstmt = connection.prepareStatement(query);
			pstmt.setString(1, t1.getLemma().trim());
			pstmt.setString(2, t2.getLemma().trim());
			pstmt.setString(3, t2.getLemma().trim());
			pstmt.setString(4, t1.getLemma().trim());
			
			ResultSet result = null;
			result = pstmt.executeQuery();
				
			roles = new ArrayList<String>();
			while(result.next()){
				roles.add(result.getString("role"));
			}
			result.close();
			pstmt.close();
		}
		catch(SQLException e) { e.printStackTrace(); }
		return roles;
	}
	
	private String getQuerySelection() {
		String query = "";
		switch (Config.MODE_SELECTION_INF_NET) {
			case reflexivity:
			{
				query = "select name_en_pre, name_en_pos, role, uri_yago_pre, id_pre, id_pos from opt_relation " +
						"where (name_en_pre = ? or name_en_pos = ?)";
						   	   //"and role = 'IsA'";
				break;
			}
			case transitivy:
			{
				query = "select name_pt_pre, name_pt_pos, role from opt_relation " +
					   "where " +
					   "role = 'IsA' and name_en_pos != name_en_pre and " +
					   "name_en_pre in (select name_en_pos from opt_relation " +
					   				   "where role = 'IsA' and name_en_pos != name_en_pre and " +
					   				   "name_en_pre = ?) and " +
					   "name_en_pos in (select name_en_pos from opt_relation " +
					   				   "where role = 'IsA' and name_en_pos != name_en_pre and " +
					   				   "name_en_pre = ?) " +
					   "union " +
					   "select name_en_pre, name_en_pos, role from opt_relation " +
					   "where " +
					   "role = 'IsA' and name_en_pos != name_en_pre and " +
					   "name_en_pre in (select name_en_pre from opt_relation " +
					   				   "where role = 'IsA' and name_en_pos != name_en_pre and " +
					   				   "name_en_pos = ?) and " +
					   "name_en_pos in (select name_en_pre from opt_relation " +
					   				   "where role = 'IsA' and name_en_pos != name_en_pre and " +
					   				   "name_en_pos = ?) ";
				break;
			}
		}
		return query;
	}
	
	/**
	 * Add the found tokens to token list
	 * @author raquelsilveira
	 * @date 07/01/2015
	 * @param name
	 * @param tokenList
	 * @param tokenWithGenerated
	 */
	private void addToken(String name, ArrayList<Token> tokenList, boolean added, String role) {
		if (!repeatedCheck(name, tokenList)) {
			//String lemma = ReadData.pseudoPhrase(lemmatizer.lemmatize(name).toString().replace("[", "").replace("]", "").replace(",", "").replace("-", " "));
			String lemma = lemmatizer.lemmatize(name).toString().replace("[", "").replace("]", "").replace(",", "").replace("-", " ");
			if (lemma != null) {
				Token tokenFound = new Token();
				//tokenFound.originalInferenceNet = !added;
				tokenFound.setName(name);
				tokenFound.setLemma(lemma);
				//tokenFound.role = role;
				tokenList.add(tokenFound);
			}
		}
	}
	
	/**
	 * Checks if the found token already exists in token list
	 * @author raquelsilveira
	 * @date 07/01/2015
	 * @param name found token
	 * @param tokenList
	 * @return
	 */
	private boolean repeatedCheck(String name, ArrayList<Token> tokenList) {
		for(Token t : tokenList) {
			if (t.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}	
	
	public static void main (String [] args) {
		Token token = new Token();
		token.setName("learning");
		token.setLemma("learning");
		ArrayList<Token> tokenFound = new InferenceNet().getTermsIsAInferenceNet(token);
		
		for(Token t : tokenFound) {
			System.out.println(t.getName() + " <-> " + t.getLemma());
		}
	}
}