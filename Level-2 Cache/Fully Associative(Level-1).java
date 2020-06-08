package CO_Bonus;
import java.util.*;

public class FullAssociative_L1 {
    static class FullAssociativeCache_L1{
        String address;
        int set;
        int tag_L1;
        int word_L1;
        int data;
        long currentTimeMillis;

        public FullAssociativeCache_L1(String add,int k, int t,int w, int data) {
            this.address=add;
            this.set = k;
            this.tag_L1= t;
            this.word_L1=w;
            this.data = data;
            this.currentTimeMillis = System.currentTimeMillis();
        }

        public void setAddress(String add){
            this.address=add;
        }
        public int getWord(){
            return this.word_L1;
        }
        public String getAddress(){
            return this.address;
        }

        public int getTag(){
            return this.tag_L1;
        }

        public int getValue() {
            return this.data;
        }

        public long getLastUseTime() {
            return this.currentTimeMillis;
        }
        public void setTag(int tag1){
            this.tag_L1=tag1;
        }
        public void setSet(int set1){
            this.set=set1;
        }

        public void setValue(int data) {
            this.data = data;
            this.currentTimeMillis = System.currentTimeMillis();
        }

        public void setWord(int word_L1){
            this.word_L1=word_L1;
        }
    }

    int numSets;
    int sizeOfSet;
    int sizeOfBlock;
    ReplacementPolicy replacementPolicy;
    enum ReplacementPolicy { LRU }

    private FullAssociativeCache_L1[][] cacheEntries;

    public void initialise(){
        for(int i=0;i<this.numSets;i++) {
            FullAssociativeCache_L1 cacheEntry = new FullAssociativeCache_L1("0",i, -1, -1, 0);
            for (int j = 0; j < this.sizeOfSet; j++) {
                if (cacheEntries[i][j] == null) { // To search for an empty space to fill in the data
                    cacheEntries[i][j]=cacheEntry;
                }
            }
        }
    }

    public FullAssociative_L1(int sizeB,int numSets, int sizeOfSet, ReplacementPolicy replacementPolicy) {
        this.sizeOfBlock=sizeB;
        this.numSets = numSets;
        this.sizeOfSet = sizeOfSet;
        this.cacheEntries = new FullAssociativeCache_L1[numSets][sizeOfSet];
        this.replacementPolicy = replacementPolicy;
    }

    public int get(int tag_L1,int setNum,int word_L1) {
        int set=setNum%numSets;
        for(FullAssociativeCache_L1 entry : cacheEntries[set]) {
            if(entry != null) {
                if(entry.getTag() == tag_L1 && entry.getWord()==word_L1) {
                    //System.out.println("CACHE HIT IN L1");
                    return entry.getValue();
                }
            }
        }
        return -1;
    }

    public void put(String address,int setNum, int tag_L1, int word_L1,int data,FullAssociative_L2 cache_L2,int set_L2_Bits, int tag_L2_Bits,int word_L2_Bits) {
        // IF THERE IS ALREADY AN ENTRY IN CACHE
        int set=setNum%numSets;
        for (FullAssociativeCache_L1 entry : cacheEntries[set]) {
            if(entry!=null){
                if (entry.getTag() == tag_L1 && entry.getWord() == word_L1) {
                    entry.setValue(data);
                    return;
                }
                else if(entry.getTag()==tag_L1 && entry.getWord()!=word_L1 && entry.getValue()==1){
                    entry.setValue(data);
                    entry.setWord(word_L1);
                    entry.setAddress(address);
                    return;
                }
            }
        }

        // IF THERE IS NOT ALREADY AN ENTRY IN CACHE
        FullAssociativeCache_L1 cacheEntry = new FullAssociativeCache_L1(address,set, tag_L1, word_L1, data);

        for (int i = 0; i < this.sizeOfSet; i++) {
            if (cacheEntries[set][i].getValue() == 0) { // To search for an empty space to fill in the data
                cacheEntries[set][i] = cacheEntry;
                for(int j=i+1;j<i+sizeOfBlock;j++){
                    cacheEntries[set][j]= new FullAssociativeCache_L1("0",set, tag_L1, -1, 1);
                }
                return;
            }
        }
        isCacheL1Full(set);
        if(isCacheL1Full(set)){
            int tag_temp_L1=LRU(set);
            for(FullAssociativeCache_L1 entry : cacheEntries[set]){
                if(entry.getTag()==tag_temp_L1 && !entry.getAddress().equals("0")) {
                    int word_L2 = Integer.parseInt(entry.getAddress().substring(entry.getAddress().length() - word_L2_Bits), 2);
                    int tag_L2 = Integer.parseInt(entry.getAddress().substring(0,entry.getAddress().length() - word_L2_Bits), 2);
                    cache_L2.put(0, tag_L2, word_L2, entry.getValue());
                }
                if(entry.getTag()==tag_temp_L1){
                    entry.setValue(0); entry.setWord(-1); entry.setTag(-1); entry.setSet(-1);
                }
            }
            FullAssociativeCache_L1 cacheEntry1 = new FullAssociativeCache_L1(address,set, tag_L1, word_L1, data);

            for (int i = 0; i < this.sizeOfSet; i++) {
                if (cacheEntries[set][i].getValue() == 0) { // To search for an empty space to fill in the data
                    cacheEntries[set][i] = cacheEntry1;
                    for(int j=i+1;j<i+sizeOfBlock;j++){
                        cacheEntries[set][j]= new FullAssociativeCache_L1("0",set, tag_L1, -1, 1);
                    }
                    return;
                }
            }
        }
    }

