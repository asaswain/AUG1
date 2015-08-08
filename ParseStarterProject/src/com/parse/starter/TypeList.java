package com.parse.starter;

// simple uility class to store lists of expense types and amounts
public class TypeList {

    static final String typeList[] = {"Food   ", "Fun    ", "Housing", "Medical ", "Transit"};
    static final int typeCnt = 5;

    float typeAmts[];

    public TypeList() {
        typeAmts = new float[getTypeCnt()];
        for (int i = 0; i < TypeList.getTypeCnt(); i++) {
            typeAmts[i] = 0f;
        }
    }

    /**
     * Add amount to typeAmts array
     * @param i - index to add to
     * @param amt - amount to add
     */
    public void addAmt(int i, float amt) {
        typeAmts[i] += amt;
    }

    /**
     * Get list of type amounts
     * @return - float array of amounts for each type
     */
    public float[] getTypeAmts() {
        float[] newAmts = typeAmts;
        return newAmts;
    }

    public static String[] getTypeList() {
        return typeList;
    }

    public static int getTypeCnt() {
        return typeCnt;
    }
}
