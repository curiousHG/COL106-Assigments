import java.util.*;
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
class Employee {
	int id;
	int level = 0;
	Vector<Integer> children = new Vector<>();
	int bossIndex = -1;
	int numChildren = 0;
	Employee(int val) {
		id = val;
	}
}
public class OrgHierarchy implements OrgHierarchyInterface {
	AVLTree tree = new AVLTree();
	Vector<Employee> allEmps = new Vector<>();
	int size = 0;
	Employee root;
	int endIndex = 0;
	int own;
	public boolean isEmpty() {
		if (size == 0) {
			return true;
		} else {
			return false;
		}
	}
	public int size() {
		return size;
	}
	public int level(int id) throws IllegalIDException, EmptyTreeException {
		if (tree.find(id) == null) {
			throw new IllegalIDException("No such id exists");
		} else if (size == 0) {
			throw new EmptyTreeException("No one in the organisation");
		} else {
			Node n = tree.find(id);
			Employee e = allEmps.get(n.index);
			return e.level;
		}
	}
	public void hireOwner(int id) throws NotEmptyException {
		if (size == 0) {
			Employee o = new Employee(id);
			o.level = 1;
			allEmps.add(o);
			tree.insert(id);
			own = id;
			Node owner = tree.find(id);
			owner.index = endIndex;
			endIndex++;
			size++;
		} else {
			throw new NotEmptyException("Owner already there");
		}
	}
	public void hireEmployee(int id, int bossid) throws IllegalIDException, EmptyTreeException {
		if (tree.find(id) != null || tree.find(bossid) == null || own == id) {
			throw new IllegalIDException("Wrong id given");
		} else if (size == 0) {
			throw new EmptyTreeException("No one in the organisation");
		} else {
			int bossIndexinArray = tree.find(bossid).index;
			Employee boss = allEmps.get(bossIndexinArray);
			boss.children.add(endIndex);
			boss.numChildren++;
			Employee newEmp = new Employee(id);
			newEmp.level = boss.level + 1;
			newEmp.bossIndex = bossIndexinArray;
			allEmps.add(newEmp);
			tree.insert(id);
			Node empNode = tree.find(id);
			empNode.index = endIndex;
			size++;
			endIndex++;
		}
	}
	public void fireEmployee(int id) throws IllegalIDException, EmptyTreeException {
		if (tree.find(id) == null|| own == id) {
			throw new IllegalIDException("Wrong id given");
		} else if (size == 0) {
			throw new EmptyTreeException("No one in the organisation");
		} else {
			int employeeIndex = tree.find(id).index;
			Employee e = allEmps.get(employeeIndex);
			Employee b = allEmps.get(e.bossIndex);
			for (int i = 0; i < b.numChildren; i++) {
				if (b.children.get(i) == employeeIndex) {
					b.children.remove(i);
					b.numChildren--;
					size--;
					break;
				}
			}
			tree.delete(id);
		}
	}
	public void fireEmployee(int id, int manageid) throws IllegalIDException, EmptyTreeException {
		if (tree.find(id) == null || tree.find(manageid) == null || own == id) {
			throw new IllegalIDException("Wrong id given");
		} else if (size == 0) {
			throw new EmptyTreeException("No one in the organisation");
		} else {
			int employeeIndex = tree.find(id).index;
			Employee e = allEmps.get(employeeIndex);
			Employee b = allEmps.get(e.bossIndex);
			for (int i = 0; i < b.numChildren; i++) {
				if (b.children.get(i) == employeeIndex) {
					b.children.remove(i);
					b.numChildren--;
					size--;
					break;
				}
			}
			int newBIndex = tree.find(manageid).index;
			Employee newB = allEmps.get(newBIndex);
			for (int j = 0; j < e.numChildren; j++) {
				int childIndex = e.children.get(j);
				newB.children.add(childIndex);
				Employee c = allEmps.get(childIndex);
				c.bossIndex = newBIndex;
			}
			newB.numChildren += e.numChildren;
			tree.delete(id);
		}
	}
	public int boss(int id) throws IllegalIDException, EmptyTreeException {
		if (tree.find(id) == null || own == id) {
			throw new IllegalIDException("Wrong id");
		} else if (size == 0) {
			throw new EmptyTreeException("No one in the organisation");
		} else {
			Employee e = allEmps.get(tree.find(id).index);
			return allEmps.get(e.bossIndex).id;
		}
	}
	public int lowestCommonBoss(int id1, int id2) throws IllegalIDException, EmptyTreeException {
		if (tree.find(id1) == null || tree.find(id2) == null|| own == id1 || own == id2) {
			throw new IllegalIDException("No such id exists");
		} else if (size == 0) {
			throw new EmptyTreeException("No one in the organisation");
		} else {
			Vector<Integer> parent1 = new Vector<>();
			Vector<Integer> parent2 = new Vector<>();
			Employee e1 = allEmps.get(tree.find(id1).index);
			Employee e2 = allEmps.get(tree.find(id2).index);
			while (e1.bossIndex != -1) {
				parent1.insertElementAt(allEmps.get(e1.bossIndex).id, 0);
				e1 = allEmps.get(e1.bossIndex);
			}
			while (e2.bossIndex != -1) {
				parent2.insertElementAt(allEmps.get(e2.bossIndex).id, 0);
				e2 = allEmps.get(e2.bossIndex);
			}
			Vector<Integer> bigchain = parent1.size() > parent2.size() ? parent1 : parent2;
			Vector<Integer> smallchain = parent1.size() > parent2.size() ? parent2 : parent1;
			int lowestBoss = smallchain.get(0);
			for (int i = 0; i < smallchain.size(); i++) {
				if (smallchain.get(i) == bigchain.get(i)) {
					lowestBoss = smallchain.get(i);
				} else {
					break;
				}
			}
			return lowestBoss;
		}
	}
	public String toString(int id) throws IllegalIDException, EmptyTreeException {
		if (tree.find(id) == null) {
			throw new IllegalIDException("No such id exists");
		} else if (size == 0) {
			throw new EmptyTreeException("No one in the organisation");
		} else {
			String s = "";
			Vector<Integer> temp = new Vector<>();
			Employee start = allEmps.get(tree.find(id).index);
			int startLevel = start.level;
			Vector<Employee> q = new Vector<>();
			q.add(start);
			while (!q.isEmpty()) {
				Employee person = q.remove(0);
				if (person.level != startLevel) {
					String k = "";
					temp.sort(null);
					for (int i = 0; i < temp.size(); i++) {
						k += " " + Integer.toString(temp.get(i));
					}
					s += k.substring(1) + ",";
					temp = new Vector<>();
					startLevel = person.level;
				}
				temp.add(person.id);
				for (int i = 0; i < person.numChildren; i++) {
					q.add(allEmps.get(person.children.get(i)));
				}
				if (q.isEmpty() == true) {
					temp.sort(null);
					String k = "";
					for (int i = 0; i < temp.size(); i++) {
						k += " " + Integer.toString(temp.get(i));
					}
					s += k.substring(1) + ",";
				}
			}
			return s.substring(0, s.length() - 1);
		}
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
}
