package org.yinwang.rubysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yinwang.rubysonar.Analyzer;
import org.yinwang.rubysonar.Binder;
import org.yinwang.rubysonar.Binding;
import org.yinwang.rubysonar.State;
import org.yinwang.rubysonar.types.ClassType;
import org.yinwang.rubysonar.types.Type;


public class Class extends Node {
    private static int classCounter = 0;

    @Nullable
    public Node locator;
    public Name name;
    public Node base;
    public Node body;


    public Class(@Nullable Node locator, Node base, Node body, int start, int end) {
        super(start, end);

        // set name
        if (locator instanceof Attribute) {
            this.name = ((Attribute) locator).attr;
        } else if (locator instanceof Name) {
            this.name = (Name) locator;
        } else {
            this.name = new Name(genClassName(), start, start + 1);
            addChildren(this.name);
        }

        this.locator = locator;
        this.base = base;
        this.body = body;
        addChildren(this.locator, this.body, this.base);
    }


    @Override
    public boolean isClassDef() {
        return true;
    }


    @NotNull
    public Node getLocator() {
        return locator;
    }


    @NotNull
    public static String genClassName() {
        classCounter = classCounter + 1;
        return "class%" + classCounter;
    }


    @NotNull
    @Override
    public Type transform(@NotNull State s) {
        if (locator != null) {
            Type existing = transformExpr(locator, s);
            if (existing instanceof ClassType) {
                if (body != null) {
                    transformExpr(body, existing.table);
                }
                return Type.CONT;
            }
        }

        ClassType classType = new ClassType(name.id, s);

        if (base != null) {
            Type baseType = transformExpr(base, s);
            if (baseType.isClassType()) {
                classType.addSuper(baseType);
            } else {
                Analyzer.self.putProblem(base, base + " is not a class");
            }
        }

        // Bind ClassType to name here before resolving the body because the
        // methods need this type as self.
        Binder.bind(s, name, classType, Binding.Kind.CLASS);
        if (body != null) {
            transformExpr(body, classType.getTable());
        }
        return Type.CONT;
    }


    @NotNull
    @Override
    public String toString() {
        return "(class:" + name.id + ")";
    }

}
