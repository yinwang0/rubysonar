package org.yinwang.rubysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.rubysonar.Analyzer;
import org.yinwang.rubysonar.State;
import org.yinwang.rubysonar.types.Type;


public class Bytes extends Node {

    private Object value;


    public Bytes(@NotNull Object value, int start, int end) {
        super(start, end);
        this.value = value.toString();
    }


    public Object getStr() {
        return value;
    }


    @NotNull
    @Override
    public Type transform(State s) {
        return Analyzer.self.builtins.BaseStr;
    }


    @NotNull
    @Override
    public String toString() {
        return "<Bytpes: " + value + ">";
    }


    @Override
    public void visit(@NotNull NodeVisitor v) {
        v.visit(this);
    }
}
