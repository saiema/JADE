package visitor;

import java.util.List;

import com.sun.source.tree.*;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.Trees;

/**
 * Collects signatures and variables in an AST
 *
 * @author Simon Emmanuel Gutierrez Brida
 * @since 0.1
 */
public class SignatureCollectorVisitor implements TreeVisitor<Void,Environment> {
	
	protected final int increment = 3;
	protected int level = 0;
	protected CompilationUnitTree compilationUnit;
	protected Trees trees;
	protected boolean visitingVariable = false;
	
	
	public SignatureCollectorVisitor(Trees trees) {
		this.trees = trees;
	}
	
	protected void visitingVariable(boolean value) {
		this.visitingVariable = value;
	}
	
	
	protected void incLevel() {
		this.level += increment;
	}
	
	protected void decLevel() {
		this.level -= this.level==0?0:this.increment;
	}

    protected Void defaultAction(Tree node, Environment env) {
        return null;
    }

    public final Void visit(Tree node, Environment env) {
        return (node == null) ? null : node.accept(this, env);
    }

    public final Void visit(Iterable<? extends Tree> nodes, Environment env) {
        Void r = null;
        if (nodes != null)
            for (Tree node : nodes)
                r = visit(node, env);
        return r;
    }

    public Void visitCompilationUnit(CompilationUnitTree node, Environment env) {
    	this.compilationUnit = node;
    	env.addEntry(node);
        List<? extends AnnotationTree> annotations = node.getPackageAnnotations();
        if (annotations != null && !annotations.isEmpty()) {
        	for (AnnotationTree ann : annotations) {
        		ann.accept(this, env);
        	}
        }
        if (node.getPackageName() != null) {
        	node.getPackageName().accept(this, env);
        }
        List<? extends ImportTree> imports = node.getImports();
        if (imports != null && !imports.isEmpty()) {
        	for (ImportTree imp : imports) {
        		imp.accept(this, env);
        	}
        }
        if (node.getTypeDecls() != null) {
        	if (!node.getTypeDecls().isEmpty()) {
        		List<? extends Tree> decls = node.getTypeDecls();
        		for (Tree decl : decls) {
        			decl.accept(this, env);
        		}
        	}
        }
    	return defaultAction(node, env);
    }

    public Void visitImport(ImportTree node, Environment env) {
    	env.addEntry(this.compilationUnit, node);
        return defaultAction(node, env);
    }

    public Void visitClass(ClassTree node, Environment env) {
        env.addEntry(this.compilationUnit, node);
        Environment classEnv = new Environment(env);
        List<? extends Tree> members = node.getMembers();
        if (members != null && !members.isEmpty()) {
            incLevel();
        	for (Tree m : members) {
        		m.accept(this, classEnv);
        	}
        	decLevel();
        }
    	return defaultAction(node, env);
    }

    public Void visitMethod(MethodTree node, Environment env) {
    	env.addEntry(this.compilationUnit, node);
    	Environment methodEnv = new Environment(env);
        List<? extends VariableTree> params = node.getParameters();
        if (params != null && !params.isEmpty()) {
        	for (int p = 0; p < params.size(); p++) {
        		params.get(p).accept(this, methodEnv);
        	}
        }
        if (node.getBody() != null) {
        	node.getBody().accept(this, methodEnv);
        }
        return defaultAction(node, env);
    }

    public Void visitVariable(VariableTree node, Environment env) {
    	env.addEntry(this.compilationUnit, node);
    	env.addEntry(this.compilationUnit, node.getType());
    	if (node.getInitializer() != null) {
    		node.getInitializer().accept(this, env);
    	}
    	return defaultAction(node, env);
    }

    public Void visitEmptyStatement(EmptyStatementTree node, Environment env) {
    	return defaultAction(node, env);
    }

    public Void visitBlock(BlockTree node, Environment env) {
        List<? extends StatementTree> statements = node.getStatements();
        if (statements != null && !statements.isEmpty()) {
        	incLevel();
        	for (int st = 0; st < statements.size(); st++) {
        		Environment stmtEnv = new Environment(env.getLeafEnvironment());
        		statements.get(st).accept(this, stmtEnv);
        	}
        	decLevel();
        }
    	return defaultAction(node, env);
    }

    public Void visitDoWhileLoop(DoWhileLoopTree node, Environment env) {
        Environment whileEnv = new Environment(env);
    	if (node.getStatement().getKind().compareTo(Kind.BLOCK) == 0) {
        	node.getStatement().accept(this, whileEnv);
        } else {
        	incLevel();
        	Environment stmtEnv = new Environment(whileEnv);
        	node.getStatement().accept(this, stmtEnv);
        	decLevel();
        }
        node.getCondition().accept(this, whileEnv.getLeafEnvironment());
    	return defaultAction(node, env);
    }

