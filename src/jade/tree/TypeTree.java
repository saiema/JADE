package jade.tree;

import jade.exceptions.ASTInternalException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This tree node represents a type, some examples are the following:
 * <p>
 * <li> a class like java.lang.Integer </li>
 * <li> a primitive type like int</li>
 * <li> an identifier like T</li>
 * <li> a parameterized type like Type<TypeParameters></li>
 * <li> a bounded type like T (implements Types)|(extends Bounds)|(super Type)</li>
 * <p>
 * 
 * @author Simon Emmanuel Gutierrez Brida
 * @version 0.1u
 * @see IdentifierTree
 * @see BoundsTree
 */
public class TypeTree extends Tree {
	private static enum Type {CLASS, PRIMITIVE, IDENTIFIER, PARAMETERIZED, BOUNDED, INVALID};
	private static enum BoundedType {IMPLEMENTS, EXTENDS, SUPER, NONE};
	
	protected Type type;
	protected Class<?> typeClass = null;
	protected BoundedType boundedType;

	protected TypeTree(Tree parent, Tree[] childs) {
		super(parent, childs);
		this.type = Type.INVALID;
		this.boundedType = BoundedType.NONE;
	}
	
	//+++++++++++Class/Primitive type constructors
	
	public TypeTree(Tree parent, Class<?> typeClass) {
		super(parent, null);
		this.type = typeClass.isPrimitive()?Type.PRIMITIVE:Type.CLASS;
		this.boundedType = BoundedType.NONE;
		this.typeClass = typeClass;
	}
	
	public TypeTree(Class<?> typeClass) {
		this(null, typeClass);
	}
	
	//-----------Class/Primitive type constructors
	
	//+++++++++++Identifier type constructors
	
	public TypeTree(Tree parent, IdentifierTree identifier) {
		super(parent, new Tree[]{identifier});
		this.type = Type.IDENTIFIER;
		this.boundedType = BoundedType.NONE;
	}
	
	public TypeTree(IdentifierTree identifier) {
		this(null, identifier);
	}
	
	//-----------Identifier type constructors
	
	//+++++++++++Parameterized/Bounded(implements) type constructors
	
	public TypeTree(Tree parent, TypeTree parameterizedType, TypeTree[] typeParameters, boolean parameterized) {
		super(parent, null);
		this.type = parameterized?Type.PARAMETERIZED:Type.BOUNDED;
		this.boundedType = parameterized?BoundedType.NONE:BoundedType.IMPLEMENTS;
		TypeTree[] contents = new TypeTree[(typeParameters==null?0:typeParameters.length) + 1];
		contents[0] = parameterizedType;
		if (typeParameters != null) {
			for (int tp = 0; tp < typeParameters.length; tp++) {
				contents[tp+1] = typeParameters[tp];
			}
		}
		this.childs = contents;
	}
	
	public TypeTree(Tree parent, TypeTree parameterizedType, List<TypeTree> typeParameters, boolean parameterized) {
		this(parent, parameterizedType, typeParameters==null?(TypeTree[])null:typeParameters.toArray(new TypeTree[typeParameters.size()]), parameterized);
	}
	
	public TypeTree(TypeTree parameterizedType, TypeTree[] typeParameters, boolean parameterized) {
		this(null, parameterizedType, typeParameters, parameterized);
	}
	
	public TypeTree(TypeTree parameterizedType, List<TypeTree> typeParameters, boolean parameterized) {
		this(null, parameterizedType, typeParameters, parameterized);
	}
	
	//-----------Parameterized/Bounded(implements) type constructors
	
	//+++++++++++Bounded(extends) type constructors
	
	public TypeTree(Tree parent, TypeTree boundedType, BoundsTree bounds) {
		super(parent, new Tree[]{boundedType, bounds});
		this.type = Type.BOUNDED;
		this.boundedType = BoundedType.EXTENDS;
	}
	
	public TypeTree(TypeTree boundedType, BoundsTree bounds) {
		this(null, boundedType, bounds);
	}
	
	//-----------Bounded(extends) type constructors
	
