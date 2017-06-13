package com.workout.diary.main;

import java.security.interfaces.RSAKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

/**This class executes most of the queries on the database
 * @author Ketil
 *
 */
/**
 * @author Ketil
 *
 */
/**
 * @author Ketil
 *
 */
public class MySQLAccess {
	static final String DATABASE_NAME="workout_diary";
	static final String EXERCISE_TABLE="exercise";
	static final String EXERCISE_TYPE_TABLE="exercise_type";
	static final String SET_TABLE="w_set";
	static final String WORKOUT_TABLE="workout";
	
	private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;

    
    /**This connects to the databas and throws an exception if it does not work. It expects that a database is hosted on localhost named workout_diary with a user named admin
     * and a password named password.
     * 
     * @throws Exception
     */
    public void initDataBase() throws Exception {
            try {
                    // This will load the MySQL driver, each DB has its own driver
                    Class.forName("com.mysql.jdbc.Driver");
                    // Setup the connection with the DB
                    connect = DriverManager
                                    .getConnection("jdbc:mysql://localhost/workout_diary?"
                                                    + "user=admin&password=password");

                    // Statements allow to issue SQL queries to the database
                    statement = connect.createStatement();

    		} 
            catch (Exception e) {
    			throw e;
    		} 
    }
    
    /**This simply returns a {@linkplain PreparedStatement} constructed from the given sql string.
     * @param sqlString
     * @return {@linkplain PreparedStatement}
     */
    public PreparedStatement getPreparedStatementFromSqlString(String sqlString){
    	try {
			return connect.prepareStatement(sqlString);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    
    /**This creates a {@linkplain PreparedStatement} that can be used for inserting values in the columns of the last variable length argument
     * @param database
     * @param table
     * @param columnNames
     * @return
     */
    public PreparedStatement getPreparedStatementForInsertion(String database,String table, String... columnNames){
			try {
				StringBuilder sb=new StringBuilder();
				sb.append("insert into ").append(database).append(".").append(table).append("( ");
				for (int x=0;x<columnNames.length-1;x++){
					sb.append(columnNames[x]).append(",");
				}
				sb.append(columnNames[columnNames.length-1]).append(") values (");
				for(int x=0;x<columnNames.length-1;x++){
					sb.append("?");
					sb.append(",");
				}
				sb.append("?)");
				System.out.println(sb.toString());
				preparedStatement = connect.prepareStatement(sb.toString(),Statement.RETURN_GENERATED_KEYS);
				//preparedStatement = connect.prepareStatement("insert into "+database+"."+table+" values (default, ?, ?, ?, ? , ?, ?)");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return preparedStatement;
    }
    
    /**This simply returns a {@linkplain ResultSet} containing all the rows of the given table
     * @param database
     * @param table
     * @return
     */
    public ResultSet getAllRows(String database, String table){
		ResultSet resultSet=null;
		StringBuilder sb=new StringBuilder();
		sb.append("select * from ").append(database).append(".").append(table);
		System.out.print(sb.toString());
		
		try {
			preparedStatement=connect.prepareStatement(sb.toString());
			System.out.println(preparedStatement);
			resultSet = preparedStatement.executeQuery();
			return resultSet;
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
    }
    
	/**This returns the id of a row in the exercise_type table given an identifying type and subtype
	 * @param type
	 * @param subtype
	 * @return
	 */
	public Integer getETypeId(String type, String subtype){
		ResultSet resultSet=null;
		StringBuilder sb=new StringBuilder();
		sb.append("select * from ").append(DATABASE_NAME).append(".").append(EXERCISE_TYPE_TABLE).append(" where (e_type=? and subtype=?)");
		System.out.print(sb.toString());
		
		try {
			preparedStatement=connect.prepareStatement(sb.toString());
			preparedStatement.setString(1,type);
			preparedStatement.setString(2, subtype);
			System.out.println(preparedStatement);
			resultSet = preparedStatement.executeQuery();
			resultSet.first();
			return resultSet.getInt(1);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
    
    /**
     * This is a simple function to test reading from the database
     */
    public void testDataBaseRead(){
        // Result set get the result of the SQL query
    	ResultSet resultSet=null;
        try {
			resultSet = statement.executeQuery("select * from workout_diary.exercise");
	        writeTestResultSet(resultSet);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally{
            if (resultSet != null) {
                try {
					resultSet.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
    }
                    
	
	
	
	
    /**This is code to test getting data from a resultset
     * @param resultSet
     * @throws SQLException
     */
    private void writeTestResultSet(ResultSet resultSet) throws SQLException {
        // ResultSet is initially before the first data set
        while (resultSet.next()) {
                // It is possible to get the columns via name
                // also possible to get the columns via the column number
                // which starts at 1
                // e.g. resultSet.getSTring(2);
                String user = resultSet.getString("exercise_name");
                String website = resultSet.getString("e_type");
                String summary = resultSet.getString("description");
                System.out.println("User: " + user);
                System.out.println("Website: " + website);
                System.out.println("summary: " + summary);
        }
    }
    public void close() {
        try {

                if (statement != null) {
                        statement.close();
                }

                if (connect != null) {
                        connect.close();
                }
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
	/**This code creates an sql query that returns the total amount lifted in a given exercise for the last given number of days
	 * @param nrDays
	 * @param excName
	 */
	public void getTonnageForLastDays(int nrDays, String excName) {
		Timestamp now=new Timestamp(System.currentTimeMillis());
		StringBuilder sb=new StringBuilder();
		sb.append("select workout.workout_id,w_date_time, exercise_name, sum(actual_load) as sum_load "
				+ "from workout join w_set "
				+ "on workout.workout_id=w_set.workout_id "
				+ "where w_date_time between date_sub(now(),interval ")
				.append(nrDays).append(" day) and now() and exercise_name='").append(excName).append("' group by(workout_id) order by sum_load desc;");
		try {
			preparedStatement=connect.prepareStatement(sb.toString());
			System.out.println(preparedStatement);
			ResultSet resultSet = preparedStatement.executeQuery();

			if(!resultSet.first()){
				System.out.println("No workouts in period");
				return;
			}
			ArrayList<TonnageParam> tons=new ArrayList<MySQLAccess.TonnageParam>();
			while(!resultSet.isAfterLast()){
				tons.add(new TonnageParam(resultSet));
				resultSet.next();
			}
			System.out.println(tons);
			
		}catch(SQLException e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * @author Ketil
	 *Convenience class to make it easier to read the results of the query in {@link MySQLAccess# getTonnageForLastDays}
	 */
	private class TonnageParam{
		private int wId;
		private Timestamp dateTime;
		private String exc_name;
		private int set_nr;
		private long sumLoad;
		public TonnageParam(ResultSet rs) throws SQLException{
			this(rs.getInt(1), rs.getTimestamp(2), rs.getString(3), rs.getLong(4));
		}
		public TonnageParam(int wId, Timestamp dateTime, String exc_name, long sumLoad){
			this.wId = wId;
			this.dateTime = dateTime;
			this.exc_name = exc_name;
			this.sumLoad = sumLoad;
		}
		public String toString(){
			return new StringBuilder().append(wId).append("\t").append(dateTime).append("\t")
					.append(exc_name).append("\t").append(sumLoad)
					.append("\n").toString();
		}
	}
	
	/**This method gets the number of workouts for a given number of days back in time with an sql query.
	 * @param nrDays
	 * @return
	 */
	public Pair<Integer, Double> getNumberOfWorkoutsAndDurationForPeriod(int nrDays) {
		StringBuilder sb=new StringBuilder().append("select count(workout_id),sum(duration) "
				+ "from workout_diary.workout "
				+ "where w_date_time between date_sub(now(),interval ")
				.append(nrDays).append(" day) and now()");
		try {
			preparedStatement=connect.prepareStatement(sb.toString());
			System.out.println(preparedStatement);
			ResultSet rs=preparedStatement.executeQuery();
			if(!rs.first()){
				return null;
			}
			return new Pair<Integer, Double>(rs.getInt(1), rs.getDouble(2));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
