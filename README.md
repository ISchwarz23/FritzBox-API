# FritzBox-API
Access your FritzBox from any Java application using this API.
Tested on Fritz!Box 7390 (Fritz!OS 06.20)

This API is using the WebApp running on the FritzBox for interaction. So whether Telnet nor UPnP have to be activated.


## Functionalities
- log in
- get public IP
- reconnect to internet (get new IP)
- turn on guest WiFi access point
- configure guest WiFi access point
- receive caller list


## Usage Examples

### Receiving new IP
```java
final FritzBoxInterface fritzBox = new FritzBoxConnector().login("myPassword");
System.out.println("Old IP: " + fritzBox.getInternetIP());
fritzBox.reconnectToInternet();
System.out.println("New IP: " + fritzBox.getInternetIP());
```

### Receiving caller list
```java
final FritzBoxInterface fritzBox = new FritzBoxConnector().login("myPassword");
final List<Call> callList = fritzBox.getCallList();
for (final Call call : callList) {
	System.out.println(call);
}
```

### Turn on Guest WiFi (simple mode)
```java
final FritzBoxInterface fritzBox = new FritzBoxConnector().login("myPassword");
fritzBox.turnOnGuestWiFi("MyGuest WiFi", "myPassword");
```

### Turn on Guest WiFi (advanced mode)
```java
final boolean activatePushService = true;
final boolean limitOnWebAccess = true;
final boolean allowCommunicationBetweenClients = false;
final FritzBoxInterface fritzBox = new FritzBoxConnector().login("myPassword");
fritzBox.turnOnGuestWiFi("MyGuest WiFi", SecurityMode.WPA2, "myPassword", activatePushService, limitOnWebAccess, allowCommunicationBetweenClients);
```

### Turn off Guest WiFi
```java
final FritzBoxInterface fritzBox = new FritzBoxConnector().login("myPassword");
fritzBox.turnOffGuestWiFi();
```


## Dependencies
- slf4j-api-1.7.10
- slf4j-simple-1.7.10
