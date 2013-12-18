package org.yinwang.rubysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.rubysonar.Analyzer;
import org.yinwang.rubysonar.Binding;
import org.yinwang.rubysonar.State;
import org.yinwang.rubysonar._;
import org.yinwang.rubysonar.types.ModuleType;
import org.yinwang.rubysonar.types.Type;


public class Module extends Node {

    public Block body;

    public Module(Block body, int start, int end) {
        super(start, end);
        this.body = body;
        addChildren(this.body);
    }


    @NotNull
    @Override
    public Type transform(@NotNull State s) {
        ModuleType mt = new ModuleType(name, file, Analyzer.self.globaltable);
        s.insert(_.moduleQname(file), this, mt, Binding.Kind.MODULE);
        transformExpr(body, mt.getTable());
        return mt;
    }


    @NotNull
    @Override
    public String toString() {
        return "(module:" + file + ")";
    }


    @Override
    public void visit(@NotNull NodeVisitor v) {
        if (v.visit(this)) {
            visitNode(body, v);
        }
    }
}
