package PO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;


public class BITool {

   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
   static final String DB_URL = "jdbc:mysql://localhost/grocery";

   //  Database credentials
   static final String USER = "root";
   static final String PASS = "sesame";

   
   /**
    * @param product
    * @param store
    * @param dateTime
    * @return
   * @throws Exception 
    */
  public JSONArray centralCube(String product, String store, String dateTime) throws Exception
  {
      Connection conn = null;
      PreparedStatement statement = null;
      ResultSet rs = null;
      //List<Sales> sales = new ArrayList<Sales>();
   
      
      JSONArray jsonArray = new JSONArray();
      try {
        //Open a connection
        System.out.println("Connecting to database...");
        conn = DriverManager.getConnection(DB_URL,USER,PASS);
        
        
        //Execute a query that returns the central cube
        System.out.println("Creating statement...");
         String sql;
        /* sql = "SELECT p.category, s.store_state, t.year, sum(f.dollar_sales) AS sales_total" +
          "FROM Product p, Store s, Date_time t, Sales f" +
          "WHERE f.product_key = p.product_key AND" +
          "f.store_key = s.store_key AND f.time_key = t.time_key" +
          "GROUP BY p.category, s.store_state, t.year";*/        
        
        sql = "SELECT " + product + ", " + store + ", " + dateTime + ", " + "sum(sales.dollar_sales) AS sales_total" +
            " FROM Product, Store, Date_time, Sales " +
            "WHERE Sales.product_key = Product.product_key AND " +
            "Sales.store_key = Store.store_key AND Sales.time_key = Date_time.time_key" +
            " GROUP BY " + product + ", " + store +", " + dateTime;
        
        //sql = "SELECT PRODUCT.brand FROM product"; 
         statement = conn.prepareStatement(sql);
       
         /*statement.setString(1,  product);
         statement.setString(2, store);*/
      //  statement.setInt(3, dateTime);
   
         //sql = "SELECT * FROM product";
         
         rs = statement.executeQuery(sql);
        
         /*
         ResultSetMetaData rsmd = rs.getMetaData();
         System.out.println("querying SELECT * FROM XXX");
         int columnsNumber = rsmd.getColumnCount();
         while (rs.next()) {
             for (int i = 1; i <= columnsNumber; i++) {
                 if (i > 1) System.out.print(",  ");
                 String columnValue = rs.getString(i);
                 System.out.print(columnValue + " " + rsmd.getColumnName(i));
             }
             System.out.println("test");
         }

        

         */
         
         ArrayList<String> attr = new ArrayList<>();
         attr.add(parseParam(product));
         attr.add(parseParam(store));
         attr.add(parseParam(dateTime));
         
         ToJSON rsToJSON = new ToJSON();



         
         jsonArray = rsToJSON.toJSONArray(rs, attr);
         
         
         //Extract data from result set
         
   /*      while(rs.next()){
          int total_rows = rs.getMetaData().getColumnCount();
          JSONObject obj = new JSONObject();
            for (int i = 0; i < total_rows; i++) {
              obj.put(rs.getMetaData().getColumnLabel(i+ 1).toLowerCase(),
                rs.getObject(i + 1));
              jsonArray.put(obj);
            } 
         
         
            //Retrieve by column name
          Sales sale = new Sales();
          sale.setProduct(rs.getString("p.category"));
            sale.setStore(rs.getString("s.store_state"));
            sale.setDateTime(rs.getInt("t.year"));
            sale.setSalesTotal(rs.getInt("sales_total"));
            sales.add(sale);
    
            //Display values
            System.out.print("Category: " + category);
            System.out.print(", Store_state: " + storeState);
            System.out.print(", Year: " + year);
            System.out.print(", Sales_total: " + salesTotal);
         } */
      
       } //end try
       finally{
        if (rs != null) try { rs.close(); } catch (SQLException ignore) {}
        if (statement != null) try { statement.close(); } catch (SQLException ignore) {}
        System.out.println("Closing....");
        if (conn != null) try { conn.close(); } catch (SQLException ignore) {} 
       } //end finally 
      
      return jsonArray; 
  } //central cube
   
    
  public String parseParam(String param){
    String[] text = null;
    String st = "";
    text = param.split("\\.");
    
    st = text[1];
    
    System.out.println(st);
    return st;
  }
  
  
  
