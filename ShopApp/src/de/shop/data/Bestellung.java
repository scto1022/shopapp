package de.shop.data;

import static de.shop.ShopApp.jsonBuilderFactory;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;


public class Bestellung implements Serializable, JsonMappable {
	private static final long serialVersionUID = 1293068472891525011L;
	
	public String id;
	public String gesamtpreis;

	public Bestellung() {
		super();
	}
	
	public Bestellung(String id, String gesamtpreis) {
		super();
		this.id = id;
		this.gesamtpreis = gesamtpreis;
	}

	
	
	@Override
	public String toString() {
		return "Bestellung [id=" + id + ", gesamtpreis=" + gesamtpreis + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((gesamtpreis == null) ? 0 : gesamtpreis.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bestellung other = (Bestellung) obj;
		if (gesamtpreis == null) {
			if (other.gesamtpreis != null)
				return false;
		} else if (!gesamtpreis.equals(other.gesamtpreis))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	// TODO Neue Bestellung anlegen
	protected JsonObjectBuilder getJsonObjectBuilder() {
	    return jsonBuilderFactory.createObjectBuilder()
	                      	.add("MOCK", id);
	                           
	                          
	}
	  
	@Override
	public JsonObject toJsonObject() {
		return getJsonObjectBuilder().build();
	}
	
	// TODO Version implementieren
	@Override
	public void updateVersion() {
	}
	
	public void fromJsonObject(JsonObject jsonObject) {
	  id = String.valueOf(jsonObject.getJsonNumber("bId").longValue());
	  gesamtpreis = String.valueOf(jsonObject.getJsonNumber("gesamtpreis").bigDecimalValue());
	}
	
	
}
