-- media e deviazione standard delle temperature (su tutto il dataset)

SELECT F.test_id, AVG(F.engine_coolant_temperature_by_ecu_degc), STDDEV(F.engine_coolant_temperature_by_ecu_degc), AVG(F.ambient_air_temperature_by_ecu_degc), STDDEV(F.ambient_air_temperature_by_ecu_degc), AVG(F.engine_oil_temperature_by_ecu_degc), STDDEV(F.engine_oil_temperature_by_ecu_degc)
FROM acquisition_fact F
GROUP BY F.test_id