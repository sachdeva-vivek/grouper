/* Generated By:JJTree: Do not edit this line. ASTEQNode.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser;

import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.JexlNode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.Parser;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ParserVisitor;

public
class ASTEQNode extends JexlNode {
  public ASTEQNode(int id) {
    super(id);
  }

  public ASTEQNode(Parser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=9b4cefe749cbd821451c8ba315d250cb (do not edit this line) */
