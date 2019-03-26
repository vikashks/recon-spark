package com.practice.recon.antrl;// Generated from Reconciliation.g4 by ANTLR 4.7.2
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ReconciliationParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ReconciliationVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ReconciliationParser#def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDef(ReconciliationParser.DefContext ctx);
	/**
	 * Visit a parse tree produced by {@link ReconciliationParser#source}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSource(ReconciliationParser.SourceContext ctx);
	/**
	 * Visit a parse tree produced by {@link ReconciliationParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(ReconciliationParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link ReconciliationParser#join}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJoin(ReconciliationParser.JoinContext ctx);
	/**
	 * Visit a parse tree produced by {@link ReconciliationParser#operation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperation(ReconciliationParser.OperationContext ctx);
}