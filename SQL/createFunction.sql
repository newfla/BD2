--Function to switch index status before and after insert--
CREATE OR REPLACE FUNCTION set_index (enable boolean) RETURNS VOID AS
  $$
  BEGIN
    IF(enable= true) THEN
      CREATE INDEX if not exists acquisition_fact_date_index on acquisition_fact using hash(date_key);
      CREATE INDEX if not exists acquisition_fact_step_index on acquisition_fact using hash(step_key);
      CREATE INDEX if not exists acquisition_fact_km_index on acquisition_fact using hash(km_key);
    ELSE
      DROP INDEX IF EXISTS acquisition_fact_date_index;
      DROP INDEX IF EXISTS acquisition_fact_step_index;
      DROP INDEX IF EXISTS acquisition_fact_km_index;
    END IF;
  END;
  $$
  LANGUAGE plpgsql;

--Function to extract the season from month--
CREATE OR REPLACE FUNCTION get_season(month smallint) RETURNS seasons AS
  $$
  BEGIN
    RETURN (
          CASE
            WHEN month in (1,2,12) THEN
              'winter'
            WHEN month in (3,4,5) THEN
              'spring'
            WHEN month in (6,7,8) THEN
              'summer'
            ELSE
              'autumn'
            END
    );
  END;
  $$ LANGUAGE plpgsql;

