package aguiaj.adt;

public interface Queue extends Collection {
	
	void enqueue(Object object);
	
	void dequeue(Object object);
	
	Object peek();
}
