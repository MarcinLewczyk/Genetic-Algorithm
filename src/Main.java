import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
	public static int populationSize = 10; //10
	public static int trainingPlansSize = 8;
	public static int generations = 75; //75 
	public static int crossoverChance = 80; // 80%
	public static double mutationChance = 500; // 50 = 5% 
	
	public static int totalCalories = 600; //total amount of calories that user wants to burn
	public static int totalTime = 60; //wanted training duration
	
	public static double accuracyPercentage = 3; //accepted accuracy (it is total accuracy which means ((points from eval)/(totalCalories + totalTime))*100
	public static int tournamentSize = 3; //tournament selection
	
	public static boolean outside = false;  //false means that we don't want that
	public static boolean equipment = false;
	
	public static boolean elite = true; // it is getting the best plan due to tournament on whole population and then passing it to random position in the 
										// new population after this population was crossovered and mutated
	
	public static int outsidePenalty = 30;
	public static int equipmentPenalty = 30;
	
	public static int numberOfAlgorithmLaunches = 10;
	
	public static void main(String[] args) {
		int[] test = {-5, -7, -13, -1, -6, -1};
	/*	int[] result = getBestSolutions(test);
		for(int i: result) {
			System.out.println(i);
		}*/
	//	System.out.println("best index " + tournament(test, test.length));
	//	System.out.println(ranking(test));
	//	oneIteration();
	//	multipleIterationsWithFileOutput();
	    startBees();
	}
	
	public static int numOfBestSolutions = 5; // m
	public static int neighborhoodSize = 3; // k
	public static int numOfEliteSolutions = 2; //e
	public static int nsp = 2;
	public static int nep = 4;
	
	public static void startBees() {
		Exercise[] allExercises = createExercises();
		TrainingPlan[] population = fillPopulation(allExercises);
		int[] trainingPlansPoints;
		trainingPlansPoints = evaluate(population);
		TrainingPlan globalBest = population[getBestPlanIndex(trainingPlansPoints)];
		DoubleTable doubleTable = getEliteAndRestSolutions(trainingPlansPoints, population);
		TrainingPlan[] eliteSolutions = doubleTable.getEliteSolutions();
		TrainingPlan[] restSolutions = doubleTable.getRestSolutions();
		System.out.println(eliteSolutions.length + " " + restSolutions.length);
		printPoints(eliteSolutions);
		System.out.println(" ----------- ");
		printPoints(restSolutions);
		System.out.println(" ----------- ");
		printPopulation(eliteSolutions);
		System.out.println(" ----------- ");
		TrainingPlan[] mutatedElite = kMutation(eliteSolutions, allExercises);
		System.out.println(mutatedElite.length);
		printPopulation(mutatedElite);
		System.out.println(" ----------- ");
		TrainingPlan[] mutatedRest = kMutation(restSolutions, allExercises);
		System.out.println(mutatedRest.length);
		printPopulation(mutatedRest);
		//printPoints(mutatedElite);
	/*	for(int i = 0; i < generations; i++) {
			trainingPlansPoints = evaluate(population);		
			TrainingPlan[] bestSolutionsPositions = getBestSolutions(trainingPlansPoints, population); // M
			TrainingPlan[] eliteSolutionsPositions = getEliteSolutions(bestSolutionsPositions, population); //EL
			bestSolutionsPositions = ;
			
		}*/
		
	}
	// wybrac najlepszych, spoœród najlepszych wybrac elite i resztê,
	// mutac k razy kazda elite i reszte, sposrod mutacji elity wybrac nep spoœrod mutacji reszty wybrac nsp, 
	// utworzyc nowa populacje z tego, z reszty miejsc co zostanie to losowe zapelnienie
	public static int getBestPlanIndex(int[] trainingPlansPoints) {
		int bestIndex = 0;
		int points = Integer.MIN_VALUE;
		for(int i = 0; i < trainingPlansPoints.length; i++) {
			if(trainingPlansPoints[i] > points) {
				points = trainingPlansPoints[i];
				bestIndex = i;
			}
		}
		return bestIndex;
	}
	
	public static DoubleTable getEliteAndRestSolutions(int[] trainingPlansPoints, TrainingPlan[] population) {
		TrainingPlan[] eliteTrainings = new TrainingPlan[numOfEliteSolutions];
		TrainingPlan[] restTrainings = new TrainingPlan[numOfBestSolutions - numOfEliteSolutions];
		DoubleTable doubleTable = new DoubleTable();
		int[] plansPoints = Arrays.copyOf(trainingPlansPoints, trainingPlansPoints.length);
		for(int h = 0; h < numOfBestSolutions; h++) {
			int bestIndex = 0;
			int points = Integer.MIN_VALUE;
			for(int i = 0; i < plansPoints.length; i++) {
				if(plansPoints[i] > points) {
					points = plansPoints[i];
					bestIndex = i;
				}
			}
			if(h < numOfEliteSolutions) {
				eliteTrainings[h] = population[bestIndex];
			} else {
				restTrainings[h - numOfEliteSolutions] = population[bestIndex];
			}
			plansPoints[bestIndex] = Integer.MIN_VALUE;
		}
		doubleTable.setEliteSolutions(eliteTrainings);
		doubleTable.setRestSolutions(restTrainings);
		return doubleTable;
	}
	
	public static TrainingPlan[] kMutation(TrainingPlan[] population, Exercise[] allExercises) {
		int mutationSize = population.length * neighborhoodSize;
		TrainingPlan[] mutatedPopulation = new TrainingPlan[mutationSize];
		for(int i = 0; i < population.length; i++) {
			for(int j = 0; j < neighborhoodSize; j++) {
				mutatedPopulation[2 * i + i + j] = mutate(population[i], allExercises);
			}
		}	
		return mutatedPopulation;
	}
	
	public static TrainingPlan mutate(TrainingPlan trainingPlan, Exercise[] allExercises) {
		TrainingPlan mutatedPlan = new TrainingPlan(trainingPlan.getExercisesInPlan().length);
		Random randomPosition = new Random();
		Exercise[] exercises = trainingPlan.getExercisesInPlan();	
		mutatedPlan.setExercisesInPlanNew(exercises); //we need new objects -> with old ones all results for same trainingPlan will be same
		for(int j = 0; j < exercises.length; j++) {
			int randomMutationProb = ThreadLocalRandom.current().nextInt(0, 1001); //so chance = 10 is 1%
			if(mutationChance >= randomMutationProb) {
				mutatedPlan.getExercisesInPlan()[j] = allExercises[randomPosition.nextInt(allExercises.length)];
			}
		}	
		return mutatedPlan;
	}
	
	public static void oneIteration() {
		Exercise[] allExercises = createExercises();
		TrainingPlan[] population = fillPopulation(allExercises);
		TrainingPlan eliteParent = null;
		int[] trainingPlansPoints; 
		for(int i = 0; i < generations; i++) {	
			trainingPlansPoints = evaluate(population);
	/*		if(checkStopConditionWithPoints(trainingPlansPoints)) {
				System.out.println("Stop condition in generation " + i);
				break;
			}  */
			if(elite) {
				eliteParent = population[tournament(trainingPlansPoints, population.length)];
				System.out.println("elite index: " + tournament(trainingPlansPoints, population.length));
			}
	//		population = crossover(population, trainingPlansPoints);	
	//		population = twoPointsCrossoverWithThreeParents(population, trainingPlansPoints);	
			population = twoPointsCrossover(population, trainingPlansPoints);	

			population = mutation(population, allExercises);
		
		//	population = inversion(population);
		
			if(elite) {
				int randomToThrow = ThreadLocalRandom.current().nextInt(0, population.length); 
				population[randomToThrow] = eliteParent;
			}
			
			trainingPlansPoints = evaluate(population);		
			System.out.println("Generation " + i);
			printBestPlan(population);
		}
	//	printPopulation(population);
	//	printPoints(population);
	//	printBestPlan(population);
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
	    		TrainingPlan eliteParent = null;
	    		for(int j = 0; j < generations; j++) {
					int best = Integer.MAX_VALUE;
			    	int worst = Integer.MIN_VALUE;
			    	int avg = 0;
			    	
					int[] trainingPlansPoints = evaluate(population);
					if(elite) {
						eliteParent = population[tournament(trainingPlansPoints, population.length)];
					}
			//		population = crossover(population, trainingPlansPoints);	
					population = twoPointsCrossoverWithThreeParents(population, trainingPlansPoints);	
			//		population = twoPointsCrossover(population, trainingPlansPoints);	

					population = mutation(population, allExercises);
					population = inversion(population);
					if(elite) {
						int randomToThrow = ThreadLocalRandom.current().nextInt(0, population.length); 
						population[randomToThrow] = eliteParent;
					}
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
		int[] evaluation = new int[trainingPlans.length];
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
		//	TrainingPlan parent1 = population[tournament(trainingPlansPoints, tournamentSize)];
		//	TrainingPlan parent1 = population[roullete(trainingPlansPoints)];
			TrainingPlan parent1 = population[ranking(trainingPlansPoints)];		
			int randomCrossoverProb = ThreadLocalRandom.current().nextInt(0, 101);
			if(randomCrossoverProb <= crossoverChance) {
			//	TrainingPlan parent2 = population[tournament(trainingPlansPoints, tournamentSize)];
			//	TrainingPlan parent2 = population[roullete(trainingPlansPoints)];	
				TrainingPlan parent2 = population[ranking(trainingPlansPoints)];		
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
	
	public static TrainingPlan[] twoPointsCrossover(TrainingPlan[] population, int[] trainingPlansPoints) {
		TrainingPlan[] selectedPopulation = new TrainingPlan[population.length];
		for(int i = 0; i < selectedPopulation.length; i++) {
		//	TrainingPlan parent1 = population[tournament(trainingPlansPoints, tournamentSize)];
		//	TrainingPlan parent1 = population[roullete(trainingPlansPoints)];
			TrainingPlan parent1 = population[ranking(trainingPlansPoints)];		
			int randomCrossoverProb = ThreadLocalRandom.current().nextInt(0, 101);
			if(randomCrossoverProb <= crossoverChance) {
			//	TrainingPlan parent2 = population[tournament(trainingPlansPoints, tournamentSize)];
			//	TrainingPlan parent2 = population[roullete(trainingPlansPoints)];	
				TrainingPlan parent2 = population[ranking(trainingPlansPoints)];		
				Exercise[] firstParentExercises = parent1.getExercisesInPlan();
				Exercise[] secondParentExercises = parent2.getExercisesInPlan();
				int cuttingPosition = ThreadLocalRandom.current().nextInt(0, trainingPlansSize);
				int secondCuttingPosition = ThreadLocalRandom.current().nextInt(0, trainingPlansSize);
				while(secondCuttingPosition == cuttingPosition) {
					secondCuttingPosition = ThreadLocalRandom.current().nextInt(0, trainingPlansSize);
				}
				TrainingPlan child = new TrainingPlan(trainingPlansSize);
				Exercise[] childExercises = new Exercise[trainingPlansSize];
				for(int j = 0; j < trainingPlansSize; j++) {
					if(cuttingPosition < secondCuttingPosition) {
						if(j < cuttingPosition) {
							childExercises[j] = firstParentExercises[j];
						} else if(j > cuttingPosition && j < secondCuttingPosition){
							childExercises[j] = secondParentExercises[j];
						} else {
							childExercises[j] = firstParentExercises[j];
						}				
					} else {
						if(j > cuttingPosition) {
							childExercises[j] = firstParentExercises[j];
						} else if(j < cuttingPosition && j > secondCuttingPosition){
							childExercises[j] = secondParentExercises[j];
						} else {
							childExercises[j] = firstParentExercises[j];
						}
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
	
	public static TrainingPlan[] twoPointsCrossoverWithThreeParents(TrainingPlan[] population, int[] trainingPlansPoints) {
		TrainingPlan[] selectedPopulation = new TrainingPlan[population.length];
		for(int i = 0; i < selectedPopulation.length; i++) {
		//	TrainingPlan parent1 = population[tournament(trainingPlansPoints, tournamentSize)];
		//	TrainingPlan parent1 = population[roullete(trainingPlansPoints)];
			TrainingPlan parent1 = population[ranking(trainingPlansPoints)];		
			int randomCrossoverProb = ThreadLocalRandom.current().nextInt(0, 101);
			if(randomCrossoverProb <= crossoverChance) {
			//	TrainingPlan parent2 = population[tournament(trainingPlansPoints, tournamentSize)];
			//	TrainingPlan parent2 = population[roullete(trainingPlansPoints)];	
				TrainingPlan parent2 = population[ranking(trainingPlansPoints)];
			//	TrainingPlan parent3 = population[tournament(trainingPlansPoints, tournamentSize)];
			//	TrainingPlan parent3 = population[roullete(trainingPlansPoints)];	
				TrainingPlan parent3 = population[ranking(trainingPlansPoints)];
				
				Exercise[] firstParentExercises = parent1.getExercisesInPlan();
				Exercise[] secondParentExercises = parent2.getExercisesInPlan();
				Exercise[] thirdParentExercises = parent3.getExercisesInPlan();
				
				int cuttingPosition = ThreadLocalRandom.current().nextInt(0, trainingPlansSize);
				int secondCuttingPosition = ThreadLocalRandom.current().nextInt(0, trainingPlansSize);
				while(secondCuttingPosition == cuttingPosition) {
					secondCuttingPosition = ThreadLocalRandom.current().nextInt(0, trainingPlansSize);
				}
				TrainingPlan child = new TrainingPlan(trainingPlansSize);
				Exercise[] childExercises = new Exercise[trainingPlansSize];
				for(int j = 0; j < trainingPlansSize; j++) {
					if(cuttingPosition < secondCuttingPosition) {
						if(j < cuttingPosition) {
							childExercises[j] = firstParentExercises[j];
						} else if(j > cuttingPosition && j < secondCuttingPosition){
							childExercises[j] = secondParentExercises[j];
						} else {
							childExercises[j] = thirdParentExercises[j];
						}				
					} else {
						if(j > cuttingPosition) {
							childExercises[j] = firstParentExercises[j];
						} else if(j < cuttingPosition && j > secondCuttingPosition){
							childExercises[j] = secondParentExercises[j];
						} else {
							childExercises[j] = thirdParentExercises[j];
						}
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
	
	public static int tournament(int[] trainingPlansPoints, int tournamentSize) {
		int bestPlanPoints = Integer.MIN_VALUE;
		int bestPlanIndex = 0;
		for(int i = 0; i < tournamentSize; i++) {
			if(elite) {
				if(trainingPlansPoints[i] > bestPlanPoints) {
					bestPlanPoints = trainingPlansPoints[i];
					bestPlanIndex = i;
				}
			} else {
				int randomPosition = ThreadLocalRandom.current().nextInt(0, populationSize);
				if(trainingPlansPoints[randomPosition] > bestPlanPoints) {
					bestPlanPoints = trainingPlansPoints[randomPosition];
					bestPlanIndex = randomPosition;
				}
			}
		}
		return bestPlanIndex;
	}
	
	public static int roullete(int[] trainingPlansPoints) {
		double sumOfPoints = 0;
		for(int i: trainingPlansPoints) {
			sumOfPoints += 1.0/(double)i;
		}
		double positiveSum = Math.abs(sumOfPoints);
		double randomNumber =  ThreadLocalRandom.current().nextDouble(0, positiveSum);
		double partialPositiveSum = 0; //partial sum of current element
		for(int i = 0; i < trainingPlansPoints.length; i ++) {
			partialPositiveSum += 1/Math.abs((double)trainingPlansPoints[i]);
			if(partialPositiveSum >= randomNumber) {
				return i;
			}
		}
		return -1;
	}
	
	public static int ranking(int[] trainingPlansPoints) {
		int sumOfPoints = 0;
		int[] rankPoints = givePoints(trainingPlansPoints);
		for(int i = 0; i < rankPoints.length; i++) {
			sumOfPoints += rankPoints[i];
		}
		int randomNumber =  ThreadLocalRandom.current().nextInt(0, sumOfPoints);
		int partialSum = 0;
		for(int i = 0; i < trainingPlansPoints.length; i++) {
			partialSum += rankPoints[i];
			if(partialSum >= randomNumber) {
				return i;
			}
		}
		return -1;
	}
	
	public static int[] givePoints(int[] trainingPlansPoints) {
		int[] rankPoints = new int[trainingPlansPoints.length];
		int bestValue; 
		int bestIndex;
		int multipleSameValues = 0;
		for(int i = 0; i < trainingPlansPoints.length - multipleSameValues; i++) {
			bestIndex = 0;
			bestValue = Integer.MIN_VALUE;
			for(int j = trainingPlansPoints.length - 1; j >= 0; j--) {		
				if(trainingPlansPoints[j] > bestValue) {
					bestValue = trainingPlansPoints[j];
					bestIndex = j;
				}
			}
			trainingPlansPoints[bestIndex] = Integer.MIN_VALUE;
			rankPoints[bestIndex] = trainingPlansPoints.length - i;
			for(int k = 0; k < trainingPlansPoints.length; k++) {
				if(trainingPlansPoints[k] == bestValue) {
					rankPoints[k] =  trainingPlansPoints.length - i;
					trainingPlansPoints[k] = Integer.MIN_VALUE;
					multipleSameValues++;
				}
			}
		}
		return rankPoints;
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
	
	public static TrainingPlan[] inversion(TrainingPlan[] population) {
		for(int i = 0; i < population.length; i++) {
			Exercise[] exercises = population[i].getExercisesInPlan();
			int secondPosition = ThreadLocalRandom.current().nextInt(1, exercises.length);
			int firstPosition = ThreadLocalRandom.current().nextInt(0, secondPosition + 1);
			while(firstPosition >= secondPosition) {
				firstPosition = ThreadLocalRandom.current().nextInt(0, secondPosition + 1);
			}
			Exercise[] partToInvert = new Exercise[secondPosition - firstPosition + 1];
			for(int j = 0; j < secondPosition - firstPosition + 1; j++) {
				partToInvert[j] = exercises[firstPosition + j];
			}
			Exercise[] inverted = reverseExercisesArray(partToInvert);
			int k = 0;
			for(int j = firstPosition; j <= secondPosition; j++) {
				exercises[j] = inverted[k];
				k++;
			}		
			population[i].setExercisesInPlan(exercises);
		}
		return population;
	}
	
	public static Exercise[] reverseExercisesArray(Exercise[] exercises) {
		Exercise[] inverted = new Exercise[exercises.length];
		int j = 0;
		for(int i = exercises.length - 1; i >= 0; i--) {
			inverted[i] = exercises[j];
			j++;
		}
		return inverted;
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