    public Void visitWhileLoop(WhileLoopTree node, Environment env) {
    	Environment whileEnv = new Environment(env);
    	node.getCondition().accept(this, whileEnv);
        if (node.getStatement().getKind().compareTo(Kind.BLOCK) == 0) {
        	node.getStatement().accept(this, whileEnv);
        } else {
        	incLevel();
        	Environment stmtEnv = new Environment(whileEnv);
        	node.getStatement().accept(this, stmtEnv);
        	decLevel();
        }
    	return defaultAction(node, env);
    }

    public Void visitForLoop(ForLoopTree node, Environment env) {
        Environment forEnv = new Environment(env);
    	List<? extends StatementTree> initializers = node.getInitializer();
        if (initializers != null && !initializers.isEmpty()) {
        	for (int ini = 0; ini < initializers.size(); ini++) {
        		Environment initEnv = new Environment(forEnv.getLeafEnvironment());
        		initializers.get(ini).accept(this, initEnv);
        	}
        }
        ExpressionTree condition = node.getCondition();
        if (condition != null) {
        	condition.accept(this, forEnv.getLeafEnvironment());
        }
        List<? extends ExpressionStatementTree> updates = node.getUpdate();
        if (updates != null && !updates.isEmpty()) {
        	for (int up = 0; up < updates.size(); up++) {
        		updates.get(up).accept(this, forEnv.getLeafEnvironment());
        	}
        }
        if (node.getStatement().getKind().compareTo(Kind.BLOCK) == 0) {
        	node.getStatement().accept(this, forEnv.getLeafEnvironment());
        } else {
        	incLevel();
        	Environment stmtEnv = new Environment(forEnv.getLeafEnvironment());
        	node.getStatement().accept(this, stmtEnv);
        	decLevel();
        }
    	return defaultAction(node, env);
    }

    public Void visitEnhancedForLoop(EnhancedForLoopTree node, Environment env) {
    	Environment forEnv = new Environment(env);
    	node.getVariable().accept(this, forEnv);
        node.getExpression().accept(this, forEnv);
        if (node.getStatement().getKind().compareTo(Kind.BLOCK) == 0) {
        	node.getStatement().accept(this, forEnv);
        } else {
        	incLevel();
        	Environment stmtEnv = new Environment(forEnv.getLeafEnvironment());
        	node.getStatement().accept(this, stmtEnv);
        	decLevel();
        }
    	return defaultAction(node, env);
    }

    public Void visitLabeledStatement(LabeledStatementTree node, Environment env) {
    	Environment labelEnv = new Environment(env);
    	if (node.getStatement().getKind().compareTo(Kind.BLOCK) == 0) {
        	node.getStatement().accept(this, labelEnv);
        } else {
        	incLevel();
        	node.getStatement().accept(this, labelEnv);
        	decLevel();
        }
    	return defaultAction(node, env);
    }

    public Void visitSwitch(SwitchTree node, Environment env) {
        Environment switchEnv = new Environment(env);
    	node.getExpression().accept(this, switchEnv);
        List<? extends CaseTree> cases = node.getCases();
        if (cases != null && !cases.isEmpty()) {
        	incLevel();
        	for (int c = 0; c < cases.size(); c++) {
        		cases.get(c).accept(this, switchEnv);
        	}
        	decLevel();
        }
    	return defaultAction(node, env);
    }

    public Void visitCase(CaseTree node, Environment env) {
        Environment caseEnv = new Environment(env.getLeafEnvironment());
    	if (node.getExpression() != null) {	
        	node.getExpression().accept(this, caseEnv);
        }
        List<? extends StatementTree> statements = node.getStatements();
        if (statements != null && !statements.isEmpty()) {
        	if (statements.size() == 1) {
        		Environment stmtEnv = new Environment(caseEnv.getLeafEnvironment());
        		statements.get(0).accept(this, stmtEnv);
        	} else {
        		incLevel();
        		for (int s = 0; s < statements.size(); s++) {
        			Environment stmtEnv = new Environment(caseEnv.getLeafEnvironment());
        			statements.get(s).accept(this, stmtEnv);
        		}
        		decLevel();	
        	}
        }
    	return defaultAction(node, env);
    }

