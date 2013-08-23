package model.scaffold;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;

import model.team.Channels;

/**
 * Builds a team of actors from lists of transitions in the model/transitions package.
 * Transition := (CurrentState,[Channel=InputData*],[Memory=InputValue*],Priority,Duration,Probability)x(NextState,[Channel=OutputData*],[Memory=OutputValue*])
 * CurrentState := Name
 * Name := string
 * Channel := ChannelType
 * ChannelType := a||v||d||e
 * InputData := Data
 * Data := Source_Name_Destination
 * Source := Name
 * Destination := Name
 * Memory := Name
 * InputValue := boolean||integer
 * Duration := Name||[integer,integer]
 * NextState := Name
 * OutputData := Data
 * OutputValue := boolean||integer||Operation
 * Operation := "++"||"--"
 */
public class Scaffold {
	public static void main(String[] args){
		Scaffold xml = new Scaffold();
		File f = new File("/Users/rob/git/UAV_ROLE_MODEL/");
		for(File file : f.listFiles()){
			if(file.getName().equals("src"))
				f = file;
		}
		for(File file : f.listFiles()){
			if(file.getName().equals("model"))
				f = file;
		}
		for(File file : f.listFiles()){
			if(file.getName().equals("transitions"))
				f = file;
		}
		//each actor
		for(File file : f.listFiles()){
			BufferedReader br;
			try {
				if(file.getName().endsWith(".java"))
					continue;
				br = new BufferedReader(new FileReader(file));
				StringBuilder constructor = new StringBuilder();
				StringBuilder memory = new StringBuilder();
				memory.append("\n@Override\nprotected void initializeInternalVariables() {");
				String name = file.getName();
				name = name.substring(0, name.indexOf('.'));
				if(name.length() > 0){
					String line = br.readLine();
					HashMap<String, String> initializers = new HashMap<String, String>();
					HashMap<String, String> enumerations = new HashMap<String, String>();
					while(line != null){
						line = line.trim();
						if(line.startsWith("(")){
							String[] transition_state = xml.parseComment(line, memory, name, enumerations);
							String state = transition_state[0].substring(0, transition_state[0].indexOf('.')).trim();
							if(initializers.containsKey(state)){
								if(initializers.get(state).contains("State " + transition_state[1])){
									initializers.put(state, initializers.get(state) + "\n\t// " + line + transition_state[0]);
								}else
									initializers.put(state, "State " + transition_state[1] + ", " + initializers.get(state) + "\n\t// " + line + transition_state[0]);
							}else{
								initializers.put(state, "State " + state + ", State " + transition_state[1] + ") {" + "\n\t// " + line + transition_state[0]);
							}
						}
						line = br.readLine();
					}
					memory.append("\n}");
					StringBuilder body = new StringBuilder();
					for(Entry<String, String> transition : initializers.entrySet()){
						constructor.insert(0, "\n\tState " + transition.getKey() + " = new State(\"" + transition.getKey() + "\",1);");
						String call_line = transition.getValue().split("\n")[0];
						String parameters = call_line.substring(call_line.indexOf("State"), call_line.indexOf(')'));
						parameters = parameters.replaceAll("State ", "");
						constructor.append("\n\tinitialize" + transition.getKey() + "(inputs, outputs, " + parameters + ");");
						body.append("\n public void initialize" + transition.getKey() + "(ComChannelList inputs, ComChannelList outputs, ");
						body.append(transition.getValue()); 
						body.append("\n\tadd(" + transition.getKey() + ");\n}");
					}
					constructor.insert(0,"\npublic " + name + "(ComChannelList inputs, ComChannelList outputs) {");
					constructor.append("\n}");
					StringBuilder enums = new StringBuilder();
					for(Entry<String, String> enumeration : enumerations.entrySet()){
						enums.append(enumeration.getValue() + "\n}");
					}
					File new_file = file.getParentFile().getParentFile();
					for(File temp : new_file.listFiles()){
						if(temp.getName().equals("actors")){
							new_file = temp;
							break;
						}
					}
					new_file = new File(new_file.getPath() + "/" + name + ".java");
					new_file.createNewFile();
					System.out.println(new_file.toPath());
					PrintWriter writer = new PrintWriter(new_file);
					writer.print("package model.actors;\n\nimport model.team.Channels;\nimport model.team.Duration;\nimport simulator.Actor;\nimport simulator.ComChannelList;\nimport simulator.State;\nimport simulator.Transition;\n\npublic class " + name + " extends Actor {" + enums.toString() + constructor.toString() + body.toString() + memory.toString() + "\n}");
					writer.close();
				}
//				System.out.println(enums.toString() + constructor.toString() + body.toString() + memory.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public String[] parseComment(String s, StringBuilder memory, String name, HashMap<String,String> enumerations){
		StringBuilder transition = new StringBuilder();
		int start = s.indexOf('(')+1;
		int end = s.indexOf(',');
		String startState = s.substring(start, end);
		transition.append("\n\t" + startState + ".add(new Transition(_internal_vars, inputs, outputs, ");
		start = s.indexOf('(', end)+1;
		end = s.indexOf(',',start);
		String endState = s.substring(start, end);
		
		String[] endingValues = s.substring(0,s.indexOf(')')).split(",");
		String probability = endingValues[endingValues.length-1].trim();
		String duration = endingValues[endingValues.length-2].trim();
		if(duration.contains("[")){
			String[] ints = duration.substring(1, duration.length()-1).split("-");
			String min = ints[0].trim();
			String max = ints[1].trim();
			duration = "new Range(" + min + ", " + max + ")";
		}else{
			duration = "Duration." + duration + ".getRange()";
		}
		String priority = endingValues[endingValues.length-3].trim();
		transition.append(endState + ", " + duration + ", "  + priority + ", "+ probability + ") {\n\t\t@Override\n\t\tpublic boolean isEnabled() { ");
		start = s.indexOf('[')+1;
		end = s.indexOf(']', start);
		String[] inputs = s.substring(start,end).split(", ");
		for(String input : inputs){
			if(input.contains("=")){
				String[] division = input.split("=");
				String[] value_channel = getValue_Channel(division);
				if(value_channel[0].length() > 0 && name.equals(value_channel[0].substring(0, value_channel[0].indexOf('.')))){
					if(!enumerations.containsKey(value_channel[1])){
						enumerations.put(value_channel[1], "\npublic enum " + value_channel[1] + "{\n\t" + division[1] + ",");
					}else{
						if(!enumerations.get(value_channel[1]).contains(division[1]))
							enumerations.put(value_channel[1], enumerations.get(value_channel[1]) + "\n\t" + division[1] + ",");
					}
				}
				if(!value_channel[1].equals("EVENT")){
					value_channel[0] += value_channel[1] + "." + division[1];
					transition.append("\n\t\t\tif(!" + value_channel[0] + ".equals(_inputs.get(Channels." + value_channel[1] + ".name()).value())) {\n\t\t\t\treturn false;\n\t\t\t}");
				}else{
					transition.append("\n\t\t\tif(_inputs.get(Channels."+division[1]+".name()).value() == null && !(Boolean)_inputs.get(Channels."+division[1]+".name()).value()) {\n\t\t\t\treturn false;\n\t\t\t}");
				}
					
			}

			start = s.indexOf('[',end)+1;
			end = s.indexOf(']', start);
			String[] internals = s.substring(start,end).split(", ");
			for(String internal : internals){
				if(internal.contains("=")){
					String[] division = internal.split("=");
					boolean add_to_memory = false;
					if(!memory.toString().contains(division[0])){
						memory.append("\n\t_internal_vars.addVariable(\"" + division[0] + "\", ");
						add_to_memory = true;
					}
					String value = "";
					if(division[1].equalsIgnoreCase("true") || division[1].equalsIgnoreCase("false")){
						value = "new Boolean(" + division[1].toLowerCase() + ")";
						if(add_to_memory)
							memory.append("false);");
					} else {
						try{
							value = "new Integer(" + Integer.parseInt(division[1]) + ")";
							if(add_to_memory)
								memory.append("0);");
						}catch(NumberFormatException e){
							value = "\"" + division[1] + "\"";
							if(add_to_memory)
								memory.append("null);");
						}
					}
					transition.append("\n\t\t\tif(!" + value + ".equals(_internal_vars.getVariable (\"" + division[0] + "\"))) {\n\t\t\t\treturn false;\n\t\t\t}");
				}
			}
		}

		start = s.indexOf('[', end)+1;
		end = s.indexOf(']', start);
		String[] temp_outputs = s.substring(start, end).split(", ");
		for(String temp_output : temp_outputs){
			if(temp_output.contains("=")){
				String[] division = temp_output.split("=");
				String[] value_channel = getValue_Channel(division);
				if(name.equals(value_channel[0].substring(0, value_channel[0].indexOf('.')))){
					if(!enumerations.containsKey(value_channel[1])){
						enumerations.put(value_channel[1], "\npublic enum " + value_channel[1] + "{\n\t" + division[1] + ",");
					}else{
						if(!enumerations.get(value_channel[1]).contains(division[1]))
							enumerations.put(value_channel[1], enumerations.get(value_channel[1]) + "\n\t" + division[1] + ",");
					}
				}
				value_channel[0] += value_channel[1] + "." + division[1];
				transition.append("\n\t\t\tsetTempOutput(Channels." + value_channel[1] + ".name(), " + value_channel[0] + ");");
			}
		}
		start = s.indexOf('[', end)+1;
		end = s.indexOf(']', start);
		String[] temp_internals;
		if(s.substring(start, end).contains(", ")){
			temp_internals = s.substring(start, end).split(", ");
		}else{
			temp_internals = s.substring(start, end).split(",");
		}
		for(String temp_internal : temp_internals){
			if(temp_internal.contains("=")){
				String[] division = temp_internal.split("=");
				boolean add_to_memory = false;
				if(!memory.toString().contains(division[0])){
					memory.append("\n\t_internal_vars.addVariable(\"" + division[0] + "\", ");
					add_to_memory = true;
				}
				String value = "";
				if(division[1].equalsIgnoreCase("true") || division[1].equalsIgnoreCase("false")){
					value = division[1].toLowerCase();
					if(add_to_memory)
						memory.append("false);");
				} else {
					try{
						value = String.valueOf(Integer.parseInt(division[1]));
						if(add_to_memory)
							memory.append("0);");
					}catch(NumberFormatException e){
						value = "\"" + division[1].toUpperCase() + "\"";
						if(add_to_memory)
							memory.append("null);");
					}
				}
				transition.append("\n\t\t\tsetTempInternalVar(\"" + division[0] + "\", " + value + ");");
			}
		}
		transition.append("\n\t\t\treturn true;\n\t\t}\n\t});");
		return new String[]{transition.toString(), endState};
	}

	/**
	 * @param division
	 * @return
	 */
	private String[] getValue_Channel(String[] division) {
		String[] value_channel = new String[2];
		value_channel[0] = "";
		String prefix = "";
		if(division[1].startsWith("MM")){
			value_channel[0] = "MissionManager.";
			prefix = "MM";
		} else if(division[1].startsWith("OP")){
			value_channel[0] = "Operator.";
			prefix = "OP";
		} else if(division[1].startsWith("VO")){
			value_channel[0] = "VideoOperator.";
			prefix = "VO";
		} else if(division[1].startsWith("VGUI")){
			value_channel[0] = "VideoOperatorGui.";
			prefix = "VGUI";
		} else if(division[1].startsWith("OGUI")){
			value_channel[0] = "OperatorGui.";
			prefix = "OGUI";
		} else if(division[1].startsWith("PS")){
			value_channel[0] = "ParentSearch.";
			prefix = "PS";
		} else if(division[1].startsWith("UAV")){
			value_channel[0] = "UAV.";
			prefix = "UAV";
		} else if(division[1].startsWith("UAVHAG")){
			value_channel[0] = "UAVHeightAboveGround.";
			prefix = "UAVHAG";
		} else if(division[1].startsWith("UAVVF")){
			value_channel[0] = "UAVVideoFeed.";
			prefix = "UAVVF";
		} else if(division[1].startsWith("UAVBAT")){
			value_channel[0] = "UAVBattery.";
			prefix = "UAVBAT";
		} else if(division[1].startsWith("UAVFP")){
			value_channel[0] = "UAVFlightPlan.";
			prefix = "UAVFP";
		}
		value_channel[1] = getChannel(division, prefix);
		return value_channel;
	}
	/**
	 * @param division
	 * @param prefix
	 * @return
	 */
	private String getChannel(String[] division, String prefix) {
		String suffix = extractSuffix(division);
		String channel = "";
		switch(division[0]){
		case "A": channel = "AUDIO_";break;
		case "V": channel = "VIDEO_";break;
		case "D": channel = "DATA_";break;
		case "E": return "EVENT";
		}
		channel += prefix + "_" + suffix + "_COMM";
		return channel;
	}
	/**
	 * @param division
	 * @return
	 */
	private String extractSuffix(String[] division) {
		String suffix = "";
		if(division[1].endsWith("MM")){
			suffix = "MM";
		} else if(division[1].endsWith("OP")){
			suffix = "OP";
		} else if(division[1].endsWith("VO")){
			suffix = "VO";
		} else if(division[1].endsWith("VGUI")){
			suffix = "VGUI";
		} else if(division[1].endsWith("OGUI")){
			suffix = "OGUI";
		} else if(division[1].endsWith("PS")){
			suffix = "PS";
		} else if(division[1].endsWith("UAV")){
			suffix = "UAV";
		}
		return suffix;
	}
}
