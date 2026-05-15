$ErrorActionPreference = "Stop"
if ($PSVersionTable.PSVersion.Major -ge 7) {
    $PSNativeCommandUseErrorActionPreference = $false
}

$root = Split-Path -Parent $PSScriptRoot
$composeFile = Join-Path $root "docker-compose.yml"
$secretsFile = Join-Path $root "config\application-secrets.env"

if (!(Test-Path $composeFile)) {
    Write-Host "No existe docker-compose.yml en la raiz del proyecto." -ForegroundColor Red
    exit 1
}

if (Test-Path $secretsFile) {
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
}

if (!(Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Host "Docker CLI no esta disponible." -ForegroundColor Red
    exit 1
}

cmd /c "docker info >nul 2>nul" | Out-Null
if ($LASTEXITCODE -ne 0) {
    Write-Host "Docker Engine no esta activo. Inicia Docker Desktop e intenta de nuevo." -ForegroundColor Red
    exit 1
}

Set-Location $root
docker-compose -f $composeFile up -d mongo redis
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error al iniciar MongoDB y Redis." -ForegroundColor Red
    exit $LASTEXITCODE
}
Write-Host "Servicios locales listos: MongoDB y Redis." -ForegroundColor Green
