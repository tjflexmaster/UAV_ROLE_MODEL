package model.scaffold;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.junit.Test;

public class channelScaffold {
	
	/**
	 * builds Channels.java
	 * @param channels This is a list of all communication channel names.
	 */
	public void buildClass(ArrayList<String> channels){
		//initialize the text of the class
		String text = "";
		text = "package model.team;\n\npublic enum Channels {";
		
		//add the channels
		for(String channelName : channels){
			text += "\n\t" + channelName + ",";
		}
		
		//terminate the class
		text += "\n}";
		
		//print the text
		try {
			PrintWriter writer = new PrintWriter("src/model/team/Channels.java", "UTF-8");
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
		ArrayList<String> testChannels = new ArrayList<String>();
		
		testChannels.add("NEW_SEARCH_EVENT");
		testChannels.add("TERMINATE_SEARCH_EVENT");
		testChannels.add("AUDIO_PS_MM_COMM");
		testChannels.add("AUDIO_MM_PS_COMM");
		testChannels.add("NEW_SEARCH_AREA_EVENT");
		testChannels.add("TARGET_DESCRIPTION_EVENT");
		testChannels.add("AUDIO_VO_MM_COMM");
		testChannels.add("AUDIO_MM_VO_COMM");
		testChannels.add("VIDEO_MM_VGUI_COMM");
		testChannels.add("AUDIO_OP_MM_COMM");
		testChannels.add("AUDIO_MM_OP_COMM");
		testChannels.add("VIDEO_VGUI_MM_COMM");
		testChannels.add("AUDIO_VGUI_MM_COMM");
		testChannels.add("VIDEO_OGUI_OP_COMM");
		testChannels.add("AUDIO_VO_OP_COMM");
		testChannels.add("VISUAL_VO_VGUI");
		testChannels.add("VISUAL_MM_VGUI_COMM");
		testChannels.add("VIDEO_UAV_OP_COMM");
		testChannels.add("DATA_UAV_VGUI_COMM");
		testChannels.add("DATA_UAV_OGUI_COMM");
		testChannels.add("VIDEO_OP_OGUI_COMM");
		testChannels.add("NEW_TARGET_DESCRIPTION_EVENT");
		testChannels.add("DATA_UAV_COMM");
		testChannels.add("DATA_UAV_UAV_COMM");
		testChannels.add("HAG_EVENT");
		testChannels.add("CRASHED_EVENT");
		testChannels.add("VISUAL_OP_OGUI_COMM");
		testChannels.add("VISUAL_OP_UAV_COMM");
		testChannels.add("DATA_OGUI_UAV_COMM");
		
		buildClass(testChannels);
	}
}
