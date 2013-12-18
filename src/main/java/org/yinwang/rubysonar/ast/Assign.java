package org.yinwang.rubysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.rubysonar.Analyzer;
import org.yinwang.rubysonar.Binder;
import org.yinwang.rubysonar.Constants;
import org.yinwang.rubysonar.State;
import org.yinwang.rubysonar.types.Type;


public class Assign extends Node {

    @NotNull
    public Node target;
    @NotNull
    public Node value;


    public Assign(@NotNull Node target, @NotNull Node value, int start, int end) {
        super(start, end);
        this.target = target;
        this.value = value;
        addChildren(target);
        addChildren(value);
    }


    @NotNull
    @Override
    public Type transform(@NotNull State s) {
        Type valueType = transformExpr(value, s);
        if (target.isName() && target.asName().isInstanceVar()) {
            Type thisType = s.lookupType(Constants.SELFNAME);
            if (thisType == null) {
                Analyzer.self.putProblem(this, "Instance variable assignment not within class");
            } else {
                Binder.bind(thisType.table, target, valueType);
            }
        } else {
            Binder.bind(s, target, valueType);
        }
        return Type.CONT;
    }


    @NotNull
    @Override
    public String toString() {
        return "(" + target + " = " + value + ")";
    }


}