	//+++++++++++Bounded(super) type constructors
	
	public TypeTree(Tree parent, TypeTree boundedType, TypeTree superType) {
		super(parent, new Tree[]{boundedType, superType});
		this.type = Type.BOUNDED;
		this.boundedType = BoundedType.SUPER;
	}
	
	public TypeTree(TypeTree boundedType, TypeTree superType) {
		this(null, boundedType, superType);
	}
	
	//-----------Bounded(super) type constructors
	
	//+++++++++++Members type check
	
	public boolean isClass() {
		return this.type.equals(Type.CLASS);
	}
	
	public boolean isPrimitive() {
		return this.type.equals(Type.PRIMITIVE);
	}
	
	public boolean isIdentifier() {
		return this.type.equals(Type.IDENTIFIER);
	}
	
	public boolean isParameterized() {
		return this.type.equals(Type.PARAMETERIZED);
	}
	
	public boolean isBounded() {
		return this.type.equals(Type.BOUNDED);
	}
	
	public boolean isValid() {
		return !this.type.equals(Type.INVALID);
	}
	
	//-----------Members type check
	
	//+++++++++++Members bounded type check
	
	public boolean isImplementsBound() {
		return this.boundedType.equals(BoundedType.IMPLEMENTS);
	}
	
	public boolean isExtendsBound() {
		return this.boundedType.equals(BoundedType.EXTENDS);
	}
	
	public boolean isSuperBound() {
		return this.boundedType.equals(BoundedType.SUPER);
	}
	
	//-----------Members bounded type check
	
	//+++++++++++Members getters
	
	@SuppressWarnings("unchecked")
	public Class<?> getTypeClass() throws ASTInternalException {
		checkValidState("jade.tree.TypeTree#getClass()");
		checkExpectedState("jade.tree.TypeTree#getClass()", (Set<Type>) Arrays.asList(new Type[]{Type.CLASS, Type.PRIMITIVE}), (Set<BoundedType>) Arrays.asList(new BoundedType[]{BoundedType.NONE}));
		return this.typeClass;
	}
	
	@SuppressWarnings("unchecked")
	public IdentifierTree getIdentifier() throws ASTInternalException {
		checkValidState("jade.tree.TypeTree#getIdentifier()");
		checkExpectedState("jade.tree.TypeTree#getIdentifier()", (Set<Type>) Arrays.asList(new Type[]{Type.IDENTIFIER}), (Set<BoundedType>) Arrays.asList(new BoundedType[]{BoundedType.NONE}));
		return (IdentifierTree) getChild(0);
	}
	
	@SuppressWarnings("unchecked")
	public TypeTree getParameterizedType() throws ASTInternalException {
		checkValidState("jade.tree.TypeTree#getParameterizedType()");
		checkExpectedState("jade.tree.TypeTree#getParameterizedType()", (Set<Type>) Arrays.asList(new Type[]{Type.PARAMETERIZED}), (Set<BoundedType>) Arrays.asList(new BoundedType[]{BoundedType.NONE}));
		return (TypeTree) getChild(0);
	}
	
	@SuppressWarnings("unchecked")
	public TypeTree[] getTypeParameters() throws ASTInternalException {
		checkValidState("jade.tree.TypeTree#getTypeParameters()");
		checkExpectedState("jade.tree.TypeTree#getTypeParameters()", (Set<Type>) Arrays.asList(new Type[]{Type.PARAMETERIZED}), (Set<BoundedType>) Arrays.asList(new BoundedType[]{BoundedType.NONE}));
		return (TypeTree[]) getChilds(1, this.childs.length - 1);
	}
	
	@SuppressWarnings("unchecked")
	public TypeTree getBoundedType() throws ASTInternalException {
		checkValidState("jade.tree.TypeTree#getBoundedType()");
		checkExpectedState("jade.tree.TypeTree#getBoundedType()", (Set<Type>) Arrays.asList(new Type[]{Type.BOUNDED}), (Set<BoundedType>) Arrays.asList(new BoundedType[]{BoundedType.EXTENDS, BoundedType.IMPLEMENTS, BoundedType.SUPER}));
		return (TypeTree) getChild(0);
	}
	
