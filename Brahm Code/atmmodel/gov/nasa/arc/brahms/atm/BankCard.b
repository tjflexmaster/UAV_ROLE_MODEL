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

class BankCard {

	display: "BankCard";
  	cost: 0.0;
	
	attributes:
		public int code;

	relations:
		public Account accesses;
		
	activities:

}