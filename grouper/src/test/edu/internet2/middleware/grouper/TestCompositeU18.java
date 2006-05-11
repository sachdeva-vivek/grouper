/*
  Copyright 2004-2006 University Corporation for Advanced Internet Development, Inc.
  Copyright 2004-2006 The University Of Chicago

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package edu.internet2.middleware.grouper;

import  edu.internet2.middleware.subject.*;
import  edu.internet2.middleware.subject.provider.*;
import  java.util.*;
import  junit.framework.*;
import  org.apache.commons.logging.*;

/**
 * @author  blair christensen.
 * @version $Id: TestCompositeU18.java,v 1.1.2.1 2006-05-11 17:29:40 blair Exp $
 */
public class TestCompositeU18 extends TestCase {

  // Private Static Class Constants
  private static final Log LOG = LogFactory.getLog(TestCompositeU18.class);

  public TestCompositeU18(String name) {
    super(name);
  }

  protected void setUp () {
    LOG.debug("setUp");
    RegistryReset.reset();
  }

  protected void tearDown () {
    LOG.debug("tearDown");
  }

  public void testDelUnionWithTwoCompositeChildrenAndNoParents() {
    LOG.info("testDelUnionWithTwoCompositeChildrenAndNoParents");
    try {
      R       r     = R.populateRegistry(1, 7, 4);
      // Feeder Groups
      Subject subjA = r.getSubject("a");
      Group   a     = r.getGroup("a", "a");
      a.addMember(subjA);
      Subject subjB = r.getSubject("b");
      Group   b     = r.getGroup("a", "b");
      b.addMember(subjB);
      Subject subjC = r.getSubject("c");
      Group   c     = r.getGroup("a", "c");
      c.addMember(subjC);
      Subject subjD = r.getSubject("d");
      Group   d     = r.getGroup("a", "d");
      d.addMember(subjD);
      // Feeder Composite Groups
      Group   e     = r.getGroup("a", "e");
      e.addCompositeMember(CompositeType.UNION, a, b);
      Group   f     = r.getGroup("a", "f");
      f.addCompositeMember(CompositeType.UNION, c, d);
      // And our ultimate composite group
      Group   g     = r.getGroup("a", "g");
      g.addCompositeMember(CompositeType.UNION, e, f);
      g.deleteCompositeMember();
      // And test
      Assert.assertFalse( "a !hasComposite" , a.hasComposite()  );
      Assert.assertFalse( "b !hasComposite" , b.hasComposite()  );
      Assert.assertFalse( "c !hasComposite" , c.hasComposite()  );
      Assert.assertFalse( "d !hasComposite" , d.hasComposite()  );
      Assert.assertTrue(  "e hasComposite"  , e.hasComposite()  );
      Assert.assertTrue(  "f hasComposite"  , f.hasComposite()  );
      Assert.assertFalse( "g !hasComposite" , g.hasComposite()  );

      Assert.assertTrue(  "a isComposite"   , a.isComposite()   );
      Assert.assertTrue(  "b isComposite"   , b.isComposite()   );
      Assert.assertTrue(  "c isComposite"   , c.isComposite()   );
      Assert.assertTrue(  "d isComposite"   , d.isComposite()   );
      Assert.assertFalse( "e !isComposite"  , e.isComposite()   );
      Assert.assertFalse( "f !isComposite"  , f.isComposite()   );
      Assert.assertFalse( "g !isComposite"  , g.isComposite()   );

      T.amount( "a members", 1, a.getMembers().size() );
      Assert.assertTrue(  "a has subjA",  a.hasMember(subjA)  );
      T.amount( "b members", 1, b.getMembers().size() );
      Assert.assertTrue(  "b has subjB",  b.hasMember(subjB)  );
      T.amount( "c members", 1, c.getMembers().size() );
      Assert.assertTrue(  "c has subjC",  c.hasMember(subjC)  );
      T.amount( "d members", 1, d.getMembers().size() );
      Assert.assertTrue(  "d has subjD",  d.hasMember(subjD)  );
      T.amount( "e members", 2, e.getMembers().size() );
      Assert.assertTrue(  "e has subjA",  e.hasMember(subjA)  );
      Assert.assertTrue(  "e has subjB",  e.hasMember(subjB)  );
      T.amount( "f members", 2, f.getMembers().size() );
      Assert.assertTrue(  "f has subjC",  f.hasMember(subjC)  );
      Assert.assertTrue(  "f has subjD",  f.hasMember(subjD)  );
      T.amount( "g members", 0, g.getMembers().size() );

      r.rs.stop();
    }
    catch (Exception e) {
      T.e(e);
    }
  } // public void testDelUnionWithTwoCompositeChildrenAndNoParents()

}

