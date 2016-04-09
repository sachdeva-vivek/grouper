<%@ include file="../assetsJsp/commonTaglib.jsp"%>

                <li class="dropdown"><a data-toggle="dropdown" href="#" class="dropdown-toggle">${textContainer.text['groupMoreTab'] } <b class="caret"></b></a>
                  <ul class="dropdown-menu">
                    <li><a href="#" onclick="return guiV2link('operation=UiV2Group.thisGroupsMemberships&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}', {dontScrollTop: true});">${textContainer.text['thisGroupsMembershipsTab'] }</a></li>
                    <li><a href="#" onclick="return guiV2link('operation=UiV2Group.thisGroupsGroupPrivileges&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}', {dontScrollTop: true});">${textContainer.text['thisGroupsGroupPrivilegesTab'] }</a></li>
                    <li><a href="#" onclick="return guiV2link('operation=UiV2Group.thisGroupsStemPrivileges&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}', {dontScrollTop: true});">${textContainer.text['thisGroupsStemPrivilegesTab'] }</a></li>
                    <li><a href="#" onclick="return guiV2link('operation=UiV2Group.thisGroupsAttributeDefPrivileges&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}', {dontScrollTop: true});">${textContainer.text['thisGroupsAttributeDefPrivilegesTab'] }</a></li>
                    <c:if test="${grouperRequestContainer.groupContainer.canReadPrivilegeInheritance}">   
                      <li><a href="#" onclick="return guiV2link('operation=UiV2Group.thisGroupsPrivilegesInheritedFromFolders&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}', {dontScrollTop: true});">${textContainer.text['thisGroupsPrivilegesFromFolders'] }</a></li>
                    </c:if>
                    <c:if test="${grouperRequestContainer.rulesContainer.canReadPrivilegeInheritance}">   
                      <li><a href="#" onclick="return guiV2link('operation=UiV2Group.inheritedPrivilegesAssignedToThisGroupFromFolders&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}', {dontScrollTop: true});">${textContainer.text['inheritedPrivilgesAssignedToThisGroupFromFolders'] }</a></li>
                    </c:if>
                  </ul>
                </li>
