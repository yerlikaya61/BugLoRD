package se.de.hu_berlin.informatik.astlmbuilder.mapping;

import java.util.Collection;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.TypeParameter;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.AnnotationMemberDeclaration;
import com.github.javaparser.ast.body.BaseParameter;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EmptyMemberDeclaration;
import com.github.javaparser.ast.body.EmptyTypeDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.MultiTypeParameter;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.body.VariableDeclaratorId;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.InstanceOfExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.IntegerLiteralMinValueExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralMinValueExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.QualifiedNameExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.SuperExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.AssertStmt;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.LabeledStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.SynchronizedStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.TypeDeclarationStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.IntersectionType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.UnionType;
import com.github.javaparser.ast.type.UnknownType;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.type.WildcardType;

import se.de.hu_berlin.informatik.astlmbuilder.ElseStmt;
import se.de.hu_berlin.informatik.astlmbuilder.ExtendsStmt;
import se.de.hu_berlin.informatik.astlmbuilder.ImplementsStmt;

public interface ITokenMapper<T> {

	public static final String COMPILATION_UNIT = "COMP_UNIT";
	public static final String LINE_COMMENT = "LINE_COMMENT";
	public static final String BLOCK_COMMENT = "BLOCK_COMMENT";
	public static final String JAVADOC_COMMENT = "JAVADOC_COMMENT";
	