    public Void visitSynchronized(SynchronizedTree node, Environment env) {
        Environment syncEnv = new Environment(env);
    	node.getExpression().accept(this, syncEnv);
        node.getBlock().accept(this, syncEnv);
    	return defaultAction(node, env);
    }

    public Void visitTry(TryTree node, Environment env) {
        node.getBlock().accept(this, env);
        List<? extends CatchTree> catches = node.getCatches();
        if (catches != null && !catches.isEmpty()) {
        	for (int c = 0; c < catches.size(); c++) {
        		catches.get(c).accept(this, env.getLeafEnvironment());
        	}
        }
        if (node.getFinallyBlock() != null) {
        	node.getFinallyBlock().accept(this, env.getLeafEnvironment());
        }
    	return defaultAction(node, env);
    }

    public Void visitCatch(CatchTree node, Environment env) {
    	Environment catchEnv = new Environment(env);
        node.getParameter().accept(this, catchEnv);
        node.getBlock().accept(this, catchEnv);
    	return defaultAction(node, env);
    }

    public Void visitConditionalExpression(ConditionalExpressionTree node, Environment env) {
    	node.getCondition().accept(this, env);
    	node.getTrueExpression().accept(this, env);
    	node.getFalseExpression().accept(this, env);
    	return defaultAction(node, env);
    }

    public Void visitIf(IfTree node, Environment env) {
    	Environment ifEnv = new Environment(env);
    	node.getCondition().accept(this, env);
    	if (node.getThenStatement() != null && node.getThenStatement().getKind().compareTo(Kind.BLOCK) == 0) {
        	node.getThenStatement().accept(this, ifEnv);
        } else if (node.getThenStatement() != null) {
        	incLevel();
        	Environment stmtEnv = new Environment(ifEnv.getLeafEnvironment());
        	node.getThenStatement().accept(this, stmtEnv);
        	decLevel();
        }
    	Environment elseEnv = node.getElseStatement()!=null?new Environment(ifEnv.getLeafEnvironment()):null;
    	if (node.getElseStatement() != null && node.getElseStatement().getKind().compareTo(Kind.BLOCK) == 0) {
        	node.getElseStatement().accept(this, elseEnv);
        } else if (node.getElseStatement() != null) {
        	incLevel();
        	node.getElseStatement().accept(this, elseEnv);
        	decLevel();
        }
        return defaultAction(node, env);
    }

    public Void visitExpressionStatement(ExpressionStatementTree node, Environment env) {
        node.getExpression().accept(this, env);
    	return defaultAction(node, env);
    }

    public Void visitBreak(BreakTree node, Environment env) {
        return defaultAction(node, env);
    }

    public Void visitContinue(ContinueTree node, Environment env) {
    	return defaultAction(node, env);
    }

    public Void visitReturn(ReturnTree node, Environment env) {
        if (node.getExpression() != null) {	
        	node.getExpression().accept(this, env);
        }
    	return defaultAction(node, env);
    }

    public Void visitThrow(ThrowTree node, Environment env) {
        node.getExpression().accept(this, env);
    	return defaultAction(node, env);
    }

    public Void visitAssert(AssertTree node, Environment env) {
        node.getCondition().accept(this, env);
        if (node.getDetail() != null) {
        	node.getDetail().accept(this, env);
        }
    	return defaultAction(node, env);
    }

    public Void visitMethodInvocation(MethodInvocationTree node, Environment env) {
    	Environment methodInvocationEnv = new Environment(env);
    	List<? extends Tree> typeArguments = node.getTypeArguments();
    	if (typeArguments != null && !typeArguments.isEmpty()) {
    		for (int ta = 0; ta < typeArguments.size(); ta++) {
    			typeArguments.get(ta).accept(this, methodInvocationEnv.getLeafEnvironment());
    		}
    	}
    	node.getMethodSelect().accept(this, methodInvocationEnv.getLeafEnvironment());
    	List<? extends ExpressionTree> params = node.getArguments();
    	if (params != null && !params.isEmpty()) {
    		for (int param = 0; param < params.size(); param++) {
    			params.get(param).accept(this, methodInvocationEnv.getLeafEnvironment());
    		}
    	}
    	return defaultAction(node, env);
    }

