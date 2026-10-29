$ErrorActionPreference = "Stop"

Write-Host "=============================================" -ForegroundColor Cyan
Write-Host " FindIT - Spring Boot & React Server Startup " -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan

# 1. Check Java
$javaCmd = Get-Command java -ErrorAction SilentlyContinue
if (-not $javaCmd) {
    if ($env:JAVA_HOME -and (Test-Path (Join-Path $env:JAVA_HOME "bin\java.exe"))) {
        $javaExe = Join-Path $env:JAVA_HOME "bin\java.exe"
    } else {
        throw "Java is not installed or not in PATH. Please install JDK 17+."
    }
} else {
    $javaExe = "java"
}

# 2. Check if port 8080 is already active
$portCheck = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
if ($portCheck) {
    Write-Host "A server is already running on port 8080." -ForegroundColor Yellow
    Write-Host "Open the app in your browser: http://localhost:8080/" -ForegroundColor Green
    exit 0
}

# 3. Build React Frontend
Write-Host "Building React frontend..." -ForegroundColor Gray
$frontendDir = Join-Path $PSScriptRoot "frontend"
if (Test-Path $frontendDir) {
    Push-Location $frontendDir
    try {
        if (-not (Test-Path "node_modules")) {
            Write-Host "Installing frontend dependencies..." -ForegroundColor Gray
            npm install
        }
        npm run build
        if ($LASTEXITCODE -ne 0) {
            throw "Frontend build failed with exit code $LASTEXITCODE"
        }
    } finally {
        Pop-Location
    }
}

# 4. Build Spring Boot Jar
Write-Host "Building Spring Boot application..." -ForegroundColor Gray
$mvnCmd = Get-Command mvn -ErrorAction SilentlyContinue
if ($mvnCmd) {
    & mvn clean package -DskipTests
} else {
    $toolsMvn = Join-Path $env:LOCALAPPDATA "FindIT\tools\apache-maven-3.10.0\bin\mvn.cmd"
    if (Test-Path $toolsMvn) {
        & $toolsMvn clean package -DskipTests
    } else {
        throw "Maven is not found in PATH or standard tools location."
    }
}

if ($LASTEXITCODE -ne 0) {
    throw "Maven packaging failed with exit code $LASTEXITCODE"
}

$jarPath = Join-Path $PSScriptRoot "target\FindIT-2.0.0.jar"
if (-not (Test-Path $jarPath)) {
    throw "Target JAR file not found at: $jarPath"
}

# 5. Start Spring Boot Server
Write-Host "Starting FindIT Spring Boot server..." -ForegroundColor Green
Write-Host "Application will be available at: http://localhost:8080/" -ForegroundColor Cyan
Write-Host "Press Ctrl+C to stop the server." -ForegroundColor Yellow

& $javaExe -jar $jarPath
