package org.yinwang.rubysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.rubysonar.Binder;
import org.yinwang.rubysonar.State;
import org.yinwang.rubysonar.types.Type;

import java.util.List;


public class Handler extends Node {

    public List<Node> exceptions;
    public Node binder;
    public Block body;


    public Handler(List<Node> exceptions, Node binder, Block body, int start, int end) {
        super(start, end);
        this.binder = binder;
        this.exceptions = exceptions;
        this.body = body;
        addChildren(binder, body);
        addChildren(exceptions);
    }


    @NotNull
    @Override
    public Type transform(@NotNull State s) {
        Type typeval = Type.UNKNOWN;
        if (exceptions != null) {
            typeval = resolveUnion(exceptions, s);
        }
        if (binder != null) {
            Binder.bind(s, binder, typeval);
        }
        if (body != null) {
            return transformExpr(body, s);
        } else {
            return Type.UNKNOWN;
        }
    }


    @NotNull
    @Override
    public String toString() {
        return "(handler:" + exceptions + ":" + binder + ")";
    }


}
