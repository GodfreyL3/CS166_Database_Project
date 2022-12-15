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

                if(checkIfManager(esql, authorisedUser) || checkIfAdmin(esql, authorisedUser))
                {
                    //the following functionalities basically used by managers
                    System.out.println("5. [M] Update Product");
                    System.out.println("6. [M] View 5 recent Product Updates Info");
                    System.out.println("7. [M] View 5 Popular Items");
                    System.out.println("8. [M] View 5 Popular Customers");
                    System.out.println("9. [M] Place Product Supply Request to Warehouse");
		    if(checkIfAdmin(esql, authorisedUser))
		    {
			System.out.println("10. [A] View Users");
			System.out.println("11. [A] View Managers");
		        System.out.println("12. [A] Update User Information");
                    }
		}
                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewStores(esql, authorisedUser); break;
                   case 2: viewProducts(esql, authorisedUser); break;
                   case 3: placeOrder(esql, authorisedUser); break;
                   case 4: viewRecentOrders(esql, authorisedUser); break;
                   case 5: updateProduct(esql, authorisedUser); break;
                   case 6: viewRecentUpdates(esql, authorisedUser); break;
                   case 7: viewPopularProducts(esql, authorisedUser); break;
                   case 8: viewPopularCustomers(esql, authorisedUser); break;
                   case 9: placeProductSupplyRequests(esql, authorisedUser); break;
                   case 12: updateUserInfo(esql, authorisedUser); break;
		   case 10: viewUsers(esql, authorisedUser); break;
		   case 11: peekManagerData(esql, authorisedUser); break;
                   case 20:
			System.out.print("\033[H\033[2J");
                        System.out.flush(); 
			usermenu = false; 
			break;
                   default :
			System.out.print("\033[H\033[2J");
                        System.out.flush(); 
			System.out.println("Unrecognized choice!"); 
			break;
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
        System.out.print("\033[H\033[2J");
       System.out.flush();
	System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface                         \n" +
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
            System.out.println(String.format("The user '%s' already exists within the database.", name));
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
	 System.out.print("\033[H\033[2J");
         System.out.flush();
         System.out.println("Sorry, we couldn't find this username/password combination....");
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here

   public static void viewStores(Retail esql, String user) {
   	      try{	
		  
		    System.out.print("\033[H\033[2J");
                    System.out.flush();
		  
		    List<List<String>> userLocation = esql.executeQueryAndReturnResult(String.format("SELECT latitude, longitude FROM Users WHERE name = '%s';", user));
		    
		    double uLat = Double.parseDouble(userLocation.get(0).get(0));
		    double uLong = Double.parseDouble(userLocation.get(0).get(1));

		    List<List<String>> stores = esql.executeQueryAndReturnResult("SELECT storeID, name, latitude, longitude FROM Store;");
		    
		    String sID;
		    String sname;
		    double sLat;
		    double sLong;

		    System.out.println(String.format("\nStores within 30 km of (%.2f, %.2f): ", uLat, uLong));
	 	    System.out.println(" _____________________________________");
	            System.out.println("| ID# | Name  | Lat   | Long  | Dist  |");
		    System.out.println("|=====+=======+=======+=======+=======|");
		    for(List<String> location : stores){
			
			sID = location.get(0);
			sname = location.get(1).replaceAll(" ", "");
			sLat = Double.parseDouble(location.get(2));
			sLong = Double.parseDouble(location.get(3));

			double dist = esql.calculateDistance(uLat, uLong, sLat, sLong);
			
			if(dist <= 30.0)
				System.out.println(String.format("| %-3s | %-5s | %-5.2f | %-5.2f | %-5.2f |", sID, sname, sLat, sLong, dist));
		     }
		     System.out.println(" ===================================== \n");
	 	   
     		 }catch(Exception e){
       			  System.err.println (e.getMessage ());
    			  return;
     		 }

    }
    public static void viewProducts(Retail esql, String user) {
            try
	    {
	       System.out.print("\033[H\033[2J");
                    System.out.flush();

                    List<List<String>> userLocation = esql.executeQueryAndReturnResult(String.format("SELECT latitude, longitude FROM Users WHERE name = '%s';", user));

                    double uLat = Double.parseDouble(userLocation.get(0).get(0));
                    double uLong = Double.parseDouble(userLocation.get(0).get(1));

                    List<List<String>> stores = esql.executeQueryAndReturnResult("SELECT storeID, name, latitude, longitude FROM Store;");

                    String sID;
                    String sname;
                    double sLat;
                    double sLong;
			
		    List<String> valid_stores = new ArrayList<String>();


                    System.out.println("\nAvailable Stores: ");
                    System.out.println(" _____________________________________");
                    System.out.println("| ID# | Name  | Lat   | Long  | Dist  |");
                    System.out.println("|=====+=======+=======+=======+=======|");
                    for(List<String> location : stores){

                        sID = location.get(0);
                        sname = location.get(1).replaceAll(" ", "");
                        sLat = Double.parseDouble(location.get(2));
                        sLong = Double.parseDouble(location.get(3));

                        double dist = esql.calculateDistance(uLat, uLong, sLat, sLong);

                        if(dist <= 30.0 || user.equals("Admin"))
                        {
                                System.out.println(String.format("| %-3s | %-5s | %-5.2f | %-5.2f | %-5.2f |", sID, sname, sLat, sLong, dist));
                                valid_stores.add(sID);  //      Add store to list of valid stores
                        }
                     }
                     System.out.println(" ===================================== \n");
		
		     System.out.print("\tEnter store ID: ");
         	     String store = in.readLine();

         	     if(!valid_stores.contains(store))
                     {
             		System.out.print("\033[H\033[2J");
             		System.out.flush();
             		System.out.println("Invalid Store...");
             		return;
         	     }
		     
		     List<List<String>> output = esql.executeQueryAndReturnResult(String.format("SELECT name FROM Store WHERE storeID = %s", store));
         	     String storeName = output.get(0).get(0);

         	     System.out.print("\033[H\033[2J");
         	     System.out.flush();

		     List<List<String>> productData = esql.executeQueryAndReturnResult(String.format("SELECT DISTINCT p.productName, p.pricePerUnit, p.numberOfUnits FROM Product p, Store s, Users u WHERE s.storeID = %s AND s.storeID = p.storeID;", store));
         	     System.out.print("Products sold at store ");
         	     System.out.println(storeName + ": ");
         	     
		     System.out.println(" ___________________________________");
		     System.out.println("| Product         | Price   | Units |");
		     System.out.println("|=================+=========+=======|");
		     String pname, price, punits;
		     for(List<String> row : productData)
		     { 
			pname = row.get(0).replaceAll(" ", "");
			price = row.get(1).replaceAll(" ", "");
	 		punits = row.get(2).replaceAll(" ", "");
			System.out.println(String.format("| %-15s | %-7s | %-5s |", pname, price, punits));	
		     }
         	     System.out.println(" ===================================\n");

    	     }catch (Exception e) {
        	 System.err.println(e.getMessage());
     	      }
      }
   public static void placeOrder(Retail esql, String user) {
     try{
        System.out.print("\033[H\033[2J");
        System.out.flush();

        List<List<String>> userLocation = esql.executeQueryAndReturnResult(String.format("SELECT latitude, longitude FROM Users WHERE name = '%s';", user));

        double uLat = Double.parseDouble(userLocation.get(0).get(0));
        double uLong = Double.parseDouble(userLocation.get(0).get(1));
        
        List<List<String>> stores = esql.executeQueryAndReturnResult("SELECT storeID, name, latitude, longitude FROM Store;");
                    String sID;
                    String sname;
                    double sLat;
                    double sLong;

		    List<String> valid_stores = new ArrayList<String>();

                    System.out.println("\nAvailable Stores: ");
                    System.out.println(" _____________________________________");
		    System.out.println("| ID# | Name  | Lat   | Long  | Dist  |");
		    System.out.println("|=====+=======+=======+=======+=======|");
                    for(List<String> location : stores){

                        sID = location.get(0);
                        sname = location.get(1).replaceAll(" ", "");
                        sLat = Double.parseDouble(location.get(2));
                        sLong = Double.parseDouble(location.get(3));
			

                        double dist = esql.calculateDistance(uLat, uLong, sLat, sLong);

                        if(dist <= 30.0 || user.equals("Admin"))
			{
                                System.out.println(String.format("| %-3s | %-5s | %-5.2f | %-5.2f | %-5.2f |", sID, sname, sLat, sLong, dist));
                     		valid_stores.add(sID);	//	Add store to list of valid stores
			}
		     }
                     System.out.println(" ===================================== \n");

         System.out.print("\nenter store ID: ");
         String store = in.readLine();

	 if(!valid_stores.contains(store))
	 {
	     System.out.print("\033[H\033[2J");
             System.out.flush();
	     System.out.println("Invalid Store...");
	     return;
	 }
        
	 List<List<String>> output = esql.executeQueryAndReturnResult(String.format("SELECT name FROM Store WHERE storeID = %s", store));
         String storeName = output.get(0).get(0);

         System.out.print("\033[H\033[2J");
         System.out.flush();
         
	  List<List<String>> productData = esql.executeQueryAndReturnResult(String.format("SELECT DISTINCT p.productName, p.pricePerUnit, p.numberOfUnits FROM Product p, Store s, Users u WHERE s.storeID = %s AND s.storeID = p.storeID;", store));
	 System.out.print("Products sold at store ");
         System.out.println(storeName);
         
	 System.out.println(" ___________________________________");
         System.out.println("| Product         | Price   | Units |");
         System.out.println("|=================+=========+=======|");
         String pname, price, punits;
         for(List<String> row : productData)
         {
             pname = row.get(0).replaceAll(" ", "");
             price = row.get(1).replaceAll(" ", "");
             punits = row.get(2).replaceAll(" ", "");
             System.out.println(String.format("| %-15s | %-7s | %-5s |", pname, price, punits));
         }
         System.out.println(" ===================================\n");


         System.out.print("Insert Product: ");
         String product = in.readLine();
         if(esql.executeQuery(String.format("SELECT DISTINCT p.productName, s.name FROM Product p, Store s, Users u WHERE s.storeID = p.storeID AND s.storeID = %s AND p.productName = '%s'", store, product)) == 0){
            System.out.print("\033[H\033[2J");
            System.out.flush();
            System.out.print("This store does not hold the product: ");
            System.out.println(product);
            return;
         }

	 //  Make sure there is enough product
	 List<List<String>> choice = esql.executeQueryAndReturnResult(String.format("SELECT DISTINCT p.numberOfUnits FROM Product p, Store s WHERE s.storeID = p.storeID AND s.storeID = %s AND p.productName = '%s';", store, product));
	 int product_left = Integer.parseInt(choice.get(0).get(0));

         System.out.print("Enter Number of units: ");
         String unitNumbers = in.readLine();
         int units = Integer.parseInt(unitNumbers);
         if(units <= 0 || units > 1000)
	 {
	    System.out.print("\033[H\033[2J");
            System.out.flush();
            System.out.println("Invalid Value...");
            return;
	 }

	 if(units > product_left)
	 {
	    System.out.print("\033[H\033[2J");
            System.out.flush();
	    System.out.println("Not enough stock at store to process order....");
	    return;
	 }	 
	  
	 //  Fetch ID
         List<List<String>> IDs = esql.executeQueryAndReturnResult(String.format("SELECT DISTINCT u.userID FROM Users u WHERE u.name = '%s';", user));
         String uID = IDs.get(0).get(0);

         String query = String.format("INSERT INTO Orders(customerID, storeID, productName, unitsOrdered) VALUES( %s, %s, '%s', %s)", uID, store ,product, unitNumbers);
         esql.executeUpdate(query); 

	 System.out.print("\033[H\033[2J");
         System.out.flush();	 
	 Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	 System.out.println(String.format("%s: Placed an order for %s %s units from %s", timestamp, unitNumbers, product, storeName));


      }catch(Exception e){
         System.err.println (e.getMessage ());
         return;
      }



   }
  public static void viewRecentOrders(Retail esql, String user) {
      
      int value;    
	
      try{
	 System.out.print("\033[H\033[2J");
         System.out.flush();
         System.out.println(String.format("%s's Recent orders: ", user));

         String query = String.format("SELECT o.orderNumber, o.orderTime, s.name, o.productName, o.unitsOrdered FROM Orders o, Users u, Store s WHERE s.storeID = o.storeID AND customerID = userID AND u.name = '%s' ORDER BY orderNumber DESC LIMIT 5;", user);
         List<List<String>> output = esql.executeQueryAndReturnResult(query);

	 System.out.println(" ______________________________________________________________________");
	 System.out.println("| O#   | Timestamp                   | Store | Product         | Units |");
	 System.out.println("|======+=============================+=======+=================+=======|");

	  for(int i = 0; i < 5; i++)
           {
              String orderNum = output.get(i).get(0).replaceAll(" ", "");
              String time = output.get(i).get(1).replaceAll(" ", "");
              String store = output.get(i).get(2).replaceAll(" ", "");;
              String product = output.get(i).get(3).replaceAll(" ", "");;
	      String units = output.get(i).get(4).replaceAll(" ", "");;
              String statement = String.format("| %-4s | %-27s | %-5s | %-15s | %-5s |", orderNum, time, store, product, units);
              System.out.println(statement);
           }
           System.out.println(" =======================================================================\n");

      }catch(Exception e){
         System.err.println (e.getMessage ());
         return;
      }

}
    public static void updateProduct(Retail esql, String manager) {
         boolean update = true;
	 String value;
    try {
    while(update)
    {
      //   Check if user is admin or manager
      String check = String.format("SELECT DISTINCT u.name FROM Users u, Store s WHERE s.managerID = userID AND u.name = '%s'", manager);
      if(!checkIfManager(esql, manager) && !checkIfAdmin(esql, manager))
         {
            System.out.println("You are not authorized to do such action...");
            return;
         }
      System.out.print("\033[H\033[2J");
      System.out.flush();

      //  Lists store for user to chose from
      System.out.println("\nStores managed by you:\n _____________");
      System.out.println("| SID | Store |");
      System.out.println("|=====+=======|");
      
      List<List<String>> managedStores;
      if(checkIfAdmin(esql, manager))
         managedStores = esql.executeQueryAndReturnResult("SELECT s.storeID, s.name FROM Store s, Users u WHERE u.userID = s.managerID");
      else
       	 managedStores = esql.executeQueryAndReturnResult(String.format("SELECT s.storeID, s.name FROM Store s, Users u WHERE u.name = '%s' AND u.userID = s.managerID", manager));

      String storeID, sname;
      for(List<String> row : managedStores)
      {
	 storeID = row.get(0).replaceAll(" ", "");
	 sname = row.get(1).replaceAll(" ", "");
	 System.out.println(String.format("| %-3s | %-5s |", storeID, sname));	 
      }

      System.out.println(" ==============\n");
      System.out.println("Insert Store ID: ");
      String store = in.readLine();
      if((esql.executeQuery(String.format("SELECT s.storeID FROM Store s, Users u WHERE s.managerID = u.userID AND u.name = '%s' AND s.storeID = %s", manager , store)) == 0) && !manager.equals("Admin"))
      {
         System.out.println(String.format("This store does not exist or is not managed by you. Exiting..."));
         return;
      }
      List<List<String>> output = esql.executeQueryAndReturnResult(String.format("SELECT name FROM Store WHERE storeID = %s", store));
      String storeName = output.get(0).get(0);
      
      //   Clear screen
      System.out.print("\033[H\033[2J");
      System.out.flush();

      //   List products sold at store

          List<List<String>> productData = esql.executeQueryAndReturnResult(String.format("SELECT DISTINCT p.productName, p.pricePerUnit, p.numberOfUnits FROM Product p, Store s, Users u WHERE s.storeID = %s AND s.storeID = p.storeID;", store));
         System.out.print("Products sold at store ");
         System.out.println(storeName);

         System.out.println(" ___________________________________");
         System.out.println("| Product         | Price   | Units |");
         System.out.println("|=================+=========+=======|");
         String pname, price, punits;
         for(List<String> row : productData)
         {
             pname = row.get(0).replaceAll(" ", "");
             price = row.get(1).replaceAll(" ", "");
             punits = row.get(2).replaceAll(" ", "");
             System.out.println(String.format("| %-15s | %-7s | %-5s |", pname, price, punits));
         }
         System.out.println(" ===================================\n");
      System.out.println("Insert Product: ");
      String product = in.readLine();
      if(esql.executeQuery(String.format("SELECT DISTINCT p.productName, s.name FROM Product p, Store s, Users u WHERE u.userID = s.managerID AND s.storeID = p.storeID AND s.storeID = %s AND p.productName = '%s'", store, product)) == 0){
            System.out.print("\033[H\033[2J");
         System.out.flush();
         System.out.print("This store does not hold the product: ");
         System.out.println(product);
         return;
      }

      //  Get manager ID
      List<List<String>> IDs = esql.executeQueryAndReturnResult(String.format("SELECT DISTINCT u.userID FROM Users u WHERE u.name = '%s'", manager));
      String manID = IDs.get(0).get(0);
      
      //  Use trigger here to insert updateNumber and updatedOn values
      esql.executeUpdate(String.format("INSERT INTO ProductUpdates(managerID, storeID, productName) VALUES (%s, %s,'%s');", manID, store, product));

      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	
      //  Manager chooses what to update
      System.out.println("What would you like to update?");
      System.out.println("1. Number of Units \n2. Price per Unit");
      switch(readChoice()){
        case 1: System.out.println("Number of units: ");
           value = in.readLine();
           int units = Integer.parseInt(value);

	   esql.executeUpdate(String.format("UPDATE Product SET numberOfUnits = %s WHERE storeID = %s AND productName = '%s';", value, store, product));

	   System.out.print("\033[H\033[2J");
           System.out.flush();

           System.out.println(String.format("\n%s:", timestamp));
           System.out.println(String.format("%s: Successfully modified product %s unit number to %s from store %s. ", timestamp, product, value, storeName));

        break;
        case 2: System.out.println("Price per Unit: ");
           value = in.readLine();
           float new_price = Float.parseFloat(value);

	   esql.executeUpdate(String.format("UPDATE Product SET pricePerUnit = %s WHERE storeID = %s AND productName = '%s';", value, store, product));

           System.out.print("\033[H\033[2J");
           System.out.flush();

           System.out.println(String.format("\n%s:", timestamp));
           System.out.println(String.format("%s: Successfully modified product %s price to %s from store %s. ", timestamp, product, value, storeName));

        break;
        default : System.out.println("Invalid Value");

      }
      // esql.executeUpdate(query);
      System.out.println("Product Updated!");
      System.out.println("Do you want to update more products?");
      System.out.println("1. Yes 2. No");

      switch(readChoice()){
        case 1: break;
        case 2: update = false; break;
      }
    }
  }
  catch(Exception e){
    System.err.println (e.getMessage());
  }

}

   public static void viewRecentUpdates(Retail esql, String manager)
   {
      try
        {
           String check = String.format("SELECT DISTINCT u.name FROM Users u, Store s WHERE s.managerID = userID AND u.name = '%s'", manager);
           //  Check to see if user is actually a manager
           if(!checkIfManager(esql, manager) && !checkIfAdmin(esql, manager))
           {
              System.out.println("You are not authorized to do such action...");
              return;
           }
           System.out.print("\033[H\033[2J");
           System.out.flush();
           String query = "";
           if(checkIfAdmin(esql, manager))
           {
               query = "SELECT DISTINCT pu.updateNumber, s.name AS store, pu.productName AS product, pu.updatedOn FROM Users u, Store s, ProductUpdates pu, Warehouse w WHERE u.userID = pu.managerID AND s.storeID = pu.storeID ORDER BY pu.updateNumber DESC LIMIT 5;";
           }else
           {
               query = String.format("SELECT DISTINCT pu.updateNumber, s.name AS store, pu.productName AS product, pu.updatedOn FROM Users u, Store s, ProductUpdates pu, Warehouse w WHERE u.userID = pu.managerID AND u.name = '%s' AND s.storeID = pu.storeID ORDER BY pu.updateNumber DESC LIMIT 5;", manager);
           }
           List<List<String>> output = esql.executeQueryAndReturnResult(query);
           System.out.println(String.format("\n\nRecent updates for %s", manager));
           System.out.println(" ___________________________________________________________________");
           System.out.println("| U#   | Store | Product              | Timestamp                   |");
           System.out.println("|======+=======+======================+=============================|");
           
	    String orderNum, store, product, time, statement;
		
	   for(int i = 0; i < 5; i++)
           {
              orderNum = output.get(i).get(0).replaceAll(" ", "");
              store = output.get(i).get(1).replaceAll(" ", "");
              product = output.get(i).get(2).replaceAll(" ", "");
              time = output.get(i).get(3).replaceAll(" ", "");
              statement = String.format("| %-4s | %-5s | %-20s | %-27s |", orderNum, store, product, time);
              System.out.println(statement);
           }
           System.out.println("====================================================================\n");
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

        if(!checkIfManager(esql, manager) && !checkIfAdmin(esql, manager))
        {
           System.out.println("You are not authorized to do such action...");
           return;
        }
        System.out.print("\033[H\033[2J");
        System.out.flush();
        String query = "";
        if(checkIfAdmin(esql, manager))
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

         if(!checkIfManager(esql, manager) && !checkIfAdmin(esql, manager))
         {
            System.out.println("You are not authorized to do such action...");
            return;
         }
         System.out.print("\033[H\033[2J");
         System.out.flush();
         String query = "";
         if(checkIfAdmin(esql, manager))
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
         if(!checkIfManager(esql, manager) && !checkIfAdmin(esql, manager))
         {
            System.out.println("You are not authorized to do such action...");
            return;
         }
         System.out.print("\033[H\033[2J");
         System.out.flush();
         //  Check for storeID validity
         //  Add stores managed by user
         System.out.println("\nStores managed by you:\n");
         List<List<String>> stores;

	 if(checkIfAdmin(esql, manager)) 
	     stores = esql.executeQueryAndReturnResult(String.format("SELECT s.storeID, s.name FROM Store s, Users u WHERE u.userID = s.managerID", manager));
	 else
	     stores = esql.executeQueryAndReturnResult(String.format("SELECT s.storeID, s.name FROM Store s, Users u WHERE u.name = '%s' AND u.userID = s.managerID", manager));
         System.out.println(" ____________ ");
	 System.out.println("| S# | Name  |");
	 System.out.println("|====+=======|");
	 String id, sname;
	 for(List<String> row : stores)
	 {
	    id = row.get(0).replaceAll(" ", "");
	    sname = row.get(1).replaceAll(" ", "");
	    System.out.println(String.format("| %-2s | %-5s |", id, sname));
	 }
	 System.out.println(" ============ ");
         
         System.out.println("Insert Store ID: ");
         String store = in.readLine();
	 if(checkIfAdmin(esql, manager))
         {
             if(esql.executeQuery(String.format("SELECT s.storeID FROM Store s, Users u WHERE s.managerID = u.userID AND s.storeID = %s", store)) == 0)
             {
                System.out.println(String.format("This store does not exist or is not managed by you. Exiting..."));
                return;
             }
	 }
	 else
         {
             if(esql.executeQuery(String.format("SELECT s.storeID FROM Store s, Users u WHERE s.managerID = u.userID AND u.name = '%s' AND s.storeID = %s", manager , store)) == 0)
             {
                System.out.println(String.format("This store does not exist or is not managed by you. Exiting..."));
                return;
             }
         }
         List<List<String>> output = esql.executeQueryAndReturnResult(String.format("SELECT name FROM Store WHERE storeID = %s", store));
         String storeName = output.get(0).get(0);

         System.out.print("\033[H\033[2J");
         System.out.flush();
         System.out.print("Products sold at store ");
         System.out.println(storeName);
	 System.out.println(" ______________________________ ");
	 System.out.println("| Name                 | Units |");
         System.out.println("|======================+=======|");
         List<List<String>> productData = esql.executeQueryAndReturnResult(String.format("SELECT p.productName, p.numberOfUnits FROM Product p, Store s, Users u WHERE u.userID = s.managerID AND s.storeID = %s AND s.storeID = p.storeID;", store));
         String pname, punits;
	 for(List<String> row : productData)
	 {
	    pname = row.get(0).replaceAll(" ", "");
	    punits =  row.get(1).replaceAll(" ", "");
	    System.out.println(String.format("| %-20s | %-5s |", pname, punits));
	 }

	 System.out.println(" ============================== \n");
         System.out.println("Insert Product: ");
         String product = in.readLine();
         if(esql.executeQuery(String.format("SELECT DISTINCT p.productName, s.name FROM Product p, Store s, Users u WHERE u.userID = s.managerID AND s.storeID = p.storeID AND s.storeID = %s AND p.productName = '%s'", store, product)) == 0){
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
         else if(intUnits >= 101)       //      Do an admin check?
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
   
   public static void viewUsers(Retail esql, String admin)
   {
	try
         {
            System.out.print("\033[H\033[2J");
            System.out.flush();
            if(!checkIfAdmin(esql, admin))
            {
               System.out.println("You are not authorised to change user information.");
               return;
            }

         System.out.print("Insert username or part of username: ");
         String part = in.readLine();
         String query = "SELECT userID, name, type FROM Users WHERE name LIKE \'%";
         query += part;
         query += "%\' ORDER BY userID;";
	 List<List<String>> allUsers = esql.executeQueryAndReturnResult(query);
            System.out.print("\033[H\033[2J");
            System.out.flush();
            System.out.println("\n __________________________________________________ ");
            System.out.println("| ID  | Name                           | Type       |");
            System.out.println("|=====+================================+============|");
            String id, name, type;
            for(List<String> row : allUsers)
            {
               id = row.get(0);
               name = row.get(1).replaceAll(" ", "");
               type = row.get(2).replaceAll(" ","");
               System.out.println(String.format("| %-3s | %-30s | %-10s |", id, name, type));
            }
            System.out.println(" =================================================== \n");
	   }
           catch(Exception e)
           {
              System.err.println(e.getMessage());
      	   }
   	}
   

   public static void updateUserInfo(Retail esql, String admin)
   {
      try
      {
         System.out.print("\033[H\033[2J");
         System.out.flush();
         if(!checkIfAdmin(esql, admin))
         {
            System.out.println("You are not authorised to change user information.");
            return;
         }

	 System.out.print("Insert username or part of username: ");	 
	 String part = in.readLine();
         String query = "SELECT userID, name, type FROM Users WHERE name LIKE \'";
         query += part;
         query += "%\' ORDER BY userID;";

         while(esql.executeQuery(query) < 1 || esql.executeQuery(query) > 1)
	 {
            List<List<String>> allUsers = esql.executeQueryAndReturnResult(query);
            System.out.print("\033[H\033[2J");
            System.out.flush();
            
            System.out.println("\n __________________________________________________ ");
            System.out.println("| ID  | Name                           | Type       |");
            System.out.println("|=====+================================+============|");
            String id, name, type;
            for(List<String> row : allUsers)
            {
               id = row.get(0);
               name = row.get(1).replaceAll(" ", "");
               type = row.get(2).replaceAll(" ","");
               System.out.println(String.format("| %-3s | %-30s | %-10s |", id, name, type));
            }
            System.out.println(" =================================================== \n");

	    System.out.print("Insert username or part of username: ");
            part = in.readLine();
			
	    query = "SELECT userID, name, type FROM Users WHERE name LIKE \'%";
            query += part;
            query += "%\';";
	}

        //  Get ID of user
        List<List<String>> UID_get = esql.executeQueryAndReturnResult(String.format("SELECT userID FROM Users WHERE name = '%s';", part));
        String uid = UID_get.get(0).get(0);

	//  At this point, part must be a username, and we have ID#
        System.out.print("\033[H\033[2J");
        System.out.flush();

	System.out.println(String.format("What would you like to change for %s?", part));
        System.out.println("===============================");
        System.out.println("1. Name");
        System.out.println("2. Password");
	System.out.println("3. Location");
        if(checkIfAdmin(esql, uid))
	{
	    System.out.println("4. [M] Add managed Stores");
            System.out.println("5. [M] Remove managed Stores");	
	}

	System.out.print("Input: ");

        switch(readChoice())
	{
	   case 1:
	      System.out.print(String.format("Insert %s\'s new name: ", part));
              String newName = in.readLine();
	      if(newName.length() < 3 || esql.executeQuery(String.format("SELECT name FROM Users WHERE name = '%s';", newName)) != 0)
	      {
		  System.out.print("\033[H\033[2J");
                  System.out.flush();
	          System.out.println("Invalid name: name must be longer that 5 characters and unique....");
		  return;
	      }

              esql.executeUpdate(String.format("UPDATE Users SET name = '%s' WHERE userID = %s", newName, uid));
              
	      System.out.print("\033[H\033[2J");
              System.out.flush();
 
	      System.out.println(String.format("\nChanged %s\'s name to %s\n\n", part, newName));
	     
              break;
	   case 2:
	      System.out.print(String.format("Insert %s\'s new Password: ", part));
	      String password = in.readLine();
              if(password.length() < 3)
              {
            	 System.out.println("Password must be greater than 3 characters in length.");
            	 return;
              }
	      
	      esql.executeUpdate(String.format("UPDATE Users SET password = '%s' WHERE userID = %s", password, uid));

              System.out.print("\033[H\033[2J");
              System.out.flush();

              System.out.println(String.format("\nChanged %s\'s password to %s\n\n", part, password));
	      break;
	   case 3:
	      System.out.print(String.format("Insert %s\'s new Latitude(0.00 - 100.00): ", part));
	      String latitude = in.readLine();
	      System.out.print(String.format("Insert %s\'s new Longitude(0.00 - 100.00): ", part));
	      String longitude = in.readLine();

	       int lat_int = Integer.parseInt(latitude);
               int lon_int = Integer.parseInt(longitude);

               // Check in range
               if(lat_int < 0 || lat_int > 100 || lon_int < 0 || lon_int > 100)
               {
                   System.out.println(String.format("The coordinates (%s, %s) are out of scope.", latitude, longitude));
                   return;
               }
	       
	       System.out.print("\033[H\033[2J");
               System.out.flush();
	       esql.executeUpdate(String.format("UPDATE Users SET latitude = %s, longitude = %s WHERE userID = %s", latitude, longitude, uid));
	       System.out.println(String.format("\nChanged %s\'s location to (%s,%s)\n\n", part, latitude, longitude));	
	       break;
           default:
	      System.out.println("Invalid Input");
	      break;
	}

	return;

      } 
      catch(Exception e)
      {
	 System.err.println(e.getMessage());
	 return;
      }
   }

   public static void peekManagerData(Retail esql, String admin)
   {  try{
	if(!checkIfAdmin(esql, admin))
	{
	    System.out.print("\033[H\033[2J");
            System.out.flush();
	    System.out.println("Only an admin can use this function....");
	    return;
	}
	System.out.print("\033[H\033[2J");
        System.out.flush();
        String query = "SELECT s.storeID, s.name, u.userID, u.name FROM Users u, Store s WHERE u.userID = s.managerID";
        List<List<String>> manData = esql.executeQueryAndReturnResult(query);

	System.out.println(" ____________________________________________________ ");
	System.out.println("| S#  | SName | M#  | Manager Name                   |");
	System.out.println("|=====+=======+=====+================================|");
	String sID, sname, uID, uname;
        for(List<String> row : manData)
	{
	    sID = row.get(0).replaceAll(" ", "");
	    sname = row.get(1).replaceAll(" ", "");
	    uID = row.get(2).replaceAll(" ", "");
	    uname = row.get(3).replaceAll(" ", "");
	    System.out.println(String.format("| %-3s | %-5s | %-3s | %-30s |",sID, sname, uID, uname));   
	}
	System.out.println(" ====================================================");
       }catch(Exception e)
      {
         System.err.println(e.getMessage());
         return;
      }
   }

   public static boolean checkIfManager(Retail esql, String manager)
   {
      try
      {
         String check = String.format("SELECT DISTINCT u.name FROM Users u WHERE u.name = '%s' AND u.type = 'manager';", manager);
         if(esql.executeQuery(check) == 0)
            return false;
         else
	    return true;
      }catch(Exception e)
      {
         System.err.println(e.getMessage());
         return false;
      }
   }
 
   public static boolean checkIfAdmin(Retail esql, String admin)
   {
      try
      {
         String check = String.format("SELECT DISTINCT u.name FROM Users u WHERE u.name = '%s' AND u.type = 'admin';", admin);
         if(esql.executeQuery(check) == 0)
            return false;
         else
            return true;
      }catch(Exception e)
      {
         System.err.println(e.getMessage());
         return false;
      }
   }

}//end Retail
