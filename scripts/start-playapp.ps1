$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$secretsFile = Join-Path $root "config\application-secrets.env"

if (!(Test-Path $secretsFile)) {
    Write-Host "No existe config\application-secrets.env" -ForegroundColor Red
    Write-Host "Copia config\application-secrets.env.example como config\application-secrets.env y coloca la URI real de MongoDB Atlas."
    exit 1
}

Get-Content $secretsFile | ForEach-Object {
    $line = $_.Trim()
    if ($line -eq "" -or $line.StartsWith("#")) {
        return
    }

    $parts = $line.Split("=", 2)
    if ($parts.Length -eq 2) {
        [Environment]::SetEnvironmentVariable($parts[0].Trim(), $parts[1].Trim(), "Process")
    }
}

$port = 8080
while (Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue) {
    Write-Host "El puerto $port ya esta ocupado. Probando el puerto $($port + 1)..." -ForegroundColor Yellow
    $port++
}

Write-Host "PlayApp se abrira en http://localhost:$port" -ForegroundColor Green

Start-Process powershell -ArgumentList "-NoProfile", "-WindowStyle", "Hidden", "-Command", "Start-Sleep -Seconds 10; Start-Process 'http://localhost:$port'"

Set-Location $root
& ".\mvnw.cmd" spring-boot:run "-Dspring-boot.run.arguments=--server.port=$port"
