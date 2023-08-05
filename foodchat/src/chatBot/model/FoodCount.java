package chatBot.model;

public class FoodCount {
	private String food;
	private int count;
	public FoodCount(String food, int count) {
		super();
		this.food = food;
		this.count = count;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((food == null) ? 0 : food.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FoodCount other = (FoodCount) obj;
		if (food == null) {
			if (other.food != null)
				return false;
		} else if (!food.equals(other.food))
			return false;
		return true;
	}

	public String getFood() {
		return food;
	}
	public void setFood(String food) {
		this.food = food;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	@Override
	public String toString() {
		return "FoodCount [food=" + food + ", count=" + count + "]";
	}
	
	
	
}
