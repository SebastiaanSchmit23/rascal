package org.rascalmpl.library.experiments.Compiler.RVM.Interpreter.Instructions;

import org.rascalmpl.library.experiments.Compiler.RVM.Interpreter.BytecodeGenerator;
import org.rascalmpl.library.experiments.Compiler.RVM.Interpreter.CodeBlock;
import org.rascalmpl.library.experiments.Compiler.RVM.Interpreter.RascalPrimitive;
import io.usethesource.vallang.ISourceLocation;

public class PushCallPrimN extends Instruction {

	RascalPrimitive prim;
	int arity;
	ISourceLocation src;
	
	public PushCallPrimN(CodeBlock ins, RascalPrimitive prim, int arity, ISourceLocation src){
		super(ins, Opcode.PUSHCALLPRIMN);
		this.prim = prim;
		this.arity = arity;
		this.src = src;
	}
	
	public String toString() { return "PUSHCALLPRIMN " + prim + ", " + arity + ", " + src; }
	
	public void generate(){
		codeblock.addCode2(opcode.getOpcode(), prim.ordinal(), arity);
		codeblock.addCode(codeblock.getConstantIndex(src));
	}
	
	public void generateByteCode(BytecodeGenerator codeEmittor, boolean debug){
		if ( debug ) 
			codeEmittor.emitDebugCall2(opcode.name(), prim.name(), arity);

		codeEmittor.emitInlinePushCallPrimN(prim, arity, codeblock.getConstantIndex(src)); 
	}
}
