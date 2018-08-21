@echo off
if "%~1%"=="" goto noMessage
cls
git status
git add .
git commit -m "%~1"
git push
goto exit
:noMessage
echo "You should add message as a parameter"
:exit