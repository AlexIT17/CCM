-- ---------- 1. Банки ----------
INSERT INTO banks (id, name, color, website) VALUES
  (1, 'Сбербанк',    '#21A038', 'https://www.sberbank.ru'),
  (2, 'ВТБ',         '#0A2896', 'https://www.vtb.ru'),
  (3, 'Т-Банк',      '#FFDD2D', 'https://www.tbank.ru');

-- ---------- 2. Продукты ----------
INSERT INTO products (id, bank_id, type, name) VALUES
  (1,  1, 'Кредит',  'Сбербанк — Кредит наличными'),
  (2,  1, 'Ипотека', 'Сбербанк — Ипотека: программы и расчёт'),
  (3,  1, 'Вклад',   'Сбербанк — СберВклад'),

  (4,  2, 'Кредит',  'ВТБ — Кредит наличными'),
  (5,  2, 'Ипотека', 'ВТБ — Рыночная ипотека'),
  (6,  2, 'Вклад',   'ВТБ — ВТБ-Вклад'),

  (7,  3, 'Кредит',  'Т-Банк — Кредит наличными'),
  (8,  3, 'Ипотека', 'Т-Банк — Ипотека'),
  (9,  3, 'Вклад',   'Т-Банк — СмартВклад');

-- ---------- 3. Источники ----------
INSERT INTO source_info (id, url, checked_date) VALUES
  (1,  'https://www.sberbank.ru/ru/person/credits/money', '2026-10-13'),
  (2,  'https://www.sberbank.ru/ru/person/mortgage',      '2026-10-13'),
  (3,  'https://www.sberbank.ru/ru/person/contributions', '2026-10-13'),

  (4,  'https://www.vtb.ru/personal/kredit/',             '2026-10-13'),
  (5,  'https://www.vtb.ru/personal/ipoteka/',            '2026-10-13'),
  (6,  'https://www.vtb.ru/personal/vklady/',             '2026-10-13'),

  (7,  'https://www.tbank.ru/credit/',                    '2026-10-13'),
  (8,  'https://www.tbank.ru/mortgage/',                  '2026-10-13'),
  (9,  'https://www.tbank.ru/deposits/',                  '2026-10-13'),


  (16, 'https://digital.sber.ru/ru/person/contributions/deposits/vklad', '2026-11-10'),
  (17, 'https://yandex.ru/finance/deposit/offers/sberbank-sbervklad', '2026-11-10'),
  (18, 'https://bankinform.ru/services/deposits/14816/91', '2026-11-10'),
  (27, 'https://www.vtb.ru/personal/kredit/kalkulyator/', '2026-11-10'),
  (28, 'https://www.vtb.ru/personal/vklady-i-scheta/kalkulyator/', '2026-11-11'),
  (29, 'https://www.vtb.ru/personal/vklady-i-scheta/vtb-vklad-r/', '2026-11-11'),
  (30, 'https://www.vtb.ru/personal/vklady-i-scheta/vtb-schet/', '2026-11-11'),
  (31, 'https://fondium.ru/banks/deposits/vtb/vtb-vklad/', '2026-11-09');

-- ---------- 3а. Параметры калькулятора вкладов ВТБ ----------
INSERT INTO vtb_deposit_settings
  (product_id, min_online_amount, min_office_amount, min_interest_balance,
   min_term_months, max_term_months, max_ui_amount, max_vklad_rate,
   max_account_rate, source_id, account_source_id) VALUES
  (6, 10000, 50000, 1000, 3, 36, 1000000000, 13.70, 14.20, 29, 30);

