import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static java.lang.System.out;


public class GetDoc {
    static HashMap intid;
    static ArrayList docno_title;
    public GetDoc() throws IOException {
        intid = DOCNOtoID();//constant structure for internal ID and DOCNO
        docno_title = IDtoDOCNO();// docno
    }

    public static void getScore(String DOCNO, String Query) throws IOException{
///////////////////////////////////////hash map///////////////////////
        int InternalID = 0;
        InternalID = Integer.parseInt((String) intid.get(DOCNO));
        /////////////////////file
        int year = Integer.parseInt(DOCNO.substring(6, 8));
        int month = Integer.parseInt(DOCNO.substring(2, 4));
        int day = Integer.parseInt(DOCNO.substring(4, 6));
        int No = Integer.parseInt(DOCNO.substring(9, 13));
        String Doc_path = "latime" + "/" + year + "/" + month + "/" + day + "/" + No;
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(Doc_path)));
        String Tokener = Create_File(br);
        String[] Tokeners = Tokener.split("\\.!?");
        //System.out.println(Tokeners[0]);
        //int score = scoreLine(Tokeners[0], "from chernobyl the");
        //System.out.println(score);
        /////////////////////////////////select the best 2
        String sentence1 = new String();
        String sentence2 = new String();
        int score1 = 1;
        int score2 = 0;
        //out.println(Tokeners[0]);
        if(-1 == metadata(docno_title,InternalID))// to prevent there is no title
            System.out.print(Tokeners[0].substring(1, Math.max(50, Tokeners[0].length())));
        for(int i = 0; i < Tokeners.length; i++) {
            String str = Tokeners[i];
            int scorenew = 0;
            if(i == 0)
                scorenew= scoreLine(str, Query) + 2;
            else if(i ==1 )
                scorenew = scoreLine(str, Query) + 1;
            else
                scorenew = scoreLine(str, Query);
            //System.out.println(scorenew);
            if(scorenew >= score1) {
                score2 = score1;
                sentence2 = sentence1;
                sentence1 = str;
                score1 = scorenew;
                //System.out.println("score1:" +score1);
                //System.out.println("score2:" +score2);
            }
            if(scorenew > score2 && scorenew < score1) {
                sentence2 = str;
                score2 = scorenew;
                //System.out.println("score2:" +score2);
                //System.out.println("sentence2:" +sentence2);
            }
        }
        System.out.print(sentence1.trim() + ". ");
        System.out.print(sentence2.trim() + ".");
    }

    private static int scoreLine(String Tokener, String Query) {//calculate each sentence's score
        String Queryslowcase = Query.toLowerCase().replaceAll("[\\pP‘’“”><+=$]", "");
        String[] Querys = Queryslowcase.split(" +");
        String Tokenerlowcase = Tokener.toLowerCase().replaceAll("[\\pP‘’“”><+=$]", "");
        String[] Tokeners = Tokenerlowcase.split(" +");
        if (Tokeners.length < 5)
            return 0;
        int C = 0, D = 0, Dflag = 0, N = 0;
        for(String query : Querys) {
            for (String tokener : Tokeners) {
                if (query.equals(tokener)) {
                    C++;
                    if (Dflag == 0) {
                        D++;
                        Dflag = 1;
                    }
                }
            }
            Dflag = 0;
        }//calculate C D
        for(int n = 0; n <Querys.length; n++) {//calculate N
            String Querylength = new String();
            for(int i = n; i < Querys.length; i++){
                if(i != n)
                    Querylength = Querylength.concat(" ");
                Querylength = Querylength.concat(Querys[i]);
                //System.out.println(Querylength);
                //System.out.println(N);
                if(-1 != Tokenerlowcase.indexOf(Querylength))
                    N =Math.max(N, i - n + 1);
            }
        }
        //System.out.println(N);
        return C + D + N;
    }

    private static String Create_File(BufferedReader br) throws IOException{
        String Line = "", Tokener = "";
        /////////////////////append the docno/////////////////////////////
        while(!(Line = br.readLine()).equals("</DOC>")) {
            ////////writer.append(Line + "\r\n");
            //////////////////////////////////////////write words//////////////////////
            if(Line.equals("<HEADLINE>")) {
                while(!((Line = br.readLine()).equals("</HEADLINE>"))) {
                    if(Line.equals("<P>") )
                        continue;
                    if(Line.equals("</P>")) {
                        //Tokener = Tokener.concat("\r\n");
                        continue;}
                    Tokener = Tokener.concat(Line + ".");
                }}
            /////////////////////////////////////////////
            if(Line.equals("<GRAPHIC>") || Line.equals("<TEXT>") ) {
                while(!((Line = br.readLine()).equals("</GRAPHIC>") || (Line.equals("</TEXT>")))) {
                    if(Line.equals("<P>") )
                        continue;
                    if(Line.equals("</P>")) {
                        //Tokener = Tokener.concat("\r\n");
                        continue;}
                    Tokener = Tokener.concat(Line);
                }
            }
        }
        return Tokener;
    }

    public static HashMap DOCNOtoID() throws IOException{
        String Line_example;
        HashMap<String,String> hMap = new HashMap<String,String>();
        BufferedReader index_br0 = new BufferedReader(new InputStreamReader(new FileInputStream("latime/index.txt")));
        for (int i = 1; (Line_example = index_br0.readLine()) != null; i++) {
            String DOCNO = Line_example.split(" ")[0];
            hMap.put(DOCNO,i+"");
        }
        return hMap;
    }

    public static ArrayList IDtoDOCNO() throws IOException{
        String Line_example;
        ArrayList <String> vector = new ArrayList <String>();
        BufferedReader index_br0 = new BufferedReader(new InputStreamReader(new FileInputStream("latime/index.txt")));
        for (int i = 1; (Line_example = index_br0.readLine()) != null; i++) {
            vector.add(Line_example);
        }
        return vector;
    }

    private static int metadata(ArrayList docno_title, int InternalID) {
        String[] content = docno_title.get(InternalID-1).toString().split(" +");
        int year = Integer.parseInt(content[0].substring(6, 8));
        int month = Integer.parseInt(content[0].substring(2, 4));
        int day = Integer.parseInt(content[0].substring(4, 6));
        //String[] Months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        if(content.length == 2)
            return -1;
        out.print(content[1]);
        for(int i = 2; i < content.length - 1; i++)
            out.print(" "+content[i]);
        out.print(";");
        out.println("(" + month + "/" + day + "/19" + year + ")");
            return 0;
    }
}

