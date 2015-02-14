/*
 * Copyright 2005 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package visitor;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Modifier;

import com.sun.source.tree.*;
import com.sun.source.tree.Tree.Kind;

/**
 * Prints an AST to console
 *
 * @author Simon Emmanuel Gutierrez Brida
 * @since 0.2
 */
public class PrintVisitor implements TreeVisitor<Void,Void> {
	
	protected final int increment = 3;
	protected int level = 0;
	protected final String tab = " ";
	
	
	protected void incLevel() {
		this.level += increment;
	}
	
	protected void decLevel() {
		this.level -= this.level==0?0:this.increment;
	}
	
	protected void printTab() {
		for (int currLevel = 0; currLevel < level; currLevel++) {
			System.out.print(this.tab);
		}
	}
	
	protected void print(String value, boolean useTab) {
		if (useTab) {
			printTab();
		}
		System.out.print(value);
	}
	
	protected void print(String value) {
		print(value, true);
	}
	
	protected void newLine() {
		System.out.println();
	}

    protected Void defaultAction(Tree node, Void Void) {
        return null;
    }

    public final Void visit(Tree node, Void Void) {
        return (node == null) ? null : node.accept(this, Void);
    }

    public final Void visit(Iterable<? extends Tree> nodes, Void Void) {
        Void r = null;
        if (nodes != null)
            for (Tree node : nodes)
                r = visit(node, Void);
        return r;
    }

    public Void visitCompilationUnit(CompilationUnitTree node, Void Void) {
        List<? extends AnnotationTree> annotations = node.getPackageAnnotations();
        if (annotations != null && !annotations.isEmpty()) {
        	for (AnnotationTree ann : annotations) {
        		ann.accept(this, Void);
        		newLine();
        	}
        }
        if (node.getPackageName() != null) {
        	print("package ", false);
        	node.getPackageName().accept(this, Void);
        	print(";", false);
        	newLine();
        	newLine();
        }
        List<? extends ImportTree> imports = node.getImports();
        if (imports != null && !imports.isEmpty()) {
        	for (ImportTree imp : imports) {
        		imp.accept(this, Void);
        		newLine();
        	}
        }
        if (node.getTypeDecls() != null) {
        	if (!node.getTypeDecls().isEmpty()) {
        		List<? extends Tree> decls = node.getTypeDecls();
        		for (Tree decl : decls) {
        			decl.accept(this, Void);
        			newLine();
        		}
        	}
        }
    	return defaultAction(node, Void);
    }

    public Void visitImport(ImportTree node, Void Void) {
    	if (node.isStatic()) {
    		print("static ");
    	}
    	print("import ", !node.isStatic());
    	node.getQualifiedIdentifier().accept(this, Void);
    	print(";", false);
        return defaultAction(node, Void);
    }

    public Void visitClass(ClassTree node, Void Void) {
        print(""); //prints tab
        ModifiersTree modifiers = node.getModifiers();
        modifiers.accept(this, Void);
        print("class ", false);
        print(node.getSimpleName() + " ", false);
        List<? extends TypeParameterTree> typeParams = node.getTypeParameters();
        if (typeParams != null && !typeParams.isEmpty()) {
        	print("<", false);
        	for (int tp = 0; tp < typeParams.size(); tp++) {
        		typeParams.get(tp).accept(this, Void);
        		if (tp + 1 < typeParams.size()) {
        			print(", ", false);
        		}
        	}
        	print("> ", false);
        }
        if (node.getExtendsClause() != null) {
        	print("extends ", false);
        	node.getExtendsClause().accept(this, Void);
        	print(" ", false);
        }
        List<? extends Tree> implementsClause = node.getImplementsClause();
        if (implementsClause != null && !implementsClause.isEmpty()) {
        	print("implements ", false);
        	for (int ic = 0; ic < implementsClause.size(); ic++) {
        		implementsClause.get(ic).accept(this, Void);
        		if (ic + 1 < implementsClause.size()) {
        			print(", ", false);
        		}
        	}
        }
        print("{ ", false);
        List<? extends Tree> members = node.getMembers();
        if (members != null && !members.isEmpty()) {
        	newLine();
        	newLine();
            incLevel();
        	for (Tree m : members) {
        		print(""); //prints tab
        		m.accept(this, Void);
        		newLine();
        	}
        	decLevel();
        	newLine();
            print("}");
        } else {
            print("}", false);
        }
    	return defaultAction(node, Void);
    }

