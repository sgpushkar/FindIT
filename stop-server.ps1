$ErrorActionPreference = "SilentlyContinue"

$tomcatHome = "C:\Users\Pushkar Mhatre\scoop\apps\tomcat10\current"
$catalinaBat = Join-Path $tomcatHome "bin\catalina.bat"

Write-Host "Stopping Apache Tomcat..."
& $catalinaBat stop
