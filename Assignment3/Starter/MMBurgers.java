import java.util.*;

class Customer {
    int customerId;
    int enterTime;
    int orderTime;
    int waitTime;

    int orderQueueNum;
    int burgers;
    int locationIndex;
    Boolean inQueue = true;
    Boolean complete = false;

    Customer(int id, int time, int queueNum, int numberofBurgers) {
        enterTime = time;
        orderQueueNum = queueNum;
        burgers = numberofBurgers;
        customerId = id;
    }
}

class Order {
    int customerId;
    int customerLoca;
    int nBurgers;
    Integer queueNum = -1;
    int orderTime;
    int completeTime = 0;

    Order(int id, int b, int customerLocation, int queue) {
        customerId = id;
        nBurgers = b;
        customerLoca = customerLocation;
        queueNum = queue;
    }
}

public class MMBurgers implements MMBurgersInterface {
    int globalTime = 0;
    float totalCustomerTime = 0;
    int numberOfCustomers = 0;

    // Griddle variables
    Deque<Order> waitingOrders = new LinkedList<Order>();
    int gridSpace;
    int totalGridSpace;
    Deque<Order> griddle = new LinkedList<Order>();

    // For finding right queue for customer
    // PriorityQueue findSmallQueue = new PriorityQueue();
    int numberOfQueues = 0;

    // for picking right customer accd to time
    PriorityQueue customerHeap = new PriorityQueue();

    // For num of customers in each queue;
    ArrayList<LinkedList<Customer>> customerLines;
    int queueLengths[];
    int customerInQueues = 0;

    // For finding customers
    Vector<Customer> CustomerArray = new Vector<Customer>();;
    int CustomerArrayIndex;
    AVLTree customerTree = new AVLTree();