    public Void visitMethod(MethodTree node, Void Void) {
    	ModifiersTree modifiers = node.getModifiers();
        modifiers.accept(this, Void);
        List<? extends TypeParameterTree> typeParameters = node.getTypeParameters();
        if (typeParameters != null && !typeParameters.isEmpty()) {
        	print("<", false);
        	for (int tp = 0; tp < typeParameters.size(); tp++) {
        		typeParameters.get(tp).accept(this, Void);
        		if (tp + 1 < typeParameters.size()) {
        			print(", ", false);
        		}
        	}
        	print("> ", false);
        }
        if (node.getReturnType() != null) {
        	node.getReturnType().accept(this, Void);
        	print(" ", false);
        }
        print(node.getName() + "(", false);
        List<? extends VariableTree> params = node.getParameters();
        if (params != null && !params.isEmpty()) {
        	for (int p = 0; p < params.size(); p++) {
        		params.get(p).accept(this, Void);
        		if (p + 1 < params.size()) {
        			print(", ", false);
        		}
        	}
        }
        print(")", false);
        if (node.getDefaultValue() != null) {
        	print(" default ", false);
        	node.getDefaultValue().accept(this, Void);
        	print(";", false);
        } else if (node.getBody() != null) {
        	print(" ", false);
        	node.getBody().accept(this, Void);
        } else {
        	print(";", false);
        }
        return defaultAction(node, Void);
    }

    public Void visitVariable(VariableTree node, Void Void) {
    	node.getModifiers().accept(this, Void);
    	node.getType().accept(this, Void);
    	print(" ", false);
    	print(node.getName().toString(), false);
    	if (node.getInitializer() != null) {
    		print(" = ", false);
    		node.getInitializer().accept(this, Void);
    	}
    	return defaultAction(node, Void);
    }

    public Void visitEmptyStatement(EmptyStatementTree node, Void Void) {
        print(";");
    	return defaultAction(node, Void);
    }

    public Void visitBlock(BlockTree node, Void Void) {
        if (node.isStatic()) {
        	print("static ", false);
        }
        print("{", false);
        List<? extends StatementTree> statements = node.getStatements();
        if (statements != null && !statements.isEmpty()) {
        	incLevel();
        	newLine();
        	for (int st = 0; st < statements.size(); st++) {
        		print(""); //prints tab
        		statements.get(st).accept(this, Void);
        		newLine();
        	}
        	decLevel();
        	print("}");
        } else {
        	print("}", false);
        }
    	return defaultAction(node, Void);
    }

    public Void visitDoWhileLoop(DoWhileLoopTree node, Void Void) {
        print("do", false);
        if (node.getStatement().getKind().compareTo(Kind.BLOCK) == 0) {
        	print(" ", false);
        	node.getStatement().accept(this, Void);
        } else {
        	newLine();
        	incLevel();
        	print(""); //prints tab
        	node.getStatement().accept(this, Void);
        	decLevel();
        	newLine();
        }
        print("while (");
        node.getCondition().accept(this, Void);
        print(")", false);
        print(";", false);
    	return defaultAction(node, Void);
    }

    public Void visitWhileLoop(WhileLoopTree node, Void Void) {
        print("while (", false);
        node.getCondition().accept(this, Void);
        print(")", false);
        if (node.getStatement().getKind().compareTo(Kind.BLOCK) == 0) {
        	print(" ", false);
        	node.getStatement().accept(this, Void);
        } else {
        	newLine();
        	incLevel();
        	print(""); //prints tab
        	node.getStatement().accept(this, Void);
        	decLevel();
        }
    	return defaultAction(node, Void);
    }

