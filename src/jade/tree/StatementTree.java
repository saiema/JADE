package jade.tree;

/**
 * Represents a statement
 * 
 * @author Simon Emmanuel Gutierrez Brida
 * @version 0.1u
 */
public abstract class StatementTree extends Tree {

	protected StatementTree(Tree parent, Tree[] childs) {
		super(parent, childs);
	}

}