	public static final String CONSTRUCTOR_DECLARATION = "CNSTR_DEC";
	public static final String INITIALIZER_DECLARATION = "INIT_DEC";
	public static final String ENUM_CONSTANT_DECLARATION = "ENUM_CONST_DEC";
	public static final String VARIABLE_DECLARATION = "VAR_DEC";
	public static final String ENUM_DECLARATION = "ENUM_DEC";
	public static final String ANNOTATION_DECLARATION = "ANN_DEC";
	public static final String ANNOTATION_MEMBER_DECLARATION = "ANN_MEMBER_DEC";
	public static final String EMPTY_MEMBER_DECLARATION = "EMPTY_MEMBER_DEC";
	public static final String EMPTY_TYPE_DECLARATION = "EMPTY_TYPE_DEC";
	public static final String WHILE_STATEMENT = "WHILE";
	public static final String TRY_STATEMENT = "TRY";
	public static final String THROW_STATEMENT = "THROW";
	public static final String SYNCHRONIZED_STATEMENT = "SYNC";
	public static final String SWITCH_STATEMENT = "SWITCH";
	public static final String SWITCH_ENTRY_STATEMENT = "SWITCH_ENTRY";
	public static final String RETURN_STATEMENT = "RETURN";
	public static final String LABELED_STATEMENT = "LABELED";
	public static final String IF_STATEMENT = "IF";
	public static final String ELSE_STATEMENT = "ELSE";
	public static final String FOR_STATEMENT = "FOR";
	public static final String FOR_EACH_STATEMENT = "FOR_EACH";
	public static final String EXPRESSION_STATEMENT = "EXPR_STMT";
	public static final String EXPLICIT_CONSTRUCTOR_STATEMENT = "EXPL_CONSTR";
	public static final String EMPTY_STATEMENT = "EMPTY";
	public static final String DO_STATEMENT = "DO";
	public static final String CONTINUE_STATEMENT = "CONTINUE";
	public static final String CATCH_CLAUSE_STATEMENT = "CATCH";
	public static final String BLOCK_STATEMENT = "BLOCK";
	public static final String VARIABLE_DECLARATION_ID = "VAR_DEC_ID";
	public static final String VARIABLE_DECLARATION_EXPRESSION = "VAR_DEC_EXPR";
	public static final String TYPE_EXPRESSION = "TYPE_EXPR";
	public static final String SUPER_EXPRESSION = "SUPER";
	public static final String QUALIFIED_NAME_EXPRESSION = "QUALIFIED_NAME";
	public static final String NULL_LITERAL_EXPRESSION = "NULL_LIT";
	public static final String METHOD_REFERENCE_EXPRESSION = "M_REF";
	public static final String LONG_LITERAL_MIN_VALUE_EXPRESSION = "LONG_LIT_MIN";
	public static final String LAMBDA_EXPRESSION = "LAMBDA";
	public static final String INTEGER_LITERAL_MIN_VALUE_EXPRESSION = "INT_LIT_MIN";
	public static final String INSTANCEOF_EXPRESSION = "INSTANCEOF";
	public static final String FIELD_ACCESS_EXPRESSION = "FIELD_ACC";
	public static final String CONDITIONAL_EXPRESSION = "CONDITION";
	public static final String CLASS_EXPRESSION = "CLASS";
	public static final String CAST_EXPRESSION = "CAST";
	public static final String ASSIGN_EXPRESSION = "ASSIGN";
	public static final String ARRAY_INIT_EXPRESSION = "INIT_ARR";
	public static final String ARRAY_CREATE_EXPRESSION = "CREATE_ARR";
	public static final String ARRAY_ACCESS_EXPRESSION = "ARR_ACC";
	public static final String PACKAGE_DECLARATION = "P_DEC";
	public static final String IMPORT_DECLARATION = "IMP_DEC";
	public static final String FIELD_DECLARATION = "FIELD_DEC";
	public static final String CLASS_TYPE = "CLASS_TYPE";
	public static final String CLASS_DECLARATION = "CLASS_OR_INTERFACE_DEC";
	public static final String EXTENDS_STATEMENT = "EXTENDS";
	public static final String IMPLEMENTS_STATEMENT = "IMPLEMENTS";
	public static final String METHOD_DECLARATION = "M_DEC";
	public static final String BINARY_EXPRESSION = "BIN_EXPR";
	public static final String UNARY_EXPRESSION = "UNARY_EXPR";
	public static final String METHOD_CALL_EXPRESSION = "M_CALL";
	// if a private method is called we handle it differently
	public static final String PRIVATE_METHOD_CALL_EXPRESSION = "M_CALL_PRIV";
	public static final String NAME_EXPRESSION = "NAME_EXPR";
	public static final String INTEGER_LITERAL_EXPRESSION = "INT_LIT";
	public static final String DOUBLE_LITERAL_EXPRESSION = "DOUBLE_LIT";
	public static final String STRING_LITERAL_EXPRESSION = "STR_LIT";
	public static final String BOOLEAN_LITERAL_EXPRESSION = "BOOL_LIT";
	public static final String CHAR_LITERAL_EXPRESSION = "CHAR_LIT";
	public static final String LONG_LITERAL_EXPRESSION = "LONG_LIT";
	public static final String THIS_EXPRESSION = "THIS";
	public static final String BREAK = "BREAK";
	public static final String OBJ_CREATE_EXPRESSION = "CREATE_OBJ";
	public static final String MARKER_ANNOTATION_EXPRESSION = "MARKER_ANN_EXPR";
	public static final String NORMAL_ANNOTATION_EXPRESSION = "NORMAL_ANN_EXPR";
	public static final String SINGLE_MEMBER_ANNOTATION_EXPRESSION = "SM_ANN_EXPR";
	
	public static final String PARAMETER = "PAR";
	public static final String MULTI_TYPE_PARAMETER = "MT_PAR"; 
	public static final String ENCLOSED_EXPRESSION = "ENCLOSED";
	public static final String ASSERT_STMT = "ASSERT";
	public static final String MEMBER_VALUE_PAIR = "MV_PAIR"; 
	
