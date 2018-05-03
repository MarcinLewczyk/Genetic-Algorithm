
public class DoubleTable {
	private TrainingPlan[] restSolutions;
	private TrainingPlan[] eliteSolutions;
	
	public DoubleTable() {
		
	}
	
	public DoubleTable(TrainingPlan[] restSolutions, TrainingPlan[] eliteSolutions) {
		super();
		this.restSolutions = restSolutions;
		this.eliteSolutions = eliteSolutions;
	}
	
	public TrainingPlan[] getRestSolutions() {
		return restSolutions;
	}
	
	public void setRestSolutions(TrainingPlan[] restSolutions) {
		this.restSolutions = restSolutions;
	}
	
	public TrainingPlan[] getEliteSolutions() {
		return eliteSolutions;
	}
	
	public void setEliteSolutions(TrainingPlan[] eliteSolutions) {
		this.eliteSolutions = eliteSolutions;
	}
}