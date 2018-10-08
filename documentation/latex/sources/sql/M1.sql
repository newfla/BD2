SELECT F.test_id, AVG(F.co2_mass_g_s), AVG(F.nox_mass_g_s), AVG(F.power_kw), AVG(F.speed_km_h)
FROM acquisition_fact F
GROUP BY F.test_id