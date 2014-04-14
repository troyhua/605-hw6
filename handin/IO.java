import java.io.BufferedReader;
import java.io.FileReader;

public class IO{
   static BufferedReader reader;

    public static void reset(String filename){
        try{
//        if (reader != null){
//            try{
//            reader.close();
//            }catch(Exception e){   // in case multiple close on the same reader         }
//            }
//        }
        reader = new BufferedReader(new FileReader(filename));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static String[] nextLine(){
        try{
        String line = reader.readLine();
        if (line == null)
            return null;
        else{
                String[] rest = line.split("\\t");

            return rest;
            }

        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
