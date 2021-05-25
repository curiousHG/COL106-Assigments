// You should utilise your implementation of ArrayDeque methods to implement this
public class Stack implements StackInterface {	
	ArrayDeque arr = new ArrayDeque();

	public void push(Object o){
    	//you need to implement this
		arr.insertLast(o);
    	// throw new java.lang.UnsupportedOperationException("Not implemented yet.");
  	}

	public Object pop() throws EmptyStackException{
		//you need to implement this
		if (arr.isEmpty()){
			throw new EmptyStackException("Empty");
		}else{
			try{
				Object o = arr.removeLast();
				return o;
			}
			catch(EmptyDequeException e){
				throw new EmptyStackException("Empty");
			}
		}
    	// throw new java.lang.UnsupportedOperationException("Not implemented yet.");
	}

	public Object top() throws EmptyStackException{
    	//you need to implement this
		if (arr.isEmpty()){
			throw new EmptyStackException("Empty");
		}else{
			try{
				Object o = arr.last();
				return o;
			}
			catch(EmptyDequeException e){
				throw new EmptyStackException("Empty");
			}
		}
    	// throw new java.lang.UnsupportedOperationException("Not implemented yet.");
	}

	public boolean isEmpty(){
    	//you need to implement this
		if(arr.isEmpty())return true;
		return false;
    	// throw new java.lang.UnsupportedOperationException("Not implemented yet.");
	}

    public int size(){
    	//you need to implement this
		return arr.size();
    	// throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }
}