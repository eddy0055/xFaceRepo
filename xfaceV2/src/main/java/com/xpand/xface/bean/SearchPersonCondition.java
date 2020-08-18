package com.xpand.xface.bean;

import java.io.Serializable;

public class SearchPersonCondition implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int OPERATION_LIKE = 0;
	public static final int OPERATION_GREATHER_THAN = 1;
	public static final int OPERATION_GREATHER_THAN_OR_EQUAL = 2;
	public static final int OPERATION_LESS_THAN = 3;
	public static final int OPERATION_LESS_THAN_OR_EQUAL = 4;	
	public static final int OPERATION_EQUAL = 5;
	
	public static final String FIELD_FULLNAME = "fullName";	
	public static final String FIELD_CERTIFICATENO = "certificateNo";
	String searchField;
	String searchValue;
	int searchOperation;
	public SearchPersonCondition() {
		
	}
	public SearchPersonCondition(String searchField, int searchOperation, String searchValue) {
		this.searchField = searchField;
		this.searchOperation = searchOperation;
		this.searchValue = searchValue;
	}
	public String getSearchField() {
		return searchField;
	}
	public void setSearchField(String searchField) {
		this.searchField = searchField;
	}
	public String getSearchValue() {
		return searchValue;
	}
	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}
	public int getSearchOperation() {
		return searchOperation;
	}
	public void setSearchOperation(int searchOperation) {
		this.searchOperation = searchOperation;
	}
}
