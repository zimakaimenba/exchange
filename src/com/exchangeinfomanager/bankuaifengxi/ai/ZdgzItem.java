package com.exchangeinfomanager.bankuaifengxi.ai;

public class ZdgzItem {

	public ZdgzItem(String id) 
	{
		this.id = id;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the selectedcolor
	 */
	public String getSelectedcolor() {
		return selectedcolor;
	}
	/**
	 * @param selectedcolor the selectedcolor to set
	 */
	public void setSelectedcolor(String selectedcolor) {
		this.selectedcolor = selectedcolor;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return the contents
	 */
	public String getContents() {
		return contents;
	}
	/**
	 * @param contents the contents to set
	 */
	public void setContents(String contents) {
		this.contents = contents;
	}

	private String id;
	private String selectedcolor;
	private String value;
	private String contents;

}