    public Void visitNewClass(NewClassTree node, Environment env) {
        Environment newClassEnv = new Environment(env);
    	if (node.getEnclosingExpression() != null) {
        	ExpressionTree enclosingExpression = node.getEnclosingExpression();
        	enclosingExpression.accept(this, newClassEnv);
        }
        node.getIdentifier().accept(this, newClassEnv);
        List<? extends Tree> typeArguments = node.getTypeArguments();
        if (typeArguments != null && !typeArguments.isEmpty()) {
        	for (int ta = 0; ta < typeArguments.size(); ta++) {
        		typeArguments.get(ta).accept(this, newClassEnv.getLeafEnvironment());
        	}	
        }
        List<? extends ExpressionTree> arguments = node.getArguments();
        if (arguments != null && !arguments.isEmpty()) {
        	for (int a = 0; a < arguments.size(); a++) {
        		arguments.get(a).accept(this, newClassEnv.getLeafEnvironment());
        	}
        } 
        Environment bodyEnv = node.getClassBody()==null?null:new Environment(newClassEnv.getLeafEnvironment());
        if (node.getClassBody() != null) {
        	incLevel();
        	node.getClassBody().accept(this, bodyEnv.getLeafEnvironment());
        	decLevel();
        }
    	return defaultAction(node, env);
    }

    public Void visitNewArray(NewArrayTree node, Environment env) {
        Environment newArrayEnv = new Environment(env);
    	node.getType().accept(this, newArrayEnv);
        List<? extends ExpressionTree> dimensions = node.getDimensions();
        if (dimensions != null && !dimensions.isEmpty()) {
        	for (int dim = 0; dim < dimensions.size(); dim++) {
        		dimensions.get(dim).accept(this, newArrayEnv);
        	}
        }
        List<? extends ExpressionTree> initializers = node.getInitializers();
        for (int ini = 0; ini < initializers.size(); ini++) {
        	initializers.get(ini).accept(this, newArrayEnv);
        }
    	return defaultAction(node, newArrayEnv);
    }

    public Void visitParenthesized(ParenthesizedTree node, Environment env) {
        node.getExpression().accept(this, env);
    	return defaultAction(node, env);
    }

    public Void visitAssignment(AssignmentTree node, Environment env) {
    	node.getVariable().accept(this, env);
    	node.getExpression().accept(this, env);
    	return defaultAction(node, env);
    }

    public Void visitCompoundAssignment(CompoundAssignmentTree node, Environment env) {
    	node.getVariable().accept(this, env);
    	switch (node.getKind()) {
			case AND_ASSIGNMENT: {
				break;
			}
			case ASSIGNMENT: {
				break;
			}
			case DIVIDE_ASSIGNMENT: {
				
				break;
			}
			case LEFT_SHIFT_ASSIGNMENT: {
				break;
			}
			case MINUS_ASSIGNMENT: {
				break;
			}
			case MULTIPLY_ASSIGNMENT: {
				break;
			}
			case OR_ASSIGNMENT: {
				break;
			}
			case PLUS_ASSIGNMENT: {
				break;
			}
			case REMAINDER_ASSIGNMENT: {
				break;
			}
			case RIGHT_SHIFT_ASSIGNMENT: {
				break;
			}
			case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT: {
				break;
			}
			case XOR_ASSIGNMENT: {
				break;
			}
			default: {
				break;
			}
    	}
    	node.getExpression().accept(this, env);
    	return defaultAction(node, env);
    }

    public Void visitUnary(UnaryTree node, Environment env) {
        switch(node.getKind()) {
			case BITWISE_COMPLEMENT: {
				break;
			}
			case LOGICAL_COMPLEMENT: {
				break;
			}
			case POSTFIX_DECREMENT: {
				break;
			}
			case POSTFIX_INCREMENT: {
				break;
			}
			case PREFIX_DECREMENT: {
				break;
			}
			case PREFIX_INCREMENT: {
				break;
			}
			case UNARY_MINUS: {
				break;
			}
			case UNARY_PLUS: {
				break;
			}
			default:
				break;
        }
        node.getExpression().accept(this, env);
    	return defaultAction(node, env);
    }

    public Void visitBinary(BinaryTree node, Environment env) {
        node.getLeftOperand().accept(this, env);
        switch (node.getKind()) {
			case AND: {
				break;
			}
			case CONDITIONAL_AND: {
				break;
			}
			case CONDITIONAL_OR: {
				break;
			}
			case DIVIDE: {
				break;
			}
			case EQUAL_TO: {
				break;
			}
			case GREATER_THAN: {
				break;
			}
			case GREATER_THAN_EQUAL: {
				break;
			}
			case LEFT_SHIFT: {
				break;
			}
			case LESS_THAN: {
				break;
			}
			case LESS_THAN_EQUAL: {
				break;
			}
			case MINUS: {
				break;
			}
			case MULTIPLY: {
				break;
			}
			case NOT_EQUAL_TO: {
				break;
			}
			case OR: {
				break;
			}
			case PLUS: {
				break;
			}
			case REMAINDER: {
				break;
			}
			case RIGHT_SHIFT: {
				break;
			}
			case UNSIGNED_RIGHT_SHIFT: {
				break;
			}
			case XOR: {
				break;
			}
			default:
				break;
        }
        node.getRightOperand().accept(this, env);
    	return defaultAction(node, env);
    }