	@SuppressWarnings("unchecked")
	public TypeTree[] getImplementedTypes() throws ASTInternalException {
		checkValidState("jade.tree.TypeTree#getImplementedTypes()");
		checkExpectedState("jade.tree.TypeTree#getImplementedTypes()", (Set<Type>) Arrays.asList(new Type[]{Type.PARAMETERIZED}), (Set<BoundedType>) Arrays.asList(new BoundedType[]{BoundedType.IMPLEMENTS}));
		return (TypeTree[]) getChilds(1, this.childs.length - 1);
	}
	
	@SuppressWarnings("unchecked")
	public BoundsTree getBounds() throws ASTInternalException {
		checkValidState("jade.tree.TypeTree#getBounds()");
		checkExpectedState("jade.tree.TypeTree#getBounds()", (Set<Type>) Arrays.asList(new Type[]{Type.BOUNDED}), (Set<BoundedType>) Arrays.asList(new BoundedType[]{BoundedType.EXTENDS}));
		return (BoundsTree) getChild(1);
	}
	
	@SuppressWarnings("unchecked")
	public TypeTree getSuperType() throws ASTInternalException {
		checkValidState("jade.tree.TypeTree#getSuperType()");
		checkExpectedState("jade.tree.TypeTree#getSuperType()", (Set<Type>) Arrays.asList(new Type[]{Type.BOUNDED}), (Set<BoundedType>) Arrays.asList(new BoundedType[]{BoundedType.EXTENDS}));
		return (TypeTree) getChild(1);
	}
	
	//-----------Members getters
	
	//+++++++++++Validators (private)
	
	private void checkValidState(String caller) throws ASTInternalException {
		if (!this.isValid()) {
			throw new ASTInternalException(caller, new IllegalStateException("This instance was not created using a public constructor. The current state is not legal"));
		}
	}
	
	private void checkExpectedState(String caller, Set<Type> expectedTypes, Set<BoundedType> expectedBoundedTypes) throws ASTInternalException {
		boolean expectedTypeValid = expectedTypes.contains(this.type);
		boolean expectedBoundedTypeValid = expectedBoundedTypes==null?true:expectedBoundedTypes.contains(this.boundedType);
		String expectedTypesAsString = "";
		String expectedBoundedTypesAsString = "";
		Iterator<Type> expectedTypesIt = expectedTypes.iterator();
		while (expectedTypesIt.hasNext()) {
			expectedTypesAsString += expectedTypesIt.next().toString();
			if (expectedTypesIt.hasNext()) {
				expectedTypesAsString += " or ";
			}
		}
		if (expectedBoundedTypes != null) {
			Iterator<BoundedType> expectedBoundedTypesIt = expectedBoundedTypes.iterator();
			while (expectedBoundedTypesIt.hasNext()) {
				expectedBoundedTypesAsString += expectedBoundedTypesIt.next().toString();
				if (expectedBoundedTypesIt.hasNext()) {
					expectedBoundedTypesAsString += " or ";
				}
			}
		}
		if (!expectedTypeValid && expectedBoundedTypeValid) {
			throw new ASTInternalException(caller, new IllegalStateException("Expecting type to be one of : " + expectedTypesAsString + " but got " + this.type.toString() + " instead"));
		} else if (!expectedTypeValid && !expectedBoundedTypeValid) {
			throw new ASTInternalException(caller, new IllegalStateException("Expecting type to be one of : " + expectedTypesAsString + " and bounded type to be one of : " + expectedBoundedTypesAsString + " but got " + this.type.toString() + " and " + this.boundedType.toString() + " instead"));
		} else if (expectedTypeValid && !expectedBoundedTypeValid) {
			throw new ASTInternalException(caller, new IllegalStateException("Expecting bounded type to be one of : " + expectedBoundedTypesAsString + " but got " + this.boundedType.toString() + " instead"));
		}
	}
	
	//-----------Validators (private)

}
