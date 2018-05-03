public class TrainingPlan {
	private Exercise[] exercisesInPlan;

	public TrainingPlan(int n) {
		exercisesInPlan = new Exercise[n];
	}
	
	public void addExercise(Exercise exercise, int position) {
		exercisesInPlan[position] = exercise;
	}
	
	public Exercise[] getExercisesInPlan() {
		return exercisesInPlan;
	}

	public void setExercisesInPlan(Exercise[] exercisesInPlan) {
		this.exercisesInPlan = exercisesInPlan;
	}
	
	public void setExercisesInPlanNew(Exercise[] exercisesInPlan) {
		this.exercisesInPlan = new Exercise[exercisesInPlan.length];
		for(int i = 0; i < this.exercisesInPlan.length; i++) {
			Exercise newExercise = new Exercise(exercisesInPlan[i].getName(), exercisesInPlan[i].getMusclePart(),
					                            exercisesInPlan[i].getCalories(), exercisesInPlan[i].getRequiredTime(),
					                            exercisesInPlan[i].isOutside(), exercisesInPlan[i].isEquipment());
			this.exercisesInPlan[i] = newExercise;
		}
	}
}