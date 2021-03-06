<h1 align="center">
  <br>
  <a href=""><img src="https://i.postimg.cc/fL5xFTvZ/encrypted-data.png" alt="Quanage"></a>
</h1>

<h4 align="center">Quanage Android</h4>

<p align="center">
    <a href="https://github.com/Aemerse/Quanage-Android/commits/master">
    <img src="https://img.shields.io/github/last-commit/Aemerse/Quanage-Android.svg?style=flat-square&logo=github&logoColor=white"
         alt="GitHub last commit"></a>
    <a href="https://github.com/Aemerse/Quanage-Android/issues">
    <img src="https://img.shields.io/github/issues-raw/Aemerse/Quanage-Android.svg?style=flat-square&logo=github&logoColor=white"
         alt="GitHub issues"></a>
    <a href="https://github.com/Aemerse/Quanage-Android/pulls">
    <img src="https://img.shields.io/github/issues-pr-raw/Aemerse/Quanage-Android.svg?style=flat-square&logo=github&logoColor=white"
         alt="GitHub pull requests"></a>
</p>
      
<p align="center">
  <a href="#about">About</a> •
  <a href="#development">Development</a> •
<!--   <a href="#downloading">Downloading</a> • -->
  <a href="#contributing">Contributing</a> •
  <a href="#issues">Issues</a> •
  <a href="#license">License</a>
</p>

---

## About

Quanage for Android is a mobile app for the application of Quantum Encryption on the Android platform. Here we establish two randomly generated keys and keep a track of them leveraging a quantum computer provided by IBM Quantum using their APIs. The app is a live demonstration of the QKD concepts and tends out to be the first ever truly secure application for exchange of data across the internet.

In order to share messages with someone, the users first generate a random key using QRNG, the Qiskit SDK and IBM Quantum APIs, which is then shared amongst them through peer to peer networks for the first time. After this, the keys are monitored by a server and read/write access to the key is only provided to the app and any foreign interaction will trigger a function call to reset the key and discard the previous one.
We are happy to say that the demonstration works and is currently implemented in the app which we have made.

[![Screenshot-20211129-122625.png](https://i.postimg.cc/sxDK1TzR/Screenshot-20211129-122625.png)](https://postimg.cc/2bMnKF3H)

## Development
	    
* Prerequisite: Latest version of the Android Studio and SDKs on your pc.
* Clone this repository.
* Use the `gradlew build` command to build the project directly or use the IDE to run the project to your phone or the emulator.

<!-- ## Downloading

* [Google Play Store](https://play.google.com/store/apps/details?id=org.aemerse.quanage) -->
	    	    
## Contributing
	  
Got **something interesting** you'd like to **ask or share**? Create a pull request.
	    
## Issues
	  
Create an issue on GitHub.

## License

This Project is licensed under the [GPL version 3 or later](https://www.gnu.org/licenses/gpl-3.0.html) with sections under the [Apache License version](https://www.apache.org/licenses/LICENSE-2.0.html) 
