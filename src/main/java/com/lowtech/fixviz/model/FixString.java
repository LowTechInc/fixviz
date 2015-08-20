package com.lowtech.fixviz.model;

import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.InvalidMessage;
import quickfix.fix50.Message;

public class FixString {
    public static final String DEFAULT_DATA_DICTIONARY = "FIX50.xml";
	private static final String GENUINE_SEPARATOR = "\u0001";
	
	private String rawFixStr = null;
	private String separator = "|";
	private DataDictionary dictionary = null;
	
	public FixString(String fixStr) {
		this.rawFixStr = fixStr;
		try {
			dictionary = new DataDictionary(DEFAULT_DATA_DICTIONARY);
		} 
		catch (ConfigError err) {
			System.err.println(err);
		}
	}
	
	public Message toFixMessage() {
		Message fixMessage = new Message();
        try {
            fixMessage.fromString(rawFixStr.replace(separator, GENUINE_SEPARATOR), dictionary, false);
        }
        catch (InvalidMessage err)    {
        	System.err.println(err);
            return null;
        }
        
        return fixMessage;
	}
	
	public String getFixStr() {
		return rawFixStr;
	}
	
	public void setSeparator(String separator) {
		if (separator != null && !separator.isEmpty()) {
			this.rawFixStr = this.rawFixStr.replace(this.separator, separator);
			this.separator = separator;
		}
	}
	
	public String getSeparator() {
		return this.separator;
	}
	
	public DataDictionary getDictionary() {
		return this.dictionary;
	}
	
    public void setDictionary(String dataDictionaryFile) throws ConfigError {
        dictionary = new DataDictionary(dataDictionaryFile);
    }
}
