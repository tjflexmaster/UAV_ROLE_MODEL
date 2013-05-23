areadef House extends BaseAreaDef { }

areadef Room extends House { }

areadef areaOfInterest extends Room { }

area Geography instanceof World { }

area AlexHouse instanceof House { }

area LivingRoom instanceof Room partof AlexHouse { }

area BedRoom instanceof Room partof AlexHouse { }

area Kitchen instanceof Room partof AlexHouse { }

area BathRoom instanceof Room partof AlexHouse { }

area chair instanceof areaOfInterest partof LivingRoom { }

area frontDoor instanceof areaOfInterest partof LivingRoom { }

area medCabinet instanceof areaOfInterest partof LivingRoom { }

area sinkOne instanceof areaOfInterest partof Kitchen { }

area dishWasher instanceof areaOfInterest partof Kitchen { }

area microWave instanceof areaOfInterest partof Kitchen { }

area medicationBox instanceof areaOfInterest partof LivingRoom { }

area bed instanceof areaOfInterest partof BedRoom { }

area toilet instanceof areaOfInterest partof BathRoom { }

area sinkTwo instanceof areaOfInterest partof BathRoom { }

area careCentre instanceof House {}


path chair_to_from_medCabinet {
	area1: chair;
	area2: medCabinet;
	distance: 10;
}

path chair_to_from_sinkOne {
	area1: chair;
	area2: sinkOne;
	distance: 10;
}


path chair_to_from_bed {
	area1: chair;
	area2: bed;
	distance: 15;
}


path chair_to_from_toilet {
	area1: chair;
	area2: toilet;
	distance: 5;
}


path chair_to_from_frontDoor {
	area1: chair;
	area2: frontDoor;
	distance: 5;
}


path toilet_to_from_sinkTwo {
	area1: toilet;
	area2: sinkTwo;
	distance: 2;
}

path sinkOne_to_from_dishWasher {
	area1: sinkOne;
	area2: dishWasher;
	distance: 2;
}

path dishWasher_to_from_microWave {
	area1: dishWasher;
	area2: microWave;
	distance: 2;
}

path careCentre_to_from_frontDoor {
	area1: careCentre;
	area2: frontDoor;
	distance: 2000;
}

group person{

}