    public Void visitForLoop(ForLoopTree node, Void Void) {
        print("for (", false);
        List<? extends StatementTree> initializers = node.getInitializer();
        if (initializers != null && !initializers.isEmpty()) {
        	for (int ini = 0; ini < initializers.size(); ini++) {
        		initializers.get(ini).accept(this, Void);
        		if (ini + 1 < initializers.size()) {
        			print(", ", false);
        		}
        	}
        }
        print(";", false);
        ExpressionTree condition = node.getCondition();
        if (condition != null) {
        	condition.accept(this, Void);
        }
        print(";", false);
        List<? extends ExpressionStatementTree> updates = node.getUpdate();
        if (updates != null && !updates.isEmpty()) {
        	for (int up = 0; up < updates.size(); up++) {
        		updates.get(up).accept(this, Void);
        		if (up + 1 < updates.size()) {
        			print(", ", false);
        		}
        	}
        }
        print(")", false);
        if (node.getStatement().getKind().compareTo(Kind.BLOCK) == 0) {
        	print(" ", false);
        	node.getStatement().accept(this, Void);
        	newLine();
        } else {
        	newLine();
        	incLevel();
        	print(""); //prints tab
        	node.getStatement().accept(this, Void);
        	decLevel();
        	newLine();
        }
    	return defaultAction(node, Void);
    }

    public Void visitEnhancedForLoop(EnhancedForLoopTree node, Void Void) {
        print("for (", false);
        node.getVariable().accept(this, Void);
        print(" : ", false);
        node.getExpression().accept(this, Void);
        print(")", false);
        if (node.getStatement().getKind().compareTo(Kind.BLOCK) == 0) {
        	print(" ", false);
        	node.getStatement().accept(this, Void);
        	newLine();
        } else {
        	newLine();
        	incLevel();
        	print(""); //prints tab
        	node.getStatement().accept(this, Void);
        	decLevel();
        	newLine();
        }
    	return defaultAction(node, Void);
    }

    public Void visitLabeledStatement(LabeledStatementTree node, Void Void) {
    	print(node.getLabel() + " : ", false);
    	if (node.getStatement().getKind().compareTo(Kind.BLOCK) == 0) {
        	node.getStatement().accept(this, Void);
        	newLine();
        } else {
        	newLine();
        	incLevel();
        	print(""); //prints tab
        	node.getStatement().accept(this, Void);
        	decLevel();
        	newLine();
        }
    	return defaultAction(node, Void);
    }

    public Void visitSwitch(SwitchTree node, Void Void) {
        print("switch (", false);
        node.getExpression().accept(this, Void);
        print(") {", false);
        List<? extends CaseTree> cases = node.getCases();
        if (cases != null && !cases.isEmpty()) {
        	incLevel();
        	newLine();
        	for (int c = 0; c < cases.size(); c++) {
        		print(""); //prints tab
        		cases.get(c).accept(this, Void);
        		newLine();
        	}
        	decLevel();
        }
        print("}");
    	return defaultAction(node, Void);
    }

    public Void visitCase(CaseTree node, Void Void) {
        if (node.getExpression() != null) {
        	print("case ", false);
        	node.getExpression().accept(this, Void);
        } else {
        	print("default ", false);
        }
        print(": ", false);
        List<? extends StatementTree> statements = node.getStatements();
        if (statements != null && !statements.isEmpty()) {
        	if (statements.size() == 1) {
        		statements.get(0).accept(this, Void);
        	} else {
        		print("{", false);
        		newLine();
        		incLevel();
        		for (int s = 0; s < statements.size(); s++) {
        			print(""); //prints tab
        			statements.get(s).accept(this, Void);
        			if (s + 1 < statements.size()) {
        				newLine();
        			}
        		}
        		decLevel();
        		print("}");
        	}
        }
    	return defaultAction(node, Void);
    }

    public Void visitSynchronized(SynchronizedTree node, Void Void) {
        print("synchronized (", false);
        node.getExpression().accept(this, Void);
        print(") ", false);
        node.getBlock().accept(this, Void);
        newLine();
    	return defaultAction(node, Void);
    }

    public Void visitTry(TryTree node, Void Void) {
        print("try ", false);
        node.getBlock().accept(this, Void);
        List<? extends CatchTree> catches = node.getCatches();
        if (catches != null && !catches.isEmpty()) {
        	for (int c = 0; c < catches.size(); c++) {
        		print(""); //prints tab
        		catches.get(c).accept(this, Void);
        		if (c + 1 < catches.size()) {
        			newLine();
        		}
        	}
        }
        if (node.getFinallyBlock() != null) {
        	print("finally ", false);
        	node.getFinallyBlock().accept(this, Void);
        }
    	return defaultAction(node, Void);
    }

    public Void visitCatch(CatchTree node, Void Void) {
        print("catch (", false);
        node.getParameter().accept(this, Void);
        print(") ", false);
        node.getBlock().accept(this, Void);
    	return defaultAction(node, Void);
    }

