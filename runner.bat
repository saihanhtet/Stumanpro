@echo off
cd "C:\Users\hanht\Documents\Stumanpro"

REM Run the Java application with specified parameters
"C:\workLibrary\jdks\jdk-19.0.2\bin\java.exe" --module-path "C:\workLibrary\jdks\javafx-sdk-19.0.2\lib" --add-modules javafx.controls,javafx.fxml,java.logging,java.naming,jdk.httpserver,jdk.crypto.ec -cp "build\classes\java\main\com\hanhtet\stumanpro" com.hanhtet.stumanpro.MainApplication

REM Pause to see output (optional)
pause
