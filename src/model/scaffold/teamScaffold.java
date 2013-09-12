package model.scaffold;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class teamScaffold {
	
	/**
	 * builds WiSARTeam.java
	 * @param tuple This is a list of string arrays that represent the channel assignments of a team.
	 * The first element of an array is the actor name.
	 * The second element of an array is the channel name.
	 * The second element of an array is the channel type (must be "VISUAL", "AUDIO", or "DATA").
	 */
	public void buildClass(ArrayList<String[]> tuple){
		//initialize text for new team class
		String text = "";
		text = "package model.team;\n\npublic enum Channels {";
		
		//add tuples
		for(String[] channelName : tuple){
		}
		
		//terminate class
		text += "\n}";
		
		//print the class
		try {
			PrintWriter writer = new PrintWriter("src/model/team/WiSARTeam.java", "UTF-8");
			writer.println(text);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}
