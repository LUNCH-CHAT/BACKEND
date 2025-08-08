@echo off
chcp 65001 > nul

echo 🛑 Lunch Chat 모니터링 스택을 중지합니다...
echo ===============================================

REM 현재 디렉토리를 monitoring 폴더로 변경
cd /d "%~dp0"

REM 모니터링 스택 중지
echo 🔧 모니터링 컨테이너들을 중지하는 중...
docker-compose down

echo.
echo ✅ 모니터링 스택이 성공적으로 중지되었습니다!
echo.
echo 📝 참고사항:
echo - 데이터는 Docker 볼륨에 보존됩니다
echo - 완전히 삭제하려면: docker-compose down -v
echo - 다시 시작하려면: start-monitoring.bat
echo.
pause