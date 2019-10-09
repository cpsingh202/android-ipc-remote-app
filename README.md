# android-ipc-remote-app
Remote app to respond the client request through IPC

## Summary
Remote app is responsible for processing the client app request and send back the respective response. App is not supposed to be running to process the request, it should only be installed on same device Client app is running on. 

It incorporates the use of Service, Messanger and Handler classes to keep going the communication seemlessly. For message echoing, it makes the server call to postman-echo API which in returns echo back the message in json wrapper.

## Prerequisites
It should be installed before the client app and internet connection is required for message echoing part.
