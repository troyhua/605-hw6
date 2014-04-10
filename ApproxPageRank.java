import java.util.Hashtable;

public class ApproxPageRank{
    static String input_path = "";
    static String seed = "";
    static double alpha;
    static double epsilon;

    public static void main(String[] args){
        input_path = args[0];
        seed = args[1];
        alpha = Double.parseDouble(args[2]);
        epsilon = Double.parseDouble(args[3]);

        Hashtable<String, Double> pr = computePR();

        Hashtable<String, Double> subgraph = computeSubgraph();

        output(subgraph);

        
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
                if ( rd(line, r) > epsilon ){
                    push(line, p, r);
                    changed++;
                }
                line = IO.nextLine();
            }
            System.err.println(changed);
        }while (changed > 0);

        return p;
    }

    static void push(String[] line, Hashtable<String, Double> p, Hashtalbe<String, Double> r){
        String u = line[0];
        p.put(u, Util.get(p, u) + alpha * Util.get(r, u));
        r.put(u, Util.get(r, u) * ( 1 - alpha ) / 2.0);

        for (int i = 1; i < line.length; i++){
            String v = line[i];
            r.put(v, Util.get(v) + ( 1 - alpha ) * Util.get(r, u) / 2 / (line.length - 1));
        }
    }

    static double rd(String[] u, Hashtable<String, Double> table){
        return Util.get(table, u[0]) / (u.length - 1);
    }

    static Hashtable<String, Double> computeSubgraph(){
        return null;
    }

    static void output(Hashtable<String, Double> subgraph){
    }
}