agent Alex memberof person {
	location: chair;
	attributes:
		public int perceivedtime;
		public int howHungry;
		public int needToilet;
		public int waitingForFood;
		public boolean askedFood;
		public boolean updateAskedFood;
		public boolean hasFood;
		public boolean handsWashed;
		public boolean hasEmptyPlate;
		public boolean hasMedicationA;
		public boolean takeMedicationA;
		public boolean hasTakenMedicationA;
		public boolean toiletFlushed;
		public boolean evacuate;
		public boolean fireDecision;
		public boolean danger;
		public int missedMedicationA;
		public int timeSinceAskedFood;
	initial_beliefs:
		(current.timeSinceAskedFood = 0);
		(current.howHungry = 15);
		(current.fireDecision = false);
		(current.toiletFlushed = true);
		(current.handsWashed = true);
		(current.needToilet = 1);
		(current.askedFood = false);
		(current.waitingForFood = 0);
		(current.updateAskedFood = false);
		(current.perceivedtime = 1);
		(Campanile_Clock.time = 1);
		(current.hasFood = false);
		(current.takeMedicationA = false);
		(current.hasMedicationA = false);
		(current.hasTakenMedicationA = false);
		(current.hasEmptyPlate = false);
		(theEnvironment.fire = false);
		(current.evacuate = false);
	initial_facts:

	activities:
		primitive_activity eat() {
			max_duration: 1000;
		}

		primitive_activity watchTV() {
			max_duration: 2000;
		}
		
		primitive_activity takeMedication() {
			max_duration: 100;
		}
		
		communicate askForFood(){
			max_duration: 1;
			with: robotHelper;
			about:
				send(current.askedFood = current.askedFood);
		}

		primitive_activity wait(){
			max_duration: 100;
		}

		primitive_activity goToilet(){
			max_duration: 400;
		}

		primitive_activity flushToilet(){
			max_duration: 3;
		}
		
		primitive_activity washHands(){
			max_duration: 100;
		}

		move moveToChair() {
			 location: chair;
		}

		move moveToMeds() {
			 location: medCabinet;
		}
		
		move moveToBed() {
			 location: bed;
		}
		
		move moveToSinkOne() {
			 location: sinkOne;
		}
		
		move moveToDishWasher() {
			 location: dishWasher;
		}
		
		move moveToMicroWave() {
			 location: microWave;
		}
		
		move moveToToilet() {
			 location: toilet;
		}
		
		move moveToSinkTwo() {
			 location: sinkTwo;
		}
		
		move moveToFrontDoor() {
			 location: frontDoor;
		}


	workframes:

			workframe wf_watchTV {
					repeat: true;
					priority: 1;

					detectables:
						detectable stop{
							when(whenever)
								detect((current.perceivedtime = 10), dc:100)
								then abort;
						}
						
					when(knownval(current.perceivedtime < 10) and 
						knownval(current.location = chair))
					do {
						watchTV();
					}
			}
			
			workframe wf_evacuate {
					repeat: true;
					priority: 1000;
					
					when(knownval(current.evacuate = true))
					do {
						moveToFrontDoor();
						conclude((current.evacuate = false));
						conclude((current.perceivedtime = 10));
					}
			}
			

			workframe wf_goToilet {
					repeat: true;
					priority: 4;
					detectables:
						detectable stop{
							when(whenever)
								detect((current.perceivedtime = 10), dc:100)
								then abort;
						}
					when(knownval(current.perceivedtime < 10) and 
						knownval(current.needToilet > 5))
					do {
						moveToToilet();
						goToilet();
						conclude((current.needToilet = 0));
						conclude((current.handsWashed = false));
						conclude((current.toiletFlushed = false));
					}
			}

			workframe wf_flushToilet {
					repeat: true;
					priority: 7;
					detectables:
						detectable stop{
							when(whenever)
								detect((current.perceivedtime = 10), dc:100)
								then abort;
						}	
					when(knownval(current.perceivedtime < 10) and 
						knownval(current.location = toilet) and
						knownval(current.toiletFlushed = false))
					do {
						flushToilet();
						conclude((current.toiletFlushed = true));
					}
			}

			workframe wf_flushToiletTwo {
					repeat: true;
					priority: 3;
					detectables:
						detectable stop{
							when(whenever)
								detect((current.perceivedtime = 10), dc:100)
								then abort;
						}
					when(knownval(current.perceivedtime < 10) and 
						knownval(current.location != toilet) and
						knownval(current.toiletFlushed = false))
					do {
						moveToToilet();
						flushToilet();
						conclude((current.toiletFlushed = true));
					}
			}

			workframe wf_forgetFlushToilet {
					repeat: true;
					priority: 7;
					detectables:
						detectable stop{
							when(whenever)
								detect((current.perceivedtime = 10), dc:100)
								then abort;
						}
					when(knownval(current.perceivedtime < 10) and 
						knownval(current.location = toilet) and
						knownval(current.toiletFlushed = false))
					do {
						conclude((current.toiletFlushed = true), fc:0);
					}
			}

			workframe wf_washHands {
					repeat: true;
					priority: 6;
					detectables:
						detectable stop{
							when(whenever)
								detect((current.perceivedtime = 10), dc:100)
								then abort;
						}
					when(knownval(current.perceivedtime < 10) and 
						knownval(current.handsWashed = false))
					do {
						moveToSinkTwo();
						washHands();
						conclude((current.handsWashed = true));
						moveToChair();
					}
			}
			

			workframe wf_askForFood {
					repeat: true;
					priority: 9;
					detectables:
						detectable stop{
							when(whenever)
								detect((current.perceivedtime = 10), dc:100)
								then abort;
						}
					when(knownval(current.howHungry > 10) and
						knownval(current.perceivedtime < 10) and
						knownval(current.askedFood = false) and
						knownval(current.hasFood = false))	
					do {
						conclude((current.askedFood = true));
						askForFood();
						conclude((current.waitingForFood = 0));
					}
			}

			workframe wf_takeMedicationA {
					repeat: true;
					priority: 10;
					detectables:
						detectable stop{
							when(whenever)
								detect((current.perceivedtime = 10), dc:100)
								then abort;
						}
					when(knownval(current.takeMedicationA = true) and
						knownval(current.perceivedtime < 10) and
						knownval(current.location = chair))	
					do {
						takeMedication();
						conclude((current.takeMedicationA = false));
						conclude((current.hasMedicationA = false));
						conclude((current.hasTakenMedicationA = true));
					}
			}

			workframe wf_DontTakeMedicationA {
					repeat: true;
					priority: 10;
					detectables:
						detectable stop{
							when(whenever)
								detect((current.perceivedtime = 10), dc:100)
								then abort;
						}
					when(knownval(current.takeMedicationA = true) and
						knownval(current.perceivedtime < 10) and
						knownval(current.location = chair))	
					do {
						takeMedication();
						conclude((current.takeMedicationA = false));
					}
			}
	
			workframe wf_eat {
					repeat: true;
					priority: 11;
					detectables:
						detectable stop{
							when(whenever)
								detect((current.perceivedtime = 10), dc:100)
								then abort;
						}
					when(knownval(current.howHungry > 10) and
						knownval(current.perceivedtime < 10) and
						knownval(current.askedFood = true) and
						knownval(current.hasFood = true) and
						knownval(current.location = chair))	
					do {
						eat();
						conclude((current.askedFood = false));
						conclude((current.howHungry = 0));
						conclude((current.waitingForFood = 0));
						conclude((current.hasFood = false));
						conclude((current.hasEmptyPlate = true));
						conclude((current.timeSinceAskedFood = 0));
					}
			}
			
		workframe wf_atToilet{
			repeat: true;
					detectables:
						detectable stop{
							when(whenever)
								detect((current.perceivedtime = 10), dc:100)
								then abort;
						}
			when(knownval(current.perceivedtime < 10) and
				knownval(current.location = toilet) and
				knownval(current.handsWashed = true) and
				knownval(current.toiletFlushed = true) and
				knownval(current.needToilet < 5))
			do{
				moveToChair();
			}
		}

	thoughtframes:
	
				thoughtframe tf_durationAskedForFood {
					repeat: true;
					priority: 2;

					when(knownval(Campanile_Clock.time > current.perceivedtime) and
						knownval(current.askedFood = true) and
						knownval(current.updateAskedFood = true))

					do {
						conclude((current.waitingForFood = current.waitingForFood + 1), bc:100);
						conclude((current.updateAskedFood = false));
					}
				}
	
				thoughtframe tf_asTimeGoesBy {
					repeat: true;
					priority: 1;
					
					when(knownval(Campanile_Clock.time > current.perceivedtime) and
						knownval(current.howHungry < 21 ))

					do {
						conclude((current.perceivedtime = Campanile_Clock.time), bc: 100);
						conclude((current.howHungry = current.howHungry + 3));
						conclude((current.updateAskedFood = true));
						conclude((current.needToilet = current.needToilet +1));
					}
				}
				
				thoughtframe tf_timeSinceAskedFood {
					repeat: true;
					
					when(knownval(current.howHungry > 10 ) and 
						knownval(Campanile_Clock.time > current.perceivedtime))

					do {
						conclude((current.perceivedtime = Campanile_Clock.time), bc: 100);
						conclude((current.howHungry = current.howHungry + 3));
						conclude((current.updateAskedFood = true));
						conclude((current.needToilet = current.needToilet +1));
						conclude((current.timeSinceAskedFood = current.timeSinceAskedFood+1));
					}
				}

				thoughtframe tf_takeMedicationA {
					repeat: true;
					priority: 3;
					
					when(knownval(current.perceivedtime < 10) and
						knownval(current.hasMedicationA = true) and
						knownval(current.takeMedicationA = false) and
						knownval(current.location = chair))

					do {
						conclude((current.takeMedicationA = true));
						conclude((current.hasMedicationA = false));
					}
				}

				thoughtframe tf_fireAlarm {
					repeat: true;
					priority: 99;
					
					when(knownval(theEnvironment.fire = true) and 
						knownval(current.fireDecision = false))

					do {
						conclude((current.evacuate = true),bc:50);
						conclude((current.fireDecision = true));
					}
				}


}
agent robotHelper{
	location: chair;
	attributes:
		public int perceivedtime;
		public int fireAlerted;
		public boolean checkMedicationA;
		public boolean medNotificationA;
		public int timeSinceMedANotification;

	initial_beliefs:
		(current.timeSinceMedANotification = 0);
		(Alex.location = chair);
		(robotHouse.doorBellRang = false);
		(careWorker.location = careCentre);
		(Alex.hasEmptyPlate = false);
		(Alex.askedFood = false);
		(Alex.hasMedicationA = false);
		(Alex.missedMedicationA = 0);
		(Alex.takeMedicationA = false);
		(current.perceivedtime = 1);
		(current.fireAlerted = 0);
		(theEnvironment.fire = false);
		(Alex.evacuate = false);
		(Alex.danger = false);
		(current.medNotificationA = false);
		(Alex.hasTakenMedicationA = false);
	initial_facts:

	activities:

		primitive_activity getFood(){
			max_duration: 2;
		}

		primitive_activity cookFood(){
			max_duration: 1200;
		}

		primitive_activity placeFoodOnTray(){
			max_duration: 20;
		}

		primitive_activity putMedsOnTray(){
			max_duration: 15;
		}

		primitive_activity pickupMeds(){
			max_duration: 100;
		}
				
		primitive_activity checkMedication(){
			max_duration: 10;
		}

		communicate tellAlexFood(){
			max_duration: 1;
			with: Alex;
			about:
				send(Alex.hasFood = Alex.hasFood);
		}

		communicate remindMedictionA(){
			max_duration: 1;
			with: Alex;
			about:
				send(Alex.takeMedicationA = Alex.takeMedicationA);
		}
		
				
		communicate tellHouseMed(){
			max_duration: 1;
			with: robotHouse;
			about:
				send(Alex.missedMedicationA = Alex.missedMedicationA);
		}

		communicate giveMedicationA(){
			max_duration: 1;
			with: Alex;
			about:
				send(Alex.hasMedicationA = Alex.hasMedicationA);
		}
		
		communicate alertFire(){
			max_duration: 1;
			with: Alex;
			about:
				send(Alex.evacuate = Alex.evacuate);
		}

		communicate TellAlexLocation(){
			max_duration: 1;
			with: careWorker;
			about:
				send(Alex.location = Alex.location);
		}

		communicate alexInDanger(){
			max_duration: 1;
			with: robotHouse;
			about:
				send(Alex.danger = Alex.danger);
		}
		primitive_activity openDoor(){
			max_duration: 1;
		}
		primitive_activity closeFrontDoor(){
			max_duration: 1;
		}
		primitive_activity wait(){
			max_duration: 1000;
		}

		primitive_activity platesInDishWasher(){
			max_duration: 50;
		}	

		primitive_activity pickupPlates(){
			max_duration: 5;
		}	

		move moveToChair() {
			 location: chair;
		}

		move moveToMeds() {
			 location: medCabinet;
		}
		
		move moveToBed() {
			 location: bed;
		}
		
		move moveToSinkOne() {
			 location: sinkOne;
		}
		
		move moveToDishWasher() {
			 location: dishWasher;
		}
		
		move moveToMicroWave() {
			 location: microWave;
		}
		
		move moveToToilet() {
			 location: toilet;
		}
		
		move moveToSinkTwo() {
			 location: sinkTwo;
		}
		
		move moveToFrontDoor() {
			 location: frontDoor;
		}

	workframes:


		workframe wf_answerDoor {
			repeat: true;
			priority: 10;
			
			when(knownval(robotHouse.doorBellRang = true))
			do{
				moveToFrontDoor();
				conclude((robotHouse.doorBellRang = false));
				openDoor();
				conclude((robotHouse.doorOpen = true));
				TellAlexLocation();
			}
		}
	
		workframe wf_closeDoor {
				repeat: true;
				priority: 10;
				
				when(knownval(robotHouse.doorBellRang = false) and
					knownval(robotHouse.doorOpen = true))
				do{
					closeFrontDoor();
					conclude((robotHouse.doorOpen = false));
				}
		}

		workframe wf_fireAlarmChair {
			repeat: true;
			priority: 100;
			
			detectables:
				detectable AlexSafe{
					when(whenever)
						detect((Alex.location = frontDoor), dc:100)
						then abort;
				}
			
			when(knownval(theEnvironment.fire = true) and
				knownval(Alex.location = chair))
			do{
				moveToChair();
				conclude((Alex.evacuate = true));
				alertFire();
			}
		
		}

		workframe wf_fireAlarmToilet {
			repeat: true;
			priority: 100;
			
			detectables:
				detectable AlexSafe{
					when(whenever)
						detect((Alex.location = frontDoor), dc:100)
						then abort;
				}
			
			when(knownval(theEnvironment.fire = true) and
				knownval(Alex.location = toilet))
			do{
				moveToToilet();
				conclude((Alex.evacuate = true));
				alertFire();
			}
		
		}

		workframe wf_fireAlarmSink {
			repeat: true;
			priority: 100;
			
			detectables:
				detectable AlexSafe{
					when(whenever)
						detect((Alex.location = frontDoor), dc:100)
						then abort;
				}
			
			when(knownval(theEnvironment.fire = true) and
				knownval(Alex.location = sinkTwo))
			do{
				moveToSinkTwo();
				conclude((Alex.evacuate = true));
				alertFire();
			}
		
		}

		workframe wf_alexInDanger {
			repeat: true;
			priority: 101;
			
			when(knownval(theEnvironment.fire = true) and
				knownval(Alex.evacuate = false) and
				knownval(current.fireAlerted > 5) and
				knownval(Alex.danger = false))
			do{
				conclude((Alex.danger = true));
				alexInDanger();
			}
		
		}

		workframe wf_cleanPlates {
				repeat: true;
				priority: 2;
				when(knownval(Alex.hasEmptyPlate = true))	
				do {
					moveToChair();
					pickupPlates();
					moveToDishWasher();
					conclude((Alex.hasEmptyPlate = false));
					platesInDishWasher();
					moveToChair();
				}
		}
	
		workframe wf_medicationA {
			repeat: true;
			priority: 5;
			when(knownval(current.perceivedtime = 2) and
				knownval(Alex.hasMedicationA = false))	
			do {
				moveToMeds();
				pickupMeds();
				moveToChair();
				putMedsOnTray();
				conclude((Alex.hasMedicationA = true));
				conclude((Alex.missedMedicationA = 0));
				giveMedicationA();
			}
		}




		workframe wf_checkMedicationA {
			repeat: true;
			priority: 3;
			detectables:
				detectable takenMedicationA{
					when(whenever)
						detect((Alex.hasMedicationA = false), dc:100)
						then abort;
				}
			when(knownval(current.perceivedtime > 2)and
				knownval(current.perceivedtime < 10) and
				knownval(Alex.hasMedicationA = true) and
				knownval(Alex.missedMedicationA < 2) and
				knownval(current.checkMedicationA = true))	
			do {
				checkMedication();
				conclude((current.checkMedicationA = false));
				conclude((Alex.takeMedicationA = true));
				remindMedictionA();
				conclude((Alex.missedMedicationA = Alex.missedMedicationA + 1));
			}
		}

		workframe wf_notifyMedicationANotTaken {
			repeat: true;
			priority: 15;
						
			when(knownval(current.perceivedtime < 10) and
				knownval(Alex.missedMedicationA > 1) and
				knownval(current.medNotificationA = false))
			do{
				conclude((current.medNotificationA = true));
				tellHouseMed();
			}
			
		}

		workframe wf_getFood {
			repeat: true;
			priority: 4;
			when(knownval(Alex.askedFood = true))	
			do {
				moveToMicroWave();
				getFood();
				cookFood();
				moveToChair();
				placeFoodOnTray();
				conclude((Alex.askedFood = false));
				conclude((Alex.hasFood = true));
				tellAlexFood();
				}
		}
		
		workframe wf_waitForInstruction {
			repeat: true;
			priority: 1;
			
			detectables:
				detectable platesToClean{
					when(whenever)
						detect((Alex.hasEmptyPlate = true), dc:100)
						then abort;
				}
				detectable feedAlex{
					when(whenever)
						detect((Alex.askedFood = true), dc:100)
						then abort;
				}
			
			when(knownval(current.perceivedtime < 10) and
				knownval(theEnvironment.fire = false))
			do{
				wait();
			}
			
		}
		
		workframe wf_endOfSimulation {
			repeat: true;
			priority: 999;
						
			when(knownval(current.perceivedtime < 10) and
				knownval(theEnvironment.fire =  true) and
				knownval(Alex.location = frontDoor))
			do{
				conclude((current.perceivedtime = 10));
			}
			
		}

	thoughtframes:
		thoughtframe tf_updateTime {
			repeat: true;
			priority: 1;
			
			when(knownval(Campanile_Clock.time > current.perceivedtime))

			do {
				conclude((current.perceivedtime = Campanile_Clock.time), bc: 100);
				conclude((current.checkMedicationA = true));
			}
		}
		
		thoughtframe tf_MedsUpdateTime {
			repeat: true;
			priority: 2;
			
			when(knownval(Campanile_Clock.time > current.perceivedtime) and
				knownval(current.medNotificationA = true) and
				knownval(Alex.hasTakenMedicationA = false))

			do {
				conclude((current.perceivedtime = Campanile_Clock.time), bc: 100);
				conclude((current.timeSinceMedANotification = current.timeSinceMedANotification + 1));
			}
		}
}

