package de.shop.data;

import java.io.Serializable;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import static de.shop.ShopApp.jsonBuilderFactory;


public class Artikel implements Serializable,JsonMappable {
	private static final long serialVersionUID = 1293068472891525321L;
	
	public Long id;
	public String bezeichnung;
	public double preis;
	public String verfuegbarkeit;
	public int version;
	
	public Artikel() {
	super();
	}
	
	public Artikel(Long id, String bezeichnung, double preis, String verfuegbarkeit, int version) {
		super();
		this.bezeichnung = bezeichnung;
		this.id = id;
		this.preis = preis;
		this.verfuegbarkeit = verfuegbarkeit;
		this.version = 	version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bezeichnung == null) ? 0 : bezeichnung.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		long temp;
		temp = Double.doubleToLongBits(preis);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((verfuegbarkeit == null) ? 0 : verfuegbarkeit.hashCode());
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
		Artikel other = (Artikel) obj;
		if (bezeichnung == null) {
			if (other.bezeichnung != null)
				return false;
		} else if (!bezeichnung.equals(other.bezeichnung))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (Double.doubleToLongBits(preis) != Double
				.doubleToLongBits(other.preis))
			return false;
		if (verfuegbarkeit == null) {
			if (other.verfuegbarkeit != null)
				return false;
		} else if (!verfuegbarkeit.equals(other.verfuegbarkeit))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Artikel [id=" + id + ", bezeichnung=" + bezeichnung
				+ ", preis=" + preis + ", verfuegbarkeit=" + verfuegbarkeit
				+ "]";
	}
	
	 protected JsonObjectBuilder getJsonObjectBuilder() {
		    return jsonBuilderFactory.createObjectBuilder()
		                         .add("aid", id)
		                           .add("bezeichnung", bezeichnung)
		                           .add("verfuegbarkeit", verfuegbarkeit)
		                           .add("preis", preis)
		                           .add("version", version);
		                          
		  }
		  
		  @Override
		  public JsonObject toJsonObject() {
		    return getJsonObjectBuilder().build();
		  }

		  public void fromJsonObject(JsonObject jsonObject) {
		    id = Long.valueOf(jsonObject.getJsonNumber("aid").longValue());
		    version = jsonObject.getInt("version");
		    bezeichnung = jsonObject.getString("bezeichnung");
		    verfuegbarkeit = jsonObject.getString("verfuegbarkeit");
		    preis = jsonObject.getJsonNumber("preis").doubleValue();
		   
		  
		  }
		  
		  @Override
		  public void updateVersion() {
		    version++;
		  }
}
