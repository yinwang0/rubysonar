package org.yinwang.rubysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.rubysonar.Analyzer;
import org.yinwang.rubysonar.State;
import org.yinwang.rubysonar.types.Type;

import java.util.List;


public class Delete extends Node {

    public List<Node> targets;


    public Delete(List<Node> elts, int start, int end) {
        super(start, end);
        this.targets = elts;
        addChildren(elts);
    }


    @NotNull
    @Override
    public Type transform(@NotNull State s) {
        for (Node n : targets) {
            transformExpr(n, s);
            if (n instanceof Name) {
                s.remove(n.asName().id);
            }
        }
        return Analyzer.self.builtins.Cont;
    }


    @NotNull
    @Override
    public String toString() {
        return "<Delete:" + targets + ">";
    }


    @Override
    public void visit(@NotNull NodeVisitor v) {
        if (v.visit(this)) {
            visitNodes(targets, v);
        }
    }
}
