package org.yinwang.rubysonar.demos;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


/**
 * Represents a simple style run for purposes of source highlighting.
 */
public class Style implements Comparable<Style> {

    public enum Type {
        KEYWORD,
        COMMENT,
        STRING,
        DOC_STRING,
        IDENTIFIER,
        BUILTIN,
        NUMBER,
        CONSTANT,       // ALL_CAPS identifier
        FUNCTION,       // function name
        PARAMETER,      // function parameter
        LOCAL,          // local variable
        DECORATOR,      // function decorator
        CLASS,          // class name
        ATTRIBUTE,      // object attribute
        LINK,           // hyperlink
        ANCHOR,         // name anchor
        DELIMITER,
        TYPE_NAME,      // reference to a type (e.g. function or class name)

        ERROR,
        WARNING,
        INFO
    }


    public Type type;
    public int offset;
    public int length;

    public String message;  // optional hover text
    @Nullable
    public String url;      // internal or external link
    @Nullable
    public String id;       // for hover highlight
    public List<String> highlight;   // for hover highlight


    public Style(Type type, int offset, int length) {
        this.type = type;
        this.offset = offset;
        this.length = length;
    }


    public int end() {
        return offset + length;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Style)) {
            return false;
        }
        Style other = (Style) o;
        return other.type == this.type
                && other.offset == this.offset
                && other.length == this.length
                && equalFields(other.message, this.message)
                && equalFields(other.url, this.url);
    }


    private boolean equalFields(@Nullable Object o1, @Nullable Object o2) {
        if (o1 == null) {
            return o2 == null;
        } else {
            return o1.equals(o2);
        }
    }


    public int compareTo(@NotNull Style other) {
        if (this.equals(other)) {
            return 0;
        }
        if (this.offset < other.offset) {
            return -1;
        }
        if (other.offset < this.offset) {
            return 1;
        }
        return this.hashCode() - other.hashCode();
    }


    @NotNull
    @Override
    public String toString() {
        return "[" + type + " start=" + offset + " len=" + length + "]";
    }
}
