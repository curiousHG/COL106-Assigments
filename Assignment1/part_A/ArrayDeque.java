public class ArrayDeque implements DequeInterface {
  int len = 1;
  Object[] array = new Object[len];
  int currentIndex = 0;
  int start = 0;

  public void insertFirst(Object o) {
    // you need to implement this
    if (currentIndex == len) {
      len = 2 * (currentIndex - start);
    }
    if(start==0){
      Object[] tempArray = new Object[len];
      tempArray[0] = o;
      int j = 1;
      for (int i = start; i < currentIndex; i++) {
        tempArray[j] = array[i];
        j++;
      }
      start = 0;
      currentIndex = j;
      array = tempArray;
    }else{
      array[start-1]=o;
      start--;
    }
    // throw new java.lang.UnsupportedOperationException("Not implemented yet.");
  }

  public void insertLast(Object o) {
    // you need to implement this
    if (currentIndex == len) {
      len = 2 * (currentIndex - start);
      Object[] tempArray = new Object[len];
      int j = 0;
      for (int i = start; i < currentIndex; i++) {
        tempArray[j] = array[i];
        j++;
      }
      tempArray[j] = 0;
      array = tempArray;
      currentIndex = j;
    }
    array[currentIndex] = o;
    currentIndex++;
    // throw new java.lang.UnsupportedOperationException("Not implemented yet.");
  }

  public Object removeFirst() throws EmptyDequeException {
    // you need to implement this
    if (isEmpty()) {
      throw new EmptyDequeException("Deque Empty");
    } else {
      Object o = array[start];
      start++;
      return o;
    }
    // throw new java.lang.UnsupportedOperationException("Not implemented yet.");
  }

  public Object removeLast() throws EmptyDequeException {
    // you need to implement this
    if (isEmpty()) {
      throw new EmptyDequeException("Deque Empty");
    } else {
      Object o = array[currentIndex - 1];
      currentIndex--;
      return o;
    }
    // throw new java.lang.UnsupportedOperationException("Not implemented yet.");
  }

  public Object first() throws EmptyDequeException {
    // you need to implement this
    if (isEmpty())
      throw new EmptyDequeException("Deque Empty");
    else {
      return array[start];
    }
    // throw new java.lang.UnsupportedOperationException("Not implemented yet.");
  }

  public Object last() throws EmptyDequeException {
    // you need to implement this
    if (isEmpty())
      throw new EmptyDequeException("Deque Empty");
    else {
      return array[currentIndex - 1];
    }
    // throw new java.lang.UnsupportedOperationException("Not implemented yet.");
  }

  public int size() {
    // you need to implement this
    return currentIndex - start;
    // throw new java.lang.UnsupportedOperationException("Not implemented yet.");
  }

  public boolean isEmpty() {
    // you need to implement this
    if (currentIndex == start)
      return true;
    return false;
    // throw new java.lang.UnsupportedOperationException("Not implemented yet.");
  }

  public String toString() {
    // you need to implement this
    String answer = "[";
    for (int i = start; i < currentIndex; i++) {
      // String s = (String) array[i];
      answer += array[i].toString();
      if (i != currentIndex - 1) {
        answer += ",";
      }
    }
    answer += "]";
    return answer;
    // throw new java.lang.UnsupportedOperationException("Not implemented yet.");
  }
}