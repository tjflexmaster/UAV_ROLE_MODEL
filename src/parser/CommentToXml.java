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
			System.out.println(file.getName());
			System.out.println(xml.parseFile(file));
		}
		//System.out.println(xml.parseFile(new File("MissionManager")));
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
			while(!line.startsWith("* ("))line = br.readLine().trim();
			int start = line.indexOf('(')+1;
			int end = line.indexOf(',');
			String startState = line.substring(start, end);
			str.append("<actor>\n");
			str.append("\t<name>" + f.getName() + "</name>\n");
			str.append("\t<state>\n");
			str.append("\t\t<name>");
			str.append(startState);
			str.append("</name>\n");
			str.append("\t\t<transitions>\n");
			while(!line.endsWith("*/")){
				str.append(parseComment(line));
				line = br.readLine().trim();
			}
			str.append("\t\t<transitions>\n");
			str.append("\t</state>\n");
			str.append("</actor>");
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
		str.append("\t\t<transition>\n");
		str.append("\t\t\t<durationRange>\n");
		str.append("\t\t\t\t<max></max>\n");
		str.append("\t\t\t\t<min></min>\n");
		str.append("\t\t\t</durationRange>\n");
		str.append("\t\t\t<priority></priority>\n");
		str.append("\t\t\t<probability></probability>\n");
		start = s.indexOf('[')+1;
		end = s.indexOf(']', start);
		str.append("\t\t\t<inputs>");
		String[] inputs = s.substring(start, end).split(", ");
		for(String input : inputs){
			str.append("\t\t\t\t<input>\n");
			str.append("\t\t\t\t\t<predicate type =\"\">\n");
			str.append("\t\t\t\t\t<value data_type=\"\">");
			str.append(input);
			str.append("</value>\n");
			str.append("\t\t\t\t\t<source type=\"channel\" name=\"\"/>\n");
			str.append("\t\t\t\t</input>\n");
		}
		
		start = s.indexOf('[', end)+1;
		end = s.indexOf(']', start);
		String[] internals = s.substring(start, end).split(", ");
		for(String internal : internals){
			str.append("\t\t\t\t<input>\n");
			str.append("\t\t\t\t\t<predicate type =\"\">\n");
			str.append("\t\t\t\t\t<value data_type=\"\">");
			str.append(internal);
			str.append("</value>\n");
			str.append("\t\t\t\t\t<source type=\"channel\" name=\"\"/>\n");
			str.append("\t\t\t\t</input>\n");
		}
		str.append("\t\t\t</inputs>\n");
		start = s.indexOf('(', end)+1;
		end = s.indexOf(',', start);
		String endState = s.substring(start, end);
		start = s.indexOf('[',end)+1;
		end = s.indexOf(']', start);
		str.append("\t\t\t<outputs>\n");
		String[] outputs = s.substring(start, end).split(", ");
		for(String output : outputs){
			str.append("\t\t\t\t<output>\n");
			str.append("\t\t\t\t\t<predicate type =\"\">\n");
			str.append("\t\t\t\t\t<value data_type=\"\">");
			str.append(output);
			str.append("</value>\n");
			str.append("\t\t\t\t\t<source type=\"channel\" name=\"\"/>\n");
			str.append("\t\t\t\t</output>\n");
		}
		start = s.indexOf('[', end)+1;
		end = s.indexOf(']', start);
		String[] temp_internals = s.substring(start, end).split(", ");
		for(String temp_internal : temp_internals){
			str.append("\t\t\t\t<output>\n");
			str.append("\t\t\t\t\t<predicate type =\"\">\n");
			str.append("\t\t\t\t\t<value data_type=\"\">");
			str.append(temp_internal);
			str.append("</value>\n");
			str.append("\t\t\t\t\t<source type=\"channel\" name=\"\"/>\n");
			str.append("\t\t\t\t</output>\n");
		}
		str.append("\t\t\t</outputs>\n");
		str.append("\t\t\t<endState>" + endState + "</endState>\n");
		str.append("\t\t<transition>\n");
		return str.toString();
	}
}
