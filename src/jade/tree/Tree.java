package jade.tree;

import java.util.Arrays;

import jade.exceptions.ASTInternalException;

/**
 * This class represent the basic Tree element of JADE AST
 * 
 * @author Simon Emmanuel Gutierrez Brida
 * @version 0.1u
 */
public class Tree {
	
	protected static int CURRENT_ID = -1;
	
	/**
	 * The parent (if any) of this tree
	 */
	protected Tree parent;
	/**
	 * The inmediate childs of this tree
	 */
	protected Tree[] childs;
	/**
	 * Unique id to identify this tree
	 */
	protected int id = getNextID();

	/**
	 * Construtor
	 * 
	 * @param parent	:	the parent (if any) of this tree, {@code null} means that this is the root of the AST	:	{@code Tree}
	 * @param childs	:	the childs of this tree node, the ammount of childs for any tree node is fixed	:	{@code Tree[]}
	 */
	public Tree(Tree parent, Tree[] childs) {
		this.parent = parent;
		this.childs = childs;
	}
	
	/**
	 * @return the next id to use	:	{@code int}
	 */
	public synchronized static int getNextID() {
		return ++Tree.CURRENT_ID;
	}
	
	/**
	 * Sets a child in a specific position
	 * 
	 * @param child						:	the child to set	:	{@code Tree}
	 * @param pos						:	the position		:	{@code int}
	 * @throws ASTInternalException if {@link Tree#childs} is {@code null} or {@code pos < 0 || pos >=} {@link Tree#childs}{@code .length}
	 */
	protected void setChild(Tree child, int pos) throws ASTInternalException {
		if (this.childs == null) {
			throw new ASTInternalException("jade.tree.Tree#setChild", new NullPointerException("jade.tree.Tree#childs is null"));
		}
		if (pos < 0 || pos >= this.childs.length) {
			throw new ASTInternalException("jade.tree.Tree#setChild", new IndexOutOfBoundsException("jade.tree.Tree#childs size : " + this.childs.length + " ; Position : " + pos));
		}
		this.childs[pos] = child;
	}
	
	/**
	 * Returns the child in a specific position
	 * 
	 * @param pos						:	the position		:	{@code int}
	 * @throws ASTInternalException if {@link Tree#childs} is {@code null} or {@code pos < 0 || pos >=} {@link Tree#childs}{@code .length}
	 */
	protected Tree getChild(int pos) throws ASTInternalException {
		if (this.childs == null) {
			throw new ASTInternalException("jade.tree.Tree#getChild", new NullPointerException("jade.tree.Tree#childs is null"));
		}
		if (pos < 0 || pos >= this.childs.length) {
			throw new ASTInternalException("jade.tree.Tree#getChild", new IndexOutOfBoundsException("jade.tree.Tree#childs size : " + this.childs.length + " ; Position : " + pos));
		}
		return this.childs[pos];
	}
	
	/**
	 * Returns the childs contained between two positions (inclusive)
	 * 
	 * @param startPos	:	the position of the first child to return	:	{@code int}
	 * @param endPos	:	the position of the last child to return	:	{@code int}
	 * @return			:	an array containing all childs between {@code startPos} and {@code endPos}	:	{@code Tree[]}	
	 * 
	 * @throws ASTInternalException if {@link Tree#childs} is {@code null} or {@code startPos < 0 || startPos >=} {@link Tree#childs}{@code .length} or {@code endPos < 0 || endPos >=} {@link Tree#childs}{@code .length} or {@code startPos > endPos}}
	 */
	protected Tree[] getChilds(int startPos, int endPos) throws ASTInternalException {
		if (this.childs == null) {
			throw new ASTInternalException("jade.tree.Tree#getChilds", new NullPointerException("jade.tree.Tree#childs is null"));
		}
		if (startPos < 0 || startPos >= this.childs.length) {
			throw new ASTInternalException("jade.tree.Tree#getChilds", new IndexOutOfBoundsException("jade.tree.Tree#childs size : " + this.childs.length + " ; Start position : " + startPos));
		}
		if (endPos < 0 || endPos >= this.childs.length) {
			throw new ASTInternalException("jade.tree.Tree#getChilds", new IndexOutOfBoundsException("jade.tree.Tree#childs size : " + this.childs.length + " ; End position : " + endPos));
		}
		if (startPos > endPos) {
			throw new ASTInternalException("jade.tree.Tree#getChilds", new IllegalArgumentException("the start positon is greater than then end position"));
		}
		return Arrays.copyOfRange(this.childs, startPos, endPos);
	}
	
	/**
	 * @return the parent of this tree node : {@code Tree}
	 */
	public Tree getParent() {
		return this.parent;
	}
	
	/**
	 * @return {@code true} if this tree node have no children
	 */
	public boolean isLeaf() {
		return this.childs == null || this.childs.length == 0;
	}
	
	/**
	 * @return {@code true} if this tree have no parent
	 */
	public boolean isRoot() {
		return this.parent == null;
	}
	
	/**
	 * On the contrary with the normal {code clone()} this one will keep the original IDs
	 * 
	 * @return a clone of this tree (including a clone of the parent and the childs) mantaining the original IDs
	 */
	public Tree clone_keeping_id() {
		int current_id = Tree.CURRENT_ID; //store current value of Tree.CURRENT_ID
		Tree[] childClones = new Tree[this.childs.length];							//++++++++++++++++++++
		for (int c = 0; c < this.childs.length; c++) {								//++++++++++++++++++++
			childClones[c] = this.childs[c].clone_keeping_id();						//Tree.CURRENT_ID is modified
		}																			//++++++++++++++++++++
		Tree treeClone = new Tree(this.parent.clone_keeping_id(), childClones);		//++++++++++++++++++++
		Tree.CURRENT_ID = current_id; //old value of Tree.CURRENT_ID is restored
		treeClone.id = this.id;
		return treeClone;
	}
	
	@Override
	public Tree clone() {
		Tree[] childClones = new Tree[this.childs.length];
		for (int c = 0; c < this.childs.length; c++) {
			childClones[c] = this.childs[c].clone();
		}
		Tree treeClone = new Tree(this.parent.clone(), childClones);
		return treeClone;
	}
	
}
