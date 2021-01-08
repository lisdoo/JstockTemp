select b.code, b.statusa, b.base_prise, b.statusb, b.`position` ,b.position_prise, jrr.quote_time , jrr.status , jrr.conform , jrr.price from 
(select a.code, a.statusa, a.base_prise, a.statusb, jr.id, jr.`position` , jr.position_prise from
(select j.code , j.status as statusa , jr.base_prise , jr.status as statusb , jr.id from jstock j left join jstock_range jr on j.id = jr.jstock_id where code = '002180' and jstock_strategy_id = 9) as a
left join jstock_range jr on a.id = jr.parent_id) as b
left join jstock_range_record jrr on b.id = jrr.jstock_range_id ;