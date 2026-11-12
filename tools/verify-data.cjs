// Node.js 22.13+ (node:sqlite). No external packages; database stays in memory.
const fs = require('node:fs');
const path = require('node:path');
const assert = require('node:assert/strict');
const {DatabaseSync} = require('node:sqlite');
const root = path.resolve(__dirname,'..');
const helper = fs.readFileSync(path.join(root,'app/src/main/java/com/example/ccm/data/DatabaseHelper.java'),'utf8');
const seed = fs.readFileSync(path.join(root,'app/src/main/assets/seed.sql'),'utf8');
const schema = helper.slice(helper.indexOf('private void createSchema'),helper.indexOf('// ---------- данные'));
const db = new DatabaseSync(':memory:');
db.exec('PRAGMA foreign_keys=ON');
for (const m of schema.matchAll(/db\.execSQL\(([\s\S]*?)\);/g)) {
    const sql = [...m[1].matchAll(/"(?:[^"\\]|\\.)*"/g)].map(x=>JSON.parse(x[0])).join('');
    db.exec(sql);
}
// Execute with the same line-oriented rules as the Android seed loader.
let statement='';
for(const line of seed.split(/\r?\n/)) {
    if(!line.trim() || line.trim().startsWith('--'))continue;
    statement+=line+'\n';
    if(line.trim().endsWith(';')) {db.exec(statement);statement='';}
}
if(statement.trim()) db.exec(statement);
assert.deepEqual(db.prepare('PRAGMA foreign_key_check').all(),[]);
assert.equal(db.prepare('SELECT count(*) n FROM banks').get().n,3);
assert.equal(db.prepare('SELECT count(*) n FROM products').get().n,9);
assert.equal(db.prepare("SELECT count(*) n FROM rate_rules WHERE product_id=7 AND client_segment='ALL'").get().n,1);
assert.equal(db.prepare("SELECT rate FROM rate_rules WHERE product_id=7 AND client_segment='ALL'").get().rate,19.9);
const longCredit=db.prepare("SELECT * FROM rate_rules WHERE product_id=7 AND client_segment='TB_REALTY'").get();
assert.equal(longCredit.max_term_months,180);
assert.equal(longCredit.min_amount,200000);
assert.equal(longCredit.rate,17.9);
for(const program of ['TB_SECONDARY','TB_NEW_BUILD']) {
    const rule=db.prepare('SELECT * FROM mortgage_program_rules WHERE product_id=8 AND program_code=?').get(program);
    assert.equal(rule.base_rate,17.9);
    assert.equal(rule.min_down_payment_pct,20.1);
    assert.equal(rule.max_term_months,360);
    assert.equal(rule.max_loan,100000000);
}
assert.equal(db.prepare('SELECT max(max_term_months) n FROM rate_rules WHERE product_id=1').get().n,60);
for(let month=1; month<=24; month++) {
    const rules=db.prepare('SELECT rate FROM rate_rules WHERE product_id=9 AND min_term_months<=? AND max_term_months>=?').all(month,month);
    assert.equal(rules.length,1);
    assert.equal(rules[0].rate,month===1?12:month<=3?12.3:month===4?14:month<=6?12:11.5);
}
const fourMonthCap=db.prepare('SELECT max_amount FROM rate_rules WHERE product_id=9 AND min_term_months=4').get().max_amount;
assert.ok(fourMonthCap*Math.pow(1+14/1200,4)<=3000000);
assert.ok((fourMonthCap+0.01)*Math.pow(1+14/1200,4)>3000000);
for(const table of ['rate_rules','deposit_rate_rules','vtb_deposit_rate_rules']) {
    assert.equal(db.prepare(`SELECT count(*) n FROM ${table} WHERE min_amount>max_amount`).get().n,0);
}
assert.equal(db.prepare('SELECT count(*) n FROM vtb_deposit_rate_rules WHERE product_id=6 AND term_months=12 AND min_amount<=499999.50 AND max_amount>=499999.50').get().n,1);
// Same reverse-dependency order as onUpgrade: foreign keys must not break the upgrade.
const upgrade = helper.slice(helper.indexOf('public void onUpgrade'),helper.indexOf('// ---------- схема'));
for (const m of upgrade.matchAll(/db\.execSQL\("(DROP TABLE[^"\n]+)"\)/g)) db.exec(m[1]);
assert.equal(db.prepare("SELECT count(*) n FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%'").get().n,0);
console.log('PASS: seed loader, 3 banks / 9 products, references, limits, decimal boundaries, upgrade drop order');
db.close();
