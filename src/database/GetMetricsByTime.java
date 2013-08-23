package database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Scanner;

public class GetMetricsByTime {

	public static void main(String[] args) {
		try {
			//retrieve database info from user
			System.out.println("Which database should I use?");
			File folder = new File("databases");
			File[] databases = folder.listFiles();
			for (int i = 0; i < databases.length; i++) {
				if (databases[i].isFile()) {
					System.out.println(i + ":" + databases[i].getName());
				}
			}
			Scanner scanner = new Scanner(System.in);
			int index = scanner.nextInt();
			scanner.close();
			
			//start database connection
			String dbname = "databases/" + databases[index].getName();
			Connection connection = null;
			Statement statement = null;
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:"+dbname);
			System.out.println("Opening database:" + databases[index]);
			
			//query database
			int time = -1;
			boolean querying = true;
			while(querying){
				statement = connection.createStatement();
				String sql = "SELECT * FROM metrics";
				
				//form metrics
				if(statement.execute(sql)){
					
				}
				
				statement.close();
			}
			
			connection.close();
			System.out.println("Closed database successfully");
			System.out.println("Goodbye");
		} catch (Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
			System.exit(0);
		}
	}

}
