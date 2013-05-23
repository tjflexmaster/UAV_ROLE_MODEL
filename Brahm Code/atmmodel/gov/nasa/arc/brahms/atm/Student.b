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

/*
ATM Model by Alessandro Acquisti acquisti@riacs.edu


SECTION 4.7 VERSION


THE STORY:
This simulation models the simple life of laborious Berkeley students: studying, eating, and getting just enough cash to afford some staple. 
Students spend most of their time studying, but get hungrier as the time goes by. When they are particularly hungry (the level varying depending on the student), they decide to move to one of the two restaurants in town. Students choose the restaurant according to how much money they are carrying. If a student does not have enough money for the cheapest restaurant, she will decide to pass first by the ATM of the bank where she has her account.  

When she arrives at the ATM, the student inserts her bank card and attempt to remember the PIN associated to her account. The ATM gives students 3 attempts to digit the correct PIN, before refusing the card altogether. The ATM communicates with the central bank computer to verify the correctness of the information provided by the student. If the bank computer communicates to the ATM that the PIN is correct and that the student has enough balance in her account, the ATM will dispense the cash and
will print an invoice with the account number and the remaining balance.

Students need to have enough balance in their  accounts to take out
cash: if they attempt to take out more money than they have, the bank computer
will notify the students (through the ATM) of the remaining amount of dollars in the
account. The student will modify her habits accordingly, and take out just exactly the
remaining dollars. 


THE CAST (AGENTS and OBJECTS):
Students: Kim, Alex
Bank computers: Bank of America, Wells Fargo
Restaurants: Blakes, Raleighs
Studying Places: South Hall, Spraul Hall
Clock: the Campanile
ATMs: one ATM for each bank


NOTES:
- attributes and relations scopes are all set to public. private and protected scopes are admitted but not yet implemented in the current version of the language
- the simulation halts at the end of a Berkeley's student hard work day: 20 hours (sleep is not considered!)
- except in the FINAL version/section, after taking money from the ATMs, students pass by the Hall before getting to the Restaurants
- objects are not given wait activities since detactables/facts do the job (see Tutorial text)


SUGGESTED CHANGES FOR FURTHER MODELING:
- start using private/public/protected scope (see above)
- change ATM and BANK to agents


FURTHER COMPLICATIONS:
- what if each bank has more than one ATM? can you make the agent choose the closest one?
- can  you model the objects (say, Banks and ATMs) as agents? what will change? do you notice changes in the behavior related to detectables and communications?
- what if more students go to the same ATM at the same time? can you make the ATM handle just one order at a time, but the Bank central computer multiple orders coming from several ATMs?
- can you make the agents randomly meet and interact (e.g., say hello each to the other)?
- what if you want the ATM to remember the number of the cards if has refused?


VERSION HISTORY
//VERSION 0.1, February 2001
//VERSION 0.9, April 2001
//VERSION 0.91 April 30, 2001
//VERSION 0.99 August 8, 2001
//VERSION 0.999 December 8, 2001

*/



