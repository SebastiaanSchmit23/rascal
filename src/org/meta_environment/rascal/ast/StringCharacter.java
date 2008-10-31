package org.meta_environment.rascal.ast;
import org.eclipse.imp.pdb.facts.ITree;
public abstract class StringCharacter extends AbstractAST
{
  static public class Lexical extends StringCharacter
  {
    /* UnicodeEscape -> StringCharacter  */
  } static public class Ambiguity extends StringCharacter
  {
    public StringCharacter.Ambiguity makeStringCharacterAmbiguity (java.util.
								   List <
								   StringCharacter
								   >
								   alternatives)
    {
      StringCharacter.Ambiguity amb =
	new StringCharacter.Ambiguity (alternatives);
      if (!table.containsKey (amb))
	{
	  table.put (amb, amb);
	}
      return (StringCharacter.Ambiguity) table.get (amb);
    }
    private final java.util.List < StringCharacter > alternatives;
    public Ambiguity (java.util.List < StringCharacter > alternatives)
    {
      this.alternatives =
	java.util.Collections.unmodifiableList (alternatives);
    }
    public java.util.List < StringCharacter > getAlternatives ()
    {
      return alternatives;
    }
  }
}
