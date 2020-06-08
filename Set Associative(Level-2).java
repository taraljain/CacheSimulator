package CO_Bonus;

public class SetAssociative_L2 {
    static class SetAssociativeCache_L2{
        int set;
        int tag;
        int word;
        int data;
        long currentTimeMillis;

        public SetAssociativeCache_L2(int k, int t,int w, int data) {
            this.set = k;
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

    private SetAssociativeCache_L2[][] cacheEntries;

    public void initialise(){
        for(int i=0;i<this.numSets;i++) {
            SetAssociativeCache_L2 cacheEntry = new SetAssociativeCache_L2(i, -1, -1, 0);
            for (int j = 0; j < this.sizeOfSet; j++) {
                if (cacheEntries[i][j] == null) { // To search for an empty space to fill in the data
                    cacheEntries[i][j]=cacheEntry;
                }
            }
        }
    }

    public SetAssociative_L2(int sizeB,int numSets, int sizeOfSet, ReplacementPolicy replacementPolicy) {
        this.sizeOfBlock=sizeB;
        this.numSets = numSets;
        this.sizeOfSet = sizeOfSet;
        this.cacheEntries = new SetAssociativeCache_L2[numSets][sizeOfSet];
        this.replacementPolicy = replacementPolicy;
    }

    public int get(int tag,int setNum,int word) {
        int set=setNum%numSets;
        for(SetAssociativeCache_L2 entry : cacheEntries[set]) {
            if(entry != null) {
                if (entry.getTag() == tag && entry.getWord() == word) {
                    return entry.getValue();
                }
            }
        }
        return -1;
    }

    public void put(int setNum, int tag, int word,int data) {
        int set=setNum%numSets;
        // IF THERE IS ALREADY AN ENTRY IN CACHE
        for (SetAssociativeCache_L2 entry : cacheEntries[set]) {
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
        SetAssociativeCache_L2 cacheEntry = new SetAssociativeCache_L2(set, tag, word, data);

        for (int i = 0; i < this.sizeOfSet; i++) {
            if (cacheEntries[set][i].getValue() == 0) { // To search for an empty space to fill in the data
                cacheEntries[set][i] = cacheEntry;
                for(int j=i+1;j<i+sizeOfBlock;j++){
                    cacheEntries[set][j]= new SetAssociativeCache_L2(set, tag, -1, 1);
                }
                return;
            }
        }
        LRU(set);
    }

    public void LRU(int set){
        int sizeCounter = 0;

        for(int i=0;i<this.sizeOfSet;i++){
            if(cacheEntries[set][i].getValue()!=0){
                sizeCounter++;
            }
        }

        if (sizeCounter==this.sizeOfSet && this.replacementPolicy == ReplacementPolicy.LRU) { // If all space in assigned set is FULL, so Replace using LRU
            SetAssociativeCache_L2 minimumCacheEntry;
            SetAssociativeCache_L2 LatestCacheEntry=cacheEntries[set][0];

            for(int j=1;j<sizeOfBlock;j++) {
                if (cacheEntries[set][j].getLastUseTime() > LatestCacheEntry.getLastUseTime()) {
                    LatestCacheEntry = cacheEntries[set][j];
                }
            }

            minimumCacheEntry=LatestCacheEntry;

            int tag = cacheEntries[set][0].getTag();

            for(int i=sizeOfBlock; i<sizeOfSet;i=i+sizeOfBlock){
                LatestCacheEntry=cacheEntries[set][i];
                for(int j=i+1;j<sizeOfBlock;j++) {
                    if (cacheEntries[set][j].getLastUseTime() > LatestCacheEntry.getLastUseTime()) {
                        LatestCacheEntry = cacheEntries[set][j];
                    }
                }
                if(minimumCacheEntry.getLastUseTime()>LatestCacheEntry.getLastUseTime()){
                    minimumCacheEntry=LatestCacheEntry;
                    tag = cacheEntries[set][i].getTag();
                }

            }
            System.out.println("Tag of Block for replacement using LRU is "+Integer.toBinaryString(tag)+" in SET "+set+" in Cache L2");
        }
    }

    public void displayCache_L2(int k){
        for(int i=0;i<numSets;i++){
            int lineChanger=0; int counter=0;
            for(SetAssociativeCache_L2 entry : cacheEntries[i]) {
                if(lineChanger==sizeOfSet/k) {
                    lineChanger=0;
                    System.out.print(" - SET = "+i+", TAG = "+cacheEntries[i][counter-1].getTag());
                    System.out.println();
                }
                System.out.print(entry.getValue() + " ");
                lineChanger++; counter++;
            }
            System.out.print(" - SET = "+i+", TAG = "+cacheEntries[i][sizeOfSet-1].getTag());
            System.out.println();
        }
    }
}