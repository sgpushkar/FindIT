$ErrorActionPreference = "Stop"

Write-Host "=============================================" -ForegroundColor Cyan
Write-Host " FindIT - Spring Boot & React Server Startup " -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan

# 1. Resolve JDK and JAVA_HOME
$javaHome = $env:JAVA_HOME

if (-not $javaHome -or -not (Test-Path (Join-Path $javaHome "bin\javac.exe"))) {
    $candidates = @(
        "C:\Program Files\Eclipse Adoptium",
        "C:\Program Files\Java",
        "C:\Program Files\Microsoft",
        "C:\Program Files\Amazon Corretto",
        "C:\Program Files\BellSoft",
        (Join-Path $env:LOCALAPPDATA "FindIT\tools")
    )
    $foundList = @()
    foreach ($base in $candidates) {
        if (Test-Path $base) {
            $dirs = Get-ChildItem $base -Directory -ErrorAction SilentlyContinue |
                Where-Object { Test-Path (Join-Path $_.FullName "bin\javac.exe") }
            if ($dirs) { $foundList += $dirs }
        }
    }

    if ($foundList) {
        $selected = $foundList |
            Where-Object { $_.Name -match '(17|21|24)' } |
            Sort-Object Name -Descending |
            Select-Object -First 1

        if (-not $selected) {
            $selected = $foundList | Sort-Object Name -Descending | Select-Object -First 1
        }
        $javaHome = $selected.FullName
    }
}

if (-not $javaHome -or -not (Test-Path (Join-Path $javaHome "bin\java.exe"))) {
    throw "JDK 17+ not found. Please install JDK 17+ or set the JAVA_HOME environment variable."
}

$env:JAVA_HOME = $javaHome
$env:PATH = "$javaHome\bin;$env:PATH"
$javaExe = Join-Path $javaHome "bin\java.exe"
Write-Host "Using Java from: $javaHome" -ForegroundColor Gray

# 2. Check and free port 8080 if an old server (like external Tomcat) is running
$connections = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
if ($connections) {
    Write-Host "Port 8080 is currently occupied (e.g. old Tomcat server)." -ForegroundColor Yellow
    Write-Host "Stopping existing process on port 8080 to launch FindIT..." -ForegroundColor Gray
    foreach ($conn in $connections) {
        $procId = $conn.OwningProcess
        if ($procId -gt 0) {
            Stop-Process -Id $procId -Force -ErrorAction SilentlyContinue
        }
    }
    Start-Sleep -Seconds 1
}

# 3. Build React Frontend (if not already built)
$frontendDir = Join-Path $PSScriptRoot "frontend"
$staticIndex = Join-Path $PSScriptRoot "src\main\resources\static\index.html"

if (Test-Path $frontendDir) {
    if (-not (Test-Path $staticIndex)) {
        Push-Location $frontendDir
        try {
            $npmCmd = Get-Command npm -ErrorAction SilentlyContinue
            if ($npmCmd) {
                if (-not (Test-Path "node_modules")) {
                    Write-Host "Installing frontend dependencies..." -ForegroundColor Gray
                    npm install
                }
                Write-Host "Building React frontend..." -ForegroundColor Gray
                npm run build
            }
        } finally {
            Pop-Location
        }
    }
}

# 4. Check or Build Spring Boot Jar
$jarPath = Join-Path $PSScriptRoot "target\FindIT-2.0.0.jar"
if (-not (Test-Path $jarPath)) {
    Write-Host "Building Spring Boot application JAR..." -ForegroundColor Gray
    
    $mvnCmd = Get-Command mvn -ErrorAction SilentlyContinue
    if ($mvnCmd) {
        & mvn clean package -DskipTests
    } else {
        $toolsMvn = Get-ChildItem (Join-Path $env:LOCALAPPDATA "FindIT\tools") -Recurse -Filter "mvn.cmd" -ErrorAction SilentlyContinue |
            Select-Object -First 1 -ExpandProperty FullName
        if ($toolsMvn -and (Test-Path $toolsMvn)) {
            & $toolsMvn clean package -DskipTests
        } else {
            throw "Maven is not found. Please install Maven or add it to PATH."
        }
    }

    if ($LASTEXITCODE -ne 0) {
        throw "Maven packaging failed with exit code $LASTEXITCODE"
    }
}

if (-not (Test-Path $jarPath)) {
    throw "Target JAR file not found at: $jarPath"
}

# 5. Start Spring Boot Server
Write-Host "Starting FindIT Spring Boot server..." -ForegroundColor Green
Write-Host "=============================================" -ForegroundColor Green
Write-Host " Application URL: http://localhost:8080/    " -ForegroundColor Cyan
Write-Host " Press Ctrl+C to stop the server.            " -ForegroundColor Yellow
Write-Host "=============================================" -ForegroundColor Green

& $javaExe -jar $jarPath
