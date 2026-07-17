// Overi viditelnost tlacitek u urn:nbn na frontendu podle OPRAVNENI-URNNBN.md.
// Vyzaduje bezici frontend, playwright-core a chromium v ms-playwright cache.
//   npm i playwright-core && node test_ui.js
const { chromium } = require('playwright-core');

const APP = process.env.CZIDLO_APP || 'http://localhost:4200/';
const API = process.env.CZIDLO_API || 'http://localhost:8080/web-api/api';
const ADMIN = [process.env.CZIDLO_ADMIN_LOGIN || 'claude-admin', process.env.CZIDLO_ADMIN_PASSWORD];
const TP = process.env.CZIDLO_TEST_PASSWORD || 'Test1234!';
const EXEC = process.env.PLAYWRIGHT_CHROMIUM || (process.env.HOME +
  '/Library/Caches/ms-playwright/chromium-1223/chrome-mac-arm64/Google Chrome for Testing.app/Contents/MacOS/Google Chrome for Testing');
if (!ADMIN[1]) { console.error('nastav CZIDLO_ADMIN_PASSWORD'); process.exit(2); }

const USERS = {
  'nepřihlášený':  null,
  'bez práv':      ['claude-nobody', TP],
  'kurátor jen Y': ['claude-cur-y', TP],
  'kurátor jen X': ['claude-cur-x', TP],
  'kurátor X i Y': ['claude-cur-xy', TP],
  'admin':         ADMIN,
};
const SEES_PANEL = { 'nepřihlášený': false, 'bez práv': false, 'kurátor jen Y': false, 'kurátor jen X': true, 'kurátor X i Y': true, 'admin': true };
const IS_ADMIN =   { 'nepřihlášený': false, 'bez práv': false, 'kurátor jen Y': false, 'kurátor jen X': false, 'kurátor X i Y': false, 'admin': true };

// scenare pouzivaji dokumenty registratora tst01 (= X) v ruznych stavech
const SCENARIOS = [
  { doc: 'urn:nbn:cz:tst01-000003', label: 'X:x ACTIVE, bez předchůdců',
    expect: (r) => ({ 'Deaktivovat': SEES_PANEL[r], 'Reaktivovat': false, 'Přidat předchůdce': SEES_PANEL[r] }) },
  { doc: 'urn:nbn:cz:tst01-000001', label: 'X:x DEACTIVATED',
    expect: (r) => ({ 'Deaktivovat': false, 'Reaktivovat': SEES_PANEL[r] && IS_ADMIN[r], 'Přidat předchůdce': SEES_PANEL[r] }) },
  { doc: 'urn:nbn:cz:tst01-000004', label: 'X:x ACTIVE, s předchůdci',
    expect: (r) => ({ 'Deaktivovat': SEES_PANEL[r], 'Reaktivovat': false, 'Přidat předchůdce': SEES_PANEL[r], 'Odebrat': SEES_PANEL[r] }) },
];

async function login(page, creds) {
  await page.goto(APP, { waitUntil: 'domcontentloaded' });
  if (!creds) { await page.evaluate(() => localStorage.clear()); return { ok: true, rights: '-' }; }
  return await page.evaluate(async ({ api, login, password }) => {
    const r = await fetch(api + '/user', { headers: { Authorization: 'Basic ' + btoa(login + ':' + password) } });
    if (!r.ok) return { ok: false, status: r.status };
    const info = await r.json();
    localStorage.setItem('auth_username', login);
    localStorage.setItem('auth_password', password);
    localStorage.setItem('auth_user_info', JSON.stringify(info));
    localStorage.setItem('auth_user_id', String(info.id));
    localStorage.setItem('auth_is_admin', info.admin ? 'true' : 'false');
    localStorage.setItem('auth_expires_at', String(Date.now() + 86400000));
    return { ok: true, rights: JSON.stringify(info.registrarRights) };
  }, { api: API, login: creds[0], password: creds[1] });
}

async function openDoc(page, urn) {
  await page.goto(APP + 'search?q=' + encodeURIComponent(urn), { waitUntil: 'networkidle' });
  await page.waitForTimeout(2200);
  // klik je prepinac -> klikame VYHRADNE na sbalene sekce (sipka arrow-down2)
  for (let pass = 0; pass < 6; pass++) {
    const collapsed = await page.locator('.app-ie-header:has(img[src*="arrow-down2"])').all();
    if (collapsed.length === 0) break;
    for (const h of collapsed) { try { await h.click({ timeout: 700 }); await page.waitForTimeout(200); } catch (e) {} }
    await page.waitForTimeout(400);
  }
}

async function visible(page, label) {
  const loc = page.locator(`app-button:has-text("${label}")`);
  const n = await loc.count();
  for (let i = 0; i < n; i++) { try { if (await loc.nth(i).isVisible()) return true; } catch (e) {} }
  return false;
}

(async () => {
  const browser = await chromium.launch({ executablePath: EXEC, headless: true });
  let pass = 0, total = 0; const bad = [];
  for (const sc of SCENARIOS) {
    console.log('\n' + '='.repeat(96) + `\n${sc.doc}   (${sc.label})\n` + '='.repeat(96));
    for (const [role, creds] of Object.entries(USERS)) {
      const ctx = await browser.newContext();
      const page = await ctx.newPage();
      const sess = await login(page, creds);
      if (!sess.ok) { console.log(`  !! ${role}: přihlášení selhalo`); await ctx.close(); continue; }
      await openDoc(page, sc.doc);
      const exp = sc.expect(role); const parts = [];
      for (const [label, want] of Object.entries(exp)) {
        const got = await visible(page, label); total++;
        const ok = got === want; if (ok) pass++; else bad.push(`${sc.doc} | ${role} | ${label}: čekáno ${want}, dostal ${got}`);
        parts.push(`${label}=${got ? 'ANO' : 'ne'}${ok ? '' : '(!!)'}`);
      }
      console.log(`  ${role.padEnd(14)} práva=${String(sess.rights).padEnd(20)} ${parts.join('  ')}`);
      await ctx.close();
    }
  }
  console.log('\n' + '='.repeat(96) + `\nVÝSLEDEK UI: ${pass}/${total} podle očekávání`);
  if (bad.length) { console.log('\nNESOUHLASÍ:'); bad.forEach(b => console.log('  - ' + b)); }
  console.log('='.repeat(96));
  await browser.close();
  process.exit(pass === total ? 0 : 1);
})();
