package com.workout.diary.main;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;
import java.sql.Timestamp;
import java.util.logging.Logger;

/**This is the class that contains public static void main(String[] args) for the application and hence is the entry point*/
public class Main {

	private MySQLAccess sql;

	/**Entry point for the application
	 * @param args
	 */
	public static void main(String[] args) {
		Main main=new Main();
		main.mainInstance(args);

	}
	/**We want to have some member variables in our Main class, so we instantiate it. This then
	 * becomes the entry point for our instantiated main.
	 * @param args
	 */
	public void mainInstance(String[] args){
		sql=new MySQLAccess();
		try {
			sql.initDataBase();
			sql.testDataBaseRead();
		} catch (Exception e) {
			e.printStackTrace();
		}
		insertStarterValues();
		new DiaryApp(sql).mainMenu();
		
		//testMethods();
		this.sql.close();
	}
	/**
	 * This populates our database with some basic values
	 */
	private void insertStarterValues(){
		insertExerciseTypes("strength", "powerlifting", "bench, squat, deadlift");
		insertExerciseTypes("strength", "weightlifting","snatch+clean and press");
		insertExerciseTypes("strength", "compound","other compound exercises, f.ex frontsquat, incline bench, row, etc");
		insertExerciseTypes("strength", "isolation","isolation exercises, curls and similar");
		insertExercise("deadlift", "pick up weight, put it down",sql.getETypeId("strength", "powerlifting"));
		insertExercise("bench", "lower weight, push it up",sql.getETypeId("strength", "powerlifting"));
		insertExercise("squat", "put weight on back, bend down, stand up",sql.getETypeId("strength", "powerlifting"));
		insertExercise("snatch", "pick up weight, put it over your head", sql.getETypeId("strength", "weightlifting"));
		insertExercise("clean and jerk", "pick up weight to shoulders, then put it over your head", sql.getETypeId("strength", "weightlifting"));

	}
	/**This method is used as a testing area for new methods*/
	private void testMethods(){
		Workout w=new Workout(new Timestamp(System.currentTimeMillis()),sql);
		System.out.println(w);
		w.addSet("deadlift");
		w.addSet("deadlift");
		w.addSet("bench");
		w.setLoad(140);
		w.setNumberOfReps(5);
		w.setDuration(60);
		w.setPerformance(7);
		w.setNotes("This was a standard workout");
	}
	/**Insert an exercise type in the database
	 * @param type this is the main type
	 * @param subtype this is the subcategory
	 * @param subtype_description this is a detailed description of the type-subtype.
	 */
	public void insertExerciseTypes(String type, String subtype, String subtype_description){
		PreparedStatement ps=sql.getPreparedStatementForInsertion(MySQLAccess.DATABASE_NAME, MySQLAccess.EXERCISE_TYPE_TABLE,"e_type","subtype","subtype_description");
		try {
			ps.setString(1, type);
			ps.setString(2,subtype);
			ps.setString(3, subtype_description);
			System.out.println(ps.toString());
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**This inserts an exercise in the exercise table
	 * @param name Name of the exercise
	 * @param description Description of the exercise
	 * @param type_id Id for the type. This can be retrieved with the {@link MySQLAccess#getETypeId(String type, String subtype)} method.
	 */
	public void insertExercise(String name, String description, int type_id){
		PreparedStatement ps=sql.getPreparedStatementForInsertion(MySQLAccess.DATABASE_NAME, MySQLAccess.EXERCISE_TABLE,"exercise_name","description","type_id");
		try {
			ps.setString(1, name);
			ps.setString(2, description);
			ps.setInt(3, type_id);
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