-- ---------- 3б. Справочная сетка ставок ВТБ-Вклада ----------
INSERT INTO vtb_deposit_rate_rules
  (product_id, term_months, min_amount, max_amount, base_rate, source_id) VALUES
  (6, 3, 10000, 499999, 10.80, 31),
  (6, 3, 500000, 1499999, 10.90, 31),
  (6, 3, 1500000, 4999999, 12.00, 31),
  (6, 3, 5000000, 14999999, 12.00, 31),
  (6, 3, 15000000, 1000000000, 12.00, 31),
  (6, 4, 10000, 499999, 10.80, 31),
  (6, 4, 500000, 1499999, 10.90, 31),
  (6, 4, 1500000, 4999999, 12.00, 31),
  (6, 4, 5000000, 14999999, 12.00, 31),
  (6, 4, 15000000, 1000000000, 12.00, 31),
  (6, 6, 10000, 499999, 10.80, 31),
  (6, 6, 500000, 1499999, 10.90, 31),
  (6, 6, 1500000, 4999999, 12.00, 31),
  (6, 6, 5000000, 14999999, 12.00, 31),
  (6, 6, 15000000, 1000000000, 12.00, 31),
  (6, 12, 10000, 499999, 11.00, 31),
  (6, 12, 500000, 1499999, 11.00, 31),
  (6, 12, 1500000, 4999999, 12.00, 31),
  (6, 12, 5000000, 14999999, 12.00, 31),
  (6, 12, 15000000, 1000000000, 12.00, 31),
  (6, 18, 10000, 499999, 10.50, 31),
  (6, 18, 500000, 1499999, 10.50, 31),
  (6, 18, 1500000, 4999999, 11.50, 31),
  (6, 18, 5000000, 14999999, 11.50, 31),
  (6, 18, 15000000, 1000000000, 11.50, 31),
  (6, 24, 10000, 499999, 9.20, 31),
  (6, 24, 500000, 1499999, 9.20, 31),
  (6, 24, 1500000, 4999999, 10.20, 31),
  (6, 24, 5000000, 14999999, 10.20, 31),
  (6, 24, 15000000, 1000000000, 10.20, 31),
  (6, 36, 10000, 499999, 8.80, 31),
  (6, 36, 500000, 1499999, 8.80, 31),
  (6, 36, 1500000, 4999999, 9.80, 31),
  (6, 36, 5000000, 14999999, 9.80, 31),
  (6, 36, 15000000, 1000000000, 9.80, 31);

-- ---------- 4. Правила ставок ----------

INSERT INTO rate_rules
  (product_id, rate, client_segment, min_amount, max_amount, min_term_months, max_term_months, source_id) VALUES

  -- ===== Сбербанк =====
  (1, 18.40, 'SALARY_PENSION', 10000, 20000000,  3, 240, 1),
  (1, 20.40, 'GENERAL',        10000, 20000000,  3, 240, 1),

  -- Ипотека (вторичка)
  (2, 16.00, 'ALL',  300000, 100000000,  12, 360, 2),

  -- СберВклад
  (3, 13.50, 'ALL',  100000, 1000000000,   1,  36, 16),

  -- ===== ВТБ =====
  (4, 43.03, 'VTB_GENERAL',   30000, 40000000, 6, 60, 27),
  (4, 42.55, 'VTB_SALARY',    30000, 40000000, 6, 84, 27),
  (4, 42.55, 'VTB_PENSIONER', 30000,   500000, 6, 36, 27),
  -- Вклад: до 13,7%
  (6, 13.70, 'ALL',   10000,  10000000,   3,  36, 6),

  -- ===== Т-Банк =====
  -- Кредит: 19,88–39,9%
  (7, 19.88, 'ALL',   50000,   5000000,  12,  60, 7),
  (7, 39.90, 'ALL',   50000,   5000000,  12,  60, 7),
  -- Ипотека: от 16,9%
  (8, 16.90, 'ALL',  500000,  50000000,  12, 360, 8),
  -- Вклад: до 12,3%
  (9, 12.30, 'ALL',   50000,  10000000,   1,  24, 9);

-- ---------- 5. Справочная сетка СберВклада ----------

