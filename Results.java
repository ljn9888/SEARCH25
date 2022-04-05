import java.util.*;

public class Results{
    private HashMap<String, ArrayList> queryToResults;
    private HashMap<String, Boolean> queryToResultIsSorted;

    public class tupleResult implements Comparable {
        private String docno;
        private float score;
        private int rank;

        public tupleResult(String docno, float score, int rank) {
            this.docno = docno;
            this.score = score;
            this.rank = rank;
        }

        public String getDocNo() {
            return this.docno;
        }
        public double getScore() {
            return this.score;
        }
        public int getRank() {
            return this.rank;
        }

        //Sort by result rank
        public int compareTo(Object r) {
            if(rank < ((tupleResult) r).getRank()) {
                return -1;
            } else if(rank > ((tupleResult) r).getRank()){
                return 1;
            } else if (docno.compareTo(((tupleResult) r).getDocNo()) == 1){
                return -1;
            }else{
                return 1;
            }
        }

    }
    //////////////////////function in results class//////////////
    public String generateKey(String queryID, String docno )
    {
        return queryID + "-" + docno ;
    }

    public Results() {
        this.queryToResults = new HashMap<>();
        this.queryToResultIsSorted = new HashMap<>();
    }

    public void addResult(String queryID, String docno, float score, int rank){
        ArrayList results;
        if (!queryToResults.containsKey(queryID)){
            results = new ArrayList();
            queryToResults.put(queryID, results);
            queryToResultIsSorted.put(queryID, false);
        }
        else {
            results = queryToResults.get(queryID);
        }
        tupleResult result = new tupleResult(docno, score, rank);
        results.add(result);
    }

    public ArrayList<tupleResult> QueryResults(String queryID) {
        if(!this.queryToResults.containsKey(queryID)){
            System.out.println("no such query in results");
            return null;
        }
        ArrayList<tupleResult> results = this.queryToResults.get(queryID);
        if(!queryToResultIsSorted.get(queryID)){
            Collections.sort(results);
            queryToResultIsSorted.put(queryID, true);
        }
        return results;
    }

    public Set<String> getQueryIDs(){
        return this.queryToResults.keySet();
    }

    public boolean QueryIDExists(String queryID) {
        return this.queryToResults.containsKey(queryID);
    }
}
