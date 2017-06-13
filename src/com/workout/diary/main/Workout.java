package com.workout.diary.main;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;


/**
 * @author Ketil
 *This class represents a workout.
 */
/**
 * @author Ketil
 *
 */
public class Workout {
	Integer id=null;
	double duration;
	String notes;
	int performance;
	Timestamp dateTime;
	Integer workoutId=null;
	MySQLAccess sql;
	ArrayList<Set> sets=new ArrayList<Set>();
	/**
	 * Setting default constructor to private to avoid accidental instantiation.
	 */
	@SuppressWarnings("unused")
	private Workout(){
		
	}
	
	/**This creates a new Workout object with the given datetime and writes it to the database.
	 * @param dateTime
	 * @param sql
	 */
	public Workout(Timestamp dateTime, MySQLAccess sql){
		this.dateTime=dateTime;
		this.sql=sql;
		PreparedStatement ps=sql.getPreparedStatementForInsertion(MySQLAccess.DATABASE_NAME, MySQLAccess.WORKOUT_TABLE,"w_date_time" );
		try {
			ps.setTimestamp(1, dateTime);
			ps.execute();
			ResultSet rs=ps.getGeneratedKeys();
			rs.first();
			this.workoutId=rs.getInt(1);
			rs.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**This creates a prepared statement that allows for updating a value in the workout table
	 * @param col The name of the column you want to update
	 * @return
	 */
	private PreparedStatement getUpdateStatement(String col){
	      String sqlString = "UPDATE "+ MySQLAccess.DATABASE_NAME+"."+MySQLAccess.WORKOUT_TABLE+ " SET "+col
	    		  +" = ? WHERE ((workout_id='"+this.workoutId.toString()+"'))";
	      return sql.getPreparedStatementFromSqlString(sqlString);
	}
	
	/**This stores the duration of the workout in the database
	 * @param minutes
	 */
	public void setDuration(double minutes){
		PreparedStatement ps =getUpdateStatement("duration");
		try {
			ps.setDouble(1, minutes);
			ps.execute();
			this.duration=minutes;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**This stores the user performance in the database
	 * @param perf
	 */
	public void setPerformance(int perf){
		PreparedStatement ps =getUpdateStatement("performance");
		try {
			ps.setInt(1, perf);
			System.out.println(ps);
			ps.execute();
			this.performance=perf;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**This stores the notes from the workout in the database
	 * @param notes
	 */
	public void setNotes(String notes){
		PreparedStatement ps =getUpdateStatement("notes");
		try {
			ps.setString(1, notes);
			ps.execute();
			this.notes=notes;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**This adds a set to the workout
	 * @param exerciseName
	 */
	public void addSet(String exerciseName){
		sets.add(new Set(this.workoutId,exerciseName,sets));
		getLastSet(sets).saveSet();
	}
	private static Set getLastSet(ArrayList<Set> sets){
		if (sets != null && !sets.isEmpty()) {
			return sets.get(sets.size()-1);
		}
		return null;
	}
	public String toString(){
		return new StringBuilder().append("workoutId: ").append(workoutId).append(" ").append(dateTime).append("List of sets: ").append(sets).toString();
	}
	
	/**Stores the load for the last set in the current workout
	 * @param load
	 */
	public void setLoad(int load){
		getLastSet(sets).updateLoad(load);
	}
	/**Stores the number of reps for the last set in the current workout
	 * @param reps
	 */
	public void setNumberOfReps(int reps){
		getLastSet(sets).updateReps(reps);
	}
	
	/**Each set in the workout is represented by a separate instance of this class. It also contains methods to u
	 *
	 * @author Ketil
	 *
	 */
	private class Set{
		boolean stored=false;
		String exerciseName=null;
		Integer workoutId=null;
		Integer setNr=null;
		Integer actualLoad=null;
		Integer actualRepetitions=null;
		Integer goal_id=null;
		Integer set_goal_nr=null;
		
		/**
		 * @param workoutId
		 * @param exerciseName
		 * @param sets An ArrayList containing all sets done previously in the workout
		 */
		public Set(Integer workoutId,String exerciseName,ArrayList<Set> sets){
			this.workoutId=workoutId;
			this.exerciseName=exerciseName;
			if (sets != null && !sets.isEmpty()) {
				if(getLastSet(sets).exerciseName.equals(exerciseName)){
					this.setNr=getLastSet(sets).setNr+1;
				}
				else{
					this.setNr=1;
				}				  
			}else{
				this.setNr=1;
			}
		}
		
		/**
		 * Writes set to database
		 */
		public void saveSet(){
			PreparedStatement ps=sql.getPreparedStatementForInsertion(MySQLAccess.DATABASE_NAME, MySQLAccess.SET_TABLE, "workout_id", "exercise_name", "set_nr");
			try {
				ps.setInt(1,this.workoutId);
				ps.setString(2, this.exerciseName);
				ps.setInt(3, this.setNr);
				ps.execute();
				stored=true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/**This creates a statement that can be used for updating a value in the set table. 
		 * @param col The column to update
		 * @return
		 */
		private PreparedStatement getUpdateStatement(String col){
		      String sqlString = "UPDATE "+ MySQLAccess.DATABASE_NAME+"."+MySQLAccess.SET_TABLE+ " SET "+col+" = ? WHERE ((workout_id='"+this.workoutId.toString()
		      +"') and (exercise_name='"+this.exerciseName+"') and (set_nr='"+this.setNr+"'))";
		      return sql.getPreparedStatementFromSqlString(sqlString);
		}
		
		/**Writes the load for this set to database
		 * @param load
		 */
		public void updateLoad(int load){
			System.out.println(load);
			PreparedStatement ps=getUpdateStatement("actual_load");
			try {
				ps.setInt(1, load);
				System.out.println(ps);
				ps.execute();
				this.actualLoad=load;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/**Writes the reps for this set to database
		 * @param reps
		 */
		public void updateReps(int reps){
			PreparedStatement ps=getUpdateStatement("actual_repetitions");
			try {
				ps.setInt(1, reps);
				System.out.println(ps);
				ps.execute();
				this.actualRepetitions=reps;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public String toString(){
			return new StringBuilder().append("w_id/exc_name/set_nr: ").append(this.workoutId).append("/").append(this.exerciseName).append("/").append(this.setNr).toString();
		}
	}
}
