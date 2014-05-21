package com.stormrage.mydmm.server.work;

public enum WorkPictureType {
	
	COVER_ORDINARY("普通封面", 1),
	COVER_HD("高清封面", 2),
	PREVIEW_ORDINARY("普通预览图", 3),
	PREVIEW_HD("高清预览图", 4);
	
    private String name;
    private int index;

    private WorkPictureType(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String toString() {
        return this.index + "_" + this.name;
    }
    
    public int getIndex() {
		return index;
	}
    
    public static WorkPictureType valueof(int index){
    	if(index == COVER_ORDINARY.index){
    		return COVER_ORDINARY;
    	}
    	if(index == COVER_HD.index){
    		return COVER_HD;
    	}
    	if(index == PREVIEW_ORDINARY.index){
    		return PREVIEW_ORDINARY;
    	}
    	if(index == PREVIEW_HD.index){
    		return PREVIEW_HD;
    	}
    	return null;
    }
}
