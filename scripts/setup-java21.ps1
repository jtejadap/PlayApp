$ErrorActionPreference = "Stop"
if ($PSVersionTable.PSVersion.Major -ge 7) {
    $PSNativeCommandUseErrorActionPreference = $false
}

$root = Split-Path -Parent $PSScriptRoot
$toolsDir = Join-Path $root ".tools"
$jdkDir = Join-Path $toolsDir "jdk-21"
$tmpZip = Join-Path $toolsDir "jdk21.zip"
$downloadUrl = "https://api.adoptium.net/v3/binary/latest/21/ga/windows/x64/jdk/hotspot/normal/eclipse"

if (Test-Path (Join-Path $jdkDir "bin\java.exe")) {
    Write-Host "JDK 21 local ya existe en $jdkDir" -ForegroundColor Green
    exit 0
}

New-Item -ItemType Directory -Path $toolsDir -Force | Out-Null

Write-Host "Descargando JDK 21 local (solo para este proyecto)..." -ForegroundColor Cyan
Invoke-WebRequest -Uri $downloadUrl -OutFile $tmpZip

if (Test-Path $jdkDir) {
    Remove-Item -Recurse -Force $jdkDir
}
New-Item -ItemType Directory -Path $jdkDir -Force | Out-Null

Write-Host "Extrayendo JDK 21..." -ForegroundColor Cyan
Expand-Archive -Path $tmpZip -DestinationPath $toolsDir -Force

$extracted = Get-ChildItem -Path $toolsDir -Directory | Where-Object {
    $_.Name -like "jdk-21*" -and (Test-Path (Join-Path $_.FullName "bin\java.exe"))
} | Select-Object -First 1
if (!$extracted) {
    Write-Host "No fue posible localizar el directorio extraido de JDK 21." -ForegroundColor Red
    exit 1
}

if ($extracted.FullName -ne $jdkDir) {
    if (Test-Path $jdkDir) {
        Remove-Item -Recurse -Force $jdkDir
    }
    Move-Item -Path $extracted.FullName -Destination $jdkDir
}

Remove-Item $tmpZip -Force -ErrorAction SilentlyContinue
Write-Host "JDK 21 local instalado en $jdkDir" -ForegroundColor Green
