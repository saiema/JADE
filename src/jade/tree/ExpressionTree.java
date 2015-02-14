package jade.tree;

/**
 * Represents an expression that can be one of the following
 * <p>
 * <li>a constant</li>
 * <li>an identifier</li>
 * <li>an unary expression</li>
 * <li>a binary expression</li>
 * 
 * @author Simon Emmanuel Gutierrez Brida
 * @version 0.1u
 * TODO: extend the current JADE language to support method calls (as an expression)
 */
public abstract class ExpressionTree extends Tree {
	protected static enum Type {CONSTANT, IDENTIFIER, UNARY, BINARY, INVALID};

	protected Type type = Type.INVALID;
	
	protected ExpressionTree(Tree parent, Tree[] childs) {
		super(parent, childs);
	}
	
	//+++++++++++++Checkers
	
	public boolean isConstant() {
		return this.type.equals(Type.CONSTANT);
	}
	
	public boolean isIdentifier() {
		return this.type.equals(Type.CONSTANT);
	}
	
	public boolean isUnary() {
		return this.type.equals(Type.UNARY);
	}
	
	public boolean isBinary() {
		return this.type.equals(Type.BINARY);
	}
	
	public boolean isValid() {
		return !this.type.equals(Type.INVALID);
	}
	
	//-------------Checkers

}
