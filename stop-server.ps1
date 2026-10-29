$ErrorActionPreference = "SilentlyContinue"

Write-Host "Stopping FindIT server..." -ForegroundColor Cyan

# Find any process listening on port 8080
$connections = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
if ($connections) {
    foreach ($conn in $connections) {
        $procId = $conn.OwningProcess
        if ($procId -gt 0) {
            $proc = Get-Process -Id $procId -ErrorAction SilentlyContinue
            if ($proc) {
                Write-Host "Stopping process $($proc.ProcessName) (PID: $procId)..." -ForegroundColor Yellow
                Stop-Process -Id $procId -Force -ErrorAction SilentlyContinue
            }
        }
    }
}

# Also check for any java process running FindIT
$javaProcs = Get-CimInstance Win32_Process -Filter "Name LIKE 'java%' AND CommandLine LIKE '%FindIT%'" -ErrorAction SilentlyContinue
if ($javaProcs) {
    foreach ($p in $javaProcs) {
        Write-Host "Terminating FindIT Java process (PID: $($p.ProcessId))..." -ForegroundColor Yellow
        Stop-Process -Id $p.ProcessId -Force -ErrorAction SilentlyContinue
    }
}

Write-Host "Server stopped." -ForegroundColor Green