	public static final String TYPE_DECLARATION_STATEMENT = "TYPE_DEC";
	public static final String TYPE_REFERENCE = "REF_TYPE";
	public static final String TYPE_PRIMITIVE = "PRIM_TYPE";
	public static final String TYPE_UNION = "UNION_TYPE";
	public static final String TYPE_INTERSECTION = "INTERSECT_TYPE";
	public static final String TYPE_PAR = "TYPE_PAR"; 
	public static final String TYPE_WILDCARD = "WILDCARD_TYPE";
	public static final String TYPE_VOID = "VOID_TYPE";
	public static final String TYPE_UNKNOWN = "UNKNOWN_TYPE";
	
	public static final String UNKNOWN = "T_UNKNOWN";
	
	// closing tags for some special nodes
	public static final String END_SUFFIX = "_END";
	public static final String CLOSING_MDEC = METHOD_DECLARATION + END_SUFFIX;
	public static final String CLOSING_CNSTR = CONSTRUCTOR_DECLARATION + END_SUFFIX;
	public static final String CLOSING_IF = IF_STATEMENT + END_SUFFIX;
	public static final String CLOSING_WHILE = WHILE_STATEMENT + END_SUFFIX;
	public static final String CLOSING_FOR = FOR_STATEMENT + END_SUFFIX;
	public static final String CLOSING_TRY = TRY_STATEMENT + END_SUFFIX;
	public static final String CLOSING_CATCH = CATCH_CLAUSE_STATEMENT + END_SUFFIX;
	public static final String CLOSING_FOR_EACH = FOR_EACH_STATEMENT + END_SUFFIX;
	public static final String CLOSING_DO = DO_STATEMENT + END_SUFFIX;
	public static final String SWITCH_DO = SWITCH_STATEMENT + END_SUFFIX;
	
