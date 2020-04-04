package com.example.savetheparachutists;

public class PerformanceEvent extends Controller_Display_Communication_Event {

    private boolean catched;

    public PerformanceEvent(Object source, boolean catched) {
        super(source);
        this.catched = catched;
    }

    public boolean isCatched() {
        return catched;
    }

}
