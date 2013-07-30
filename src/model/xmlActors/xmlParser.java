package model.xmlActors;

import java.io.*;
import java.util.*;

public class xmlParser {
	
	/**
		<actor>
			<name>
			</name>
			<channels>
				<audio>
				<video>
			</channels>
			<states>
				<state>
					<name>
					</name>
					<assertions>
						<assert>
							<inputs>
								<input>
									<predicate>
										<type>
										</type>
									</predicate>
									<value>
										<dataType>
										</dataType>
									</value>
									<source>
										<type>
										</type>//memory or channel
									</source>
								</input>
							</inputs>
							<message>
							</message>
						</assert>
					</assertions>
					<workload>
						<channels>
						</channels>
						<startStates>
						</startStates>
						<finishStates>
						</finishStates>
					</workload>
					<transitions>
						<transition>
							<durationRange>
								<maximum>
								</maximum>
								<minimum>
								</minimum>
							</durationRange>
							<priority>
							</priority>
							<probability>
							</probability>
							<input>
								<predicate>
									<type>
									</type>
								</predicate>
								<value>
									<dataType>
									</dataType>
								</value>
								<source>
									<type>
									</type>//memory or channel
								</source>
							</input>
							<outputs>
								<output>
									<value>
										<dataType>
										</dataType>
									</value>
									<destination>
										<type>
										</type>//memory or channel
										<name>
										</name>
									</destination>
								</output>
							</outputs>
							<endState>
							</endState>
						</transition>
					</transitions>
				</state>
			</states>
			<startState>
			</startState>
			<subActor>
				...
			</subActor>
		</actor>
	 */
	public void parse(){
		BufferedReader reader = null;
		try {
			ArrayList<String> actorLines = new ArrayList<String>();
			String nextLine = "";
 
			reader = new BufferedReader(new FileReader("parentSearch.xml"));
			
			while ((nextLine = reader.readLine()) != null) {
				actorLines.add(nextLine);
			}
			
			parseActor(actorLines);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/** HELPER METHODS **/
	
	private void parseActor(ArrayList<String> actorLines){
		ArrayList<String> nameLines = new ArrayList<String>();
		ArrayList<String> channelLines = new ArrayList<String>();
		ArrayList<String> stateLines = new ArrayList<String>();
		ArrayList<String> startStateLines = new ArrayList<String>();
		ArrayList<String> subActorLines = new ArrayList<String>();
		
		boolean parsingName = false;
		boolean parsingChannels = false;
		boolean parsingStates = false;
		boolean parsingStartState = false;
		boolean parsingSubActors = false;
		for(String line : actorLines){
			if (line.equalsIgnoreCase("<name>")) {
				parsingName = true;
			} else if (line.equalsIgnoreCase("</name>")) {
				parsingName = false;
				addActorName(nameLines.get(0));
			} else if (line.equalsIgnoreCase("<channels>")) {
				parsingChannels = true;
			} else if (line.equalsIgnoreCase("</channels>")) {
				parsingChannels = false;
				parseChannels(channelLines);
			} else if (line.equalsIgnoreCase("<states>")) {
				parsingStates = true;
			} else if (line.equalsIgnoreCase("</states>")) {
				parsingStates = false;
				parseStates(stateLines);
			} else if (line.equalsIgnoreCase("<startState>")) {
				parsingStartState = true;
			} else if (line.equalsIgnoreCase("</startState>")) {
				parsingStartState = false;
				addStartState(startStateLines.get(0));
			} else if (parsingStartState) {
				//addStartState(line);
			} else if (line.equalsIgnoreCase("<subActor>")) {
				parsingSubActors = true;
			} else if (line.equalsIgnoreCase("</subActor>")) {
				parsingSubActors = false;
				parseActor(subActorLines);
			} else {
				if (parsingName) {
					nameLines.add(line);
				} else if (parsingChannels) {
					channelLines.add(line);
				} else if (parsingStates) {
					stateLines.add(line);
				} else if (parsingStartState) {
					startStateLines.add(line);
				} else if (parsingSubActors) {
					subActorLines.add(line);
				}
			}
		}
	}

	private void parseChannels(ArrayList<String> channelLines) {
		boolean parsingAudioChannels = false;
		boolean parsingVideoChannels = false;
		boolean parsingHapticChannels = false;
		boolean parsingDataChannels = false;
		for(String line : channelLines){
			if (line.equalsIgnoreCase("<audio>")) {
				parsingAudioChannels = true;
			} else if (line.equalsIgnoreCase("</audio>")) {
				parsingAudioChannels = false;
			} else if (line.equalsIgnoreCase("<video>")) {
				parsingVideoChannels = true;
			} else if (line.equalsIgnoreCase("</video>")) {
				parsingVideoChannels = false;
			} else if (line.equalsIgnoreCase("<haptic>")) {
				parsingHapticChannels = true;
			} else if (line.equalsIgnoreCase("</haptic>")) {
				parsingHapticChannels = false;
			} else if (line.equalsIgnoreCase("<data>")) {
				parsingDataChannels = true;
			} else if (line.equalsIgnoreCase("</data>")) {
				parsingDataChannels = false;
			} else {
				if (parsingAudioChannels) {
					addAudioChannel(line);
				} else if (parsingVideoChannels) {
					addVideoChannel(line);
				} else if (parsingHapticChannels) {
					addHapticChannel(line);
				} else if (parsingDataChannels) {
					addDataChannel(line);
				}
			}
		}
	}

	private void parseStates(ArrayList<String> stateLines) {
		ArrayList<String> nameLines = new ArrayList<String>();
		ArrayList<String> assertionsLines = new ArrayList<String>();
		ArrayList<String> workloadLines = new ArrayList<String>();
		ArrayList<String> transitionsLines = new ArrayList<String>();
		
		boolean parsingName = false;
		boolean parsingAssertions = false;
		boolean parsingWorkload = false;
		boolean parsingTransitions = false;
		for(String line : stateLines){
			if (line.equalsIgnoreCase("<name>")) {
				parsingName = true;
			} else if (line.equalsIgnoreCase("</name>")) {
				parsingName = false;
				addStateName(nameLines.get(0));
			} else if (line.equalsIgnoreCase("<assertions>")) {
				parsingAssertions = true;
			} else if (line.equalsIgnoreCase("</assertions>")) {
				parsingAssertions = false;
				parseAssertions(assertionsLines);
			} else if (line.equalsIgnoreCase("<workload>")) {
				parsingWorkload = true;
			} else if (line.equalsIgnoreCase("</workload>")) {
				parsingWorkload = false;
				parseWorkload(workloadLines);
			} else if (line.equalsIgnoreCase("<transitions>")) {
				parsingTransitions = true;
			} else if (line.equalsIgnoreCase("</transitions>")) {
				parsingTransitions = false;
				parseTransitions(transitionsLines);
			} else {
				if (parsingName) {
					nameLines.add(line);
				} else if (parsingAssertions) {
					assertionsLines.add(line);
				} else if (parsingWorkload) {
					workloadLines.add(line);
				} else if (parsingTransitions) {
					transitionsLines.add(line);
				}
			}
		}
	}

	private void parseAssertions(ArrayList<String> assertionsLines) {
		ArrayList<String> inputsLines = new ArrayList<String>();
		ArrayList<String> messageLines = new ArrayList<String>();
		
		boolean parsingInputs = false;
		boolean parsingMessage = false;
		for(String line : assertionsLines){
			if (line.equalsIgnoreCase("<inputs>")) {
				parsingInputs = true;
			} else if (line.equalsIgnoreCase("</inputs>")) {
				parsingInputs = false;
				parseInputs(inputsLines);
			} else if (line.equalsIgnoreCase("<message>")) {
				parsingMessage = true;
			} else if (line.equalsIgnoreCase("</message>")) {
				parsingMessage = false;
				addAssertionMessage(messageLines.get(0));
			} else {
				if (parsingInputs) {
					inputsLines.add(line);
				} else if (parsingMessage) {
					messageLines.add(line);
				}
			}
		}
	}

	private void parseWorkload(ArrayList<String> workloadLines) {
		
	}

	private void parseTransitions(ArrayList<String> transitionsLines) {
		
	}
}
