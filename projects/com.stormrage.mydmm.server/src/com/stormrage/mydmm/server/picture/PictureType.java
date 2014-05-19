package com.stormrage.mydmm.server.picture;

public enum PictureType {
	
	SMALL("小图", 1),
	GENERAL("一般", 2),
	BIG("大图", 3);
    private String name;
    private int index;

    private PictureType(String name, int index) {
        this.name = name;
        this.index = index;
    }
    
    public int getIndex() {
		return index;
	}

    public String toString() {
        return this.index + "_" + this.name;
    }
    
    public static PictureType valueof(int index){
    	if(index == SMALL.index){
    		return SMALL;
    	}
    	if(index == BIG.index){
    		return BIG;
    	}
    	return GENERAL;
    }
}
