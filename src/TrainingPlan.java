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
}