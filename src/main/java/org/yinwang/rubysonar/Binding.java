package org.yinwang.rubysonar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yinwang.rubysonar.ast.Class;
import org.yinwang.rubysonar.ast.*;
import org.yinwang.rubysonar.types.ModuleType;
import org.yinwang.rubysonar.types.Type;

import java.util.LinkedHashSet;
import java.util.Set;


public class Binding implements Comparable<Object> {

    public enum Kind {
        ATTRIBUTE,    // attr accessed with "." on some other object
        CLASS,        // class definition
        CONSTRUCTOR,  // initialize functions in classes
        FUNCTION,     // plain function
        METHOD,       // static or instance method
        MODULE,       // file
        PARAMETER,    // function param
        SCOPE,        // top-level variable ("scope" means we assume it can have attrs)
        VARIABLE      // local variable
    }


    @NotNull
    public String name;     // unqualified name
    @NotNull
    public Node node;
    @NotNull
    public String qname;    // qualified name
    public Type type;       // inferred type
    public Kind kind;        // name usage context

    public Set<Node> refs;

    public int start = -1;
    public int end = -1;
    public int bodyStart = -1;
    public int bodyEnd = -1;

    @Nullable
    public String file;


    public Binding(@NotNull String id, @NotNull Node node, @NotNull Type type, @NotNull Kind kind) {
        this.name = id;
        this.qname = type.getTable().getPath();
        this.type = type;
        this.kind = kind;
        this.node = node;
        refs = new LinkedHashSet<>(1);

        if (node instanceof Url) {
            String url = ((Url) node).getURL();
            if (url.startsWith("file://")) {
                file = url.substring("file://".length());
            } else {
                file = url;
            }
        } else {
            file = node.file;
            if (node instanceof Name) {
                name = node.asName().id;
            }
        }

        initLocationInfo(node);
        Analyzer.self.registerBinding(this);
    }


    private void initLocationInfo(Node node) {
        start = node.start;
        end = node.end;

        Node parent = node.parent;
        if ((parent instanceof Function && ((Function) parent).name == node) ||
                (parent instanceof Class && ((Class) parent).locator == node))
        {
            bodyStart = parent.start;
            bodyEnd = parent.end;
        } else if (node instanceof Module) {
            name = ((Module) node).name.id;
            start = 0;
            end = 0;
            bodyStart = node.start;
            bodyEnd = node.end;
        } else {
            bodyStart = node.start;
            bodyEnd = node.end;
        }
    }


    public void setQname(@NotNull String qname) {
        this.qname = qname;
    }


    public void addRef(Node ref) {
        refs.add(ref);
    }


    @NotNull
    public String getFirstFile() {
        Type bt = type;
        if (bt instanceof ModuleType) {
            String file = bt.asModuleType().getFile();
            return file != null ? file : "<built-in module>";
        }

        String file = this.file;
        if (file != null) {
            return file;
        }

        return "<built-in binding>";
    }


    /**
     * Bindings can be sorted by their location for outlining purposes.
     */
    public int compareTo(@NotNull Object o) {
        return start - ((Binding) o).start;
    }


    @NotNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(binding:");
        sb.append(":kind=").append(kind);
        sb.append(":node=").append(node);
        sb.append(":type=").append(type);
        sb.append(":qname=").append(qname);
        sb.append(":refs=");
        if (refs.size() > 10) {
            sb.append("[");
            sb.append(refs.iterator().next());
            sb.append(", ...(");
            sb.append(refs.size() - 1);
            sb.append(" more)]");
        } else {
            sb.append(refs);
        }
        sb.append(">");
        return sb.toString();
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Binding)) {
            return false;
        } else {
            Binding b = (Binding) obj;
            return (start == b.start &&
                    end == b.end &&
                    _.same(file, b.file));
        }
    }


    @Override
    public int hashCode() {
        return ("" + file + start).hashCode();
    }

}
