package es.upv.pros.pvalderas.contextmanager;

import org.json.JSONArray;

public class Composition {
	private String id;
	private String user;
	private String system;
	private JSONArray events;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getSystem() {
		return system;
	}
	public void setSystem(String system) {
		this.system = system;
	}
	public JSONArray getEvents() {
		return events;
	}
	public void setEvents(JSONArray events) {
		this.events = events;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		if(id==null)
			return "Select am IoT-Enhanced BP...";
		else
			return id+" developed by "+user;
	}
	
	
}
