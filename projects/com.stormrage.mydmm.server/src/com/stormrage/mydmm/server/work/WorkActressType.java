package com.stormrage.mydmm.server.work;

public enum WorkActressType {

	SINGLE("单体", 1),
	SERVERAL("群演", 2),
	COLLECTION("合集", 3);
	
    private String name;
    private int index;

    private WorkActressType(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String toString() {
        return this.index + "_" + this.name;
    }
    
    public int getIndex() {
		return index;
	}
    
    public static WorkActressType valueof(int index){
    	if(index == SINGLE.index){
    		return SINGLE;
    	}
    	if(index == SERVERAL.index){
    		return SERVERAL;
    	}
    	if(index == COLLECTION.index){
    		return COLLECTION;
    	}
    	return null;
    }
}
