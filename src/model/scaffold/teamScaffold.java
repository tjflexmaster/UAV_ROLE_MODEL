package model.scaffold;

import java.io.*;
import java.util.*;
import java.util.Map.*;
import org.junit.*;

public class teamScaffold {
	
	/**
	 * builds WiSARTeam.java
	 * @param tuple This is a map of actor names, Strings, to the channels they use, represented as a list of String[3].
	 * The key is the actor name.
	 * The first element of a channel array is the channel name.
	 * The second element of a channel array is the channel type (must be "VISUAL", "AUDIO", or "DATA").
	 * The third element of a channel array is the channel direction (aka "OUTPUT" or "INPUT").
	 */
	public void buildClass(HashMap<String, ArrayList<String[]>> channelsByActor){
		//initialize text for new team class
		String text = "";
		text += "package model.team;"
				+ "\n"
				+ "\nimport model.actors.*;"
				+ "\nimport model.events.*;"
				+ "\nimport simulator.*;"
				+ "\n"
				+ "\npublic class WiSARTeam extends Team {"
				+ "\n"
				+ "\n\tpublic WiSARTeam() {"
				+ "\n"
				+ "\n\t\t_com_channels = new ComChannelList();";
		
		//add communication channels to data structure
	    for (Entry<String, ArrayList<String[]>> actor : channelsByActor.entrySet()) {
	        String actorName = actor.getKey();
	        
	        ArrayList<String[]> channelList = actor.getValue();
	        for(String[] channel : channelList){
		        String channelName = channel[0];
		        String channelType = channel[1];
		        
		        if(channelName.contains("EVENT")){
		        	text += "\n\t\t_com_channels.add( new ComChannel<Boolean>(Channels." + channelName + ".name(), false, ComChannel.Type.AUDIO) );";
		        }else{
		        	String source = "";
		        	String destination = "";
		        	text += "\n\t\t_com_channels.add( new ComChannel<" + actorName + "." + channelName + ">(Channels." + channelName + ".name(), false, ComChannel.Type." + channelType + ", " + source + ", " + destination + ") );";
		        }
	        }
	    }
				
		text += "\n"
				+ "\n\t\tComChannelList inputs = new ComChannelList();"
				+ "\n\t\tComChannelList outputs = new ComChannelList();"
				+ "\n";
		
		//add channel assignments
	    for (Entry<String, ArrayList<String[]>> actor : channelsByActor.entrySet()) {
	        String actorName = actor.getKey();
	        
	        text += "\n\t\tinputs.clear();";
	        text += "\n\t\toutputs.clear();";
	        
	        ArrayList<String[]> channelList = actor.getValue();
	        for(String[] channel : channelList){
		        String channelName = channel[0];
		        String channelDirection = channel[2];

		        if(channelDirection.equals("INPUT")){
		        	text += "\n\t\tinputs.add(_com_channels.get(Channels." + channelName + ".name()));";
		        }else{
		        	text += "\n\t\tinputs.add(_com_channels.get(Channels." + channelName + ".name()));";
		        }
	        }
	        
	        text += "\n\t\tthis.addEvent(new " + actorName + "(inputs, outputs), 1);"
	        		+ "\n";
	    }
		
		//terminate class
		text += "\n\t}"
				+ "\n"
				+ "\n}";
		
		//print the class
		try {
			//PrintWriter writer = new PrintWriter("src/model/team/WiSARTeam.java", "UTF-8");
			PrintWriter writer = new PrintWriter("WiSARTeam.java", "UTF-8");
			writer.println(text);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test(){
		HashMap<String, ArrayList<String[]>> tests = new HashMap<String, ArrayList<String[]>>();
		
		ArrayList<String[]> list = null;
		
		list = new ArrayList<String[]>();
		list.add(new String[]{"NEW_SEARCH_EVENT", "VISUAL", "INPUT"});
		list.add(new String[]{"NEW_SEARCH_EVENT", "VISUAL", "OUTPUT"});
		tests.put("NewSearchEvent", list);

		list = new ArrayList<String[]>();
		list.add(new String[]{"MM_END_PS", "AUDIO", "INPUT"});
		tests.put("ParentSearch", list);
		
		buildClass(tests);
	}

}
