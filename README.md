# FinLand
An app that lets the user upload satellite images of land from his phone or google drive and uses ML to predict what type of land it is

## Setup
Go to [this](https://github.com/diptangsu/FinLand-API) repository and set up a `Flask` web server in your local machine.

Open up a terminal and find out your private IP address by typing the command `ifconfig`, for Windows users, open up command prompt and type in `ipconfig` and look for `IPv4 address`

Go to [MainActivity.java](https://github.com/morninigstar/FinLand/blob/master/app/src/main/java/com/morningstar/finland/ui/MainActivity.java) and change this line
```java
private final String URL_POST_IMAGE = "http://192.168.1.104:5000/upload";
```
Edit this line and enter your own private IP address.

Build and run app.
