import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Tokenize {
    public static void main(String[] args)  throws IOException {
        System.out.println(Tokenize.tokenize("LA040190-0174"));
        HashMap<String, Integer> query_judge = new HashMap<>();
        query_judge.put("g",1);
        query_judge.put("g",2);
        System.out.println(query_judge);
    }

    public static String tokenize(String DOCNO) throws IOException {
        int year = Integer.parseInt(DOCNO.substring(6,8));
        int month = Integer.parseInt(DOCNO.substring(2,4));
        int day = Integer.parseInt(DOCNO.substring(4, 6));
        int No = Integer.parseInt(DOCNO.substring(9,13));
        String path = "latime/" + year + "/" + month + "/" + day + "/" + No;
        ///////////////////////// generate path///////////////////////////////////////
        File f = new File(path); //build a new file
        ///////////////////////////////////////////////////////////////////////
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
        String Line = "", Tokener = "";
        /////////////////////append the docno/////////////////////////////
        while(!(Line = br.readLine()).equals("</DOC>")) {
            //////////////////////////////////////////write words//////////////////////
            if(Line.equals("<HEADLINE>")) {
                while(!((Line = br.readLine()).equals("</HEADLINE>"))) {
                    if(Line.equals("<P>") || Line.equals("</P>"))
                        continue;
                    Tokener = Tokener.concat(Line.toLowerCase().replaceAll("[\\pP‘’“”><+=$]", ""));
                }}
            /////////////////////////////////////////////
            if(Line.equals("<GRAPHIC>") || Line.equals("<TEXT>") ) {
                while(!((Line = br.readLine()).equals("</GRAPHIC>") || (Line.equals("</TEXT>")))) {
                    if(Line.equals("<P>") || Line.equals("</P>"))
                        continue;
                    Tokener = Tokener.concat(Line.toLowerCase().replaceAll("[\\pP‘’“”><+=$]", ""));
                }
            }
        }
        return Tokener;
    }



    public static HashMap<String, Integer> query_separate(String Query){
        String[] Querys = Query.toLowerCase().replaceAll("[\\pP‘’“”><+=$]", " ").split(" +");
        HashMap<String, Integer> query_judge = new HashMap<>();
        for(String query : Querys) {
            if(query_judge.containsKey(query))
                query_judge.put(query, (query_judge.get(query) + 1));
            else
                query_judge.put(query, 1);
        }
        return query_judge;
    }

    public static HashMap<String, Integer> doc_separate(String Query){
        String[] Querys = Query.toLowerCase().replaceAll("[\\pP‘’“”><+=$]", " ").split(" +");
        HashMap<String, Integer> query_judge = new HashMap<>();
        for(String query : Querys) {
            if(query_judge.containsKey(query))
                query_judge.put(query, (query_judge.get(query) + 1));
            else
                query_judge.put(query, 1);
        }
        return query_judge;
    }

    public static double average_doc_length(ArrayList<Double> all_length) {
        double summary_length = 0;
        for(double length : all_length)
            summary_length+= length;
        return summary_length/all_length.size();
    }


}

