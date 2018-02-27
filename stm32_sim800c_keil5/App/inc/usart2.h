#ifndef __USART2_H
#define __USART2_H	 
#include "sys.h"  
   

#define USART2_MAX_RECV_LEN		256					//�����ջ����ֽ���
#define USART2_MAX_SEND_LEN		256					//����ͻ����ֽ���
#define USART2_RX_EN 			1					      //0,������;1,����.

extern u8  USART2_RX_BUF[USART2_MAX_RECV_LEN]; 		//���ջ���,���USART2_MAX_RECV_LEN�ֽ�
extern u8  USART2_TX_BUF[USART2_MAX_SEND_LEN]; 		//���ͻ���,���USART2_MAX_SEND_LEN�ֽ�
extern vu16 USART2_RX_STA;   						//��������״̬

void usart2_init(u32 bound);				//����2��ʼ�� 
void u2_printf(char* fmt,...);
void u2_msg(u8 *msg, u8 len);
void Clear_Usart2(void);
#endif

