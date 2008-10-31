package org.meta_environment.rascal.ast;
import org.eclipse.imp.pdb.facts.ITree;
public abstract class Body extends AbstractAST
{
  static public class Toplevels extends Body
  {
/* toplevels:Toplevel* -> Body {cons("Toplevels")} */
    private Toplevels ()
    {
    }
    /*package */ Toplevels (ITree tree, java.util.List < Toplevel > toplevels)
    {
      this.tree = tree;
      this.toplevels = toplevels;
    }
    public IVisitable accept (IASTVisitor visitor)
    {
      return visitor.visitBodyToplevels (this);
    }
    private java.util.List < org.meta_environment.rascal.ast.Toplevel >
      toplevels;
    public java.util.List < org.meta_environment.rascal.ast.Toplevel >
      getToplevels ()
    {
      return toplevels;
    }
    private void $setToplevels (java.util.List <
				org.meta_environment.rascal.ast.Toplevel > x)
    {
      this.toplevels = x;
    }
    public Toplevels setToplevels (java.util.List <
				   org.meta_environment.rascal.ast.Toplevel >
				   x)
    {
      org.meta_environment.rascal.ast.Toplevels z = new Toplevels ();
      z.$setToplevels (x);
      return z;
    }
  }
  static public class Ambiguity extends Body
  {
    public Body.Ambiguity makeBodyAmbiguity (java.util.List < Body >
					     alternatives)
    {
      Body.Ambiguity amb = new Body.Ambiguity (alternatives);
      if (!table.containsKey (amb))
	{
	  table.put (amb, amb);
	}
      return (Body.Ambiguity) table.get (amb);
    }
    private final java.util.List < Body > alternatives;
    public Ambiguity (java.util.List < Body > alternatives)
    {
      this.alternatives =
	java.util.Collections.unmodifiableList (alternatives);
    }
    public java.util.List < Body > getAlternatives ()
    {
      return alternatives;
    }
  }
}
