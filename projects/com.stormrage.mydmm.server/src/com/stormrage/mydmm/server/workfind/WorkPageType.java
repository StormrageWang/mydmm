package com.stormrage.mydmm.server.workfind;

public enum WorkPageType {

	ANIMATION("動画", 1), 
	MAIL_ORDER("通販", 2);
    
    private String name;
    private int index;

    private WorkPageType(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String toString() {
        return this.index + "_" + this.name;
    }
}
