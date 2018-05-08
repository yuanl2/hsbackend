#include "sys.h"
#include "timer.h"
#include "usart.h"
#include "usart2.h"
#include "usart3.h"
#include "string.h"  
#include "stdlib.h"  
#include "device.h"
#include "queue.h"
#include "YR_4G.h"
#include "rtc.h"

extern void Reset_Device_Status(u8 status);

void TIM_Uart2_IRQHandler(void)
{
#if ENABLE_UART2
	if (TIM_GetITStatus(TIM_UART2, TIM_IT_Update) != RESET)
	{	 		
		TIM_ClearITPendingBit(TIM_UART2, TIM_IT_Update);  
		USART2_RX_STA|=1<<15;	//标记接收完成
		TIM_Cmd(TIM_UART2, DISABLE); 

		USART2_RX_BUF[USART2_RX_STA&0X7FFF]=0;	//添加结束符 
		
		//BSP_Printf("USART BUF:%s\r\n",USART2_RX_BUF);
		for(u8 i=0; i<(USART2_RX_STA&0x7FFF); i++)
			BSP_Printf("0x%02x ",USART2_RX_BUF[i]);
		BSP_Printf("\n");
		//Clear_Usart2();
	}
#else
	if (TIM_GetITStatus(TIM_UART2, TIM_IT_Update) != RESET)
	{	 		
		TIM_ClearITPendingBit(TIM_UART2, TIM_IT_Update);
		Device_Network_Ind(TRUE, TRUE);
		TIM_SetCounter(TIM_UART2,0); 
	}
#endif
}
	    
void TIM_General_IRQHandler(void)
{
	u8 index;
	//BSP_Printf("dev.hb_timer %d\n", dev.hb_timer);
#if CLOCK_TEST
	BSP_Printf("秒定时器: %d\n", dev.hb_timer++);
	TIM_ClearITPendingBit(TIM_GENERAL, TIM_IT_Update); 
	TIM_SetCounter(TIM_GENERAL,0);
	return;
#endif

	if(TIM_GetITStatus(TIM_GENERAL, TIM_IT_Update) != RESET)					  //是更新中断
	{	
		TIM_ClearITPendingBit(TIM_GENERAL, TIM_IT_Update);  					//清除TIM6更新中断标志
		//Time_Display(RTC_GetCounter());
		
		if(!dev.hb_ready)
		{
			if(dev.hb_timer >= HB_1_MIN)
			{				
				BSP_Printf("TIM: HB Ready\r\n");
				dev.hb_ready = TRUE;
				dev.hb_timer = 0;
			}
			else
				dev.hb_timer++;
		}

		for(index=DEVICE_01; index<DEVICEn; index++)
		{
			switch(g_device_status[index].power)
			{
				case ON:
				{
					if(g_device_status[index].total==0)
					{
						BSP_Printf("Error: Dev[%d] %d %d %d\n", index, g_device_status[index].power, g_device_status[index].total, g_device_status[index].passed);
						g_device_status[index].power = UNKNOWN;
					}
					else
					{
						if(g_device_status[index].passed >= g_device_status[index].total)
						{
							BSP_Printf("Dev[%d]: %d %d %d\n", index, g_device_status[index].power, g_device_status[index].total, g_device_status[index].passed);					
							g_device_status[index].passed=g_device_status[index].total=0;
							g_device_status[index].power=OFF;
							Device_OFF(index);
							BSP_Printf("TIM: 设置设备状态为CLOSE_DEVICE\r\n");
							dev.portClosed |= 1<<index;
							dev.wait_reply = FALSE;
						}
						else{
							g_device_status[index].passed++;
							Device_InTask_Ind(TRUE);
						}
					}
				}	
				break;
					
				case OFF:
					if(g_device_status[index].total!=0)
						g_device_status[index].power = UNKNOWN;
				break;
					
				case UNKNOWN:
				default:
				break;
			}
		}
	
		if(dev.wait_reply || !dev.is_login)
		{
			if(dev.reply_timer < REPLY_1_MIN)
				dev.reply_timer++;
		}
		
		TIM_SetCounter(TIM_GENERAL,0); 
	}
}

