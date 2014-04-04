MyCheapFriend was created using NetBeaNs. 

Team Members: Michael Glass, Huning Dai, Waseem Ilahi and Shaoqing Niu 

directories:
/MyCheapFriend-ejb 
Contains the following:

1. entity beans:
Bill.java
Friend.java
UserObj.java

2. session beans:
AdminLoginBean.java
AdminLoginRemote.java
PollerBean.java   (Timer service)
PollerRemote.java  
UserObjFacade.java
UserObjFacadeRemote.java

3. bussiness logic functions:
Controller.java (Main bussiness logic handler)
EmailInfo.java (Email Parser)
EmailRead.java
EmailSend.java
GmailUtilities.java
TextMessage.java

Note: References in each file

/MyCheapFriend-war
Contains the following:
Servlets accessing java beans:
Administrator.java
disable.java
enable.java
ListUsers.java
LoginHandler.java
LoginUser.java
serviceHandler.java
StartService.java
StopService.java

All the source files are inside the "mycheapfriend" 
package, in both, the ejb and the war modules.
