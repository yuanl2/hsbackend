/**
 * @(#)MsgOutputStream.java	06/09/06
 * 
 * COPYRIGHT DaTang Mobile Communications Equipment CO.,LTD
 */

package com.hansun.server.commu.msg;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * ���ڲ�����Ϣ����
 * @author Administrator
 *
 */
public class MsgOutputStream {
	private final static Logger logger = LoggerFactory.getLogger(MsgOutputStream.class);

	/**
	 * ��������д���ݵ���
	 */
	private DataOutputStream dstream;
	private ByteArrayOutputStream bstream;
	
	public MsgOutputStream()
	{
		bstream = new ByteArrayOutputStream();
		dstream = new DataOutputStream(bstream);
	}
	
	/**
	 * д��һ��int������ ��д����ֽ�
	 * @param c
	 * @return
	 */
	public MsgOutputStream writeInt(int c)
	{
		try
		{
			dstream.writeInt(c);
			return this;
		}
		catch(IOException ex)
		{
			//--------------------------log
			logger.error("write an int data occurs errors!"+ex);
			throw new RuntimeException(ex.toString());
		}
	}
	
	/**
	 * д��һ��String������ 
	 * @param c
	 * @return
	 */
	public MsgOutputStream writeString(String c)
	{
		byte[] bytes = c.getBytes();
		try {
			dstream.write(bytes);
			return this;
		} catch (IOException e) {
			//e.printStackTrace();
			//--------------------------log
			logger.error("write a String data occurs errors!"+e);
			throw new RuntimeException(e.toString());
		}
	}
	
	/**
	 * д��һ��String������,������ݲ���len,����0
	 * @param c
	 * @param len
	 * @return
	 */
	public MsgOutputStream writeString(String c, int len)
	{
		byte[] bytes = c.getBytes();
		if(bytes.length > len)
		{
			throw new RuntimeException("string length beyond parameter len");
		}
		
		try {
				dstream.write(bytes);
				byte[] remainder = new byte[len-bytes.length];
				Arrays.fill(remainder,(byte)0);
				dstream.write(remainder);
				return this;
		} catch (IOException e) {			
			//e.printStackTrace();
			//--------------------------log
			logger.error("write a String data occurs errors!"+e);
			throw new RuntimeException(e.toString());
		}
	}
	
	/**
	 * д��һ��short������ ��д����ֽ�
	 * @param c
	 * @return
	 */public MsgOutputStream writeShort(int c)
	{
		try
		{
			dstream.writeShort(c);
			return this;
		}
		catch(IOException ex)
		{
			//--------------------------log
			logger.error("write a short data occurs errors!"+ex);
			throw new RuntimeException(ex.toString());
		}
	}
	
	/**
	 * д��һ��byte������ 
	 * @param c
	 * @return
	 */
	public MsgOutputStream writeByte(int c)
	{
		try
		{
			dstream.writeByte(c);
			return this;
		}
		catch(IOException ex)
		{
			//--------------------------log
			logger.error("write a Byte data occurs errors!"+ex);
			throw new RuntimeException(ex.toString());
		}
	}
	
	/**
	 * д��һ��byte����
	 * @param bytes
	 * @return
	 */
	public MsgOutputStream writeBytes(byte[] bytes)
	{
		try
		{
			dstream.write(bytes);
			return this;
		}
		catch(IOException ex)
		{
			//--------------------------log
			logger.error("write a Byte Array data occurs errors!"+ex);
			throw new RuntimeException(ex.toString());
		}
	}
	
	/**
	 * ��Ҫ֪��-�����Χ,�Ѿ�����,Ŀǰ������
	 * @param value
	 * @return
	 */
	public MsgOutputStream writeUInt(long value)
	{
		try
		{
			if(value>2*Integer.MAX_VALUE+1){
				throw new RuntimeException("Data is too big!");
			}else{
				dstream.writeInt((int)value);
				return this;
			}			
		}
		catch(IOException ex){
			logger.error("write a Unsigned Int data occurs errors!"+ex);
			throw new RuntimeException(ex.toString());
		}
		
	}
	
	/**
	 * ������
	 * @param value
	 * @return
	 */
	public MsgOutputStream writeUShort(int value)
	{
		try
		{
			if(value>2*Short.MAX_VALUE+1){
				throw new RuntimeException("Data is too big!");
			}else{
				dstream.writeShort(value);
				return this;
			}	
		}
		catch(IOException ex){
			logger.error("write a Unsigned Short data occurs errors!"+ex);
			throw new RuntimeException(ex.toString());
		}		
	}
	
	/**
	 * ������
	 * @param value
	 * @return
	 */
	public MsgOutputStream writeUByte(int value)
	{
		try
		{
			if(value>2*Byte.MAX_VALUE+1){
				throw new RuntimeException("Data is too big!");
			}else{
				dstream.writeByte(value);
				return this;
			}	
		}
		catch(IOException ex){
			logger.error("write a Unsigned Byte data occurs errors!"+ex);
			throw new RuntimeException(ex.toString());
		}
	}
	
	/**
	 * ��д�����ת��byte������ʽ
	 * @return
	 */
	public byte[] toBytes()
	{
		return bstream.toByteArray();
	}
}
