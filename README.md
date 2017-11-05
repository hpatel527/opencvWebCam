# opencvWebCam
POC for OpenCV JavaFX Camera

Instructions on how to setup Netbeans Project 

1. Import as Netbeans Project via Netbeans or pull using Git inside Netbeans
2. Download OpenCV 3.0 via https://opencv.org/releases.html
3. Open and Extract OPENCV 3.0 somewhere and remember where the installation is for later.
4. Find the OpenCV_java331.dll inside your opencv installation (ex. "opencvinstalllocation"\opencv\build\java\x64 and copy/paste it into your windows/system32 folder. This is so the jvm can locate the opencv library for the approriate tasks.
5. Right click Libraries and click Add Library. Select Create and name it opencv. Click Add JAR and locate the opencv-331.jar in your opencv installation. (ex. "opencvinstall"\opencv\build\java). Then click ok/finish.
6. Right click the Project in the Project Explorer and select properties. Go to the run tab and in the VM options field insert the path to your opencv javadll. (ex. -Djava.libary.path="opencvinstall\opencv\build\java\x64"). Click OK
7. Right click project and select Clean and Build

You should now be able to run the project


