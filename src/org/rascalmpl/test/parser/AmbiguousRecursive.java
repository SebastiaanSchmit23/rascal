/*******************************************************************************
 * Copyright (c) 2009-2011 CWI
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:

 *   * Jurgen J. Vinju - Jurgen.Vinju@cwi.nl - CWI
 *   * Arnold Lankamp - Arnold.Lankamp@cwi.nl
*******************************************************************************/
package org.rascalmpl.test.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.imp.pdb.facts.IConstructor;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.io.StandardTextReader;
import org.rascalmpl.parser.gtd.SGTDBF;
import org.rascalmpl.parser.gtd.stack.AbstractStackNode;
import org.rascalmpl.parser.gtd.stack.LiteralStackNode;
import org.rascalmpl.parser.gtd.stack.NonTerminalStackNode;
import org.rascalmpl.parser.uptr.NodeToUPTR;
import org.rascalmpl.values.ValueFactoryFactory;
import org.rascalmpl.values.uptr.Factory;

/*
S ::= SSS | SS | a
*/
public class AmbiguousRecursive extends SGTDBF implements IParserTest{
	private final static IConstructor SYMBOL_START_S = VF.constructor(Factory.Symbol_Sort, VF.string("S"));
	private final static IConstructor SYMBOL_S = VF.constructor(Factory.Symbol_Sort, VF.string("S"));
	private final static IConstructor SYMBOL_a = VF.constructor(Factory.Symbol_Lit, VF.string("a"));
	private final static IConstructor SYMBOL_char_a = VF.constructor(Factory.Symbol_CharClass, VF.list(VF.constructor(Factory.CharRange_Single, VF.integer(97))));
	
	private final static IConstructor PROD_S_SSS = VF.constructor(Factory.Production_Default,  SYMBOL_START_S, VF.list(SYMBOL_S, SYMBOL_S, SYMBOL_S), VF.set());
	private final static IConstructor PROD_S_SS = VF.constructor(Factory.Production_Default,  SYMBOL_START_S, VF.list(SYMBOL_S, SYMBOL_S), VF.set());
	private final static IConstructor PROD_S_a = VF.constructor(Factory.Production_Default,  SYMBOL_START_S, VF.list(SYMBOL_a), VF.set());
	private final static IConstructor PROD_a_a = VF.constructor(Factory.Production_Default,  SYMBOL_a, VF.list(SYMBOL_char_a), VF.set());
	
	private final static AbstractStackNode NONTERMINAL_START_S = new NonTerminalStackNode(AbstractStackNode.START_SYMBOL_ID, 0, "S");
	private final static AbstractStackNode NONTERMINAL_S0 = new NonTerminalStackNode(0, 0, "S");
	private final static AbstractStackNode NONTERMINAL_S1 = new NonTerminalStackNode(1, 1, "S");
	private final static AbstractStackNode NONTERMINAL_S2 = new NonTerminalStackNode(2, 2, "S");
	private final static AbstractStackNode NONTERMINAL_S3 = new NonTerminalStackNode(3, 0, "S");
	private final static AbstractStackNode NONTERMINAL_S4 = new NonTerminalStackNode(4, 1, "S");
	private final static AbstractStackNode LITERAL_a5 = new LiteralStackNode(5, 0, PROD_a_a, new char[]{'a'});
	
	private final static AbstractStackNode[] S_EXPECT_1 = new AbstractStackNode[3];
	static{
		S_EXPECT_1[0] = NONTERMINAL_S0;
		S_EXPECT_1[0].setProduction(S_EXPECT_1);
		S_EXPECT_1[1] = NONTERMINAL_S1;
		S_EXPECT_1[1].setProduction(S_EXPECT_1);
		S_EXPECT_1[2] = NONTERMINAL_S2;
		S_EXPECT_1[2].setProduction(S_EXPECT_1);
		S_EXPECT_1[2].setParentProduction(PROD_S_SSS);
	}
	
