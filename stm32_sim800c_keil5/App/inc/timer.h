#ifndef _TIMER_H
#define _TIMER_H
#include "sys.h"
	
//用来设置心跳包的发送间隔（单位是分钟）
//常规的心跳包间隔是1分钟到10分钟
#define TIME_HEART 1
#define TIME_REPLY 1
#define HB_1_MIN (TIME_HEART*60-1)  	  //1MIN
#define REPLY_1_MIN (TIME_REPLY*60-1)  	  //1MIN

#define NUMBER_TIMER_1_MINUTE       60
#define NUMBER_MSG_MAX_RETRY        5

#define TIMEVAL_MAX       (23*3600+59*60+59)
#define DISCONNECT_TIMEOUT     (60*5)

#if DEV_STM32F103CB
#define TIM_UART2   TIM2
#define TIM_GENERAL TIM3
#define TIM_UART3   TIM4

#define TIM_UART2_IRQ   TIM2_IRQn
#define TIM_GENERAL_IRQ TIM3_IRQn
#define TIM_UART3_IRQ   TIM4_IRQn

#define TIM_UART2_APB   RCC_APB1Periph_TIM2
#define TIM_GENERAL_APB   RCC_APB1Periph_TIM3
#define TIM_UART3_APB   RCC_APB1Periph_TIM4
#else
#define TIM_UART2   TIM5
#define TIM_GENERAL TIM6
#define TIM_UART3   TIM7

#define TIM_UART2_IRQ   TIM5_IRQn
#define TIM_GENERAL_IRQ TIM6_IRQn
#define TIM_UART3_IRQ   TIM7_IRQn

#define TIM_UART2_APB   RCC_APB1Periph_TIM5
#define TIM_GENERAL_APB   RCC_APB1Periph_TIM6
#define TIM_UART3_APB   RCC_APB1Periph_TIM7
#endif

void TIM_Uart2_Init(u16 arr,u16 psc);
void TIM_General_Init(u16 arr,u16 psc);
void TIM_Uart3_Init(u16 arr,u16 psc);
void TIM_General_Set(u16 ms);
void TIM_Uart3_Set(u16 ms);
void TIM_Ind_Set(u16 ms);
#endif
