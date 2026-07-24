#!/usr/bin/env python3
"""Otestuje matici opravneni nad urn:nbn proti bezicimu backendu.

Model viz czidlo-frontend/OPRAVNENI-URNNBN.md. Vyzaduje testovaci uzivatele
zalozene pres setup_test_users.sh a admin ucet z promennych prostredi.

POZOR: meni data (deaktivace, predchudci) - jen proti testovaci instanci.
"""
import base64, json, os, urllib.request, urllib.error

API = os.environ.get("CZIDLO_API", "http://localhost:8080/web-api/api")
ADMIN_LOGIN = os.environ.get("CZIDLO_ADMIN_LOGIN", "claude-admin")
ADMIN_PASSWORD = os.environ.get("CZIDLO_ADMIN_PASSWORD")
TEST_PASSWORD = os.environ.get("CZIDLO_TEST_PASSWORD", "Test1234!")
if not ADMIN_PASSWORD:
    raise SystemExit("nastav CZIDLO_ADMIN_PASSWORD")

X, Y = "tst01", "tst02"

ACTORS = {
    "nepřihlášený":  None,
    "bez práv":      ("claude-nobody", TEST_PASSWORD),
    "kurátor jen Y": ("claude-cur-y", TEST_PASSWORD),
    "kurátor jen X": ("claude-cur-x", TEST_PASSWORD),
    "kurátor X i Y": ("claude-cur-xy", TEST_PASSWORD),
    "admin":         (ADMIN_LOGIN, ADMIN_PASSWORD),
}
SETUP = ("claude-cur-xy", TEST_PASSWORD)   # umi vse krome reaktivace


def call(method, path, auth, body=None, ctype=None):
    req = urllib.request.Request(API + path, method=method)
    if auth:
        req.add_header("Authorization", "Basic " +
                       base64.b64encode(f"{auth[0]}:{auth[1]}".encode()).decode())
    if body is not None:
        req.add_header("Content-Type", ctype or "application/json")
        req.data = body.encode()
    try:
        with urllib.request.urlopen(req, timeout=15) as r:
            return r.status, r.read().decode()
    except urllib.error.HTTPError as e:
        return e.code, e.read().decode()
    except Exception as e:
        return -1, str(e)


def urn(reg, n):        return f"urn:nbn:cz:{reg}-{n:06d}"
def get(u):             return call("GET", f"/documents/{u}", SETUP)
def deact(a, u):        return call("POST", f"/documents/{u}/deactivation", a, "test", "text/plain")
def react(a, u):        return call("DELETE", f"/documents/{u}/deactivation", a)
def add_pred(a, s, p):  return call("PUT", f"/documents/{s}/predecessors", a,
                                    json.dumps({"predecessorUrnNbn": p, "note": "test"}))
def rm_pred(a, s, p):   return call("DELETE", f"/documents/{s}/predecessors/{p}", a)


def status_of(u):
    c, b = get(u)
    if c != 200: return f"HTTP{c}"
    return json.loads(b).get("urnNbn", {}).get("status", "?")


def preds_of(u):
    c, b = get(u)
    if c != 200: return []
    return [f"{p['registrarCode']}-{p['documentCode']}"
            for p in (json.loads(b).get("urnNbn", {}).get("predecessors") or [])]


def ensure_active(u):
    if status_of(u) == "DEACTIVATED":
        react((ADMIN_LOGIN, ADMIN_PASSWORD), u)


results = []
def check(op, actor, expected, actual, note=""):
    ok = (actual == expected)
    results.append((op, actor, expected, actual, ok, note))
    print(f"  {'OK ' if ok else '!! '}{op:28} {actor:14} čekáno {expected:>3}  dostal {actual:>3}  {note}")


print("=" * 100 + "\nA) DEAKTIVOVAT X:x\n" + "=" * 100)
probe = urn(X, 1)
ensure_active(probe)
for actor, exp in [("nepřihlášený", 401), ("bez práv", 403), ("kurátor jen Y", 403)]:
    c, _ = deact(ACTORS[actor], probe)
    check("deaktivovat X:x", actor, exp, c)
for actor, doc in [("kurátor jen X", 1), ("kurátor X i Y", 2), ("admin", 3)]:
    u = urn(X, doc); ensure_active(u)
    c, _ = deact(ACTORS[actor], u)
    check("deaktivovat X:x", actor, 200, c, f"stav po: {status_of(u)}")

