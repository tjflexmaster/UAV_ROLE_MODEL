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

object Raleigh_Diner instanceof Diner {

	location:
		Telegraph_Av_2405;

	initial_beliefs:
		(current.foodcost = 4.0);
		
	initial_facts:
		(current.foodcost = 4.0);
		
}