    public boolean isCacheL1Full(int set){
        int sizeCounter = 0;

        for(int i=0;i<this.sizeOfSet;i++) {
            if (cacheEntries[set][i].getValue() != 0) {
                sizeCounter++;
            }
        }
        return(sizeCounter==this.sizeOfSet);
    }

    public int LRU(int set){
        FullAssociative_L1.FullAssociativeCache_L1 minimumCacheEntry;
        FullAssociative_L1.FullAssociativeCache_L1 LatestCacheEntry=cacheEntries[set][0];

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
        System.out.println("Tag of Block for replacement using LRU is "+Integer.toBinaryString(tag)+" in SET "+set+" in Cache L1");
        return tag;
    }

    public void displayCache(int k){
        for(int i=0;i<numSets;i++){
            int lineChanger=0; int counter=0;
            for(FullAssociativeCache_L1 entry : cacheEntries[i]) {
                if(lineChanger==sizeOfSet/k) {
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

    public static void main(String args[]) {
        Scanner input=new Scanner(System.in);
        System.out.println("Enter the value of N (MEMORY SIZE: EXPONENT OF 2)");
        int N=input.nextInt();

        System.out.println("Enter the value of Cache Lines (EXPONENT OF 2)");
        int cacheLinesBits=input.nextInt();
        int cacheLines=(int)Math.pow(2,cacheLinesBits);

        System.out.println("Enter the value of W (EXPONENT OF 2)");

        int W=input.nextInt();

        int numberofSets=1;
        FullAssociative_L1 cache_L1 = new FullAssociative_L1((int)Math.pow(2,W),numberofSets,cacheLines*(int)Math.pow(2,W), ReplacementPolicy.LRU);

        cache_L1.initialise();

        FullAssociative_L2 cache_L2 = new FullAssociative_L2((int)Math.pow(2,W),2*numberofSets,cacheLines*(int)Math.pow(2,W), FullAssociative_L2.ReplacementPolicy.LRU);
        cache_L2.initialise();


        while(true){
            System.out.println("Enter the Operation (R)ead or (W)rite or (D)isplay");
            String operation=input.next();

            if(operation.equals("D")){
                System.out.println("LEVEL-1 CACHE MEMORY");
                cache_L1.displayCache(cacheLines);
                System.out.println();
                System.out.println("LEVEL-2 CACHE MEMORY");
                cache_L2.displayCache_L2(cacheLines);
                continue;
            }

            System.out.println("Enter the Address To Read/Write");
            String address=input.next();

            int word_L1=Integer.parseInt(address.substring(N-W),2);
            int tag_L1=Integer.parseInt(address.substring(0,N-W),2);

            int word_L2=Integer.parseInt(address.substring(N-W),2);
            int tag_L2=Integer.parseInt(address.substring(0,N-W),2);

            int set_L2_Bits=0; int tag_L2_Bits=N-W; int word_L2_Bits=W;

            if(operation.equals("R")){
                int cacheL1_getter=cache_L1.get(tag_L1,0,word_L1);
                if(cacheL1_getter==-1){
                    int cacheL2_getter=cache_L2.get(tag_L2,0,word_L2);
                    int cacheL2_getter_2=cache_L2.get(tag_L2,1,word_L2);
                    if(cacheL2_getter==-1 && cacheL2_getter_2==-1){
                        System.out.println("CACHE MISS");
                    }
                    else if(cacheL2_getter==-1){
                        System.out.println("CACHE HIT");
                        System.out.println("DATA at "+address+" is "+cacheL2_getter_2+" and is found in Cache_L2");
                    }
                    else{
                        System.out.println("CACHE HIT");
                        System.out.println("DATA at "+address+" is "+cacheL2_getter+" and is found in Cache_L2");
                    }
                }
                else{
                    System.out.println("CACHE HIT");
                    System.out.println("DATA at "+address+" is "+cacheL1_getter+" and is found in Cache_L1");
                }
                System.out.println();
            }

            else if(operation.equals("W")){
                System.out.println("Enter The Data To Write At "+address );
                int toAdd=input.nextInt();
                cache_L1.put(address,0, tag_L1,word_L1,toAdd,cache_L2,set_L2_Bits,tag_L2_Bits,word_L2_Bits);
            }
        }
    }
}