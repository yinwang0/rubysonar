package org.yinwang.rubysonar.types;

import org.jetbrains.annotations.NotNull;
import org.yinwang.rubysonar.Analyzer;
import org.yinwang.rubysonar.Binding;
import org.yinwang.rubysonar.Constants;
import org.yinwang.rubysonar.State;
import org.yinwang.rubysonar.ast.Call;
import org.yinwang.rubysonar.ast.Name;

import java.util.List;
import java.util.Map;


public class InstanceType extends Type {

    public Type classType;


    public InstanceType(@NotNull Type c) {
        table.setStateType(State.StateType.INSTANCE);
        table.setParent(c.table.parent);
        table.setPath(c.table.path);
        classType = c;

        for (Map.Entry<String, List<Binding>> e : c.table.table.entrySet()) {
            for (Binding b : e.getValue()) {
                if (b.kind != Binding.Kind.CLASS_METHOD) {
                    table.update(e.getKey(), b);
                }
            }
        }
    }


    public InstanceType(@NotNull Type c, Name newName, Call call, List<Type> args) {
        this(c);
        
        if (newName.id.equals("new")) {
            List<Binding> bs = table.lookupAttr(Constants.SELFNAME);
            if (newName != null) {
                Analyzer.self.putRef(newName, bs);
            }
        }
        
        Type initFunc = table.lookupAttrType("initialize");
        if (initFunc != null && initFunc instanceof FunType && ((FunType) initFunc).func != null) {
            ((FunType) initFunc).setSelfType(this);
            Call.apply((FunType) initFunc, args, null, null, null, null, call);
            ((FunType) initFunc).setSelfType(null);
        }
    }


    @Override
    public boolean equals(Object other) {
        if (other instanceof InstanceType) {
            InstanceType iother = (InstanceType) other;
            // for now ignore the case where an instance of the same class is modified
            if (classType.equals(iother.classType) &&
                    table.keySet().equals(iother.table.keySet()))
            {
                return true;
            }
        }
        return false;
    }


    @Override
    public int hashCode() {
        return classType.hashCode();
    }


    @Override
    protected String printType(CyclicTypeRecorder ctr) {
        return ((ClassType) classType).name;
    }
}
