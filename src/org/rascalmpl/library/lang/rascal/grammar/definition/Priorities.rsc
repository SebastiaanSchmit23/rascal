@license{
  Copyright (c) 2009-2015 CWI
  All rights reserved. This program and the accompanying materials
  are made available under the terms of the Eclipse Public License v1.0
  which accompanies this distribution, and is available at
  http://www.eclipse.org/legal/epl-v10.html
}
module lang::rascal::grammar::definition::Priorities

extend ParseTree;
import Grammar;
import Set;
import List;
import IO;
import util::Maybe;
import Node;
 
import lang::rascal::grammar::definition::Productions;
import lang::rascal::grammar::definition::Symbols;
import lang::rascal::grammar::definition::References;
import lang::rascal::grammar::Lookahead;
// import lang::rascal::grammar::analyze::Recursion; 


public alias Priorities = rel[Production father, Production child];
public alias DoNotNest = rel[Production father, int position, Production child];

 
public DoNotNest doNotNest(Grammar g) {
  g = references(g); // references must be resolved to support the right semantics for ... and :cons references in priority and associativity groups
  DoNotNest result = {};
  
  for (s <- g.rules) {
    lefties = {s}; // leftRecursive(g, s);
    righties = {s}; //rightRecursive(g, s);
    <ordering, ass> = doNotNest(g.rules[s], lefties, righties);
    
    // associativity groups are closed under group membership
    solve (ass) {
       ass += { <a, pos, c> | <a, pos, b> <- ass, <b, _, c> <- ass };
    }
    result += ass;
    
    // priority is closed with the other elements of associativity groups
	ordering += ordering o ass<father,child> 
	          + ass<father,child> o ordering;
    
    // finally priority is transitively closed
    ordering = ordering+; 
   
    for (<Production father, Production child> <- ordering) {
      switch (father) {
        case prod(Symbol rhs,lhs:[Symbol l,_*,Symbol r],_) : {
          if (match(l,lefties) && match(r,righties)) {
            if (prod(Symbol crhs,clhs:[_*,Symbol cl],_) := child, match(cl,righties)) {
              result += {<father, 0, child>};
            }
            if (prod(Symbol crhs,clhs:[Symbol cl,_*],_) := child, match(cl,lefties)) {
              result += {<father, size(lhs) - 1, child>};
            }
          }   
          else {
            fail;
          }
      }
      case prod(Symbol rhs,lhs:[Symbol l,_*],_) :
        if (match(l,lefties), prod(Symbol crhs,clhs:[_*,Symbol cl],_) := child, match(cl,righties)) {
          result += {<father, 0, child>};
        }   
        else { 
          fail;
        }
      case prod(Symbol rhs,lhs:[_*,Symbol r],_) :
        if (match(r,righties), prod(Symbol crhs,clhs:[Symbol cl,_*],_) := child, match(cl,lefties)) {
          result += {<father, size(lhs) - 1, child>};
        }   
        else { 
          fail;
        }
      }
    } 
  }
  
  return result // TODO: in the future the except relation needs to be reported separately because it should not be indirect.
       + {*except(p, g) | /Production p <- g, p is prod || p is regular}
       ;
}

@doc{
This one-liner searches a given production for "except restrictions". 
For every position in the production that is restricted, and for every restriction it finds 
at this position, it adds a 'do-not-nest' tuple to the result.
}
public DoNotNest except(Production p:prod(Symbol _, list[Symbol] lhs, set[Attr] _), Grammar g) 
  = { <p, i, q>  | i <- index(lhs), conditional(s, excepts) := delabel(lhs[i]), isdef(g, s), except(c) <- excepts, /q:prod(label(c,s),_,_) := g.rules[s]};
 

//TODO: compiler issues when  g.rules[s]? is inlined
bool isdef(Grammar g, Symbol s) = g.rules[s]?;