	/**
	 * Returns the mapping of the abstract syntax tree node to fit the language model
	 * @param aNode The node that should be mapped
	 * @return the string representation enclosed in a wrapper
	 */
	default public MappingWrapper<T> getMappingForNode(Node aNode) {
		
		if (aNode instanceof Expression) {
			// all expressions
			if ( aNode instanceof LiteralExpr ){
				if ( aNode instanceof NullLiteralExpr ){
					return getMappingForNullLiteralExpr((NullLiteralExpr) aNode);
				} else if ( aNode instanceof BooleanLiteralExpr ){
					return getMappingForBooleanLiteralExpr((BooleanLiteralExpr) aNode);
				} else if ( aNode instanceof StringLiteralExpr ){
					if ( aNode instanceof CharLiteralExpr ){
						return getMappingForCharLiteralExpr((CharLiteralExpr) aNode);
					} else if ( aNode instanceof IntegerLiteralExpr ){
						if ( aNode instanceof IntegerLiteralMinValueExpr ){
							return getMappingForIntegerLiteralMinValueExpr((IntegerLiteralMinValueExpr) aNode);
						} else {
							return getMappingForIntegerLiteralExpr((IntegerLiteralExpr) aNode);
						}
					} else if ( aNode instanceof LongLiteralExpr ){
						if ( aNode instanceof LongLiteralMinValueExpr ){
							return getMappingForLongLiteralMinValueExpr((LongLiteralMinValueExpr) aNode);
						} else {
							return getMappingForLongLiteralExpr((LongLiteralExpr) aNode);
						}
					} else if ( aNode instanceof DoubleLiteralExpr ){
						return getMappingForDoubleLiteralExpr((DoubleLiteralExpr) aNode);
					} else {
						return getMappingForStringLiteralExpr((StringLiteralExpr) aNode);
					}
				}
			} else if ( aNode instanceof ArrayAccessExpr ){
				return getMappingForArrayAccessExpr((ArrayAccessExpr) aNode);
			} else if ( aNode instanceof ArrayCreationExpr ){
				return getMappingForArrayCreationExpr((ArrayCreationExpr) aNode);
			} else if ( aNode instanceof ArrayInitializerExpr ){
				return getMappingForArrayInitializerExpr((ArrayInitializerExpr) aNode);
			} else if ( aNode instanceof AssignExpr ){
				return getMappingForAssignExpr((AssignExpr) aNode);
			} else if ( aNode instanceof BinaryExpr ){
				return getMappingForBinaryExpr((BinaryExpr) aNode);
			} else if ( aNode instanceof CastExpr ){
				return getMappingForCastExpr((CastExpr) aNode);
			} else if ( aNode instanceof ClassExpr ){
				return getMappingForClassExpr((ClassExpr) aNode);
			} else if ( aNode instanceof ConditionalExpr ){
				return getMappingForConditionalExpr((ConditionalExpr) aNode);
			} else if ( aNode instanceof FieldAccessExpr ){
				return getMappingForFieldAccessExpr((FieldAccessExpr) aNode);
			} else if ( aNode instanceof InstanceOfExpr ){
				return getMappingForInstanceOfExpr((InstanceOfExpr) aNode);
			} else if ( aNode instanceof LambdaExpr ){
				return getMappingForLambdaExpr((LambdaExpr) aNode);
			} else if ( aNode instanceof MethodCallExpr ){
				return getMappingForMethodCallExpr((MethodCallExpr) aNode);
			} else if ( aNode instanceof MethodReferenceExpr ){
				return getMappingForMethodReferenceExpr((MethodReferenceExpr) aNode);
			} else if ( aNode instanceof ThisExpr ){
				return getMappingForThisExpr((ThisExpr) aNode);
			} else if ( aNode instanceof EnclosedExpr ){
				return getMappingForEnclosedExpr((EnclosedExpr) aNode);
			}  else if ( aNode instanceof ObjectCreationExpr ){
				return getMappingForObjectCreationExpr((ObjectCreationExpr) aNode);
			} else if ( aNode instanceof UnaryExpr ){
				return getMappingForUnaryExpr((UnaryExpr) aNode);
			} else if ( aNode instanceof SuperExpr ){
				return getMappingForSuperExpr((SuperExpr) aNode);
			} else if ( aNode instanceof TypeExpr ){
				return getMappingForTypeExpr((TypeExpr) aNode);
			} else if ( aNode instanceof VariableDeclarationExpr ){
				return getMappingForVariableDeclarationExpr((VariableDeclarationExpr) aNode);
			} else if ( aNode instanceof NameExpr ){
				if ( aNode instanceof QualifiedNameExpr ){
					return getMappingForQualifiedNameExpr((QualifiedNameExpr) aNode);
				} else {
					return getMappingForNameExpr((NameExpr) aNode);
				}
			} else if ( aNode instanceof AnnotationExpr ){
				if ( aNode instanceof MarkerAnnotationExpr ){
					return getMappingForMarkerAnnotationExpr((MarkerAnnotationExpr) aNode);
				} else if ( aNode instanceof NormalAnnotationExpr ){
					return getMappingForNormalAnnotationExpr((NormalAnnotationExpr) aNode);
				} else if ( aNode instanceof SingleMemberAnnotationExpr ){
					return getMappingForSingleMemberAnnotationExpr((SingleMemberAnnotationExpr) aNode);
				}
			}
		} else if (aNode instanceof Type) {
			// all types
			if ( aNode instanceof ClassOrInterfaceType ){			
				return getMappingForClassOrInterfaceType((ClassOrInterfaceType) aNode);
			} else if ( aNode instanceof IntersectionType ){			
				return getMappingForIntersectionType((IntersectionType) aNode);
			} else if ( aNode instanceof PrimitiveType ){
				return getMappingForPrimitiveType((PrimitiveType) aNode);
			} else if ( aNode instanceof ReferenceType ){
				return getMappingForReferenceType((ReferenceType) aNode);
			} else if ( aNode instanceof UnionType ){
				return getMappingForUnionType((UnionType) aNode);
			} else if ( aNode instanceof UnknownType ){
				return getMappingForUnknownType((UnknownType) aNode);
			} else if ( aNode instanceof VoidType ){
				return getMappingForVoidType((VoidType) aNode);
			} else if ( aNode instanceof WildcardType ){
				return getMappingForWildcardType((WildcardType) aNode);
			}
		} else if (aNode instanceof Statement) {
			// all statements
			if ( aNode instanceof AssertStmt ){
				return getMappingForAssertStmt((AssertStmt) aNode);
			} else if ( aNode instanceof BlockStmt ){
				return getMappingForBlockStmt((BlockStmt) aNode);
			} else if ( aNode instanceof BreakStmt ){
				return getMappingForBreakStmt((BreakStmt) aNode);
			} else if ( aNode instanceof ContinueStmt ){
				return getMappingForContinueStmt((ContinueStmt) aNode);
			} else if ( aNode instanceof DoStmt ){
				return getMappingForDoStmt((DoStmt) aNode);
			} else if ( aNode instanceof EmptyStmt ){
				return getMappingForEmptyStmt((EmptyStmt) aNode);
			} else if ( aNode instanceof ExplicitConstructorInvocationStmt ){
				return getMappingForExplicitConstructorInvocationStmt((ExplicitConstructorInvocationStmt) aNode);
			} else if ( aNode instanceof ExpressionStmt ){
				return getMappingForExpressionStmt((ExpressionStmt) aNode);
			} else if ( aNode instanceof ForeachStmt ){
				return getMappingForForeachStmt((ForeachStmt) aNode);
			} else if ( aNode instanceof ForStmt ){
				return getMappingForForStmt((ForStmt) aNode);
			} else if ( aNode instanceof IfStmt ){
				return getMappingForIfStmt((IfStmt) aNode);
			} else if ( aNode instanceof ElseStmt ){
				return getMappingForElseStmt((ElseStmt) aNode);
			} else if ( aNode instanceof LabeledStmt ){
				return getMappingForLabeledStmt((LabeledStmt) aNode);
			} else if ( aNode instanceof ReturnStmt ){
				return getMappingForReturnStmt((ReturnStmt) aNode);
			} else if ( aNode instanceof SwitchEntryStmt ){
				return getMappingForSwitchEntryStmt((SwitchEntryStmt) aNode);
			} else if ( aNode instanceof SwitchStmt ){
				return getMappingForSwitchStmt((SwitchStmt) aNode);
			} else if ( aNode instanceof SynchronizedStmt ){
				return getMappingForSynchronizedStmt((SynchronizedStmt) aNode);
			} else if ( aNode instanceof ThrowStmt ){
				return getMappingForThrowStmt((ThrowStmt) aNode);
			} else if ( aNode instanceof TryStmt ){
				return getMappingForTryStmt((TryStmt) aNode);
			} else if ( aNode instanceof TypeDeclarationStmt ){
				return getMappingForTypeDeclarationStmt((TypeDeclarationStmt) aNode);
			} else if ( aNode instanceof WhileStmt ){
				return getMappingForWhileStmt((WhileStmt) aNode);
			} else if ( aNode instanceof ExtendsStmt ){
				return getMappingForExtendsStmt((ExtendsStmt) aNode);
			} else if ( aNode instanceof ImplementsStmt ){
				return getMappingForImplementsStmt((ImplementsStmt) aNode);
			}
		} else if (aNode instanceof BodyDeclaration) {
			// all declarations (may all have annotations)
			if ( aNode instanceof ConstructorDeclaration ){
				return getMappingForConstructorDeclaration((ConstructorDeclaration) aNode);
			} else if ( aNode instanceof InitializerDeclaration ){
				return getMappingForInitializerDeclaration((InitializerDeclaration) aNode);
			} else if ( aNode instanceof FieldDeclaration ){
				return getMappingForFieldDeclaration((FieldDeclaration) aNode);	
			} else if ( aNode instanceof MethodDeclaration ){
				return getMappingForMethodDeclaration((MethodDeclaration) aNode);
			} else if ( aNode instanceof EnumConstantDeclaration ){
				return getMappingForEnumConstantDeclaration((EnumConstantDeclaration) aNode);
			} else if ( aNode instanceof AnnotationMemberDeclaration ){
				return getMappingForAnnotationMemberDeclaration((AnnotationMemberDeclaration) aNode);
			}  else if ( aNode instanceof EmptyMemberDeclaration ){
				return getMappingForEmptyMemberDeclaration((EmptyMemberDeclaration) aNode);
			}else if (aNode instanceof TypeDeclaration) {
				if (aNode instanceof AnnotationDeclaration) {
					return getMappingForAnnotationDeclaration((AnnotationDeclaration) aNode);
				} else if ( aNode instanceof ClassOrInterfaceDeclaration ){
					return getMappingForClassOrInterfaceDeclaration((ClassOrInterfaceDeclaration) aNode);
				} else if ( aNode instanceof EmptyTypeDeclaration ){
					return getMappingForEmptyTypeDeclaration((EmptyTypeDeclaration) aNode);
				} else if ( aNode instanceof EnumDeclaration ){
					return getMappingForEnumDeclaration((EnumDeclaration) aNode);
				}
			}
		} else if (aNode instanceof Comment) {
			// all comments
			if ( aNode instanceof LineComment) {
				return getMappingForLineComment((LineComment) aNode);
			} else if ( aNode instanceof BlockComment) {
				return getMappingForBlockComment((BlockComment) aNode);
			} else if ( aNode instanceof JavadocComment) {
				return getMappingForJavadocComment((JavadocComment) aNode);
			}
		} else if (aNode instanceof BaseParameter) {
			if ( aNode instanceof Parameter ){
				return getMappingForParameter((Parameter) aNode);		
			} else if ( aNode instanceof MultiTypeParameter ){
				return getMappingForMultiTypeParameter((MultiTypeParameter) aNode);	
			}
		}

		else if( aNode instanceof PackageDeclaration ) {
			return getMappingForPackageDeclaration((PackageDeclaration) aNode);
		} else if ( aNode instanceof ImportDeclaration ){
			return getMappingForImportDeclaration((ImportDeclaration) aNode);
		} else if ( aNode instanceof TypeParameter ){
			return getMappingForTypeParameter((TypeParameter) aNode);
		}
		
		else if ( aNode instanceof CatchClause ){
			return getMappingForCatchClause((CatchClause) aNode);
		} else if ( aNode instanceof VariableDeclarator ){
			return getMappingForVariableDeclarator((VariableDeclarator) aNode);
		} else if ( aNode instanceof VariableDeclaratorId ){
			return getMappingForVariableDeclaratorId((VariableDeclaratorId) aNode);
		} else if ( aNode instanceof MemberValuePair ){
			return getMappingForMemberValuePair((MemberValuePair) aNode);
		}
		
		// compilation unit
		else if ( aNode instanceof CompilationUnit) {
			return getMappingForCompilationUnit((CompilationUnit) aNode);
		}
		
		// this should be removed after testing i guess
		// >> I wouldn't remove it, since it doesn't hurt and constitutes a default value <<
		return getMappingForUnknownNode(aNode);
	}

