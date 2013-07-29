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
									</predicate>
									<value>
									</value>
									<source>
										//memory or channel
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
								</predicate>
								<value>
								</value>
								<source>
									//memory or channel
								</source>
							</input>
							<outputs>
								<output>
									<value>
									</value>
									<destination>
										//memory or channel
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
			
			Actor nextActor = parseActor(actorLines);
			
			//TODO
			//addActor(nextActor);
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
	
	/** PARSE METHODS **/
	
	private Actor parseActor(ArrayList<String> actorLines){
		Actor actor = new Actor();
		
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
				actor._name = nameLines.get(0);
			} else if (line.equalsIgnoreCase("<channels>")) {
				parsingChannels = true;
			} else if (line.equalsIgnoreCase("</channels>")) {
				parsingChannels = false;
				actor._channels = parseChannels(channelLines);
			} else if (line.equalsIgnoreCase("<state>")) {
				parsingStates = true;
			} else if (line.equalsIgnoreCase("</state>")) {
				parsingStates = false;
				actor._states.add(parseState(stateLines));
			} else if (line.equalsIgnoreCase("<startState>")) {
				parsingStartState = true;
			} else if (line.equalsIgnoreCase("</startState>")) {
				parsingStartState = false;
				actor._startState = new State(startStateLines.get(0));
			} else if (line.equalsIgnoreCase("<subActor>")) {
				parsingSubActors = true;
			} else if (line.equalsIgnoreCase("</subActor>")) {
				parsingSubActors = false;
				actor._subActors.add(parseActor(subActorLines));
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
		return actor;
	}

	/**
	<name>
	<channels>
	<states>
	<startState>
	<subActor>
	 */
	private CommChannels parseChannels(ArrayList<String> channelLines) {
		CommChannels commChannels = new CommChannels();
		
		ArrayList<String> audioChannelLines = new ArrayList<String>();
		ArrayList<String> videoChannelLines = new ArrayList<String>();
		ArrayList<String> hapticChannelLines = new ArrayList<String>();
		ArrayList<String> dataChannelLines = new ArrayList<String>();
		
		boolean parsingAudioChannel = false;
		boolean parsingVideoChannel = false;
		boolean parsingHapticChannel = false;
		boolean parsingDataChannel = false;
		for(String line : channelLines){
			if (line.equalsIgnoreCase("<audio>")) {
				parsingAudioChannel = true;
			} else if (line.equalsIgnoreCase("</audio>")) {
				parsingAudioChannel = false;
				commChannels._audioChannel = new Channel(audioChannelLines.get(0));
			} else if (line.equalsIgnoreCase("<video>")) {
				parsingVideoChannel = true;
			} else if (line.equalsIgnoreCase("</video>")) {
				parsingVideoChannel = false;
				commChannels._videoChannel = new Channel(videoChannelLines.get(0));
			} else if (line.equalsIgnoreCase("<haptic>")) {
				parsingHapticChannel = true;
			} else if (line.equalsIgnoreCase("</haptic>")) {
				parsingHapticChannel = false;
				commChannels._hapticChannel = new Channel(hapticChannelLines.get(0));
			} else if (line.equalsIgnoreCase("<data>")) {
				parsingDataChannel = true;
			} else if (line.equalsIgnoreCase("</data>")) {
				parsingDataChannel = false;
				commChannels._dataChannel = new Channel(dataChannelLines.get(0));
			} else {
				if (parsingAudioChannel) {
					audioChannelLines.add(line);
				} else if (parsingVideoChannel) {
					videoChannelLines.add(line);
				} else if (parsingHapticChannel) {
					hapticChannelLines.add(line);
				} else if (parsingDataChannel) {
					dataChannelLines.add(line);
				}
			}
		}
		
		return commChannels;
	}

	/**
	<name>
	<assertions>
	<workload>
	<transitions>
	 */
	private State parseState(ArrayList<String> stateLines) {
		State state = new State();
		
		ArrayList<String> nameLines = new ArrayList<String>();
		ArrayList<String> assertionLines = new ArrayList<String>();
		ArrayList<String> workloadLines = new ArrayList<String>();
		ArrayList<String> transitionLines = new ArrayList<String>();
		
		boolean parsingName = false;
		boolean parsingAssertion = false;
		boolean parsingWorkload = false;
		boolean parsingTransition = false;
		for(String line : stateLines){
			if (line.equalsIgnoreCase("<name>")) {
				parsingName = true;
			} else if (line.equalsIgnoreCase("</name>")) {
				parsingName = false;
				state._name = nameLines.get(0);
			} else if (line.equalsIgnoreCase("<assertion>")) {
				parsingAssertion = true;
			} else if (line.equalsIgnoreCase("</assertion>")) {
				parsingAssertion = false;
				parseAssertions(assertionLines);
			} else if (line.equalsIgnoreCase("<workload>")) {
				parsingWorkload = true;
			} else if (line.equalsIgnoreCase("</workload>")) {
				parsingWorkload = false;
				parseWorkload(workloadLines);
			} else if (line.equalsIgnoreCase("<transition>")) {
				parsingTransition = true;
			} else if (line.equalsIgnoreCase("</transition>")) {
				parsingTransition = false;
				parseTransition(transitionLines);
			} else {
				if (parsingName) {
					nameLines.add(line);
				} else if (parsingAssertion) {
					assertionLines.add(line);
				} else if (parsingWorkload) {
					workloadLines.add(line);
				} else if (parsingTransition) {
					transitionLines.add(line);
				}
			}
		}
		
		return state;
	}

	/**
	<inputs>
	<message>
	 */
	private Assertion parseAssertions(ArrayList<String> assertionsLines) {
		Assertion assertion = new Assertion();
		
		ArrayList<String> inputLines = new ArrayList<String>();
		ArrayList<String> messageLines = new ArrayList<String>();
		
		boolean parsingInput = false;
		boolean parsingMessage = false;
		for(String line : assertionsLines){
			if (line.equalsIgnoreCase("<input>")) {
				parsingInput = true;
			} else if (line.equalsIgnoreCase("</input>")) {
				parsingInput = false;
				assertion._input = parseInput(inputLines);
			} else if (line.equalsIgnoreCase("<message>")) {
				parsingMessage = true;
			} else if (line.equalsIgnoreCase("</message>")) {
				parsingMessage = false;
				assertion._message = messageLines.get(0);
			} else {
				if (parsingInput) {
					inputLines.add(line);
				} else if (parsingMessage) {
					messageLines.add(line);
				}
			}
		}
		
		return assertion;
	}

	/**
	<predicate>
	<value>
	<source>
	 */
	private Input parseInput(ArrayList<String> inputLines){
		Input input = new Input();
		
		ArrayList<String> predicateLines = new ArrayList<String>();
		ArrayList<String> valueLines = new ArrayList<String>();
		ArrayList<String> sourceLines = new ArrayList<String>();
		
		boolean parsingPredicate = false;
		boolean parsingValue = false;
		boolean parsingSource = false;
		for(String line : inputLines){
			if (line.equalsIgnoreCase("<predicate>")) {
				parsingPredicate = true;
			} else if (line.equalsIgnoreCase("</predicate>")) {
				parsingPredicate = false;
				input._predicate = predicateLines.get(0);
			} else if (line.equalsIgnoreCase("<value>")) {
				parsingValue = true;
			} else if (line.equalsIgnoreCase("</value>")) {
				parsingValue = false;
				input._value = valueLines.get(0);
			} else if (line.equalsIgnoreCase("<source>")) {
				parsingSource = true;
			} else if (line.equalsIgnoreCase("</source>")) {
				parsingSource = false;
				input._source = sourceLines.get(0);
			} else {
				if (parsingPredicate) {
					predicateLines.add(line);
				} else if (parsingValue) {
					valueLines.add(line);
				} else if (parsingSource) {
					sourceLines.add(line);
				}
			}
		}
		
		return input;
	}

	/**
	<channels>
	<startStates>
	<finishStates>
	 */
	private Workload parseWorkload(ArrayList<String> workloadLines) {
		Workload workload = new Workload();
		
		ArrayList<String> channelLines = new ArrayList<String>();
		ArrayList<String> startStatesLines = new ArrayList<String>();
		ArrayList<String> finishStatesLines = new ArrayList<String>();
		
		boolean parsingChannel = false;
		boolean parsingStartState = false;
		boolean parsingFinishState = false;
		for(String line : workloadLines){
			if (line.equalsIgnoreCase("<channel>")) {
				parsingChannel = true;
			} else if (line.equalsIgnoreCase("</channel>")){
				parsingChannel = false;
				workload._channels.add(new Channel(channelLines.get(0)));
			} else if (line.equalsIgnoreCase("<startState>")) {
				parsingStartState = true;
			} else if (line.equalsIgnoreCase("</startState>")) {
				parsingStartState = false;
				workload._startStates.add(new State(startStatesLines.get(0)));
			} else if (line.equalsIgnoreCase("<finishState>")) {
				parsingFinishState = true;
			} else if (line.equalsIgnoreCase("</finishState>")) {
				parsingFinishState = false;
				workload._finishStates.add(new State(finishStatesLines.get(0)));
			} else {
				if (parsingChannel) {
					channelLines.add(line);
				} else if (parsingStartState) {
					startStatesLines.add(line);
				} else if (parsingFinishState) {
					finishStatesLines.add(line);
				}
			}
		}
		
		return workload;
	}

	/**
	<durationRange>
	<priority>
	<probability>
	<input>
	<output>
	<endState>
	 */
	private Transition parseTransition(ArrayList<String> transitionsLines) {
		Transition transition = new Transition();
		
		ArrayList<String> durationRangeLines = new ArrayList<String>();
		ArrayList<String> priorityLines = new ArrayList<String>();
		ArrayList<String> probabilityLines = new ArrayList<String>();
		ArrayList<String> inputLines = new ArrayList<String>();
		ArrayList<String> outputLines = new ArrayList<String>();
		ArrayList<String> endStateLines = new ArrayList<String>();
		
		boolean parsingDurationRange = false;
		boolean parsingPriority = false;
		boolean parsingProbability = false;
		boolean parsingInput = false;
		boolean parsingOutput = false;
		boolean parsingEndState = false;
		for(String line : transitionsLines){
			if (line.equalsIgnoreCase("<durationRange>")) {
				parsingDurationRange = true;
			} else if (line.equalsIgnoreCase("</durationRange>")) {
				parsingDurationRange = false;
				transition._durationRange = parseDurationRange(durationRangeLines);
			} else if (line.equalsIgnoreCase("<priority>")) {
				parsingPriority = true;
			} else if (line.equalsIgnoreCase("</priority>")) {
				parsingPriority = false;
				transition._priority = Integer.parseInt(priorityLines.get(0));
			} else if (line.equalsIgnoreCase("<probability>")) {
				parsingProbability = true;
			} else if (line.equalsIgnoreCase("</probability>")) {
				parsingProbability = false;
				transition._probability = Float.valueOf(probabilityLines.get(0));
			} else if (line.equalsIgnoreCase("<input>")) {
				parsingInput = true;
			} else if (line.equalsIgnoreCase("</input>")) {
				parsingInput = false;
				transition._input = parseInput(inputLines);
			} else if (line.equalsIgnoreCase("<output>")) {
				parsingOutput = true;
			} else if (line.equalsIgnoreCase("</output>")) {
				parsingOutput = false;
				transition._output = parseOutput(outputLines);
			} else if (line.equalsIgnoreCase("<endState>")) {
				parsingEndState = true;
			} else if (line.equalsIgnoreCase("</endState>")) {
				parsingEndState = false;
				transition._endState._name = endStateLines.get(0);
			} else {
				if (parsingDurationRange) {
					durationRangeLines.add(line);
				} else if (parsingPriority) {
					priorityLines.add(line);
				} else if (parsingProbability) {
					probabilityLines.add(line);
				} else if (parsingInput) {
					inputLines.add(line);
				} else if (parsingOutput) {
					outputLines.add(line);
				} else if (parsingEndState) {
					endStateLines.add(line);
				}
			}
		}
		
		return transition;
	}

	/**
	<maximum>
	<minimum>
	 */
	private DurationRange parseDurationRange(ArrayList<String> durationRangeLines) {
		DurationRange durationRange = new DurationRange();
		
		ArrayList<String> maximumLines = new ArrayList<String>();
		ArrayList<String> minimumLines = new ArrayList<String>();
		
		boolean parsingMaximum = false;
		boolean parsingMinimum = false;
		for(String line : durationRangeLines){
			if (line.equalsIgnoreCase("<maximum>")) {
				parsingMaximum = true;
			} else if (line.equalsIgnoreCase("</maximum>")) {
				parsingMaximum = false;
				durationRange._maximum = Integer.parseInt(maximumLines.get(0));
			} else if (line.equalsIgnoreCase("<minimum>")) {
				parsingMinimum = true;
			} else if (line.equalsIgnoreCase("</minimum>")) {
				parsingMinimum = false;
				durationRange._minimum = Integer.parseInt(minimumLines.get(0));
			} else {
				if (parsingMaximum) {
					maximumLines.add(line);
				} else if (parsingMinimum) {
					minimumLines.add(line);
				}
			}
		}
		
		return durationRange;
	}

	/**
	<value>
	<destination>
	 */
	private Output parseOutput(ArrayList<String> outputLines) {
		Output output = new Output();
		
		ArrayList<String> valueLines = new ArrayList<String>();
		ArrayList<String> destinationLines = new ArrayList<String>();
		
		boolean parsingValue = false;
		boolean parsingDestination = false;
		for(String line : outputLines){
			if (line.equalsIgnoreCase("<value>")) {
				parsingValue = true;
			} else if (line.equalsIgnoreCase("</value>")) {
				parsingValue = false;
				output._value = valueLines.get(0);
			} else if (line.equalsIgnoreCase("<destination>")) {
				parsingDestination = true;
			} else if (line.equalsIgnoreCase("</destination>")) {
				parsingDestination = false;
				output._destination = destinationLines.get(0);
			} else {
				if (parsingValue) {
					valueLines.add(line);
				} else if (parsingDestination) {
					destinationLines.add(line);
				}
			}
		}
		
		return output;
	}
	
	/** HELPER CLASSES **/
	
	private class Actor {
		String _name = "";
		CommChannels _channels = new CommChannels();
		ArrayList<State> _states = new ArrayList<State>();
		State _startState = new State();
		ArrayList<Actor> _subActors = new ArrayList<Actor>();
	}
	
	private class CommChannels {
		Channel _dataChannel = new Channel();
		Channel _hapticChannel = new Channel();
		Channel _videoChannel = new Channel();
		Channel _audioChannel = new Channel();
	}
	
	private class Assertion {
		Input _input = new Input();
		String _message = "";
	}
	
	private class Workload {
		ArrayList<Channel> _channels = new ArrayList<Channel>();
		ArrayList<State> _startStates = new ArrayList<State>();
		ArrayList<State> _finishStates = new ArrayList<State>();
	}
	
	private class Channel {
		String _name = "";
		Channel() {
		}
		Channel(String name) {
			_name = name;
		}
	}
	
	private class Transition {
		DurationRange _durationRange = new DurationRange();
		int _priority = 0;
		double _probability = 0.0;
		Input _input = new Input();
		Output _output = new Output();
		State _endState = new State();
	}
	
	private class State {
		String _name = "";
		Assertion _assertion = new Assertion();
		Workload _workload = new Workload();
		ArrayList<Transition> _transitionLines = new ArrayList<Transition>();
		State() {
		}
		State(String name) {
			_name = name;
		}
	}
	
	private class DurationRange {
		int _maximum = 0;
		int _minimum = 0;
	}
	
	private class Input {
		String _predicate = "";
		String _value = "";
		String _source = "";
	}
	
	private class Output {
		Object _value = new Object();
		String _destination = "";
	}
}
