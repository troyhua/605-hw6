import java.util.Hashtable;

public class Util{
    public static double get(Hashtable<String, Double> table, String key){
        if (table.containsKey(key))
            return table.get(key);
        else
            return 0.0;

    }
}
