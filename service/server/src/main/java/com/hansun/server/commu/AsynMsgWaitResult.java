package com.hansun.server.commu;


import com.hansun.server.commu.msg.IMsg;

public class AsynMsgWaitResult extends MsgWaitResult {
    public AsynMsgWaitResult(IMsg msg, long timeout, IHandler handler, int port) {
        super(msg, timeout, handler, port);
    }

    @Override
    protected void notifyResponsed() {

    }

    @Override
    protected boolean notifyTimeout() {
        return false;
    }

    @Override
    protected void notifyDisconnect() {

    }
//	/**
//	 * �첽��Ϣ������
//	 */
//	private IMsg asynMsgHandler;
//
//	/**
//	 * ���캯��
//	 * @param timeout
//	 * @param msgHandler
//	 */
//	public AsynMsgWaitResult(long timeout,IUniformAsynMsgHandler msgHandler,ILink link)
//	{
//		super(timeout,link);
//		this.asynMsgHandler = msgHandler;
//
//	}
//
//	/**
//	 * �õ��첽��Ϣ������
//	 * @return
//	 */
//	public IUniformAsynMsgHandler getAsynMsgHandler()
//	{
//		return asynMsgHandler;
//	}
//
//	/**
//	 * ������������
//	 *
//	 */
//	public void startCount()
//	{
//		this.hasStarted = true;
//	}
//
//	/**
//	 * ֪ͨӦ����Ϣ�Ѿ�����
//	 */
//	@Override
//	protected void notifyResponsed()
//	{
//		setResponsed();
//	}
//
//	/**
//	 * ֪ͨӦ����Ϣ�Ѿ���ʱ
//	 */
//	@Override
//	protected void notifyTimeout()
//	{
//		setTimeout();
//	}
//
//	/**
//	 * ֪ͨӦ����Ϣ��Ԫ����
//	 */
//	@Override
//	protected void notifyDisconnect() {
//		setDisconnected();
//
//	}


}
