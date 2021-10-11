package com.Redster11.VaroChallenge;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

public class App {
  /**
   *
   */
  public static void main(String[] args) {
    System.out.println("Hello World!");
    try{// create our mysql database connection
      String myDriver = "com.mysql.cj.jdbc.Driver";
      String myUrl = "jdbc:mysql://localhost/mydb";
      Class.forName(myDriver);
      //connection to DB 
      Connection conn = DriverManager.getConnection(myUrl, "root", "Root");
      //grab all users
      Vector<String[]> listUsers = new Vector<String[]>();
      listUsers.addAll(ListUsers(conn));
      UpsertUser(conn, "45", "Red", "Gibbler", "Red.Gib@gmail.com", "8", "1234 Test St", "Suite 123", "Los Angeles", "CA", "90001", "45");
      //grab user based on UserID
      Vector<String[]> UserDetails = new Vector<String[]>();
      UserDetails.addAll(GetUserDetails(conn, "1"));
      System.out.format("FirstName: %s, LastName: %s, Email: %s Deleted: %s\n", UserDetails.get(0)[0], UserDetails.get(0)[1], UserDetails.get(0)[2], UserDetails.get(0)[3]);
      for(int i = 1; i < UserDetails.size(); i++){
        System.out.format("AddressID: %s line1: %s, line2: %s city: %s state: %s Zip: %s Date: %s active: %s\n",UserDetails.get(i)[0], UserDetails.get(i)[1], UserDetails.get(i)[2], UserDetails.get(i)[3], UserDetails.get(i)[4], UserDetails.get(i)[5], UserDetails.get(i)[6], UserDetails.get(i)[7]);
      }
      //check if a user Exists
      System.out.println(IsUserExists(conn, "kkirtland123@gmail.com"));
      System.out.println(IsUserExists(conn, "b.doney200@gmail.com"));
      
    }
    catch (Exception e)
    {
      System.err.println("Got an exception! ");
      System.err.println(e.getMessage());
    }
  }
  public static void UpsertUser(Connection conn, String UserID, String FirstName, String LastName, String Email, String AddressID, String Line1, String Line2, String City, String State, String ZIP, String ptaID) throws SQLException{
    Statement st = conn.createStatement();
    if(!IsUserExists(conn, Email)){ // Does not exist
      String Insert = "INSERT INTO people VALUES ('"+UserID+"', '"+FirstName+"', '"+LastName+"', '"+Email+"', false)";
      // execute the query, and get a java resultset
      st.executeUpdate(Insert);
      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      Insert = "INSERT INTO address VALUES ("+AddressID+", " +Line1+", "+ Line2 +", "+ City+", "+State+", "+ZIP+", "+ dtf.toString()+")";
      st.executeUpdate(Insert);
      Insert = "INSERT INTO peopletoaddress VALUES (" + ptaID +", "+ AddressID +", "+ UserID +", 1)";
      st.executeUpdate(Insert);
    }
    else{
      String Update = "UPDATE People SET Firstname = " + FirstName + ", LastName = " + LastName + ", Email = " + Email + ", Deleted = falase WHERE UserID = "+UserID+")";
      st.executeUpdate(Update);
      UpdateAddress(conn, UserID, AddressID, Line1, Line2, City, State, ZIP, ptaID);
    }
    st.close();
  }
  public static Boolean IsUserExists(Connection conn, String Email) throws SQLException{
    Statement st = conn.createStatement();
    String query = "SELECT people.FirstName, people.lastName, people.email, address.line1, address.line2, address.city, address.state, address.zip, peopletoaddress.active" +
      " FROM people, address, peopletoaddress" +
      " Where people.UserID = peopletoaddress.UserID AND address.addressID = peopletoaddress.addressID AND people.email = \"" + Email + "\"";

    // execute the query, and get a java resultset
    ResultSet rs = st.executeQuery(query);

    Boolean exists = false;
      rs = st.executeQuery(query);
      while (rs.next())
      {
        exists = true;
        break;
      }
      st.close();
      return exists;
  }
  //returns a vector in the form (UserID, First, Last, Email, deleted)
  public static Vector<String[]> ListUsers(Connection conn) throws SQLException{
    Statement st = conn.createStatement();
    String query = "SELECT * FROM people";
    ResultSet rs = st.executeQuery(query);
    Vector<String[]> UsersInfo = new Vector<String[]>();
      // iterate through the java resultset
      while (rs.next())
      {
        String[] UserInfo = new String[5];
        UserInfo[0] = rs.getString("UserID");
        UserInfo[1] = rs.getString("FirstName");
        UserInfo[2] = rs.getString("LastName");
        UserInfo[3] = rs.getString("email");
        UserInfo[4] = rs.getString("deleted");
        
        // print the results
        System.out.format("%s, %s, %s, %s, %s\n", UserInfo[0], UserInfo[1], UserInfo[2], UserInfo[3], UserInfo[4]);
        UsersInfo.add(UserInfo);
      }
      st.close();
      return UsersInfo;
  }
  //Returns a vector where index 0 == userInfo (first, last, email, deleted) then all other == past addresses (addressID, line1, line2, city, state, zip, Date, active)
  public static Vector<String[]> GetUserDetails(Connection conn, String UserID) throws SQLException{
    Statement st = conn.createStatement();
    Vector<String[]> Details = new Vector<String[]>(); 
    String query1 = "SELECT DISTINCT people.* FROM people WHERE people.UserID = " + UserID +";";
    ResultSet rs = st.executeQuery(query1);
    while (rs.next())
    {
      String[] data = new String[4];
      data[0] = rs.getString("FirstName");
      data[1] = rs.getString("LastName");
      data[2] = rs.getString("Email");
      data[3] = rs.getString("Deleted");
      Details.add(data);
    }
    String query2 = "SELECT address.*, peopletoaddress.active FROM address, peopletoaddress WHERE address.addressID = peopletoaddress.addressID AND peopletoaddress.UserID = " + UserID +";";
    rs = st.executeQuery(query2);
    while (rs.next())
    {
      String[] data = new String[8];
      data[0] = rs.getString("addressID");
      data[1] = rs.getString("line1");
      data[2] = rs.getString("line2");
      data[3] = rs.getString("city");
      data[4] = rs.getString("state");
      data[5] = rs.getString("zip");
      data[6] = rs.getString("date");
      data[7] = Boolean.toString(rs.getBoolean("active"));
      Details.add(data);
    }
    st.close();
    return Details;
  }
  public static void UpdateAddress(Connection conn, String UserID, String AddressID, String Line1, String Line2, String City, String State, String ZIP, String ptaID) throws SQLException{
    Statement st = conn.createStatement();
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String Insert = "INSERT INTO address VALUES ("+AddressID+", " +Line1+", "+ Line2 +", "+ City+", "+State+", "+ZIP+", "+ dtf.toString()+")";
    st.executeUpdate(Insert);
    Vector<String[]> UserDetails = new Vector<String[]>();
    UserDetails.addAll(GetUserDetails(conn, UserID));
    for(int i = 1; i < UserDetails.size(); i++){
      if(UserDetails.get(i)[7] == "true"){
        String Update = "UPDATE peopletoaddress SET active = 0 WHERE UserID = " + UserID + " AND AddressID = " + AddressID;
        st.executeUpdate(Update);
      }
    }
    Insert = "INSERT INTO peopletoaddress VALUES (" + ptaID +", "+ AddressID +", "+ UserID +", 1)";
    st.executeUpdate(Insert);
    st.close();
  }
  public static void DeleteUser(Connection conn, String UserID)throws SQLException{
    Statement st = conn.createStatement();
    String Update = "UPDATE people SET deleted = 1 WHERE UserID = " + UserID;
    st.executeUpdate(Update);
    st.close();
  }
}
