@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  aurel startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and AUREL_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\aurel-0.0.1.jar;%APP_HOME%\lib\ktor-server-auth-jwt-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\ktor-server-auth-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\ktor-server-sessions-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\ktor-server-cors-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\ktor-server-call-logging-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\ktor-server-status-pages-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\ktor-server-html-builder-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\ktor-server-content-negotiation-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\koin-ktor-3.1.5.jar;%APP_HOME%\lib\ktor-server-netty-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\ktor-server-host-common-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\ktor-server-core-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\kotlinx-html-jvm-0.7.3.jar;%APP_HOME%\lib\ktor-serialization-gson-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\ktor-client-jackson-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\koin-core-jvm-3.1.5.jar;%APP_HOME%\lib\kmongo-coroutine-4.4.0.jar;%APP_HOME%\lib\kmongo-coroutine-core-4.4.0.jar;%APP_HOME%\lib\kotlinx-coroutines-reactive-1.5.2.jar;%APP_HOME%\lib\ktor-client-cio-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\ktor-client-content-negotiation-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\ktor-client-serialization-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\ktor-client-auth-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\ktor-client-json-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\ktor-client-core-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\ktor-websocket-serialization-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\ktor-serialization-kotlinx-json-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\ktor-serialization-kotlinx-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\ktor-serialization-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\ktor-websockets-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\kotlinx-coroutines-jdk8-1.5.2-native-mt.jar;%APP_HOME%\lib\ktor-events-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\ktor-http-cio-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\ktor-http-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\ktor-network-tls-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\ktor-network-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\ktor-utils-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\ktor-io-jvm-2.0.0-beta-1.jar;%APP_HOME%\lib\kotlinx-coroutines-core-jvm-1.5.2.jar;%APP_HOME%\lib\kotlin-stdlib-jdk8-1.6.10.jar;%APP_HOME%\lib\kotlin-css-jvm-1.0-SNAPSHOT.jar;%APP_HOME%\lib\logback-classic-1.2.3.jar;%APP_HOME%\lib\firebase-admin-8.1.0.jar;%APP_HOME%\lib\tika-core-2.2.1.jar;%APP_HOME%\lib\kotlin-stdlib-jdk7-1.6.10.jar;%APP_HOME%\lib\kmongo-jackson-mapping-4.4.0.jar;%APP_HOME%\lib\kmongo-id-jackson-4.4.0.jar;%APP_HOME%\lib\java-jwt-3.13.0.jar;%APP_HOME%\lib\jwks-rsa-0.17.0.jar;%APP_HOME%\lib\jackson-module-loader-0.4.0.jar;%APP_HOME%\lib\jackson-databind-2.13.0.jar;%APP_HOME%\lib\jackson-annotations-2.13.0.jar;%APP_HOME%\lib\google-cloud-storage-1.118.0.jar;%APP_HOME%\lib\jackson-core-2.13.0.jar;%APP_HOME%\lib\jackson-module-kotlin-2.13.0.jar;%APP_HOME%\lib\kmongo-async-shared-4.4.0.jar;%APP_HOME%\lib\kmongo-property-4.4.0.jar;%APP_HOME%\lib\kmongo-shared-4.4.0.jar;%APP_HOME%\lib\kmongo-id-4.4.0.jar;%APP_HOME%\lib\kreflect-1.0.0.jar;%APP_HOME%\lib\kotlin-reflect-1.6.0.jar;%APP_HOME%\lib\kotlinx-serialization-json-jvm-1.3.0.jar;%APP_HOME%\lib\kmongo-data-4.4.0.jar;%APP_HOME%\lib\kotlinx-serialization-core-jvm-1.3.0.jar;%APP_HOME%\lib\kotlin-stdlib-1.6.10.jar;%APP_HOME%\lib\slf4j-api-1.7.32.jar;%APP_HOME%\lib\config-1.3.1.jar;%APP_HOME%\lib\kotlin-stdlib-common-1.6.10.jar;%APP_HOME%\lib\jansi-2.3.4.jar;%APP_HOME%\lib\json-simple-1.1.1.jar;%APP_HOME%\lib\google-cloud-firestore-2.6.1.jar;%APP_HOME%\lib\google-api-client-gson-1.32.1.jar;%APP_HOME%\lib\google-api-client-1.32.1.jar;%APP_HOME%\lib\google-auth-library-oauth2-http-0.26.0.jar;%APP_HOME%\lib\google-http-client-gson-1.39.2.jar;%APP_HOME%\lib\gson-2.8.7.jar;%APP_HOME%\lib\netty-codec-http2-4.1.72.Final.jar;%APP_HOME%\lib\alpn-api-1.1.3.v20160715.jar;%APP_HOME%\lib\netty-transport-native-kqueue-4.1.72.Final.jar;%APP_HOME%\lib\netty-transport-native-epoll-4.1.72.Final.jar;%APP_HOME%\lib\logback-core-1.2.3.jar;%APP_HOME%\lib\google-oauth-client-1.31.5.jar;%APP_HOME%\lib\google-http-client-apache-v2-1.39.2.jar;%APP_HOME%\lib\google-http-client-1.39.2.jar;%APP_HOME%\lib\proto-google-cloud-firestore-bundle-v1-2.6.1.jar;%APP_HOME%\lib\api-common-1.10.4.jar;%APP_HOME%\lib\opencensus-contrib-http-util-0.28.0.jar;%APP_HOME%\lib\guava-30.1.1-android.jar;%APP_HOME%\lib\netty-codec-http-4.1.72.Final.jar;%APP_HOME%\lib\netty-handler-4.1.72.Final.jar;%APP_HOME%\lib\netty-codec-4.1.72.Final.jar;%APP_HOME%\lib\netty-transport-classes-kqueue-4.1.72.Final.jar;%APP_HOME%\lib\netty-transport-classes-epoll-4.1.72.Final.jar;%APP_HOME%\lib\netty-transport-native-unix-common-4.1.72.Final.jar;%APP_HOME%\lib\netty-transport-4.1.72.Final.jar;%APP_HOME%\lib\commons-io-2.11.0.jar;%APP_HOME%\lib\annotations-13.0.jar;%APP_HOME%\lib\httpclient-4.5.13.jar;%APP_HOME%\lib\commons-codec-1.15.jar;%APP_HOME%\lib\netty-buffer-4.1.72.Final.jar;%APP_HOME%\lib\netty-resolver-4.1.72.Final.jar;%APP_HOME%\lib\netty-common-4.1.72.Final.jar;%APP_HOME%\lib\httpcore-4.4.14.jar;%APP_HOME%\lib\proto-google-cloud-firestore-v1-2.6.1.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\j2objc-annotations-1.3.jar;%APP_HOME%\lib\opencensus-api-0.28.0.jar;%APP_HOME%\lib\javax.annotation-api-1.3.2.jar;%APP_HOME%\lib\auto-value-annotations-1.8.1.jar;%APP_HOME%\lib\google-auth-library-credentials-0.26.0.jar;%APP_HOME%\lib\failureaccess-1.0.1.jar;%APP_HOME%\lib\listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar;%APP_HOME%\lib\checker-compat-qual-2.5.5.jar;%APP_HOME%\lib\error_prone_annotations-2.7.1.jar;%APP_HOME%\lib\google-http-client-jackson2-1.39.2.jar;%APP_HOME%\lib\google-api-services-storage-v1-rev20210127-1.32.1.jar;%APP_HOME%\lib\google-cloud-core-1.95.4.jar;%APP_HOME%\lib\proto-google-common-protos-2.3.2.jar;%APP_HOME%\lib\google-cloud-core-http-1.95.4.jar;%APP_HOME%\lib\google-http-client-appengine-1.39.2.jar;%APP_HOME%\lib\gax-httpjson-0.83.0.jar;%APP_HOME%\lib\gax-1.66.0.jar;%APP_HOME%\lib\grpc-context-1.39.0.jar;%APP_HOME%\lib\proto-google-iam-v1-1.0.14.jar;%APP_HOME%\lib\protobuf-java-3.17.3.jar;%APP_HOME%\lib\protobuf-java-util-3.17.3.jar;%APP_HOME%\lib\threetenbp-1.5.1.jar;%APP_HOME%\lib\google-cloud-core-grpc-1.95.4.jar;%APP_HOME%\lib\grpc-core-1.39.0.jar;%APP_HOME%\lib\annotations-4.1.1.4.jar;%APP_HOME%\lib\animal-sniffer-annotations-1.20.jar;%APP_HOME%\lib\perfmark-api-0.23.0.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\opencensus-contrib-grpc-util-0.28.0.jar;%APP_HOME%\lib\grpc-protobuf-1.39.0.jar;%APP_HOME%\lib\grpc-protobuf-lite-1.39.0.jar;%APP_HOME%\lib\grpc-api-1.39.0.jar;%APP_HOME%\lib\gax-grpc-1.66.0.jar;%APP_HOME%\lib\grpc-auth-1.39.0.jar;%APP_HOME%\lib\grpc-netty-shaded-1.39.0.jar;%APP_HOME%\lib\grpc-alts-1.39.0.jar;%APP_HOME%\lib\grpc-grpclb-1.39.0.jar;%APP_HOME%\lib\conscrypt-openjdk-uber-2.5.1.jar;%APP_HOME%\lib\grpc-stub-1.39.0.jar;%APP_HOME%\lib\bson4jackson-2.13.0.jar;%APP_HOME%\lib\netty-tcnative-classes-2.0.46.Final.jar;%APP_HOME%\lib\mongodb-driver-reactivestreams-4.4.0.jar;%APP_HOME%\lib\reactor-core-3.2.22.RELEASE.jar;%APP_HOME%\lib\reactive-streams-1.0.3.jar;%APP_HOME%\lib\mongodb-driver-core-4.4.0.jar;%APP_HOME%\lib\bson-4.4.0.jar


@rem Execute aurel
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %AUREL_OPTS%  -classpath "%CLASSPATH%" com.aej.ApplicationKt %*

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable AUREL_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%AUREL_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