group Student {

	attributes:

		public boolean male;
		public double howHungry;
		public double preferredCashOut;
		public int perceivedtime;
		public boolean readyToLeaveRestaurant;

		
	relations:
		public Account hasAccount;
		public Cash hasCash;
		public BankCard hasBankCard;

	//attributes and relations scopes are all set to public. private and protected scopes are admitted but not yet implemented in the current version of the language
	
	initial_beliefs:

		(Blakes_Diner.location = Telegraph_Av_2134);
		(Raleigh_Diner.location = Telegraph_Av_2405);
		(Boa_Atm.location = Telegraph_Av_113);
		(Raleigh_Diner.foodcost = 4.0);
		(current.perceivedtime = 1);
		(Campanile_Clock.time  = 1);
		(current.readyToLeaveRestaurant = false);

	initial_facts:
				
	activities:

			primitive_activity study() {
				max_duration: 1500;

			}	
		
			move moveToRestaurant() {
					

					location: Telegraph_Av_2405;
			}

			move moveToSouthHall() {
					
					location: SouthHall;
			}

			move moveToLocationForCash(Building loc) {
					location: loc;
			}

			
			move moveToLocation(Building loc) {
					location: loc;
			}
	
			
			primitive_activity eat() {
					max_duration: 400;

			}


	workframes:


		/* THIS WORKFRAME IS MODIFIED (AND HENCE COMMENTED OUT) IN SECTION 4.7

		workframe wf_moveToRestaurant {
				
				repeat: true;
		
				
				variables:


				when
					(knownval(current.howHungry > 20.00) and
					knownval(current.location != Telegraph_Av_2405))
				do
					{moveToRestaurant();
					conclude((current.howHungry = current.howHungry - 5.00), bc:100, fc:0);
					conclude((Alex_Cash.amount = Alex_Cash.amount - Raleigh_Diner.foodcost), bc:100, fc:100);
					moveToSouthHall();
				}
		}
		*/


				workframe wf_moveToRestaurant {
					repeat: true;

					
					when(knownval(current.howHungry > 20.00) and
						knownval(current.location != Telegraph_Av_2405))
				
						
					do {
						moveToLocation(Telegraph_Av_2405);	
					}
				}
		
				workframe wf_eat {

					repeat: true;

				
						
					when(knownval(current.howHungry > 20.00) and
						knownval(current.location = Telegraph_Av_2405))
										
					do {
						
						eat();
						conclude((current.howHungry = current.howHungry - 5.00), bc:100, fc:0);
						conclude((Alex_Cash.amount = Alex_Cash.amount - Raleigh_Diner.foodcost), bc:100, fc:100);
						conclude((current.readyToLeaveRestaurant = true), bc:100, fc:0);
					}


				}
			
				
				workframe wf_backToStudy {
					repeat: true;

					when(knownval(current.readyToLeaveRestaurant = true) and
						knownval(current.location = Telegraph_Av_2405))

						do {
							moveToLocation(SouthHall);
							conclude((current.readyToLeaveRestaurant = false), bc:100, fc:0);

						} 

				}
		
		/* THIS CORRECTED AFTER SECTION 4.7
		workframe wf_moveToLocationForCash {
					repeat: true;
				
					variables:
					
					
					when(knownval(Alex_Cash.amount < 10.00)) 
						
					do {
						moveToLocationForCash(Telegraph_Av_113);
						//MoveToLocation(Bancroft_Av_77);
						//before, it was: MoveToLocation(bankofamerica etc. without this generalization: an easier case...)
						conclude((Alex_Cash.amount = Alex_Cash.amount + current.preferredCashOut), bc:100, fc:100); //
						moveToLocationForCash(SouthHall);
					}
				}
		*/

		workframe wf_moveToLocationForCash {
					repeat: true;
				
					variables:
					
					
					when(knownval(Alex_Cash.amount < 10.00)) 
						
					do {
						moveToLocation(Telegraph_Av_113);
						conclude((Alex_Cash.amount = Alex_Cash.amount + current.preferredCashOut), bc:100, fc:100); //THIS IS A TEMPORARY SOLUTION - IT DOES NOT USE VARIABLES!
						moveToLocation(SouthHall);

						//NOTE: WE MAKE THE STUDENTS GO BACK TO A UNIVERSITY HALL AFTER GETTING THE MONEY AND BEFORE SPENDING THE MONEY AT SOME RESTAURANT		

					}
				}

			workframe wf_study {
					repeat: true;

	     
					when(knownval(Campanile_Clock.time < 20) and
						knownval(current.howHungry < 21) and
				knownval(current.location = SouthHall))	
						
						do { 
							
							study();
							
						}
			}



	thoughtframes:

			thoughtframe tf_feelHungry {
			
			
					repeat: true;
					variables:
						foreach(MyClock) campanile;
						foreach(int) iTime;
						forone(int) iHungry;
							
					when(
						knownval(campanile.time = iTime) and
						knownval(current.perceivedtime < iTime) and
						knownval(current.howHungry = iHungry)
						)

					do {
						conclude((current.perceivedtime = iTime), bc: 100);
						conclude((current.howHungry = iHungry + 3.00), bc:100);

					}

				}


}