	default public MappingWrapper<T> getMappingForUnknownNode(Node aNode) { return null; }
	default public MappingWrapper<T> getMappingForCompilationUnit(CompilationUnit aNode) { return null; }
	default public MappingWrapper<T> getMappingForMemberValuePair(MemberValuePair aNode) { return null; }
	default public MappingWrapper<T> getMappingForVariableDeclaratorId(VariableDeclaratorId aNode) { return null; }
	default public MappingWrapper<T> getMappingForVariableDeclarator(VariableDeclarator aNode) { return null; }
	default public MappingWrapper<T> getMappingForCatchClause(CatchClause aNode) { return null; }
	default public MappingWrapper<T> getMappingForTypeParameter(TypeParameter aNode) { return null; }
	default public MappingWrapper<T> getMappingForImportDeclaration(ImportDeclaration aNode) { return null; }
	default public MappingWrapper<T> getMappingForPackageDeclaration(PackageDeclaration aNode) { return null; }
	default public MappingWrapper<T> getMappingForMultiTypeParameter(MultiTypeParameter aNode) { return null; }
	default public MappingWrapper<T> getMappingForParameter(Parameter aNode) { return null; }
	default public MappingWrapper<T> getMappingForJavadocComment(JavadocComment aNode) { return null; }
	default public MappingWrapper<T> getMappingForBlockComment(BlockComment aNode) { return null; }
	default public MappingWrapper<T> getMappingForLineComment(LineComment aNode) { return null; }
	default public MappingWrapper<T> getMappingForEnumDeclaration(EnumDeclaration aNode) { return null; }
	default public MappingWrapper<T> getMappingForEmptyTypeDeclaration(EmptyTypeDeclaration aNode) { return null; }
	default public MappingWrapper<T> getMappingForClassOrInterfaceDeclaration(ClassOrInterfaceDeclaration aNode) { return null; }
	default public MappingWrapper<T> getMappingForAnnotationDeclaration(AnnotationDeclaration aNode) { return null; }
	default public MappingWrapper<T> getMappingForEmptyMemberDeclaration(EmptyMemberDeclaration aNode) { return null; }
	default public MappingWrapper<T> getMappingForAnnotationMemberDeclaration(AnnotationMemberDeclaration aNode) { return null; }
	default public MappingWrapper<T> getMappingForEnumConstantDeclaration(EnumConstantDeclaration aNode) { return null; }
	default public MappingWrapper<T> getMappingForMethodDeclaration(MethodDeclaration aNode) { return null; }
	default public MappingWrapper<T> getMappingForFieldDeclaration(FieldDeclaration aNode) { return null; }
	default public MappingWrapper<T> getMappingForInitializerDeclaration(InitializerDeclaration aNode) { return null; }
	default public MappingWrapper<T> getMappingForConstructorDeclaration(ConstructorDeclaration aNode) { return null; }
	default public MappingWrapper<T> getMappingForWhileStmt(WhileStmt aNode) { return null; }
	default public MappingWrapper<T> getMappingForTypeDeclarationStmt(TypeDeclarationStmt aNode) { return null; }
	default public MappingWrapper<T> getMappingForTryStmt(TryStmt aNode) { return null; }
	default public MappingWrapper<T> getMappingForThrowStmt(ThrowStmt aNode) { return null; }
	default public MappingWrapper<T> getMappingForSynchronizedStmt(SynchronizedStmt aNode) { return null; }
	default public MappingWrapper<T> getMappingForSwitchStmt(SwitchStmt aNode) { return null; }
	default public MappingWrapper<T> getMappingForSwitchEntryStmt(SwitchEntryStmt aNode) { return null; }
	default public MappingWrapper<T> getMappingForReturnStmt(ReturnStmt aNode) { return null; }
	default public MappingWrapper<T> getMappingForLabeledStmt(LabeledStmt aNode) { return null; }
	default public MappingWrapper<T> getMappingForElseStmt(ElseStmt aNode) { return null; }
	default public MappingWrapper<T> getMappingForExtendsStmt(ExtendsStmt aNode) { return null; }
	default public MappingWrapper<T> getMappingForImplementsStmt(ImplementsStmt aNode) { return null; }
	default public MappingWrapper<T> getMappingForIfStmt(IfStmt aNode) { return null; }
	default public MappingWrapper<T> getMappingForForStmt(ForStmt aNode) { return null; }
	default public MappingWrapper<T> getMappingForForeachStmt(ForeachStmt aNode) { return null; }
	default public MappingWrapper<T> getMappingForExpressionStmt(ExpressionStmt aNode) { return null; }
	default public MappingWrapper<T> getMappingForExplicitConstructorInvocationStmt(
			ExplicitConstructorInvocationStmt aNode) { return null; }
	default public MappingWrapper<T> getMappingForEmptyStmt(EmptyStmt aNode) { return null; }
	default public MappingWrapper<T> getMappingForDoStmt(DoStmt aNode) { return null; }
	default public MappingWrapper<T> getMappingForContinueStmt(ContinueStmt aNode) { return null; }
	default public MappingWrapper<T> getMappingForBreakStmt(BreakStmt aNode) { return null; }
	default public MappingWrapper<T> getMappingForBlockStmt(BlockStmt aNode) { return null; }
	default public MappingWrapper<T> getMappingForAssertStmt(AssertStmt aNode) { return null; }
	default public MappingWrapper<T> getMappingForWildcardType(WildcardType aNode) { return null; }
	default public MappingWrapper<T> getMappingForVoidType(VoidType aNode) { return null; }
	default public MappingWrapper<T> getMappingForUnknownType(UnknownType aNode) { return null; }
	default public MappingWrapper<T> getMappingForUnionType(UnionType aNode) { return null; }
	default public MappingWrapper<T> getMappingForReferenceType(ReferenceType aNode) { return null; }
	default public MappingWrapper<T> getMappingForPrimitiveType(PrimitiveType aNode) { return null; }
	default public MappingWrapper<T> getMappingForIntersectionType(IntersectionType aNode) { return null; }
	default public MappingWrapper<T> getMappingForClassOrInterfaceType(ClassOrInterfaceType aNode) { return null; }
	default public MappingWrapper<T> getMappingForSingleMemberAnnotationExpr(SingleMemberAnnotationExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForNormalAnnotationExpr(NormalAnnotationExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForMarkerAnnotationExpr(MarkerAnnotationExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForNameExpr(NameExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForQualifiedNameExpr(QualifiedNameExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForVariableDeclarationExpr(VariableDeclarationExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForTypeExpr(TypeExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForSuperExpr(SuperExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForUnaryExpr(UnaryExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForObjectCreationExpr(ObjectCreationExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForEnclosedExpr(EnclosedExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForThisExpr(ThisExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForMethodReferenceExpr(MethodReferenceExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForMethodCallExpr(MethodCallExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForLambdaExpr(LambdaExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForInstanceOfExpr(InstanceOfExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForFieldAccessExpr(FieldAccessExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForConditionalExpr(ConditionalExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForClassExpr(ClassExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForCastExpr(CastExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForBinaryExpr(BinaryExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForAssignExpr(AssignExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForArrayInitializerExpr(ArrayInitializerExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForArrayCreationExpr(ArrayCreationExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForArrayAccessExpr(ArrayAccessExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForStringLiteralExpr(StringLiteralExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForDoubleLiteralExpr(DoubleLiteralExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForLongLiteralExpr(LongLiteralExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForLongLiteralMinValueExpr(LongLiteralMinValueExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForIntegerLiteralExpr(IntegerLiteralExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForIntegerLiteralMinValueExpr(IntegerLiteralMinValueExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForCharLiteralExpr(CharLiteralExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForBooleanLiteralExpr(BooleanLiteralExpr aNode) { return null; }
	default public MappingWrapper<T> getMappingForNullLiteralExpr(NullLiteralExpr aNode) { return null; }
	
	/**
	 * Returns a closing token for some block nodes
	 * 
	 * @param aNode
	 * an AST node for which the closing token shall be generated
	 * @return Closing token or null if the node has none
	 */
	public T getClosingToken(Node aNode);
	
	/**
	 * Passes a black list of method names to the mapper.
	 * @param aBL A collection of method names that should be handled differently
	 */
	public void setPrivMethodBlackList( Collection<String> aBL );
	
	/**
	 * Clears the black list of method names from this mapper
	 */
	public void clearPrivMethodBlackList();
	
}