package com.hansun.server.common;

public class TimeoutException extends Exception
{
	/** **/
    public TimeoutException(String arg){
    	super(arg);
    }
    
    /** **/
    public TimeoutException(String arg,Throwable cause){
    	super(arg,cause);
    }
    
    /** **/
    public TimeoutException(){
    	super();
    }
}
