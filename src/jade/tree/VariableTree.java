package jade.tree;

import jade.exceptions.ASTInternalException;

/**
 * This tree node represents a variable and it's defined by :
 * <p>
 * <li> an identifier 	:	{@code IdentifierTree} </li>
 * <li> a type			:	{@code TypeTree}</li>
 * <p>
 * 
 * @author Simon Emmanuel Gutierrez Brida
 * @version 0.1u
 * @see TypeTree
 * @see IdentifierTree
 */
public class VariableTree extends Tree {

	protected VariableTree(Tree parent, Tree[] childs) {
		super(parent, childs);
	}
	
	public VariableTree(Tree parent, TypeTree type, IdentifierTree identifier) {
		super(parent, new Tree[]{type, identifier});
	}
	
	public VariableTree(TypeTree type, IdentifierTree identifier) {
		this(null, type, identifier);
	}
	
	public TypeTree getType() throws ASTInternalException {
		return (TypeTree) getChild(0);
	}
	
	public IdentifierTree getIdentifier() throws ASTInternalException {
		return (IdentifierTree) getChild(1);
	}

}
