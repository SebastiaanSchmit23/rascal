/*******************************************************************************
 * Copyright (c) 2009-2013 CWI
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:

 *   * Jurgen J. Vinju - Jurgen.Vinju@cwi.nl - CWI
 *   * Tijs van der Storm - Tijs.van.der.Storm@cwi.nl
 *   * Paul Klint - Paul.Klint@cwi.nl - CWI
 *   * Arnold Lankamp - Arnold.Lankamp@cwi.nl
*******************************************************************************/
package org.rascalmpl.interpreter.env;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.imp.pdb.facts.IConstructor;
import org.eclipse.imp.pdb.facts.IMap;
import org.eclipse.imp.pdb.facts.ISourceLocation;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeFactory;
import org.rascalmpl.ast.AbstractAST;
import org.rascalmpl.ast.QualifiedName;
import org.rascalmpl.interpreter.asserts.ImplementationError;
import org.rascalmpl.interpreter.result.AbstractFunction;
import org.rascalmpl.interpreter.result.ICallableValue;
import org.rascalmpl.interpreter.staticErrors.UndeclaredModule;
import org.rascalmpl.interpreter.utils.Names;
import org.rascalmpl.parser.gtd.IGTD;


/**
 * The global environment represents the heap of Rascal.
 * The stack is initialized with a bottom frame, which represent the shell
 * environment.
 * 
 */
public class GlobalEnvironment {
	/** The heap of Rascal */
	private final HashMap<String, ModuleEnvironment> moduleEnvironment = new HashMap<String, ModuleEnvironment>();
		
	/** Keeping track of module locations */
	private final HashMap<String, URI> moduleLocations = new HashMap<String,URI>();
	private final HashMap<URI, String> locationModules = new HashMap<URI,String>();
	
	/**
	 * Source location resolvers map user defined schemes to primitive schemes
	 */
	private final HashMap<String, ICallableValue> sourceResolvers = new HashMap<String, ICallableValue>();
	
	/** Keeping track of generated parsers */
	private final HashMap<String,ParserTuple> objectParsersForModules = new HashMap<String,ParserTuple>();
	private final HashMap<String,ParserTuple> rascalParsersForModules = new HashMap<String,ParserTuple>();
    
    private boolean bootstrapper;
	
	public void clear() {
		moduleEnvironment.clear();
		moduleLocations.clear();
		locationModules.clear();
		sourceResolvers.clear();
		objectParsersForModules.clear();
		rascalParsersForModules.clear();
	}
	
	/**
	 * Register a source resolver for a specific scheme. Will overwrite the previously
	 * registered source resolver for this scheme.
	 * 
	 * @param scheme   intended be a scheme name without + or :
	 * @param function a Rascal function of type `loc (loc)`
	 */
	public void registerSourceResolver(String scheme, ICallableValue function) {
		sourceResolvers.put(scheme, function);
	}
	
	/**
	 * Allocate a new module on the heap
	 * @param name
	 */
	public ModuleEnvironment addModule(ModuleEnvironment mod) {
		assert mod != null;
		ModuleEnvironment env = moduleEnvironment.get(mod.getName());
		if (env == null) {
			moduleEnvironment.put(mod.getName(), mod);
			return mod;
		}
		else if (env == mod) {
			return mod;
		}
		else {
			throw new ImplementationError("Reinstantiating same module " + mod.getName());
		}
	}
	
	public ModuleEnvironment resetModule(String name) {
		ModuleEnvironment mod = moduleEnvironment.get(name);
		mod.reset();
		return mod;
	}
		
	/**
	 * Retrieve a module from the heap
	 */
	public ModuleEnvironment getModule(String name) {
		return moduleEnvironment.get(name);
	}

	public ModuleEnvironment getModule(QualifiedName name, AbstractAST ast) {
		ModuleEnvironment module = getModule(Names.fullName(name));
		if (module == null) {
			throw new UndeclaredModule(Names.fullName(name), ast);
		}
		return module;
	}
	

