package org.yinwang.rubysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.rubysonar.State;
import org.yinwang.rubysonar.types.NumType;
import org.yinwang.rubysonar.types.Type;


public class Num extends Node {

    public double n;


    public Num(Object n, int start, int end) {
        super(start, end);
        if (n instanceof String) {
            String s = (String) n;
            this.n = getDouble(s);
        } else {
            this.n = (Double) n;
        }
    }


    public Double getDouble(String s) {
        try {
            s = s.replaceAll("_", "");
            if (s.startsWith("0b")) {
                return (double) Integer.parseInt(s.substring(2), 2);
            } else {
                return (double) Integer.parseInt(s);
            }
        } catch (Exception e1) {
            try {
                return Double.parseDouble(s);
            } catch (Exception e2) {
                return null;
            }
        }
    }


    public Integer getInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return null;
        }
    }


    @NotNull
    @Override
    public Type transform(State s) {
        String typename;

        if (Math.floor(n) == n) {
            typename = "int";
        } else {
            typename = "float";

        }
        return new NumType(typename, n);
    }


    @NotNull
    @Override
    public String toString() {
        return "(num:" + n + ")";
    }


    @Override
    public void visit(@NotNull NodeVisitor v) {
        v.visit(this);
    }
}
