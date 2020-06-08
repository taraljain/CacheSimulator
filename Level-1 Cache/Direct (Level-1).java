import java.util.*;
public class Direct {
    static class DirectMapCache {
        int cacheLine;
        int tag;
        int VB;
        int DB;
        int [] data;

        public DirectMapCache(){
            data= new int [10];
        }

        public DirectMapCache(int a,int b, int c, int d, int[] e) {
            this.VB = c;
            this.DB = d;
            this.cacheLine = a;
            this.tag = b;
            this.data = Arrays.copyOf(e, e.length);;
        }

        public void setData(int[] cData) {
            data = Arrays.copyOf(cData, cData.length);
        }
        
        public void DBitSetter(int cDirtyBit) {
            DB = cDirtyBit;
        }

        public void displayCach(ArrayList<DirectMapCache> Arr, int cacheLines, int blockSize){
            System.out.println("Slot"+"    "+"tag"+"    "+"VBit"+"    "+"DBit"+"       "+"Data");

            for(int i=0; i<cacheLines; i++){
                System.out.print(Integer.toBinaryString(Arr.get(i).cacheLine)+"\t"+"\t"+ Integer.toBinaryString(Arr.get(i).tag)+"\t"+"\t"+
                        Arr.get(i).VB+"\t"+"\t"+
                        Arr.get(i).DB+"\t"+"\t");
                for(int x=0; x<blockSize; x++){
                    System.out.print((Arr.get(i).data[x])+" ");
                }
                System.out.println();
            }
        }
    }
    public static void main(String[] args){
        Scanner input = new Scanner(System.in);
        DirectMapCache question = new DirectMapCache();

        System.out.println("Enter Block Size (W : EXPONENT OF 2)");
        int bitsForBlock=input.nextInt();
        int sizeOfBlock= (int) Math.pow(2,bitsForBlock);
        ArrayList<DirectMapCache> arrayCache = new ArrayList(sizeOfBlock);
        int[] cData = new int[sizeOfBlock];
        int[] tempArray = new int[sizeOfBlock];

        System.out.println("Enter Number Of Cache Lines (C.L. : EXPONENT OF 2)");
        int bitsForCacheLines=input.nextInt();
        int cacheLines=(int)Math.pow(2,bitsForCacheLines);
        for (int i = 0; i < cacheLines; i++) {
            arrayCache.add(new DirectMapCache(i, 0, 0, 0, cData));
        }

        System.out.println("Enter Memory Size (N : EXPONENT OF 2)");
        int bitsTotal=input.nextInt();
        int memorySize=(int)Math.pow(2,bitsTotal);
        int[] mainMemory = new int[memorySize];
        for (int i = 0; i < memorySize; i++) {
            mainMemory[i] = 1;
        }
        while(true) {
            System.out.println("\n(R)ead, (W)rite, or (D)isplay Cache?");
            String operation = input.next();

            if (operation.equals("D")) {
                question.displayCach(arrayCache,cacheLines,sizeOfBlock);
                continue;
            }

            if (operation.equals("R") || operation.equals("W")) {
                System.out.println("Enter The Address To READ/WRITE");
                String addressModified=input.next();
                int address=Integer.parseInt(addressModified,2);
                int Block_offset = Integer.parseInt(addressModified.substring(bitsTotal-bitsForBlock),2);
                String Block_beging_addr_temp=addressModified.substring(0,bitsTotal-bitsForBlock);
                for(int i=bitsTotal-bitsForBlock;i<addressModified.length();i++){
                    Block_beging_addr_temp+="0";
                }
                int memoryReferenceMainMemory=Integer.parseInt(Block_beging_addr_temp,2);
                int tag = address >>> (bitsTotal-bitsForBlock);
                int cacheLine = Integer.parseInt(addressModified.substring(bitsTotal-bitsForBlock-bitsForCacheLines,bitsTotal-bitsForBlock),2);
                int counterHit = 0;
                if (operation.equals("R")) {
                    boolean checkPost1=arrayCache.get(cacheLine).tag == tag && arrayCache.get(cacheLine).VB == 1;
                    if (checkPost1) {
                        counterHit = 1;
                    }
                    boolean checkPost2=arrayCache.get(cacheLine).tag != tag || arrayCache.get(cacheLine).VB != 1;
                    if (checkPost2) {
                        counterHit = 0;
                        if (arrayCache.get(cacheLine).DB == 0) {
                            for (int i = 0; i < sizeOfBlock; i++) {
                                tempArray[i] = mainMemory[memoryReferenceMainMemory + i];
                            }

                            arrayCache.set(cacheLine, new DirectMapCache(cacheLine, tag, 1, 0, tempArray));
                        } else {
                            // copy current block in cache to main memory
                            int Beging_Addr_Cach = (arrayCache.get(cacheLine).tag << 8) + (cacheLine << 4);
                            for (int i = 0; i < sizeOfBlock; i++) {
                                mainMemory[Beging_Addr_Cach + i] = arrayCache.get(cacheLine).data[i];
                            }
                            // get the new block from memory and save it to the cache
                            for (int i = 0; i < sizeOfBlock; i++) {
                                tempArray[i] = mainMemory[memoryReferenceMainMemory + i];
                            }
                            arrayCache.set(cacheLine, new DirectMapCache(cacheLine, tag, 1, 0, tempArray));

                        }
                    }
                    System.out.println("At the Address "+addressModified+" Data is "
                            + (arrayCache.get(cacheLine).data[Block_offset])
                            + " (Cache " + ((counterHit == 1) ? "Hit)" : "miss)")); // if cache counterHit then print "CACHE HIT" else "CACHE MISS"
                }
                if (operation.equals("W")) {
                    System.out.println("Enter The Value To Write At Address "+addressModified);
                    int value = input.nextInt();

                    if (arrayCache.get(cacheLine).tag == tag && arrayCache.get(cacheLine).VB == 1) {
                        counterHit = 1;
                        arrayCache.get(cacheLine).DBitSetter(1);
                        arrayCache.get(cacheLine).data[Block_offset] = value;
                    }
                    System.out.println("The value " + (value)
                            + " was written to the address " + addressModified + " (Cache "
                            + ((counterHit == 1) ? "Hit)" : "miss)"));
                    boolean checkPost3=arrayCache.get(cacheLine).tag != tag || arrayCache.get(cacheLine).VB != 1;
                    if (checkPost3) {
                        counterHit = 0;

                        if (arrayCache.get(cacheLine).DB == 0) {
                            for (int i = 0; i < sizeOfBlock; i++) {
                                tempArray[i] = mainMemory[memoryReferenceMainMemory + i];
                            }

                            arrayCache.set(cacheLine, new DirectMapCache(cacheLine, tag, 1, 1, tempArray));
                            arrayCache.get(cacheLine).data[Block_offset] = value;
                        } else {
                            // Copy the block to main memory
                            int Beging_Addr_Cach = (arrayCache.get(cacheLine).tag << 8) + (cacheLine << 4);
                            for (int i = 0; i < sizeOfBlock; i++) {
                                mainMemory[Beging_Addr_Cach + i] = arrayCache.get(cacheLine).data[i];
                            }
                            // Fetch from memory and load it on Cache
                            for (int i = 0; i < sizeOfBlock; i++) {
                                tempArray[i] = mainMemory[memoryReferenceMainMemory + i];
                            }
                            arrayCache.set(cacheLine, new DirectMapCache(cacheLine, tag, 1, 0, tempArray));
                        }
                    }
                }
            }
        }
    }
}