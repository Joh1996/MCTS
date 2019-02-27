package NewMDP;

import problem.*;
import simulator.Simulator;
import simulator.State;
import simulator.Step;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Justin on 11/8/18.
 */
public class Monte {

    Simulator simulator;
    State initialState;
    ProblemSpec ps;
    List<Node> visitedNodes;
    List<State> allStates;
    List<Action> availableActions;
    List<Action> recActions;
    static double epsilon = 1e-6;
    static final double gamma = 0.95;
    private double nVisits, totValue;
    static Random r = new Random();


    protected int NT;
    protected List<String> allCars;
    protected List<String> allDrivers;
    protected List<Tire> allTires;
    protected Tire tire;
    protected Node root;
    private static final double EXPLORATION_PARAMETER = Math.sqrt(2);


    public List<Node> bestNodes;

    public Monte(Node initialNode, ProblemSpec problem) {
        this.simulator = simulator;
        this.ps = problem;
        this.visitedNodes = new ArrayList<>();
        this.NT = ps.getNT();
        this.allStates = new ArrayList<>();
        availableActions = new ArrayList<>();
        recActions = new ArrayList<>();
        allCars = new ArrayList<>();
        allDrivers = new ArrayList<>();
        allTires = new ArrayList<>();
        this.root = initialNode;
        bestNodes = new ArrayList<>();
        this.simulator = new Simulator(problem,
                "C:/Users/Justin/Desktop/Carlo/assignment-2-master/examples/level_1/testOUTPUT123123.txt");

        fillLists();
        fillActions(ps.getLevel().getLevelNumber());
        end();

    }


    public void end() {
        //start
        Node node = traverse(root);
        State state = simulator.step(node.action);
        while(!isGoal(state)) {
            System.out.println(simulator.getSteps());
            node = traverse(node);
            state = simulator.step(node.action);
        }
    }


    public Node newSelect(Node n) {
        Node selected = null;
        double bestValue = Double.MIN_VALUE;
        for (Node c : (List<Node>) n.children) {
            double anotherUCT = ((double) c.stateVal / (double) c.visits) + (1.44 * Math.sqrt(Math.log(n.visits) / (double) c.visits));
            // small random number to break ties randomly in unexpanded nodes
            if (anotherUCT > bestValue) {
                selected = c;
                bestValue = anotherUCT;
            }
        }
        return selected;
    }




    public void backpropagate(Node expandedNode, double rolloutResult) {
        Node currentNode = expandedNode;
        while(currentNode != null) {
            currentNode.updateVisit();
            currentNode.incrementStateVal(rolloutResult);
            currentNode = currentNode.parent; // go back up
        }
    }


    // Works
    // Adds nodes to child based on all available actions from that state
    public void expand(Node selectedNode) {
        for(Action action : availableActions) {
            selectedNode.addChild(new Node(new Transition(selectedNode.getData(),
                    ps).getNextState(action), action));
        }
    }



    /**
     * This is what we time
     */
    // Tree Traverse & Node Expansion
    public Node traverse(Node node) {
        Node startingNode = node;
        if(node.children.size() == 0) {
            expand(node);
        }
        for (long stop=System.nanoTime()+TimeUnit.SECONDS.toNanos(1);stop>System.nanoTime();) {
            for (Node c : (List<Node>) startingNode.children) {
                simulate(c);
            }
        }
        startingNode = newSelect(startingNode);
        return startingNode;
    }



    public void simulate(Node initialNode) {


        Node currentNode = initialNode;
        State currentState = currentNode.data;
        Transition transition = new Transition();
        Random random = new Random();
        double reward = 1;
        int step = transition.getSteps(); // This will be the simulation count for that node if needed

        while(!isGoal(currentState)) {
            int randomIndex = random.nextInt(availableActions.size());
            Action action = availableActions.get(randomIndex);
            currentState = new Transition(currentState, ps).getNextState(action);
            step++;
        }
        reward = gamma*reward/step;
        backpropagate(initialNode, reward);

    }


    // Generate a random state until goal
    // Rollout should return an integer or double

    public boolean isGoal(State state) {
        if(state.getPos() >= ps.getN()) {
            return true;
        }
        return false;
    }
    


    /**
     *  TESTING PURPOSES
     *  Will Keep because I have been getting 2 hours of sleep for two weeks and probably also failed my COMP3506
     *  exam because Ive been trying to finish this assignment
     *
     *
     * Works for Levels 1-3
     * Couldn't get 4-5 to work
     *
     *
     */
    private void fillActions(int i) {
        if(i == 1) {
            Action A1 = new Action(ActionType.MOVE);
            Action A21 = new Action(ActionType.CHANGE_CAR, ps.getCarOrder().get(0));
            Action A22 = new Action(ActionType.CHANGE_CAR, ps.getCarOrder().get(1));
            Action A31 = new Action(ActionType.CHANGE_DRIVER, allDrivers.get(0));
            Action A32 = new Action(ActionType.CHANGE_DRIVER, allDrivers.get(1));
            Action A4 = new Action(ActionType.CHANGE_TIRES, selectTire(allTires)); // Figure out how to change Tires based on terrain
            availableActions.add(A1);
            availableActions.add(A21);
            availableActions.add(A22);
            availableActions.add(A31);
            availableActions.add(A32);
            availableActions.add(A4);
        }
        if(i == 2 || i == 3) {
            Action A1 = new Action(ActionType.MOVE);
            Action A2 = new Action(ActionType.CHANGE_CAR, ps.getCarOrder().get(1));
            Action A3 = new Action(ActionType.CHANGE_DRIVER, allDrivers.get(1));
            Action A4 = new Action(ActionType.CHANGE_TIRES, selectTire(allTires));
            Action A5 = new Action(ActionType.ADD_FUEL, ps.FUEL_MAX); // fix this
            Action A6 = new Action(ActionType.CHANGE_PRESSURE, TirePressure.ONE_HUNDRED_PERCENT); // Fix this
            availableActions.add(A1);
            availableActions.add(A2);
            availableActions.add(A3);
            availableActions.add(A4);
            availableActions.add(A5);
            availableActions.add(A6);
            //Add 2 more Levels
        }
    }

    private void fillLists() {
        for(Tire tire : ps.getTireOrder()) {
            allTires.add(tire);
        }
        for(String carType : ps.getCarOrder()) {
            allCars.add(carType);
        }
        for(String driverName : ps.getDriverOrder()) {
            allDrivers.add(driverName);
        }
    }

    public Tire selectTire(List<Tire> tireList) {
        this.tire = ps.getFirstTireModel();
        double p = Math.random();
        double cumulativeProbability = 0.0;
        for (Tire tire : allTires) {
            cumulativeProbability += ps.getTireModelMoveProbability().get(Tire.ALL_TERRAIN)[0];
            if (p <= cumulativeProbability) {
                return tire;
            }
        }

        return tire;
    }


}
