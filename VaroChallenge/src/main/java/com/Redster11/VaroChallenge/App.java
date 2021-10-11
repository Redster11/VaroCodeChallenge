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
      String query = "SELECT * FROM people";

      // create the java statement
      Statement st = conn.createStatement();

      // execute the query, and get a java resultset
      ResultSet rs = st.executeQuery(query);
      
      // iterate through the java resultset
      while (rs.next())
      {
        String firstName = rs.getString("FirstName");
        String lastName = rs.getString("LastName");
        String email = rs.getString("email");
        
        // print the results
        System.out.format("%s, %s, %s\n", firstName, lastName, email);
      }
      

      query = "SELECT * FROM address";
      rs = st.executeQuery(query);
      while (rs.next())
      {
        String line1 = rs.getString("line1");
        String city = rs.getString("city");
        String state = rs.getString("state");
        String zip = rs.getString("zip");
        String date = rs.getString("date");

        System.out.format("%s, %s, %s, %s, %s\n", line1, city, state, zip, date);
      }
      String input = "bdoney2000@yahoo.com";
      query = "SELECT people.FirstName, people.lastName, people.email, address.line1, address.line2, address.city, address.state, address.zip, peopletoaddress.active" +
      " FROM people, address, peopletoaddress" +
      " Where people.UserID = peopletoaddress.UserID AND address.addressID = peopletoaddress.addressID AND people.email = \"" + input + "\"";
      rs = st.executeQuery(query);
      while (rs.next())
      {
        String firstName = rs.getString("FirstName");
        String lastName = rs.getString("LastName");
        String email = rs.getString("email");
        String line1 = rs.getString("line1");
        String city = rs.getString("city");
        String state = rs.getString("state");
        String zip = rs.getString("zip");
        String active = rs.getString("active");

        System.out.format("%s, %s, %s, %s, %s, %s, %s, %s\n", firstName, lastName, email, line1, city, state, zip, active);
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
