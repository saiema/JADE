package jade.tree;

import jade.exceptions.ASTInternalException;

/**
 * Represents a block containing one or more statements
 * 
 * @author Simon Emmanuel Gutierrez Brida
 * @version 0.1u
 * @see StatementTree
 */
public class BlockTree extends StatementTree {

	public BlockTree(Tree parent, StatementTree[] childs) {
		super(parent, childs);
	}
	
	public StatementTree[] getStatements() throws ASTInternalException {
		return (StatementTree[]) getChilds(0, this.childs.length - 1);
	}

}
