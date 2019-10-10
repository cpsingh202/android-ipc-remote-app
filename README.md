# android-ipc-remote-app
Remote app to respond the client request through IPC

## Summary
Remote app is responsible for processing the client app request and send back the respective response. App is not supposed to be running to process the request, it should only be installed on same device Client app is running on. 

It incorporates the use of Service, Messanger and Handler classes to keep going the communication seemlessly. For message echoing, it makes the server call to postman-echo API which in returns echo back the message in json wrapper.

## Prerequisites
It should be installed before the client app and internet connection is required for message echoing part.

## Alternate Solutions
1. Use the `IntentService` to receive the input at Remote app end and send back the result via PendingIntent or Messanger or Broadcast Intent or ResultReceiver.
2. Use the `Broadcast Intent` to send & receive the data. `Intent Filters` can be defined by each application based on the type of data they receive.
3. Use `Content Provider` at Client app to store the data & access them at Remote App via `ContentResolver`.
4. We can directly access the activity of other application if they are runnning in same process, hence we can user the Intent to send the data back and forth.

Solution implemented in this app is by using `Bound Services` which optimizes the implementation as compared to above solutions: You don't need to declare any `Intent Filters` to receive data, you wake the Remote app only when Client app needs to communicate and sends backs to sleep once Client app is done with communication. Also, you don't need to worry about in which process these apps are running as we are not directly accessing their classes/methods. We are not storing the data at any common place like `Content Provider`, hence it removes the data security concern.
