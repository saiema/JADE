package jade.tree;

import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a set of bounds like
 * <p>
 * <li>& Types (represents A & B & ...</li>
 * <li>Type</li>
 * <p>
 * 
 * @author Simon Emmanuel Gutierrez Brida
 * @version 0.1u
 * @see TypeTree
 */
public class BoundsTree extends Tree {
	
	public BoundsTree(Tree parent, TypeTree[] types) {
		super(parent, types);
	}
	
	public BoundsTree(TypeTree type) {
		this(null, new TypeTree[]{type});
	}
	
	public BoundsTree(Tree parent, List<TypeTree> types) {
		this(parent, types.toArray(new TypeTree[types.size()]));
	}
	
	public BoundsTree(List<TypeTree> types) {
		this(null, types.toArray(new TypeTree[types.size()]));
	}
	
	public BoundsTree(TypeTree[] types) {
		this(null, types);
	}
	
	public List<TypeTree> getBoundsAsList() {
		if (this.childs == null) {
			return null;
		}
		List<TypeTree> bounds = new LinkedList<TypeTree>();
		for (Tree tree : this.childs) {
			bounds.add((TypeTree)tree);
		}
		return bounds;
	}
	
	public TypeTree[] getBoundsAsArray() {
		return (TypeTree[]) this.childs;
	}

}
