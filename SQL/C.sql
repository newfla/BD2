-- consumo medio per intervalli di velocita' prefissati

SELECT *
FROM fuel_compare_speed_mv;

SELECT t1.id, t1.fuel_litres AS consumo_litri_meno_di_50km_h, t2.fuel_litres AS consumo_litri_meno_di_90km_h, t3.fuel_litres AS consumo_litri_meno_di_130km_h
FROM
	(SELECT test_id AS id, ROUND(SUM((fuel_g_s)/1000)::NUMERIC/0.8,2) AS fuel_litres, ROUND(AVG(velocity_km_h)::NUMERIC,1) AS average_speed_km_h
	FROM acquisition_fact
	WHERE speed_km_h <= 50
	GROUP BY test_id) t1
JOIN (
	SELECT test_id as id, ROUND(SUM((fuel_g_s)/1000)::NUMERIC/0.8,2) AS fuel_litres, ROUND(AVG(velocity_km_h)::NUMERIC,1) AS average_speed_km_h
	FROM acquisition_fact
	WHERE speed_km_h >= 50 AND speed_km_h <= 90
	GROUP BY test_id) t2 ON t1.id = t2.id
JOIN (
	SELECT test_id AS id , ROUND(SUM((fuel_g_s)/1000)::NUMERIC/0.8,2) AS fuel_litres, ROUND(AVG(velocity_km_h)::NUMERIC,1) AS average_speed_km_h
	FROM acquisition_fact
	WHERE speed_km_h >= 90
	GROUP BY test_id) t3 ON t1.id = t3.id;