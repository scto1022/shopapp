package de.shop.data;

import static de.shop.ShopApp.jsonBuilderFactory;

import java.io.Serializable;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;


public class Kunde implements Serializable, JsonMappable {
	private static final long serialVersionUID = 1293068472891525321L;
	
	public Long id;
	public String name;
	public String vname;
	public String email;
	public String bestellungURI;

	public Kunde() {
		super();
	}
	
	public Kunde(Long id, String name, String vname, String email) {
		super();
		this.id = id;
		this.name = name;
		this.vname = vname;
		this.email = email;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Kunde other = (Kunde) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Kunde [id=" + id + ", name=" + name + "]";
	}
	
	// TODO Neuen kunden anlegen
	protected JsonObjectBuilder getJsonObjectBuilder() {
	    return jsonBuilderFactory.createObjectBuilder()
	                      	.add("MOCK", name);
	                           
	                          
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
	  id = Long.valueOf(jsonObject.getJsonNumber("kid").longValue());
	  name = jsonObject.getString("nachname");
	  vname = jsonObject.getString("vorname");
	  email = jsonObject.getString("email");
	  bestellungURI = jsonObject.getString("bestellungenUri");
	}
	
	
}
