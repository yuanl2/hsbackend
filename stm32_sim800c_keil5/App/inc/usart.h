#ifndef __USART_H
#define __USART_H
#include "stdio.h"	
#include "sys.h" 

//#define LOG_ENABLE
#ifdef LOG_ENABLE
#define BSP_Printf		printf
#else
#define BSP_Printf(...)
#endif

#define SW_VERSION            "01"

#define USART_REC_LEN  			200  
#define EN_USART1_RX 			   0
	  	
extern u8  USART_RX_BUF[USART_REC_LEN]; 
extern u16 USART_RX_STA; 
void usart1_init(u32 bound);
#endif


