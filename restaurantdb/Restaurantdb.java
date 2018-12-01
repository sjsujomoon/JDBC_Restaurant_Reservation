
import java.util.Scanner;
import java.sql.*;

/**
 * @author Hung Tang, Faith Chau, Jeong Moon
 * @description the application is about restaurant reservation system
 *
 */
public class Restaurantdb {

	Connection conn;

	public Restaurantdb() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			// DriverManager.useSSL=false;
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/RestaurantReservation?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&autoReconnect=true&useSSL=false",
					"root", "faith114");

			// triggers not working yet
			// serverOffTrigger();
			// serverOnTrigger();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	// public void serverOffTrigger() throws SQLException {
	// String trigger = "CREATE TRIGGER ServerOff\n" + "AFTER Update ON Employee\n"
	// + "FOR EACH ROW\n"
	// + "WHEN NEW.isOff = 1\n" + "BEGIN\n" + "\tUPDATE Restaurant\n"
	// + "\tSET subServerID = OLD.sID and sID = ( select min(E1.sID) " + "from
	// Employee E1 "
	// + "where E1.isOff = 0) " + "WHERE sID = NEW.sID; " + "END;";

	// Statement stmt = conn.createStatement();
	// stmt.execute(trigger);
	// }

	// public void serverOnTrigger() throws SQLException {
	// conn.createStatement().execute("DROP TRIGGER IF EXISTS ServerOn");

	// StringBuffer trigger = new StringBuffer();
	// trigger.append("CREATE TRIGGER ServerOn AFTER Update ON Employee ");
	// trigger.append("FOR EACH ROW WHEN NEW.isOff = false\nBEGIN");
	// trigger.append("UPDATE Restaurant SET subServerID = NULL and sID = NEW.sID
	// ");
	// trigger.append("WHERE subServerID = NEW.sID;\n");
	// trigger.append("END; ");

	// conn.createStatement().execute(trigger.toString());
	// }

	public void closeConnection() {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			System.out.println("Query Error:" + e.getStackTrace());
		}
	}

	public void displayTable(String tableName) {
		try {
			String query = "SELECT * FROM " + tableName;
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			int colNumber = rsmd.getColumnCount();

			System.out.println("<< " + tableName + " DB >>");
			while (rs.next()) {
				for (int i = 1; i <= colNumber; i++) {
					if (i > 1)
						System.out.print("\t");
					String colVal = rs.getString(i);
					System.out.print(rsmd.getColumnName(i) + ": " + colVal);
				}
				System.out.println("");
			}
			System.out.println();
		} catch (SQLException e) {
			System.out.println("Query Error: " + e.getStackTrace());
		}
	}

	public void restaurantMenu() {
		displayTable("restaurant");
		Scanner scanner = new Scanner(System.in);
		boolean endFlag = false;
		while (endFlag != true) {
			System.out.println("<<Restaurant Table Menu>>");
			System.out.println("[A] Insert Restaurant Table into DB");
			System.out.println("[B] Delete Restaurant Table from DB");
			System.out.println("[C] Update Restaurant Table Server Back");
			System.out.println("[M] MainMenu");
			String input = scanner.nextLine().toLowerCase();
			switch (input) {
			case "a":
				insertRestaurantMenu();
				break;
			case "b":
				deleteRestaurantMenu();
				break;
			case "c":
				updateRestaurantServerBackMenu();
				break;
			case "m":
				endFlag = true;
				break;
			default:
				menuError();
			}
		}
	}

	public int insertRestaurant(boolean occupied, int numOfSeats, int sID) {
		int result = 0;
		try {
			PreparedStatement pstmt = conn
					.prepareStatement("INSERT INTO Restaurant (occupied, numOfSeats, sID) VALUES (?,?,?)");
			pstmt.setBoolean(1, occupied);
			pstmt.setInt(2, numOfSeats);
			pstmt.setInt(3, sID);
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Query Error: " + e.getStackTrace());
		}
		return result;
	}

	public void insertRestaurantMenu() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("<<Insert Restaurant Table>>\n");
		System.out.println("Occupied?");
		System.out.println("[A] Yes");
		System.out.println("[B] No");
		String input = scanner.nextLine().toLowerCase();
		boolean occupied = input.equals("a") ? true : false;

		System.out.println("How many seats?");
		int numOfSeats = scanner.nextInt();
		String dummy = scanner.nextLine(); // Consumes "\n"

		System.out.println("Server ID?");
		int sID = scanner.nextInt();
		dummy = scanner.nextLine(); // Consumes "\n"

		if (insertRestaurant(occupied, numOfSeats, sID) != 0)
			success();
		else
			fail();
	}

	public int deleteRestaurant(int tID) {
		int result = 0;
		try {
			PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Restaurant WHERE tID=?");
			pstmt.setInt(1, tID);
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Query Error: " + e.getStackTrace());
		}
		return result;
	}

	public void deleteRestaurantMenu() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("<<Delete Restaurant Table>>\n");

		System.out.println("Table ID?");
		int tID = scanner.nextInt();
		String dummy = scanner.nextLine(); // Consumes "\n"

		if (deleteRestaurant(tID) != 0)
			success();
		else
			fail();

	}

	public int updateRestaurantServerBack(int tID) {
		int result = 0;
		try {
			PreparedStatement pstmt = conn.prepareStatement("Update Restaurant set subServerID=? where tID=?");
			pstmt.setNull(1, 1);
			pstmt.setInt(2, tID);
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Query Error: " + e.getStackTrace());
		}
		return result;
	}

	public void updateRestaurantServerBackMenu() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("<<Update Restaurant DB>>\n");

		System.out.println("Table ID?");
		int tID = scanner.nextInt();
		String dummy = scanner.nextLine(); // Consumes "\n"

		if (updateRestaurantServerBack(tID) != 0)
			success();
		else
			fail();
	}

	public void employeeMenu() {
		displayTable("employee");
		Scanner scanner = new Scanner(System.in);
		boolean endFlag = false;
		while (endFlag != true) {
			System.out.println("<<Employee Menu>>");
			System.out.println("[A] Insert Employee into DB");
			System.out.println("[B] Delete Employee from DB");
			System.out.println("[C] Update Employee IsOff");
			System.out.println("[D] Find Employee(s) W/O Assigned Table");
			System.out.println("[M] MainMenu");
			String input = scanner.nextLine().toLowerCase();
			switch (input) {
			case "a":
				insertEmployeeMenu();
				break;
			case "b":
				deleteEmployeeMenu();
				break;
			case "c":
				updateEmployeeIsOffMenu();
				break;
			case "d":
				findEmployeeWOTable();
				break;
			case "m":
				endFlag = true;
				break;
			default:
				menuError();
			}
		}
	}

	public void findEmployeeWOTable() {
		try {
			Statement stmt = conn.createStatement();
			String query = "select sID, name from employee e1 WHERE not exists (select sid from Restaurant where sid = e1.sid)";
			ResultSet rs = stmt.executeQuery(query);

			if (rs.next()) {
				System.out.println("<< Employee(s) without Assigned Table >>");
				System.out.println("sID: " + rs.getInt("sID") + "\t" + "name: " + rs.getString("name"));
			}

			while (rs.next()) {
				System.out.println("sID: " + rs.getInt("sID") + "\t" + "name: " + rs.getString("name"));
			}
			System.out.println();
		} catch (SQLException e) {
			System.out.println("Query Error: " + e.getStackTrace());
		}
	}

	public int insertEmployee(String name, boolean isOFF, int phoneNum) {
		int result = 0;
		try {
			PreparedStatement pstmt = conn
					.prepareStatement("INSERT INTO Employee (name, isOff, phoneNum) VALUES (?,?,?)");
			pstmt.setString(1, name);
			pstmt.setBoolean(2, isOFF);
			pstmt.setInt(3, phoneNum);
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Query Error: " + e.getStackTrace());
		}
		return result;
	}

	public void insertEmployeeMenu() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("<<Insert Employee>>\n");

		System.out.println("Employee Name?");
		String name = scanner.nextLine();

		System.out.println("Is the employee off today?");
		System.out.println("[A] Yes");
		System.out.println("[B] No");

		String ans = scanner.nextLine().toLowerCase();
		boolean isOff = ans.equals("a") ? true : false;

		System.out.println("Employee Phone Number?");
		int phoneNum = scanner.nextInt();
		String dummy = scanner.nextLine(); // Consumes "\n"

		if (insertEmployee(name, isOff, phoneNum) != 0)
			success();
		else
			fail();
	}

	public int deleteEmployee(String name, int phoneNum) {
		int result = 0;
		try {
			PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Employee WHERE name=? and phoneNum=?");
			pstmt.setString(1, name);
			pstmt.setInt(2, phoneNum);
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Query Error: " + e.getStackTrace());
		}
		return result;
	}

	public void deleteEmployeeMenu() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("<<Delete Employee>>\n");

		System.out.println("Employee Name?");
		String name = scanner.nextLine();

		System.out.println("Employee Phone Number?");
		int phoneNum = scanner.nextInt();
		String dummy = scanner.nextLine(); // Consumes "\n"

		if (deleteEmployee(name, phoneNum) != 0)
			success();
		else
			fail();

	}

	public int updateEmployee(String name, boolean isOFF, int phoneNum) {
		int result = 0;
		try {
			PreparedStatement pstmt = conn.prepareStatement("Update Employee set isOff=? where name=? and phoneNum=?");
			pstmt.setBoolean(1, isOFF);
			pstmt.setString(2, name);
			pstmt.setInt(3, phoneNum);
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Query Error: " + e.getStackTrace());
		}
		return result;
	}

	public void updateEmployeeIsOffMenu() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("<<Update Employee IsOff>>\n");

		System.out.println("Employee Name?");
		String name = scanner.nextLine();

		System.out.println("Employee Phone Number?");
		int phoneNum = scanner.nextInt();
		String dummy = scanner.nextLine(); // Consumes "\n"

		System.out.println("[A] Switched to Off, IsOff = 1");
		System.out.println("[B] Switched to Back to Work, IsOff = 0");

		String ans = scanner.nextLine().toLowerCase();
		boolean isOff = ans.equals("a") ? true : false;

		if (updateEmployee(name, isOff, phoneNum) != 0)
			success();
		else
			fail();
	}

	public static void menuError() {
		System.out.println("Please Choose Valid Option");
	}

	public static void success() {
		System.out.println("Successfully Done");
	}

	public static void fail() {
		System.out.println("Failed");
	}

	public void createCustomer() {
		System.out.println("Can we have your first name and last name, please?");
		Scanner firstN = new Scanner(System.in);
		System.out.print("Please enter your First Name: ");
		String firstName = firstN.nextLine();
		Scanner lastN = new Scanner(System.in);
		System.out.print("Please enter your Last Name: ");
		String lastName = lastN.nextLine();
		System.out.println("is your name " + firstName + " " + lastName + " correct?");
		System.out.println("[A] Yes");
		System.out.println("[B] No");
		Scanner optionScan = new Scanner(System.in);
		String cusInput = optionScan.nextLine();
		if (cusInput.equals("A".toLowerCase())) {
			System.out.println("Please enter your phone number: ");
			Scanner phoneN = new Scanner(System.in);
			String phoneNumber = phoneN.next(); // Will match with database later

			try {
				PreparedStatement stmt = conn.prepareStatement("INSERT INTO Customer(name, phoneNum) VALUES (?,?)");
				stmt.setString(1, firstName + " " + lastName);
				stmt.setString(2, phoneNumber);
				stmt.executeUpdate();
			} catch (Exception e) {
				System.out.println(e);
			}

		}

	}

	public void returnCustomer() {

		System.out.println("Can we have your phone number, please? ");
		Scanner phoneN = new Scanner(System.in);
		String phoneNumber = phoneN.next(); // Will match with database later
		reservationOption();

	}

	public void customerPrompt() {
		System.out.println("Are you new customer?");
		System.out.println("Please select one option: ");
		System.out.println("[A] Yes");
		System.out.println("[B] No");
		Scanner customerScan = new Scanner(System.in);
		String cusInput = customerScan.nextLine().toLowerCase();
		if (cusInput.equals("a")) {
			System.out.println("Thank you for choosing DB Restaurant");
			createCustomer();
		} else if (cusInput.equals("b")) {
			System.out.println("Welcome back, thank you for choosing DB restaurant");
			returnCustomer();
		} else {
			System.out.println("That's not a valid answer");
		}

	}

	public void reservationOption() {
		System.out.println("Please select one of the options below: ");
		System.out.println("[A] Make a new reservation");
		System.out.println("[B] Current drop in ");
		System.out.println("[C] Update personal information ");
		Scanner selectOption = new Scanner(System.in);
		String optionS = selectOption.nextLine();

	}

	/**
	 * main method prompting the user
	 * 
	 * @param args
	 */
	// public static void main(String[] args) {
	//
	// }

}
