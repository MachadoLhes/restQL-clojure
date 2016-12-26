package pdg.query;

import java.util.HashMap;
import java.util.Map.Entry;

import pdg.exception.UniqueNameViolationException;

public class Query {

	private HashMap<String, QueryItem> items;
	
	private HashMap<String, String> metadata;
	
	
	public Query() {
		this.items = new HashMap<>();
		this.metadata = new HashMap<>();
	}
	
	public Boolean isEmptyQuery() {
		return (this.items.isEmpty());
	}
	
	/**
	 * Adds a new item with an unique name to the query.
	 * 
	 * @param item {@link QueryItem}
	 * 
	 * @throws UniqueNameViolationException if item name already exists in the query object.
	 * 
	 * @return {@link Query} this
	 */
	public Query addItem(QueryItem item) throws UniqueNameViolationException {
		if(this.items.containsKey(item.getName()))
			throw new UniqueNameViolationException();
		
		this.items.put(item.getName(), item);
		
		return this;
	}
	
	
	/**
	 * Adds a new metadata to the query.
	 * 
	 * @param key The metadata key
	 * @param value The metadata value
	 */
	public void addMeta(String key, String value) {
		this.metadata.put(key, value);
	}
	
	
	/**
	 * Converts the metadata parameters to query object.
	 * 
	 * @return {@link String} the object String
	 */
	private String convertMetadataToString() {
		
		if(this.metadata.isEmpty())
			return "";
		
		String queryString = "^{";
		
		for(Entry<String, String> meta : this.metadata.entrySet()) {
			queryString += ":"+meta.getKey() + " \"" + meta.getValue() + "\" ";
		}
		
		queryString = queryString.trim();
		queryString += "}";
		
		return queryString;
	}
	
	
	@Override
	public String toString() {
		
		String queryString = this.convertMetadataToString() + "[";
		
		for(Entry<String, QueryItem> itemEntry : this.items.entrySet()) {
			queryString += itemEntry.getValue() + " ";
		}
		
		queryString = queryString.trim();
		
		queryString += "]";
		
		
		return queryString;
	}
	
}
