SELECT E.test_id, AVG(E.engine_coolant_temperature_by_ecu_degc), STDDEV(E.engine_coolant_temperature_by_ecu_degc), AVG(E.ambient_air_temperature_by_ecu_degc), STDDEV(E.ambient_air_temperature_by_ecu_degc), AVG(E.engine_oil_temperature_by_ecu_degc), STDDEV(E.engine_oil_temperature_by_ecu_degc)
FROM acquisition_fact_ecu E
GROUP BY E.test_id