package org.yinwang.rubysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.rubysonar.Analyzer;
import org.yinwang.rubysonar.State;
import org.yinwang.rubysonar.types.Type;


public class Regexp extends Node {
    public Node pattern;
    public Node end;


    public Regexp(Node pattern, Node regexpEnd, int start, int end) {
        super(start, end);
        this.pattern = pattern;
        this.end = regexpEnd;
    }


    @NotNull
    @Override
    protected Type transform(State s) {
        return Type.UNKNOWN_STR;
    }


    @Override
    public void visit(NodeVisitor visitor) {

    }
}
