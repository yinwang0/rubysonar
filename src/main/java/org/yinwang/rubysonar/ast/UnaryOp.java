package org.yinwang.rubysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.rubysonar.State;
import org.yinwang.rubysonar.types.Type;


public class UnaryOp extends Node {

    public Op op;
    public Node operand;


    public UnaryOp(Op op, Node operand, String file, int start, int end) {
        super(file, start, end);
        this.op = op;
        this.operand = operand;
        addChildren(operand);
    }


    @NotNull
    @Override
    public Type transform(State s) {
        return transformExpr(operand, s);
    }


    @NotNull
    @Override
    public String toString() {
        return "(" + op + " " + operand + ")";
    }

}