void TIM_Uart3_IRQHandler(void)
{ 	
	u16 i = 0; 

	if (TIM_GetITStatus(TIM_UART3, TIM_IT_Update) != RESET)
	{	 		
		TIM_ClearITPendingBit(TIM_UART3, TIM_IT_Update);  //清除TIM7更新中断标志    
		USART3_RX_STA|=1<<15;	//标记接收完成
		TIM_Cmd(TIM_UART3, DISABLE);  //关闭TIM7
		
		//Way 1
		USART3_RX_BUF[USART3_RX_STA&0X7FFF]=0;	//添加结束符 

		//Way 2
		for(i=0; i<(USART3_RX_STA&0X7FFF); i++)
			CycQueueIn(q,USART3_RX_BUF[i]);
		
		BSP_Printf("USART BUF:%s\r\n",USART3_RX_BUF);
		Clear_Usart3();
	}

}

void TIM2_IRQHandler(void)
{ 	
	TIM_Uart2_IRQHandler();
}

void TIM3_IRQHandler(void)
{
	TIM_General_IRQHandler();
}
	    
void TIM4_IRQHandler(void)
{ 	
	TIM_Uart3_IRQHandler();
}

void TIM5_IRQHandler(void)
{ 	
	TIM_Uart2_IRQHandler();
}

void TIM6_IRQHandler(void)
{
	TIM_General_IRQHandler();
}
	    
void TIM7_IRQHandler(void)
{ 	
	TIM_Uart3_IRQHandler();
}

void TIM_Uart2_Init(u16 arr,u16 psc)
{	
	NVIC_InitTypeDef NVIC_InitStructure;
	TIM_TimeBaseInitTypeDef  TIM_TimeBaseStructure;

	RCC_APB1PeriphClockCmd(TIM_UART2_APB, ENABLE);//TIM2时钟使能    
	
	//定时器TIM2初始化
	TIM_TimeBaseStructure.TIM_Period = arr;                     //设置在下一个更新事件装入活动的自动重装载寄存器周期的值	
	TIM_TimeBaseStructure.TIM_Prescaler =psc;                   //设置用来作为TIMx时钟频率除数的预分频值
	TIM_TimeBaseStructure.TIM_ClockDivision = TIM_CKD_DIV1;     //设置时钟分割:TDTS = Tck_tim
	TIM_TimeBaseStructure.TIM_CounterMode = TIM_CounterMode_Up; //TIM向上计数模式
	TIM_TimeBaseInit(TIM2, &TIM_TimeBaseStructure);             //根据指定的参数初始化TIMx的时间基数单位
 
	TIM_ITConfig(TIM_UART2,TIM_IT_Update,ENABLE );                   //使能指定的TIM5中断,允许更新中断
	
	TIM_Cmd(TIM_UART2,ENABLE);//开启定时器5
	
	NVIC_InitStructure.NVIC_IRQChannel = TIM_UART2_IRQ;
	NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority = 2 ;	//抢占优先级0
	NVIC_InitStructure.NVIC_IRQChannelSubPriority = 0;		    	//子优先级2
	NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;			      	//IRQ通道使能
	NVIC_Init(&NVIC_InitStructure);	//根据指定的参数初始化VIC寄存器
	
}

//通用定时器6中断初始化
//这里选择为APB1的1倍，而APB1为72M
//arr：自动重装值。
//psc：时钟预分频数
//定时器溢出时间计算方法:Tout=((arr+1)*(psc+1))/Ft us.
//Ft=定时器工作频率,单位:Mhz 
//arr：自动重装值。
//psc：时钟预分频数	
void TIM_General_Init(u16 arr,u16 psc)
{	
	NVIC_InitTypeDef NVIC_InitStructure;
	TIM_TimeBaseInitTypeDef  TIM_TimeBaseStructure;
	
	RCC_APB1PeriphClockCmd(TIM_GENERAL_APB, ENABLE);				//TIM6时钟使能    
	
	//定时器TIM6初始化
	TIM_TimeBaseStructure.TIM_Period = arr;                     //设置在下一个更新事件装入活动的自动重装载寄存器周期的值	
	TIM_TimeBaseStructure.TIM_Prescaler =psc;                   //设置用来作为TIMx时钟频率除数的预分频值
	TIM_TimeBaseStructure.TIM_ClockDivision = TIM_CKD_DIV1;     //设置时钟分割:TDTS = Tck_tim
	TIM_TimeBaseStructure.TIM_CounterMode = TIM_CounterMode_Up; //TIM向上计数模式
	TIM_TimeBaseInit(TIM_GENERAL, &TIM_TimeBaseStructure);             //根据指定的参数初始化TIMx的时间基数单位
 
	TIM_ITConfig(TIM_GENERAL,TIM_IT_Update,ENABLE );                   //使能指定的TIM6中断,允许更新中断
	
	//TIM_Cmd(TIM6,ENABLE);//开启定时器6
	
	NVIC_InitStructure.NVIC_IRQChannel = TIM_GENERAL_IRQ;
	NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority = 2 ;//抢占优先级0
	NVIC_InitStructure.NVIC_IRQChannelSubPriority = 0;		//子优先级3
	NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;			//IRQ通道使能
	NVIC_Init(&NVIC_InitStructure);	//根据指定的参数初始化VIC寄存器
	
}

