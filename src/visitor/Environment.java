package visitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.lang.model.type.TypeMirror;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.Trees;

public class Environment {
	
	protected static boolean overrideEntries = false;
	
	public static void setOverrideEntries(boolean value) {
		Environment.overrideEntries = value;
	}
	
	protected Environment parent;
	
	protected Environment child;
	
	protected Map<Tree, EnvironmentEntry> env;
	
	protected Trees trees;
	
	public Environment(Trees trees) {
		this.trees = trees;
		this.env = new HashMap<Tree, EnvironmentEntry>();
		this.parent = null;
		this.child = null;
	}
	
	public Environment(Trees trees, Environment parent) {
		this(trees);
		this.parent = parent;
		this.parent.child = this;
	}
	
	public Environment(Environment parent) {
		this(parent.trees);
		this.parent = parent;
		this.parent.child = this;
	}
	
	public void addEntry(CompilationUnitTree cutree) {
		if (!this.env.containsKey(cutree) || Environment.overrideEntries) {
			this.env.put(cutree, new EnvironmentEntry(this.trees, cutree));
		}
	}
	
	public void addEntry(CompilationUnitTree cu, Tree tree) {
		if (!this.env.containsKey(tree) || Environment.overrideEntries) {
			this.env.put(tree, new EnvironmentEntry(this.trees, cu, tree));
		}
	}
	
	public TypeMirror getType(Tree tree) {
		if (env.containsKey(tree)) {
			return this.trees.getTypeMirror(this.env.get(tree).getTreePath());
		} else if (this.parent != null) {
			return this.parent.getType(tree);
		} else {
			return null;
		}
	}
	
	public Environment getLeafEnvironment() {
		if (this.child == null) {
			return this;
		} else {
			return this.child.getLeafEnvironment();
		}
	}
	
	public Environment getRootEnvironment() {
		if (this.parent == null) {
			return this;
		} else {
			return this.parent.getRootEnvironment();
		}
	}
	
	@Override
	public String toString() {
		String envToString = "";
		for (Entry<Tree, EnvironmentEntry> entry : this.env.entrySet()) {
			envToString += entry.getValue().toString() + "\n";
			envToString += "type     : " + getType(entry.getKey()) + "\n\n";
		}
		envToString += "\n";
		if (this.child != null) {
			envToString += "===child===" + "\n";
			envToString += this.child.toString();
		}
		return envToString;
	}

}
