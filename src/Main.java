import java.util.Random;

public class Main {
	public static int populationSize = 5;
	public static int generations = 100;
	public static double crossoverChance;
	public static double mutationChance;
	
	public static int totalCalories = 500; //total amount of calories that user wants to burn
	public static int totalTime = 30; //wanted training duration
	
	public static boolean outside = true;
	public static boolean equipment = false;
	
	public static int outsidePenalty = 50;
	public static int equipmentPenalty = 50;
	
	public static void main(String[] args) {
		TrainingPlan trainingPlan = initialize(createExercises());		
		printTrainingPlan(trainingPlan);
		
	}
	
	public static TrainingPlan initialize(Exercise[] allExercises) {
		Random randomPosition = new Random(System.currentTimeMillis());
		TrainingPlan trainingPlan = new TrainingPlan(populationSize);
		for(int i = 0; i < populationSize; i++) {
			Exercise randomExercise = allExercises[randomPosition.nextInt(allExercises.length - 1)]; // -1 so there won't be IndexOutOfBoundsException
			trainingPlan.addExercise(randomExercise, i);
		}
		return trainingPlan;
	}
	
	public static Exercise[] createExercises() {
		Exercise[] exercises = new Exercise[10];
		exercises[0] = new Exercise("Ex1", "Legs", 25.0, 5, false, false);
		exercises[1] = new Exercise("Ex2", "Legs", 50.0, 15, true, false);
		exercises[2] = new Exercise("Ex3", "Chest", 35.0, 6, true, true);
		exercises[3] = new Exercise("Ex4", "Chest", 45.0, 8, false, true);
		exercises[4] = new Exercise("Ex5", "Arms", 50.0, 5, true, false);
		exercises[5] = new Exercise("Ex6", "Arms", 35.0, 10, false, false);
		exercises[6] = new Exercise("Ex7", "Back", 60.0, 20, false, false);
		exercises[7] = new Exercise("Ex8", "Back", 25.0, 5, false, true);
		exercises[8] = new Exercise("Ex9", "Shoulders", 35.0, 10, true, false);
		exercises[9] = new Exercise("Ex10", "Shoulders", 45.0, 7, false, false);
		return exercises;
	}
	
	public static int[] evaluate(TrainingPlan trainingPlan) {
		int[] eval = new int[populationSize];
		for(int i = 0; i < eval.length; i++) {
			
		}
		
		return eval;
	}
	
	public void mutation() {
		
	}
	
	public void crossover() {
		
	}
	
	public static void printTrainingPlan(TrainingPlan trainingPlan) {
		Exercise[] toShow = trainingPlan.getExercisesInPlan();
		for(Exercise tp: toShow) {
			System.out.println(tp.getName() + " ");
		}
	}
}