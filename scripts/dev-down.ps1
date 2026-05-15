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

if (!(Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Host "Docker CLI no esta disponible." -ForegroundColor Red
    exit 1
}

Set-Location $root
if (Test-Path $secretsFile) {
    docker-compose -f $composeFile --profile app down
} else {
    docker-compose -f $composeFile --profile app down
}
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error al detener servicios Docker del proyecto." -ForegroundColor Red
    exit $LASTEXITCODE
}
Write-Host "Servicios detenidos para este proyecto." -ForegroundColor Green
