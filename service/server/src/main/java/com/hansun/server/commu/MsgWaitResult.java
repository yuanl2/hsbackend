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
	 * ��ʾ��ʱ��ֵ
	 */
	private long timeout;
	
	/**
	 * �Ƿ�ʱ
	 */
	private boolean timeoutFlag = false;
	
	/**
	 * ��Ӧ��Ϣ
	 */
	private IMsg responseMsg = null;
	
    /**
     * �Ƿ�õ�����Ӧ��Ϣ
     */
	private boolean hasResponsed = false;
	
	/**
	 * ��ʾ�Ƿ���Կ�ʼ����
	 */	
	protected boolean hasStarted = false;
	
	/**
	 * �ڴ���ʱ��Ϊû��ȡ����
	 */
	private boolean isCanceled = false; 
		
	/**
	 * ������Ϣ����·�Ƿ������
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
	 * ���㵱ǰ�Ƿ��Ѿ���ʱ
	 * @return
	 */
	protected boolean computeTimeout()
	{
		if(hasStarted)
		{
			/* ֻ�����Ѿ���ʼ�����Ժ�ſ�ʼ���� */
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
			/* �����û�п�ʼ����,����Զ����false,��ʾû�г�ʱ */
			return false;
		}
	}
	
	/**
	 * �жϵ�ǰlink��·�Ƿ�Ͽ�
	 * @return
	 */
	protected boolean isDisconnected(){
		
//		/* ���link��·״̬ΪConnectionFail���򷵻�true */
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
	 * ������ʱ,����������null,�ɵ��ò㱣֤
	 * @param responseMsg
	 */
	public void setResponseMsg(IMsg responseMsg) {
		this.responseMsg = responseMsg;
		
	}

	/**
	 * ���뱻�����̳����صķ�����
	 * �÷���֪ͨӦ����Ϣ�Ѿ����
	 * ͬ�������У���Ҫ�ڸ÷�����������notify��
	 * �첽�����У�ֻ��Ҫ���ñ���������notify
	 */
	protected abstract void notifyResponsed();
	
	/**
	 * ���뱻�����̳����صķ�����
	 * �÷���֪ͨӦ����Ϣ�Ѿ���ʱ��
	 * ͬ�������У���Ҫ�ڸ÷�����������notify��
	 * �첽�����У�ֻ��Ҫ���ñ���������notify
	 */
	protected abstract boolean notifyTimeout();
	
	/**
	 * ���뱻�����̳����صķ�����
	 * �÷���֪ͨӦ����Ϣ�Ѿ���ʱ��
	 * ͬ�������У���Ҫ�ڸ÷�����������notify��
	 * �첽�����У�ֻ��Ҫ���ñ���������notify
	 * add by liuyuan 2007-2-3
	 */
	protected abstract void notifyDisconnect();	
	
	/**
	 * �������ĳ�ֿ���,��Ҫȡ���Ѿ������ĵȴ�����,����ø÷���
	 * �÷���ֻ������ȡ����־,��SyncAsynMsgController�е�ɨ���߳�������ɾ����
	 * 
	 */
	public void cancel()
	{
		isCanceled=true;
	}
	
	/**
	 * �����Ƿ�ȡ����
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
