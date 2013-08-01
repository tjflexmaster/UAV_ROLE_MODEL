package parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CommentToXml {
	public static void main(String[] args){
		CommentToXml xml = new CommentToXml();
		File f = new File("/Users/jaredmoore/git/UAV_ROLE_MODEL/");
		for(File file : f.listFiles()){
			if(file.getName().equals("src"))
				f = file;
		}
		for(File file : f.listFiles()){
			if(file.getName().equals("model"))
				f = file;
		}
		for(File file : f.listFiles()){
			if(file.getName().equals("actors"))
				f = file;
		}
		for(File file : f.listFiles()){
			if(file.getName().equals("MissionManager.java"))
				System.out.println(xml.parseFile(file));
		}
	}
	public String parseFile(File f){
		StringBuilder str = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = br.readLine().trim();
			while(!line.startsWith("/**")){
				line = br.readLine();
				if(line == null){
					return "no transitions";
				}
				line = line.trim();
			}
			str.append("\n<actor name=\""+ f.getName() + "\">");
			str.append("\n\t<channels>\n\t\t<channel name=\"\"/>\n\t</channels>");
			str.append("\n\t<memory>\n\t\t<variable name =\"\" dataType=\"\"></variable>\n\t</memory>");
			str.append("\n\t<startState name=\"\"/>");
			str.append("\n\t<states>");
			while(!line.startsWith("* ("))line = br.readLine().trim();
			while(true){
				int start = line.indexOf('(')+1;
				int end = line.indexOf(',');
				String startState = line.substring(start, end);
				str.append("\n\t\t<state name=\"" + startState + "\">");
				str.append("\n\t\t\t<assertions>\n\t\t\t\t<assert>\n\t\t\t\t\t<inputs>\n\t\t\t\t\t\t<input>\n\t\t\t\t\t\t\t<value dataType=\"\" predicate=\"\"></value>\n\t\t\t\t\t\t\t<source type=\"\" name=\"\"/>\n\t\t\t\t\t\t</input>\n\t\t\t\t\t</inputs>\n\t\t\t\t\t<message></message>\n\t\t\t\t</assert>\n\t\t\t</assertions>");
				str.append("\n\t\t\t<transitions>");
				while(!line.endsWith("*/")){
					str.append(parseComment(line));
					line = br.readLine().trim();
				}
				str.append("\n\t\t\t</transitions>");
				str.append("\n\t\t</state>");
				while(!line.startsWith("* (")){
					line = br.readLine();
					if(line == null){
						str.append("\n\t</states>");
						str.append("\n\t<subActors>\n\t\t<actor name=\"\"/>\n\t</subActors>");
						str.append("\n</actor>");
						return str.toString();
					}
					line = line.trim();
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str.toString();
	}
	
	public String parseComment(String s){
		StringBuilder str = new StringBuilder();
		int start = s.indexOf('(')+1;
		int end = s.indexOf(',');
		String startState = s.substring(start, end);
		str.append("\n\t\t\t\t<transition duration-min=\"1\" duration-max=\"1\" priority=\"0\" probability=\"1.0\">");
		start = s.indexOf('[')+1;
		end = s.indexOf(']', start);
		str.append("\n\t\t\t\t\t<inputs>");
		String[] inputs = s.substring(start, end).split(", ");
		for(String input : inputs){
			if(!input.equals("")){
				String name = "";
				if(input.startsWith("OP"))
					name = "AUDIO_OP_MM_COMM";
				else if(input.startsWith("VO"))
					name = "AUDIO_VO_MM_COMM";
				else if(input.startsWith("PS"))
					name = "AUDIO_PS_MM_COMM";
				else if(input.startsWith("VGUI"))
					name = "VIDEO_VGUI_MM_COMM";
				str.append("\n\t\t\t\t\t\t<input type=\"chan\" name=\"" + name + "\" predicate=\"eq\" value=\"" + input + "\"/>");
			}
		}
		
		start = s.indexOf('[', end)+1;
		end = s.indexOf(']', start);
		String[] internals = s.substring(start, end).split(", ");
		for(String internal : internals){
			if(!internal.equals("")){
				str.append("\n\t\t\t\t\t\t<input type=\"memory\" name=\""+ internal +"\" predicate=\"eq\" value=\"\"/>");
			}
		}
		str.append("\n\t\t\t\t\t</inputs>");
		start = s.indexOf('(', end)+1;
		end = s.indexOf(',', start);
		String endState = s.substring(start, end);
		start = s.indexOf('[',end)+1;
		end = s.indexOf(']', start);
		str.append("\n\t\t\t\t\t<outputs>");
		String[] outputs = s.substring(start, end).split(", ");
		for(String output : outputs){
			if(!output.equals("")){
				String name = "";
				if(output.endsWith("OP"))
					name = "AUDIO_OP_MM_COMM";
				else if(output.endsWith("VO"))
					name = "AUDIO_VO_MM_COMM";
				else if(output.endsWith("PS"))
					name = "AUDIO_PS_MM_COMM";
				else if(output.endsWith("VGUI"))
					name = "VIDEO_VGUI_MM_COMM";
				str.append("\n\t\t\t\t\t\t<output type=\"chan\" name=\"" + name + "\" predicate=\"eq\" value=\"" + output + "\"/>");
			}
		}
		start = s.indexOf('[', end)+1;
		end = s.indexOf(']', start);
		String[] temp_internals = s.substring(start, end).split(", ");
		for(String temp_internal : temp_internals){
			if(!temp_internal.equals("")){
				str.append("\n\t\t\t\t\t\t<output type=\"memory\" name=\"" + temp_internal + "\" predicate=\"eq\" value=\"\"/>");
			}
		}
		str.append("\n\t\t\t\t\t</outputs>");
		str.append("\n\t\t\t\t\t<endState name=\"" + endState + "\"/>");
		str.append("\n\t\t\t\t</transition>");
		return str.toString();
	}
}
