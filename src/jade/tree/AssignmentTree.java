package jade.tree;

import jade.exceptions.ASTInternalException;

/**
 * Represents an assignment : {@link IdentifierTree} {@code = } {@link ExpressionTree}
 * 
 * @author Simon Emmanuel Gutierrez Brida
 * @version 0.1u
 * @see IdentifierTree
 * @see ExpressionTree
 */
public class AssignmentTree extends StatementTree {

	protected AssignmentTree(Tree parent, Tree[] childs) {
		super(parent, childs);
	}
	
	//+++++++++++Constructors
	
	public AssignmentTree(Tree parent, IdentifierTree identifier, ExpressionTree expression) {
		this(parent, new Tree[]{identifier, expression});
	}
	
	public AssignmentTree(IdentifierTree identifier, ExpressionTree expression) {
		this(null, identifier, expression);
	}
	
	//-----------Constructors
	
	//+++++++++++Getters
	
	public IdentifierTree getIdentifier() throws ASTInternalException {
		return (IdentifierTree) getChild(0);
	}
	
	public ExpressionTree getExpression() throws ASTInternalException {
		return (ExpressionTree) getChild(1);
	}
	
	//-----------Getters

}
