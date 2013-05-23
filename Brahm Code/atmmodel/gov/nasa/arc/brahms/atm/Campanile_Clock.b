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

object Campanile_Clock instanceof MyClock  {

//	location: SouthHall; 
//	no location has been added, so that the Campanile can broadcast to all the agents, wherever they are.


	initial_beliefs:
		(current.time = 1);

	initial_facts:
		(current.time = 1);



}