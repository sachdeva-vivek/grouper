/*
 * Copyright (C) 2004 The University Of Chicago
 * All Rights Reserved. 
 *
 * You may use and distribute under the same terms as Grouper itself
 */


/**
 * Sample loader for adding groups and memberships to the registry
 * using the {@link Grouper} API.
 * <p />
 * See <i>README</i> for more information.
 * 
 * @author  blair christensen.
 * @version $Id: csv2group.java,v 1.4 2004-12-05 04:13:18 blair Exp $ 
 */

import  edu.internet2.middleware.grouper.*;
import  edu.internet2.middleware.subject.*;
import  java.io.*;
import  java.util.*;
import  org.apache.commons.cli.*;


class csv2group {

  /*
   * PRIVATE CONSTANTS
   */
  private static final String NAME = "csv2group";


  /*
   * PRIVATE CLAS VARIABLES
   */
  private static CommandLine    cmd;
  private static GrouperMember  mem;
  private static Options        options;
  private static String         path;
  private static GrouperSession s;
  private static Subject        subj;
  private static String         subjectID;
  private static boolean        verbose = false;


  /*
   * PUBLIC CLASS METHODS
   */
  public static void main(String[] args) {
    _opts(args);            // Parse and handle command line options
    _grouperStart();        // Initialize Grouper and start session
    _cvsReadAndExecute();   // Read and parse input file
    _grouperStop();         // And we're done.  Tidy up.
    System.exit(0);
  }


  /*
   * PRIVATE CLASS METHODS
   */

  /* (!javadoc)
   * Read in CSV file and execute accordingly
   */
  private static void _cvsReadAndExecute() {
    if (path != null) {
      try { 
        BufferedReader  br    = new BufferedReader(new FileReader(path));
        String          line  = null; 
        while ((line=br.readLine()) != null){ 
          StringTokenizer st = new StringTokenizer(line, ",");
          // FIXME Blindly assume that if we have two tokens, they are two
          //       *good* tokens
          if (st.countTokens() == 4) {
            String category = st.nextToken();
            String action   = st.nextToken();
            String stem     = st.nextToken();
            String extn     = st.nextToken();
            _verbose(
              "Found cat=`" + category + "', action=`" + action + 
              "', stem=`" + stem + "', extn=`" + extn + "'"
            );
            if (!_dispatch(category, action, stem, extn)) {
              System.err.println("ERROR: " + line);
            }
          } else {
            System.err.println("Skipping, invalid format: '" + line + "'");
          }
        } 
        br.close(); 
      } catch (IOException e) { 
        System.err.println("Error processing '" + path + "': " + e);
      }
    } else {
      _usage();
    }
  }

  /* (!javadoc)
   * CSV Dispatch Handler
   */
  private static boolean _dispatch( 
                           String cat, String act, 
                           String stem, String extn
                         )
  {
    boolean rv = false;
    if        (cat.equals("group")) {
      rv = _dispatchGroup(act, stem, extn);
    } else if (cat.equals("stem")) {
      rv = _dispatchStem(act, stem, extn);
    } else {
      System.err.println("ERROR: Invalid category: `" + cat + "'");
    }
    return rv;
  }

  /* (!javadoc)
   * Group Dispatch Handler
   */
  private static boolean _dispatchGroup(
                           String act, String stem, String extn
                         )
  {
    boolean rv = false;
    if (act.equals("add")) {
      rv = _groupAdd(stem, extn);
    } else {
      System.err.println("ERROR: Invalid group action: `" + act + "'");
    }
    return rv;
  }

  /* (!javadoc)
   * Stem Dispatch Handler
   */
  private static boolean _dispatchStem(
                           String act, String stem, String extn
                         )
  {
    boolean rv = false;
    if (act.equals("add")) {
      rv = _stemAdd(stem, extn);
    } else {
      System.err.println("ERROR: Invalid stem action: `" + act + "'");
    }
    return rv;
  }

