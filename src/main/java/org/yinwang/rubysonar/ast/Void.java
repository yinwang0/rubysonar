package org.yinwang.rubysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.rubysonar.State;
import org.yinwang.rubysonar.types.Type;


public class Void extends Node {

    public Void(int start, int end) {
        super(start, end);
    }


    @NotNull
    @Override
    public Type transform(State s) {
        return Type.CONT;
    }


    @NotNull
    @Override
    public String toString() {
        return "(void)";
    }


}