INSERT INTO deposit_rate_rules
  (product_id, min_amount, max_amount, min_term_months, max_term_months, top_up, rate, source_id) VALUES
  (3, 100000, 499999.99, 1, 2, 0, 11.30, 17),
  (3, 100000, 499999.99, 1, 2, 1, 10.80, 17),
  (3, 100000, 499999.99, 3, 5, 0, 12.30, 17),
  (3, 100000, 499999.99, 3, 5, 1, 11.80, 17),
  (3, 100000, 499999.99, 6, 6, 0, 11.40, 17),
  (3, 100000, 499999.99, 6, 6, 1, 10.90, 17),
  (3, 100000, 499999.99, 7, 7, 0, 11.10, 17),
  (3, 100000, 499999.99, 7, 7, 1, 10.60, 17),
  (3, 100000, 499999.99, 8, 9, 0, 10.80, 17),
  (3, 100000, 499999.99, 8, 9, 1, 10.30, 17),
  (3, 100000, 499999.99, 10, 11, 0, 9.80, 17),
  (3, 100000, 499999.99, 10, 11, 1, 9.30, 17),
  (3, 100000, 499999.99, 12, 12, 0, 8.80, 17),
  (3, 100000, 499999.99, 12, 12, 1, 8.30, 17),
  (3, 100000, 499999.99, 18, 18, 0, 8.30, 17),
  (3, 100000, 499999.99, 18, 18, 1, 7.80, 17),
  (3, 100000, 499999.99, 24, 24, 0, 8.30, 17),
  (3, 100000, 499999.99, 24, 24, 1, 7.80, 17),
  (3, 100000, 499999.99, 36, 36, 0, 6.30, 17),
  (3, 100000, 499999.99, 36, 36, 1, 5.80, 17),
  (3, 500000, 999999.99, 1, 2, 0, 11.40, 17),
  (3, 500000, 999999.99, 1, 2, 1, 10.90, 17),
  (3, 500000, 999999.99, 3, 5, 0, 12.40, 17),
  (3, 500000, 999999.99, 3, 5, 1, 11.90, 17),
  (3, 500000, 999999.99, 6, 6, 0, 11.50, 17),
  (3, 500000, 999999.99, 6, 6, 1, 11.00, 17),
  (3, 500000, 999999.99, 7, 7, 0, 11.20, 17),
  (3, 500000, 999999.99, 7, 7, 1, 10.70, 17),
  (3, 500000, 999999.99, 8, 9, 0, 10.90, 17),
  (3, 500000, 999999.99, 8, 9, 1, 10.40, 17),
  (3, 500000, 999999.99, 10, 11, 0, 9.90, 17),
  (3, 500000, 999999.99, 10, 11, 1, 9.40, 17),
  (3, 500000, 999999.99, 12, 12, 0, 8.90, 17),
  (3, 500000, 999999.99, 12, 12, 1, 8.40, 17),
  (3, 500000, 999999.99, 18, 18, 0, 8.40, 17),
  (3, 500000, 999999.99, 18, 18, 1, 7.90, 17),
  (3, 500000, 999999.99, 24, 24, 0, 8.40, 17),
  (3, 500000, 999999.99, 24, 24, 1, 7.90, 17),
  (3, 500000, 999999.99, 36, 36, 0, 6.40, 17),
  (3, 500000, 999999.99, 36, 36, 1, 5.90, 17),
  (3, 1000000, 1000000000, 1, 2, 0, 11.50, 17),
  (3, 1000000, 1000000000, 1, 2, 1, 11.00, 17),
  (3, 1000000, 1000000000, 3, 5, 0, 12.50, 17),
  (3, 1000000, 1000000000, 3, 5, 1, 12.00, 17),
  (3, 1000000, 1000000000, 6, 6, 0, 11.60, 17),
  (3, 1000000, 1000000000, 6, 6, 1, 11.10, 17),
  (3, 1000000, 1000000000, 7, 7, 0, 11.30, 17),
  (3, 1000000, 1000000000, 7, 7, 1, 10.80, 17),
  (3, 1000000, 1000000000, 8, 9, 0, 11.00, 17),
  (3, 1000000, 1000000000, 8, 9, 1, 10.50, 17),
  (3, 1000000, 1000000000, 10, 11, 0, 10.00, 17),
  (3, 1000000, 1000000000, 10, 11, 1, 9.50, 17),
  (3, 1000000, 1000000000, 12, 12, 0, 9.00, 17),
  (3, 1000000, 1000000000, 12, 12, 1, 8.50, 17),
  (3, 1000000, 1000000000, 18, 18, 0, 8.50, 17),
  (3, 1000000, 1000000000, 18, 18, 1, 8.00, 17),
  (3, 1000000, 1000000000, 24, 24, 0, 8.50, 17),
  (3, 1000000, 1000000000, 24, 24, 1, 8.00, 17),
  (3, 1000000, 1000000000, 36, 36, 0, 6.50, 17),
  (3, 1000000, 1000000000, 36, 36, 1, 6.00, 17);