  public JSONArray rollUpByHierarchy(String product, String store, String dateTime) throws Exception
  {
      Connection conn = null;
      Statement statement = null;
      ResultSet rs = null;
      //List<Sales> sales = new ArrayList<Sales>();
      JSONArray jsonArray = new JSONArray();
      try {
        //Open a connection
        System.out.println("Connecting to database...");
        conn = DriverManager.getConnection(DB_URL,USER,PASS);
        
        statement = conn.createStatement();
        //Execute a query that rolls up the central cube by rolling up the hierarchy
        System.out.println("Creating statement...");
        String sql;
        /*sql = "SELECT p.department, s.store_state, t.year, sum(f.dollar_sales) AS sales_total" +
            "FROM Product p, Store s, Date_time t, Sales f" +
            "WHERE f.product_key = p.product_key AND" +
            "f.store_key = s.store_key AND f.time_key = t.time_key" +
            "GROUP BY p.department, s.store_state, t.year";*/
        sql = "SELECT " + product + ", " + store + ", " + dateTime + ", " + "sum(sales.dollar_sales) AS sales_total " +
            "FROM Product, Store, Date_time, Sales " +
            "WHERE Sales.product_key = Product.product_key AND " +
            "Sales.store_key = Store.store_key AND Sales.time_key = Date_time.time_key " +
            "GROUP BY " + product + ", " + store + ", " + dateTime;
        rs = statement.executeQuery(sql);
    
        
        
        ArrayList<String> attr = new ArrayList<>();
        attr.add(parseParam(product));
        attr.add(parseParam(store));
        attr.add(parseParam(dateTime));
        
        ToJSON rsToJSON = new ToJSON();



        
        jsonArray = rsToJSON.toJSONArray(rs, attr);

        
/*        
         //Extract data from result set
         while(rs.next()){
           int total_rows = rs.getMetaData().getColumnCount();
            JSONObject obj = new JSONObject();
            for (int i = 0; i < total_rows; i++) {
              obj.put(rs.getMetaData().getColumnLabel(i+ 1).toLowerCase(),
                  rs.getObject(i + 1));
              jsonArray.put(obj);
            }
 
            //Retrieve by column name
          Sales sale = new Sales();
          sale.setProduct(rs.getString("p.department"));
            sale.setStore(rs.getString("s.store_state"));
            sale.setDateTime(rs.getInt("t.year"));
            sale.setSalesTotal(rs.getInt("sales_total"));
            sales.add(sale);
    
            //Display values
             System.out.print("Department: " + department);
             System.out.print(", Store_state: " + storeState);
             System.out.print(", Year: " + year);
             System.out.print(", Sales_total: " + salesTotal);
         }
         
 */        
                  
      } //end try
       finally{
        if (rs != null) try { rs.close(); } catch (SQLException ignore) {}
        if (statement != null) try { statement.close(); } catch (SQLException ignore) {}
        System.out.println("Closing....");
        if (conn != null) try { conn.close(); } catch (SQLException ignore) {} 
       } //end finally 
      return jsonArray; 
  } //rollup by hierarchy
    
    
 
