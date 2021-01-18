select b.code, b.statusa, b.base_prise, b.statusb, b.`position` ,b.position_prise, jrr.quote_time , jrr.status , jrr.conform , jrr.price from 
(
  select a.code, a.statusa, a.base_prise, a.statusb, jr.id, jr.`position` , jr.position_prise from
  (
    select j.code , j.status as statusa , jr.base_prise , jr.status as statusb , jr.id from jstock j left join jstock_range jr on j.id = jr.jstock_id where code = '002180' and jstock_strategy_id = 9
  ) as a
  left join jstock_range jr on a.id = jr.parent_id
) as b
left join jstock_range_record jrr on b.id = jrr.jstock_range_id 
-- order by quote_time 
order by `position` desc 
;

select (select CONCAT_WS('/','kkkk','kkj' )) as href, (select jr2.`position` from jstock_range jr2 where jr2.id = jrr.jstock_range_id) as position , (select jr3.position_prise from jstock_range jr3 where jr3.id = (select jrr2.jstock_range_id from jstock_range_record jrr2 where jrr2.id = jrr.id)) as position_prise, jrr.price as price, jrr.status , jrr.quote_time , jrr.volume, jrr.amount, jrr.conform from jstock_range_record jrr where id in 
(
	select jrr.id from 
	(
	  select a.code, a.statusa, a.base_prise, a.statusb, jr.id, jr.`position` , jr.position_prise from
	  (
	    select j.code , j.status as statusa , jr.base_prise , jr.status as statusb , jr.id from jstock j left join jstock_range jr on j.id = jr.jstock_id where code = '002180' and jstock_strategy_id = 9
	  ) as a
	  left join jstock_range jr on a.id = jr.parent_id
	) as b
	left join jstock_range_record jrr on b.id = jrr.jstock_range_id 
)
order by jrr.quote_time;
;

select * from jstock_strategy js where js.id = 9;

select j.code , j.status as jsStatus , jr.base_prise , jr.last_position , jr.last_trade_date , jr.status as rangeStatus,  js.price_range as priceRange, js.count as count, js.fre as fre from jstock j left join jstock_range jr on j.id = jr.jstock_id left join jstock_strategy js on jr.jstock_strategy_id = js.id where j.code = '002180';