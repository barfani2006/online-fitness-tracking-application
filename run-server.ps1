# PulseFit Server Runner for PowerShell
$ErrorActionPreference = "Stop"
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $ScriptDir

$env:JAVA_HOME = "C:\Program Files\Java\jdk-26.0.1"
$env:CATALINA_HOME = "C:\Users\hp\.gemini\antigravity\tools\apache-tomcat-10.1.25"

Write-Host "=========================================================================" -ForegroundColor Cyan
Write-Host "  Starting PulseFit Online Fitness Tracking Application..." -ForegroundColor Green
Write-Host "  Server Port: 8080" -ForegroundColor Yellow
Write-Host "  URL:         http://localhost:8080/" -ForegroundColor Green
Write-Host "  Stop Server: Press Ctrl+C in this terminal" -ForegroundColor Yellow
Write-Host "=========================================================================" -ForegroundColor Cyan

& "$env:CATALINA_HOME\bin\catalina.bat" run
