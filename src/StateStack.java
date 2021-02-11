
public class StateStack {
	
	public State top;
	public State bottom;
	
	public StateStack() {
		top = null;
		bottom = null;
	}
	
	public void push(State state) {
		state.next = top; //pushing the next element to the top of the stack
		top = state; //the node we have put in as our argument is now at the top of the stack.
	}
	
	public State pop() {
		//make a dummy node and assign it to top then see if there's anything in the stack
		State state = top;
		
		//check if the stack is empty
		if(this.isEmpty()) {
			return null;
		}
		
		//check if Stack only has 1 element
		if(top == bottom) {
			//top and bottom are both null, so they're at the same place, meaning we only have one item in the stack.
			top = null;
			bottom = null;
		}
		else {
			//else the top of the stack is now the next element down the stack.
			top = top.next;
		}
		
		return state;
	}
	
	public boolean isEmpty() {
		if(top == null && bottom == null) {
			return true;
		}
		return false;
	}
	
	public int getStackSize() {
		if(isEmpty()) {
			return 0;
		}
		State current = top;
		int count = 0;
		while(current != null) {
			count++;
			current = current.next;
		}
		return count;
	}

}
