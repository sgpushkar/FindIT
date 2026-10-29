$ErrorActionPreference = "Stop"

$tools = Join-Path $env:LOCALAPPDATA "FindIT\tools"
$tomcatHome = Join-Path $tools "apache-tomcat-10.1.61"
$mvnCmd = Join-Path $tools "apache-maven-3.10.0\bin\mvn.cmd"
$javaHome = $env:JAVA_HOME

if (-not $javaHome -or -not (Test-Path (Join-Path $javaHome "bin\java.exe"))) {
    $javaHome = Get-ChildItem "C:\Program Files\Eclipse Adoptium" -Directory -ErrorAction SilentlyContinue |
        Where-Object { $_.Name -match '^jdk-(17|21|24)' } |
        Sort-Object Name -Descending |
        Select-Object -First 1 -ExpandProperty FullName
}

foreach ($requiredPath in @($tomcatHome, $mvnCmd, (Join-Path $javaHome "bin\java.exe"))) {
    if (-not (Test-Path $requiredPath)) {
        throw "Required development tool not found: $requiredPath"
    }
}

$env:JAVA_HOME = $javaHome
$env:CATALINA_HOME = $tomcatHome
$env:CATALINA_BASE = $tomcatHome

Write-Host "Building FindIT..."
& $mvnCmd -f (Join-Path $PSScriptRoot "pom.xml") package -DskipTests
if ($LASTEXITCODE -ne 0) {
    throw "Maven build failed with exit code $LASTEXITCODE"
}

$sourceWar = Join-Path $PSScriptRoot "target\FindIT.war"
$webapps = Join-Path $tomcatHome "webapps"
$destWar = Join-Path $webapps "FindIT.war"

Write-Host "Deploying to Tomcat webapps..."
Copy-Item -Path $sourceWar -Destination $destWar -Force

Write-Host "Checking if Tomcat is already running on port 8080..."
$portCheck = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue

if ($portCheck) {
    Write-Host "A server is already running on port 8080." -ForegroundColor Yellow
    Write-Host "Open the app at: http://localhost:8080/FindIT/" -ForegroundColor Cyan
} else {
    Write-Host "Starting Apache Tomcat 10..."
    Write-Host "App will be available at: http://localhost:8080/FindIT/" -ForegroundColor Cyan
    $catalinaBat = Join-Path $tomcatHome "bin\catalina.bat"
    & $catalinaBat start
}
