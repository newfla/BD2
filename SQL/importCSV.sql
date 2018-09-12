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

    --insert csv records into temp_table

    LOOP
        fn_i := (select fn from files limit 1); -- Pick the first file
        raise notice 'fn: %', fn_i;
        EXECUTE 'COPY ' || mytable || ' FROM ''' || fn_i || ''' DELIMITER '';'' CSV HEADER';
        DELETE FROM files WHERE fn = fn_i; -- Delete the file just inserted from the queue
        EXIT  WHEN (SELECT COUNT(*) FROM files) = 0;
     END LOOP;


    LOOP

    END LOOP ;



END $$;