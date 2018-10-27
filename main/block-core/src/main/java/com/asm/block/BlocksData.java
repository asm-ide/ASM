package com.asm.block;

public class BlocksData {
    public static int lastId = 0;

    public static void init(){
        lastId = 0;

    }

    public static int getNewId(){
        lastId ++;
        return lastId;
    }
}
