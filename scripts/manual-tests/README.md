# Manuální testy oprávnění nad urn:nbn

Ověřují autorizaci operací deaktivace / reaktivace / přidání a odebrání
předchůdce podle modelu popsaného ve frontend repu
(`czidlo-frontend/OPRAVNENI-URNNBN.md`).

Nejsou to unit testy pro Maven — jsou to **integrační nástroje proti běžícímu
serveru** a testovacím datům. Vyžadují nasazený web-api a testovací
registrátory `tst01` / `tst02` s několika dokumenty.

## Předpoklady

- Běžící web-api (default `http://localhost:8080/web-api/api`).
- Globální admin účet (jeho přihlašovací údaje předáváš přes proměnné
  prostředí, do repa se nic tajného nezapisuje).
- Pro UI testy: běžící frontend (`http://localhost:4200`),
  Node s `playwright-core` a stažený chromium v ms-playwright cache.

## Proměnné prostředí

| Proměnná | Význam | Default |
|---|---|---|
| `CZIDLO_API` | základ web-api | `http://localhost:8080/web-api/api` |
| `CZIDLO_APP` | frontend URL (UI testy) | `http://localhost:4200/` |
| `CZIDLO_ADMIN_LOGIN` | login admina | `claude-admin` |
| `CZIDLO_ADMIN_PASSWORD` | heslo admina | *(povinné)* |
| `CZIDLO_TEST_PASSWORD` | heslo testovacích kurátorů | `Test1234!` |

## Postup

```bash
export CZIDLO_ADMIN_PASSWORD='...'          # heslo tvého admin účtu

# 1) založí testovací uživatele a přiřadí práva k registrátorům
./setup_test_users.sh

# 2) API test celé matice oprávnění (30 případů)
python3 test_matrix.py

# 3) UI testy viditelnosti tlačítek (potřebují playwright-core)
npm i playwright-core        # kdekoliv, kde na to Node dosáhne
node test_ui.js              # tlačítka podle role
node test_mirror.js          # read-only blok vs admin panel se vylučují
```

## Testovací uživatelé (zakládá setup_test_users.sh)

| Login | admin | práva |
|---|---|---|
| `claude-cur-x` | ne | tst01 |
| `claude-cur-y` | ne | tst02 |
| `claude-cur-xy` | ne | tst01, tst02 |
| `claude-nobody` | ne | — |

Admin účet si založ zvlášť (nebo použij existující) a předej přes proměnné.

## Pozor

`test_matrix.py` mění data (deaktivuje dokumenty, přidává/odebírá
předchůdce) — pouštěj **jen proti testovací instanci**. Skript se snaží
stavy vracet zpět, ale spoléhat se na to nelze.