	public boolean existsModule(String name) {
		return moduleEnvironment.containsKey(name);
	}

	
	@Override
	public String toString(){
		StringBuffer res = new StringBuffer();
		res.append("heap.modules: ");
		for (String mod : moduleEnvironment.keySet()) {
			res.append(mod + ",");
		}
		return res.toString();
	}

	public void removeModule(ModuleEnvironment env) {
		moduleEnvironment.remove(env.getName());
	}

	public void setModuleURI(String name, URI location) {
		moduleLocations.put(name, location);
		locationModules.put(location, name);
	}
	
	public URI getModuleURI(String name) {
		return moduleLocations.get(name);
	}
	
	public String getModuleForURI(URI location) {
		return locationModules.get(location);
	}

	/**
	 * A utility method for getting the right environment for a qualified name in a certain context.
	 * 
	 * @param current  the current environment
	 * @return current if the given name is not qualifed, otherwise it returns the environment that the qualified name points to
	 */
	public Environment getEnvironmentForName(QualifiedName name, Environment current) {
		if (Names.isQualified(name)) {
			ModuleEnvironment mod = getModule(Names.moduleName(name));
			if (mod == null) {
				throw new UndeclaredModule(Names.moduleName(name), name);
			}
			return mod;
		}

		return current;
	}
	
	public Class<IGTD<IConstructor, IConstructor, ISourceLocation>> getObjectParser(String module, IMap productions) {
		return getParser(objectParsersForModules, module, productions);
	}
	
	/**
	 * Retrieves a parser for a module.
	 * 
	 * @param module
	 * @param productions
	 */
	private Class<IGTD<IConstructor, IConstructor, ISourceLocation>> getParser(Map<String,ParserTuple> store, String module, IMap productions) {
		ParserTuple parser = store.get(module);
		if(parser != null && parser.getProductions().isEqual(productions)) {
			return parser.getParser();
		}
		
		return null;
	}
	
	public void storeObjectParser(String module, IMap productions, Class<IGTD<IConstructor, IConstructor, ISourceLocation>>  parser) {
		storeParser(objectParsersForModules, module, productions, parser);
	}
	
	private static void storeParser(HashMap<String, ParserTuple> store, String module, IMap productions, Class<IGTD<IConstructor, IConstructor, ISourceLocation>> parser) {
		ParserTuple newT = new ParserTuple(productions, parser);
		store.put(module, newT);
	}
	
	public Set<String> getImportingModules(String mod) {
		Set<String> result = new HashSet<String>();
		
		for (ModuleEnvironment env : moduleEnvironment.values()) {
			if (env.getImports().contains(mod)) {
				result.add(env.getName());
			}
		}
		
		return result;
	}
	
	public Set<String> getExtendingModules(String mod) {
		Set<String> result = new HashSet<String>();
		List<String> todo = new LinkedList<String>();
		todo.add(mod);
		
		while (!todo.isEmpty()) {
			String next = todo.remove(0);
			
			for (ModuleEnvironment env : moduleEnvironment.values()) {
				if (env.getExtends().contains(next)) {
					if (!result.contains(next)) {
						todo.add(env.getName());
						result.add(next);
						todo.removeAll(result);
					}
				}
			}
			result.add(next);
		}
		
		result.remove(mod);
		return result;
	}
	
	private static class ParserTuple {
		private final IMap production;
		private final Class<IGTD<IConstructor, IConstructor, ISourceLocation>> parser;

		public ParserTuple(IMap productions, Class<IGTD<IConstructor, IConstructor, ISourceLocation>> parser) {
			this.production = productions;
			this.parser = parser;
		}
		
		public IMap getProductions() {
			return production;
		}
		
		public Class<IGTD<IConstructor, IConstructor, ISourceLocation>> getParser() {
			return parser;
		}
	}
	
	public AbstractFunction getResourceImporter(String resourceScheme) {
		for (ModuleEnvironment menv : this.moduleEnvironment.values()) {
			if (menv.hasImporterForResource(resourceScheme))
				return menv.getResourceImporter(resourceScheme);
		}
		return null;
	}

	public void isBootstrapper(boolean b) {
	  this.bootstrapper = b;
	}
	
  public boolean isBootstrapper() {
    return bootstrapper;
  }
}
