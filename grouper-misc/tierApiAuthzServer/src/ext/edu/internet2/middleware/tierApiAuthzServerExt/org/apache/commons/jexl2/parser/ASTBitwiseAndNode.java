/* Generated By:JJTree: Do not edit this line. ASTBitwiseAndNode.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser;

import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.JexlNode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.Parser;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ParserVisitor;

public
class ASTBitwiseAndNode extends JexlNode {
  public ASTBitwiseAndNode(int id) {
    super(id);
  }

  public ASTBitwiseAndNode(Parser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=9e7b215b948b0b07a58ecc6192b85a8c (do not edit this line) */
