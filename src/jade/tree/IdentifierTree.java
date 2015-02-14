package jade.tree;

/**
 * This tree node represents an identifier
 * 
 * @author Simon Emmanuel Gutierrez Brida
 * @version 0.1u
 */
public class IdentifierTree extends Tree {
	
	protected String name;

	protected IdentifierTree(Tree parent, Tree[] childs) {
		super(parent, childs);
		this.name = "NO_NAME.ID: " + this.id;
	}
	
	/**
	 * Constructor
	 * <p>
	 * Creates a new {@code IdentifierTree} node with no parent
	 * 
	 * @param name	:	the name associated to this identifier	:	{@code String}
	 */
	public IdentifierTree(String name) {
		super(null, null);
		this.name = name;
	}
	
	/**
	 * Constructor
	 * <p>
	 * Creates a new {@code IdentifierTree} node with a specific parent
	 * 
	 * @param parent	:	the parent of this identifier			:	{@code Tree}
	 * @param name		:	the name associated to this identifier	:	{@code String}
	 */
	public IdentifierTree(Tree parent, String name) {
		this(name);
		this.parent = parent;
	}
	
	/**
	 * @return the name associated to this identifier	:	{@code String}
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * On the contrary with the normal {code clone()} this one will keep the original IDs
	 * 
	 * @return a clone of this tree (including a clone of the parent and the childs) mantaining the original IDs
	 */
	public Tree clone_keeping_id() {
		int current_id = Tree.CURRENT_ID; //store current value of Tree.CURRENT_ID
		IdentifierTree identifierTreeClone = new IdentifierTree(this.parent.clone_keeping_id(), this.name);
		Tree.CURRENT_ID = current_id; //old value of Tree.CURRENT_ID is restored
		identifierTreeClone.id = this.id;
		return identifierTreeClone;
	}
	
	@Override
	public Tree clone() {
		IdentifierTree identifierTreeClone = new IdentifierTree(this.parent.clone(), this.name);
		return identifierTreeClone;
	}

}