    public Void visitConditionalExpression(ConditionalExpressionTree node, Void Void) {
    	node.getCondition().accept(this, Void);
    	print(" ? ", false);
    	node.getTrueExpression().accept(this, Void);
    	print(" : ", false);
    	node.getFalseExpression().accept(this, Void);
    	return defaultAction(node, Void);
    }

    public Void visitIf(IfTree node, Void Void) {
    	print("if (", false);
    	node.getCondition().accept(this, Void);
    	print(")", false);
    	if (node.getThenStatement() != null && node.getThenStatement().getKind().compareTo(Kind.BLOCK) == 0) {
        	print(" ", false);
        	node.getThenStatement().accept(this, Void);
        } else if (node.getThenStatement() != null) {
        	newLine();
        	incLevel();
        	print(""); //prints tab
        	node.getThenStatement().accept(this, Void);
        	decLevel();
        	newLine();
        }
    	if (node.getElseStatement() != null && node.getElseStatement().getKind().compareTo(Kind.BLOCK) == 0) {
        	print(" else ", false);
        	node.getElseStatement().accept(this, Void);
        } else if (node.getElseStatement() != null) {
        	print(" else", false);
        	newLine();
        	incLevel();
        	print(""); //prints tab
        	node.getElseStatement().accept(this, Void);
        	decLevel();
        	newLine();
        }
        return defaultAction(node, Void);
    }

    public Void visitExpressionStatement(ExpressionStatementTree node, Void Void) {
        node.getExpression().accept(this, Void);
        print(";", false);
    	return defaultAction(node, Void);
    }

    public Void visitBreak(BreakTree node, Void Void) {
    	print("break", false);
    	if (node.getLabel() != null) {
    		print(" " + node.getLabel(), false);
    	}
    	print(";", false);
        return defaultAction(node, Void);
    }

    public Void visitContinue(ContinueTree node, Void Void) {
    	print("continue", false);
    	if (node.getLabel() != null) {
    		print(" " + node.getLabel(), false);
    	}
    	print(";", false);
    	return defaultAction(node, Void);
    }

    public Void visitReturn(ReturnTree node, Void Void) {
        print("return", false);
        if (node.getExpression() != null) {
        	print(" ", false);
        	node.getExpression().accept(this, Void);
        }
        print(";", false);
    	return defaultAction(node, Void);
    }

    public Void visitThrow(ThrowTree node, Void Void) {
        print("throw ", false);
        node.getExpression().accept(this, Void);
        print(";", false);
    	return defaultAction(node, Void);
    }

    public Void visitAssert(AssertTree node, Void Void) {
        print("assert ", false);
        node.getCondition().accept(this, Void);
        if (node.getDetail() != null) {
        	print(" : ", false);
        	node.getDetail().accept(this, Void);
        }
        print(";", false);
    	return defaultAction(node, Void);
    }

    public Void visitMethodInvocation(MethodInvocationTree node, Void Void) {
    	List<? extends Tree> typeArguments = node.getTypeArguments();
    	if (typeArguments != null && !typeArguments.isEmpty()) {
    		for (int ta = 0; ta < typeArguments.size(); ta++) {
    			typeArguments.get(ta).accept(this, Void);
    			print(".", false);
    		}
    	}
    	node.getMethodSelect().accept(this, Void);
    	print("(", false);
    	List<? extends ExpressionTree> params = node.getArguments();
    	if (params != null && !params.isEmpty()) {
    		for (int param = 0; param < params.size(); param++) {
    			params.get(param).accept(this, Void);
    			if (param + 1 < params.size()) {
    				print(", ", false);
    			}
    		}
    	}
    	print(")", false);
    	return defaultAction(node, Void);
    }

