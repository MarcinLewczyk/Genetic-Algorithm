import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
	public static int populationSize = 5;
	public static int generations = 100;
	public static double crossoverChance;
	public static double mutationChance = 200;// 1% chance
	
	public static int totalCalories = 500; //total amount of calories that user wants to burn
	public static int totalTime = 30; //wanted training duration
	
	public static double accuracyPercentage = 1;//accepted accuracy
	
	public static boolean outside = true;
	public static boolean equipment = false;
	
	public static int outsidePenalty = 50;
	public static int equipmentPenalty = 50;
	
	public static void main(String[] args) {
		Exercise[] allExercises = createExercises();
		TrainingPlan[] population = fillPopulation(allExercises);
		printPopulation(population);
		mutation(population, allExercises);
		printPopulation(population);
	}
	
	public static TrainingPlan[] fillPopulation(Exercise[] allExercises) {
		TrainingPlan[] population = new TrainingPlan[populationSize];
		for(int i = 0; i < populationSize; i++) {
			population[i] = initialize(allExercises);
			//printTrainingPlan(population[i]);
		}
		return population;
	}
	
	public static Exercise[] createExercises() {
		Exercise[] exercises = new Exercise[10];
		exercises[0] = new Exercise("Ex1", "Legs", 25, 5, false, false);
		exercises[1] = new Exercise("Ex2", "Legs", 50, 15, true, false);
		exercises[2] = new Exercise("Ex3", "Chest", 35, 6, true, true);
		exercises[3] = new Exercise("Ex4", "Chest", 45, 8, false, true);
		exercises[4] = new Exercise("Ex5", "Arms", 50, 5, true, false);
		exercises[5] = new Exercise("Ex6", "Arms", 35, 10, false, false);
		exercises[6] = new Exercise("Ex7", "Back", 60, 20, false, false);
		exercises[7] = new Exercise("Ex8", "Back", 25, 5, false, true);
		exercises[8] = new Exercise("Ex9", "Shoulders", 35, 10, true, false);
		exercises[9] = new Exercise("Ex10", "Shoulders", 45, 7, false, false);
		return exercises;
	}
	
	public static TrainingPlan initialize(Exercise[] allExercises) {
		Random randomPosition = new Random();
		TrainingPlan trainingPlan = new TrainingPlan(populationSize);
		for(int i = 0; i < populationSize; i++) {
			Exercise randomExercise = allExercises[randomPosition.nextInt(allExercises.length)];
			trainingPlan.addExercise(randomExercise, i);
		}
		return trainingPlan;
	}
	
	public static int[] evaluate(TrainingPlan trainingPlan) {//trzeba jakos wymyslic sposob na punktowanie za calokszta³t
		int[] evaluation = new int[populationSize];
		int pointsSum;
		int timeSum = 0;
		double caloriesSum = 0;
		for(int i = 0; i < evaluation.length; i++) {
			Exercise exercise = trainingPlan.getExercisesInPlan()[i];
			pointsSum = 0;
			caloriesSum += exercise.getCalories();
			timeSum += exercise.getRequiredTime();
			
			if(!equipment && exercise.isEquipment()) {
				pointsSum += equipmentPenalty;
			}
			
			if(!outside && exercise.isOutside()) {
				pointsSum += outsidePenalty;
			}
			evaluation[i] = pointsSum;
		}
		return evaluation;
	}
	
	public static boolean checkStopCondition(TrainingPlan trainingPlan, int iteration) {
		boolean condition = false;
		if(iteration == generations) {
			condition = true;
		}
		/*if() {
			
		}*/
		return condition;
	}
	
	public static void crossover() {
		
	}
	
	public static TrainingPlan[] mutation(TrainingPlan[] trainingPlan, Exercise[] allExercises) {
		TrainingPlan[] mutatedPopulation = new TrainingPlan[trainingPlan.length];
		Random randomPosition = new Random();
		for(int i = 0; i < trainingPlan.length; i++) {
			Exercise[] exercises = trainingPlan[i].getExercisesInPlan();
			for(int j = 0; j < exercises.length; j++) {
				int randomMutationProb = ThreadLocalRandom.current().nextInt(0, 1001); //so chance = 10 is 1%
				if(mutationChance >= randomMutationProb) {
					trainingPlan[i].getExercisesInPlan()[j] = allExercises[randomPosition.nextInt(allExercises.length)];
				}
			}
			mutatedPopulation[i] = trainingPlan[i];
		}
		return mutatedPopulation;
	}
	
	public static void printPopulation(TrainingPlan[] population) {
		for(TrainingPlan tp: population) {
			printTrainingPlan(tp);
			System.out.println("End of training plan");
			System.out.println();
		}	
	}
	
	public static void printTrainingPlan(TrainingPlan trainingPlan) {
		Exercise[] toShow = trainingPlan.getExercisesInPlan();
		for(Exercise e: toShow) {
			System.out.println(e.getName() + " " + e.getMusclePart());
		}
	}
}