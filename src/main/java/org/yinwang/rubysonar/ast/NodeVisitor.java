package org.yinwang.rubysonar.ast;

/**
 * garbage code - to be removed
 */
public interface NodeVisitor {

    public static final class StopIterationException extends RuntimeException {
        public StopIterationException() {
        }
    }


    public boolean visit(Assign m);


    public boolean visit(Attribute m);


    public boolean visit(BinOp m);


    public boolean visit(Symbol m);


    public boolean visit(Block m);


    public boolean visit(Control m);


    public boolean visit(Call m);


    public boolean visit(Class m);


    public boolean visit(Undef m);


    public boolean visit(Dict m);


    public boolean visit(Handler m);


    public boolean visit(For m);


    public boolean visit(Function m);


    public boolean visit(If m);


    public boolean visit(IfExp m);


    public boolean visit(Index m);


    public boolean visit(Keyword m);


    public boolean visit(Array m);


    public boolean visit(Module m);


    public boolean visit(Name m);


    public boolean visit(Num m);


    public boolean visit(Op m);


    public boolean visit(Void m);


    public boolean visit(Raise m);


    public boolean visit(Return m);


    public boolean visit(Slice m);


    public boolean visit(Str m);


    public boolean visit(Subscript m);


    public boolean visit(Try m);


    public boolean visit(Tuple m);


    public boolean visit(UnaryOp m);


    public boolean visit(Url m);


    public boolean visit(While m);


    public boolean visit(Yield m);


    public boolean visit(Starred s);
}
