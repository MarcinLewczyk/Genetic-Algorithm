import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
	public static int populationSize = 5;
	public static int trainingPlansSize = 6;
	public static int generations = 20;
	public static int crossoverChance = 50; // 50%
	public static double mutationChance = 100;// 10% 
	
	public static int totalCalories = 300; //total amount of calories that user wants to burn
	public static int totalTime = 60; //wanted training duration
	
	public static double accuracyPercentage = 5;//accepted accuracy
	public static int tournamentSize = 3;//tournament selection
	
	public static boolean outside = false;
	public static boolean equipment = false;
	
	public static int outsidePenalty = 10;
	public static int equipmentPenalty = 10;
	
	public static void main(String[] args) {//to do - need to add stop condition (except generations number)
		Exercise[] allExercises = createExercises();
		TrainingPlan[] population = fillPopulation(allExercises);
		
		for(int i = 0; i < generations; i++) {
			int[] trainingPlansPoints = evaluate(population);
		/*	if(checkStopCondition()) {
				break;
			} */
			if(checkStopConditionWithPoints(trainingPlansPoints)) {
				System.out.println("Stop condition in generation " + i);
				break;
			}
			population = crossover(population, trainingPlansPoints);	
			//printPopulation(population);
			population = mutation(population, allExercises);
			trainingPlansPoints = evaluate(population);
			
			
		/*	printBestPlan(population);
			printPoints(population);
			*///printPopulation(population);
			
		}
		
	//	printPopulation(population);
		//printPoints(population);
		printBestPlan(population);
	}
	
	public static TrainingPlan[] fillPopulation(Exercise[] allExercises) {
		TrainingPlan[] population = new TrainingPlan[populationSize];
		for(int i = 0; i < populationSize; i++) {
			population[i] = initialize(allExercises);
		}
		return population;
	}
	
	public static Exercise[] createExercises() {
		Exercise[] exercises = new Exercise[30];
		exercises[0] = new Exercise("Ex1", "Legs", 10, 5, false, false);
		exercises[1] = new Exercise("Ex2", "Legs", 25, 15, true, false);
		exercises[2] = new Exercise("Ex3", "Chest", 30, 6, true, true);
		exercises[3] = new Exercise("Ex4", "Chest", 12, 8, false, true);
		exercises[4] = new Exercise("Ex5", "Arms", 8, 5, true, false);
		exercises[5] = new Exercise("Ex6", "Arms", 15, 10, false, false);
		exercises[6] = new Exercise("Ex7", "Back", 16, 20, false, false);
		exercises[7] = new Exercise("Ex8", "Back", 25, 5, false, true);
		exercises[8] = new Exercise("Ex9", "Shoulders", 35, 10, true, false);
		exercises[9] = new Exercise("Ex10", "Shoulders", 15, 7, false, false);
		
		exercises[10] = new Exercise("Ex11", "Legs", 5, 5, false, false);
		exercises[11] = new Exercise("Ex12", "Legs", 16, 15, false, false);
		exercises[12] = new Exercise("Ex13", "Chest", 5, 6, false, false);
		exercises[13] = new Exercise("Ex14", "Chest", 25, 8, false, false);
		exercises[14] = new Exercise("Ex15", "Arms", 15, 5, false, false);
		exercises[15] = new Exercise("Ex16", "Arms", 13, 10, false, false);
		exercises[16] = new Exercise("Ex17", "Back", 16, 20, false, false);
		exercises[17] = new Exercise("Ex18", "Back", 25, 5, false, false);
		exercises[18] = new Exercise("Ex19", "Shoulders", 26, 10, false, false);
		exercises[19] = new Exercise("Ex20", "Shoulders", 27, 7, false, false);
		
		exercises[20] = new Exercise("Ex21", "Legs", 22, 3, true, true);
		exercises[21] = new Exercise("Ex22", "Legs", 12, 3, true, false);
		exercises[22] = new Exercise("Ex23", "Chest", 17, 6, true, true);
		exercises[23] = new Exercise("Ex24", "Chest", 18, 6, false, true);
		exercises[24] = new Exercise("Ex25", "Arms", 8, 7, true, false);
		exercises[25] = new Exercise("Ex26", "Arms", 7, 7, false, false);
		exercises[26] = new Exercise("Ex27", "Back", 19, 10, false, false);
		exercises[27] = new Exercise("Ex28", "Back", 22, 10, false, true);
		exercises[28] = new Exercise("Ex29", "Shoulders", 25, 8, true, false);
		exercises[29] = new Exercise("Ex30", "Shoulders", 19, 8, false, false);
		
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
		double timePercentage = 0;
		double caloriesPercentage = 0;
		int pointsSum = 0;
		
		timePercentage = Math.abs(((double)timeSum / (double)totalTime) * 100);
		caloriesPercentage = Math.abs(((double)caloriesSum / (double)totalCalories) * 100);
		pointsSum = (int)((double)pointsSum - (timePercentage/100) - (caloriesPercentage/100));
		return pointsSum;
	}
	
	public static boolean checkStopConditionWithPoints(int[] points) {
		double pointsPercentage;
		for(int i = 0; i < points.length; i++) {
			pointsPercentage = Math.abs(((double)points[i]/((double)totalCalories + (double)totalTime)) * 100);
			System.out.println(pointsPercentage + "/" + accuracyPercentage);
			if(pointsPercentage <= accuracyPercentage) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean checkStopConditionWithSumAndCalories(int timeSum, int caloriesSum) {
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
			if(trainingPlansPoints[randomPosition] > bestPlanPoints) {
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
	
	public static void printBestPlan(TrainingPlan[] trainingPlans) {
		int[] plansPoints = evaluate(trainingPlans);
		int bestIndex = 0;
		int points = Integer.MIN_VALUE;
		for(int i = 0; i <plansPoints.length; i++) {
			if(plansPoints[i] > points) {
				points = plansPoints[i];
				bestIndex = i;
			}
		}
		System.out.println("Najlepszy plan z liczba punktow:  " + points);
		printTrainingPlan(trainingPlans[bestIndex]);
	}
}