print("\n" + "=" * 100 + "\nB) REAKTIVOVAT X:x\n" + "=" * 100)
target = urn(X, 1)
if status_of(target) != "DEACTIVATED":
    deact(SETUP, target)
print(f"  (výchozí stav {target}: {status_of(target)})")
for actor, exp in [("nepřihlášený", 401), ("bez práv", 403), ("kurátor jen Y", 403),
                   ("kurátor jen X", 403), ("kurátor X i Y", 403)]:
    c, _ = react(ACTORS[actor], target)
    check("reaktivovat X:x", actor, exp, c)
c, _ = react(ACTORS["admin"], target)
check("reaktivovat X:x", "admin", 200, c, f"stav po: {status_of(target)}")

print("\n" + "=" * 100 + "\nC) PŘIDAT X:x2 JAKO PŘEDCHŮDCE X:x  (týž registrátor)\n" + "=" * 100)
succ = urn(X, 4); ensure_active(succ)
for actor, exp in [("nepřihlášený", 401), ("bez práv", 403), ("kurátor jen Y", 403)]:
    p = urn(X, 5); ensure_active(p)
    c, _ = add_pred(ACTORS[actor], succ, p)
    check("přidat X:x2 -> X:x", actor, exp, c)
for actor, pn in [("kurátor jen X", 5), ("kurátor X i Y", 6), ("admin", 7)]:
    p = urn(X, pn); ensure_active(p)
    c, _ = add_pred(ACTORS[actor], succ, p)
    created = f"{X}-{pn:06d}" in preds_of(succ)
    note = f"relace={'ano' if created else 'NE'}, předchůdce po: {status_of(p)}"
    if c != 200 and created: note += "  !!! ČÁSTEČNÝ ZÁPIS"
    check("přidat X:x2 -> X:x", actor, 200, c, note)

print("\n" + "=" * 100 + "\nD) PŘIDAT Y:y JAKO PŘEDCHŮDCE X:x  (cizí registrátor)\n" + "=" * 100)
succ2 = urn(X, 8); ensure_active(succ2)
for actor, exp in [("nepřihlášený", 401), ("bez práv", 403), ("kurátor jen Y", 403),
                   ("kurátor jen X", 403)]:
    p = urn(Y, 1); ensure_active(p)
    c, _ = add_pred(ACTORS[actor], succ2, p)
    check("přidat Y:y -> X:x", actor, exp, c)
for actor, pn in [("kurátor X i Y", 1), ("admin", 2)]:
    p = urn(Y, pn); ensure_active(p)
    c, _ = add_pred(ACTORS[actor], succ2, p)
    created = f"{Y}-{pn:06d}" in preds_of(succ2)
    note = f"relace={'ano' if created else 'NE'}, předchůdce po: {status_of(p)}"
    if c != 200 and created: note += "  !!! ČÁSTEČNÝ ZÁPIS"
    check("přidat Y:y -> X:x", actor, 200, c, note)

print("\n" + "=" * 100 + "\nE) ODEBRAT Y:y Z PŘEDCHŮDCŮ X:x\n" + "=" * 100)
def setup_relation(pn):
    p = urn(Y, pn); ensure_active(p)
    if f"{Y}-{pn:06d}" not in preds_of(succ2):
        add_pred(SETUP, succ2, p)
    return p
pf = setup_relation(5)
for actor, exp in [("nepřihlášený", 401), ("bez práv", 403), ("kurátor jen Y", 403)]:
    c, _ = rm_pred(ACTORS[actor], succ2, pf)
    check("odebrat Y:y z X:x", actor, exp, c)
for actor, pn in [("kurátor jen X", 5), ("kurátor X i Y", 6), ("admin", 7)]:
    p = setup_relation(pn)
    c, _ = rm_pred(ACTORS[actor], succ2, p)
    gone = f"{Y}-{pn:06d}" not in preds_of(succ2)
    check("odebrat Y:y z X:x", actor, 200, c, "relace odebrána" if gone else "relace ZŮSTALA")

print("\n" + "=" * 100)
failed = [r for r in results if not r[4]]
print(f"VÝSLEDEK: {len(results) - len(failed)}/{len(results)} podle očekávání")
if failed:
    print("\nNESOUHLASÍ:")
    for op, actor, exp, act, _, note in failed:
        print(f"  - {op:28} {actor:14} čekáno {exp}, dostal {act}   {note}")
print("=" * 100)
raise SystemExit(1 if failed else 0)
