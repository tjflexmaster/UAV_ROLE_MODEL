/********************************************************************
 *                   NASA CONFIDENTIAL INFORMATION                  *
 *               (c)1997-2003 Nasa Ames Research Center             *
 *                        All Rights Reserved                       *
 *                                                                  *
 *  This program contains confidential and proprietary information  *
 *  of NASA Ames Research Center, any reproduction, disclosure,     *
 *  or use in whole or in part is expressly prohibited, except as   *
 *  may be specifically authorized by prior written agreement.      *
 *                                                                  *
 ********************************************************************/
package gov.nasa.arc.brahms.atm;

object Alex_Account instanceof Account {
	display: "Alex_Account";
  
	initial_facts:
		(current.balance = 100.00);
		(current.typeof = checking);
		(current.code = 1212);
		(current.pin = 1111);
		(current openedWithBank Boa_Bank);

}