//TIMER7的初始化 用在USART3（对接SIM800）的中断接收程序/////////
//通用定时器7中断初始化
//这里选择为APB1的1倍，而APB1为24M
//arr：自动重装值。
//psc：时钟预分频数
//定时器溢出时间计算方法:Tout=((arr+1)*(psc+1))/Ft us.
//Ft=定时器工作频率,单位:Mhz 
//arr：自动重装值。
//psc：时钟预分频数		 
void TIM_Uart3_Init(u16 arr,u16 psc)
{	
	NVIC_InitTypeDef NVIC_InitStructure;
	TIM_TimeBaseInitTypeDef  TIM_TimeBaseStructure;

	RCC_APB1PeriphClockCmd(TIM_UART3_APB, ENABLE);//TIM7时钟使能    
	
	//定时器TIM7初始化
	TIM_TimeBaseStructure.TIM_Period = arr;                     //设置在下一个更新事件装入活动的自动重装载寄存器周期的值	
	TIM_TimeBaseStructure.TIM_Prescaler =psc;                   //设置用来作为TIMx时钟频率除数的预分频值
	TIM_TimeBaseStructure.TIM_ClockDivision = TIM_CKD_DIV1;     //设置时钟分割:TDTS = Tck_tim
	TIM_TimeBaseStructure.TIM_CounterMode = TIM_CounterMode_Up; //TIM向上计数模式
	TIM_TimeBaseInit(TIM_UART3, &TIM_TimeBaseStructure);             //根据指定的参数初始化TIMx的时间基数单位
 
	TIM_ITConfig(TIM_UART3,TIM_IT_Update,ENABLE );                   //使能指定的TIM7中断,允许更新中断
	
	TIM_Cmd(TIM_UART3,ENABLE);//开启定时器7
	
	NVIC_InitStructure.NVIC_IRQChannel = TIM_UART3_IRQ;
	NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority = 2 ;	//抢占优先级0
	NVIC_InitStructure.NVIC_IRQChannelSubPriority = 0;		    	//子优先级2
	NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;			      	//IRQ通道使能
	NVIC_Init(&NVIC_InitStructure);	//根据指定的参数初始化VIC寄存器
	
}

void TIM_General_Set(u16 ms)
{
	u32 arr,psc;
#if DEV_STM32F103CB
#if INTERNAL_CLOCK	
	psc = 1999;
	arr = ((8000000/1000)*ms)/(psc+1);
#else
	psc = 2399;
	arr = ((72000000/1000)*ms)/(psc+1);
#endif
#else
	psc = 29999;
	arr = ((72000000/1000)*ms)/(psc+1);
#endif
	TIM_General_Init((u16)arr, (u16)psc);
}

void TIM_Uart3_Set(u16 ms)
{
	u32 arr, psc;
#if DEV_STM32F103CB	
#if INTERNAL_CLOCK	
	psc = 1999;
	arr = ((8000000/1000)*ms)/(psc+1);
#else
	psc = 2399;
	arr = ((72000000/1000)*ms)/(psc+1);
#endif
	psc = 29999;
	arr = ((72000000/1000)*ms)/(psc+1);
#endif	
	TIM_Uart3_Init((u16)arr, (u16)psc);
}

void TIM_Ind_Set(u16 ms)
{
	u32 arr,psc;
#if DEV_STM32F103CB
#if INTERNAL_CLOCK	
	psc = 1999;
	arr = ((8000000/1000)*ms)/(psc+1);
#else
	psc = 2399;
	arr = ((72000000/1000)*ms)/(psc+1);
#endif
#else
	psc = 29999;
	arr = ((72000000/1000)*ms)/(psc+1);
#endif
	TIM_Uart2_Init((u16)arr, (u16)psc); //reuse TIM2 with Uart2
}

