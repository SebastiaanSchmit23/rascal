package org.meta_environment.rascal.ast;
import org.eclipse.imp.pdb.facts.ITree;
public abstract class TypeArg extends AbstractAST
{
  static public class Default extends TypeArg
  {
/* type:Type -> TypeArg {cons("Default")} */
    private Default ()
    {
    }
    /*package */ Default (ITree tree,
			  org.meta_environment.rascal.ast.Type type)
    {
      this.tree = tree;
      this.type = type;
    }
    public IVisitable accept (IASTVisitor visitor)
    {
      return visitor.visitTypeArgDefault (this);
    }
    private org.meta_environment.rascal.ast.Type type;
    public org.meta_environment.rascal.ast.Type getType ()
    {
      return type;
    }
    private void $setType (org.meta_environment.rascal.ast.Type x)
    {
      this.type = x;
    }
    public Default setType (org.meta_environment.rascal.ast.Type x)
    {
      org.meta_environment.rascal.ast.Default z = new Default ();
      z.$setType (x);
      return z;
    }
  }
  static public class Ambiguity extends TypeArg
  {
    public TypeArg.Ambiguity makeTypeArgAmbiguity (java.util.List < TypeArg >
						   alternatives)
    {
      TypeArg.Ambiguity amb = new TypeArg.Ambiguity (alternatives);
      if (!table.containsKey (amb))
	{
	  table.put (amb, amb);
	}
      return (TypeArg.Ambiguity) table.get (amb);
    }
    private final java.util.List < TypeArg > alternatives;
    public Ambiguity (java.util.List < TypeArg > alternatives)
    {
      this.alternatives =
	java.util.Collections.unmodifiableList (alternatives);
    }
    public java.util.List < TypeArg > getAlternatives ()
    {
      return alternatives;
    }
  }
  static public class Named extends TypeArg
  {
/* type:Type name:Name -> TypeArg {cons("Named")} */
    private Named ()
    {
    }
    /*package */ Named (ITree tree, org.meta_environment.rascal.ast.Type type,
			org.meta_environment.rascal.ast.Name name)
    {
      this.tree = tree;
      this.type = type;
      this.name = name;
    }
    public IVisitable accept (IASTVisitor visitor)
    {
      return visitor.visitTypeArgNamed (this);
    }
    private org.meta_environment.rascal.ast.Type type;
    public org.meta_environment.rascal.ast.Type getType ()
    {
      return type;
    }
    private void $setType (org.meta_environment.rascal.ast.Type x)
    {
      this.type = x;
    }
    public Named setType (org.meta_environment.rascal.ast.Type x)
    {
      org.meta_environment.rascal.ast.Named z = new Named ();
      z.$setType (x);
      return z;
    }
    private org.meta_environment.rascal.ast.Name name;
    public org.meta_environment.rascal.ast.Name getName ()
    {
      return name;
    }
    private void $setName (org.meta_environment.rascal.ast.Name x)
    {
      this.name = x;
    }
    public Named setName (org.meta_environment.rascal.ast.Name x)
    {
      org.meta_environment.rascal.ast.Named z = new Named ();
      z.$setName (x);
      return z;
    }
  }
}
