import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
	public static int populationSize = 10;
	public static int trainingPlansSize = 8;
	public static int generations = 75;
	public static int crossoverChance = 80; // 80%
	public static double mutationChance = 5; // 0.5% 
	
	public static int totalCalories = 600; //total amount of calories that user wants to burn
	public static int totalTime = 60; //wanted training duration
	
	public static double accuracyPercentage = 3; //accepted accuracy (it is total accuracy which means ((points from eval)/(totalCalories + totalTime))*100
	public static int tournamentSize = 3; //tournament selection
	
	public static boolean outside = false;  //false means that we don't want that
	public static boolean equipment = false;
	
	public static int outsidePenalty = 30;
	public static int equipmentPenalty = 30;
	
	public static int numberOfAlgorithmLaunches = 10;
	
	public static void main(String[] args) {
	//	oneIteration();
		multipleIterationsWithFileOutput();
	}
	
	public static void oneIteration() {
		Exercise[] allExercises = createExercises();
		TrainingPlan[] population = fillPopulation(allExercises);
		for(int i = 0; i < generations; i++) {	
			int[] trainingPlansPoints = evaluate(population);
			if(checkStopConditionWithPoints(trainingPlansPoints)) {
				System.out.println("Stop condition in generation " + i);
				break;
			}
			population = crossover(population, trainingPlansPoints);	
			population = mutation(population, allExercises);
			trainingPlansPoints = evaluate(population);		
		}
		printPopulation(population);
		printPoints(population);
		printBestPlan(population);
	}
	
	public static void multipleIterationsWithFileOutput() {
		String fileName = "Results.txt";
		File file = new File(fileName);
		
		int[] avgBest = new int[generations];
		int[] avgWorst = new int[generations];
		int[] avgAvg = new int[generations];
		
		Exercise[] allExercises = createExercises();
		
		try{
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fileWriter);  
			
			for(int i = 0; i < numberOfAlgorithmLaunches; i++) {			
	    		TrainingPlan[] population = fillPopulation(allExercises);
				for(int j = 0; j < generations; j++) {
					int best = Integer.MAX_VALUE;
			    	int worst = Integer.MIN_VALUE;
			    	int avg = 0;
			    	
					int[] trainingPlansPoints = evaluate(population);
					population = crossover(population, trainingPlansPoints);	
					population = mutation(population, allExercises);
					trainingPlansPoints = evaluate(population);		
					
					int[] bestTab = new int[generations];
			    	int[] worstTab = new int[generations];
			    	int[] avgTab = new int[generations];
					for(int k = 0; k < populationSize; k++){
		    			if(best > trainingPlansPoints[k]){
		    				best = trainingPlansPoints[k];
		    			}
		    			if(worst < trainingPlansPoints[k]){
		    				worst = trainingPlansPoints[k];
		    			}
		    			avg += trainingPlansPoints[k];
					}
			    	avg = avg / trainingPlansPoints.length;
			    	bestTab[j] = best;
			    	worstTab[j] = worst;
			    	avgTab[j] = avg;
			    	avgBest[j] += bestTab[j];
			    	avgWorst[j] += worstTab[j];
			    	avgAvg[j] += avgTab[j];
				}		
			}
  
	    	for(int i = 0; i < generations; i++){
	    		avgBest[i] = avgBest[i] / numberOfAlgorithmLaunches;
	    		avgWorst[i] = avgWorst[i] / numberOfAlgorithmLaunches; 
	    		avgAvg[i] = avgAvg[i] / numberOfAlgorithmLaunches;
	    		bw.write(avgBest[i] + ",");
	        	bw.write(avgWorst[i] + ",");
	        	bw.write(avgAvg[i] + ",");
	        	bw.newLine();
	    	}
	    	bw.close();
   		}catch(IOException e){
    		e.printStackTrace();
    	}
    	System.out.println("Done");
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
		exercises[0] = new Exercise("Kettlebell swing - 5 mins", "arms", 100, 5, false, true);
		exercises[1] = new Exercise("Indoor rowing - 5 mins", "whole body", 60, 5, false, true);
		exercises[2] = new Exercise("Kettlebell swing - 10 mins", "arms", 200, 10, false, true);
		exercises[3] = new Exercise("Indoor rowing - 10 mins", "whole body", 120, 10, false, true);
		exercises[4] = new Exercise("Burpees - 3 mins", "cardio", 42, 3, false, false);
		exercises[5] = new Exercise("Burpees - 10 mins", "cardio", 143, 10, false, false);
		exercises[6] = new Exercise("Airdyne bike sprints - 1 min", "cardio", 87, 1, false, true);
		exercises[7] = new Exercise("Airdyne bike sprints - 5 mins", "cardio", 435, 5, false, true);
		exercises[8] = new Exercise("Jumping rope - 5 mins", "cardio", 65, 5, false, true);
		exercises[9] = new Exercise("Jumping rope - 2 mins", "cardio", 26, 2, false, true);
		
		exercises[10] = new Exercise("Fat-tire biking - 10 mins", "cardio", 250, 10, true, true);
		exercises[11] = new Exercise("Fat-tire biking - 20 mins", "cardio", 500, 20, true, true);
		exercises[12] = new Exercise("Fat-tire biking - 30 mins", "cardio", 750, 30, true, true);
		exercises[13] = new Exercise("CINDY - crossfit - 10 mins", "cardio", 130, 10, false, false);
		exercises[14] = new Exercise("CINDY - crossfit - 20 mins", "cardio", 260, 20, false, false);
		exercises[15] = new Exercise("CINDY - crossfit - 5 mins", "cardio", 65, 5, false, false);
		exercises[16] = new Exercise("Cross-country skiing - 15 mins", "cardio", 180, 15, true, true);
		exercises[17] = new Exercise("Cross-country skiing - 20 mins", "cardio", 300, 20, true, true);
		exercises[18] = new Exercise("Cross-country skiing - 30 mins", "cardio", 450, 30, true, true);
		exercises[19] = new Exercise("Tabata jump squats - 2 mins", "cardio", 26, 2, false, false);
		
		exercises[20] = new Exercise("Tabata jump squats - 1 min", "cardio", 13, 1, false, false);
		exercises[21] = new Exercise("Tabata jump squats - 3 mins", "cardio", 39, 3, false, false);
		exercises[22] = new Exercise("Tabata jump squats - 5 mins", "cardio", 65, 5, false, false);
		exercises[23] = new Exercise("Battling ropes - 2 mins", "cardio", 20, 2, false, true);
		exercises[24] = new Exercise("Battling ropes - 3 mins", "cardio", 30, 3, false, true);
		exercises[25] = new Exercise("Battling ropes - 4 mins", "cardio", 40, 4, false, true);
		exercises[26] = new Exercise("Battling ropes - 5 mins", "cardio", 50, 5, false, true);
		exercises[27] = new Exercise("Battling ropes - 6 mins", "cardio", 60, 6, false, true);
		exercises[28] = new Exercise("Battling ropes - 10 mins", "cardio", 100, 10, false, true);
		exercises[29] = new Exercise("Battling ropes - 15 mins", "cardio", 150, 15, false, true);
	
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
		TrainingPlan trainingPlanToEval;
		Exercise[] exercisesToEval;
		Exercise exerciseToEval;
		int pointsSum = 0;
		int timeSum = 0;
		int caloriesSum = 0;
		for(int i = 0; i < trainingPlans.length; i++) {
			pointsSum = 0;
			timeSum = 0;
			caloriesSum = 0;
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
		int totalTime = 0;
		int totalCalories = 0;
		for(int i = 0; i <plansPoints.length; i++) {
			if(plansPoints[i] > points) {
				points = plansPoints[i];
				bestIndex = i;
			}
		}
		for(Exercise e: trainingPlans[bestIndex].getExercisesInPlan()) {
			totalTime += e.getRequiredTime();
			totalCalories += e.getCalories();
		}
		System.out.println("Best plan with:  " + points + " points, " + totalTime + " minutes, " + totalCalories + " calories.");
		printTrainingPlan(trainingPlans[bestIndex]);
	}
}