/**
  ******************************************************************************
  * @file    Demo/src/main.c 
  * @author  MCD Application Team
  * @version V1.0.0
  * @date    09/13/2010
  * @brief   Main program body
  ******************************************************************************
  * @copy
  *
  * THE PRESENT FIRMWARE WHICH IS FOR GUIDANCE ONLY AIMS AT PROVIDING CUSTOMERS
  * WITH CODING INFORMATION REGARDING THEIR PRODUCTS IN ORDER FOR THEM TO SAVE
  * TIME. AS A RESULT, STMICROELECTRONICS SHALL NOT BE HELD LIABLE FOR ANY
  * DIRECT, INDIRECT OR CONSEQUENTIAL DAMAGES WITH RESPECT TO ANY CLAIMS ARISING
  * FROM THE CONTENT OF SUCH FIRMWARE AND/OR THE USE MADE BY CUSTOMERS OF THE
  * CODING INFORMATION CONTAINED HEREIN IN CONNECTION WITH THEIR PRODUCTS.
  *
  * <h2><center>&copy; COPYRIGHT 2010 STMicroelectronics</center></h2>
  */ 

/* Includes ------------------------------------------------------------------*/
#include "stm32F10x.h"
#include "usart.h"
#include "usart2.h"
#include "usart3.h"
#include "delay.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "timer.h"
#include "YR_4G.h"
#include "device.h"
#include "queue.h"
#include "rtc.h"

////////////////////////ST�ٷ���������Ҫ�ı����ͺ���///////////////////////////////////////////

/**********************�����ж����ȼ�����******************************************  
                PreemptionPriority        SubPriority
USART3                 0											0

DMA1                   1											0

TIM7									 2											0	

TIM6									 3											0

TIM4                   3                      1

************************************************************************************/

////////////////////////�û������Զ���ı����ͺ���///////////////////////////////////////////
void Reset_Device_Status(void)
{
	dev.is_login = FALSE;
	dev.hb_ready = FALSE;
	dev.hb_timer = 0;
	dev.wait_reply = FALSE;
	dev.reply_timer = 0;
	dev.portClosed = 0;
	dev.msg_seq = 0;
}

