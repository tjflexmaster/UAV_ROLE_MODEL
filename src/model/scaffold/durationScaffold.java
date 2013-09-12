package model.scaffold;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.junit.Test;

public class durationScaffold {
	
	/**
	 * builds Durations.java
	 * @param durations This is a list of arrays of strings three long that represent a duration.
	 * The first string of the array represents the name.
	 * The second string of the array represents the minimum duration.
	 * The third string of the array represents the maximum duration.  
	 */
	public void buildClass(ArrayList<String[]> durations){
		//initialize text of the class
		String text = "";
		text = "package model.team;"
				+ "\n"
				+ "\nimport simulator.Range;"
				+ "\n"
				+ "\npublic enum Duration {";
		
		//add durations
		for(String[] duration : durations){
			String name = duration[0];
			String min = duration[1];
			String max = duration[2];
			text += "\n\t" + name + "(" + min + "," + max + "),";
		}
		
		//add duration methods
		text += "\n"
				+ "\n\tprivate Integer _minimum;"
				+ "\n\tprivate Integer _maximum;"
				+ "\n\tprivate Range _range;"
				+ "\n"
				+ "\n\tDuration(int minimum, int maximum) {"
				+ "\n\t\t_minimum = minimum;"
				+ "\n\t\t_maximum = maximum;"
				+ "\n\t\t_range = new Range(_minimum, _maximum);"
				+ "\n\t}"
				+ "\n"
				+ "\n\tDuration(Integer duration){"
				+ "\n\t\t_minimum = duration;"
				+ "\n\t\t_maximum = duration;"
				+ "\n\t\t_range = new Range(_minimum, _maximum);"
				+ "\n\t}"
				+ "\n"
				+ "\n\tpublic Duration update(Integer duration){"
				+ "\n\t\t_minimum = duration;"
				+ "\n\t\t_maximum = duration;"
				+ "\n\t\t_range = new Range(_minimum, _maximum);"
				+ "\n\t\treturn this;"
				+ "\n\t}"
				+ "\n"
				+ "\n\tpublic String toString() {"
				+ "\n\t\tString result = \"\";"
				+ "\n\t\tif (_minimum == _maximum) {"
				+ "\n\t\t\tresult = Integer.toString(_maximum);"
				+ "\n\t\t} else {"
				+ "\n\t\t\tresult = \"(\" + Integer.toString(_minimum) + \"-\" + Integer.toString(_maximum) + \")\";"
				+ "\n\t\t}"
				+ "\n\t\treturn result;"
				+ "\n\t}"
				+ "\n"
				+ "\n\tpublic Range getRange() {"
				+ "\n\t\treturn _range;"
				+ "\n\t}"
				+ "\n}";
		
		try {
			PrintWriter writer = new PrintWriter("src/model/team/Duration.java", "UTF-8");
			//PrintWriter writer = new PrintWriter("src/model/team/Duration.java", "UTF-8");
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
		ArrayList<String[]> tests = new ArrayList<String[]>();
		
		String[] nextDuration = new String[3];
		
		nextDuration[0] = "MM_OBSERVING_VGUI";
		nextDuration[1] = "15";
		nextDuration[2] = "20";
		tests.add(nextDuration);
		
		buildClass(tests);
	}

}
