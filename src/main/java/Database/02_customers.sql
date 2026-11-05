-- =====================================================================
-- 02_customers.sql  -  Sample data: 10 members + their workouts, meals, goals
--
-- Run AFTER 01_admin.sql. (To start over, just run 01_admin.sql again.)
-- Every sample member logs in with password:  pass123
--   usernames: member01 ... member10
-- =====================================================================

USE fitness_tracker;

-- ---------- 10 sample members (ids 2-11) ----------
INSERT INTO users (id, username, password, name, role) VALUES
(2,  'member01', SHA2('pass123', 256), 'Maria Santos',      'MEMBER'),
(3,  'member02', SHA2('pass123', 256), 'Juan Dela Cruz',    'MEMBER'),
(4,  'member03', SHA2('pass123', 256), 'Ana Reyes',         'MEMBER'),
(5,  'member04', SHA2('pass123', 256), 'Carlo Mendoza',     'MEMBER'),
(6,  'member05', SHA2('pass123', 256), 'Liza Garcia',       'MEMBER'),
(7,  'member06', SHA2('pass123', 256), 'Paolo Villanueva',  'MEMBER'),
(8,  'member07', SHA2('pass123', 256), 'Grace Lim',         'MEMBER'),
(9,  'member08', SHA2('pass123', 256), 'Miguel Torres',     'MEMBER'),
(10, 'member09', SHA2('pass123', 256), 'Bea Ramos',         'MEMBER'),
(11, 'member10', SHA2('pass123', 256), 'Jose Navarro',      'MEMBER');

-- ---------- workouts (transactions) ----------
INSERT INTO workouts (user_id, name, duration, calories_burned, workout_date) VALUES
(2,  'Running',        45, 480, '2026-10-26'),
(2,  'Yoga',           60, 210, '2026-10-27'),
(2,  'Cycling',        50, 420, '2026-10-29'),
(3,  'Weight Lifting', 70, 380, '2026-10-25'),
(3,  'Running',        30, 330, '2026-10-27'),
(3,  'Swimming',       40, 400, '2026-10-28'),
(4,  'Zumba',          55, 450, '2026-10-24'),
(4,  'Walking',        60, 240, '2026-10-26'),
(4,  'Running',        35, 370, '2026-10-28'),
(5,  'Boxing',         45, 520, '2026-10-23'),
(5,  'Weight Lifting', 60, 340, '2026-10-25'),
(5,  'Cycling',        75, 610, '2026-10-28'),
(6,  'Pilates',        50, 230, '2026-10-22'),
(6,  'Yoga',           45, 160, '2026-10-24'),
(6,  'Swimming',       45, 450, '2026-10-27'),
(7,  'Running',        60, 650, '2026-10-21'),
(7,  'HIIT',           30, 360, '2026-10-24'),
(7,  'Weight Lifting', 65, 360, '2026-10-26'),
(8,  'Dancing',        50, 330, '2026-10-23'),
(8,  'Walking',        45, 180, '2026-10-25'),
(8,  'Swimming',       30, 300, '2026-10-29'),
(9,  'Rowing',         40, 400, '2026-10-22'),
(9,  'HIIT',           25, 300, '2026-10-25'),
(9,  'Running',        50, 540, '2026-10-28'),
(10, 'Zumba',          60, 490, '2026-10-24'),
(10, 'Pilates',        40, 190, '2026-10-26'),
(10, 'Walking',        70, 280, '2026-10-29'),
(11, 'Boxing',         35, 410, '2026-10-23'),
(11, 'Cycling',        60, 500, '2026-10-26'),
(11, 'Running',        40, 430, '2026-10-29');

