package com.stormrage.mydmm.server.task.status;


public enum TaskStatus {
	
	UN_FINISH("运行", 2),
	FINISH("完成", 3);
	
    private String name;
    private int index;

    private TaskStatus(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String toString() {
        return this.index + "_" + this.name;
    }
    
    public int getIndex() {
		return index;
	}
    
    public String getName() {
		return name;
	}
   
}
