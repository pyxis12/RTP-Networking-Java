import java.net.*;
import java.io.*;
import java.util.HashMap;

public class dbengineTCP {
	private static final int BUFSIZE = 200;//32; // SIZE OF THE RECEIVE BUFFER
	private static HashMap<String, String[]> db = new HashMap<String, String[]>();
			
	
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			throw new IllegalArgumentException("Parameter(s): <Port>");
		}
		
		// hardcode the database
		populateDB();

		int servPort = Integer.parseInt(args[0]);
		
		// Create a server socket to accept client connection requests
		ServerSocket servSock = new ServerSocket(servPort);
		
		int recvMsgSize; // Size of received message
		byte[] byteBuffer = new byte[BUFSIZE]; // Receive buffer
		
		for (;;) {
			Socket clntSock = servSock.accept();
			
			System.out.println("Handling client at " + clntSock.getInetAddress().getHostAddress() + " on Port " + clntSock.getPort());
			
			InputStream in = clntSock.getInputStream();
			OutputStream out = clntSock.getOutputStream();
			
			while ((recvMsgSize = in.read(byteBuffer)) != -1) {
				// "query" here
				String[] query = separate(new String(byteBuffer));
				String queryId = query[0];
				String attributes = query[1];
				
				if (!db.containsKey(queryId)) {
					throw new Exception("GTID does not exist in the database");
				}
				
				String[] row = db.get(queryId);
				
				String[] attributeList = getAttributeList(attributes);
				
				StringBuilder resultBuilder = new StringBuilder();
				
				boolean isFirstAttribute = true;
				
				int numAttributes = Integer.valueOf(attributeList[0]);
				for (int i = 1; i <= numAttributes; i++) {
					String queryResult = getValue(row, attributeList[i]);
					
					if (isFirstAttribute) {
						resultBuilder.append(attributeList[i] + ": " + queryResult);
						isFirstAttribute = false;
					} else {
						resultBuilder.append(", " + attributeList[i] + ": " + queryResult);
					}
				}
				
				
				String resultString = resultBuilder.toString();		
				byte[] resultBuffer = resultString.getBytes();
				
				out.write(resultBuffer);
			}
			
			clntSock.close(); // Close the socket. We are done with this client!
			
		}
		/* NOT REACHED */
	}
	
	/**
	 * Queries a row for the value for an attribute.
	 * @param row
	 * @param attribute
	 * @return value for that attribute
	 * @throws Exception 
	 */
	private static String getValue(String[] row, String attribute) throws Exception {
		if (attribute.contains("first_name")) {
			return row[0];
		} else if (attribute.toLowerCase().contains("last_name")) {
			return row[1];
		} else if (attribute.toLowerCase().contains("quality_points")) {
			return row[2];
		} else if (attribute.toLowerCase().contains("gpa_hours")) {
			return row[3];
		} else if (attribute.toLowerCase().contains("gpa")) {
			return row[4];
		} else {
			throw new Exception("You have spelled an attribute incorrectly.");
		}
	}
	
	/**
	 * Generates a list of attributes to query from the client
	 * @param input
	 * @return a string array of attribute names
	 */
	private static String[] getAttributeList(String input) {
		String[] result = new String[6]; // index 0 contains the number of attributes
		
		int numAttributes = 0;
		int start = 0;
		
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == ' ' || (i == (input.length() - 1))) {
				if (input.charAt(i) == ' ') {
					String attribute = input.substring(start, i);
					numAttributes++;
					result[numAttributes] = attribute;
					start = i + 1;
				} else {
					String attribute = input.substring(start, i + 1);
					numAttributes++;
					result[numAttributes] = attribute;
					start = i + 1;
				}
			}
		}
				
		result[0] = String.valueOf(numAttributes);
		
		return result;
	}
	
	/**
	 * Hardcoding the database
	 */
	private static void populateDB() {
		String[] row1 = {"Anthony", "Peterson", "231", "63", "3.666667"};
		String[] row2 = {"Richard", "Harris", "236", "66", "3.575758"};
		String[] row3 = {"Joe", "Miller", "224", "65", "3.446154"};
		String[] row4 = {"Todd", "Collins", "218", "56", "3.892857"};
		String[] row5 = {"Laura", "Stewart", "207", "64", "3.234375"};
		String[] row6 = {"Marie", "Cox", "246", "63", "3.904762"};
		String[] row7 = {"Stephen", "Baker", "234", "66", "3.545455"};
		
		db.put("903076259", row1);
		db.put("903084074", row2);
		db.put("903077650", row3);
		db.put("903083691", row4);
		db.put("903082265", row5);
		db.put("903075951", row6);
		db.put("903084336", row7);
	}
	
	/**
	 * Separates gtid from attributes to query.
	 * input is in the format:
	 * gtid:attribute ... attribute
	 * @param input
	 * @return an array containing the gtid at index 0 and 
	 * a string of attributes separated by spaces at index 1
	 */
	private static String[] separate(String input) {
		StringBuilder gtidBuilder = new StringBuilder();
		StringBuilder attBuilder = new StringBuilder();
		String[] result = new String[2];
		
		int indexOfColon = input.indexOf(":");
		int indexOfStar = input.indexOf("*");
		
		for (int i = 0; i < indexOfColon; i++) {
			gtidBuilder.append(input.charAt(i));
		}
		
		for (int i = indexOfColon + 1; i < indexOfStar; i++) {
			attBuilder.append(input.charAt(i));
		}
		
		String gtid = gtidBuilder.toString();
		String attributes = attBuilder.toString();
		
		result[0] = gtid;
		result[1] = attributes;

		return result;
	}
}