    public boolean isEmpty() {
        if (customerInQueues == 0 && waitingOrders.size() == 0 && griddle.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void setK(int k) throws IllegalNumberException {
        if (numberOfQueues != 0) {
            throw new IllegalNumberException("Wrong");
        } else {
            customerLines = new ArrayList<LinkedList<Customer>>(k);
            queueLengths = new int[k];
            numberOfQueues = k;

            for (int i = 0; i < k; i++) {
                queueLengths[i] = 0;
                customerLines.add(new LinkedList<Customer>());
            }
        }

    }

    public void setM(int m) throws IllegalNumberException {
        if (totalGridSpace != 0) {
            throw new IllegalNumberException("Wrong");
        } else {
            gridSpace = m;
            totalGridSpace = m;
        }

    }

    public void advanceTime(int t) throws IllegalNumberException {
        if (globalTime > t) {
            throw new IllegalNumberException("Wrong");
        } else {
            while (globalTime < t) {

                // Skipping time to maximimum possible
                int a = customerInQueues > 0 ? CustomerArray.get(customerHeap.peek().index).orderTime : t;
                int b = gridSpace != totalGridSpace ? griddle.peek().completeTime : a;
                int min = Math.min(Math.min(a, b), t);
                globalTime = min;

                // Billing specialist prints an order and sends it to the chef; customer leaves
                // the queue.
                takingOrders();

                // A cooked patty is removed from the griddle.
                removePatties();

                // Adding patty to griddle
                addPatties();
                if (customerInQueues == 0 && waitingOrders.size() == 0 && gridSpace == totalGridSpace) {
                    globalTime = t;
                }

            }
        }

    }

    public void arriveCustomer(int id, int t, int numb) throws IllegalNumberException {
        if (globalTime > t || customerTree.search(id) == true) {
            throw new IllegalNumberException("Wrong");
        } else {
            if (globalTime != t) {
                advanceTime(t);
            }
            // find queue for customer
            int queueNum = shortestQueueIndex();

            Customer newC = new Customer(id, t, queueNum, numb);
            newC.locationIndex = CustomerArrayIndex;
            CustomerArray.add(newC);

            // Finding time to process customer
            if (queueLengths[queueNum] == 0) {
                newC.orderTime = queueNum + 1 + t;
            } else {
                Customer oldC = customerLines.get(queueNum).getLast();
                newC.orderTime = oldC.orderTime + queueNum + 1;

            }

            // Adding customer to heap to get the earliest customer.
            customerHeap.add(newC.orderTime, CustomerArrayIndex);
            customerLines.get(queueNum).add(newC);
            queueLengths[queueNum]++;

            // Adding customer to customer tree for quick access
            customerTree.insert(id);
            Node customer = customerTree.find(id);
            customer.index = CustomerArrayIndex;
            CustomerArrayIndex++;
            numberOfCustomers++;
            customerInQueues++;
        }

    }

    public int customerState(int id, int t) throws IllegalNumberException {
        if (customerTree.search(id) != true) {
            return 0;
        } else {
            if (globalTime != t) {
                advanceTime(t);
            }
            int c = customerTree.find(id).index;
            Customer Cu = CustomerArray.get(c);
            if (Cu.enterTime > t) {
                return 0;
            } else if (Cu.inQueue) {
                return Cu.orderQueueNum + 1;
            } else if (!Cu.complete) {
                return numberOfQueues + 1;
            } else {
                return numberOfQueues + 2;
            }
        }

    }

    public int griddleState(int t) throws IllegalNumberException {
        if (globalTime > t) {
            throw new IllegalNumberException("Wrong");
        } else {
            if (globalTime != t) {
                advanceTime(t);
            }
            int space = 0;
            for (Order o : griddle) {
                space += o.nBurgers;
            }
            return space;
        }

    }

    public int griddleWait(int t) throws IllegalNumberException {
        if (globalTime > t) {
            throw new IllegalNumberException("Wrong");
        } else {
            if (globalTime != t) {
                advanceTime(t);
            }
            int waiting = 0;
            for (Order o : waitingOrders) {
                waiting += o.nBurgers;
            }
            return waiting;
        }

    }

    public int customerWaitTime(int id) throws IllegalNumberException {
        if (customerTree.search(id) == false) {
            throw new IllegalNumberException("Exception");
        } else {
            int cIndex = customerTree.find(id).index;
            return CustomerArray.get(cIndex).waitTime;
        }
    }

    public float avgWaitTime() {
        return totalCustomerTime / numberOfCustomers;
    }

    private void takingOrders() {
        boolean found = true;
        ArrayList<Order> newOrders = new ArrayList<Order>();
        while (found && customerInQueues > 0) {
            int customerIndex = customerHeap.peek().index;
            Customer c = CustomerArray.get(customerIndex);

            if (c.orderTime == globalTime) {

                // Taking order
                Order o = new Order(c.customerId, c.burgers, c.locationIndex, c.orderQueueNum);
                o.orderTime = globalTime;
                newOrders.add(o);

                // Removing customer from queue
                customerHeap.poll();
                int queueIndex = c.orderQueueNum;
                queueLengths[queueIndex]--;
                customerInQueues--;
                customerLines.get(queueIndex).removeFirst();
                c.inQueue = false;

            } else {
                found = false;
            }
        }
        // sorting orders of same time = globalTime based on queueNumber in reverse and
        // adding to waiting
        // orders.
        Collections.sort(newOrders, new Comparator<Order>() {
            public int compare(Order o1, Order o2) {
                return o2.queueNum - o1.queueNum;
            }
        });
        for (int k = 0; k < newOrders.size(); k++) {
            waitingOrders.add(newOrders.get(k));
        }
    }

    private void removePatties() {
        int i = 0;
        while (i < griddle.size()) {
            Order o = griddle.peek();
            if (o.completeTime == globalTime) {
                gridSpace += o.nBurgers;
                griddle.removeFirst();
                Customer doneC = CustomerArray.get(o.customerLoca);
                if (o.nBurgers == doneC.burgers) {
                    doneC.complete = true;
                    doneC.waitTime = globalTime - doneC.enterTime + 1;
                    totalCustomerTime += doneC.waitTime;
                } else {
                    doneC.burgers -= o.nBurgers;
                }
            } else {
                break;
            }
        }
    }

    private void addPatties() {
        while (gridSpace > 0 && waitingOrders.size() > 0) {
            Order toG = waitingOrders.removeFirst();
            if (toG.nBurgers <= gridSpace) {
                toG.completeTime = globalTime + 10;
                gridSpace -= toG.nBurgers;
                griddle.add(toG);
            } else {
                Order break1 = new Order(toG.customerId, gridSpace, toG.customerLoca, toG.queueNum);
                Order break2 = new Order(toG.customerId, toG.nBurgers - gridSpace, toG.customerLoca, toG.queueNum);
                break1.completeTime = globalTime + 10;
                griddle.add(break1);
                waitingOrders.addFirst(break2);
                gridSpace = 0;
            }
        }
    }

    private int shortestQueueIndex() {
        int min = queueLengths[0];
        int index = 0;
        for (int i = 0; i < numberOfQueues; i++) {
            if (queueLengths[i] < min) {
                min = queueLengths[i];
                index = i;

            }
        }
        return index;
    }
}

class QueueNode {
    int key;
    int index;

    QueueNode(int val, int ind) {
        key = val;
        index = ind;
    }
}

class PriorityQueue {
    public Vector<QueueNode> A;

    public PriorityQueue() {
        A = new Vector<>();
    }

    public int parent(int i) {
        if (i == 0) {
            return 0;
        }
        return (i - 1) / 2;
    }

    public int LEFT(int i) {
        return (2 * i + 1);
    }

    public int RIGHT(int i) {
        return (2 * i + 2);
    }

    public void swap(int x, int y) {
        QueueNode temp = A.get(x);
        A.setElementAt(A.get(y), x);
        A.setElementAt(temp, y);
    }

    public void heapify_down(int i) {
        int left = LEFT(i);
        int right = RIGHT(i);
        int smallest = i;
        if (left < size() && A.get(left).key < A.get(i).key) {
            smallest = left;
        }
        if (right < size() && A.get(right).key < A.get(smallest).key) {
            smallest = right;
        }
        if (smallest != i) {
            swap(i, smallest);
            heapify_down(smallest);
        }
    }

    public void heapify_up(int i) {
        if (i > 0 && A.get(parent(i)).key > A.get(i).key) {
            swap(i, parent(i));
            heapify_up(parent(i));
        }
    }

    public int size() {
        return A.size();
    }

    public Boolean isEmpty() {
        return A.isEmpty();
    }

    public void add(Integer key, int ind) {
        QueueNode newN = new QueueNode(key, ind);
        A.addElement(newN);
        int index = size() - 1;
        heapify_up(index);
    }

    public QueueNode poll() {
        if (size() != 0) {
            QueueNode root = A.firstElement();
            A.setElementAt(A.lastElement(), 0);
            A.remove(size() - 1);
            heapify_down(0);
            return root;
        } else {
            return null;
        }
    }

    public QueueNode peek() {
        if (size() != 0) {
            return A.firstElement();
        } else {
            return null;
        }
    }

}

class Node {
    public int key;
    public int balance;
    public int height;
    public Node left, right, parent;
    public int index;

    Node(int k, Node p) {
        key = k;
        parent = p;
    }
}

class AVLTree {
    private Node root;

    public boolean insert(int key) {
        if (root == null)
            root = new Node(key, null);
        else {
            Node n = root;
            Node parent;
            while (true) {
                if (n.key == key)
                    return false;
                parent = n;
                boolean goLeft = n.key > key;
                n = goLeft ? n.left : n.right;
                if (n == null) {
                    if (goLeft) {
                        parent.left = new Node(key, parent);
                    } else {
                        parent.right = new Node(key, parent);
                    }
                    rebalance(parent);
                    break;
                }
            }
        }
        return true;
    }

    private void delete(Node node) {
        if (node.left == null && node.right == null) {
            if (node.parent == null)
                root = null;
            else {
                Node parent = node.parent;
                if (parent.left == node) {
                    parent.left = null;
                } else
                    parent.right = null;
                rebalance(parent);
            }
            return;
        }
        if (node.left != null) {
            Node child = node.left;
            while (child.right != null)
                child = child.right;
            node.key = child.key;
            node.index = child.index;
            delete(child);
        } else {
            Node child = node.right;
            while (child.left != null)
                child = child.left;
            node.key = child.key;
            node.index = child.index;
            delete(child);
        }
    }

    public void delete(int delKey) {
        if (root == null)
            return;
        Node node = root;
        Node child = root;
        while (child != null) {
            node = child;
            child = delKey >= node.key ? node.right : node.left;
            if (delKey == node.key) {
                delete(node);
                return;
            }
        }
    }

    private void rebalance(Node n) {
        setBalance(n);
        if (n.balance == -2) {
            if (height(n.left.left) >= height(n.left.right))
                n = rotateRight(n);
            else
                n = rotateLeftThenRight(n);
        } else if (n.balance == 2) {
            if (height(n.right.right) >= height(n.right.left))
                n = rotateLeft(n);
            else
                n = rotateRightThenLeft(n);
        }
        if (n.parent != null) {
            rebalance(n.parent);
        } else {
            root = n;
        }
    }

    private Node rotateLeft(Node a) {
        Node b = a.right;
        b.parent = a.parent;
        a.right = b.left;
        if (a.right != null)
            a.right.parent = a;
        b.left = a;
        a.parent = b;
        if (b.parent != null) {
            if (b.parent.right == a) {
                b.parent.right = b;
            } else {
                b.parent.left = b;
            }
        }
        setBalance(a, b);
        return b;
    }

    private Node rotateRight(Node a) {
        Node b = a.left;
        b.parent = a.parent;
        a.left = b.right;
        if (a.left != null)
            a.left.parent = a;
        b.right = a;
        a.parent = b;
        if (b.parent != null) {
            if (b.parent.right == a) {
                b.parent.right = b;
            } else {
                b.parent.left = b;
            }
        }
        setBalance(a, b);
        return b;
    }

    private Node rotateLeftThenRight(Node n) {
        n.left = rotateLeft(n.left);
        return rotateRight(n);
    }

    private Node rotateRightThenLeft(Node n) {
        n.right = rotateRight(n.right);
        return rotateLeft(n);
    }

    private int height(Node n) {
        if (n == null)
            return -1;
        return n.height;
    }

    private void setBalance(Node... nodes) {
        for (Node n : nodes) {
            reheight(n);
            n.balance = height(n.right) - height(n.left);
        }
    }

    private void reheight(Node node) {
        if (node != null) {
            node.height = 1 + Math.max(height(node.left), height(node.right));
        }
    }

    public Node find(int key) {
        Node result = searchHelper(this.root, key);
        if (result != null)
            return result;
        return null;
    }

    public boolean search(int key) {
        Node result = searchHelper(this.root, key);
        if (result != null)
            return true;
        return false;
    }

    private Node searchHelper(Node root, int key) {
        if (root == null || root.key == key)
            return root;
        if (root.key > key)
            return searchHelper(root.left, key);
        return searchHelper(root.right, key);
    }

    public int minIndex() {
        Node min = root;
        while (min.left != null) {
            min = min.left;
        }
        return min.index;
    }
}