agent robotHouse{
	attributes:
		public int perceivedtime;
		public boolean alarmSounding;
		public boolean calledCareWorker;
		public boolean doorBellRang;
		public boolean doorOpen;
	initial_beliefs:
		(current.doorOpen = false);
		(current.doorBellRang = false);
		(current.calledCareWorker = false);
		(current.perceivedtime = 1);
		(current.alarmSounding = false);
		(theEnvironment.fire = false);
		(Alex.location = chair);
		(Alex.handsWashed = true);
		(Alex.toiletFlushed = true);
		(Alex.danger = false);
		(Alex.missedMedicationA = 0);
	initial_facts:
		(current.doorOpen = false);

	activities:
		primitive_activity monitorAlex(){
			max_duration: 3000;
		}
		primitive_activity pause(){
			max_duration: 120;
		}
		primitive_activity checkAlex(){
			max_duration: 1;
		}
		primitive_activity fireAlarm(){
			max_duration: 100;
		}
		communicate AlexWashHands(){
			max_duration: 1;
			with: Alex;
			about:
				send(Alex.handsWashed = Alex.handsWashed);
		}
		
		communicate AlexFlushToilet(){
			max_duration: 1;
			with: Alex;
			about:
				send(Alex.toiletFlushed = Alex.toiletFlushed);
		}
		
		communicate announceFireAlex(){
			max_duration: 1;
			with: Alex;
			about:
				send(theEnvironment.fire = theEnvironment.fire);
		}	

		communicate doorBell(){
			max_duration: 1;
			with: robotHelper;
			about:
				send(current.doorBellRang = current.doorBellRang);
		}

		communicate callCareWorker(){
			max_duration: 1;
			with: careWorker;
			about:
				send(Alex.missedMedicationA = Alex.missedMedicationA);
		}	

		communicate announceFireRobot(){
			max_duration: 1;
			with: robotHelper;
			about:
				send(theEnvironment.fire = theEnvironment.fire),
				send(Alex.location = Alex.location);
		}	
	workframes:

		workframe wf_monitorAlex {
			repeat: true;
			priority: 1;
			
			detectables:
				detectable AlexOnToilet{
					when(whenever)
						detect((Alex.location = toilet), dc:100)
						then abort;
				}				
				detectable fire{
					when(whenever)
						detect((theEnvironment.fire = true), dc:100)
						then abort;
				}
			
			when(knownval(current.perceivedtime < 10) and
				knownval(theEnvironment.fire = false))
			do{
				monitorAlex();
			}
		}
		
		workframe wf_soundFireAlarm {
			repeat: true;
			priority: 1000;
			detectables:
				detectable AlexSafe{
					when(whenever)
						detect((Alex.location = frontDoor), dc:100)
						then abort;
				}
			when(knownval(theEnvironment.fire = true)and
				knownval(Alex.location != frontDoor))
			do{
				conclude((current.alarmSounding = true));
				conclude((current.perceivedtime = 10));
				announceFireAlex();
				announceFireRobot();
				fireAlarm();
				
			}
		}
	
	workframe wf_AlexOnToilet{
			repeat: true;
			priority: 3;
			detectables:
				detectable AlexNotOnToilet{
					when(whenever)
						detect((Alex.location != toilet), dc:100)
						then complete;
				}
				detectable fire{
					when(whenever)
						detect((theEnvironment.fire = true), dc:100)
						then abort;
				}
			when(knownval(current.perceivedtime < 10) and
				knownval(Alex.location = toilet))
			do{
				monitorAlex();
				conclude((Alex.handsWashed = false),fc:0);
				conclude((Alex.toiletFlushed = false),fc:0);
			}
	}

	workframe wf_AlexWashHands{
			repeat: true;
			priority: 2;
			detectables:
				detectable AlexNotWashedHands{
					when(whenever)
						detect((Alex.handsWashed = true), dc:100)
						then abort;
				}
				detectable fire{
					when(whenever)
						detect((theEnvironment.fire = true), dc:100)
						then abort;
				}
			when(knownval(current.perceivedtime < 10) and
				knownval(Alex.location != toilet) and
				knownval(Alex.location != sinkTwo) and
				knownval(Alex.handsWashed = false))
			do{
				checkAlex();
				AlexWashHands();
				pause();
			}
	}

	workframe wf_AlexFlushToilet{
			repeat: true;
			priority: 4;
			detectables:
				detectable AlexNotFlushed{
					when(whenever)
						detect((Alex.toiletFlushed = true), dc:100)
						then abort;
				}
				detectable fire{
					when(whenever)
						detect((theEnvironment.fire = true), dc:100)
						then abort;
				}
			when(knownval(current.perceivedtime < 10) and
				knownval(Alex.location != toilet) and
				knownval(Alex.location != sinkTwo) and
				knownval(Alex.toiletFlushed = false))
			do{
				checkAlex();
				AlexFlushToilet();
			}
	}

	workframe wf_callCareWorker{
			repeat: true;
			priority: 10;
			detectables:
				
			when(knownval(current.perceivedtime < 10) and
				knownval(Alex.missedMedicationA > 1) and
				knownval(current.calledCareWorker = false))
			do{
				callCareWorker();
				conclude((current.calledCareWorker = true));
			}
	}

	workframe wf_doorBellRang{
			repeat: true;
			priority: 10;
			detectables:
				
			when(knownval(current.perceivedtime < 10) and
				knownval(current.doorBellRang = true))
			do{
				doorBell();
				conclude((current.doorBellRang = false));
			}
	}

	thoughtframes:
		thoughtframe tf_updateTime {
			repeat: true;
			priority: 1;
			
			when(knownval(Campanile_Clock.time > current.perceivedtime))

			do {
				conclude((current.perceivedtime = Campanile_Clock.time), bc: 100);
			}
		}
		
		thoughtframe tf_alarmOff {
			repeat: true;

			when(knownval(current.alarmSounding = true)and
				knownval(Alex.location = frontDoor))
			do{
				conclude((current.alarmSounding = false));				
			}
		}

}
agent careWorker memberof person {
	location: careCentre;
	attributes:
		public int perceivedtime;
	initial_beliefs:
		(current.perceivedtime = 1);
		(Campanile_Clock.time = 1);
		(Alex.missedMedicationA = 0);
		(robotHouse.doorBellRang = false);
		(Alex.hasTakenMedicationA = false);
	initial_facts:
	activities:
		primitive_activity waitForAnswer(){
			max_duration: 5000;
		}

		primitive_activity wait(){
			max_duration: 5000;
		}

		primitive_activity stuff(){
			max_duration: 7800;
		}

		primitive_activity medicateAlex(){
			max_duration: 500;
		}

		communicate pressDoorBell(){
			max_duration: 1;
			with: robotHouse;
			about:
				send(robotHouse.doorBellRang = robotHouse.doorBellRang);
		}
		communicate informRobotMedsTaken(){
			max_duration: 1;
			with: robotHelper;
			about:
				send(Alex.hasTakenMedicationA = Alex.hasTakenMedicationA);
		}
		move moveToChair() {
			 location: chair;
		}
		
		move moveToFrontDoor() {
			 location: frontDoor;
		}
		move moveToCareCentre() {
			 location: careCentre;
		}
		
	workframes:
		workframe wf_careWorkerStuff{
			repeat: true;
			priority: 10;
			
			when(knownval(current.perceivedtime < 10) and
				knownval(Alex.missedMedicationA < 2) and
				knownval(current.location = careCentre))
			do{
				stuff();
			}
		}
	
		workframe wf_respondToCall{
			repeat: true;
			priority: 9;
		
			when(knownval(current.perceivedtime < 10) and
				knownval(Alex.missedMedicationA > 1) and
				knownval(current.location = careCentre))
			do{
				moveToFrontDoor();
			}
		}

		workframe gotoChair{
			repeat: true;
			priority: 8;
		
			when(knownval(current.perceivedtime < 10) and
				knownval(current.location = frontDoor) and
				knownval(robotHouse.doorBellRang = true))
			do{
				moveToChair();
			}
		}
		
		workframe waitForAlex {
			repeat: true;
			priority: 7;
			detectables:
				
				detectable alexAtChair{
					when(whenever)
						detect((Alex.location = chair), dc:100)
						then abort;
				}				
			when(knownval(current.perceivedtime < 10) and
				knownval(current.location = chair) and
				knownval(Alex.location != chair))
			do{
				wait();
			}
		}

		workframe administerMeds {
			repeat: true;
			priority: 6;
			when(knownval(current.perceivedtime < 10) and
				knownval(current.location = chair) and
				knownval(Alex.location = chair) and
				knownval(Alex.hasTakenMedicationA = false))
			do{
				medicateAlex();
				conclude((Alex.missedMedicationA = 0));
				conclude((Alex.hasTakenMedicationA = true));
				informRobotMedsTaken();
				moveToCareCentre();
			}
		}

		workframe wf_knockDoor{
				repeat: true;
				priority: 5;
				detectables:
					
					detectable doorOpen{
						when(whenever)
						detect((robotHouse.doorOpen = true), dc:100)
							then abort;
					}
						
				when(knownval(current.perceivedtime < 10) and
					knownval(current.location = frontDoor) and
					knownval(robotHouse.doorBellRang = false))
				do{
					conclude((robotHouse.doorBellRang = true));
					pressDoorBell();
					waitForAnswer();
					conclude((robotHouse.doorBellRang = false));
				}
		}
		
	
	thoughtframes:
	
		thoughtframe tf_asTimeGoesBy {
		repeat: true;
		priority: 1;
		
		when(knownval(Campanile_Clock.time > current.perceivedtime))
	
		do {
			conclude((current.perceivedtime = Campanile_Clock.time), bc: 100);
		}
	}
	

}


