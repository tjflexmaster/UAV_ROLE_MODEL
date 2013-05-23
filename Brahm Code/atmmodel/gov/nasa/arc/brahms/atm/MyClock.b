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



class MyClock  {

	attributes:
		public int time;

	
	activities:

			primitive_activity asTimeGoesBy() {
						random: false;
						max_duration: 3599;
			}
	

			broadcast announceTime() {
						random: false;
						max_duration: 1;

						about: 
							send(current.time = current.time);

						when: end;	 
			}
			
	workframes:

			
			workframe wf_asTimeGoesBy {
					repeat: true;
				            
					when(knownval(current.time < 20))	
					//THE MODEL HALTS AFTER 20 HOURS - THE END OF A VERY LONG WORKING DAY!
					
						
					do {
						asTimeGoesBy();
						conclude((current.time = current.time + 1), bc:100, fc:100);
						announceTime();
					}
			}	

			
			
}