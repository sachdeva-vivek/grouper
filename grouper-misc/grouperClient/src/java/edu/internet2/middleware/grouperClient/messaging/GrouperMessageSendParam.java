/**
 * @author mchyzer
 * $Id$
 */
package edu.internet2.middleware.grouperClient.messaging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.internet2.middleware.grouperClient.util.GrouperClientUtils;


/**
 * param for sending a message
 */
public class GrouperMessageSendParam {

  /**
   * describes the grouper message system
   */
  private GrouperMessageSystemParam grouperMessageSystemParam;
  
  /**
   * assign th grouper message system param
   * @param theGrouperMessageSystemParam
   * @return this for chaining
   */
  public GrouperMessageSendParam assignGrouperMessageSystemParam(GrouperMessageSystemParam theGrouperMessageSystemParam) {
    this.grouperMessageSystemParam = theGrouperMessageSystemParam;
    return this;
  }
  
  /**
   * assign the grouper messaging system
   * @param theGrouperMessageSystemName
   * @return this for chaining
   */
  public GrouperMessageSendParam assignGropuerMessageSystemName(String theGrouperMessageSystemName) {
    if (this.grouperMessageSystemParam == null) {
      this.grouperMessageSystemParam = new GrouperMessageSystemParam();
    }
    this.grouperMessageSystemParam.assignMesssageSystemName(theGrouperMessageSystemName);
    return this;
  }
  
  /**
   * describes the queue or topic
   */
  private GrouperMessageQueueParam grouperMessageQueueParam;

  /**
   * 
   * @param theGrouperMessageQueueParam
   * @return this for chaining
   */
  public GrouperMessageSendParam assignGrouperMessageQueueParam(GrouperMessageQueueParam theGrouperMessageQueueParam) {
    this.grouperMessageQueueParam = theGrouperMessageQueueParam;
    return this;
  }

  /**
   * assign queue or topic to send the message to
   * @param theQueueOrTopic
   * @return this for chaining
   */
  public GrouperMessageSendParam assignQueueOrTopic(String theQueueOrTopic) {
    if (this.grouperMessageQueueParam == null) {
      this.grouperMessageQueueParam = new GrouperMessageQueueParam();
    }
    this.grouperMessageQueueParam.assignQueueOrTopic(theQueueOrTopic);
    return this;
  }
  
  
  /**
   * message body for the message
   */
  private List<GrouperMessage> grouperMessages = new ArrayList<GrouperMessage>();

  /**
   * message body for the message
   * @param theMessageBody
   * @return this for chaining
   */
  public GrouperMessageSendParam addMessageBody(String theMessageBody) {
    GrouperMessage grouperMessage = new GrouperMessageDefault();
    grouperMessage.setMessageBody(theMessageBody);
    this.grouperMessages.add(grouperMessage);
    return this;
  }

  
  /**
   * @return the grouperMessageSystemParam
   */
  public GrouperMessageSystemParam getGrouperMessageSystemParam() {
    return this.grouperMessageSystemParam;
  }

  
  /**
   * @return the grouperMessageQueueParam
   */
  public GrouperMessageQueueParam getGrouperMessageQueueParam() {
    return this.grouperMessageQueueParam;
  }

  /**
   * assign multiple message bodies
   * @param theMessageBodies
   * @return this for chaining
   */
  public GrouperMessageSendParam assignMessageBodies(Collection<String> theMessageBodies) {
    
    this.grouperMessages.clear();
    for (String theMessageBody : GrouperClientUtils.nonNull(theMessageBodies)) {
      this.addMessageBody(theMessageBody);
    }
    return this;
  }
  
  /**
   * add a grouper message to send
   * @param theGrouperMessage
   * @return this for chaining
   */
  public GrouperMessageSendParam addGrouperMessage(GrouperMessage theGrouperMessage) {
    this.grouperMessages.add(theGrouperMessage);
    return this;
  }
  
  /**
   * assign grouper messages to send
   * @param theGrouperMessages
   * @return this for chaining
   */
  public GrouperMessageSendParam assignGrouperMessages(Collection<GrouperMessage> theGrouperMessages) {
    
    this.grouperMessages.clear();
    for (GrouperMessage theMessage : GrouperClientUtils.nonNull(theGrouperMessages)) {
      this.addGrouperMessage(theMessage);
    }
    return this;
  }
  
  /**
   * get the grouper messages
   * @return messages
   */
  public Collection<GrouperMessage> getGrouperMessages() {
    return this.grouperMessages;
  }

  /**
   * 
   */
  public GrouperMessageSendParam() {
  }

}
