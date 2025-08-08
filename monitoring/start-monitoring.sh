#!/bin/bash

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${GREEN}🚀 LunchChat 모니터링 스택을 시작합니다...${NC}"
echo "==============================================="

cd "$(dirname "$0")/.."

if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker가 설치되어 있지 않습니다. Docker를 먼저 설치해주세요.${NC}"
    exit 1
fi

if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
    echo -e "${RED}❌ Docker Compose가 설치되어 있지 않습니다. Docker Compose를 먼저 설치해주세요.${NC}"
    exit 1
fi

if [ ! -f .env ]; then
    echo -e "${YELLOW}⚠️  환경 변수 파일을 생성합니다...${NC}"
    if [ -f .env.example ]; then
        cp .env.example .env
        echo -e "${RED}❗ .env 파일을 수정한 후 다시 실행해주세요!${NC}"
        echo -e "${BLUE}💡 다음 항목들을 확인해주세요:${NC}"
        echo "   - GRAFANA_ADMIN_PASSWORD: 강한 비밀번호로 변경"
        echo "   - 서버 IP 주소들이 정확한지 확인"
        exit 1
    else
        echo -e "${RED}❌ .env.example 파일이 없습니다. 파일을 확인해주세요.${NC}"
        exit 1
    fi
fi

source .env

echo -e "${BLUE}🔍 LunchChat 앱 서버 연결 테스트...${NC}"
if curl -s -m 5 "http://${APP_SERVER_2_IP}:8080/actuator/health" > /dev/null; then
    echo -e "${GREEN}✅ LCC #2 (${APP_SERVER_2_IP}) 연결 OK${NC}"
else
    echo -e "${YELLOW}⚠️  LCC #2 (${APP_SERVER_2_IP}) 연결 실패 - 모니터링은 계속 진행됩니다${NC}"
fi

if curl -s -m 5 "http://${APP_SERVER_3_IP}:8080/actuator/health" > /dev/null; then
    echo -e "${GREEN}✅ LCC #3 (${APP_SERVER_3_IP}) 연결 OK${NC}"
else
    echo -e "${YELLOW}⚠️  LCC #3 (${APP_SERVER_3_IP}) 연결 실패 - 모니터링은 계속 진행됩니다${NC}"
fi

if docker ps | grep -q redis; then
    echo -e "${GREEN}✅ Redis 컨테이너 실행 중${NC}"
else
    echo -e "${YELLOW}⚠️  Redis 컨테이너를 찾을 수 없습니다${NC}"
fi

echo ""
echo -e "${BLUE}🔧 모니터링 컨테이너들을 시작하는 중...${NC}"

if ! docker-compose --env-file .env up -d; then
    echo -e "${RED}❌ 모니터링 스택 시작에 실패했습니다.${NC}"
    exit 1
fi

echo -e "${BLUE}⏳ 컨테이너 초기화 대기 중...${NC}"
sleep 10

echo ""
echo -e "${BLUE}📊 컨테이너 상태 확인...${NC}"
docker-compose ps

echo ""
echo -e "${BLUE}🏥 서비스 헬스체크...${NC}"
sleep 5

if curl -s -m 5 "http://localhost:${PROMETHEUS_PORT:-9090}/-/healthy" > /dev/null; then
    echo -e "${GREEN}✅ Prometheus 정상 작동${NC}"
else
    echo -e "${YELLOW}⚠️  Prometheus 헬스체크 실패${NC}"
fi

if curl -s -m 5 "http://localhost:${GRAFANA_PORT:-3000}/api/health" > /dev/null; then
    echo -e "${GREEN}✅ Grafana 정상 작동${NC}"
else
    echo -e "${YELLOW}⚠️  Grafana 헬스체크 실패${NC}"
fi

echo ""
echo -e "${GREEN}🎉 LunchChat 모니터링 스택이 성공적으로 시작되었습니다!${NC}"
echo ""
echo -e "${YELLOW}🌐 접속 정보:${NC}"
echo -e "- Grafana:    http://localhost:${GRAFANA_PORT:-3000}"
echo -e "- Prometheus: http://localhost:${PROMETHEUS_PORT:-9090}"
echo -e "- Node Exporter: http://localhost:${NODE_EXPORTER_PORT:-9100}"
echo ""
echo -e "${YELLOW}🔐 기본 로그인 정보:${NC}"
echo -e "- Grafana: ${GRAFANA_ADMIN_USER:-admin} / [.env 파일에서 설정한 비밀번호]"
echo ""
echo -e "${BLUE}📝 참고사항:${NC}"
echo "- 첫 Grafana 로그인 후 비밀번호를 변경하세요"
echo "- 대시보드는 자동으로 프로비저닝됩니다"
echo "- Prometheus에서 모든 타겟이 UP 상태인지 확인하세요"
echo ""
echo -e "${YELLOW}🛑 중지하려면: ./scripts/stop-monitoring.sh${NC}"