    public Void visitNewClass(NewClassTree node, Void Void) {
        if (node.getEnclosingExpression() != null) {
        	ExpressionTree enclosingExpression = node.getEnclosingExpression();
        	enclosingExpression.accept(this, Void);
        	print(".", false);
        }
        node.getIdentifier().accept(this, Void);
        List<? extends Tree> typeArguments = node.getTypeArguments();
        if (typeArguments != null && !typeArguments.isEmpty()) {
        	print("<", false);
        	for (int ta = 0; ta < typeArguments.size(); ta++) {
        		typeArguments.get(ta).accept(this, Void);
        		if (ta + 1 < typeArguments.size()) {
        			print(", ", false);
        		}
        	}
        	print(">", false);
        }
        print("(", false);
        List<? extends ExpressionTree> arguments = node.getArguments();
        if (arguments != null && !arguments.isEmpty()) {
        	for (int a = 0; a < arguments.size(); a++) {
        		arguments.get(a).accept(this, Void);
        		if (a + 1 < arguments.size()) {
        			print(", ", false);
        		}
        	}
        }
        print(")", false);
        if (node.getClassBody() != null) {
        	print(" {", false);							//+++++Maybe {} are not needed
        	incLevel();									//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        	newLine();									//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        	node.getClassBody().accept(this, Void);		//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        	newLine();									//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        	decLevel();									//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        	print("}");									//-----------------------------
        }
    	return defaultAction(node, Void);
    }

    public Void visitNewArray(NewArrayTree node, Void Void) {
        print("new ", false);
        node.getType().accept(this, Void);
        print("[", false);
        List<? extends ExpressionTree> dimensions = node.getDimensions();
        if (dimensions != null && !dimensions.isEmpty()) {
        	for (int dim = 0; dim < dimensions.size(); dim++) {
        		dimensions.get(dim).accept(this, Void);
        		if (dim + 1 < dimensions.size()) {
        			print(", ", false);
        		}
        	}
        }
        List<? extends ExpressionTree> initializers = node.getInitializers();
        print("{", false);
        for (int ini = 0; ini < initializers.size(); ini++) {
        	initializers.get(ini).accept(this, Void);
        	if (ini + 1 < initializers.size()) {
        		print(", ", false);
        	}
        }
        print("}", false);
    	return defaultAction(node, Void);
    }

    public Void visitParenthesized(ParenthesizedTree node, Void Void) {
        print("(", false);
        node.getExpression().accept(this, Void);
        print(")", false);
    	return defaultAction(node, Void);
    }

    public Void visitAssignment(AssignmentTree node, Void Void) {
    	node.getVariable().accept(this, Void);
    	print(" = ", false);
    	node.getExpression().accept(this, Void);
    	return defaultAction(node, Void);
    }

    public Void visitCompoundAssignment(CompoundAssignmentTree node, Void Void) {
    	node.getVariable().accept(this, Void);
    	switch (node.getKind()) {
			case AND_ASSIGNMENT: {
				print(" &= ", false);
				break;
			}
			case ASSIGNMENT: {
				print(" = ", false);
				break;
			}
			case DIVIDE_ASSIGNMENT: {
				print(" /= ", false);
				break;
			}
			case LEFT_SHIFT_ASSIGNMENT: {
				print(" <<= ", false);
				break;
			}
			case MINUS_ASSIGNMENT: {
				print(" -= ", false);
				break;
			}
			case MULTIPLY_ASSIGNMENT: {
				print(" -= ", false);
				break;
			}
			case OR_ASSIGNMENT: {
				print(" |= ", false);
				break;
			}
			case PLUS_ASSIGNMENT: {
				print(" += ", false);
				break;
			}
			case REMAINDER_ASSIGNMENT: {
				print(" %= ", false);
				break;
			}
			case RIGHT_SHIFT_ASSIGNMENT: {
				print(" >>= ", false);
				break;
			}
			case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT: {
				print(" >>>= ", false);
				break;
			}
			case XOR_ASSIGNMENT: {
				print(" ^= ", false);
				break;
			}
			default: {
				print(" =WAT DA WAT?= ", false);
				break;
			}
    	}
    	node.getExpression().accept(this, Void);
    	return defaultAction(node, Void);
    }

    public Void visitUnary(UnaryTree node, Void Void) {
        boolean isPostfix = false;
        String operator = "=WAT DA WAT?=";
        switch(node.getKind()) {
			case BITWISE_COMPLEMENT: {
				operator = "~";
				break;
			}
			case LOGICAL_COMPLEMENT: {
				operator = "!";
				break;
			}
			case POSTFIX_DECREMENT: {
				isPostfix = true;
				operator = "--";
				break;
			}
			case POSTFIX_INCREMENT: {
				isPostfix = true;
				operator = "++";
				break;
			}
			case PREFIX_DECREMENT: {
				operator = "--";
				break;
			}
			case PREFIX_INCREMENT: {
				operator = "++";
				break;
			}
			case UNARY_MINUS: {
				operator = "-";
				break;
			}
			case UNARY_PLUS: {
				operator = "+";
				break;
			}
			default:
				break;
        }
        if (isPostfix) {
        	node.getExpression().accept(this, Void);
        	print(operator, false);
        } else {
        	print(operator, false);
        	node.getExpression().accept(this, Void);
        }
    	return defaultAction(node, Void);
    }

