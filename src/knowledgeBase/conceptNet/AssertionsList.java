package knowledgeBase.conceptNet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;

import util.StanfordLemmatizer;
import br.com.informar.knowledgebase.SourceBase;
import br.com.informar.knowledgebase.collection.Concepts;
import br.com.informar.knowledgebase.collection.Entity;
import br.com.informar.knowledgebase.collection.EntityDAO;
import br.com.informar.knowledgebase.collection.Mentions;
import br.com.informar.knowledgebase.db.MongoDB;
import br.com.informar.knowledgebase.exception.RepositoryException;
import br.com.informar.knowledgebase.model.Concept;
import br.com.informar.knowledgebase.model.Mention;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import config.Config;

public class AssertionsList extends EntityDAO<Assertions> {

	private static Long count; 

	public AssertionsList() {
		super(Assertions.class);
	}

	public List<Assertions> getAssertionByNameStart(String name) {
		DBObject query = new BasicDBObject("start", "/c/" + Config.LANGUAGE + "/" + name.replace(" ", "_"));
		return findKeyValue(query);
	}
	
	public List<Assertions> getAssertionByNameEnd(String name) {
		DBObject query = new BasicDBObject("end", "/c/" + Config.LANGUAGE + "/" + name.replace(" ", "_"));
		return findKeyValue(query);
	}  
	
	public List<Assertions> getAssertionByName(String name) {
		ArrayList orList = new ArrayList();
		orList.add(new BasicDBObject("end", "/c/" + Config.LANGUAGE + "/" + name.replace(" ", "_")));
		orList.add(new BasicDBObject("start", "/c/" + Config.LANGUAGE + "/" + name.replace(" ", "_")));
		DBObject query = new BasicDBObject("$or", orList);
		System.out.println(query);
		
		return findKeyValue(query);
	} 

	public Assertions getConceptFromDataSet(String dataset) {
		DBObject query = new BasicDBObject("dataset", dataset);
		return findOne(query);
	}

	@Override
	public synchronized void save(Assertions assertion) {
		try {
			super.save(assertion);

			if (count != null) {
				count++;
			}
		}
		catch (RepositoryException e) {
			throw e;
		}
	}

	@Override
	public long count() {
		if (count == null) {
			count = super.count();
		}
		return count;
	}

	@Override
	protected Assertions convert(DBObject dbObject, boolean lazy) {
		if (dbObject != null &&
			dbObject.containsField("start") && dbObject.get("start").toString().contains("/c/" + Config.LANGUAGE + "/") &&
			dbObject.containsField("end") && dbObject.get("end").toString().contains("/c/" + Config.LANGUAGE + "/")) {
			
			String id = dbObject.get("id").toString();
			
			String start = dbObject.get("start").toString().replace("/c/" + Config.LANGUAGE + "/" , "");
			if (start.contains("/")) return null;
			//String nameStart = start.substring(0, start.contains("/") ? start.indexOf("/") : start.length()).replace("_", " ").replace("-", " ");
			String nameStart = start.replace("_", " ").replace("-", " ");
			
			String end = dbObject.get("end").toString().replace("/c/" + Config.LANGUAGE + "/" , "");
			if (end.contains("/")) return null;
			//String nameEnd = end.substring(0, end.contains("/") ? end.indexOf("/") : end.length()).replace("_", " ").replace("-", " ");
			String nameEnd = end.replace("_", " ").replace("-", " ");
			
			String relation = (dbObject.containsField("rel")) ? dbObject.get("rel").toString().replace("/r/", "") : null;
			String dataset = (dbObject.containsField("dataset")) ? dbObject.get("dataset").toString().replace("/d/", "") : null;
			String surfaceText = (dbObject.containsField("surfaceText") && dbObject.get("surfaceText") != null) ? dbObject.get("surfaceText").toString() : null;
			String uri = (dbObject.containsField("uri")) ? dbObject.get("uri").toString() : null;
			double weight = (dbObject.containsField("weight")) ? (Double) dbObject.get("weight") : null;
			
			Assertions assertion = new Assertions(id, weight, nameStart, nameEnd, relation, surfaceText, dataset, uri);

			((Entity) assertion).setRepository(this);
			((Entity) assertion).setId(id);
			return assertion;
		}
		return null;
	} 

	@Override
	protected DBObject convert(Assertions assertion, boolean lazy) {
		DBObject dbObject = new BasicDBObject();

		if (assertion.getId() != null) {
			ObjectId objectId = new ObjectId(assertion.getId());
			dbObject.put("id", objectId);
		}

		if (assertion.getNameStart() != null) {
			dbObject.put("start", assertion.getNameStart());
		}
		
		if (assertion.getNameEnd() != null) {
			dbObject.put("end", assertion.getNameEnd());
		}
		
		if (assertion.getRelation() != null) {
			dbObject.put("rel", assertion.getRelation());
		}
		
		return dbObject;
	}

	public static void main(String[] args) {
		MongoDB.changeMongoParameters(Config.SERVIDOR_DB, Config.PORT_DB, Config.DB_NAME_CONCEPTNET);
		System.out.println("Searching...");
		
		List<Assertions> assertion = new AssertionsList().getAssertionByName("girl");
		System.out.println("Size: " + assertion.size());
		
		int i = 0;
		for (Assertions aux : assertion) {
			if (aux != null) {
				System.out.println(aux.getNameStart() + " -> " + aux.getRelation() + " -> " + aux.getNameEnd());
				i++;
			}
		}
		
		System.out.println("Size: " + i);
		System.out.println("Finish...");
	}

}
