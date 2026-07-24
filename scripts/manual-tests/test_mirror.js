// Overi, ze se read-only blok a admin panel urn:nbn na frontendu navzajem vylucuji.
// read-only varianta renderuje "(registrováno ...)" v zavorce,
// admin panel renderuje samostatny prvek .app-ie-urnnbn.
//   npm i playwright-core && node test_mirror.js
const { chromium } = require('playwright-core');

const APP = process.env.CZIDLO_APP || 'http://localhost:4200/';
const API = process.env.CZIDLO_API || 'http://localhost:8080/web-api/api';
const ADMIN = [process.env.CZIDLO_ADMIN_LOGIN || 'claude-admin', process.env.CZIDLO_ADMIN_PASSWORD];
const TP = process.env.CZIDLO_TEST_PASSWORD || 'Test1234!';
const EXEC = process.env.PLAYWRIGHT_CHROMIUM || (process.env.HOME +
  '/Library/Caches/ms-playwright/chromium-1223/chrome-mac-arm64/Google Chrome for Testing.app/Contents/MacOS/Google Chrome for Testing');
if (!ADMIN[1]) { console.error('nastav CZIDLO_ADMIN_PASSWORD'); process.exit(2); }

const DOC = 'urn:nbn:cz:tst01-000003';   // registrator tst01
const USERS = {
  'bez práv':      ['claude-nobody', TP],
  'kurátor jen Y': ['claude-cur-y', TP],
  'kurátor jen X': ['claude-cur-x', TP],
  'admin':         ADMIN,
};
const EXPECT = {
  'bez práv':      { readonly: true,  panel: false },
  'kurátor jen Y': { readonly: true,  panel: false },
  'kurátor jen X': { readonly: false, panel: true  },
  'admin':         { readonly: false, panel: true  },
};

(async () => {
  const browser = await chromium.launch({ executablePath: EXEC, headless: true });
  let pass = 0, total = 0;
  for (const [role, creds] of Object.entries(USERS)) {
    const ctx = await browser.newContext();
    const page = await ctx.newPage();
    await page.goto(APP, { waitUntil: 'domcontentloaded' });
    await page.evaluate(async ({ api, login, password }) => {
      const r = await fetch(api + '/user', { headers: { Authorization: 'Basic ' + btoa(login + ':' + password) } });
      const info = await r.json();
      localStorage.setItem('auth_username', login);
      localStorage.setItem('auth_password', password);
      localStorage.setItem('auth_user_info', JSON.stringify(info));
      localStorage.setItem('auth_user_id', String(info.id));
      localStorage.setItem('auth_is_admin', info.admin ? 'true' : 'false');
      localStorage.setItem('auth_expires_at', String(Date.now() + 86400000));
    }, { api: API, login: creds[0], password: creds[1] });

    await page.goto(APP + 'search?q=' + encodeURIComponent(DOC), { waitUntil: 'networkidle' });
    await page.waitForTimeout(2000);
    for (let i = 0; i < 6; i++) {
      const c = await page.locator('.app-ie-header:has(img[src*="arrow-down2"])').all();
      if (!c.length) break;
      for (const h of c) { try { await h.click({ timeout: 700 }); await page.waitForTimeout(200); } catch (e) {} }
      await page.waitForTimeout(350);
    }
    const text = await page.locator('body').innerText();
    const readonly = /\(registrováno\s/i.test(text);
    const panel = await page.locator('.app-ie-urnnbn').count() > 0;
    const exp = EXPECT[role];
    const okR = readonly === exp.readonly, okP = panel === exp.panel;
    total += 2; pass += (okR ? 1 : 0) + (okP ? 1 : 0);
    const both = readonly && panel ? '   <<< OBOJÍ NARÁZ!' : '';
    console.log(`  ${role.padEnd(14)} read-only=${String(readonly).padEnd(5)}${okR ? '' : '(!!)'}  admin-panel=${String(panel).padEnd(5)}${okP ? '' : '(!!)'}${both}`);
    await ctx.close();
  }
  console.log(`\n  VÝSLEDEK: ${pass}/${total} — bloky se vzájemně vylučují`);
  await browser.close();
  process.exit(pass === total ? 0 : 1);
})();
