package com.hansun.server.commu;

import com.hansun.server.commu.common.IMsg;

public abstract class MsgWaitResult {

	protected int retryCount;

	private IMsg requestMsg = null;

	public IMsg getRequestMsg() {
		return requestMsg;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}


	private long timeout;

	private boolean timeoutFlag = false;

	private IMsg responseMsg = null;
	
	private boolean hasResponsed = false;
	
	protected boolean hasStarted = false;
	
	private boolean isCanceled = false;
		
	private boolean isDisconnected = false;
	
	private IHandler handler = null;

	private int port;
	
	public MsgWaitResult(IMsg msg, long timeout,IHandler handler, int port)
	{
		this.requestMsg = msg;
		this.timeout = timeout;
		this.handler = handler;
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public boolean isTimeout()
	{
		return timeoutFlag;
	}
	
	public void setTimeout()
	{
		timeoutFlag = true;
	}

	protected boolean computeTimeout()
	{
		if(hasStarted)
		{
			timeout--;
			if(timeout < 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	protected boolean isDisconnected(){
		
//		if(link.getLinkState().getVal() == BusinessLinkState.ConnectionFail.getVal()){
//			return true;
//		}
//		else{
//			return false;
//		}
		return false;
		
	}

	protected boolean hasResponsed()
	{
		return hasResponsed;
	}
	
	protected void setResponsed()
	{
		hasResponsed = true;
	}

	public IMsg getResponseMsg() {
		return responseMsg;
	}


	/**
	 * @param responseMsg
	 */
	public void setResponseMsg(IMsg responseMsg) {
		this.responseMsg = responseMsg;
		
	}


	protected abstract void notifyResponsed();
	

	protected abstract boolean notifyTimeout();
	

	protected abstract void notifyDisconnect();	
	
	/**
	 *
	 */
	public void cancel()
	{
		isCanceled=true;
	}
	
	/**
	 * @return
	 */
	boolean isCanceled()
	{
		return isCanceled;
	}

	/**
	 * @return Returns the isDisconnected.
	 */
	protected boolean hasDisconnected() {
		return isDisconnected;
	}

	/**
	 *
	 */
	protected void setDisconnected() {
		isDisconnected = true;
	}

	/**
	 * @return Returns the link.
	 */
	protected IHandler getHandler() {
		return handler;
	}

	/**
	 * @param handler The link to set.
	 */
	protected void setHandler(IHandler handler) {
		this.handler = handler;
	}

}
