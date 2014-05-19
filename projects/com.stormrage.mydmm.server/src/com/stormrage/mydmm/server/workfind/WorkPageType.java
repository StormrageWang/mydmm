package com.stormrage.mydmm.server.workfind;

public enum WorkPageType {

	ANIMATION("動画", 1), 
	MAIL_ORDER("通販", 2),
    NUKNOWN("未定", 0);
    private String name;
    private int index;

    private WorkPageType(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String toString() {
        return this.index + "_" + this.name;
    }
    
    public int getIndex() {
		return index;
	}
    
    public static WorkPageType valueof(int index){
    	if(index == ANIMATION.index){
    		return ANIMATION;
    	}
    	if(index == MAIL_ORDER.index){
    		return MAIL_ORDER;
    	}
    	return NUKNOWN;
    }
    
}
