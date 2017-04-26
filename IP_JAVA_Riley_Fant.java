
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class FutureDBDriver {
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		//load the oracle driver
		try {
			Class.forName("oracle.jdbc.OracleDriver");
		}
		catch(Exception x) {
			System.out.println( "Unable to load the driver class!" );
		}
		
		//connect to the database
		Connection dbConnection = null;
		Statement stmt = null;
		try{
			dbConnection=DriverManager.getConnection("jdbc:oracle:thin:@//oracle.cs.ou.edu:1521/pdborcl.cs.ou.edu","fant6608","RBpx4Ac3"); 
			stmt = dbConnection.createStatement();
		}catch( SQLException x ){
			System.out.println( "Couldn’t get connection!" );
			System.exit(0);
		}
		
		//entry point
		boolean continu = true;
		while(continu) {
			
			//user enters query
			Scanner scanner = new Scanner(System.in);
			int input = start(scanner);

			switch(input) {
			case 1:  //Enter a new employee (2/month).
				//Get input
				System.out.println("Enter the new employee's name:");
				String name = scanner.nextLine();
				System.out.println("Enter the new employee's address:");
				String address = scanner.nextLine();
				
				//Execute Query
				try {
					CallableStatement query1 = dbConnection.prepareCall("{CALL INSERT_NEW_EMPLOYEE(?,?)}");
					query1.setString(1, name);
					query1.setString(2, address);
					query1.executeUpdate();
				} catch (SQLException e) {
					System.out.println("Failed to insert new employee " + name + ".");
					e.printStackTrace();
					break;
				}
				
				System.out.println("Enter the type of employee (Worker, Technical Staff, or Quality Controller:");
				String employeeType = scanner.nextLine();
				
				if (employeeType.equalsIgnoreCase("Worker")) {
					System.out.println("Enter the production limit for " + name + ":");
					int productionLimit = scanner.nextInt();
					try {
						String sql = "INSERT INTO WORKER(\"EMPLOYEE.NAME\", PRODUCTION_LIMIT) VALUES(?,?)";
						PreparedStatement query = dbConnection.prepareCall(sql);
						query.setString(1, name);
						query.setInt(2, productionLimit);
						query.executeUpdate();
						System.out.println("Successfully inserted new worker " + name + ".");
					} catch(SQLException e) {
						System.out.println("Failed to insert new worker " + name + ".");
						e.printStackTrace();
					}
					
				}
				else if (employeeType.equalsIgnoreCase("Quality Controller")) {
					System.out.println("Enter the product type for " + name + " (1, 2, or 3):");
					int productType = scanner.nextInt();
					try {
						String sql = "INSERT INTO QUALITYCONTROLLER(\"EMPLOYEE.NAME\", PRODUCT_TYPE) VALUES(?,?)";
						PreparedStatement query = dbConnection.prepareCall(sql);
						query.setString(1, name);
						query.setInt(2, productType);
						query.executeUpdate();
						System.out.println("Successfully inserted new quality controller " + name + ".");
					} catch(SQLException e) {
						System.out.println("Failed to insert new quality controller " + name + ".");
						e.printStackTrace();
					}
					
				}
				else if (employeeType.equalsIgnoreCase("Technical Staff")) {
					System.out.println("Enter " + name + "'s highest level of education (BS, MS, or PhD):");
					String education = scanner.nextLine();
					System.out.println("Enter " + name + "'s technical position:");
					String technicalPosition = scanner.nextLine();
					try {
						String sql = "INSERT INTO TECHNICALSTAFF(\"EMPLOYEE.NAME\", EDUCATION, TECHNICAL_POSITION) VALUES(?,?,?)";
						PreparedStatement query = dbConnection.prepareCall(sql);
						query.setString(1, name);
						query.setString(2, education);
						query.setString(3, technicalPosition);
						query.executeUpdate();
						System.out.println("Successfully inserted new technical staff " + name + ".");
					} catch(SQLException e) {
						System.out.println("Failed to insert new technical staff " + name + ".");
						e.printStackTrace();
					}
					
				}
				
				break;
			case 2: /*Enter a new product associated with the person who made the product, repaired the product if
				it is repaired, or checked the product (400/day).*/
				//Get input
				System.out.println("Enter a pid:");
				int pid = Integer.parseInt(scanner.nextLine());
				System.out.println("Enter the production year (int):");
				int year = Integer.parseInt(scanner.nextLine());
				System.out.println("Enter the production month (int):");
				int month = Integer.parseInt(scanner.nextLine());
				System.out.println("Enter the production day (int):");
				int day = Integer.parseInt(scanner.nextLine());
				Date productionDate = new Date(year, month, day);
				System.out.println("Enter a production time (minutes):");
				int productionTimeMinutes = Integer.parseInt(scanner.nextLine());
				System.out.println("Enter a size:");
				String size = scanner.nextLine();
				System.out.println("Enter a worker name:");
				String workerName = scanner.nextLine();
				System.out.println("Enter a quality controller name:");
				String qcName = scanner.nextLine();
				System.out.println("Enter a technical staff name:");
				String tsName = scanner.nextLine();
				Date repairDate = null;
				if (!tsName.equals("")) {
					System.out.println("Enter the repair year (int):");
					year = Integer.parseInt(scanner.nextLine());
					System.out.println("Enter the repair month (int):");
					month = Integer.parseInt(scanner.nextLine());
					System.out.println("Enter the repair day (int):");
					day = Integer.parseInt(scanner.nextLine());
					repairDate = new Date(year, month, day);
				}
				//Execute Query
				try {
					CallableStatement query1 = dbConnection.prepareCall("{CALL INSERT_NEW_PRODUCT(?,?,?,?,?,?,?,?,?)}");
					query1.setInt(1, pid);
					query1.setDate(2, productionDate);
					query1.setInt(3, productionTimeMinutes);
					query1.setString(4, size);
					query1.setString(5, workerName);
					query1.setString(6, qcName);
					query1.setString(7, tsName);
					query1.setDate(8, repairDate);
					query1.setString(9, "");
					query1.executeUpdate();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println("Enter the product type (1, 2, or 3)");
				int productType = Integer.parseInt(scanner.nextLine());
				switch (productType) {
				case 1:
					System.out.println("Enter the name of the software name for product 1:");
					String softwareName = scanner.nextLine();
					try {
						CallableStatement query1 = dbConnection.prepareCall("INSERT INTO PRODUCT1(PID, SOFTWARE) VALUES (?,?)");
						query1.setInt(1, pid);
						query1.setString(2, softwareName);
						query1.executeUpdate();
						System.out.println("Successfully inserted new product1 with pid " + pid);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case 2:
					System.out.println("Enter the color of the product 2:");
					String color = scanner.nextLine();
					try {
						CallableStatement query1 = dbConnection.prepareCall("INSERT INTO PRODUCT2(PID, COLOR) VALUES (?,?)");
						query1.setInt(1, pid);
						query1.setString(2, color);
						query1.executeUpdate();
						System.out.println("Successfully inserted new product2 with pid " + pid);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case 3:
					System.out.println("Enter the weight of the product 3:");
					int weight = Integer.parseInt(scanner.nextLine());
					try {
						CallableStatement query1 = dbConnection.prepareCall("INSERT INTO PRODUCT3(PID, WEIGHT) VALUES (?,?)");
						query1.setInt(1, pid);
						query1.setInt(2, weight);
						query1.executeUpdate();
						System.out.println("Successfully inserted new product3 with pid " + pid);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
				
				break;
			case 3: //Enter a customer associated with some products (50/day).
				System.out.println("Enter the customer name:");
				name = scanner.nextLine();
				System.out.println("Enter the customer's address");
				address = scanner.nextLine();
				//Execute Query
				try {
					CallableStatement query1 = dbConnection.prepareCall("{CALL INSERT_NEW_CUSTOMER(?,?)}");
					query1.setString(1, name);
					query1.setString(2, address);
					query1.executeUpdate();
					System.out.println("Successfully inserted new customer " + name);
				} catch (SQLException e) {
					System.out.println("Failed to insert new customer " + name);
					e.printStackTrace();
				}
				
				pid = 0;
				while(pid != -1) {
					System.out.println("Enter pid purchased by " + name +  " or -1 to exit:");
					pid = scanner.nextInt();
					if (pid != -1) {
						try {
							CallableStatement query1 = dbConnection.prepareCall("{CALL CUSTOMER_PURCHASE(?,?)}");
							query1.setString(1, name);
							query1.setInt(2, pid);
							query1.executeUpdate();
							System.out.println("Successfully stored " + name + "'s purchase of product " + pid);
						} catch (SQLException e) {
							System.out.println("No such product or customer exists");
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
				break;
			case 4: //Create a new account associated with a product (40/day).
				//Get input
				System.out.println("Enter the account number:");
				int number = Integer.parseInt(scanner.nextLine());
				System.out.println("Enter the product id the this account will track:");
				pid = Integer.parseInt(scanner.nextLine());
				System.out.println("Enter the cost of the product:");
				int cost = Integer.parseInt(scanner.nextLine());
				//Execute Query
				try {
					CallableStatement query1 = dbConnection.prepareCall("{CALL INSERT_NEW_ACCOUNT(?,?,?,?)}");
					query1.setInt(1, number);
					query1.setDate(2, new Date(System.currentTimeMillis()));
					query1.setInt(3, cost);
					query1.setInt(4, pid);
					query1.executeUpdate();
					System.out.println("Successfully created new account " + number);
				} catch (SQLException e) {
					System.out.println("Failed to create new account " + number);
					e.printStackTrace();
				}
				break;
				
			case 5: //Enter a complaint associated with a customer and product (30/day).
				//Get input
				System.out.println("Enter the complaint id:");
				int cid = Integer.parseInt(scanner.nextLine());
				System.out.println("Enter the complaint year (int):");
				year = Integer.parseInt(scanner.nextLine());
				System.out.println("Enter the complaint month (int):");
				month = Integer.parseInt(scanner.nextLine());
				System.out.println("Enter the complaint day (int):");
				day = Integer.parseInt(scanner.nextLine());
				@SuppressWarnings("deprecation")
				Date date = new Date(year, month, day);
				System.out.println("Enter the description of the complaint (max 512 char):");
				String description = scanner.nextLine();
				System.out.println("Enter the expected treatment:");
				String treatment = scanner.nextLine();
				System.out.println("Enter the customer's name:");
				String customerName = scanner.nextLine();
				System.out.println("Enter the product id:");
				pid = Integer.parseInt(scanner.nextLine());
				//Execute Query
				try {
					CallableStatement query1 = dbConnection.prepareCall("{CALL INSERT_NEW_COMPLAINT(?,?,?,?,?,?)}");
					query1.setInt(1, cid);
					query1.setDate(2, date);
					query1.setString(3, description);
					query1.setString(4, treatment);
					query1.setString(5, customerName);
					query1.setInt(6, pid);
					query1.executeUpdate();
					System.out.println("Successfully created new complaint " + cid + " with product " + pid);
				} catch (SQLException e) {
					System.out.println("Failed to create new complaint " + cid + " with product " + pid);
					e.printStackTrace();
				}
				break;
				
			case 6: //Enter an accident associated with appropriate employee and product (1/week).
				//Get input
				System.out.println("Enter the new accident number:");
				number = Integer.parseInt(scanner.nextLine());
				System.out.println("Enter the year of the accident:");
				year = Integer.parseInt(scanner.nextLine());
				System.out.println("Enter the month of the accident:");
				month = Integer.parseInt(scanner.nextLine());
				System.out.println("Enter the day of the accident:");
				day =Integer.parseInt(scanner.nextLine());
				date = new Date(year, month, day);
				System.out.println("Enter the number of work days lost:");
				int daysLost = Integer.parseInt(scanner.nextLine());
				System.out.println("Enter the product id:");
				pid = Integer.parseInt(scanner.nextLine());
				System.out.println("Enter the employee's name:");
				name = scanner.nextLine();
				//Execute Query
				try {
					CallableStatement query1 = dbConnection.prepareCall("{CALL INSERT_NEW_ACCIDENT(?,?,?,?,?)}");
					query1.setInt(1, number);
					query1.setDate(2, date);
					query1.setInt(3, daysLost);
					query1.setInt(4, pid);
					query1.setString(5, name);
					query1.executeUpdate();
					System.out.println("Successfully inserted new accident " + number);
				} catch (SQLException e) {
					System.out.println("Failed to insert new accident " + number);
					e.printStackTrace();
				}
				break;
				
			case 7: //Retrieve the date produced and time spent to produce a particular product (100/day).
				//Get input
				System.out.println("Enter the product id:");
				pid = Integer.parseInt(scanner.nextLine());
				//Execute Query
				try {
					CallableStatement query1 = dbConnection.prepareCall("SELECT PRODUCTION_DATE, PRODUCTION_TIME_MINUTES FROM PRODUCT WHERE PID = ?");
					query1.setInt(1, pid);
					ResultSet rs = query1.executeQuery();
					rs.next();
					productionDate = rs.getDate("PRODUCTION_DATE");
					productionTimeMinutes = rs.getInt("PRODUCTION_TIME_MINUTES");
					System.out.println("Production date: " + productionDate + "\nProduction time (minutes): " + productionTimeMinutes);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
				
			case 8: //Retrieve all products made by a particular worker (2000/day).
				//Get input
				System.out.println("Enter a workers name:");
				name = scanner.nextLine();
				//Execute Query
				try {
					CallableStatement query1 = dbConnection.prepareCall("SELECT PID FROM PRODUCT WHERE \"W.NAME\" = ?");
					query1.setString(1, name);
					ResultSet rs = query1.executeQuery();
					System.out.println("Here are the products made by " + name);
					while (rs.next()) {
						pid = rs.getInt("PID");
						System.out.println(pid);
					}
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
				
			case 9: /*Retrieve the total number of errors a particular quality controller made. This is the total
				number of products certified by this controller and got some complaints (400/day).*/
				//Get input
				System.out.println("Enter a quality controller's name:");
				name = scanner.nextLine();
				//Execute Query
				try {
					CallableStatement query1 = dbConnection.prepareCall("SELECT COUNT(*) FROM COMPLAINT WHERE \"PRODUCT.PID\" IN "
							+ "(SELECT \"PID\" FROM PRODUCT WHERE \"QC.NAME\"=?)");
					query1.setString(1, name);
					ResultSet rs = query1.executeQuery();
					rs.next();
					int count = rs.getInt(1);
					System.out.println(name + " made " + count + " errors.");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
				
			case 10: /*Retrieve the total costs of the products in the product3 category which were repaired at the
						request of a particular quality controller (40/day).*/
				//Get input
				System.out.println("Enter a quality controller's name:");
				name = scanner.nextLine();
				//Execute Query
				try {
					CallableStatement query1 = dbConnection.prepareCall("SELECT SUM(CASE WHEN NUMBER IN "
							 + "(SELECT NUMBER FROM ACCOUNT WHERE 'PRODUCT.PID' IN "
							 + "(SELECT PID FROM PRODUCT WHERE 'QC.NAME=?)"
							 + ") THEN COST ELSE 0 END) FROM ACCOUNT");
					query1.setString(1, name);
					ResultSet rs = query1.executeQuery();
					rs.next();
					cost = rs.getInt(1);
					System.out.println("Total cost: " + cost);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
				
			case 11: /*Retrieve all customers who purchased all products of a particular color (5/month).*/
				//Get input
				System.out.println("Enter the color of the product2:");
				String color = scanner.nextLine();
				//Execute Query
				try {
					CallableStatement query1 = dbConnection.prepareCall("SELECT \"CUSTOMER.NAME\" FROM PRODUCT WHERE "
							+ "PID IN (SELECT PID FROM PRODUCT2 WHERE COLOR=?)");
					query1.setString(1, color);
					ResultSet rs = query1.executeQuery();
					System.out.println("Here are the customers who purchased a " + color + " product2:");
					while (rs.next()) {
						customerName = rs.getString(1);
						System.out.println(customerName);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 12: /*Retrieve the total number of work days lost due to accidents in repairing the products which
					got complaints (1/month).*/
				try {
					CallableStatement query1 = dbConnection.prepareCall("SELECT SUM(CASE WHEN 'PRODUCT.PID' IN "
							+ "(SELECT 'PRODUCT.PID' FROM COMPLAINT) THEN DAYS_LOST ELSE 0 END) FROM ACCIDENT");
					ResultSet rs = query1.executeQuery();
					rs.next();
					int workDaysLost = rs.getInt(1);
					System.out.println("Work days lost: " + workDaysLost);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 13: /*Retrieve all customers who are also workers (10/month).*/
				try {
					CallableStatement query1 = dbConnection.prepareCall("SELECT \"NAME\" FROM CUSTOMER WHERE NAME IN "
							+ "(SELECT \"EMPLOYEE.NAME\" FROM WORKER)");
					ResultSet rs = query1.executeQuery();
					System.out.println("Here are all of the customers who are also workers:");
					while (rs.next()) {
						System.out.println(rs.getString("name"));
					}
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 14: /*Retrieve all the customers who have purchased the products made or certified or repaired by
					themselves (5/day).*/
				try {
					CallableStatement query1 = dbConnection.prepareCall("SELECT \"CUSTOMER.NAME\" FROM PRODUCT"
							+ " WHERE \"CUSTOMER.NAME\" = \"W.NAME\" "
							+ " OR \"CUSTOMER.NAME\" = \"QC.NAME\" "
							+ " OR \"CUSTOMER.NAME\" = \"TS.NAME\" ");
					ResultSet rs = query1.executeQuery();
					System.out.println("The customers who have purchased the products made or certified or "
							+ "repaired by themselves:");
					while (rs.next()) {
						System.out.println(rs.getString(1));
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 15: /*Retrieve the average cost of all products made in a particular year*/
				//Get input
				System.out.println("Enter a year (yyyy):");
				year = Integer.parseInt(scanner.nextLine());
				Date startDate = new Date(year, 1, 1);
				Date endDate = new Date(year + 1, 1, 1);
				try {
					CallableStatement query1 = dbConnection.prepareCall("SELECT AVG(COST) FROM \"ACCOUNT\" WHERE DATE_EST BETWEEN ? AND ?");
					query1.setDate(1, startDate);
					query1.setDate(2, endDate);
					ResultSet rs = query1.executeQuery();
					rs.next();
					cost = rs.getInt(1);
					System.out.println("Average cost of products made during " + year + ": " + cost);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 16: /*Switch the position between a technical staff and a quality controller (1/ 3 months).*/
				//Get input
				System.out.println("Enter a quality controller's name:");
				name = scanner.nextLine();
				try {
					CallableStatement query1 = dbConnection.prepareCall("");
					query1.setString(1, name);
					ResultSet rs = query1.executeQuery();
					rs.next();
					cost = rs.getInt(1);
					System.out.println("Cost: " + cost);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 17: /*Delete all accidents whose dates are in some range (1/day).*/
				//Get input
				System.out.println("Enter the starting date year (yyyy):");
				year = Integer.parseInt(scanner.nextLine());
				System.out.println("Enter the starting date month (mm):");
				month = Integer.parseInt(scanner.nextLine());
				System.out.println("Enter the starting date day (dd):");
				day = Integer.parseInt(scanner.nextLine());
				startDate = new Date(year, month, day);
				
				System.out.println("Enter the ending date year (yyyy):");
				year = Integer.parseInt(scanner.nextLine());
				System.out.println("Enter the ending date month (mm):");
				month = Integer.parseInt(scanner.nextLine());
				System.out.println("Enter the ending date day (dd):");
				day = Integer.parseInt(scanner.nextLine());
				endDate = new Date(year, month, day);
				try {
					CallableStatement query1 = dbConnection.prepareCall("DELETE FROM ACCIDENT WHERE \"DATE\" BETWEEN ? AND ?");
					query1.setDate(1, startDate);
					query1.setDate(2, endDate);
					query1.executeQuery();
					System.out.println("Deleted accidents between " + startDate.toString() + " and " + endDate.toString());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 18: //Import customer information from a file.
				//Get input
				System.out.println("Enter the file name containing the customer information:");
				String fileName = scanner.nextLine();
				try {
					BufferedReader in = new BufferedReader(new FileReader(fileName));
					String line;
					while((line = in.readLine()) != null)
					{
					    String[] items = line.split("\\s");
					    name = items[0];
					    address = "";
					    for (int i = 1; i < items.length; i++) {
					    	address += items[i] + " ";
					    }
					    try {
							CallableStatement query1 = dbConnection.prepareCall("{CALL INSERT_NEW_CUSTOMER(?,?)}");
							query1.setString(1, name);
							query1.setString(2, address);
							query1.executeUpdate();
							System.out.println("Successfully inserted new customer " + name);
						} catch (SQLException e) {
							System.out.println("Failed to insert new customer " + name);
							e.printStackTrace();
						}
					}
					in.close();
				} catch (IOException e) {
					System.out.println("Could not open file " + fileName);
					e.printStackTrace();
				}
				break;
			case 19: //Export customer information to a file.
				//Get input
				System.out.println("Enter the file name to output the customer information to:");
				fileName = scanner.nextLine();
				try {
					CallableStatement query1 = dbConnection.prepareCall("SELECT * FROM CUSTOMER");
					ResultSet rs = query1.executeQuery();
					PrintWriter writer = new PrintWriter(fileName, "UTF-8");
					while (rs.next()) {
						writer.print(rs.getString(1) + " " + rs.getString(2) + "\n");
					}
					writer.close();
					System.out.println("Successfully wrote all customer information to " + fileName);
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					System.out.println("Could not open file " + fileName);
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 20: //Quit
				System.out.println("Closing Future Inc DB Driver...");
				continu = false;
				break;
			}
		}
		System.out.println("Closed");
	}
	
	private static int start(Scanner scanner) {
		String introQueries = 
				"Please choose from one of the following options:\n"
				+ "1) Enter a new employee (2/month).\n"
				+ "2) Enter a new product associated with the person who made the product, repaired the product if\n"
				+ "it is repaired, or checked the product (400/day).\n"
				+ "3) Enter a customer associated with some products (50/day).\n"
				+ "4) Create a new account associated with a product (40/day).\n"
				+ "5) Enter a complaint associated with a customer and product (30/day).\n"
				+ "6) Enter an accident associated with appropriate employee and product (1/week).\n"
				+ "7) Retrieve the date produced and time spent to produce a particular product (100/day).\n"
				+ "8) Retrieve all products made by a particular worker (2000/day).\n"
				+ "9) Retrieve the total number of errors a particular quality controller made. This is the total\n"
				+ "number of products certified by this controller and got some complaints (400/day).\n"
				+ "10) Retrieve the total costs of the products in the product3 category which were repaired at the\n"
				+ "request of a particular quality controller (40/day).\n"
				+ "11) Retrieve all customers who purchased all products of a particular color (5/month).\n"
				+ "12) Retrieve the total number of work days lost due to accidents in repairing the products which\n"
				+ "got complaints (1/month).\n"
				+ "13) Retrieve all customers who are also workers (10/month).\n"
				+ "14) Retrieve all the customers who have purchased the products made or certified or repaired by\n"
				+ "themselves (5/day).\n"
				+ "15) Retrieve the average cost of all products made in a particular year (5/day).\n"
				+ "16) Switch the position between a technical staff and a quality controller (1/ 3 months).\n"
				+ "17) Delete all accidents whose dates are in some range (1/day).\n"
				+ "18) Import customer information from a file.\n"
				+ "19) Export customer information to a file.\n"
				+ "20) Quit\n";
		System.out.println(introQueries);
		return Integer.parseInt(scanner.nextLine());
	}
}
