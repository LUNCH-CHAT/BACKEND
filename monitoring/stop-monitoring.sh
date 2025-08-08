#!/bin/bash

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}🛑 LunchChat 모니터링 스택을 중지합니다...${NC}"
echo "==============================================="

cd "$(dirname "$0")/.."

echo -e "${YELLOW}🔧 모니터링 컨테이너들을 중지하는 중...${NC}"

docker-compose down

echo ""
echo -e "${GREEN}✅ LunchChat 모니터링 스택이 성공적으로 중지되었습니다!${NC}"
echo ""
echo -e "${BLUE}📝 참고사항:${NC}"
echo "- 데이터는 Docker 볼륨에 보존됩니다"
echo "- 완전히 삭제하려면: docker-compose down -v"
echo "- 다시 시작하려면: ./scripts/start-monitoring.sh"