    public Void visitBinary(BinaryTree node, Void Void) {
        node.getLeftOperand().accept(this, Void);
        String op = "=WAT DA WAT?=";
        switch (node.getKind()) {
			case AND: {
				op = " && ";
				break;
			}
			case CONDITIONAL_AND: {
				op = " & ";
				break;
			}
			case CONDITIONAL_OR: {
				op = " | ";
				break;
			}
			case DIVIDE: {
				op = " / ";
				break;
			}
			case EQUAL_TO: {
				op = " == ";
				break;
			}
			case GREATER_THAN: {
				op = " > ";
				break;
			}
			case GREATER_THAN_EQUAL: {
				op = " >= ";
				break;
			}
			case LEFT_SHIFT: {
				op = " << ";
				break;
			}
			case LESS_THAN: {
				op = " < ";
				break;
			}
			case LESS_THAN_EQUAL: {
				op = " <= ";
				break;
			}
			case MINUS: {
				op = " - ";
				break;
			}
			case MULTIPLY: {
				op = " * ";
				break;
			}
			case NOT_EQUAL_TO: {
				op = " != ";
				break;
			}
			case OR: {
				op = " || ";
				break;
			}
			case PLUS: {
				op = " + ";
				break;
			}
			case REMAINDER: {
				op = " % ";
				break;
			}
			case RIGHT_SHIFT: {
				op = " >> ";
				break;
			}
			case UNSIGNED_RIGHT_SHIFT: {
				op = " >>> ";
				break;
			}
			case XOR: {
				op = " ^ ";
				break;
			}
			default:
				break;
        }
        print(op, false);
        node.getRightOperand().accept(this, Void);
    	return defaultAction(node, Void);
    }

    public Void visitTypeCast(TypeCastTree node, Void Void) {
    	print("(", false);
    	node.getType().accept(this, Void);
    	print(") ", false);
    	node.getExpression().accept(this, Void);
        return defaultAction(node, Void);
    }

    public Void visitInstanceOf(InstanceOfTree node, Void Void) {
        node.getExpression().accept(this, Void);
        print(" instanceof ", false);
        node.getType().accept(this, Void);
    	return defaultAction(node, Void);
    }

    public Void visitArrayAccess(ArrayAccessTree node, Void Void) {
        node.getExpression().accept(this, Void);
        print("[", false);
        node.getIndex().accept(this, Void);
        print("]", false);
    	return defaultAction(node, Void);
    }

    public Void visitMemberSelect(MemberSelectTree node, Void Void) {
        node.getExpression().accept(this, Void);
        print("."+node.getIdentifier(), false);
    	return defaultAction(node, Void);
    }

    public Void visitIdentifier(IdentifierTree node, Void Void) {
    	print(node.getName().toString(), false);
        return defaultAction(node, Void);
    }

    public Void visitLiteral(LiteralTree node, Void Void) {
    	String literalValue = "=WAT DA WAT?=";
    	switch (node.getKind()) {
			case BOOLEAN_LITERAL: {
				literalValue = ((Boolean) node.getValue()).toString();
				break;
			}
			case CHAR_LITERAL: {
				literalValue = ((Character) node.getValue()).toString();
				break;
			}
			case DOUBLE_LITERAL: {
				literalValue = ((Double) node.getValue()).toString();
				break;
			}
			case FLOAT_LITERAL: {
				literalValue = ((Float) node.getValue()).toString();
				break;
			}
			case INT_LITERAL: {
				literalValue = ((Integer) node.getValue()).toString();
				break;
			}
			case LONG_LITERAL: {
				literalValue = ((Long) node.getValue()).toString();
				break;
			}
			case NULL_LITERAL: {
				literalValue = "null";
				break;
			}
			case STRING_LITERAL: {
				literalValue = ((String) node.getValue());
				break;
			}
			default:
				break;
    	}
    	print(literalValue, false);
        return defaultAction(node, Void);
    }

