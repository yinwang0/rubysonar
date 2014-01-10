package org.yinwang.rubysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.rubysonar.Analyzer;
import org.yinwang.rubysonar.Binder;
import org.yinwang.rubysonar.Binding;
import org.yinwang.rubysonar.State;
import org.yinwang.rubysonar.types.ClassType;
import org.yinwang.rubysonar.types.FunType;
import org.yinwang.rubysonar.types.Type;

import java.util.List;


public class Function extends Node {

    public Name name;
    public List<Node> args;
    public List<Node> defaults;
    public Name vararg;  // *args
    public Name kwarg;   // **kwarg
    public Name blockarg = null;   // block arg of Ruby
    public List<Node> afterRest = null;   // after rest arg of Ruby
    public Node body;
    public boolean called = false;
    public boolean isLamba = false;
    public Str docstring;


    public Function(Name name, List<Node> args, Node body, List<Node> defaults,
                    Name vararg, Name kwarg, List<Node> afterRest, Name blockarg,
                    Str docstring, String file, int start, int end)
    {
        super(file, start, end);
        if (name != null) {
            this.name = name;
        } else {
            isLamba = true;
            String fn = genLambdaName();
            this.name = new Name(fn, file, start, start + "lambda".length());
            addChildren(this.name);
        }

        this.args = args;
        this.body = body;
        this.defaults = defaults;
        this.vararg = vararg;
        this.kwarg = kwarg;
        this.afterRest = afterRest;
        this.blockarg = blockarg;
        this.docstring = docstring;
        addChildren(args);
        addChildren(defaults);
        addChildren(afterRest);
        addChildren(name, body, vararg, kwarg, blockarg);
    }


    public void setDocstring(Str docstring) {
        this.docstring = docstring;
    }


    @NotNull
    @Override
    public Type transform(@NotNull State s) {
        FunType fun = new FunType(this, s);
        fun.table.setParent(s);
        fun.table.setPath(s.extendPath(name.id));
        fun.setDefaultTypes(resolveList(defaults, s));
        Analyzer.self.addUncalled(fun);

        if (isLamba) {
            return fun;
        } else {
            Type outType = s.type;
            if (outType instanceof ClassType) {
                fun.setCls(outType.asClassType());
            }

            Binder.bind(s, name, fun, Binding.Kind.METHOD);
            return Type.CONT;
        }
    }


    private static int lambdaCounter = 0;


    @NotNull
    public static String genLambdaName() {
        lambdaCounter = lambdaCounter + 1;
        return "lambda%" + lambdaCounter;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Function) {
            Function fo = (Function) obj;
            return (fo.file.equals(file) && fo.start == start);
        } else {
            return false;
        }
    }


    @NotNull
    @Override
    public String toString() {
        return "(func:" + name + ")";
    }

}
