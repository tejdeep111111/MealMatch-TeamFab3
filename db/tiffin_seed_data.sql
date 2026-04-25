-- =============================================================================
-- MealMatch Seed Data — Indian Tiffin Context
-- =============================================================================

USE mealmatch;


INSERT INTO users (id, name, email, phone, dietary_tags, location, password_hash, role) VALUES
  ('u-prov-001', 'TiffinWala Koramangala', 'provider.tiffin1@mealmatch.com', '9876543210', NULL, 'Koramangala, Bangalore',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'PROVIDER'),
  ('u-prov-002', 'SouthSpice Tiffins', 'provider.tiffin2@mealmatch.com', '9876543211', NULL, 'Indiranagar, Bangalore',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'PROVIDER'),
  ('u-prov-003', 'HealthyBites Kitchen', 'provider.tiffin3@mealmatch.com', '9876543212', NULL, 'HSR Layout, Bangalore',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'PROVIDER'),
  ('u-user-001', 'Aarav (Diabetic Safe)',    'user.diabetic@mealmatch.com',     '9000000001', 'DIABETIC_CONTROL,LOW_GI',   'Koramangala, Bangalore',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'USER'),
  ('u-user-002', 'Meera (Low Sodium)',       'user.hypertension@mealmatch.com', '9000000002', 'HYPERTENSION,LOW_SODIUM',   'Indiranagar, Bangalore',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'USER'),
  ('u-user-003', 'Kabir (Fat Loss)',         'user.fatloss@mealmatch.com',      '9000000003', 'FAT_LOSS,HIGH_PROTEIN',     'HSR Layout, Bangalore',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'USER'),
  ('u-user-004', 'Riya (Muscle Gain)',       'user.musclegain@mealmatch.com',   '9000000004', 'MUSCLE_GAIN,HIGH_PROTEIN',  'Whitefield, Bangalore',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'USER');

INSERT INTO providers (id, name, email, phone, location, cuisine_type, rating, is_active) VALUES
  ('p-001', 'TiffinWala Koramangala', 'provider.tiffin1@mealmatch.com', '9876543210', 'Koramangala, Bangalore', 'North Indian Tiffin', 4.5, 1),
  ('p-002', 'SouthSpice Tiffins',    'provider.tiffin2@mealmatch.com', '9876543211', 'Indiranagar, Bangalore', 'South Indian Tiffin', 4.2, 1),
  ('p-003', 'HealthyBites Kitchen',  'provider.tiffin3@mealmatch.com', '9876543212', 'HSR Layout, Bangalore',  'Health / Diet Food',  4.7, 1);

