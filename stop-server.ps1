$ErrorActionPreference = "SilentlyContinue"

$tomcatHome = Join-Path $env:LOCALAPPDATA "FindIT\tools\apache-tomcat-10.1.61"
$env:CATALINA_HOME = $tomcatHome
$env:CATALINA_BASE = $tomcatHome
$catalinaBat = Join-Path $tomcatHome "bin\catalina.bat"

Write-Host "Stopping Apache Tomcat..."
& $catalinaBat stop
