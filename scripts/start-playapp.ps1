$ErrorActionPreference = "Stop"
if ($PSVersionTable.PSVersion.Major -ge 7) {
    $PSNativeCommandUseErrorActionPreference = $false
}

$root = Split-Path -Parent $PSScriptRoot
$secretsFile = Join-Path $root "config\application-secrets.env"
$composeFile = Join-Path $root "docker-compose.yml"

if (!(Test-Path $secretsFile)) {
    Write-Host "No existe config\application-secrets.env" -ForegroundColor Red
    Write-Host "Copia config\application-secrets.env.example como config\application-secrets.env y ajusta los valores."
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

function Get-JavaMajorVersion {
    $javaExe = $null
    if ($env:JAVA_HOME) {
        $candidate = Join-Path $env:JAVA_HOME "bin\java.exe"
        if (Test-Path $candidate) {
            $javaExe = $candidate
        }
    }
    if (!$javaExe) {
        $javaCmd = Get-Command java -ErrorAction SilentlyContinue
        if (!$javaCmd) {
            return 0
        }
        $javaExe = $javaCmd.Source
    }

    $versionLine = (cmd /c "`"$javaExe`" -version 2>&1" | Select-Object -First 1)
    if ($versionLine -match '"(\d+)(\.\d+)?') {
        return [int]$matches[1]
    }

    return 0
}

function Open-Url($url) {
    Start-Process powershell -ArgumentList "-NoProfile", "-WindowStyle", "Hidden", "-Command", "Start-Sleep -Seconds 7; Start-Process '$url'"
}

function Test-TcpEndpoint($hostname, $port) {
    try {
        $client = New-Object System.Net.Sockets.TcpClient
        $async = $client.BeginConnect($hostname, [int]$port, $null, $null)
        $ok = $async.AsyncWaitHandle.WaitOne(1200, $false)
        $client.Close()
        return $ok
    } catch {
        return $false
    }
}

function Enable-ProjectJava21 {
    $projectJavaHome = $null
    $projectJavaBin = $null
    $setupScript = Join-Path $root "scripts\setup-java21.ps1"

    $candidateHomes = @(Get-ChildItem -Path (Join-Path $root ".tools") -Directory -ErrorAction SilentlyContinue |
            Where-Object { $_.Name -like "jdk-21*" -and (Test-Path (Join-Path $_.FullName "bin\java.exe")) } |
            Select-Object -ExpandProperty FullName)

    if ($candidateHomes) {
        $projectJavaHome = $candidateHomes[0]
        $projectJavaBin = Join-Path $projectJavaHome "bin\java.exe"
    }

    if ((!$projectJavaBin -or !(Test-Path $projectJavaBin)) -and (Test-Path $setupScript)) {
        Write-Host "Preparando JDK 21 local para este proyecto..." -ForegroundColor Cyan
        & $setupScript

        $candidateHomes = @(Get-ChildItem -Path (Join-Path $root ".tools") -Directory -ErrorAction SilentlyContinue |
                Where-Object { $_.Name -like "jdk-21*" -and (Test-Path (Join-Path $_.FullName "bin\java.exe")) } |
                Select-Object -ExpandProperty FullName)
        if ($candidateHomes) {
            $projectJavaHome = $candidateHomes[0]
            $projectJavaBin = Join-Path $projectJavaHome "bin\java.exe"
        }
    }

    if ($projectJavaBin -and (Test-Path $projectJavaBin)) {
        $env:JAVA_HOME = $projectJavaHome
        $env:PATH = "$($projectJavaHome)\bin;$($env:PATH)"
        return $true
    }

    return $false
}

function Test-DockerEngine {
    cmd /c "docker info >nul 2>nul" | Out-Null
    return $LASTEXITCODE -eq 0
}

function Ensure-DockerEngine {
    if (Test-DockerEngine) {
        return $true
    }

    $dockerDesktopPath = $null
    $path1 = Join-Path $env:ProgramFiles "Docker\Docker\Docker Desktop.exe"
    $path2 = Join-Path $env:LocalAppData "Docker\Docker Desktop.exe"
    if (Test-Path $path1) {
        $dockerDesktopPath = $path1
    } elseif (Test-Path $path2) {
        $dockerDesktopPath = $path2
    }

    if ($dockerDesktopPath) {
        Write-Host "Docker Engine no esta activo. Intentando iniciar Docker Desktop..." -ForegroundColor Yellow
        Start-Process -FilePath "$dockerDesktopPath"

        for ($i = 0; $i -lt 30; $i++) {
            Start-Sleep -Seconds 4
            if (Test-DockerEngine) {
                return $true
            }
        }
    }

    return $false
}

$playappPort = if ($env:PLAYAPP_APP_PORT) { $env:PLAYAPP_APP_PORT } else { "8080" }
$useDockerMode = $false
$javaMajor = Get-JavaMajorVersion

if ($javaMajor -lt 21) {
    Write-Host "Java 21 no esta disponible (version detectada: $javaMajor)." -ForegroundColor Yellow
    if (Enable-ProjectJava21) {
        $javaMajor = Get-JavaMajorVersion
    }

    if ($javaMajor -lt 21) {
        Write-Host "No fue posible activar Java 21 local. Se usara modo Docker para evitar errores." -ForegroundColor Yellow
        $useDockerMode = $true
    } else {
        Write-Host "Java 21 local activado para este proyecto." -ForegroundColor Green
    }
}

if ($env:PLAYAPP_RUN_MODE -eq "docker") {
    $useDockerMode = $true
}

if ($useDockerMode) {
    if (!(Test-Path $composeFile)) {
        Write-Host "No existe docker-compose.yml en la raiz del proyecto." -ForegroundColor Red
        exit 1
    }

    if (!(Get-Command docker -ErrorAction SilentlyContinue)) {
        Write-Host "Docker no esta disponible y Java 21 no existe en el sistema. No se puede iniciar PlayApp." -ForegroundColor Red
        exit 1
    }
    if (!(Ensure-DockerEngine)) {
        Write-Host "Docker no esta activo y Java 21 no existe en el sistema. No se puede iniciar PlayApp." -ForegroundColor Red
        exit 1
    }

    Set-Location $root
    Write-Host "Levantando PlayApp en Docker (mongo + redis + app)..." -ForegroundColor Cyan
    docker-compose -f $composeFile --profile app up -d --build
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Fallo al levantar servicios Docker." -ForegroundColor Red
        exit $LASTEXITCODE
    }

    $url = "http://localhost:$playappPort"
    Write-Host "PlayApp disponible en $url" -ForegroundColor Green
    Open-Url $url
    exit 0
}

$skipDocker = $env:PLAYAPP_SKIP_DOCKER -eq "true"
if (!$skipDocker -and (Test-Path $composeFile)) {
    if (Get-Command docker -ErrorAction SilentlyContinue) {
        if (!(Ensure-DockerEngine)) {
            Write-Host "Docker no esta activo. Se continua sin iniciar MongoDB/Redis automaticamente." -ForegroundColor Yellow
        } else {
        Write-Host "Levantando MongoDB y Redis de forma local para este proyecto..." -ForegroundColor Cyan
            docker-compose -f $composeFile up -d mongo redis | Out-Null
            if ($LASTEXITCODE -ne 0) {
                Write-Host "No fue posible iniciar MongoDB/Redis con Docker." -ForegroundColor Yellow
            }
        }
    } else {
        Write-Host "Docker no esta disponible. Se asume MongoDB/Redis ya estan corriendo." -ForegroundColor Yellow
    }
}

$redisHost = if ($env:REDIS_HOST) { $env:REDIS_HOST } else { "localhost" }
$redisPort = if ($env:REDIS_PORT) { [int]$env:REDIS_PORT } else { 6379 }
$mongoHost = "localhost"
$mongoPort = 27017
if ($env:MONGODB_URI -and $env:MONGODB_URI -match "mongodb:\/\/([^:\/]+):(\d+)") {
    $mongoHost = $matches[1]
    $mongoPort = [int]$matches[2]
}

if (($mongoHost -eq "localhost" -or $mongoHost -eq "127.0.0.1") -and !(Test-TcpEndpoint $mongoHost $mongoPort)) {
    Write-Host "MongoDB no esta disponible en ${mongoHost}:${mongoPort}." -ForegroundColor Red
    Write-Host "Inicia Docker Desktop y ejecuta scripts/dev-up.ps1, o cambia MONGODB_URI a una instancia activa." -ForegroundColor Yellow
    exit 1
}

if (($redisHost -eq "localhost" -or $redisHost -eq "127.0.0.1") -and !(Test-TcpEndpoint $redisHost $redisPort)) {
    Write-Host "Redis no esta disponible en ${redisHost}:${redisPort}." -ForegroundColor Red
    Write-Host "Inicia Docker Desktop y ejecuta scripts/dev-up.ps1, o cambia REDIS_HOST/REDIS_PORT a una instancia activa." -ForegroundColor Yellow
    exit 1
}

$port = 8080
while (Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue) {
    Write-Host "El puerto $port ya esta ocupado. Probando el puerto $($port + 1)..." -ForegroundColor Yellow
    $port++
}

$url = "http://localhost:$port"
Write-Host "PlayApp se abrira en $url" -ForegroundColor Green
Open-Url $url

Set-Location $root
$env:MAVEN_OPTS = "-Xms128m -Xmx512m"
& ".\mvnw.cmd" -DskipTests spring-boot:run "-Dspring-boot.run.jvmArguments=-Xms128m -Xmx384m" "-Dspring-boot.run.arguments=--server.port=$port"
if ($LASTEXITCODE -ne 0) {
    Write-Host "Fallo al iniciar Spring Boot en modo local." -ForegroundColor Red
    exit $LASTEXITCODE
}
