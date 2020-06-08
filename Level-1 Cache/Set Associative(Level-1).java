import java.util.*;

public class SetAssociative {
    static class SetAssociativeCache{
        int set;
        int tag;
        int word;
        int data;
        long currentTimeMillis;

        public SetAssociativeCache(int k, int t,int w, int data) {
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

    private SetAssociativeCache[][] cacheEntries;

    public void initialise(){
        for(int i=0;i<this.numSets;i++) {
            SetAssociativeCache cacheEntry = new SetAssociativeCache(i, -1, -1, 0);
            for (int j = 0; j < this.sizeOfSet; j++) {
                if (cacheEntries[i][j] == null) { // To search for an empty space to fill in the data
                    cacheEntries[i][j]=cacheEntry;
                }
            }
        }
    }

    public SetAssociative(int sizeB,int numSets, int sizeOfSet, ReplacementPolicy replacementPolicy) {
        this.sizeOfBlock=sizeB;
        this.numSets = numSets;
        this.sizeOfSet = sizeOfSet;
        this.cacheEntries = new SetAssociativeCache[numSets][sizeOfSet];
        this.replacementPolicy = replacementPolicy;
    }

    public int get(int tag_L1,int setNum,int word_L1) {
        int set=setNum%numSets;
        for(SetAssociativeCache entry : cacheEntries[set]) {
            if(entry != null) {
                if(entry.getTag() == tag_L1 && entry.getWord()==word_L1) {
                    System.out.println("CACHE HIT");
                    return entry.getValue();
                }
            }
        }
		System.out.println("CACHE MISS");
        return -1;
    }

    public void put(int setNum, int tag, int word,int data) {
        // IF THERE IS ALREADY AN ENTRY IN CACHE
		int set=setNum%numSets;
        for (SetAssociativeCache entry : cacheEntries[set]) {
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
        SetAssociativeCache cacheEntry = new SetAssociativeCache(set, tag, word, data);

        for (int i = 0; i < this.sizeOfSet; i++) {
            if (cacheEntries[set][i].getValue() == 0) { // To search for an empty space to fill in the data
                cacheEntries[set][i] = cacheEntry;
                for(int j=i+1;j<i+sizeOfBlock;j++){
                    cacheEntries[set][j]= new SetAssociativeCache(set, tag, -1, 1);
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
            SetAssociativeCache minimumCacheEntry;
            SetAssociativeCache LatestCacheEntry=cacheEntries[set][0];

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
            System.out.println("Tag of Block for replacement using LRU is "+Integer.toBinaryString(tag)+" in SET "+set);
        }
    }

    public void displayCache(int k){
        for(int i=0;i<numSets;i++){
            int lineChanger=0;
            for(SetAssociativeCache entry : cacheEntries[i]) {
                if(lineChanger==sizeOfSet/k) {
                    lineChanger=0;
                    System.out.println();
                }
                System.out.print(entry.getValue() + " ");
                lineChanger++;
            }
            System.out.println();
        }
    }

    public String toString() {
        return Arrays.deepToString(cacheEntries);
    }

    public static void main(String args[]) {
        Scanner input=new Scanner(System.in);
        System.out.println("Enter the value of N (MEMORY SIZE: EXPONENT OF 2)");
        int N=input.nextInt();

        System.out.println("Enter the value of Cache Lines (EXPONENT OF 2)");
		int cacheLineBits=input.nextInt();
        int cacheLines=(int)Math.pow(2,cacheLineBits);

        System.out.println("Enter the value of K");
        int k=input.nextInt();

        System.out.println("Enter the value of W (EXPONENT OF 2)");

        int W=input.nextInt();

        int numberofSets=cacheLines/k;

        int bitsForSets=(int)Math.floor(Math.log(numberofSets)/Math.log(2));

        if(bitsForSets==0){bitsForSets=1;}

        SetAssociative cache = new SetAssociative((int)Math.pow(2,W),numberofSets,k*(int)Math.pow(2,W),ReplacementPolicy.LRU);

        cache.initialise();

        while(true){
            System.out.println("Enter the Operation (R)ead or (W)rite or (D)isplay");
            String operation=input.next();

            if(operation.equals("D")){
                cache.displayCache(k);
                continue;
            }

            System.out.println("Enter the Address To Read/Write");
            String address=input.next();

            int setNumber=Integer.parseInt(address.substring(N-W-bitsForSets,N-W),2);
            int word=Integer.parseInt(address.substring(N-W),2);
            int tag=Integer.parseInt(address.substring(0,N-W-bitsForSets),2);

            if(operation.equals("R")){
                System.out.println(cache.get(tag,setNumber,word));
            }

            else if(operation.equals("W")){
                System.out.println("Enter The Data To Write At "+address );
                int toAdd=input.nextInt();
                cache.put(setNumber, tag,word,toAdd);
            }
        }
    }
}