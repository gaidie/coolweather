-- Province:
create table Province(
 id integer primary key autoincrement,
 province_name text,
 province_code text
)

--City:
create table City(
  id integer primary key autoincrement,
  city_name text,
  city_code text,
  province_id integer
)

-- Çø
create table Country(
 id integer primary key autoincrement,
 country_name text,
 country_code text,
 city_code text
)
