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
import  junit.framework.*;
import  org.apache.commons.logging.*;

/**
 * @author  blair christensen.
 * @version $Id: TestCompositeU6.java,v 1.4 2006-08-30 18:35:38 blair Exp $
 */
public class TestCompositeU6 extends TestCase {

  // Private Static Class Constants
  private static final Log LOG = LogFactory.getLog(TestCompositeU6.class);

  public TestCompositeU6(String name) {
    super(name);
  }

  protected void setUp () {
    LOG.debug("setUp");
    RegistryReset.reset();
  }

  protected void tearDown () {
    LOG.debug("tearDown");
  }

  public void testFailToAddCompositeMemberWhenHasMember() {
    LOG.info("testFailToAddCompositeMemberWhenHasMember");
    try {
      R     r = R.populateRegistry(1, 3, 1);
      Group a = r.getGroup("a", "a");
      Group b = r.getGroup("a", "b");
      Group c = r.getGroup("a", "c");
      a.addMember( r.getSubject("a") );
      try {
        a.addCompositeMember(CompositeType.UNION, b, c);
        Assert.fail("FAIL: expected exception: " + E.GROUP_ACTM);
      }
      catch (MemberAddException eMA) {
        Assert.assertTrue("OK: cannot add composite member to group with mship", true);
        T.string("error message", E.GROUP_ACTM, eMA.getMessage());
      }
      finally {
        r.rs.stop();
      }
    }
    catch (Exception e) {
      T.e(e);
    }
  } // public void testFailToAddCompositeMemberWhenHasMember()

}

