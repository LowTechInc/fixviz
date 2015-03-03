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
		this.separator = separator;
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

    public void replaceMessageSegment(String ori, String newVal){
    	int beginPos = rawFixStr.indexOf(ori);
    	int endPos = beginPos + ori.length();
    	rawFixStr = rawFixStr.substring(0, beginPos) + newVal
    			+ rawFixStr.substring(endPos);
    	//TODO restraint of change. Basically changes on head and some other piece should be closely watched
    	int differ = ori.length() - newVal.length();
    	updateFixLength(differ);
    	
    }
    
    public void insertNewSegment(String SelectedPosition, String newValue){
    	int Pos = rawFixStr.indexOf(SelectedPosition) 
    			+ SelectedPosition.length();
    	
    	rawFixStr = rawFixStr.substring(0, Pos)
    			+ newValue + "|" + rawFixStr.substring(Pos);
    	
    	int differ = -(newValue.length()+1);
    	updateFixLength(differ);
    	
    }
    
    private void updateFixLength(int change){
    	int numIDBegin = rawFixStr.lastIndexOf("|9=")+2;
    	int numIDEnd = rawFixStr.indexOf("|", numIDBegin);
    	String header = rawFixStr.substring(0, numIDBegin+1);
    	String footer = rawFixStr.substring(numIDEnd);
    	int length = Integer.parseInt(rawFixStr.substring(numIDBegin+1, numIDEnd));
    	length -= change;
    	rawFixStr = header + length + footer;
    }
}
