-- impatto ambientale medio in corse da 5 km (rollup implicito)

SELECT F.test_id, FLOOR(F.km_key/5), AVG(F.co2_mass_g_s), AVG(F.nox_mass_g_s)
FROM acquisition_fact F
GROUP BY F.test_id, FLOOR(F.km_key/5)