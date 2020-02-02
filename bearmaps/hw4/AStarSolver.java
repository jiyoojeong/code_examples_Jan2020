package bearmaps.hw4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import bearmaps.proj2ab.ArrayHeapMinPQ;
import edu.princeton.cs.algs4.Stopwatch;

public class AStarSolver<Vertex> implements ShortestPathsSolver<Vertex> {
    private AStarGraph<Vertex> g;
    private Vertex goal;
    private double time;
    private SolverOutcome outcome;
    private List<Vertex> solution;
    private double weight;
    private int numOps;

    private ArrayHeapMinPQ<Vertex> fringe;
    private Map<Vertex, Double> dists; //tracks the best known dists
    private Map<Vertex, Vertex> parents; //key is the node, value is the parent

    public AStarSolver(AStarGraph<Vertex> input, Vertex start, Vertex end, double timeout) {
        Stopwatch w = new Stopwatch();
        g = input;
        goal = end;
        numOps = 0;
        solution = new ArrayList<>();
        weight = 0;
        dists = new HashMap<>();
        parents = new HashMap<>();

        //solve
        //insert source index to pq
        fringe = new ArrayHeapMinPQ<>();
        fringe.add(start, 0);
        dists.put(start, 0.0);
        parents.put(start, null);
        // repeat until pq is empty, pq.getsmallest is goal, or timeout
        Vertex p = null;

        while (fringe.size() > 0 && w.elapsedTime() < timeout) {
            p = fringe.removeSmallest();
            numOps += 1;
            //solution.add(p);

            if (p.equals(goal)) {
                outcome = SolverOutcome.SOLVED;
                break;
            }

            for (WeightedEdge edge : g.neighbors(p)) {
                relax(edge);
            }
        }

        if (outcome == null && w.elapsedTime() >= timeout) {
            outcome = SolverOutcome.TIMEOUT;
        } else if (outcome == null && fringe.size() == 0) {
            outcome = SolverOutcome.UNSOLVABLE;
        }
        System.out.println("hi");
        while (p != null && parents.get(p) != null) {
            solution.add(0, p);
            p = parents.get(p);
        }
        //starts with p which is the end goal
        solution.add(0, start);
        weight = dists.get(solution.get(solution.size() - 1));
        time = w.elapsedTime();
    }

    private void relax(WeightedEdge e) {
        Vertex p = (Vertex) e.from();
        Vertex q = (Vertex) e.to();
        double w = e.weight();
        if (!dists.containsKey(q)) {
            dists.put(q, Double.POSITIVE_INFINITY);
        }

        if (dists.get(p) + w < dists.get(q)) {
            dists.put(q, dists.get(p) + w);
            if (parents.containsKey(q)) {
                parents.remove(q);
            }
            parents.put(q, p);
            if (fringe.contains(q)) {
                fringe.changePriority(q, dists.get(q) + g.estimatedDistanceToGoal(q, goal));
                //heuristic
            } else {
                fringe.add(q, dists.get(q) + g.estimatedDistanceToGoal(q, goal));
            }
        }
    }

    public SolverOutcome outcome() {
        return outcome;
    }

    public List<Vertex> solution() {
        return solution;
    }

    public double solutionWeight() {
        if (!outcome.equals(SolverOutcome.SOLVED)) {
            return 0;
        }
        return weight;
    }

    public int numStatesExplored() {
        //total num of pq dequeue ops
        return numOps;
    }

    public double explorationTime() {
        return time;
    }
}
