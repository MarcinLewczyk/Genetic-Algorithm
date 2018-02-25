import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
	public static int populationSize = 5;
	public static int trainingPlansSize = 6;
	public static int generations = 100;
	public static int crossoverChance = 50; // 25%
	public static double mutationChance = 10;// 20% 
	
	public static int totalCalories = 300; //total amount of calories that user wants to burn
	public static int totalTime = 60; //wanted training duration
	
	public static double accuracyPercentage = 1;//accepted accuracy
	public static int tournamentSize = 3;//tournament selection
	
	public static boolean outside = false;
	public static boolean equipment = false;
	
	public static int outsidePenalty = 50;
	public static int equipmentPenalty = 50;
	
	public static void main(String[] args) {//to do - need to add stop condition (except generations number)
		Exercise[] allExercises = createExercises();
		TrainingPlan[] population = fillPopulation(allExercises);
		
		for(int i = 0; i < generations; i++) {
			System.out.println(i);
			int[] trainingPlansPoints = evaluate(population);
		/*	if(checkStopCondition()) {
				break;
			} */
			population = crossover(population, trainingPlansPoints);	
			printPopulation(population);
			population = mutation(population, allExercises);
			printPopulation(population);
		//	trainingPlansPoints = evaluate(population);
		}
		
		printPopulation(population);
		printPoints(population);
	}
	
	public static TrainingPlan[] fillPopulation(Exercise[] allExercises) {
		TrainingPlan[] population = new TrainingPlan[populationSize];
		for(int i = 0; i < populationSize; i++) {
			population[i] = initialize(allExercises);
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
		TrainingPlan trainingPlan = new TrainingPlan(trainingPlansSize);
		for(int i = 0; i < trainingPlansSize; i++) {
			Exercise randomExercise = allExercises[randomPosition.nextInt(allExercises.length)];
			trainingPlan.addExercise(randomExercise, i);
		}
		return trainingPlan;
	}
	
	public static int[] evaluate(TrainingPlan[] trainingPlans) {
		int[] evaluation = new int[populationSize];
		int pointsSum = 0;
		int timeSum = 0;
		int caloriesSum = 0;
		TrainingPlan trainingPlanToEval;
		Exercise[] exercisesToEval;
		Exercise exerciseToEval;
		for(int i = 0; i < trainingPlans.length; i++) {
			trainingPlanToEval = trainingPlans[i];
			exercisesToEval = trainingPlanToEval.getExercisesInPlan();
			caloriesSum = 0;
			timeSum = 0;
			for(int j = 0; j < exercisesToEval.length; j++) {
				exerciseToEval = exercisesToEval[j];	
				caloriesSum += exerciseToEval.getCalories();
				timeSum += exerciseToEval.getRequiredTime();
				
				if(!equipment && exerciseToEval.isEquipment()) {
					pointsSum -= equipmentPenalty;
				}
			
				if(!outside && exerciseToEval.isOutside()) {
					pointsSum -= outsidePenalty;
				}
			}
			pointsSum += calculatePoints(timeSum, caloriesSum);
			evaluation[i] = pointsSum;
		}
		return evaluation;
	}
	
	public static int calculatePoints(int timeSum, int caloriesSum) {
		int timePercentage = 0;
		int caloriesPercentage = 0;
		int pointsSum = 0;
		
		timePercentage = Math.abs((timeSum / totalTime) * 100);
		caloriesPercentage = Math.abs((caloriesSum / totalCalories) * 100);
		pointsSum = pointsSum - (timePercentage/100) - (caloriesPercentage/100);
		return pointsSum;
	}
	
	public static boolean checkStopCondition(int timeSum, int caloriesSum) {
		int caloriesRange = (int)((accuracyPercentage/100) * totalCalories); 
		int timeRange = (int)((accuracyPercentage/100) * totalTime);
		if(checkIfTimeIsInGivenRange(timeSum, timeRange) && checkIfCaloriesIsInGivenRange(caloriesSum, caloriesRange)) {
			return true;
		}
		return false;
	}
	
	public static boolean checkIfTimeIsInGivenRange(int timeSum, int range) {
		return (timeSum < totalTime + range) && (timeSum > totalTime - range);
	}
	
	public static boolean checkIfCaloriesIsInGivenRange(int caloriesSum, int range) {
		return (caloriesSum < totalCalories + range) && (caloriesSum > totalCalories - range);
	}
	
	public static TrainingPlan[] crossover(TrainingPlan[] population, int[] trainingPlansPoints) {
		TrainingPlan[] selectedPopulation = new TrainingPlan[population.length];
		for(int i = 0; i < selectedPopulation.length; i++) {
			TrainingPlan parent1 = population[selection(trainingPlansPoints)];
			int randomCrossoverProb = ThreadLocalRandom.current().nextInt(0, 101);
			if(randomCrossoverProb <= crossoverChance) {
				TrainingPlan parent2 = population[selection(trainingPlansPoints)];
				Exercise[] firstParentExercises = parent1.getExercisesInPlan();
				Exercise[] secondParentExercises = parent2.getExercisesInPlan();
				int cuttingPosition = ThreadLocalRandom.current().nextInt(0, trainingPlansSize);
				TrainingPlan child = new TrainingPlan(trainingPlansSize);
				Exercise[] childExercises = new Exercise[trainingPlansSize];
				for(int j = 0; j < trainingPlansSize; j++) {
					if(j < cuttingPosition) {
						childExercises[j] = firstParentExercises[j];
					} else {
						childExercises[j] = secondParentExercises[j];
					}
				}
				child.setExercisesInPlan(childExercises);
				selectedPopulation[i] = child;
			} else {
				selectedPopulation[i] = parent1;
			}
		}
		return selectedPopulation;
	}
	
	public static int selection(int[] trainingPlansPoints) {
		int bestPlanPoints = Integer.MIN_VALUE;
		int bestPlanIndex = 0;
		for(int i = 0; i < tournamentSize; i++) {
			int randomPosition = ThreadLocalRandom.current().nextInt(0, populationSize);
			if(bestPlanPoints > trainingPlansPoints[randomPosition]) {
				bestPlanPoints = trainingPlansPoints[randomPosition];
				bestPlanIndex = randomPosition;
			}
		}
		return bestPlanIndex;
	}
	
	public static TrainingPlan[] mutation(TrainingPlan[] population, Exercise[] allExercises) {
		TrainingPlan[] mutatedPopulation = new TrainingPlan[population.length];
		Random randomPosition = new Random();
		for(int i = 0; i < population.length; i++) {
			Exercise[] exercises = population[i].getExercisesInPlan();
			for(int j = 0; j < exercises.length; j++) {
				int randomMutationProb = ThreadLocalRandom.current().nextInt(0, 1001); //so chance = 10 is 1%
				if(mutationChance >= randomMutationProb) {
					population[i].getExercisesInPlan()[j] = allExercises[randomPosition.nextInt(allExercises.length)];
				}
			}
			mutatedPopulation[i] = population[i];
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
	
	public static void printPoints(TrainingPlan[] trainingPlans) {
		int[] pointsToPrint = evaluate(trainingPlans);
		for(int i = 0; i < pointsToPrint.length; i++) {
			System.out.println("Plan " + i + " " + "Points: " + pointsToPrint[i]);
		}
	}
}