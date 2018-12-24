package knowledgeBase.conceptNet;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import br.com.informar.knowledgebase.SourceBase;
import br.com.informar.knowledgebase.collection.Concepts;
import br.com.informar.knowledgebase.collection.Entity;
import br.com.informar.knowledgebase.model.Mention;

public class Assertions extends Entity {

	private String id;
	
	private double weight;
	
	private String nameStart;
	
	private String nameEnd;
	
	private String relation;
	
	private String surfaceText;
	
	private String dataSet;
	
	private String uri;
	
	private String sourceUri;
	
	public Assertions() {
		super();
	}
	
	public Assertions(String id, double weight, String nameStart, String nameEnd, String relation, String surfaceText, String dataSet, String uri) {
		super();
		this.id = id;
		this.weight = weight;
		this.nameStart = nameStart;
		this.nameEnd = nameEnd;
		this.relation = relation;
		this.surfaceText = surfaceText;
		this.dataSet = dataSet;
		this.uri = uri;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getNameStart() {
		return nameStart;
	}

	public void setNameStart(String nameStart) {
		this.nameStart = nameStart;
	}

	public String getNameEnd() {
		return nameEnd;
	}

	public void setNameEnd(String nameEnd) {
		this.nameEnd = nameEnd;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getSurfaceText() {
		return surfaceText;
	}

	public void setSurfaceText(String surfaceText) {
		this.surfaceText = surfaceText;
	}

	public String getDataSet() {
		return dataSet;
	}

	public void setDataSet(String dataSet) {
		this.dataSet = dataSet;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getSourceUri() {
		return sourceUri;
	}

	public void setSourceUri(String sourceUri) {
		this.sourceUri = sourceUri;
	}

	public String getSimpleName() {
		return "assertions";
	}

	private AssertionsList getAssertions() {
		if (getRepository() != null) {
			return (AssertionsList) getRepository();
		}        
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Assertions) {
			return ((Assertions) obj).getId().equals(super.getId());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}
}
