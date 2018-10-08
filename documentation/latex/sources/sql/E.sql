SELECT *
FROM efficiency_compare_rpm_mv;

SELECT t1.id,  ROUND((t1.rendimento*100)::NUMERIC, 2) AS efficiency_perc_max2000rpm, ROUND((t2.rendimento*100)::NUMERIC,2) AS efficiency_perc_max3000rpm, ROUND((t3.rendimento*100)::NUMERIC,2) AS efficiency_perc_over3000rpm
FROM
	(SELECT test_id AS id, AVG(power_kw)/ (AVG(fuel_g_s)/1000 * 458000) AS rendimento
	FROM acquisition_fact
	WHERE engine_RPM_by_ecu_rpm < 2000
	GROUP BY test_id) t1
JOIN (
	SELECT test_id AS id, AVG(power_kw)/ (AVG(fuel_g_s)/1000 * 458000) AS rendimento
	FROM acquisition_fact
	WHERE engine_RPM_by_ecu_rpm >= 2000 AND engine_RPM_by_ecu_rpm <= 3000
	GROUP BY test_id) t2 ON  t1.id = t2.id
JOIN (
	SELECT  test_id AS id, AVG(power_kw)/( AVG(fuel_g_s)/1000 * 458000) AS rendimento
	FROM acquisition_fact
	WHERE engine_RPM_by_ecu_rpm >= 3000
	GROUP BY test_id) t3 ON t1.id = t3.id;