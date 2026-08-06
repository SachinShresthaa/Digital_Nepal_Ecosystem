@echo off
REM Minimal Windows wrapper - requires Git Bash / bash available on PATH
where bash >nul 2>&1 || (
  echo Please install Git for Windows (Git Bash) or provide bash on PATH.
  exit /b 1
)

bash mvnw %*
