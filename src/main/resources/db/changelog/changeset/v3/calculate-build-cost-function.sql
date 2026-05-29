CREATE OR REPLACE FUNCTION calculate_build_cost(p_build_id BIGINT)
    RETURNS NUMERIC(15,2)
    LANGUAGE plpgsql
AS $$
DECLARE
    v_total_cost NUMERIC(15,2);
BEGIN
    SELECT COALESCE(SUM(bp.quantity * c.price), 0)
    INTO v_total_cost
    FROM build_partitions bp
             JOIN components c ON bp.component_id = c.id
    WHERE bp.build_id = p_build_id;

    RETURN v_total_cost;
END;
$$;