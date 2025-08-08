@echo off
chcp 65001 > nul

echo 🚀 Lunch Chat 모니터링 스택을 시작합니다...
echo ===============================================

REM 현재 디렉토리를 monitoring 폴더로 변경
cd /d "%~dp0"

REM Docker가 실행 중인지 확인
docker version > nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker가 실행되지 않거나 설치되어 있지 않습니다.
    echo Docker Desktop을 시작하거나 설치해주세요.
    pause
    exit /b 1
)

REM 모니터링 스택 시작
echo 🔧 모니터링 컨테이너들을 시작하는 중...
docker-compose --env-file .env up -d

REM 컨테이너 상태 확인
echo.
echo 📊 컨테이너 상태 확인...
docker-compose ps

echo.
echo ✅ 모니터링 스택이 성공적으로 시작되었습니다!
echo.
echo 🌐 접속 정보:
echo - Grafana: http://localhost:3000 (admin/admin123)
echo - Prometheus: http://localhost:9090
echo - Node Exporter: http://localhost:9100
echo.
echo 📝 참고사항:
echo - Grafana 첫 로그인 후 비밀번호를 변경하세요
echo - Spring Boot 애플리케이션이 실행 중이어야 메트릭이 수집됩니다
echo - 시스템 모니터링 대시보드와 Spring Boot 대시보드가 자동으로 로드됩니다
echo.
echo 🛑 중지하려면: stop-monitoring.bat
echo.
pause