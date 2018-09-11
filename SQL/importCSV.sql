DO $$

DECLARE file_path TEXT; -- Path where your CSV files are
DECLARE fn_i TEXT; -- Variable to hold name of current CSV file being inserted
DECLARE mytable TEXT; -- Variable to hold name of table to insert data into

BEGIN

    file_path:= './AAA/csv/';
    mytable:='temp_table';

    CREATE TEMP TABLE files AS
    SELECT file_path || pg_ls_dir AS fn -- get all of the files in the directory, prepending with file path
    FROM pg_ls_dir(file_path);

    /*CREATE TABLE tempTable (
        date Date,
        time Time,
        relative_s int,
        co_concentration_vol_per double precision,
        co2_concentration_vol_perc double precision,
        nox_concentration_ppm double precision,
        h2O_conc_vol_perc double precision,
        a_f double precision,
        exh_flow_corr_m3_min double precision,
        exh_flow_corr_m3_s double precision,
        exh_Temp_degC double precision,
        exh_press_kPa double precision,
        amb_temp_degC double precision,
        amb_press_kPa double precision,
        amb_humid_RH double precision,
        altitude_m double precision,
        velocity_km_h double precision,
        battery_V double precision,
        co_mass_g_s double precision,
        co2_mass_g_s double precision,
        nox_mass_g_s double precision,
        fuel_g_s double precision,
        power_kW double precision,
        speed_km_h double precision,
        voltage_V double precision,
        engine_coolant_temperature_by_ecu_degC double precision,
        fuel_pressure_by_ecu_kPa double precision,
        engine_RPM_by_ecu_rpm double precision,
        vehicle_speed_by_ecu_km_h double precision,
        vehicle_speed_by_ecu_m_s double precision,
        distance_m double precision,
        intake_air_temperature_by_ecu_degC double precision,
        maf_air_flow_rate_by_ecu_grams_sec double precision,
        maf_air_flow_rate_by_ecu_m3_s double precision,
        fuel_rail_pressure_by_ecu_kPa double precision,
        fuel_rail_pressure_diesel_by_ecu_kPa double precision,
        commande_EGR_by_ecu_perc double precision,
        barometric_pressure_by_ecu_kPa double precision,
        ambient_air_temperature_by_ecu_degC double precision,
        fuel_rail_pressure_Time_by_ecu_kPa double precision,
        engine_oil_temperature_by_ecu_degC double precision,
        engine_fuel_rate_by_ecu_L_h double precision,
        actual_engine_by_ecu_perc double precision,
        engine_reference_torque_by_ecu_Nm double precision
    );*/


    LOOP
        fn_i := (select fn from files limit 1); -- Pick the first file
        raise notice 'fn: %', fn_i;
        EXECUTE 'COPY ' || mytable || ' FROM ''' || fn_i || ''' DELIMITER '';'' CSV HEADER';
        DELETE FROM files WHERE fn = fn_i; -- Delete the file just inserted from the queue
        EXIT  WHEN (SELECT COUNT(*) FROM files) = 0;
     END LOOP;

END $$;