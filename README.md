# Reproducer for temp file creation issue


## Background
I have a complex Gluon Mobile application I can't share that I was previously building with Gluon GraalVM 21.2. It creates a temporary file and this works fine on Android. I have just updated to Gluon GraalVM 22 and now it fails to create the temporary file. I have reproduced the issue with this simple example.

This application was created using the Gluon Mobile Eclipse plugin (single view project). The versions of the various dependencies and plugins have been updated to their latest versions. I also added storage permissions to `AndroidManifest.xml`. The temporary file creation happens in `GluonApplication`'s constructor.

I have tried building with various versions of GraalVM 22:
* Oracle GraalVM CE 22.0.0.2 JDK 11
* Oracle GraalVM CE 22.0.0.2 JDK 17
* Gluon GraalVM 22.0.0.3 JDK 11
* Gluon GraalVM 22.0.0.3 JDK 17

The JDK 11 versions work. The JDK 17 versions do not. This has been reproduced on phones running Android 10, 11, and 12.


## Reproducing the issue
This is a maven project, so in Linux (I used an Ubuntu 20.04 VM) to run do this:

```
mvn -Pandroid gluonfx:build gluonfx:package gluonfx:install gluonfx:nativerun
```

The temp file is created during `GluonApplication`'s constructor and a message is printed with the result.

It appears to work with the JDK 11 versions of GraalVM 22 (both Oracle's and Gluon's), but not with the JDK 17 versions of either.

When it works it looks like this (buried among all the output):

```
[Tue. Feb. 08 14:34:16 EST 2022][INFO] [SUB] D/GraalCompiled(11718): Created temp file at: /data/user/0/com.gluonapplication.gluonmobilesingleviewproject/9221306799863213037.tmp
```

When it fails it looks like this:

```
[Tue. Feb. 08 13:39:45 EST 2022][INFO] [SUB] D/GraalCompiled( 5369): Failed to create temp file
[Tue. Feb. 08 13:39:45 EST 2022][INFO] [SUB] D/GraalCompiled( 5369): java.nio.file.NoSuchFileException: /tmp/13973661146619086989.tmp
[Tue. Feb. 08 13:39:45 EST 2022][INFO] [SUB] D/GraalCompiled( 5369): 	at sun.nio.fs.UnixFileSystemProvider.newByteChannel(UnixFileSystemProvider.java:218)
[Tue. Feb. 08 13:39:45 EST 2022][INFO] [SUB] D/GraalCompiled( 5369): 	at java.nio.file.Files.newByteChannel(Files.java:380)
[Tue. Feb. 08 13:39:45 EST 2022][INFO] [SUB] D/GraalCompiled( 5369): 	at java.nio.file.Files.createFile(Files.java:658)
[Tue. Feb. 08 13:39:45 EST 2022][INFO] [SUB] D/GraalCompiled( 5369): 	at java.nio.file.TempFileHelper.create(TempFileHelper.java:136)
[Tue. Feb. 08 13:39:45 EST 2022][INFO] [SUB] D/GraalCompiled( 5369): 	at java.nio.file.TempFileHelper.createTempFile(TempFileHelper.java:159)
[Tue. Feb. 08 13:39:45 EST 2022][INFO] [SUB] D/GraalCompiled( 5369): 	at java.nio.file.Files.createTempFile(Files.java:923)
[Tue. Feb. 08 13:39:45 EST 2022][INFO] [SUB] D/GraalCompiled( 5369): 	at com.gluonapplication.GluonApplication.<init>(GluonApplication.java:17)
[Tue. Feb. 08 13:39:45 EST 2022][INFO] [SUB] D/GraalCompiled( 5369): 	at java.lang.reflect.Constructor.newInstanceWithCaller(Constructor.java:499)
[Tue. Feb. 08 13:39:45 EST 2022][INFO] [SUB] D/GraalCompiled( 5369): 	at java.lang.reflect.Constructor.newInstance(Constructor.java:480)
[Tue. Feb. 08 13:39:45 EST 2022][INFO] [SUB] D/GraalCompiled( 5369): 	at com.sun.javafx.application.LauncherImpl.lambda$launchApplication1$8(LauncherImpl.java:803)
[Tue. Feb. 08 13:39:45 EST 2022][INFO] [SUB] D/GraalCompiled( 5369): 	at com.sun.javafx.application.PlatformImpl.lambda$runAndWait$12(PlatformImpl.java:484)
[Tue. Feb. 08 13:39:45 EST 2022][INFO] [SUB] D/GraalCompiled( 5369): 	at com.sun.javafx.application.PlatformImpl.lambda$runLater$10(PlatformImpl.java:457)
[Tue. Feb. 08 13:39:45 EST 2022][INFO] [SUB] D/GraalCompiled( 5369): 	at java.security.AccessController.doPrivileged(AccessController.java:107)
[Tue. Feb. 08 13:39:45 EST 2022][INFO] [SUB] D/GraalCompiled( 5369): 	at com.sun.javafx.application.PlatformImpl.lambda$runLater$11(PlatformImpl.java:456)
[Tue. Feb. 08 13:39:45 EST 2022][INFO] [SUB] D/GraalCompiled( 5369): 	at com.sun.glass.ui.monocle.RunnableProcessor.runLoop(RunnableProcessor.java:92)
[Tue. Feb. 08 13:39:45 EST 2022][INFO] [SUB] D/GraalCompiled( 5369): 	at com.sun.glass.ui.monocle.RunnableProcessor.run(RunnableProcessor.java:51)
[Tue. Feb. 08 13:39:45 EST 2022][INFO] [SUB] D/GraalCompiled( 5369): 	at java.lang.Thread.run(Thread.java:833)
[Tue. Feb. 08 13:39:45 EST 2022][INFO] [SUB] D/GraalCompiled( 5369): 	at com.oracle.svm.core.thread.JavaThreads.threadStartRoutine(JavaThreads.java:597)
[Tue. Feb. 08 13:39:45 EST 2022][INFO] [SUB] D/GraalCompiled( 5369): 	at com.oracle.svm.core.posix.thread.PosixJavaThreads.pthreadStartRoutine(PosixJavaThreads.java:194)
```

One potentially suspicious element is that the path it reports when it fails is completely different (in `/tmp` instead of `/data/user/0/com.gluonapplication.gluonmobilesingleviewproject`).
