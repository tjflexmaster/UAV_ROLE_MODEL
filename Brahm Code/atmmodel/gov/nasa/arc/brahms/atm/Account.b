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

class Account {
	display: "Account";
  	cost: 0.0;
 	resource: true;

	attributes:
		public double balance;
		public string typeof;
		public int code;
		public int pin;

	relations:
		public Bank openedWithBank;
		
	activities:

}