INSERT INTO menu_items (id, provider_id, name, meal_type, dietary_tags, price, is_available) VALUES
  ('mi-1001', 'p-001', 'Brown Rice / Millets Bowl',        'LUNCH',     'DIABETIC_CONTROL,LOW_GI,TIFFIN',           70.00, 1),
  ('mi-1002', 'p-001', 'Dal (Moong / Masoor)',             'LUNCH',     'DIABETIC_CONTROL,HIGH_FIBER,TIFFIN',       55.00, 1),
  ('mi-1003', 'p-001', 'Methi Sabzi',                     'LUNCH',     'DIABETIC_CONTROL,VEG,TIFFIN',              45.00, 1),
  ('mi-1004', 'p-001', 'Bitter Gourd (Karela) Sabzi',     'LUNCH',     'DIABETIC_CONTROL,VEG,TIFFIN',              50.00, 1),
  ('mi-1005', 'p-001', 'Oats Upma',                       'BREAKFAST', 'DIABETIC_CONTROL,LOW_GI,BREAKFAST,TIFFIN', 60.00, 1),
  ('mi-1006', 'p-001', 'Whole Wheat Chapati (2 pcs)',      'LUNCH',     'DIABETIC_CONTROL,WHOLE_WHEAT,TIFFIN',      35.00, 1),
  ('mi-1007', 'p-001', 'Paneer Bhurji + Roti',            'LUNCH',     'MUSCLE_GAIN,HIGH_PROTEIN,CALORIE_DENSE,VEG',140.00, 1),
  ('mi-1008', 'p-001', 'Rajma Chawal',                    'LUNCH',     'MUSCLE_GAIN,COMPLEX_CARBS,VEG',            110.00, 1),
  ('mi-1009', 'p-001', 'Ghee Roti',                       'LUNCH',     'MUSCLE_GAIN,CALORIE_DENSE,TIFFIN',          70.00, 1),
  ('mi-2001', 'p-002', 'Steamed Vegetables',              'LUNCH',     'HYPERTENSION,LOW_SODIUM,VEG,TIFFIN',          45.00, 1),
  ('mi-2002', 'p-002', 'Moong Dal Khichdi (Unsalted)',    'LUNCH',     'HYPERTENSION,LOW_SODIUM,COMFORT_FOOD,TIFFIN', 65.00, 1),
  ('mi-2003', 'p-002', 'Homemade Curd (Unsalted)',        'LUNCH',     'HYPERTENSION,LOW_SODIUM,PROBIOTIC,TIFFIN',    35.00, 1),
  ('mi-2004', 'p-002', 'Poha (No Salt Added)',            'BREAKFAST', 'HYPERTENSION,LOW_SODIUM,BREAKFAST,TIFFIN',   50.00, 1),
  ('mi-2005', 'p-002', 'Banana',                          'SNACK',     'HYPERTENSION,LOW_SODIUM,FRUIT',               25.00, 1),
  ('mi-2006', 'p-002', 'Coconut Water',                   'DRINK',     'HYPERTENSION,LOW_SODIUM,DRINK',               40.00, 1),
  ('mi-2007', 'p-002', 'Banana + Milk Combo',             'SNACK',     'MUSCLE_GAIN,CALORIE_DENSE,SNACK',             60.00, 1),
  ('mi-2008', 'p-002', 'Dry Fruits Mix (Almonds+Cashews)','SNACK',     'MUSCLE_GAIN,HEALTHY_FATS,SNACK',              85.00, 1),
  ('mi-2009', 'p-002', 'Curd Rice',                       'LUNCH',     'MUSCLE_GAIN,PROBIOTIC,HIGH_CARBS,TIFFIN',     75.00, 1),
  ('mi-3001', 'p-003', 'Grilled Chicken Breast',          'LUNCH',     'FAT_LOSS,HIGH_PROTEIN,LEAN,NON_VEG',        120.00, 1),
  ('mi-3002', 'p-003', 'Boiled Egg Whites (6 pcs)',       'BREAKFAST', 'FAT_LOSS,HIGH_PROTEIN,LOW_CAL',              55.00, 1),
  ('mi-3003', 'p-003', 'Low-Fat Paneer',                  'LUNCH',     'FAT_LOSS,HIGH_PROTEIN,LOW_FAT,VEG',          95.00, 1),
  ('mi-3004', 'p-003', 'Moong Dal Chilla',                'BREAKFAST', 'FAT_LOSS,HIGH_PROTEIN,BREAKFAST,VEG',        70.00, 1),
  ('mi-3005', 'p-003', 'Sprouts Salad',                   'SNACK',     'FAT_LOSS,HIGH_FIBER,VEG,SALAD',              60.00, 1),
  ('mi-3006', 'p-003', 'Chicken Clear Soup',              'LUNCH',     'FAT_LOSS,LOW_CAL,NON_VEG',                   80.00, 1),
  ('mi-3007', 'p-003', 'Sauteed Seasonal Vegetables',     'LUNCH',     'FAT_LOSS,LOW_CAL,VEG,TIFFIN',                55.00, 1),
  ('mi-3008', 'p-003', 'Brown Rice (Small Portion)',      'LUNCH',     'FAT_LOSS,CONTROLLED_CARBS,LOW_GI,TIFFIN',    55.00, 1),
  ('mi-3009', 'p-003', 'Whole Eggs (4 pcs)',              'BREAKFAST', 'MUSCLE_GAIN,HIGH_PROTEIN,CALORIE_DENSE',      90.00, 1),
  ('mi-3010', 'p-003', 'Chicken Curry with Rice',         'LUNCH',     'MUSCLE_GAIN,HIGH_PROTEIN,HIGH_CARBS,NON_VEG',150.00, 1),
  ('mi-3011', 'p-003', 'Cucumber Salad',                  'SNACK',     'DIABETIC_CONTROL,LOW_GI,SALAD,TIFFIN',        30.00, 1),
  ('mi-3012', 'p-003', 'Boiled Sweet Potato',             'SNACK',     'DIABETIC_CONTROL,LOW_GI,TIFFIN',              40.00, 1);

INSERT INTO subscriptions (id, user_id, provider_id, menu_item_id, days_of_week, delivery_time, delivery_address, status, start_date, end_date) VALUES
  ('sub-001', 'u-user-001', 'p-001', 'mi-1001', 'MON,TUE,WED,THU,FRI', '12:30', '42, 5th Cross, Koramangala', 'ACTIVE',    '2026-04-01', '2026-04-30'),
  ('sub-002', 'u-user-002', 'p-002', 'mi-2002', 'MON,TUE,WED,THU,FRI', '13:00', '12, CMH Road, Indiranagar',  'PAUSED',    '2026-04-05', '2026-05-05'),
  ('sub-003', 'u-user-003', 'p-003', 'mi-3001', 'MON,TUE,WED,THU,FRI', '12:00', '88, 27th Main, HSR Layout',  'ACTIVE',    '2026-04-10', '2026-05-10'),
  ('sub-004', 'u-user-004', 'p-003', 'mi-3010', 'MON,WED,FRI',          '13:00', '23, ITPL Main Rd, Whitefield','CANCELLED', '2026-04-15', '2026-05-15');
