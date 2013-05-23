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

*/




area AtmGeography instanceof World { }

areadef University extends BaseAreaDef { }

areadef UniversityHall extends Building { }

areadef BankBranch extends Building { }

areadef Restaurant extends Building { }


// Berkeley
area Berkeley instanceof City partof AtmGeography { }

// inside Berkeley

area UCB instanceof University partof Berkeley { }

area SouthHall instanceof UniversityHall partof UCB { }

area Telegraph_Av_113 instanceof BankBranch partof Berkeley { }

area SpraulHall instanceof UniversityHall partof UCB { }

area Bancroft_Av_77 instanceof BankBranch partof Berkeley { }

area Telegraph_Av_2405 instanceof Restaurant partof Berkeley { }

area Telegraph_Av_2134 instanceof Restaurant partof Berkeley { }


//paths to and from banks from spraul and south halls

path StH_to_from_BOA {
	area1: SouthHall;
	area2: Telegraph_Av_113;
	distance: 240;
}
path SpH_to_from_BOA {
	area1: SpraulHall;
	area2: Telegraph_Av_113;
	distance: 240;
}

path StH_to_from_WF {
	area1: SouthHall;
	area2: Bancroft_Av_77;
	distance: 200;
}
path SpH_to_from_WF {
	area1: SpraulHall;
	area2: Bancroft_Av_77;
	distance: 200;
}


//paths to and from restaurants from and to spraul and south halls

path StH_to_from_BB {
	area1: SouthHall;
	area2: Telegraph_Av_2134;
	distance: 360;
}
path SpH_to_from_BB {
	area1: SpraulHall;
	area2: Telegraph_Av_2134;
	distance: 280;
}
path StH_to_from_RB {
	area1: SouthHall;
	area2: Telegraph_Av_2405;
	distance: 400;
}
path SpH_to_from_RB {
	area1: SpraulHall;
	area2: Telegraph_Av_2405;
	distance: 320;
}


//paths to and from restaurants and banks

path BOA_to_from_BB {
	area1: Telegraph_Av_113;
	area2: Telegraph_Av_2134;
	distance: 60;
}
path WF_to_from_BB {
	area1: Bancroft_Av_77;
	area2: Telegraph_Av_2134;
	distance: 80;
}
path BOA_to_from_RB {
	area1: Telegraph_Av_113;
	area2: Telegraph_Av_2405;
	distance: 80;
}
path WF_to_from_RB {
	area1: Bancroft_Av_77;
	area2: Telegraph_Av_2405;
	distance: 100;
}