-- ---------- 6. Ипотека Сбербанка: программы и региональные условия ----------
INSERT INTO source_info (id, url, checked_date) VALUES
  (19, 'https://digital.sber.ru/ru/person/credits/home/ipotechniy-kalkulyator', '2026-11-10'),
  (20, 'https://domclick.ru/ipoteka?from=topline2020', '2026-11-10'),
  (22, 'https://digital.sber.ru/ru/person/credits/home/buying_complete_house', '2026-11-10'),
  (23, 'https://www.sberbank.ru/ru/sberpress/all/article?blockID=1303&lang=ru&newsID=b7fad162-bc6a-41d3-926b-b8327dd4088f&regionID=77&type=NEWS', '2026-11-10'),
  (24, 'https://doneck.domclick.ru/ipoteka/programs/it-workers', '2026-11-10'),
  (25, 'https://www-1.banki.ru/products/hypothec/sberbank/calculator/', '2026-11-10'),
  (26, 'https://realtystats.ru/penza', '2026-11-10');

-- Источники условий ипотечного калькулятора ВТБ для Пензы.
INSERT INTO source_info (id, url, checked_date) VALUES
  (32, 'https://www.vtb.ru/personal/ipoteka/ipotechnyj-kalkulyator/', '2026-11-11'),
  (33, 'https://www.vtb.ru/personal/ipoteka/penza/', '2026-11-11'),
  (34, 'https://www.vtb.ru/personal/ipoteka/vtorichnoe-zhile/', '2026-11-11'),
  (35, 'https://www.vtb.ru/personal/ipoteka/novostrojki/', '2026-11-11'),
  (36, 'https://www.vtb.ru/personal/ipoteka/dlya-semej-s-detmi/', '2026-11-11'),
  (37, 'https://www.vtb.ru/personal/ipoteka/it/', '2026-11-11'),
  (38, 'https://www.vtb.ru/personal/ipoteka/stroitelstvo-doma/', '2026-11-11'),
  (39, 'https://www.vtb.ru/personal/ipoteka/zemelniy-uchastok/', '2026-11-11'),
  (40, 'https://www.vtb.ru/personal/ipoteka/svoy-dom/', '2026-11-11'),
  (41, 'https://www.vtb.ru/personal/ipoteka/refinansirovanie/', '2026-11-11'),
  (42, 'https://www.vtb.ru/personal/ipoteka/voennaja-ipoteka/', '2026-11-11'),
  (43, 'https://www.vtb.ru/personal/ipoteka/mashinomesta-i-kladovki/', '2026-11-11'),
  (44, 'https://www.vtb.ru/personal/ipoteka/commercial/', '2026-11-11');