	private final static AbstractStackNode[] S_EXPECT_2 = new AbstractStackNode[2];
	static{
		S_EXPECT_2[0] = NONTERMINAL_S3;
		S_EXPECT_2[0].setProduction(S_EXPECT_2);
		S_EXPECT_2[1] = NONTERMINAL_S4;
		S_EXPECT_2[1].setProduction(S_EXPECT_2);
		S_EXPECT_2[1].setParentProduction(PROD_S_SS);
	}
	
	private final static AbstractStackNode[] S_EXPECT_3 = new AbstractStackNode[1];
	static{
		S_EXPECT_3[0] = LITERAL_a5;
		S_EXPECT_3[0].setProduction(S_EXPECT_3);
		S_EXPECT_3[0].setParentProduction(PROD_S_a);
	}
	
	public AmbiguousRecursive(){
		super();
	}
	
	public void S(){
		expect(S_EXPECT_1[0]);
		expect(S_EXPECT_2[0]);
		expect(S_EXPECT_3[0]);
	}
	
	public IConstructor executeParser(){
		return (IConstructor) parse(NONTERMINAL_START_S, null, "aaa".toCharArray(), new NodeToUPTR());
	}
	
	public IValue getExpectedResult() throws IOException{
		String expectedInput = "amb({appl(prod(sort(\"S\"),[sort(\"S\"),sort(\"S\"),sort(\"S\")],{}),[appl(prod(sort(\"S\"),[lit(\"a\")],{}),[appl(prod(lit(\"a\"),[\\char-class([single(97)])],{}),[char(97)])]),appl(prod(sort(\"S\"),[lit(\"a\")],{}),[appl(prod(lit(\"a\"),[\\char-class([single(97)])],{}),[char(97)])]),appl(prod(sort(\"S\"),[lit(\"a\")],{}),[appl(prod(lit(\"a\"),[\\char-class([single(97)])],{}),[char(97)])])]),appl(prod(sort(\"S\"),[sort(\"S\"),sort(\"S\")],{}),[appl(prod(sort(\"S\"),[lit(\"a\")],{}),[appl(prod(lit(\"a\"),[\\char-class([single(97)])],{}),[char(97)])]),appl(prod(sort(\"S\"),[sort(\"S\"),sort(\"S\")],{}),[appl(prod(sort(\"S\"),[lit(\"a\")],{}),[appl(prod(lit(\"a\"),[\\char-class([single(97)])],{}),[char(97)])]),appl(prod(sort(\"S\"),[lit(\"a\")],{}),[appl(prod(lit(\"a\"),[\\char-class([single(97)])],{}),[char(97)])])])]),appl(prod(sort(\"S\"),[sort(\"S\"),sort(\"S\")],{}),[appl(prod(sort(\"S\"),[sort(\"S\"),sort(\"S\")],{}),[appl(prod(sort(\"S\"),[lit(\"a\")],{}),[appl(prod(lit(\"a\"),[\\char-class([single(97)])],{}),[char(97)])]),appl(prod(sort(\"S\"),[lit(\"a\")],{}),[appl(prod(lit(\"a\"),[\\char-class([single(97)])],{}),[char(97)])])]),appl(prod(sort(\"S\"),[lit(\"a\")],{}),[appl(prod(lit(\"a\"),[\\char-class([single(97)])],{}),[char(97)])])])})";
		return new StandardTextReader().read(ValueFactoryFactory.getValueFactory(), Factory.uptr, Factory.Tree, new ByteArrayInputStream(expectedInput.getBytes()));
	}

	public static void main(String[] args){
		AmbiguousRecursive ar = new AmbiguousRecursive();
		IConstructor result = ar.executeParser();
		System.out.println(result);
		
		System.out.println("[S(S(a),S(a),S(a)),S(S(a),S(S(a),S(a))),S(S(S(a),S(a)),S(a))] <- good");
	}
}
