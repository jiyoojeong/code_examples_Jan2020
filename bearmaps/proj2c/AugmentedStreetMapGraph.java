package bearmaps.proj2c;

import bearmaps.hw4.streetmap.Node;
import bearmaps.hw4.streetmap.StreetMapGraph;
import bearmaps.proj2ab.Point;
import bearmaps.proj2ab.KDTree;
import edu.princeton.cs.algs4.TST;

import java.util.*;

/**
 * An augmented graph that is more powerful that a standard StreetMapGraph.
 * Specifically, it supports the following additional operations:
 *
 *
 * @author Alan Yao, Josh Hug, ________
 */
public class AugmentedStreetMapGraph extends StreetMapGraph {
    private Map<Point, Node> pts = new HashMap<>();
    private Map<String, LinkedList<Node>> names = new HashMap<>(); //clean, node
    private Map<String, LinkedList<String>> cleaned = new HashMap<>(); //cleaned, reg
    private KDTree tree;
    private TST<Node> trie;

    public AugmentedStreetMapGraph(String dbPath) {
        super(dbPath);
        // You might find it helpful to uncomment the line below:
        List<Node> nodes = this.getNodes();
        trie = new TST<>();
        //System.out.println(nodes);
        Point p;
        for (Node n : nodes) {
            if (!neighbors(n.id()).isEmpty()) {
                p = new Point(n.lon(), n.lat());
                pts.put(p, n);
            }
            if (n.name() != null) {
                String nn = cleanString(n.name());
                if (nn.length() >= 1) {
                    //System.out.println(nn);
                    trie.put(nn, n);
                }
                if (names.containsKey(nn)) {
                    names.get(nn).add(n);
                } else {
                    LinkedList<Node> nnn = new LinkedList<>();
                    nnn.add(n);
                    names.put(nn, nnn);
                }

                if (cleaned.containsKey(nn)) {
                    cleaned.get(nn).add(n.name());
                } else {
                    LinkedList<String> l = new LinkedList<>();
                    l.add(n.name());
                    cleaned.put(nn, l);
                }
            }
        }

        System.out.println(names);
        System.out.println(cleaned);
        //System.out.println();
        ArrayList<Point> arr = new ArrayList<>(pts.keySet());
        Collections.shuffle(arr);
        tree = new KDTree(arr);
    }


    /**
     * For Project Part II
     * Returns the vertex closest to the given longitude and latitude.
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    public long closest(double lon, double lat) {
        return pts.get(tree.nearest(lon, lat)).id();
    }


    /**
     * For Project Part III (gold points)
     * In linear time, collect all the names of OSM locations that prefix-match the query string.
     * @param prefix Prefix string to be searched for. Could be any case, with our without
     *               punctuation.
     * @return A <code>List</code> of the full names of locations whose cleaned name matches the
     * cleaned <code>prefix</code>.
     */
    public List<String> getLocationsByPrefix(String prefix) {
        List<String> fulls = new LinkedList<>();
        //System.out.print(trie.keysWithPrefix(prefix));
        for (String p : trie.keysWithPrefix(cleanString(prefix))) {
            if (p == null) {
                System.out.println("wot");
            } else {
                for (Node n : names.get(p)) {
                    fulls.add(n.name());
                }
            }
        }
        return fulls;
    }

    /**
     * For Project Part III (gold points)
     * Collect all locations that match a cleaned <code>locationName</code>, and return
     * information about each node that matches.
     * @param locationName A full name of a location searched for.
     * @return A list of locations whose cleaned name matches the
     * cleaned <code>locationName</code>, and each location is a map of parameters for the Json
     * response as specified: <br>
     * "lat" -> Number, The latitude of the node. <br>
     * "lon" -> Number, The longitude of the node. <br>
     * "name" -> String, The actual name of the node. <br>
     * "id" -> Number, The id of the node. <br>
     */
    public List<Map<String, Object>> getLocations(String locationName) {
        List<Map<String, Object>> locs = new ArrayList<>();
        //find all the nodes with the location name
        //for all the nodes, find their lat, lon, name, id
        //List<Node> nodesWName = new LinkedList<>();
        //LinkedList<String> realNames = cleaned.get(locationName); // linked list w all real names
        //LinkedList<Node> nodes;

        for (Node n : names.get(cleanString(locationName))) {
            Map<String, Object> m = new HashMap<>();
            m.put("lat", n.lat());
            m.put("lon", n.lon());
            m.put("name", n.name());
            m.put("id", n.id());
            locs.add(m);
        }
        return locs;
    }


    /**
     * Useful for Part III. Do not modify.
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    private static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

}