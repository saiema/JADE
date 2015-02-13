package visitor;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;

public class EnvironmentEntry {
	
	protected Tree treeElem;
	protected TreePath treePath;
	protected Scope scope;
	
	public EnvironmentEntry(Trees trees, CompilationUnitTree cu) {
		this.treePath = new TreePath(cu);
		this.treeElem = cu;
		this.scope = trees.getScope(this.treePath);
	}
	
	public EnvironmentEntry(Trees trees, CompilationUnitTree cu, Tree tree) {
		this.treePath = trees.getPath(cu, tree);
		this.treeElem = tree;
		this.scope = trees.getScope(this.treePath);
	}
	
	public Tree getTree() {
		return this.treeElem;
	}
	
	public TreePath getTreePath() {
		return this.treePath;
	}
	
	public Scope getScope() {
		return this.scope;
	}
	
	@Override
	public String toString() {
		return 		"tree elem: " + this.treeElem.toString() + "\n"
				+ 	"tree path: " + this.treePath.toString() + "\n"
				+	"scope    : " + this.scope.toString();
	}
	
}
