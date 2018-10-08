-- per ogni test, media di NOx, CO2, Potenza e Velocita' (con partizionamento)

SELECT P.test_id, AVG(P.co2_mass_g_s), AVG(P.nox_mass_g_s), AVG(G.power_kw), AVG(G.speed_km_h)
FROM acquisition_fact_particles P, acquisition_fact_general G
WHERE P.row_id = G.row_id
GROUP BY P.test_id;