    public Void visitTypeCast(TypeCastTree node, Environment env) {
    	node.getType().accept(this, env);
    	node.getExpression().accept(this, env);
        return defaultAction(node, env);
    }

    public Void visitInstanceOf(InstanceOfTree node, Environment env) {
        node.getExpression().accept(this, env);
        node.getType().accept(this, env);
    	return defaultAction(node, env);
    }

    public Void visitArrayAccess(ArrayAccessTree node, Environment env) {
        node.getExpression().accept(this, env);  
        node.getIndex().accept(this, env);
    	return defaultAction(node, env);
    }

    public Void visitMemberSelect(MemberSelectTree node, Environment env) {
        node.getExpression().accept(this, env);
    	return defaultAction(node, env);
    }

    public Void visitIdentifier(IdentifierTree node, Environment env) {
        if (this.visitingVariable) {
        	env.addEntry(this.compilationUnit, node);
        }
    	return defaultAction(node, env);
    }

    public Void visitLiteral(LiteralTree node, Environment env) {
    	switch (node.getKind()) {
			case BOOLEAN_LITERAL: {
				break;
			}
			case CHAR_LITERAL: {
				break;
			}
			case DOUBLE_LITERAL: {
				break;
			}
			case FLOAT_LITERAL: {
				break;
			}
			case INT_LITERAL: {
				break;
			}
			case LONG_LITERAL: {
				break;
			}
			case NULL_LITERAL: {
				break;
			}
			case STRING_LITERAL: {
				break;
			}
			default:
				break;
    	}
        return defaultAction(node, env);
    }

    public Void visitPrimitiveType(PrimitiveTypeTree node, Environment env) {
    	return defaultAction(node, env);
    }

    public Void visitArrayType(ArrayTypeTree node, Environment env) {
        node.getType().accept(this, env);
    	return defaultAction(node, env);
    }

    public Void visitParameterizedType(ParameterizedTypeTree node, Environment env) {
    	Environment parameterizedType = new Environment(env);
    	node.getType().accept(this, parameterizedType);
        List<? extends Tree> typeParameters = node.getTypeArguments();
        if (typeParameters != null && !typeParameters.isEmpty()) {
        	for (int tp = 0; tp < typeParameters.size(); tp++) {
        		Environment paramType = new Environment(parameterizedType.getLeafEnvironment());
        		typeParameters.get(tp).accept(this, paramType);
        	}
        	
        }
    	return defaultAction(node, env);
    }

    public Void visitTypeParameter(TypeParameterTree node, Environment env) {
    	env.addEntry(this.compilationUnit, node);
    	List<? extends Tree> bounds = node.getBounds();
    	if (bounds != null && !bounds.isEmpty()) {
    		for (int bound = 0; bound < bounds.size(); bound++) {
    			Environment boundEnv = new Environment(env.getLeafEnvironment());
    			bounds.get(bound).accept(this, boundEnv);
    		}
    	}
        return defaultAction(node, env);
    }

    public Void visitWildcard(WildcardTree node, Environment env) {
    	Environment wildcardEnv = new Environment(env);
    	Tree bound = node.getBound();
    	boolean bounded = false;
    	if (bound != null) {
    		switch (node.getKind()) {
				case EXTENDS_WILDCARD: {
					bounded = true;
					break;
				}
				case SUPER_WILDCARD: {
					bounded = true;
					break;
				}
				case UNBOUNDED_WILDCARD: {
					break;
				}
				default: {
					break;
				}
    		}
    	}
    	if (bounded && node.getBound() != null) {
    		node.getBound().accept(this, wildcardEnv);
    	}
        return defaultAction(node, env);
    }

    public Void visitModifiers(ModifiersTree node, Environment env) {
        return defaultAction(node, env);
    }

    public Void visitAnnotation(AnnotationTree node, Environment env) {
    	return defaultAction(node, env);
    }

    public Void visitErroneous(ErroneousTree node, Environment env) {
        return defaultAction(node, env);
    }

    public Void visitOther(Tree node, Environment env) {
        return defaultAction(node, env);
    }


	@Override
	public Void visitUnionType(UnionTypeTree node, Environment env) {
		return defaultAction(node, env);
	}
}
