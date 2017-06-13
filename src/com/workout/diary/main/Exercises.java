package com.workout.diary.main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


/**An instance read all exercises in database on initialization
 * @author Ketil
 *
 */
public class Exercises {
	ArrayList<Exercise> exercises=new ArrayList<Exercise>();
	public Exercises(MySQLAccess sql){
		ResultSet rs=sql.getAllRows(MySQLAccess.DATABASE_NAME, MySQLAccess.EXERCISE_TABLE);
		try {
			while(rs.next()){
			exercises.add(new Exercise(rs.getString(1),rs.getString(2),rs.getInt(3)));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String toString(){
		return exercises.toString();
	}
	public ArrayList<Exercise> getAllExercises(){
		return exercises;
	}
	
	/**Represents a single row in the exercise table.
	 * @author Ketil
	 *
	 */
	public class Exercise{
		private String exerciseName;
		private String description;
		private Integer typeId;

		public Exercise(String exerciseName, String description, int typeId) {
			this.exerciseName = exerciseName;
			this.description = description;
			this.typeId = typeId;
		}
		
		/**Gets the name of the exercise in the row that this instance represents
		 * @return
		 */
		public String getName(){
			return exerciseName;
		}
		/**Gets the description of the exercise in the row that this instance represents
		 * @return
		 */
		public String getDescription(){
			return description;
		}
		
		/**Gets the type_id of the exercise in the row that this instance represents
		 * @return
		 */
		public Integer getTypeId(){
			return typeId;
		}
		public String toString(){
			return exerciseName+" "+description+" "+typeId.toString();
		}

	}
}
