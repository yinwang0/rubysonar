package org.yinwang.rubysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.rubysonar.State;
import org.yinwang.rubysonar.types.ListType;
import org.yinwang.rubysonar.types.Type;

import java.util.List;


public class ExtSlice extends Node {

    public List<Node> dims;


    public ExtSlice(List<Node> dims, int start, int end) {
        super(start, end);
        this.dims = dims;
        addChildren(dims);
    }


    @NotNull
    @Override
    public Type transform(State s) {
        for (Node d : dims) {
            transformExpr(d, s);
        }
        return new ListType();
    }


    @NotNull
    @Override
    public String toString() {
        return "<ExtSlice:" + dims + ">";
    }


    @Override
    public void visit(@NotNull NodeVisitor v) {
        if (v.visit(this)) {
            for (Node d : dims) {
                visitNode(d, v);
            }
        }
    }
}
