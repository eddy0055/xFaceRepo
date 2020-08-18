package com.xpand.xface.util;

import org.hibernate.cfg.ImprovedNamingStrategy;

public class MyNamingStrategy extends ImprovedNamingStrategy {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    public String classToTableName(String className) {        
        return className;
    }

    @Override
    public String propertyToColumnName(String propertyName) {
        return propertyName;
    }

    @Override
    public String columnName(String columnName) {
        return columnName;
    }

    @Override
    public String tableName(String tableName) {
        return tableName;
    }
}