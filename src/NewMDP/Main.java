package NewMDP;

import problem.*;
import simulator.Simulator;
import simulator.State;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Created by Justin on 11/7/18.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        ProblemSpec problem = new ProblemSpec("C:/Users/Justin/Desktop/Carlo/assignment-2-master/examples/level_1/input_lvl1_2.txt");
        State state = getStartState(problem);
        Node initialNode = new Node(state);
        Monte monte = new Monte(initialNode, problem);

    }


    /**
     * Returns start state for main
     * @param problem
     * @return
     */
    public static State getStartState(ProblemSpec problem) {
        return new State(1, false, false, problem.getFirstCarType(), ProblemSpec.FUEL_MAX,
                TirePressure.ONE_HUNDRED_PERCENT, problem.getFirstDriver(), problem.getFirstTireModel());
    }

    /**
     * gets Goal State for main
     * @param problem
     * @return
     */
    public int getGoalStatePos(ProblemSpec problem) {
        int goalInt = problem.getNT();
        return goalInt;
    }

}
