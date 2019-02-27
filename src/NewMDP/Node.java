package NewMDP;

import problem.Action;
import simulator.State;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Node<T extends State> {


    /**
     * Node class for the tree
     * Each Node contains a State, Action, as well as their value, visits
     */


    protected T data = null;
    protected Node<T> parent;
    protected List<Node<T>> children = new ArrayList<>();
    protected double visits = 0;
    protected double stateVal;
    protected Action action;
    protected double UCT;

    public Node() {
    }

    public Node(State state, Action action) {
        this.data = (T) state;
        this.action = action;
    }

    public Node(Node node, Action action) {
        this.data = data;
        this.stateVal = node.stateVal;
        this.visits = node.visits;
        this.action = node.action;
    }

    public Node(T data) {
        this.data = data;
        this.stateVal = 0;
        this.action = null;
    }

    public Node(Node node) {
        this.data = data;
        this.stateVal = node.stateVal;
        this.visits = node.visits;
        this.UCT = (this.stateVal / this.visits) + (2 * Math.sqrt(Math.log(this.parent.visits) / this.visits));
    }

    public Node(T data, Node<T> parent) {
        this.data = data;
        this.parent = parent;
    }

    public Node(T data, Node<T> parent, Action action) {
        this.data = data;
        this.parent = parent;
        this.action = action;
    }

    /**
     * Returns Children of the Node
     */
    public List<Node<T>> getChildren() {
        return children;
    }

    /**
     * Sets parent of the node
     */
    public void setParent(Node<T> parent) {
        this.parent = parent;
    }

    /**
     * Adds child to the node
     */
    public void addChild(T data) {
        Node<T> newChild = new Node<>(data);
        newChild.setParent(this);
        children.add(newChild);
    }


    /**
     * Another add Child method
     */
    public void addChild(Node<T> child) {
        child.setParent(this);
        this.children.add(child);
    }


    /**
     * @return Data of node
     */
    public T getData() {
        return this.data;
    }

    /**
     * Sets data manually
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * Returns whether the node is a root
     *
     * @return
     */
    public boolean isRoot() {
        return (this.parent == null);
    }

    /**
     * Returns whether the node is a leaf
     */
    public boolean isLeaf() {
        return this.children.size() == 0;  // Changed this from == null to size == 0;
    }

    /**
     * Remove parent if needed
     */
    public void removeParent() {
        this.parent = null;
    }


    /**
     * Raises state value
     */
    public double incrementStateVal(double result) {
        stateVal += result;
        return stateVal;
    }

    /**
     * Updates node visit count
     */
    public void updateVisit() {
        visits++;
    }

    /**
     * returns visit count for node
     */
    public double getVisits() {
        return visits;
    }

    // update without returning anything
    public void updateStateValue(double d) {
        stateVal += d;
    }

    // return without updating anything
    public double getStateVal() {
        return stateVal;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}

