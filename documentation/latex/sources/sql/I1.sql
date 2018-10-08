SELECT F.test_id, H.km_5, AVG(F.co2_mass_g_s), AVG(F.nox_mass_g_s)
FROM acquisition_fact F INNER JOIN distance_hierarchy H
	ON (F.km_key = H.km_key)
GROUP BY F.test_id, H.km_5