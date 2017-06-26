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
//	 * 异步消息处理类
//	 */
//	private IMsg asynMsgHandler;
//
//	/**
//	 * 构造函数
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
//	 * 得到异步消息处理类
//	 * @return
//	 */
//	public IUniformAsynMsgHandler getAsynMsgHandler()
//	{
//		return asynMsgHandler;
//	}
//
//	/**
//	 * 开启计数功能
//	 *
//	 */
//	public void startCount()
//	{
//		this.hasStarted = true;
//	}
//
//	/**
//	 * 通知应答消息已经到达
//	 */
//	@Override
//	protected void notifyResponsed()
//	{
//		setResponsed();
//	}
//
//	/**
//	 * 通知应答消息已经超时
//	 */
//	@Override
//	protected void notifyTimeout()
//	{
//		setTimeout();
//	}
//
//	/**
//	 * 通知应答消息网元断连
//	 */
//	@Override
//	protected void notifyDisconnect() {
//		setDisconnected();
//
//	}


}
