import java.util.Hashtable;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.io.BufferedReader;
import java.io.FileReader;

public class ApproxPageRank{
    static String input_path = "";
    static String seed = "";
    static double alpha;
    static double epsilon;
    static Hashtable<String, String[]> cache;

    public static void main(String[] args){
        input_path = args[0];
        seed = args[1];
        alpha = Double.parseDouble(args[2]);
        epsilon = Double.parseDouble(args[3]);
        System.err.println("seed: " + seed);
        System.err.println("alpha: " + alpha);
        System.err.println("epsilon: " + epsilon);
        
        cache = new Hashtable<String, String[]>();

        Hashtable<String, Double> pr = computePR();
        List<Node> subgraph = createSubGraph(pr);

        System.err.println(subgraph.size());

        for (Node node : subgraph){
            System.out.println(node.name + "\t" + pr.get(node.name));
        }

//        Hashtable<String, Node> nodeDict = createNodeDict(subgraph);
//        HashSet<String> selected = computeConduct(subgraph);

        //outputGDF(selected, nodeDict);

//        output(selected, nodeDict);

        
    }

    static void outputGDF(HashSet<String> selected, Hashtable<String, Node> nodeDict){
        System.out.println("nodedef>name VARCHAR,label VARCHAR,width DOUBLE");
        for (String nodeName : selected){
            double size = Math.max(1.0, Math.log(nodeDict.get(nodeName).score / epsilon));
            System.out.println(nodeName + ",'" + nodeName + "'," + size);
        }
        System.out.println("edgedef>node1 VARCHAR,node2 VARCHAR");
        for (String nodeName : selected){
            for (String nei : nodeDict.get(nodeName).neighbors){
                if (selected.contains(nei)){
                    System.out.println(nodeName + "," + nei);
                }
            }
        }
    }

    static Hashtable<String, Node> createNodeDict(List<Node> subgraph){
        Hashtable<String, Node> dict = new Hashtable<String, Node>();
        for (Node node : subgraph){
            dict.put(node.name, node);
        }
        return dict;
    }

    static Hashtable<String, Double> computePR(){
        Hashtable<String, Double> p = new Hashtable<String, Double>();
        Hashtable<String, Double> r = new Hashtable<String, Double>();
        r.put(seed, 1.0);
        int changed = 0;
        do{
            changed = 0;
            IO.reset(input_path);
            String[] line = IO.nextLine();
            while (line != null){
                if ( rd(line, r) >= epsilon ){
                    updateCache(line);
                    push(line, p, r);
                    changed++;
                }
                line = IO.nextLine();
            }
            if (cache.size() > 10){
                for (int i = 0; i < 5; i++){
                    for (String key : cache.keySet()){
                        String[] aline = cache.get(key);
                        if (rd(aline, r) >= epsilon ){
                            push(aline, p, r);
                        }
                    }
                }
            }
            System.err.println(changed);
        }while (changed > 0);

        return p;
    }

    static void updateCache(String[] line){
        if (!cache.containsKey(line[0])){
            cache.put(line[0], line);
        }
    }

    static void push(String[] line, Hashtable<String, Double> p, Hashtable<String, Double> r){
        String u = line[0];
        p.put(u, Util.get(p, u) + alpha * Util.get(r, u));

        for (int i = 1; i < line.length; i++){
            String v = line[i];
            r.put(v, Util.get(r ,v) + ( 1.0 - alpha ) * Util.get(r, u) / 2.0 / (line.length - 1.0));
        }
        r.put(u, Util.get(r, u) * ( 1.0 - alpha ) / 2.0);
    }

    static double rd(String[] u, Hashtable<String, Double> table){
        return Util.get(table, u[0]) / (u.length - 1.0);
    }


    static List<Node> createSubGraph(Hashtable<String, Double> pr){
        List<Node> subgraph = new ArrayList<Node>();
        Hashtable<String, Node> nodeDict = new Hashtable<String, Node>();
        for (String key : pr.keySet()){
            Node node = new Node(key);
            node.score = pr.get(key);
            subgraph.add(node);
            nodeDict.put(key, node);
        }
        Collections.sort(subgraph);
        IO.reset(input_path);
        String[] line = IO.nextLine();
        while (line != null){
            String u = line[0];
            if (nodeDict.containsKey(u)){
                Node node = nodeDict.get(u);
                node.degree = line.length - 1;
                for (int i = 1; i < line.length; i++){
                    if (nodeDict.containsKey(line[i]))
                        node.neighbors.add(line[i]);
                }
            }
            line = IO.nextLine();
        }
        return subgraph;
    }

    static HashSet<String> computeConduct(List<Node> subgraph){
        HashSet<String> S = new HashSet<String>();
        Node u = subgraph.get(0);
        S.add(u.name);
        double volume = u.degree;
        double boundary = u.degree;
        for (int i = 1; i < subgraph.size(); i++){
            Node v = subgraph.get(i);
            double tmpVolume = volume + v.degree;
            double tmpBoundary = boundary;
            int change = 0;
            for (String nei : v.neighbors){
                if (S.contains(nei)){
                    change++;
                }
                tmpBoundary -= change;
                tmpBoundary += v.degree - change;
            }
            if (tmpBoundary / tmpVolume < boundary / volume){
                S.add(v.name);
                boundary = tmpBoundary;
                volume = tmpVolume;
            }
        }
        return S;
    }

    static void output(HashSet<String> selected, Hashtable<String, Node> subgraph){
        for (String name : selected){
            System.out.println(name + "\t" + subgraph.get(name).score);
        }
    }

    static class Node implements Comparable<Node>{
        public String name;
        public double score;
        public int degree;
        public HashSet<String> neighbors;
        public Node(String name){
            neighbors = new HashSet<String>();
            this.name = name;
        }
        
        @Override
        public int compareTo(Node anotherNode) {
        return this.score - anotherNode.score > 0 ? -1 : 1;
        } 
    }
}