--Function to populate temp_table with CSV records
CREATE OR REPLACE FUNCTION import_records_temp_table(file_path TEXT) RETURNS VOID AS
  $$
  DECLARE fn_i TEXT; -- Variable to hold name of current CSV file being inserted
  DECLARE mytable TEXT; -- Variable to hold name of table to insert data into
  BEGIN
    mytable:='temp_table';
    DROP TABLE IF EXISTS files;
    CREATE TEMP TABLE files AS
      SELECT file_path || pg_ls_dir AS fn -- get all of the files in the directory, prepending with file path
      FROM pg_ls_dir(file_path);

    --insert csv records into temp_table
    LOOP
      fn_i := (select fn from files limit 1); -- Pick the first file
      raise notice 'fn: %', fn_i;
      EXECUTE 'COPY ' || mytable || ' FROM ''' || fn_i || ''' DELIMITER '';'' CSV HEADER';
      DELETE FROM files WHERE fn = fn_i; -- Delete the file just inserted from the queue
      EXIT  WHEN (SELECT COUNT(*) FROM files) = 0;
    END LOOP;

  END;
  $$
  LANGUAGE plpgsql;

--Function to populate DataMart--
CREATE OR REPLACE FUNCTION import_records_into_data_mart(file_path TEXT) RETURNS VOID AS
  $$
  BEGIN
  PERFORM import_records_temp_table(file_path);
  PERFORM set_index(false);

  --populate Fact Table--
  /*INSERT INTO acquisition_fact (
                                 test_id,
                                 date_key,
                                 step_key,
                                 km_key,
                                 co_concentration_vol_per  ,
                                 co2_concentration_vol_perc  ,
                                 nox_concentration_ppm  ,
                                 h2O_conc_vol_perc  ,
                                 a_f  ,
                                 exh_flow_corr_m3_min  ,
                                 exh_flow_corr_m3_s  ,
                                 exh_Temp_degC  ,
                                 exh_press_kPa  ,
                                 amb_temp_degC  ,
                                 amb_press_kPa  ,
                                 amb_humid_RH  ,
                                 altitude_m  ,
                                 velocity_km_h  ,
                                 battery_V  ,
                                 co_mass_g_s  ,
                                 co2_mass_g_s  ,
                                 nox_mass_g_s  ,
                                 fuel_g_s  ,
                                 power_kW  ,
                                 speed_km_h  ,
                                 voltage_V  ,
                                 engine_coolant_temperature_by_ecu_degC  ,
                                 fuel_pressure_by_ecu_kPa  ,
                                 engine_RPM_by_ecu_rpm  ,
                                 vehicle_speed_by_ecu_km_h  ,
                                 vehicle_speed_by_ecu_m_s  ,
                                 distance_m  ,
                                 intake_air_temperature_by_ecu_degC  ,
                                 maf_air_flow_rate_by_ecu_grams_sec  ,
                                 maf_air_flow_rate_by_ecu_m3_s  ,
                                 fuel_rail_pressure_by_ecu_kPa  ,
                                 fuel_rail_pressure_diesel_by_ecu_kPa  ,
                                 commande_EGR_by_ecu_perc  ,
                                 barometric_pressure_by_ecu_kPa  ,
                                 ambient_air_temperature_by_ecu_degC  ,
                                 fuel_rail_pressure_Time_by_ecu_kPa  ,
                                 engine_oil_temperature_by_ecu_degC  ,
                                 engine_fuel_rate_by_ecu_L_h  ,
                                 actual_engine_by_ecu_perc  ,
                                 engine_reference_torque_by_ecu_Nm  )
    SELECT     T.test_id
               T.date,
               T.relative_s,
               (T.distance_m::int/1000),
               T.co_concentration_vol_per  ,
               T.co2_concentration_vol_perc  ,
               T.nox_concentration_ppm  ,
               T.h2O_conc_vol_perc  ,
               T.a_f  ,
               T.exh_flow_corr_m3_min  ,
               T.exh_flow_corr_m3_s  ,
               T.exh_Temp_degC  ,
               T.exh_press_kPa  ,
               T.amb_temp_degC  ,
               T.amb_press_kPa  ,
               T.amb_humid_RH  ,
               T.altitude_m  ,
               T.velocity_km_h  ,
               T.battery_V  ,
               T.co_mass_g_s  ,
               T.co2_mass_g_s  ,
               T.nox_mass_g_s  ,
               T.fuel_g_s  ,
               T.power_kW  ,
               T.speed_km_h  ,
               T.voltage_V  ,
               T.engine_coolant_temperature_by_ecu_degC  ,
               T.fuel_pressure_by_ecu_kPa  ,
               T.engine_RPM_by_ecu_rpm  ,
               T.vehicle_speed_by_ecu_km_h  ,
               T.vehicle_speed_by_ecu_m_s  ,
               T.distance_m  ,
               T.intake_air_temperature_by_ecu_degC  ,
               T.maf_air_flow_rate_by_ecu_grams_sec  ,
               T.maf_air_flow_rate_by_ecu_m3_s  ,
               T.fuel_rail_pressure_by_ecu_kPa  ,
               T.fuel_rail_pressure_diesel_by_ecu_kPa  ,
               T.commande_EGR_by_ecu_perc  ,
               T.barometric_pressure_by_ecu_kPa  ,
               T.ambient_air_temperature_by_ecu_degC  ,
               T.fuel_rail_pressure_Time_by_ecu_kPa  ,
               T.engine_oil_temperature_by_ecu_degC  ,
               T.engine_fuel_rate_by_ecu_L_h  ,
               T.actual_engine_by_ecu_perc  ,
               T.engine_reference_torque_by_ecu_Nm
    FROM temp_table as T;*/

  --populate Distance Hierarchy--
    INSERT INTO distance_hierarchy
    SELECT T.distance_m::int /1000 , T.distance_m::int/5000, T.distance_m::int/250000, T.distance_m::int/500000
    FROM temp_table T
    WHERE T.distance_m::int /1000 NOT IN (SELECT U.km_key FROM distance_hierarchy U);

  --populate Date Hierarchy--
    INSERT INTO date_hierarchy
    SELECT DISTINCT T.date, extract(month from T.date), get_season(extract(month from T.date)::smallint) ,extract(YEAR from T.date)
    FROM temp_table T
    WHERE T.date NOT IN (SELECT U.date_key FROM date_hierarchy U);

    --populate Time Hierarchy--
  /*  INSERT INTO time_hierarchy
    SELECT EXTRACT(hour FROM T.time
    FROM temp_table T;*/

  TRUNCATE TABLE temp_table;
  PERFORM set_index(true);
  END;
  $$
  LANGUAGE plpgsql;