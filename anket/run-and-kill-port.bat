@echo off
REM run-and-kill-port.bat
REM Usage: double-click or run from project root (cmd.exe). This will kill any process listening on port 8080 then start the app.

echo Checking for processes listening on port 8080...
powershell -NoProfile -Command "
  $pids = (netstat -ano | Select-String ':8080' | ForEach-Object { ($_ -split '\\s+')[-1] }) | Sort-Object -Unique;
  if ($pids -and $pids.Count -gt 0) {
    foreach ($pid in $pids) {
      Write-Host 'Found process listening on port 8080, PID=' $pid;
      try { Stop-Process -Id $pid -Force -ErrorAction Stop; Write-Host 'Killed PID' $pid } catch { Write-Host 'Failed to kill PID' $pid ':' $_.Exception.Message }
    }
  } else { Write-Host 'No process found listening on port 8080.' }
"

echo Starting Spring Boot application in a new window (this will keep the process running)...
start "anket" cmd /k ".\mvnw.cmd -DskipTests spring-boot:run"

echo Done. A new window should be running the application. If you prefer foreground start, run: .\mvnw.cmd -DskipTests spring-boot:run
pause

