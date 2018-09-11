DO $$

DECLARE file_path TEXT; -- Path where your CSV files are
DECLARE fn_i TEXT; -- Variable to hold name of current CSV file being inserted
DECLARE mytable TEXT; -- Variable to hold name of table to insert data into

BEGIN

    file_path := '/home/flavio/Progetti/BD2/excel/duplicates/csv';
    mytable := 'product(title,department)'; -- Declare table to insert data into. You can give columns too since it's just going into an execute statement.

    CREATE TEMP TABLE files AS
    SELECT file_path || pg_ls_dir AS fn -- get all of the files in the directory, prepending with file path
    FROM pg_ls_dir(file_path);

    LOOP
        fn_i := (select fn from files limit 1); -- Pick the first file
        raise notice 'fn: %', fn_i;
        EXECUTE 'COPY ' || mytable || ' from ''' || fn_i || ''' with csv header';
        DELETE FROM files WHERE fn = fn_i; -- Delete the file just inserted from the queue
        EXIT  WHEN (SELECT COUNT(*) FROM files) = 0;
     END LOOP;

END $$;