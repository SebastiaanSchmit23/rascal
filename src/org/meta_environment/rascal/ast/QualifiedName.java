package org.meta_environment.rascal.ast;
import org.eclipse.imp.pdb.facts.ITree;
public abstract class QualifiedName extends AbstractAST
{
  static public class Default extends QualifiedName
  {
/* names:{Name "::"}+ -> QualifiedName {cons("Default")} */
    private Default ()
    {
    }
    /*package */ Default (ITree tree, java.util.List < Name > names)
    {
      this.tree = tree;
      this.names = names;
    }
    public IVisitable accept (IASTVisitor visitor)
    {
      return visitor.visitQualifiedNameDefault (this);
    }
    private java.util.List < org.meta_environment.rascal.ast.Name > names;
    public java.util.List < org.meta_environment.rascal.ast.Name > getNames ()
    {
      return names;
    }
    private void $setNames (java.util.List <
			    org.meta_environment.rascal.ast.Name > x)
    {
      this.names = x;
    }
    public Default setNames (java.util.List <
			     org.meta_environment.rascal.ast.Name > x)
    {
      org.meta_environment.rascal.ast.Default z = new Default ();
      z.$setNames (x);
      return z;
    }
  }
  static public class Ambiguity extends QualifiedName
  {
    public QualifiedName.Ambiguity makeQualifiedNameAmbiguity (java.util.
							       List <
							       QualifiedName >
							       alternatives)
    {
      QualifiedName.Ambiguity amb =
	new QualifiedName.Ambiguity (alternatives);
      if (!table.containsKey (amb))
	{
	  table.put (amb, amb);
	}
      return (QualifiedName.Ambiguity) table.get (amb);
    }
    private final java.util.List < QualifiedName > alternatives;
    public Ambiguity (java.util.List < QualifiedName > alternatives)
    {
      this.alternatives =
	java.util.Collections.unmodifiableList (alternatives);
    }
    public java.util.List < QualifiedName > getAlternatives ()
    {
      return alternatives;
    }
  }
}
