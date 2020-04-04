package com.example.savetheparachutists;

import java.io.Serializable;
import java.util.EventObject;

public abstract class Controller_Display_Communication_Event extends EventObject {

    public Controller_Display_Communication_Event(Object source) {
        super(source);
    }
}
