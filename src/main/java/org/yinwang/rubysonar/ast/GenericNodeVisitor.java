package org.yinwang.rubysonar.ast;

/**
 * garbage code - to be removed
 */
public abstract class GenericNodeVisitor extends DefaultNodeVisitor {

    public boolean dispatch(Node n) {
        return traverseIntoNodes;
    }


    public boolean visit(Assign n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(Attribute n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(BinOp n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(Block n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(Call n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(Class n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(Undef n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(Dict n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(Handler n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(For n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(Function n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(If n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(IfExp n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(Index n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(Keyword n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(Array n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(Module n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(Name n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(RbInt n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(RbFloat n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(Void n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(Raise n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(Return n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(Slice n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(Str n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(Subscript n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(Tuple n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(UnaryOp n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(Url n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(While n) {
        return traverseIntoNodes && dispatch(n);
    }


    public boolean visit(Yield n) {
        return traverseIntoNodes && dispatch(n);
    }
}
