package com.hansun.server.commu;

import com.hansun.server.commu.msg.IMsg;

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


	/**
	 * 表示超时的值
	 */
	private long timeout;
	
	/**
	 * 是否超时
	 */
	private boolean timeoutFlag = false;
	
	/**
	 * 响应消息
	 */
	private IMsg responseMsg = null;
	
    /**
     * 是否得到了响应消息
     */
	private boolean hasResponsed = false;
	
	/**
	 * 标示是否可以开始计数
	 */	
	protected boolean hasStarted = false;
	
	/**
	 * 在创建时认为没有取消的
	 */
	private boolean isCanceled = false; 
		
	/**
	 * 发送消息的链路是否断连了
	 * add by liuyuan 2007-2-3
	 */
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
	/**
	 * 计算当前是否已经超时
	 * @return
	 */
	protected boolean computeTimeout()
	{
		if(hasStarted)
		{
			/* 只有在已经开始计数以后才开始计数 */
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
			/* 如果还没有开始计数,则永远返回false,表示没有超时 */
			return false;
		}
	}
	
	/**
	 * 判断当前link链路是否断开
	 * @return
	 */
	protected boolean isDisconnected(){
		
//		/* 如果link链路状态为ConnectionFail，则返回true */
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
	 * 在设置时,不允许设置null,由调用层保证
	 * @param responseMsg
	 */
	public void setResponseMsg(IMsg responseMsg) {
		this.responseMsg = responseMsg;
		
	}

	/**
	 * 必须被用来继承重载的方法。
	 * 该方法通知应答消息已经到达。
	 * 同步处理中，需要在该方法中真正的notify。
	 * 异步处理中，只需要设置变量，不用notify
	 */
	protected abstract void notifyResponsed();
	
	/**
	 * 必须被用来继承重载的方法。
	 * 该方法通知应答消息已经超时。
	 * 同步处理中，需要在该方法中真正的notify。
	 * 异步处理中，只需要设置变量，不用notify
	 */
	protected abstract boolean notifyTimeout();
	
	/**
	 * 必须被用来继承重载的方法。
	 * 该方法通知应答消息已经超时。
	 * 同步处理中，需要在该方法中真正的notify。
	 * 异步处理中，只需要设置变量，不用notify
	 * add by liuyuan 2007-2-3
	 */
	protected abstract void notifyDisconnect();	
	
	/**
	 * 如果基于某种考虑,需要取消已经创建的等待对象,则调用该方法
	 * 该方法只是设置取消标志,由SyncAsynMsgController中的扫描线程真正的删除它
	 * 
	 */
	public void cancel()
	{
		isCanceled=true;
	}
	
	/**
	 * 返回是否被取消了
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
