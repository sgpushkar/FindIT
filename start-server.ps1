$ErrorActionPreference = "Stop"

$tomcatHome = "C:\Users\Pushkar Mhatre\scoop\apps\tomcat10\current"
$tomcatPersist = "C:\Users\Pushkar Mhatre\scoop\persist\tomcat10"
$mvnCmd = "C:\Users\Pushkar Mhatre\scoop\apps\maven\current\bin\mvn.cmd"
$javaHome = "C:\Program Files\Java\jdk-24"

$env:JAVA_HOME = $javaHome
$env:CATALINA_HOME = $tomcatHome
$env:CATALINA_BASE = $tomcatHome

Write-Host "Building FindIT..."
& $mvnCmd package -DskipTests

$sourceWar = Join-Path $PSScriptRoot "target\FindIT.war"
$destWar = Join-Path $tomcatPersist "webapps\FindIT.war"
$destApp = Join-Path $tomcatPersist "webapps\FindIT"
$sourceWebapp = Join-Path $PSScriptRoot "src\main\webapp\*"

Write-Host "Deploying to Tomcat webapps..."
Copy-Item -Path $sourceWar -Destination $destWar -Force
Copy-Item -Path $sourceWebapp -Destination $destApp -Recurse -Force

Write-Host "Checking if Tomcat is already running on port 8080..."
$portCheck = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue

if ($portCheck) {
    Write-Host "Tomcat is already running on port 8080!" -ForegroundColor Green
    Write-Host "Open the app at: http://localhost:8080/FindIT/" -ForegroundColor Cyan
} else {
    Write-Host "Starting Apache Tomcat 10..."
    Write-Host "App will be available at: http://localhost:8080/FindIT/" -ForegroundColor Cyan
    $catalinaBat = Join-Path $tomcatHome "bin\catalina.bat"
    & $catalinaBat run
}