  /* (!javadoc)
   * Add a group to the registry.
   */
  private static boolean _groupAdd(String stem, String extn) {
    boolean rv = false;
    GrouperGroup g = GrouperGroup.create(
                       s, stem, extn, Grouper.DEF_GROUP_TYPE
                     );
    if (g != null) {
      _verbose("Added group: " + g);
      rv = true;
    } else {
      System.err.println(
        "Failed to add group=`" + stem + ", extn=`" + extn + "'"
      );
    }
    return rv;
  }

  /* (!javadoc)
   * Initialize the {@link Grouper} environment and start a 
   * {@link GrouperSession}.
   */
  private static void _grouperStart() {
    _subject();
    s = new GrouperSession();
    s.start(subj);
    _verbose(
             "Started session as "           + 
             subj.getId() + ":"              +
             subj.getSubjectType().getId()
            );
    mem = GrouperMember.lookup(subj);
    _verbose("Loaded member " + mem.subjectID() + ":" + mem.typeID());
  }

  /* (!javadoc)
   * Stop the {@link Grouper} session.
   */
  private static void _grouperStop() {
    s.stop();
  }

  /* (!javadoc)
   * Handle command line options.
   */
  private static void _opts(String[] args) {
    _optsParse(args); // Parse CLI options
    _optsProcess();   // Handle CLI options
  }

  /* (!javadoc)
   * Parse command line options.
   * <p />
   * @param   String array.
   */
  private static void _optsParse(String[] args) {
    options = new Options();
    options.addOption(
                      OptionBuilder.withArgName("file")
                                   .withDescription(
                                     "Specify input file [REQUIRED]"
                                    )
                                   .hasArg()
                                   .create("f")
                     );
    options.addOption("h", false, "Print usage information");
    options.addOption("v", false, "Be more verbose");
    CommandLineParser parser = new PosixParser();
    try {
      cmd = parser.parse(options, args);
    } catch (ParseException e) {
      System.err.println("Unable to parse command line options: " + e.getMessage());
      System.exit(1);
    }
  }

  /* (!javadoc)
   *
   * Process command line options.
   */
  private static void _optsProcess() {
    if (cmd == null) {
      System.err.println("Error parsing command line options!");
      System.exit(1);
    }
    // Handle help first
    if (cmd.hasOption("h")) {
      _usage();
      System.exit(0);
    }
    // And then verbose because it may affect output later in this
    // method
    if (cmd.hasOption("v")) {
      verbose = true;
      _verbose("Enabling verbose mode");
    }
    // And now everything else
    if (cmd.hasOption("f")) {
      path = cmd.getOptionValue("f");
      _verbose("Using input file '" + path + "'");
    }
  }

  /* (!javadoc)
   * Add a group to the registry.
   */
  private static boolean _stemAdd(String stem, String extn) {
    boolean rv = false;
    // TODO Bah.  I have to do the interpolation for the config file.
    if (stem.equals("Grouper.NS_ROOT")) {
      stem = Grouper.NS_ROOT;
    }
    GrouperGroup g = GrouperGroup.create(s, stem, extn, Grouper.NS_TYPE);
    if (g != null) {
      _verbose("Added stem: " + g);
      rv = true;
    } else {
      System.err.println(
        "Failed to add stem=`" + stem + ", extn=`" + extn + "'"
      );
    }
    return rv;
  }

  /* (!javadoc)
   * Instantiate a subject via the {@link Subject} interface.
   */
  private static void _subject() {
    if (subjectID == null) {
      _verbose("Using default subjectID");
      subjectID = Grouper.config("member.system");
      if (subjectID == null) {
        System.err.println("Unable to retrieve default subjectID!");
        System.exit(1);
      }
    }
    _verbose("Using default subjectTypeID (" + Grouper.DEF_SUBJ_TYPE + ")");
    _verbose("Looking up subjectID '" + subjectID + "'");
    subj = GrouperSubject.lookup(subjectID, Grouper.DEF_SUBJ_TYPE);
  }

  /* (!javadoc)
   *
   * Print usage information.
   */
  private static void _usage() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(NAME, options);
  }

  /* (!javadoc)
   *
   * Conditionally print messages depending upon verbosity level.
   * <p />
   * @param   msg Message to print if running verbosely.
   */
  private static void _verbose(String msg) {
    if (verbose == true) {
      System.err.println(msg);
    }
  }

}

