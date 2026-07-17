#!/bin/bash
# Zalozi testovaci kuratory a priradi jim prava k registratorum tst01/tst02.
# Vyzaduje admin ucet predany pres CZIDLO_ADMIN_LOGIN / CZIDLO_ADMIN_PASSWORD.
set -euo pipefail

API="${CZIDLO_API:-http://localhost:8080/web-api/api}"
ADMIN_LOGIN="${CZIDLO_ADMIN_LOGIN:-claude-admin}"
ADMIN_PASSWORD="${CZIDLO_ADMIN_PASSWORD:?nastav CZIDLO_ADMIN_PASSWORD}"
TEST_PASSWORD="${CZIDLO_TEST_PASSWORD:-Test1234!}"
AUTH="${ADMIN_LOGIN}:${ADMIN_PASSWORD}"

create_user() {
  local login="$1"
  local code
  code=$(curl -s -o /dev/null -w "%{http_code}" --max-time 15 -u "$AUTH" -X POST "$API/users" \
    -H "Content-Type: application/json" \
    -d "{\"login\":\"$login\",\"email\":\"$login@example.test\",\"password\":\"$TEST_PASSWORD\",\"isAdmin\":false}")
  echo "  user $login -> HTTP $code"
}

user_id() {
  curl -s --max-time 15 -u "$AUTH" "$API/users" \
    | python3 -c "import sys,json;print(next((u['id'] for u in json.load(sys.stdin).get('items',[]) if u['login']=='$1'),''))"
}

assign() {
  local login="$1" reg="$2" uid code
  uid=$(user_id "$login")
  [ -z "$uid" ] && { echo "  !! $login nenalezen"; return; }
  code=$(curl -s -o /dev/null -w "%{http_code}" --max-time 15 -u "$AUTH" -X POST "$API/users/$uid/registrar_rights/$reg")
  echo "  právo $login + $reg -> HTTP $code"
}

echo "=== zakládám uživatele ==="
for u in claude-cur-x claude-cur-y claude-cur-xy claude-nobody; do create_user "$u"; done

echo "=== přiřazuji práva ==="
assign claude-cur-x  tst01
assign claude-cur-y  tst02
assign claude-cur-xy tst01
assign claude-cur-xy tst02

echo "=== hotovo, kontrola /user ==="
for u in claude-cur-x claude-cur-y claude-cur-xy claude-nobody; do
  curl -s --max-time 15 -u "$u:$TEST_PASSWORD" "$API/user" \
    | python3 -c "import sys,json;d=json.load(sys.stdin);print('  %-16s admin=%-5s práva=%s'%('$u',d.get('admin'),d.get('registrarRights')))"
done