  public JSONArray rollUpByDimension(ArrayList<String> dimensions) throws Exception
  {
      Connection conn = null;
      Statement statement = null;
      ResultSet rs = null;
      JSONArray jsonArray = new JSONArray();
      try {
        //Open a connection
        System.out.println("Connecting to database...");
        conn = DriverManager.getConnection(DB_URL,USER,PASS);
        
        statement = conn.createStatement();
        //Execute a query that rolls up by reducing a dimension
        System.out.println("Creating statement...");
         String sql;
         //Extracts all dimension-attributes names and formats them into Select statement
         String select_statement = "";
         for (int i = 0; i < dimensions.size(); i++) {
           if (i == 0) select_statement = "" + dimensions.get(i);
           else {
            select_statement = select_statement + ", " + dimensions.get(i);
           }
         }
        sql = "SELECT " + select_statement + ", " + "sum(sales.dollar_sales) AS sales_total " +
            "FROM Product, Store, Date_time, Sales " +
            "WHERE Sales.product_key = Product.product_key AND " +
            "Sales.store_key = Store.store_key AND Sales.time_key = Date_time.time_key " +
            "GROUP BY " + select_statement;
         rs = statement.executeQuery(sql);
    
         //Extract data from result set
         
         
         ArrayList<String> attr = new ArrayList<>();
         attr.add(parseParam(dimensions.get(0)));
         attr.add(parseParam(dimensions.get(1)));
         attr.add(parseParam(dimensions.get(2)));
         
         ToJSON rsToJSON = new ToJSON();



         
         jsonArray = rsToJSON.toJSONArray(rs, attr);
         
         
         
      } //end try
       finally{
        if (rs != null) try { rs.close(); } catch (SQLException ignore) {}
        if (statement != null) try { statement.close(); } catch (SQLException ignore) {}
        System.out.println("Closing....");
        if (conn != null) try { conn.close(); } catch (SQLException ignore) {} 
       } //end finally 
      return jsonArray; 
  } //roll up by dimension
    

   
  public JSONArray drillDownByHierarchy(String product, String store, String dateTime) throws SQLException, JSONException
  {
      Connection conn = null;
      Statement statement = null;
      ResultSet rs = null;
      //List<Sales> sales = new ArrayList<Sales>();
      JSONArray jsonArray = new JSONArray();
      try {
        //Open a connection
        System.out.println("Connecting to database...");
        conn = DriverManager.getConnection(DB_URL,USER,PASS);
        
        statement = conn.createStatement();
        //Execute a query that drills down by climbing down the hierarchy
        System.out.println("Creating statement...");
         String sql;
         /*sql = "SELECT + p.brand, s.city, t.year, sum(f.dollar_sales) AS sales_total" +
            "FROM Product p, Store s, Date_time t, Sales f" +
            "WHERE f.product_key = p.product_key AND" +
            "f.store_key = s.store_key AND f.time_key = t.time_key" +
            "GROUP BY p.brand, s.city, t.year";*/
         sql = "SELECT " + product + ", " + store + ", " + dateTime + ", " + "sum(f.dollar_sales) AS sales_total" +
            "FROM Product, Store, Date_time, Sales" +
            "WHERE Sales.product_key = Product.product_key AND" +
            "Sales.store_key = Store.store_key AND Sales.time_key = Date_time.time_key" +
            "GROUP BY " + product + ", " + store + ", " + dateTime;
         rs = statement.executeQuery(sql);
    
         //Extract data from result set
         while(rs.next()){
           int total_rows = rs.getMetaData().getColumnCount();
            JSONObject obj = new JSONObject();
            for (int i = 0; i < total_rows; i++) {
              obj.put(rs.getMetaData().getColumnLabel(i+ 1).toLowerCase(),
                  rs.getObject(i + 1));
              jsonArray.put(obj);
            }
            //Retrieve by column name
          /*Sales sale = new Sales();
          sale.setProduct(rs.getString("p.brand"));
            sale.setStore(rs.getString("s.city"));
            sale.setDateTime(rs.getInt("t.year"));
            sale.setSalesTotal(rs.getInt("sales_total"));
            sales.add(sale);*/
    
            
            //Display values
            /*System.out.print("Brand: " + brand);
             System.out.print(", City: " + storeCity);
             System.out.print(", Year: " + year);
             System.out.print(", Sales_total: " + salesTotal);*/
         }
      } //end try
       finally{
        if (rs != null) try { rs.close(); } catch (SQLException ignore) {}
        if (statement != null) try { statement.close(); } catch (SQLException ignore) {}
        System.out.println("Closing....");
        if (conn != null) try { conn.close(); } catch (SQLException ignore) {} 
       } //end finally 
      return jsonArray; 
  } //drill down by hierarchy
    
  
 