agent theEnvironment{

	attributes:
		boolean fire;
		int perceivedtime;
		int timeSinceFire;
	initial_beliefs:
		(current.fire = false);
		(current.timeSinceFire = 0);
		(current.perceivedtime = 1);
		
	initial_facts:
		(current.fire = false);
		(current.timeSinceFire = 0);
	
	workframes:
		workframe wf_fireTwentyOne{
			repeat: false;
						
			when(knownval(current.perceivedtime = 21) and 
				knownval(current.fire = false))
			do{
				conclude((current.fire = true), fc:50);
			}
			
		}
		
		workframe wf_fireTen{
			repeat: false;
						
			when(knownval(current.perceivedtime = 10) and
				knownval(current.fire = false))
			do{
				conclude((current.fire = true), fc:50);
			}
			
		}
		
	thoughtframes:
		thoughtframe tf_asTimeGoesBy {
			repeat: true;
			priority: 1;
			
			when(knownval(Campanile_Clock.time > current.perceivedtime))
	
			do {
				conclude((current.perceivedtime = Campanile_Clock.time), bc: 100);
			}
		}
		
		thoughtframe tf_asTimeGoesByTwo {
			repeat: true;
			priority: 2;
			
			when(knownval(Campanile_Clock.time > current.perceivedtime) and
				knownval(current.fire = true))
	
			do {
				conclude((current.perceivedtime = Campanile_Clock.time), bc: 100);
				conclude((current.timeSinceFire = current.timeSinceFire + 1), bc: 100);
			}
		}

		
}
agent Campanile_Clock  {

	attributes:
		public int time;

	initial_beliefs:
		(current.time = 1);

	initial_facts:
		(current.time = 1);

	
	activities:

			primitive_activity asTimeGoesBy() {
						max_duration: 3599;
			}
	

			communicate announceTimeToAlex() {
						max_duration: 1;
						with: Alex;
						about: 
							send(current.time = current.time);

						when: end;	 
			}

			communicate announceTimeToCareWorker() {
						max_duration: 1;
						with: careWorker;
						about: 
							send(current.time = current.time);

						when: end;	 
			}

			communicate announceTimeToHouse() {
						max_duration: 1;
						with: robotHelper;
						about: 
							send(current.time = current.time);

						when: end;	 
			}
		
			communicate announceTimeToRobot() {
						max_duration: 1;
						with: robotHouse;
						about: 
							send(current.time = current.time);

						when: end;	 
			}

			communicate announceTimeToEnvironment() {
						max_duration: 1;
						with: theEnvironment;
						about: 
							send(current.time = current.time);

						when: end;	 
			}
			
	workframes:

			
			workframe wf_asTimeGoesBy {
					repeat: true;
				            
					when(knownval(current.time < 10))		

					do {
						asTimeGoesBy();
						conclude((current.time = current.time + 1), bc:100, fc:100);
						announceTimeToEnvironment();
						announceTimeToAlex();
						announceTimeToHouse();
						announceTimeToRobot();
						announceTimeToCareWorker();
					}
			}	
}

