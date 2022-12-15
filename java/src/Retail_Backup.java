/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
import java.sql.Timestamp;
import java.util.Date;
/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Retail {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Retail shop
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Retail(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Retail

   // Method to calculate euclidean distance between two latitude, longitude pairs. 
   public double calculateDistance (double lat1, double long1, double lat2, double long2){
      double t1 = (lat1 - lat2) * (lat1 - lat2);
      double t2 = (long1 - long2) * (long1 - long2);
      return Math.sqrt(t1 + t2); 
   }
   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Retail.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if
      Greeting();
      Retail esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Retail object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Retail (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default :
                  System.out.print("\033[H\033[2J");
                  System.out.flush(); 
                  System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
	      System.out.print("\033[H\033[2J");
              System.out.flush();
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Stores within 30 miles");
                System.out.println("2. View Product List");
                System.out.println("3. Place a Order");
                System.out.println("4. View 5 recent orders");
		
		if(esql.executeQuery(String.format("SELECT DISTINCT u.name FROM Users u, Store s WHERE s.managerID = userID AND u.name = '%s'", authorisedUser)) != 0)
		{
                    //the following functionalities basically used by managers
                    System.out.println("5. Update Product");
                    System.out.println("6. View 5 recent Product Updates Info");
                    System.out.println("7. View 5 Popular Items");
                    System.out.println("8. View 5 Popular Customers");
                    System.out.println("9. Place Product Supply Request to Warehouse");
		}
                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewStores(esql); break;
                   case 2: viewProducts(esql); break;
                   case 3: placeOrder(esql); break;
                   case 4: viewRecentOrders(esql); break;
                   case 5: updateProduct(esql); break;
                   case 6: viewRecentUpdates(esql, authorisedUser); break;
                   case 7: viewPopularProducts(esql, authorisedUser); break;
                   case 8: viewPopularCustomers(esql, authorisedUser); break;
                   case 9: placeProductSupplyRequests(esql, authorisedUser); break;

                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(Retail esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
	 if(esql.executeQuery(String.format("SELECT userID FROM Users WHERE name = '%s';", name)) != 0)
	 {
 	    System.out.println(String.format("The user %s already exists in the database."));
	    return;
	 }
         System.out.print("\tEnter password: ");
         String password = in.readLine();
	 if(password.length() < 3)
	 {
	    System.out.println("Password must be greater than 3 characters in length.");
	    return;
	 }
         System.out.print("\tEnter latitude (0.0 - 100.0): ");   
         String latitude = in.readLine();       //enter lat value between [0.0, 100.0]
         System.out.print("\tEnter longitude (0.0 - 100.0): ");  //enter long value between [0.0, 100.0]
         String longitude = in.readLine();
	 
	 int lat_int = Integer.parseInt(latitude);
	 int lon_int = Integer.parseInt(longitude);
	 	
	 // Check in range
	 if(lat_int < 0 || lat_int > 100 || lon_int < 0 || lon_int > 100)
	 {
	    System.out.println(String.format("The coordinates (%s, %s) are out of scope.", latitude, longitude));
	    return;
	 }
         
         String type="Customer";

			String query = String.format("INSERT INTO USERS (name, password, latitude, longitude, type) VALUES ('%s','%s', %s, %s,'%s')", name, password, latitude, longitude, type);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Retail esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USERS WHERE name = '%s' AND password = '%s'", name, password);
         int userNum = esql.executeQuery(query);
	 if (userNum > 0)
		return name;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here

   public static void viewStores(Retail esql) {}
   public static void viewProducts(Retail esql) {}
   public static void placeOrder(Retail esql) {}
   public static void viewRecentOrders(Retail esql) {}
   public static void updateProduct(Retail esql) {}
   public static void viewRecentUpdates(Retail esql, String manager) 
   {
      try
        {
           String check = String.format("SELECT DISTINCT u.name FROM Users u, Store s WHERE s.managerID = userID AND u.name = '%s'", manager);
           //  Check to see if user is actually a manager
           if(esql.executeQuery(check) == 0 && !(manager.equals("Admin")))
           {
              System.out.println("You are not authorized to do such action...");
              return;
           }
	   System.out.print("\033[H\033[2J");  
           System.out.flush();
	   String query = "";
	   if(manager.equals("Admin"))
           {
	       query = "SELECT s.name AS store, pu.productName AS product, pu.updatedOn, w.WarehouseID FROM Users u, Store s, ProductUpdates pu, Warehouse w WHERE u.userID = pu.managerID AND s.storeID = pu.storeID ORDER BY pu.updateNumber DESC LIMIT 5;";
           }else
	   {
               query = String.format("SELECT s.name AS store, pu.productName AS product, pu.updatedOn, w.WarehouseID FROM Users u, Store s, ProductUpdates pu, Warehouse w WHERE u.userID = pu.managerID AND u.name = '%s' AND s.storeID = pu.storeID ORDER BY pu.updateNumber DESC LIMIT 5;", manager);
           }
           List<List<String>> output = esql.executeQueryAndReturnResult(query);
	   System.out.println(String.format("\n\nRecent updates for %s", manager));
           System.out.println(" _____________________________________________________________");
           System.out.println("| Store | Product              | Timestamp              | WH# |");
	   System.out.println("|=======+======================+========================+=====|");
	   for(int i = 0; i < 5; i++)
	   {
	      String store = output.get(i).get(0).replaceAll(" ", "");
	      String product = output.get(i).get(1).replaceAll(" ", "");
	      String date = output.get(i).get(2);
              String warehouse = output.get(i).get(3);
	      String statement = String.format("| %-5s | %-20s | %-22s | %-3s |", store, product, date, warehouse);
	      System.out.println(statement);
	   }
	   System.out.println("===============================================================\n");
	}catch(Exception e)
        {
            System.err.println(e.getMessage());
        }
   }
   public static void viewPopularProducts(Retail esql, String manager) 
   {
      try
      {
	String check = String.format("SELECT DISTINCT u.name FROM Users u, Store s WHERE s.managerID = userID AND u.name = '%s'", manager);

	if(esql.executeQuery(check) == 0 && !(manager.equals("Admin")))
        {
           System.out.println("You are not authorized to do such action...");
           return;
        }
	System.out.print("\033[H\033[2J");
        System.out.flush();
	String query = "";
	if(manager.equals("Admin"))
	{
	    query = "SELECT p.productName AS product, s.name AS store, o.unitsOrdered FROM Product p, Store s, Orders o, Users u WHERE p.storeID = s.storeID AND p.storeID = o.storeID AND p.productName = o.productName  AND s.managerID = u.userID ORDER BY o.unitsOrdered DESC LIMIT 5;";
	}
	else
	{
        query = String.format("SELECT p.productName AS product, s.name AS store, o.unitsOrdered FROM Product p, Store s, Orders o, Users u WHERE p.storeID = s.storeID AND p.storeID = o.storeID AND p.productName = o.productName  AND s.managerID = u.userID AND u.name = '%s' ORDER BY o.unitsOrdered DESC LIMIT 5;", manager);
        }
      List<List<String>> output = esql.executeQueryAndReturnResult(query);
      System.out.println(String.format("\n\nPopular Items For %s", manager));
           System.out.println(" ________________________________________");
	   System.out.println("| Store | Product              | Units   |");
	   System.out.println("|=======+======================+=========|");
           for(int i = 0; i < 5; i++)
           {
              String product = output.get(i).get(0).replaceAll(" ", "");
              String store = output.get(i).get(1).replaceAll(" ", "");
              String units = output.get(i).get(2).replaceAll(" ", "");
              String statement = String.format("| %-5s | %-20s | %-7s |",store,product,units);
              System.out.println(statement);
           }
           System.out.println("==========================================\n");
      }
      catch(Exception e)
      {
         System.err.println(e.getMessage());
      }
   }
   public static void viewPopularCustomers(Retail esql, String manager) 
   {
      try
      {
         String check = String.format("SELECT DISTINCT u.name FROM Users u, Store s WHERE s.managerID = userID AND u.name = '%s'", manager);

         if(esql.executeQuery(check) == 0 && !manager.equals("Admin"))
         {
            System.out.println("You are not authorized to do such action...");
            return;
         }
	 System.out.print("\033[H\033[2J");
         System.out.flush();
         String query = "";
	 if(manager.equals("Admin"))
	 {
	     query = "SELECT u.name, s.name, SUM(o.unitsOrdered) AS orders FROM Users u, Users manager , Store s, Orders o WHERE s.managerID = manager.userID AND u.userID = o.customerID AND o.storeID = s.storeID GROUP BY u.name, s.name ORDER BY orders DESC LIMIT 5;";
	 }else
	 {
	     query = String.format("SELECT u.name, s.name, SUM(o.unitsOrdered) AS orders FROM Users u, Users manager , Store s, Orders o WHERE s.managerID = manager.userID AND manager.name = '%s' AND u.userID = o.customerID AND o.storeID = s.storeID GROUP BY u.name, s.name ORDER BY orders DESC LIMIT 5;", manager);
         }
	 List<List<String>> output = esql.executeQueryAndReturnResult(query);
        System.out.println(String.format("\n\nPopular Customers For %s", manager));
        System.out.println(" _________________________________________");
        System.out.println("| Store | Customer             | Orders   |");
        System.out.println("|=======+======================+==========|");
        for(int i = 0; i < 5; i++)
        {
           String customer = output.get(i).get(0).replaceAll(" ", "");
           String store = output.get(i).get(1).replaceAll(" ", "");
           String orders = output.get(i).get(2).replaceAll(" ", "");
           String statement = String.format("| %-5s | %-20s | %-8s |",store,customer,orders);
           System.out.println(statement);
        }
        System.out.println("===========================================\n");
      }
      catch(Exception e)
      {
         System.err.println(e.getMessage());
      }
   }
   public static void placeProductSupplyRequests(Retail esql, String manager) 
   {
      try
      {
         //  Check if user is able to run this action
	 String check = String.format("SELECT DISTINCT u.name FROM Users u, Store s WHERE s.managerID = userID AND u.name = '%s'", manager);
         if(esql.executeQuery(check) == 0 && !manager.equals("Admin"))
         {
            System.out.println("You are not authorized to do such action...");
            return;
         }
         System.out.print("\033[H\033[2J");
         System.out.flush();
         //  Check for storeID validity
         //  Add stores managed by user
         System.out.println("\nStores managed by you:\n====================================");
         esql.executeQueryAndPrintResult(String.format("SELECT s.storeID, s.name FROM Store s, Users u WHERE u.name = '%s' AND u.userID = s.managerID", manager));	 
         System.out.println("====================================\n");
	 System.out.println("Insert Store ID: ");
	 String store = in.readLine();
	 if(esql.executeQuery(String.format("SELECT s.storeID FROM Store s, Users u WHERE s.managerID = u.userID AND u.name = '%s' AND s.storeID = %s", manager , store)) == 0)
	 {
	    System.out.println(String.format("This store does not exist or is not managed by you. Exiting..."));
            return;  
	 }
	 List<List<String>> output = esql.executeQueryAndReturnResult(String.format("SELECT name FROM Store WHERE storeID = %s", store));
         String storeName = output.get(0).get(0);
	 
	 System.out.print("\033[H\033[2J");
         System.out.flush();
	 System.out.print("Products sold at store ");
	 System.out.println(storeName);
	 System.out.println("====================================");
         esql.executeQueryAndPrintResult(String.format("SELECT p.productName, p.numberOfUnits FROM Product p, Store s, Users u WHERE u.name = '%s' AND u.userID = s.managerID AND s.storeID = %s AND s.storeID = p.storeID;", manager, store));
         System.out.println("====================================\n");
	 System.out.println("Insert Product: ");
         String product = in.readLine();
	 if(esql.executeQuery(String.format("SELECT DISTINCT p.productName, s.name FROM Product p, Store s, Users u WHERE u.userID = s.managerID AND s.storeID = p.storeID AND u.name = '%s' AND s.storeID = %s AND p.productName = '%s'", manager, store, product)) == 0){
	    System.out.print("\033[H\033[2J");
            System.out.flush();
            System.out.print("This store does not hold the product: ");
            System.out.println(product);
	    return;
	 }
         
	 System.out.print("\033[H\033[2J");
         System.out.flush();

	 // Get warehouses, and calculate distance from store
	 output = esql.executeQueryAndReturnResult("SELECT w.WarehouseID, w.latitude, w.longitude FROM Warehouse w;");
	 List<List<String>> store_loc =  esql.executeQueryAndReturnResult(String.format("SELECT latitude, longitude FROM Store WHERE storeID = %s;", store));
	 
	 System.out.println("\nAvailable Warehouses:\n __________________ ");
	 System.out.println("| WH# | Distance   |");
	 System.out.println("|=====+============|");
	 for(int i = 0; i < output.size(); i++)
	 {
	    System.out.println(String.format("| %-3s | %-10f |", output.get(i).get(0), esql.calculateDistance(Double.parseDouble(output.get(i).get(1)), Double.parseDouble(output.get(i).get(2)), Double.parseDouble(store_loc.get(0).get(0)), Double.parseDouble(store_loc.get(0).get(1)))));
	 }
	 System.out.println(" ==================\n ");
	 System.out.println(String.format("\nITEM: %s\nSTORE: %s\n", product, storeName));
	 
	 	
	 // Gte location of store to compare
	 output = esql.executeQueryAndReturnResult(String.format("SELECT name FROM Store WHERE storeID = %s", store));
	 	 

	 // Find Product
	 System.out.println("Insert WarehouseID: ");
	 String warehouse = in.readLine();
         if(esql.executeQuery(String.format("SELECT DISTINCT WarehouseID FROM Warehouse WHERE WarehouseID = %s", warehouse)) == 0)
         {
	    System.out.print("\033[H\033[2J");
            System.out.flush();
	    System.out.println("The warehouse with the specified ID does not exist");  
	    return;
	 }
         
	 // At this point we know all user input are valid
	 System.out.println("Insert # of units: ");	 
	 String units = in.readLine();
	 int intUnits = Integer.parseInt(units);
	 if(intUnits <= 0)
	 {
	    System.out.println("You must order more than 0 units");
	    return;
	 }
	 else if(intUnits >= 101)	//	Do an admin check?
	 {
	    System.out.println("An Admin must authorize orders of more than 100 units.");
	    return;	
	 }
	 // Fetch manager ID
	 List<List<String>> IDs = esql.executeQueryAndReturnResult(String.format("SELECT DISTINCT u.userID FROM Users u WHERE u.name = '%s';", manager));
	 String manID = IDs.get(0).get(0); 
	 // Update Product Table 
	 esql.executeUpdate(String.format("INSERT INTO ProductSupplyRequests(managerID, warehouseID, storeID, productName, unitsRequested) VALUES (%s, %s, %s, '%s', %s);", manID, warehouse, store, product, units));
	 //  This is probably where we should set up a trigger, so I'll leave this empty for now
	 System.out.print("\033[H\033[2J");
         System.out.flush();
	 Timestamp timestamp = new Timestamp(System.currentTimeMillis());
         System.out.println(String.format("\n%s:", timestamp));
	 System.out.println(String.format("Ordered %s units of %s from Warehouse #%s to be sent to %s.", units, product, warehouse, storeName));
	 	 
	 
      }
      catch(Exception e)
      {
         System.err.println(e.getMessage());
      }
   }

}//end Retail

