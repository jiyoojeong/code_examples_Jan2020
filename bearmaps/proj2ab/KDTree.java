package bearmaps.proj2ab;

import java.util.ArrayList;
import java.util.List;


public class KDTree implements PointSet {
    private ArrayList<Node> tree;

    public static class Node {
        private Point p;
        private Node left, right;
        private Node parent;
        private boolean X;

        private Node(Point point, boolean x) {
            p = point;
            this.parent = null;
            left = null;
            right = null;
            X = x;
        }

        private void setParent(Node parent) {
            this.parent = parent;
        }

        private Node getParent() {
            return parent;
        }

        public Point getP() {
            return p;
        }

        private boolean filled() {
            return left != null && right != null;
        }
        public boolean hasRight() {
            return right != null;
        }
        public boolean hasLeft() {
            return left != null;
        }
    }

    public KDTree(List<Point> points) {
        //shuffle points
        tree = new ArrayList<>(points.size());
        boolean x = true;
        Node n = new Node(points.get(0), true);
        tree.add(n);
        Node par = n;
        for (int i = 1; i < points.size(); i++) {
            n = new Node(points.get(i), false);
            par = findPar(tree.get(0), n);
            if (par.X == n.X) {
                n.X = !par.X;
            }
            n.setParent(par);

            tree.add(n);
        }
    }

    //this is a helper method to find the correct parent to branch off of
    private Node findPar(Node par, Node n) {
        while (par.filled()) {
            //compare
            double c1 = par.getP().getY();
            double c2 = n.getP().getY();
            if (par.X) {
                c1 = par.getP().getX();
                c2 = n.getP().getX();
            }

            if (c2 < c1) {
                par = par.left;
            } else {
                par = par.right;
            }
        } //once par is not filled
        //check par has any left or right
        double c1 = par.getP().getY();
        double c2 = n.getP().getY();
        if (par.X) {
            c1 = par.getP().getX();
            c2 = n.getP().getX();
        }
        if (par.hasRight() || par.hasLeft()) {
            if (c2 < c1 && par.hasLeft()) {
                return findPar(par.left, n);
            } else if (c2 < c1) {
                par.left = n;
                return par;
            } else if (c2 >= c1 && par.hasRight()) {
                return findPar(par.right, n);
            } else {
                par.right = n;
                return par;
            }
        }

        if (c2 < c1) {
            par.left = n;
        } else {
            par.right = n;
        }
        return par;
    }

    @Override
    public Point nearest(double x, double y) {
        Node best = tree.get(0);
        best = nearest(best.right, new Point(x, y), best);
        best = nearest(tree.get(0).left, new Point(x, y), best);
        return best.getP();
    }

    private Node nearest(Node nn, Point goal, Node best) {
        if (nn == null) {
            return best;
        }
        if (dist(nn.getP(), goal) < dist(best.getP(), goal)) {
            best = nn;
        }

        double n, g;
        if (nn.X) {
            n = nn.getP().getX();
            g = goal.getX();
        } else {
            n = nn.getP().getY();
            g = goal.getY();
        }

        Node good, bad;
        if (g < n) {
            good = nn.left;
            bad = nn.right;
        } else {
            good = nn.right;
            bad = nn.left;
        }

        best = nearest(good, goal, best);

        if (Math.abs(g - n) < dist(best.getP(), goal)) {
            best = nearest(bad, goal, best);
        }
        return best;
    }

    private double dist(Point p, Point b) {
        return Math.sqrt(Point.distance(p, b));
    }

    public void print() {
        Node po = tree.get(0);
        printHelper(po, 1, 0);
    }

    private void printHelper(Node po, int index, int r) {
        if (po == null) {
            return;
        }

        String tabs = "";
        // 1 2 4 8 = 2^k
        //for (int i = index - (int) Math.pow(2, r); i < (int) (2 * Math.pow(2, r)) && i < 5; i++) {
          //  tabs += "  ";
        //}

        System.out.printf(tabs + "(%.2f, %.2f) " + index, po.getP().getX(), po.getP().getY());
        System.out.println();
        printHelper(po.left, index * 2, r + 1);
        printHelper(po.right, index * 2 + 1, r + 1);
    }

}
