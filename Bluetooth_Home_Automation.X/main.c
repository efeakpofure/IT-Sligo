///******************************************************************************/
///* Files to Include                                                           */
///******************************************************************************/
//
//#if defined(__XC)
//    #include <xc.h>        /* XC8 General Include File */
//#elif defined(HI_TECH_C)
//    #include <htc.h>       /* HiTech General Include File */
//#elif defined(__18CXX)
//    #include <p18cxxx.h>   /* C18 General Include File */
//#endif
//
//#if defined(__XC) || defined(HI_TECH_C)
//
//#include <stdint.h>        /* For uint8_t definition */
//#include <stdbool.h>       /* For true/false definition */
//
//#endif
//
//#include "system.h"        /* System funct/params, like osc/peripheral config */
//#include "user.h"          /* User funct/params, such as InitApp */
//
///******************************************************************************/
///* User Global Variable Declaration                                           */
///******************************************************************************/
//
///* i.e. uint8_t <variable_name>; */
//
///******************************************************************************/
///* Main Program                                                               */
///******************************************************************************/
//
////void main(void)
////{
////    /* Configure the oscillator for the device */
////    ConfigureOscillator();
////
////    /* Initialize I/O and Peripherals for application */
////    InitApp();
////
////    /* TODO <INSERT USER APPLICATION CODE HERE> */
////
////    while(1)
////    {
////
////    }
////
////}
//
//
//


// CONFIG
#pragma config FOSC = HS       // Oscillator Selection bits (HS oscillator)
#pragma config WDTE = OFF       // Watchdog Timer Enable bit (WDT disabled)
#pragma config PWRTE = OFF       // Power-up Timer Enable bit (PWRT enabled)
#pragma config BOREN = OFF        // Brown-out Reset Enable bit (BOR enabled)
#pragma config LVP = OFF        // Low-Voltage (Single-Supply) In-Circuit Serial Programming Enable bit (RB3 is digital I/O, HV on MCLR must be used for programming)
#pragma config CPD = OFF        // Data EEPROM Memory Code Protection bit (Data EEPROM code protection off)
#pragma config WRT = OFF        // Flash Program Memory Write Enable bits (Write protection off; all program memory may be written to by EECON control)
#pragma config CP = OFF         // Flash Program Memory Code Protection bit (Code protection off)
//End of CONFIG registers

#define _XTAL_FREQ 20000000
#include<xc.h>

//******Initialize Bluetooth using USART********//
void Initialize_Bluetooth()
{
   //Set the pins of RX and TX//
    TRISC6=1;
    TRISC7=1;
    
  //Set the baud rate using the look up table in datasheet(pg114)//
    BRGH=1;      //Always use high speed baud rate with Bluetooth else it wont work
    SPBRG  =129;
    
    //Turn on Asyc. Serial Port//
    SYNC=0;
    SPEN=1;
    
    //Set 8-bit reception and transmission
    RX9=0;
    TX9=0;

   //Enable transmission and reception//
    TXEN=1; 
    CREN=1; 
    
    //Enable global and ph. interrupts//
    GIE = 1;
    PEIE= 1;
  
    //Enable interrupts for Tx. and Rx.//
    RCIE=1;
    TXIE=1;
}
//___________BT initialized_____________//

//Function to load the Bluetooth Rx. buffer with one char.//
void BT_load_char(char byte)  
{
    TXREG = byte;
    while(!TXIF);  
    while(!TRMT);
}
//End of function//

//Function to Load Bluetooth Rx. buffer with string//
void BT_load_string(char* string)
{
    while(*string)
    BT_load_char(*string++);
}
//End of function//

//Function to broadcast data from RX. buffer//
void broadcast_BT()
{
  TXREG = 13;  
  __delay_ms(500);
}
//End of function//

//Function to get a char from Rx.buffer of BT//
char BT_get_char(void)   
{
    if(OERR) // check for over run error 
    {
        CREN = 0;
        CREN = 1; //Reset CREN
    }
    
    if(RCIF==1) //if the user has sent a char return the char (ASCII value)
    {
    while(!RCIF);  
    return RCREG;
    }
    else //if user has sent no message return 0
        return 0;
}
//End of function/

void main(void)
{
    //Scope variable declarations//
    int get_value;
    //End of variable declaration//
   
    //I/O Declarations//
    TRISB3=0;
    //End of I/O declaration//
   
   Initialize_Bluetooth(); //lets get our bluetooth ready for action
    
   //Show some introductory message once on power up//
   BT_load_string("Bluetooth Initialized and Ready");
   broadcast_BT();
   BT_load_string("Press 1 to turn ON LED");
   broadcast_BT();
   BT_load_string("Press 0 to turn OFF LED");
   broadcast_BT();
   //End of message//
    
    while(1) //The infinite lop
    {   
        
    get_value = BT_get_char(); //Read the char. received via BT
    
    //If we receive a '0'//
        if (get_value=='0')
          {
             RB3=0; 
             BT_load_string("LED turned OFF");
             broadcast_BT();
          }
       
    //If we receive a '1'//   
        if (get_value=='1')
          {
             RB3=1;
             BT_load_string("LED turned ON");
             broadcast_BT();
          }      
    }
}