INSERT INTO mortgage_program_rules
  (product_id, program_code, region_group, base_rate, min_rate, max_loan,
   min_down_payment_pct, min_property_price, min_term_months, max_term_months, source_id) VALUES
  (2, 'NEW_BUILD',  'OTHER',  2.00,  0.50, 100000000, 20.1, 376000, 12, 360, 25),
  (2, 'READY_HOME', 'OTHER', 15.50, 12.50, 100000000, 20.1, 376000, 12, 360, 25),
  (2, 'IT',         'OTHER',  6.00,  6.00,  18000000, 20.1, 376000, 12, 360, 24);

-- Дополнительные категории из калькулятора Сбера (минимальные рекламные ставки,
INSERT INTO mortgage_program_rules
  (product_id, program_code, region_group, base_rate, min_rate, max_loan,
   min_down_payment_pct, min_property_price, min_term_months, max_term_months, source_id) VALUES
  (2, 'BUY_HOME',       'OTHER',  6.00,  3.00, 100000000, 20.1, 376000, 12, 360, 19),
  (2, 'BUILD_HOME',     'OTHER',  6.00,  3.00, 100000000, 20.1, 376000, 12, 360, 19),
  (2, 'LAND_DACHA',     'OTHER', 16.80, 13.80, 100000000, 20.1, 376000, 12, 360, 19),
  (2, 'REFINANCE',      'OTHER',  2.00,  0.50, 100000000,  0.0, 376000, 12, 360, 19),
  (2, 'GARAGE',         'OTHER', 18.70, 15.70, 100000000, 20.1, 376000, 12, 360, 19),
  (2, 'CASH_SECURED',   'OTHER', 16.20, 13.20, 100000000,  0.0, 376000, 12, 360, 19),
  (2, 'COMMERCIAL',     'OTHER', 19.20, 16.20, 100000000, 20.1, 376000, 12, 360, 19);

-- ---------- 7. Ипотека ВТБ: калькулятор для Пензы ----------
INSERT INTO mortgage_program_rules
  (product_id, program_code, region_group, base_rate, min_rate, max_loan,
   min_down_payment_pct, min_property_price, min_term_months, max_term_months, source_id) VALUES
  (5, 'VTB_SECONDARY',          'PENZA', 18.00, 16.10, 100000000, 20.1, 500000, 12, 360, 34),
  (5, 'VTB_NEW_BUILD',          'PENZA', 18.00, 16.10, 100000000, 20.1, 500000, 12, 360, 35),
  (5, 'VTB_COMBINED',           'PENZA', 18.00,  6.00,  30000000, 20.1, 500000, 12, 360, 33),
  (5, 'VTB_BUILD_HOME',         'PENZA', 18.90, 18.30,  60000000, 20.1, 500000, 12, 360, 38),
  (5, 'VTB_LAND',               'PENZA', 18.90, 17.30,  30000000, 20.1, 500000, 12, 360, 39),
  (5, 'VTB_REFINANCE_LAND',     'PENZA', 18.60, 18.60,  60000000,  0.0, 500000, 12, 360, 41),
  (5, 'VTB_READY_HOME',         'PENZA', 18.40, 16.80, 100000000, 20.1, 500000, 12, 360, 40),
  (5, 'VTB_IT',                 'PENZA',  6.00,  6.00,   9000000, 20.1, 500000, 12, 360, 37),
  (5, 'VTB_REFINANCE',          'PENZA', 18.60, 18.60, 100000000,  0.0, 500000, 12, 360, 41),
  (5, 'VTB_MILITARY',           'PENZA', 19.00, 19.00,   2125000,  5.0, 500000, 12, 300, 42),
  (5, 'VTB_MORTGAGED_PROPERTY', 'PENZA', 18.00, 16.10, 100000000, 20.1, 500000, 12, 360, 34),
  (5, 'VTB_MILITARY_REFINANCE', 'PENZA', 19.00, 19.00,   2125000,  0.0, 500000, 12, 300, 42),
  (5, 'VTB_PARKING',            'PENZA', 19.90, 19.00,   4000000, 20.1, 500000, 12, 360, 43),
  (5, 'VTB_STORAGE',            'PENZA', 19.90, 19.00,   2000000, 20.1, 500000, 12, 360, 43),
  (5, 'VTB_COMMERCIAL',         'PENZA', 20.90, 20.50,  30000000, 30.1, 500000, 12, 360, 44);

-- Семейная ипотека ВТБ
INSERT INTO family_mortgage_rules
  (product_id, region_group, min_children, max_children, rate, max_loan,
   min_down_payment_pct, min_property_price, min_term_months, max_term_months, source_id) VALUES
  (5, 'PENZA', 1, 1, 10.00,  6000000, 20.1, 500000, 12, 360, 36),
  (5, 'PENZA', 2, 2,  8.00,  8000000, 20.1, 500000, 12, 360, 36),
  (5, 'PENZA', 3, 3,  6.00, 10000000, 20.1, 500000, 12, 360, 36),
  (5, 'PENZA', 4, 4,  4.00, 10000000, 20.1, 500000, 12, 360, 36),
  (5, 'PENZA', 5, 99, 2.00, 10000000, 20.1, 500000, 12, 360, 36);

-- Скидки ВТБ для Пензы. Применяются только при выполнении соответствующих условий программы.
INSERT INTO mortgage_adjustments (product_id, program_code, adjustment_code, rate_delta, source_id) VALUES
  (5, 'VTB_SECONDARY', 'SALARY_CURRENT', -0.30, 33),
  (5, 'VTB_SECONDARY', 'SALARY_NEW', -0.40, 33),
  (5, 'VTB_SECONDARY', 'ONLINE', -0.30, 33),
  (5, 'VTB_SECONDARY', 'LARGE_LOAN', -0.20, 33),
  (5, 'VTB_SECONDARY', 'INSURANCE', -1.00, 33),
  (5, 'VTB_NEW_BUILD', 'SALARY_CURRENT', -0.40, 35),
  (5, 'VTB_NEW_BUILD', 'SALARY_NEW', -0.40, 35),
  (5, 'VTB_NEW_BUILD', 'ONLINE', -0.30, 33),
  (5, 'VTB_NEW_BUILD', 'INSURANCE', -1.00, 35),
  (5, 'VTB_READY_HOME', 'SALARY_CURRENT', -0.40, 40),
  (5, 'VTB_READY_HOME', 'SALARY_NEW', -0.40, 40),
  (5, 'VTB_READY_HOME', 'ONLINE', -0.30, 33),
  (5, 'VTB_READY_HOME', 'INSURANCE', -1.00, 40),
  (5, 'VTB_BUILD_HOME', 'SALARY_CURRENT', -0.40, 38),
  (5, 'VTB_BUILD_HOME', 'SALARY_NEW', -0.40, 38),
  (5, 'VTB_BUILD_HOME', 'INSURANCE', -1.00, 38),
  (5, 'VTB_LAND', 'SALARY_CURRENT', -0.40, 39),
  (5, 'VTB_LAND', 'SALARY_NEW', -0.40, 39),
  (5, 'VTB_LAND', 'INSURANCE', -1.00, 39),
  (5, 'VTB_REFINANCE', 'INSURANCE', -1.00, 41),
  (5, 'VTB_REFINANCE_LAND', 'INSURANCE', -1.00, 41);

-- Ставка и лимит зависят от числа детей и дополнительных условий программы.
INSERT INTO family_mortgage_rules
  (product_id, region_group, min_children, max_children, rate, max_loan,
   min_down_payment_pct, min_property_price, min_term_months, max_term_months, source_id) VALUES
  (2, 'OTHER', 1, 1, 10.00,  6000000, 20.1, 376000, 12, 360, 23),
  (2, 'OTHER', 2, 2,  8.00,  8000000, 20.1, 376000, 12, 360, 23),
  (2, 'OTHER', 3, 3,  6.00, 10000000, 20.1, 376000, 12, 360, 23),
  (2, 'OTHER', 4, 4,  4.00, 10000000, 20.1, 376000, 12, 360, 23),
  (2, 'OTHER', 5, 99, 2.00, 10000000, 20.1, 376000, 12, 360, 23);

-- Надбавки, опубликованные на страницах программ Сбера/Домклик.
INSERT INTO mortgage_adjustments (product_id, program_code, adjustment_code, rate_delta, source_id) VALUES
  (2, 'READY_HOME', 'GUARANTEE_DISCOUNT', -0.70, 19),
  (2, 'READY_HOME', 'ONLINE_DEAL_DISCOUNT', -0.50, 19),
  (2, 'READY_HOME', 'APPROVED_TAG_DISCOUNT', -0.30, 19),
  (2, 'READY_HOME', 'SALARY_DISCOUNT', -1.00, 19);

UPDATE rate_rules SET rate=19.90, source_id=4 WHERE product_id=4;
UPDATE source_info SET url='https://www.vtb.ru/personal/kredit/nalichnymi/', checked_date='2026-11-11' WHERE id=4;
UPDATE rate_rules SET max_term_months=60 WHERE product_id=1;
UPDATE rate_rules SET rate=19.90 WHERE product_id=7 AND rate<30;
DELETE FROM rate_rules WHERE product_id=7 AND rate>30;
UPDATE source_info SET url='https://www.tbank.ru/loans/cash-loan/nopledge/tariffs/', checked_date='2026-11-11' WHERE id=7;
UPDATE source_info SET url='https://www.tbank.ru/bank/help/loans/mortgage/secondary-mortgage/terms/', checked_date='2026-11-11' WHERE id=8;
UPDATE mortgage_program_rules SET base_rate=15.50, min_rate=15.50, source_id=20 WHERE product_id=2 AND program_code='READY_HOME';
UPDATE source_info SET checked_date='2026-11-11' WHERE id=20;
UPDATE vtb_deposit_rate_rules SET max_amount=max_amount+0.99 WHERE max_amount<1000000000;

INSERT INTO source_info (id,url,checked_date) VALUES
 (45,'https://www.vbr.ru/novosti/banki/2026/11/10/tbank-povysit-stavki-vkladov/','2026-11-11');
DELETE FROM rate_rules WHERE product_id=9;
INSERT INTO rate_rules
 (product_id,rate,client_segment,min_amount,max_amount,min_term_months,max_term_months,source_id) VALUES
 (9,12.0,'ALL',50000,10000000,1,1,45),
 (9,12.3,'ALL',50000,10000000,2,3,45),
 (9,14.0,'ALL',50000,2863989.96,4,4,45),
 (9,12.0,'ALL',50000,10000000,5,6,45),
 (9,11.5,'ALL',50000,10000000,7,24,45);

-- Версия 4: Т-Банк
INSERT INTO source_info (id,url,checked_date) VALUES
 (46,'https://www.tbank.ru/mortgage/','2026-11-11'),
 (47,'https://www.tbank.ru/loans/cash-loan/realty/','2026-11-11');
UPDATE rate_rules SET rate=17.9,max_amount=100000000,source_id=46 WHERE product_id=8;
INSERT INTO rate_rules
 (product_id,rate,client_segment,min_amount,max_amount,min_term_months,max_term_months,source_id) VALUES
 (7,17.9,'TB_REALTY',200000,30000000,12,180,47);
INSERT INTO mortgage_program_rules
 (product_id,program_code,region_group,base_rate,min_rate,max_loan,min_down_payment_pct,min_property_price,min_term_months,max_term_months,source_id) VALUES
 (8,'TB_SECONDARY','ALL',17.9,17.9,100000000,20.1,625783,12,360,46),
 (8,'TB_NEW_BUILD','ALL',17.9,17.9,100000000,20.1,625783,12,360,46);
