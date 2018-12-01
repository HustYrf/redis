package com.learn.redis.BloomFliter;

import java.util.BitSet;


/**
 * @ClassName BloomFilter
 * @Descripition TODO
 * @Author Administrator
 * @Date 2018/12/1 16:08
 **/
public class BloomFilter {
    private static final int DEFAULT_SIZE = 1 << 25;

    private static final int[] seeds= new int[]{5,7,11,13,31,37,61};

    private BitSet bits = new BitSet(DEFAULT_SIZE);

    private SimpleHash[] func = new SimpleHash[seeds.length];

    public BloomFilter() {
        for (int i = 0; i < seeds.length; i++) {
            func[i]=new SimpleHash(DEFAULT_SIZE,seeds[i]);
        }
    }

    public boolean add(String value){
        if(value==null){
            return false;
        }
        for(SimpleHash hashFun:func){
            bits.set(hashFun.hash(value),true);
        }
        return true;
    }

    public boolean contain(String value){
        if(value == null){
            return false;
        }
        for(SimpleHash hashFun:func){
            if(!bits.get(hashFun.hash(value))){
                return false;
            }
        }
        return true;
    }

    /* 哈希函数类 */
    public static class SimpleHash {
        private int cap;
        private int seed;

        public SimpleHash(int cap, int seed) {
            this.cap = cap;
            this.seed = seed;
        }

        // hash函数，采用简单的加权和hash
        public int hash(String value) {
            int result = 0;
            int len = value.length();
            for (int i = 0; i < len; i++) {
                result = seed * result + value.charAt(i);
            }
            return (cap - 1) & result;
        }
    }


}