int main(void)
{
	u8 i;
	char recv[MAXSIZE+1]={0};
	char *uart_data_left;
	char *p, *p1;	
	u16 length = 0; 
	u8 sum = 0;
	u8 sum_msg = 0;	
	MsgSrv *msgSrv=NULL;
	bool retryConn = FALSE;
	//�����ж����ȼ�����Ϊ��2��2λ��ռ���ȼ���2λ��Ӧ���ȼ�
	NVIC_PriorityGroupConfig(NVIC_PriorityGroup_2); 
	/* Enable GPIOx Clock */
	RCC_APB1PeriphClockCmd(RCC_APB1Periph_PWR, ENABLE);

	q = CycQueueInit();
	
	if(q == NULL)
	{
		//BSP_Printf("malloc error!\n");
	}
	
	delay_init();

#ifdef LOG_ENABLE	
	//ע�⴮��1�����������ͣ����յĺ�û��ʹ��
	//#define EN_USART1_RX 			   0		//ʹ�ܣ�1��/��ֹ��0������1����
	usart1_init(115200);                            //����1,Log
#endif

	//usart2_init(9600);                            //����2,�Խ�����
	usart3_init(115200);                            //����3,�Խ�YR4G

	rtc_init();	
	Reset_Device_Status();
	dev.need_login = LOGIN_POWERUP;		
	Clear_Usart3();
	Device_Init();
	//Device_ON(DEVICE_01);
	//Device_ON(DEVICE_04);
	
#if GPIO_TEST
	while(1)
	{	
		Device_ON(DEVICE_01);
		delay_s(1);  
		BSP_Printf("2: %d 3: %d 4: %d\n", Device_Power_Status(DEVICE_01),
        	GPIO_ReadInputDataBit(GPIOA, GPIO_Pin_3), GPIO_ReadInputDataBit(GPIOA, GPIO_Pin_4));		
		BSP_Printf("Power: %d Busy: %d Working: %d\n", Device_Power_Status(DEVICE_01), isDevBusy(DEVICE_01), isDevWorking(DEVICE_01));
		delay_s(5);
		Device_OFF(DEVICE_01);	
		delay_s(1);
		BSP_Printf("2: %d 3: %d 4: %d\n", Device_Power_Status(DEVICE_01),
        	GPIO_ReadInputDataBit(GPIOA, GPIO_Pin_3), GPIO_ReadInputDataBit(GPIOA, GPIO_Pin_4));	
		BSP_Printf("Power: %d Busy: %d Working: %d\n", Device_Power_Status(DEVICE_01), isDevBusy(DEVICE_01), isDevWorking(DEVICE_01));
		delay_s(5);
		
	}
#endif

	BSP_Printf("\n\nSW VERSION: %s Clock: %d\n\n", SW_VERSION, SystemCoreClock);
	for(i=DEVICE_01; i<DEVICEn; i++)
	{
		BSP_Printf("Power[%d]: %d\n", i, Device_Power_Status(i));
	}

#if CLOCK_TEST
	//TIM_General_Init(29999,2399);						     // 1s�ж�
	//TIM_General_Init(3999,1999);						     // 1s�ж�	
	TIM_General_Set(1000);
	TIM_SetCounter(TIM_GENERAL,0); 
	TIM_Cmd(TIM_GENERAL,ENABLE);
	while(1)
	{
		if(dev.hb_timer >= 60)
		{
			i = RTC_GetCounter();	
			BSP_Printf("[%0.2d:%0.2d:%0.2d]\n", i / 3600, (i % 3600) / 60, (i % 3600) % 60);		
			dev.hb_timer = 0;
		}
	}
#endif

#if 0  //for usart2 test
	u8 cmd[2]={0x12, 0x34};
	while(!Device_SendCmd(cmd, sizeof(cmd), recv, 1000));
	BSP_Printf("uart2 test phase 1 finished\r\n");
	cmd[0]=0x22;
	while(!Device_SendCmd(cmd, sizeof(cmd), recv, 1000));
	BSP_Printf("uart2 test finished\r\n");
#endif

	YR4G_ResetRestart();
	BSP_Printf("YR4GC�������\r\n");
	
	while(!YR4G_Link_Server())
	{
		BSP_Printf("INIT: YR Module not working\r\n");
	}

	BSP_Printf("YR4GC Connect to Network\r\n");
	Device_Network_Ind(TRUE, FALSE);
	lastInActivity = lastOutActivity = RTC_GetCounter();
	SendLogin();

	BSP_Printf("YR4GC Send Login\r\n");

	//TIM_General_Init(29999,2399);						     // 1s�ж�
	//TIM_General_Init(3999,1999);
	TIM_General_Set(1000);
	TIM_SetCounter(TIM_GENERAL,0); 
	TIM_Cmd(TIM_GENERAL,ENABLE);

	//TIM_Cmd(TIM_UART2,ENABLE);

	//for Connect-LED
	//TIM_Ind_Set(1000);

	while(1)
	{		
		while(isWorking())
		{
#if 0			
			if(((lastInActivity>lastOutActivity)&&((lastInActivity-lastOutActivity)>REPLY_1_MIN))
				|| ((lastOutActivity>lastInActivity)&&((lastOutActivity-lastInActivity)>REPLY_1_MIN)))
			{
				if(!retryConn){
					BSP_Printf("Start flashing Connect-LED...\n");
					TIM_SetCounter(TIM_UART2,0); 
					TIM_Cmd(TIM_UART2,ENABLE);
					retryConn = TRUE;
				}
			}
#endif
			if(((lastInActivity>lastOutActivity)&&((lastInActivity-lastOutActivity)>DISCONNECT_TIMEOUT))
				|| ((lastOutActivity>lastInActivity)&&((lastOutActivity-lastInActivity)>DISCONNECT_TIMEOUT)))
			{
				BSP_Printf("lastInActivity: %d, lastOutActivity: %d\n", lastInActivity, lastOutActivity);
				dev.need_login = LOGIN_SEND_RECV_TIMEOUT;				
				goto Restart;
			}

			if((lastInActivity>TIMEVAL_MAX) && (lastOutActivity>TIMEVAL_MAX))
			{
				uint32_t temp=RTC_GetCounter();
				while(temp>TIMEVAL_MAX)
					temp-=(TIMEVAL_MAX+1);
				RTC_Configuration();
				Time_Adjust(temp);
				lastInActivity-=(TIMEVAL_MAX+1);
				lastOutActivity-=(TIMEVAL_MAX+1);				
			}
			
			if(!dev.is_login)
			{
				if(dev.reply_timer >= REPLY_1_MIN)
					SendLogin();  //is_login: login msg switch
			}
			else
			{
				if(dev.hb_ready)
				{
					SendHeart();
					dev.hb_ready = FALSE;
				}

				//case 1: finish msg not sent yet
				//case 2: finish msg already sent but timeout
				if(((dev.portClosed != 0) && !dev.wait_reply) ||(dev.wait_reply && (dev.reply_timer >= REPLY_1_MIN)))
				{
					//if(isAnyDevBusy())
					//	dev.wait_busy = TRUE;
					SendFinish();
				}
			}
			
			if(DumpQueue(recv) != NULL)
			{
				if((strstr(recv,"reset")!=NULL) || strstr(recv,"reboot")!=NULL)
				{
					dev.need_login = LOGIN_DISCONNECT;				
					goto Restart;
				}
				
				uart_data_left = (char *)recv;
				while((p=strstr(uart_data_left, MSG_STR_SERVER_HEADER))!=NULL)
				{
					if((p1=strstr((const char*)p,"#"))!=NULL)
					{
						//�������ͺ�����У�����	
						length = p1 - p +1;
						//У������
						sum = CheckSum((char *)(p),length-5);
						BSP_Printf("sum:%d\r\n",sum);
						
						//ȡ�ַ����е�У��ֵ,У��ֵת��Ϊ���֣�����ӡ
						sum_msg = atoi((const char *)(p+length-5));	
						BSP_Printf("sum_msg:%d\r\n",sum_msg);
						
						//������ȷ
						if(sum == sum_msg)
						{
							msgSrv = (MsgSrv *)p;						
							u8 seq = atoi(msgSrv->seq);
							lastInActivity = RTC_GetCounter();
							//TIM_Cmd(TIM_UART2,DISABLE);
							//retryConn = FALSE;
							//Device_Network_Ind(TRUE, FALSE);
							BSP_Printf("[%d]: Recv[%d] Seq[%d] Dup[%d] from Server\n", lastInActivity, atoi(msgSrv->id), seq, atoi(msgSrv->dup));

							if(!dev.is_login)
							{
								if(atoi(msgSrv->id) == MSG_STR_ID_LOGIN)
								{
									uint32_t TimeVar=atoi(p+sizeof(MsgSrv));
									if((TimeVar > 0) && (TimeVar <= TIMEVAL_MAX))
									{
										RTC_Configuration();
										Time_Adjust(TimeVar);
										lastInActivity = lastOutActivity = RTC_GetCounter();
										dev.is_login = TRUE;
									}
								}

								break;
							}
								
							switch(atoi(msgSrv->id))
							{
								case MSG_STR_ID_OPEN:
								{
									dev.msg_seq_s = seq;
									
									char *interfaces, *periods, *extra_sec;
									bool interface_on[DEVICEn]={FALSE};
									int period_on[DEVICEn]={0};
									int extra_sec[DEVICEn]={0};
									bool alreadySet = FALSE;
									//���ݵ�ǰ�豸״̬���п���(GPIO)���Ѿ����˵ľͲ�������
									//�����豸�����ؼ�ʱ
									interfaces = strtok(p+sizeof(MsgSrv), ",");
									if(interfaces)
									{						
										//BSP_Printf("ports: %s\n", interfaces);
									}
									for(i=DEVICE_01; i<DEVICEn; i++)
									{
										interface_on[i]=(interfaces[i]=='1')?TRUE:FALSE;
										if((interface_on[i]) && (g_device_status[i].seq == seq))
										{
											alreadySet = TRUE;
										}
									}

									if(alreadySet)
									{
										SendStartAck();
										break;		
									}
									
									periods = strtok(NULL, ",");
									if(periods)
									{						
										//BSP_Printf("periods: %s\n", periods);	
#if TEST
										sscanf(periods, "%02d,", &period_on[DEVICE_01]);
#else
										sscanf(periods, "%02d%02d%02d%02d,", &period_on[DEVICE_01], 
											&period_on[DEVICE_02], &period_on[DEVICE_03], &period_on[DEVICE_04]);
#endif										
									} else {
										break;
									}

									extra_sec = strtok(NULL, ",");
									if(extra_sec)
									{						
										//BSP_Printf("extra_sec: %s\n", extra_sec);
#if TEST
										sscanf(periods, "%02d,", &extra_sec[DEVICE_01]);
#else
										sscanf(extra_sec, "%02d%02d%02d%02d,", &extra_sec[DEVICE_01], 
											&extra_sec[DEVICE_02], &extra_sec[DEVICE_03], &extra_sec[DEVICE_04]);
#endif											
									} else {
										break;
									}

									for(i=DEVICE_01; i<DEVICEn; i++)
									{
										if(interface_on[i]){
#if TEST										
											//if(!isDevWorking(i) || isDevBusy(i))
											if(!isDevWorking(i))
											{						
												BSP_Printf("Wrong Status\n");	
											}												
											else
#endif												
												if(g_device_status[i].power == OFF)
												{
													g_device_status[i].total = period_on[i] * NUMBER_TIMER_1_MINUTE + extra_sec[i];
													g_device_status[i].passed = 0;
													g_device_status[i].power = ON;		
													g_device_status[i].seq = seq;											
													Device_ON(i);	
												}
										}
									}

									SendStartAck();
								}
								break;
								case MSG_STR_ID_CLOSE:
								{
									if(seq < dev.msg_seq)
										break;

									dev.portClosed = 0;
									dev.wait_reply = FALSE;
								}
								break;
								case MSG_STR_ID_HB:
								break;
								default:
								break;

							}
						}
						uart_data_left = p1;
					}
					else
						break;
				}		
			}
		}

Restart:
		Reset_Device_Status();
		YR4G_ResetRestart();
		//TIM_Cmd(TIM_UART2,DISABLE);
		//retryConn = FALSE;
		Device_Network_Ind(FALSE, FALSE);
		while(!YR4G_Link_Server())
		{
			BSP_Printf("INIT: YR Module not working\r\n");
		}
		Device_Network_Ind(TRUE, FALSE);
		lastInActivity = lastOutActivity = RTC_GetCounter();
		SendLogin();	
	}
}


#ifdef  USE_FULL_ASSERT
/**
  * @brief  Reports the name of the source file and the source line number
  *         where the assert_param error has occurred.
  * @param  file: pointer to the source file name
  * @param  line: assert_param error line source number
  * @retval None
  */
void assert_failed(uint8_t* file, uint32_t line)
{ 
  /* User can add his own implementation to report the file name and line number,
     ex: printf("Wrong parameters value: file %s on line %d\r\n", file, line) */

  /* Infinite loop */
  while (1)
  {
  }
}
#endif

/**
  * @}
  */

/**
  * @}
  */

/******************* (C) COPYRIGHT 2010 STMicroelectronics *****END OF FILE****/
