package jade.tree;

import jade.exceptions.ASTInternalException;

/**
 * Represents an access in the JADE language
 * <p>
 * an access is defined like: {@code access} {@link IdentifierTree} {@code [from } {@link IdentifierTree}{@code ]} {@link BlockTree}
 * 
 * @author Simon Emmanuel Gutierrez Brida
 * @version 0.1u
 * TODO: extend the current access specification to support method calls
 * @see IdentifierTree
 * @see BlockTree
 */
public class AccessTree extends Tree {
	
	protected boolean hasFrom = false;

	protected AccessTree(Tree parent, Tree[] childs) {
		super(parent, childs);
	}
	
	//++++++++++++Public constructors
	
	public AccessTree(Tree parent, IdentifierTree identifier, BlockTree block) {
		this(parent, new Tree[]{identifier, block});
	}
	
	public AccessTree(IdentifierTree identifier, BlockTree block) {
		this(null, identifier, block);
	}
	
	public AccessTree(Tree parent, IdentifierTree identifier, IdentifierTree from, BlockTree block) {
		this(parent, new Tree[]{identifier, from, block});
		this.hasFrom = true;
	}
	
	public AccessTree(IdentifierTree identifier, IdentifierTree from, BlockTree block) {
		this(null, identifier, from, block);
	}
	
	//------------Public constructors

	//++++++++++++Checkers
	
	public boolean hasFrom() {
		return this.hasFrom;
	}
	
	//------------Checkers
	
	//++++++++++++Getters
	
	public IdentifierTree getIdentifier() throws ASTInternalException {
		return (IdentifierTree) getChild(0);
	}
	
	public IdentifierTree getFromIdentifier() throws ASTInternalException {
		if (!this.hasFrom) {
			throw new ASTInternalException("jade.tree.AccessTree#getFromIdentifier()", new IllegalStateException("Trying to get the from identifier from an AccessTree constructed without a from identifier"));
		}
		return (IdentifierTree) getChild(1);
	}
	
	public BlockTree getBlock() throws ASTInternalException {
		return (BlockTree) getChild(this.hasFrom?2:1);
	}
	
	//------------Getters

}