  public JSONArray drillDownAddDimension(ArrayList<String> dimensions) throws SQLException, JSONException
  {
      Connection conn = null;
      Statement statement = null;
      ResultSet rs = null;
      //List<Sales> sales = new ArrayList<Sales>();
      JSONArray jsonArray = new JSONArray();
      try {
        //Open a connection
        System.out.println("Connecting to database...");
        conn = DriverManager.getConnection(DB_URL,USER,PASS);
        
        statement = conn.createStatement();
        //Execute a query that drills down by adding a dimension
          System.out.println("Creating statement...");
         String sql;
         String select_statement = "";
         for (int i = 0; i < dimensions.size(); i++) {
           if (i == 0) select_statement = "" + dimensions.get(i);
           else {
            select_statement = select_statement + ", " + dimensions.get(i);
           }
         }
         /*sql = "SELECT p.package_size, p.category, s.city,  s.store_state, t.year, sum(f.dollar_sales) AS sales_total" +
            "FROM Product p, Store s, Date_time t, Sales f" +
            "WHERE f.product_key = p.product_key AND" +
            "f.store_key = s.store_key AND f.time_key = t.time_key" +
            "GROUP BY p.package_size, p.category, s.city, s.store_state, t.year";*/
        sql = "SELECT " + select_statement + ", " + "sum(f.dollar_sales) AS sales_total" +
            "FROM Product, Store, Date_time, Sales" +
            "WHERE Sales.product_key = Product.product_key AND" +
            "Sales.store_key = Store.store_key AND Sales.time_key = Date_time.time_key" +
            "GROUP BY " + select_statement;
         rs = statement.executeQuery(sql);
    
         //Extract data from result set
         while(rs.next()){
           
           int total_rows = rs.getMetaData().getColumnCount();
            JSONObject obj = new JSONObject();
            for (int i = 0; i < total_rows; i++) {
              obj.put(rs.getMetaData().getColumnLabel(i+ 1).toLowerCase(),
                  rs.getObject(i + 1));
              jsonArray.put(obj);
            }
            //Retrieve by column name
          /*Sales sale = new Sales();
          sale.setProduct(rs.getString("p.package_size"));
          sale.setProduct(rs.getString("p.category"));  //check how to deal with two product strings
            sale.setStore(rs.getString("s.city"));      //check how to deal with two store strings
            sale.setStore(rs.getString("s.store_state"));
            sale.setDateTime(rs.getInt("t.year"));
            sale.setSalesTotal(rs.getInt("sales_total"));
            sales.add(sale);*/
  
  
             //Display values
             /*System.out.print("Package_size: " + packageSize);
             System.out.print(", Category: " + category);
             System.out.print(", City: " + storeCity);
             System.out.print(", Store_state: " + storeState);
             System.out.print(", Year: " + year);
             System.out.print(", Sales_total: " + salesTotal);*/
         }
      } //end try
       finally{
        if (rs != null) try { rs.close(); } catch (SQLException ignore) {}
        if (statement != null) try { statement.close(); } catch (SQLException ignore) {}
        System.out.println("Closing....");
        if (conn != null) try { conn.close(); } catch (SQLException ignore) {} 
       } //end finally 
      return jsonArray; 
  } //drill down by adding dimension
    
  


  public JSONArray slice(ArrayList<String> parameters) throws SQLException, JSONException
  {
      Connection conn = null;
      Statement statement = null;
      ResultSet rs = null;
      //List<Sales> sales = new ArrayList<Sales>();
      JSONArray jsonArray = new JSONArray();
      try {
        //Open a connection
        System.out.println("Connecting to database...");
        conn = DriverManager.getConnection(DB_URL,USER,PASS);
        
        statement = conn.createStatement();
        //Execute a query that slices the cube
        System.out.println("Creating statement...");
         String sql;
         sql = "SELECT s.city, s.store_state, s.store_zip, sum(f.dollar_sales) AS sales_total" +
            "FROM Product p, Store s, Date_time t, Sales f" +
            "WHERE f.product_key = p.product_key AND" +
            "f.store_key = s.store_key AND f.time_key = t.time_key AND" +
            "s.store_state = PA" +
            "GROUP BY s.city, s.store_state, s.store_zip";
         rs = statement.executeQuery(sql);
    
         //Extract data from result set
         while(rs.next()){
           
           int total_rows = rs.getMetaData().getColumnCount();
            JSONObject obj = new JSONObject();
            for (int i = 0; i < total_rows; i++) {
              obj.put(rs.getMetaData().getColumnLabel(i+ 1).toLowerCase(),
                  rs.getObject(i + 1));
              jsonArray.put(obj);
            }
            //Retrieve by column name
          /*Sales sale = new Sales();
          sale.setStore(rs.getString("s.city"));
            sale.setStore(rs.getString("s.store_state"));
            sale.setStore(rs.getString("s.store_zip"));
            sale.setSalesTotal(rs.getInt("sales_total"));
            sales.add(sale);*/
  
             //Display values
             /*System.out.print("City: " + storeCity);
             System.out.print(", Store_state: " + storeState);
             System.out.print(", store_zip: " + zipCode);
             System.out.print(", Sales_total: " + salesTotal);*/
         }
      } //end try
       finally{
        if (rs != null) try { rs.close(); } catch (SQLException ignore) {}
        if (statement != null) try { statement.close(); } catch (SQLException ignore) {}
        System.out.println("Closing....");
        if (conn != null) try { conn.close(); } catch (SQLException ignore) {} 
       } //end finally 
      return jsonArray; 
  } //slice
  
  
  
  
  
