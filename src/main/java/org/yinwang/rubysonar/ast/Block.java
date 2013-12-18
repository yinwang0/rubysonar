package org.yinwang.rubysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.rubysonar.Analyzer;
import org.yinwang.rubysonar.State;
import org.yinwang.rubysonar.types.Type;
import org.yinwang.rubysonar.types.UnionType;

import java.util.List;


public class Block extends Node {

    @NotNull
    public List<Node> seq;


    public Block(@NotNull List<Node> seq, int start, int end) {
        super(start, end);
        this.seq = seq;
        addChildren(seq);
    }


    @NotNull
    @Override
    public Type transform(@NotNull State state) {

        boolean returned = false;
        Type retType = Type.UNKNOWN;

        for (Node n : seq) {
            Type t = transformExpr(n, state);
            if (!returned) {
                retType = UnionType.union(retType, t);
                if (!UnionType.contains(t, Type.CONT)) {
                    returned = true;
                    retType = UnionType.remove(retType, Type.CONT);
                }
            } else if (state.getStateType() != State.StateType.GLOBAL &&
                    state.getStateType() != State.StateType.MODULE)
            {
                Analyzer.self.putProblem(n, "unreachable code");
            }
        }

        return retType;
    }


    public boolean isEmpty() {
        return seq.isEmpty();
    }


    @NotNull
    @Override
    public String toString() {
        return "(block:" + seq + ")";
    }


}