-- ---------- meals (transactions) ----------
INSERT INTO meals (user_id, name, calories, protein, carbs, fat, nutritional_info, meal_date) VALUES
(2,  'Oatmeal with Banana',    350, 10, 62,  6, 'High fiber, low fat',          '2026-10-28'),
(2,  'Chicken Adobo with Rice',620, 38, 65, 22, 'Good protein, high sodium',     '2026-10-28'),
(2,  'Fruit Salad',            180,  2, 44,  1, 'Vitamin C, low protein',        '2026-10-29'),
(3,  'Grilled Salmon',         480, 42,  4, 30, 'Omega-3 rich',                  '2026-10-27'),
(3,  'Scrambled Eggs and Toast',400, 24, 30, 20, 'High protein breakfast',       '2026-10-28'),
(3,  'Protein Shake',          220, 30, 12,  4, 'Post-workout recovery',         '2026-10-28'),
(4,  'Sinigang na Baboy',      450, 30, 25, 24, 'Vegetables and protein',        '2026-10-26'),
(4,  'Vegetable Salad',        150,  5, 18,  7, 'Low calorie',                   '2026-10-27'),
(4,  'Pasta Carbonara',        720, 25, 80, 32, 'High carb, high fat',           '2026-10-28'),
(5,  'Beef Steak with Rice',   780, 50, 70, 30, 'Very high protein',             '2026-10-27'),
(5,  'Greek Yogurt',           160, 17,  9,  5, 'Probiotic',                     '2026-10-28'),
(5,  'Chicken Breast Salad',   380, 45, 12, 14, 'Lean protein',                  '2026-10-29'),
(6,  'Avocado Toast',          340, 9,  34, 20, 'Healthy fats',                  '2026-10-25'),
(6,  'Tuna Sandwich',          430, 28, 40, 16, 'Good protein',                  '2026-10-26'),
(6,  'Brown Rice Bowl',        520, 18, 85, 10, 'Whole grain',                   '2026-10-27'),
(7,  'Pork Sisig',             690, 35, 20, 48, 'High fat, high sodium',         '2026-10-25'),
(7,  'Banana Smoothie',        260,  8, 52,  3, 'Potassium',                     '2026-10-26'),
(7,  'Chicken Fajita Wrap',    540, 36, 48, 20, 'Balanced meal',                 '2026-10-27'),
(8,  'Tofu Stir Fry',          390, 22, 30, 18, 'Plant protein',                 '2026-10-26'),
(8,  'Lentil Soup',            310, 18, 45,  5, 'High fiber',                    '2026-10-27'),
(8,  'Boiled Eggs',            210, 18,  2, 14, 'Low carb snack',                '2026-10-28'),
(9,  'Fried Chicken Meal',     850, 45, 70, 42, 'High calorie treat',            '2026-10-24'),
(9,  'Oat Pancakes',           430, 14, 62, 13, 'Whole grain breakfast',         '2026-10-27'),
(9,  'Veggie Omelette',        320, 22,  8, 22, 'Low carb',                      '2026-10-28'),
(10, 'Fish Fillet with Veggies',410, 38, 20, 18, 'Light dinner',                 '2026-10-26'),
(10, 'Mango Shake',            280,  4, 60,  4, 'High sugar',                    '2026-10-27'),
(10, 'Quinoa Salad',           360, 14, 48, 11, 'Complete protein grain',        '2026-10-28'),
(11, 'Pancit Canton',          560, 20, 75, 18, 'High carb',                     '2026-10-25'),
(11, 'Peanut Butter Toast',    330, 12, 32, 17, 'Energy dense',                  '2026-10-27'),
(11, 'Grilled Chicken Rice',   590, 46, 60, 16, 'High protein',                  '2026-10-29');

-- ---------- goals ----------
INSERT INTO goals (user_id, title, goal_type, target_value, start_date, deadline) VALUES
(2,  'Burn 1000 kcal this month',    'BURN_CALORIES',   1000, '2026-10-01', '2026-10-31'),
(3,  'Work out 300 minutes',         'WORKOUT_MINUTES',  300, '2026-10-15', '2026-11-15'),
(4,  'Keep meals under 2500 kcal',   'EAT_LIMIT',       2500, '2026-10-20', '2026-11-05'),
(5,  'Burn 1500 kcal in two weeks',  'BURN_CALORIES',   1500, '2026-10-20', '2026-11-03'),
(6,  'Work out 200 minutes',         'WORKOUT_MINUTES',  200, '2026-10-01', '2026-10-25'),
(7,  'Burn 3000 kcal by December',   'BURN_CALORIES',   3000, '2026-10-15', '2026-12-15'),
(8,  'Keep meals under 1000 kcal',   'EAT_LIMIT',       1000, '2026-10-25', '2026-11-10'),
(9,  'Work out 500 minutes',         'WORKOUT_MINUTES',  500, '2026-10-01', '2026-11-30'),
(10, 'Burn 800 kcal this week',      'BURN_CALORIES',    800, '2026-10-24', '2026-10-31'),
(11, 'Work out 250 minutes',         'WORKOUT_MINUTES',  250, '2026-10-20', '2026-11-20');
