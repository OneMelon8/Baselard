package JProjects.BaseInfoBot.tools;

public class Tuple {

	private Object obj1, obj2;

	public Tuple(Object obj1, Object obj2) {
		this.obj1 = obj1;
		this.obj2 = obj2;
	}

	public String toString() {
		return "(" + this.getObj1().toString() + ", " + this.getObj2().toString() + ")";
	}

	public Object getObj1() {
		return obj1;
	}

	public Object getObj2() {
		return obj2;
	}

	public void setObj1(Object obj1) {
		this.obj1 = obj1;
	}

	public void setObj2(Object obj2) {
		this.obj2 = obj2;
	}

}