    public Void visitPrimitiveType(PrimitiveTypeTree node, Void Void) {
        print(node.getPrimitiveTypeKind().toString().toLowerCase(), false);
    	return defaultAction(node, Void);
    }

    public Void visitArrayType(ArrayTypeTree node, Void Void) {
        node.getType().accept(this, Void);
        print("[]", false);
    	return defaultAction(node, Void);
    }

    public Void visitParameterizedType(ParameterizedTypeTree node, Void Void) {
        node.getType().accept(this, Void);
        List<? extends Tree> typeParameters = node.getTypeArguments();
        if (typeParameters != null && !typeParameters.isEmpty()) {
        	print("<", false);
        	for (int tp = 0; tp < typeParameters.size(); tp++) {
        		typeParameters.get(tp).accept(this, Void);
        		if (tp + 1 < typeParameters.size()) {
        			print(", ", false);
        		}
        	}
        	print(">", false);
        }
    	return defaultAction(node, Void);
    }

    public Void visitTypeParameter(TypeParameterTree node, Void Void) {
    	print(node.getName().toString(), false);
    	List<? extends Tree> bounds = node.getBounds();
    	if (bounds != null && !bounds.isEmpty()) {
    		print(" extends ", false);
    		for (int bound = 0; bound < bounds.size(); bound++) {
    			bounds.get(bound).accept(this, Void);
    			if (bound + 1 < bounds.size()) {
    				print(" & ");
    			}
    		}
    	}
        return defaultAction(node, Void);
    }

    public Void visitWildcard(WildcardTree node, Void Void) {
    	Tree bound = node.getBound();
    	boolean bounded = false;
    	if (bound != null) {
    		switch (node.getKind()) {
				case EXTENDS_WILDCARD: {
					print("? extends ", false);
					bounded = true;
					break;
				}
				case SUPER_WILDCARD: {
					print("? super ", false);
					bounded = true;
					break;
				}
				case UNBOUNDED_WILDCARD: {
					print("?", false);
					break;
				}
				default: {
					print("=WAT DA WAT?=", false);
					break;
				}
    		}
    	}
    	if (bounded && node.getBound() != null) {
    		node.getBound().accept(this, Void);
    	}
        return defaultAction(node, Void);
    }

    public Void visitModifiers(ModifiersTree node, Void Void) {
    	Set<Modifier> flagsSet = node.getFlags();
    	List<Modifier> flags = flagsSet==null?null:new LinkedList<Modifier>();
    	if (flagsSet != null) flags.addAll(flagsSet);
    	if (flags != null && !flags.isEmpty()) {
    		for (int f = 0; f < flags.size(); f++) {
    			print(flags.get(f).toString(), false);
    			if (f + 1 < flags.size()) {
    				print(" ", false);
    			}
    		}
    		print(" ", false);
    	}
    	List<? extends AnnotationTree> annotations = node.getAnnotations();
    	if (annotations != null && !annotations.isEmpty()) {
    		print(" ", false);
    		for (int a = 0; a < annotations.size(); a++) {
    			annotations.get(a).accept(this, Void);
    			if (a + 1 < annotations.size()) {
    				print(" ", false);
    			}
    		}
    		print(" ", false);
    	}
        return defaultAction(node, Void);
    }

    public Void visitAnnotation(AnnotationTree node, Void Void) {
        print("@"+node.getAnnotationType().toString() + " ");
        if (node.getArguments() != null && !node.getArguments().isEmpty()) {
        	for (ExpressionTree arg : node.getArguments()) {
        		arg.accept(this, Void);
        	}
        }
    	return defaultAction(node, Void);
    }

    public Void visitErroneous(ErroneousTree node, Void Void) {
    	print("ERRONEUS CHABON!", false);
        return defaultAction(node, Void);
    }

    public Void visitOther(Tree node, Void Void) {
    	print("OTHER CHABON!", false);
        return defaultAction(node, Void);
    }

	@Override
	public Void visitUnionType(UnionTypeTree node, Void Void) {
		List<? extends Tree> alternatives = node.getTypeAlternatives();
		if (alternatives != null && !alternatives.isEmpty()) {
			for (int a = 0; a < alternatives.size(); a++) {
				alternatives.get(a).accept(this, Void);
				if (a + 1 < alternatives.size()) {
					print("|", false);
				}
			}
		}
		return defaultAction(node, Void);
	}
}
