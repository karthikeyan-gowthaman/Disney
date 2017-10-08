package com.ibm.rds.util;

import java.util.Properties;

import com.yantra.interop.japi.YIFCustomApi;

/**
 * This class is the base custom API class that all Home Depot custom APIs shall
 * extend.
 * 
 * @author Shresta NM
 * 
 */
public class BaseCustomAPI implements YIFCustomApi, XMLLiterals, Constants {

	Properties mProperties = new Properties();

	/**
	 * This method is overridden from interface.
	 * 
	 * @param properties
	 *            Properties set
	 */
	public void setProperties(Properties properties) {
		if (properties != null) {
			this.mProperties = properties;
		}
	}

	/**
	 * This method is overridden from interface.
	 * 
	 * @return Properties
	 */
	public Properties getProperties() {
		return this.mProperties;
	}

}
