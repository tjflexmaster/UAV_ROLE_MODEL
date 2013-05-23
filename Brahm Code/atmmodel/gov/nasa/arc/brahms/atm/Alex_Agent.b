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

agent Alex_Agent memberof Student {

	location: SouthHall;

	initial_beliefs:
		(current.howHungry = 15.00);
		(current.male = true);
		(current.preferredCashOut = 8.0);
		(current contains Alex_Cash);
		(current contains Alex_BankCard);
		(Alex_Account.balance = 100.00);
		(Alex_Account.code = 1212 );
		(Alex_Account.pin = 1111);
		(Alex_Account openedWithBank Boa_Bank);
		(Alex_Cash.amount = 13.00);
		(current hasCash Alex_Cash);
		(current hasBankCard Alex_BankCard);

	initial_facts:
		(current.male = true);
		(current.preferredCashOut = 8.0);
}
