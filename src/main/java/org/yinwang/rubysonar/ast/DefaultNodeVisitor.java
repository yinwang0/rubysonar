package org.yinwang.rubysonar.ast;

/**
 * garbage code - to be removed
 */
public class DefaultNodeVisitor implements NodeVisitor {

    protected boolean traverseIntoNodes = true;


    public void stopTraversal() {
        traverseIntoNodes = false;
    }


    public boolean visit(Symbol n) {
        return traverseIntoNodes;
    }


    public boolean visit(Assign n) {
        return traverseIntoNodes;
    }


    public boolean visit(Attribute n) {
        return traverseIntoNodes;
    }


    public boolean visit(BinOp n) {
        return traverseIntoNodes;
    }


    public boolean visit(Block n) {
        return traverseIntoNodes;
    }


    public boolean visit(Call n) {
        return traverseIntoNodes;
    }


    public boolean visit(Class n) {
        return traverseIntoNodes;
    }


    public boolean visit(Undef n) {
        return traverseIntoNodes;
    }


    public boolean visit(Dict n) {
        return traverseIntoNodes;
    }


    public boolean visit(Handler n) {
        return traverseIntoNodes;
    }


    public boolean visit(For n) {
        return traverseIntoNodes;
    }


    public boolean visit(Function n) {
        return traverseIntoNodes;
    }


    public boolean visit(If n) {
        return traverseIntoNodes;
    }


    public boolean visit(IfExp n) {
        return traverseIntoNodes;
    }


    public boolean visit(Index n) {
        return traverseIntoNodes;
    }


    public boolean visit(Keyword n) {
        return traverseIntoNodes;
    }


    public boolean visit(Array n) {
        return traverseIntoNodes;
    }


    public boolean visit(Module n) {
        return traverseIntoNodes;
    }


    public boolean visit(Name n) {
        return traverseIntoNodes;
    }


    public boolean visit(RbInt n) {
        return traverseIntoNodes;
    }


    public boolean visit(RbFloat n) {
        return traverseIntoNodes;
    }


    public boolean visit(Op n) {
        return traverseIntoNodes;
    }


    public boolean visit(Void n) {
        return traverseIntoNodes;
    }


    public boolean visit(Raise n) {
        return traverseIntoNodes;
    }


    public boolean visit(Return n) {
        return traverseIntoNodes;
    }


    public boolean visit(Control n) {
        return traverseIntoNodes;
    }


    public boolean visit(Slice n) {
        return traverseIntoNodes;
    }


    public boolean visit(Str n) {
        return traverseIntoNodes;
    }


    public boolean visit(Subscript n) {
        return traverseIntoNodes;
    }


    public boolean visit(Try n) {
        return traverseIntoNodes;
    }


    public boolean visit(Tuple n) {
        return traverseIntoNodes;
    }


    public boolean visit(UnaryOp n) {
        return traverseIntoNodes;
    }


    public boolean visit(Url n) {
        return traverseIntoNodes;
    }


    public boolean visit(While n) {
        return traverseIntoNodes;
    }


    public boolean visit(Yield n) {
        return traverseIntoNodes;
    }


    public boolean visit(Starred n) {
        return traverseIntoNodes;
    }
}