public DoNotNest except(Production p:regular(Symbol s), Grammar g) {
  Maybe[Production] find(str c, Symbol t) = (/q:prod(label(c,t),_,_) := (g.rules[t]?choice(s,{}))) ? just(q) : nothing();
  
  switch (s) {
    case \opt(conditional(t,cs)) : 
      return {<p,0,q> | except(c) <- cs, just(q) := find(c,t)};
    case \iter-star(conditional(t,cs)) :
      return {<p,0,q> | except(c) <- cs, just(q) := find(c,t)};
    case \iter(conditional(t,cs)) :
      return {<p,0,q> | except(c) <- cs, just(q) := find(c,t)};
    case \iter-seps(conditional(t,cs),ss) :
      return {<p,0,q> | except(c) <- cs, just(q) := find(c,t)}
           + {<p,i+1,q> | i <- index(ss), conditional(u,css) := ss[i], except(ds) <- css, just(q) := find(ds,u)};
    case \iter-seps(_,ss) :
      return {<p,i+1,q> | i <- index(ss), conditional(u,css) := ss[i], except(ds) <- css, just(q) := find(ds,u)};
    case \iter-star-seps(conditional(t,cs),ss) :
      return {<p,0,q> | except(c) <- cs, just(q) := find(c,t)}
           + {<p,i+1,q> | i <- index(ss), conditional(u,css) := ss[i], except(ds) <- css, just(q) := find(ds,u)};
    case \iter-star-seps(_,ss) :
      return {<p,i+1,q> | i <- index(ss), conditional(u,css) := ss[i], except(ds) <- css, just(q) := find(ds,u)};       
    case \alt(as) :
      return {<p,0,q> | conditional(t,cs) <- as, except(c) <- cs, just(q) := find(c,t)};
    case \seq(ss) :
      return {<p,i,q> | i <- index(ss), conditional(t,cs) <- ss, except(c) <- cs, just(q) := find(c,t)};
     default: return {};
  }
  
  return {};
}


public tuple[Priorities prio,DoNotNest ass] doNotNest(Production p, set[Symbol] lefties, set[Symbol] righties) {
  switch (p) {
    case choice(_, set[Production] alts) : {
        Priorities pr = {}; DoNotNest as = {};
        for (a <- alts, <prA,asA> := doNotNest(a, lefties, righties)) {
          pr += prA;
          as += asA;
        }
        return <pr, as>; 
      }
    case priority(_, list[Production] levels) : 
      return priority(levels, lefties, righties);
    case \associativity(_, Associativity a, set[Production] alts) : 
      return associativity(a, alts, lefties, righties);
  }
  
  return <{},{}>;
}

tuple[Priorities, DoNotNest] associativity(Associativity a, set[Production] alts, set[Symbol] lefties, set[Symbol] righties) {
  result = {};
  
  for ({Production pivot, *Production rest} := alts,  Production child:prod(_,_,_) := pivot) {
    switch (a) {
      case \left(): 
        result += {<father, size(lhs) - 1, child> | /Production father:prod(Symbol rhs,lhs:[_*,Symbol r],_) <- alts, match(r,righties)};  
      case \assoc():
        result += {<father, size(lhs) - 1, child> | /Production father:prod(Symbol rhs,lhs:[_*,Symbol r],_) <- alts, match(r,righties)};
      case \right():
        result += {<father, 0, child>             | /Production father:prod(Symbol rhs,lhs:[Symbol l,_*],_) <- alts, match(l,lefties)};
      case \non-assoc(): {
        result += {<father, size(lhs) - 1, child> | /Production father:prod(Symbol rhs,lhs:[_*,Symbol r],_) <- alts, match(r,righties)}
                + {<father, 0, child>             | /Production father:prod(Symbol rhs,lhs:[Symbol l,_*],_) <- alts, match(l,lefties)};
      }
    } 
  }
  
  pr = {};
  for (x <- alts, <prX, asX> := doNotNest(x, lefties, righties)) {
    pr += prX;
    result += asX;
  }
  
  return <pr, result>;
}

public tuple[Priorities,DoNotNest] priority(list[Production] levels, set[Symbol] lefties, set[Symbol] righties) {
  // collect basic filter; note that this duplicates references such that they are resolved twice in the right positions
  ordering = { <father,child> | [pre*,Production father, Production child, post*] := levels };

  // flatten nested structure to obtain direct relations
  todo = ordering;
  ordering = {};
  while (todo != {}) {
    <prio,todo> = takeOneFrom(todo);
    switch (prio) {
      case <choice(_,set[Production] alts),Production child> :
        todo += alts * {child};
      case <Production father, choice(_,set[Production] alts)> :
        todo += {father} * alts;
      case <associativity(_,_,set[Production] alts),Production child> :
        todo += alts * {child};
      case <Production father, associativity(_,_,set[Production] alts)> :
        todo += {father} * alts;
      default:
        ordering += prio;
    }
  }
  
  DoNotNest as = {};
  for (x <- levels, <prX,asX> := doNotNest(x, lefties, righties)) {
    ordering += prX;
    as += asX;
  }
  
  return <ordering, as>;
}

private bool match(Symbol x, set[Symbol] reference) = striprec(x) in reference;

