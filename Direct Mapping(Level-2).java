package CO_Bonus;

public class Direct_L2 {
    static class DirectCache_L2{
        int cacheLine;
        int tag;
        int word;
        int data;
        long currentTimeMillis;

        public DirectCache_L2(int k, int t,int w, int data) {
            this.cacheLine = k;
            this.tag= t;
            this.word=w;
            this.data = data;
            this.currentTimeMillis = System.currentTimeMillis();
        }

        public int getWord(){
            return this.word;
        }

        public int getTag(){
            return this.tag;
        }

        public int getValue() {
            return this.data;
        }

        public long getLastUseTime() {
            return this.currentTimeMillis;
        }

        public void setValue(int data) {
            this.data = data;
            this.currentTimeMillis = System.currentTimeMillis();
        }

        public void setWord(int word){
            this.word=word;
        }
    }

    int numSets;
    int sizeOfSet;
    int sizeOfBlock;
    ReplacementPolicy replacementPolicy;
    enum ReplacementPolicy { LRU }

    private DirectCache_L2[][] cacheEntries;

    public void initialise(){
        for(int i=0;i<this.numSets;i++) {
            DirectCache_L2 cacheEntry = new DirectCache_L2(i, -1, -1, 0);
            for (int j = 0; j < this.sizeOfSet; j++) {
                if (cacheEntries[i][j] == null) { // To search for an empty space to fill in the data
                    cacheEntries[i][j]=cacheEntry;
                }
            }
        }
    }

    public Direct_L2(int sizeB,int numSets, int sizeOfSet, ReplacementPolicy replacementPolicy) {
        this.sizeOfBlock=sizeB;
        this.numSets = numSets;
        this.sizeOfSet = sizeOfSet;
        this.cacheEntries = new DirectCache_L2[numSets][sizeOfSet];
        this.replacementPolicy = replacementPolicy;
    }

    public int get(int tag,int cacheLineNum,int word) {
        int cacheLine=cacheLineNum%numSets;
        for(DirectCache_L2 entry : cacheEntries[cacheLine]) {
            if(entry != null) {
                if (entry.getTag() == tag && entry.getWord() == word) {
                    return entry.getValue();
                }
            }
        }
        return -1;
    }

    public void put(int cacheLineNum, int tag, int word,int data) {
        int cacheLine=cacheLineNum%numSets;
        // IF THERE IS ALREADY AN ENTRY IN CACHE
        for (DirectCache_L2 entry : cacheEntries[cacheLine]) {
            if(entry!=null){
                if (entry.getTag() == tag && entry.getWord() == word) {
                    entry.setValue(data);
                    return;
                }
                else if(entry.getTag()==tag && entry.getWord()!=word && entry.getValue()==1){
                    entry.setValue(data);
                    entry.setWord(word);
                    return;
                }
            }
        }

        // IF THERE IS NOT ALREADY AN ENTRY IN CACHE
        DirectCache_L2 cacheEntry = new DirectCache_L2(cacheLine, tag, word, data);

        for (int i = 0; i < this.sizeOfSet; i++) {
            if (cacheEntries[cacheLine][i].getValue() == 0) { // To search for an empty space to fill in the data
                cacheEntries[cacheLine][i] = cacheEntry;
                for(int j=i+1;j<i+sizeOfBlock;j++){
                    cacheEntries[cacheLine][j]= new DirectCache_L2(cacheLine, tag, -1, 1);
                }
                return;
            }
        }
        LRU(cacheLine);
    }

    public void LRU(int cacheLine){
        int sizeCounter = 0;

        for(int i=0;i<this.sizeOfSet;i++){
            if(cacheEntries[cacheLine][i].getValue()!=0){
                sizeCounter++;
            }
        }

        if (sizeCounter==this.sizeOfSet && this.replacementPolicy == ReplacementPolicy.LRU) { // If all space in assigned cacheLine is FULL, so Replace using LRU
            DirectCache_L2 minimumCacheEntry;
            DirectCache_L2 LatestCacheEntry=cacheEntries[cacheLine][0];

            for(int j=1;j<sizeOfBlock;j++) {
                if (cacheEntries[cacheLine][j].getLastUseTime() > LatestCacheEntry.getLastUseTime()) {
                    LatestCacheEntry = cacheEntries[cacheLine][j];
                }
            }

            minimumCacheEntry=LatestCacheEntry;

            int tag = cacheEntries[cacheLine][0].getTag();

            for(int i=sizeOfBlock; i<sizeOfSet;i=i+sizeOfBlock){
                LatestCacheEntry=cacheEntries[cacheLine][i];
                for(int j=i+1;j<sizeOfBlock;j++) {
                    if (cacheEntries[cacheLine][j].getLastUseTime() > LatestCacheEntry.getLastUseTime()) {
                        LatestCacheEntry = cacheEntries[cacheLine][j];
                    }
                }
                if(minimumCacheEntry.getLastUseTime()>LatestCacheEntry.getLastUseTime()){
                    minimumCacheEntry=LatestCacheEntry;
                    tag = cacheEntries[cacheLine][i].getTag();
                }

            }
            System.out.println("Tag of Block for replacement using LRU is "+Integer.toBinaryString(tag)+" in Cache Line "+cacheLine+" in Cache L2");
        }
    }

    public void displayCache_L2(){
        for(int i=0;i<numSets;i++){
            int lineChanger=0; int counter=0;
            for(DirectCache_L2 entry : cacheEntries[i]) {
                if(lineChanger==sizeOfSet) {
                    lineChanger=0;
                    System.out.print("TAG = "+cacheEntries[i][counter-1].getTag());
                    System.out.println();
                }
                System.out.print(entry.getValue() + " ");
                lineChanger++; counter++;
            }
            System.out.print("TAG = "+cacheEntries[i][sizeOfSet-1].getTag());
            System.out.println();
        }
    }
}