package com.practice.recon.antrl;// Generated from Reconciliation.g4 by ANTLR 4.7.2
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ReconciliationParser}.
 */
public interface ReconciliationListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ReconciliationParser#def}.
	 * @param ctx the parse tree
	 */
	void enterDef(ReconciliationParser.DefContext ctx);
	/**
	 * Exit a parse tree produced by {@link ReconciliationParser#def}.
	 * @param ctx the parse tree
	 */
	void exitDef(ReconciliationParser.DefContext ctx);
	/**
	 * Enter a parse tree produced by {@link ReconciliationParser#source}.
	 * @param ctx the parse tree
	 */
	void enterSource(ReconciliationParser.SourceContext ctx);
	/**
	 * Exit a parse tree produced by {@link ReconciliationParser#source}.
	 * @param ctx the parse tree
	 */
	void exitSource(ReconciliationParser.SourceContext ctx);
	/**
	 * Enter a parse tree produced by {@link ReconciliationParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(ReconciliationParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link ReconciliationParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(ReconciliationParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link ReconciliationParser#join}.
	 * @param ctx the parse tree
	 */
	void enterJoin(ReconciliationParser.JoinContext ctx);
	/**
	 * Exit a parse tree produced by {@link ReconciliationParser#join}.
	 * @param ctx the parse tree
	 */
	void exitJoin(ReconciliationParser.JoinContext ctx);
	/**
	 * Enter a parse tree produced by {@link ReconciliationParser#operation}.
	 * @param ctx the parse tree
	 */
	void enterOperation(ReconciliationParser.OperationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ReconciliationParser#operation}.
	 * @param ctx the parse tree
	 */
	void exitOperation(ReconciliationParser.OperationContext ctx);
}