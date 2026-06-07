@echo off
cd /d "%~dp0"
javac *.java 2>nul
start javaw MainGUI
exit
