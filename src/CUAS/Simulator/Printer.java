package CUAS.Simulator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

public class Printer {
	private HashMap<String,ArrayList<String>> swimlanes;
	int time_step;
	int min_width;
	int printed;
	String file_name;
	ArrayList<String> last_states;
	public Printer(){
		swimlanes = new HashMap<String,ArrayList<String>>();
		time_step = 0;
		min_width = 0;
		printed = 0;
		last_states = new ArrayList<String>();
	}
	
	public void addLane(String name){
		swimlanes.put(name, new ArrayList<String>());
		min_width = Math.max(min_width, name.length());
	}
	
	public void addStateChange(String name, String state){
		if(name.contains("Event")){
			state = name + "-" + state;
			name = "Event";
		}else if(name.contains("UAV") && name.length() > 3){
			state = name + "-" + state;
			name = "UAV";
		}
		ArrayList<String> lane = swimlanes.get(name);
		if(lane == null){
			addLane(name);
			lane = swimlanes.get(name);
		}
		lane.add(time_step, state);
		min_width = Math.max(min_width, state.length());
	}
	public void increment(){
		time_step++;
		for(Entry<String, ArrayList<String>> lane : swimlanes.entrySet()){
			if(lane.getValue().size() == time_step){
				lane.getValue().add("");
			}
		}
	}
	public void printSectionA(){
		File dir = new File("simulations");
		dir.mkdir();
		String path = dir.getPath();
		File file = new File(path +"\\temp.txt");
		
		try {
			file.createNewFile();
			PrintWriter out = new PrintWriter(new FileWriter(file)); 
			ArrayList<String> entries = new ArrayList<String>();
			for(String name : swimlanes.keySet()){
				entries.add(name);
			}
			out.println(formatLine(entries));
			if(printed > 0){
				out.println(formatLine(last_states));
			}
			entries.clear();
			for(int i = printed; i < time_step; i++){
				for(String name : swimlanes.keySet()){
					String entry = swimlanes.get(name).get(i);
					entries.add(entry);
				}
				out.println(formatLine(entries));
				entries.clear();
				printed++;
			}
			out.flush();
			out.close();
			updateLastStates();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void updateLastStates() {
		last_states.clear();
		for(ArrayList<String> lane : swimlanes.values()){
			int last_state = 0;
			for(int index = 0; index < printed; index++){
				String state = lane.get(index);
				if(state.length() > 0 &&(state.contains("UAV") && !state.contains("-"))){
					last_state = index;
				}
			}
			last_states.add(lane.get(last_state));
		}
		
	}

	public void printToFileA(){
		File dir = new File("simulations");
		dir.mkdir();
		String path = dir.getPath();
		if(file_name == null || file_name.isEmpty()){
			System.out.println("Type simulation name: ");
			Scanner readUserInput = new Scanner(System.in);
			file_name = readUserInput.nextLine();
			if(file_name.isEmpty()){
				file_name = "outputA.txt";
			}else if(!file_name.contains(".txt")){
				file_name = file_name.concat(".txt");
			}
		}else{
			int i = file_name.indexOf('.');
			file_name = file_name.substring(0, i) + 'A' + file_name.substring(i);
		}
		File file = new File(path +"\\" +file_name);
		try {
			file.createNewFile();
			System.out.printf("file located in: %s\n",file.getAbsolutePath());
			PrintWriter out = new PrintWriter(new FileWriter(file)); 
			ArrayList<String> entries = new ArrayList<String>();
			for(String name : swimlanes.keySet()){
				entries.add(name);
			}
			out.println(formatLine(entries));
			entries.clear();
			for(int i = 0; i < time_step; i++){
				for(String name : swimlanes.keySet()){
					entries.add(swimlanes.get(name).get(i));
				}
				out.println(formatLine(entries));
				entries.clear();
				
			}
			
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	private String formatLine(ArrayList<String> entries){
		StringBuilder str = new StringBuilder();
		int size = entries.size();
		for(int i = 0; i < size; i++){
			String entry = entries.get(i);
			int spaces = min_width-entry.length();
			int count = spaces;
			if(!entry.contains("-")){
				while(count>spaces/2){
					str.append(' ');
					count--;
				}
				str.append(entry);
				while(count>0){
					str.append(' ');
					count--;
				}
			}else{
				int center = entry.indexOf('-');
				str.append(entry.substring(0, center));
				while(count>spaces/2){
					str.append('-');
					count--;
				}
				while(count>0){
					str.append('-');
					count--;
				}
				str.append(entry.substring(center));
			}
			str.append('|');
		}
		return str.toString();
	}
}
