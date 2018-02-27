#include "usart2.h"
#include "usart.h"
#include "stdarg.h"	 	 
#include "stdio.h"	 	 
#include "string.h"	 
#include "timer.h"
#include "delay.h"
#include "YR_4G.h"
   
//���ڽ��ջ����� 	
u8 USART2_RX_BUF[USART2_MAX_RECV_LEN]; 				//���ջ���,���USART2_MAX_RECV_LEN���ֽ�.
u8 USART2_TX_BUF[USART2_MAX_SEND_LEN]; 			  //���ͻ���,���USART2_MAX_SEND_LEN�ֽ�
//ɨ�����ڣ� timer=10ms
vu16 USART2_RX_STA=0;   	

void USART2_IRQHandler(void)
{
	u8 res;	      
	if(USART_GetITStatus(USART2, USART_IT_RXNE) != RESET)//���յ�����
	{	 
		res = USART_ReceiveData(USART2);		 
		if((USART2_RX_STA&(1<<15)) == 0)//�������һ������,��û�б�����,���ٽ�����������
		{ 
			if(USART2_RX_STA<USART2_MAX_RECV_LEN)	//�����Խ�������
			{
				TIM_SetCounter(TIM_UART2,0);             //���������          				
				if(USART2_RX_STA==0) 				        //ʹ�ܶ�ʱ��5���ж� 
				{
					TIM_Cmd(TIM_UART2,ENABLE);             //ʹ�ܶ�ʱ��5
				}
				USART2_RX_BUF[USART2_RX_STA++]=res;	//��¼���յ���ֵ	 
			}else 
			{
				USART2_RX_STA|=1<<15;				//ǿ�Ʊ�ǽ������
			} 
		}		
	}  				 											 
}   


//��ʼ��IO ����2
//pclk1:PCLK1ʱ��Ƶ��(Mhz)
//bound:������	  
void usart2_init(u32 bound)
{  
	NVIC_InitTypeDef NVIC_InitStructure;
	GPIO_InitTypeDef GPIO_InitStructure;
	USART_InitTypeDef USART_InitStructure;

	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOA, ENABLE);	                       //GPIOAʱ��
	RCC_APB1PeriphClockCmd(RCC_APB1Periph_USART2,ENABLE);                          //����2ʱ��ʹ��

 	USART_DeInit(USART2);  //��λ����2
	 //USART2_TX   PA2
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_2;                                     //PA2
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AF_PP;	                               //�����������
	GPIO_Init(GPIOA, &GPIO_InitStructure);                                         //��ʼ��PA2
 
	//USART2_RX	  PA3
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_3;
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IN_FLOATING;                          //��������
	GPIO_Init(GPIOA, &GPIO_InitStructure);                                         //��ʼ��PA3
	
	USART_InitStructure.USART_BaudRate = bound;                                    //������һ������Ϊ9600;
	USART_InitStructure.USART_WordLength = USART_WordLength_8b;                    //�ֳ�Ϊ8λ���ݸ�ʽ
	USART_InitStructure.USART_StopBits = USART_StopBits_1;                         //һ��ֹͣλ
	USART_InitStructure.USART_Parity = USART_Parity_No;                            //����żУ��λ
	USART_InitStructure.USART_HardwareFlowControl = USART_HardwareFlowControl_None;//��Ӳ������������
	USART_InitStructure.USART_Mode = USART_Mode_Rx | USART_Mode_Tx;	               //�շ�ģʽ
  
	USART_Init(USART2, &USART_InitStructure);       //��ʼ������2

	USART_Cmd(USART2, ENABLE);                      //ʹ�ܴ��� 
	
	//ʹ�ܽ����ж�
	USART_ITConfig(USART2, USART_IT_RXNE, ENABLE);  //�����ж�   
	
	//�����ж����ȼ�
	NVIC_InitStructure.NVIC_IRQChannel = USART2_IRQn;
	NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority = 0 ;//��ռ���ȼ�0
	NVIC_InitStructure.NVIC_IRQChannelSubPriority = 0;		//�����ȼ�0
	NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;			//IRQͨ��ʹ��
	NVIC_Init(&NVIC_InitStructure);	//����ָ���Ĳ�����ʼ��VIC�Ĵ���
	
	
	TIM_Uart2_Init(299,2399);		//10ms�ж�
	USART2_RX_STA=0;		    	//����
	TIM_Cmd(TIM_UART2,DISABLE);		//�رն�ʱ��7
}

//����2,printf ����
//ȷ��һ�η������ݲ�����USART2_MAX_SEND_LEN�ֽ�
void u2_printf(char* fmt,...)  
{  
	u16 i,j; 
	va_list ap; 
	va_start(ap,fmt);
	memset(USART2_TX_BUF, 0, USART2_MAX_SEND_LEN);	
	vsprintf((char*)USART2_TX_BUF,fmt,ap);
	va_end(ap);
	i=strlen((const char*)USART2_TX_BUF);		//�˴η������ݵĳ���
	BSP_Printf("S2: %s\r\n", USART2_TX_BUF);
	for(j=0;j<i;j++)							//ѭ����������
	{
	  while(USART_GetFlagStatus(USART2,USART_FLAG_TC)==RESET); //ѭ������,ֱ���������   
		USART_SendData(USART2,USART2_TX_BUF[j]); 
	}
}

void u2_msg(u8 *msg, u8 len)  
{
	BSP_Printf("u2_msg: ");        
	for(u8 i=0;i<len;i++)							//ѭ����������
	{
		while(USART_GetFlagStatus(USART2,USART_FLAG_TC)==RESET); //ѭ������,ֱ���������   
			USART_SendData(USART2,msg[i]);
		BSP_Printf("0x%02x ", msg[i]);
	}
	BSP_Printf("\n");
}

void Clear_Usart2(void)
{
	memset(USART2_RX_BUF,0,USART2_MAX_RECV_LEN);	
	USART2_RX_STA = 0;		    	//����
}
