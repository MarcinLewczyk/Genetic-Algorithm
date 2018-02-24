public class Exercise {
	private String name;
	private String musclePart;
	
	private int calories;
	private int requiredTime;
	
	private boolean outside;
	private boolean equipment;
	
	public Exercise(String name, String musclePart, int calories,
			int requiredTime, boolean outside, boolean equipment) {
		super();
		this.name = name;
		this.musclePart = musclePart;
		this.calories = calories;
		this.requiredTime = requiredTime;
		this.outside = outside;
		this.equipment = equipment;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMusclePart() {
		return musclePart;
	}
	public void setMusclePart(String musclePart) {
		this.musclePart = musclePart;
	}
	public int getCalories() {
		return calories;
	}
	public void setCalories(int calories) {
		this.calories = calories;
	}
	public int getRequiredTime() {
		return requiredTime;
	}
	public void setRequiredTime(int requiredTime) {
		this.requiredTime = requiredTime;
	}
	public boolean isOutside() {
		return outside;
	}
	public void setOutside(boolean outside) {
		this.outside = outside;
	}
	public boolean isEquipment() {
		return equipment;
	}
	public void setEquipment(boolean equipment) {
		this.equipment = equipment;
	}
}