  public JSONArray dice(ArrayList<String> parameters) throws SQLException, JSONException
  {
      Connection conn = null;
      Statement statement = null;
      ResultSet rs = null;
      //List<Sales> sales = new ArrayList<Sales>();
      JSONArray jsonArray = new JSONArray();
      try {
        //Open a connection
        System.out.println("Connecting to database...");
        conn = DriverManager.getConnection(DB_URL,USER,PASS);
        
        statement = conn.createStatement();
        //Execute a query that dices the cube
        System.out.println("Creating statement...");
         String sql;
         sql = "SELECT p.category, s.store_state, t.year, sum(f.dollar_sales) AS sales_total" +
            "FROM Product p, Store s, Date_time t, Sales f" +
            "WHERE f.product_key = p.product_key AND" +
            "f.store_key = s.store_key AND f.time_key = t.time_key AND" +
            "(p.category = Food OR p.category = Drinks) AND" +
            "(s.store_state = CA OR s.store_state = NY) AND t.year = 1994" +
            "GROUP BY p.category, s.store_state, t.year";
         rs = statement.executeQuery(sql);
    
         //Extract data from result set
         while(rs.next()){
           
           int total_rows = rs.getMetaData().getColumnCount();
            JSONObject obj = new JSONObject();
            for (int i = 0; i < total_rows; i++) {
              obj.put(rs.getMetaData().getColumnLabel(i+ 1).toLowerCase(),
                  rs.getObject(i + 1));
              jsonArray.put(obj);
            }
            //Retrieve by column name
          /*Sales sale = new Sales();
          sale.setProduct(rs.getString("p.category"));
            sale.setStore(rs.getString("s.store_state"));
            sale.setDateTime(rs.getInt("t.year"));
            sale.setSalesTotal(rs.getInt("sales_total"));
            sales.add(sale);*/
  
  
             //Display values
             /*System.out.print("City: " + category);
             System.out.print(", Store_state: " + storeState);
             System.out.print(", Year: " + year);
             System.out.print(", Sales_total: " + salesTotal);*/
         }
      } //end try
       finally{
        if (rs != null) try { rs.close(); } catch (SQLException ignore) {}
        if (statement != null) try { statement.close(); } catch (SQLException ignore) {}
        System.out.println("Closing....");
        if (conn != null) try { conn.close(); } catch (SQLException ignore) {} 
       } //end finally 
      return jsonArray; 
  } //dice
  
    
    
    
  
  
   public static void main(String[] args) {
     Connection conn = null;
     Statement stmt = null;
     try{
          //Register JDBC driver
          Class.forName("com.mysql.jdbc.Driver");
  
          //Open a connection
          System.out.println("Connecting to database...");
          conn = DriverManager.getConnection(DB_URL,USER,PASS);
  
          //Execute a query that creates the central cube
          System.out.println("Creating statement...");


          stmt.close();
          conn.close();
     }
     catch(SQLException se) {
       //Handles errors for JDBC
       se.printStackTrace();
     }
     catch (Exception e) {
       //Handles errors for Class.forName
       e.printStackTrace();
     } finally{
          //finally block used to close resources
          try{
             if(stmt!=null)
                stmt.close();
          }catch(SQLException se2){
          }// nothing we can do
          try{
             if(conn!=null)
                conn.close();
          }catch(SQLException se){
             se.printStackTrace();
          }//end finally try
     }//end try
     System.out.println("Closing");
   }//end main
} // end class