package com.Redster11.VaroChallenge;
import java.sql.*;
public class App {
  public static void main(String[] args) {
    System.out.println("Hello World!");
    try{// create our mysql database connection
      String myDriver = "com.mysql.cj.jdbc.Driver";
      String myUrl = "jdbc:mysql://localhost/mydb";
      Class.forName(myDriver);
      Connection conn = DriverManager.getConnection(myUrl, "root", "Root");
       // our SQL SELECT query. 
      // if you only need a few columns, specify them by name instead of using "*"
      String query = "SELECT P.*, A.*, AP.Active " +
      "FROM Address AS A, People AS P, PeopleToAddress AS AP" +
      "WHERE ((P.UserID=AP.UserID) And (AP.AddressID=A.AddressID) And (P.Email='kkirtland123@gmail.com'));";

      // create the java statement
      Statement st = conn.createStatement();

      // execute the query, and get a java resultset
      ResultSet rs = st.executeQuery(query);
      
      // iterate through the java resultset
      while (rs.next())
      {
        String firstName = rs.getString("FirstName");
        String lastName = rs.getString("LastName");
        Date dateCreated = rs.getDate("date");
        Boolean active = rs.getBoolean("Active");
        
        // print the results
        System.out.format("%s, %s, %s, %s\n", firstName, lastName, dateCreated, active);
      }
      st.close();
    }
    catch (Exception e)
    {
      System.err.println("Got an exception! ");
      System.err.println(e.getMessage());
    }
  }
}
