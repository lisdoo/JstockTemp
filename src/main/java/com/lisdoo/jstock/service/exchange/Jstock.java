/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lisdoo.jstock.service.exchange;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@NamedNativeQueries({

        @NamedNativeQuery(name = "Jstock.findAllJstocks", //
                query = "select code, CONCAT_WS('/',:requestURL,code) as href from jstock"),

        @NamedNativeQuery(name = "Jstock.findSpecJs", //
                query = "select j.code , CONCAT_WS('/',:requestURL,jr.jstock_strategy_id ) as href, j.status as jsStatus , jr.base_prise as basePrise , jr.last_position as lastPosition , jr.last_trade_date as lastTradeDate, jr.status as rangeStatus , js.price_range as priceRange, js.count as count, js.fre as fre, CONCAT_WS('/',REPLACE(:requestURL,code,'strategy'),jr.jstock_strategy_id ) as strategyHref from jstock j left join jstock_range jr on j.id = jr.jstock_id left join jstock_strategy js on jr.jstock_strategy_id = js.id where j.code = :code"),

        @NamedNativeQuery(name = "Jstock.findRangRec", //
                query = "select (select jr2.`position` from jstock_range jr2 where jr2.id = jrr.jstock_range_id) as `position` , (select jr3.position_prise from jstock_range jr3 where jr3.id = (select jrr2.jstock_range_id from jstock_range_record jrr2 where jrr2.id = jrr.id)) as positionPrise, jrr.price as price, jrr.status as status, jrr.quote_time as quoteTime, jrr.volume as volume, jrr.amount as amount, jrr.conform as conformStatus from jstock_range_record jrr where id in \n" +
                        "(\n" +
                        "\tselect jrr.id from \n" +
                        "\t(\n" +
                        "\t  select a.code, a.statusa, a.base_prise, a.statusb, jr.id, jr.`position` , jr.position_prise from\n" +
                        "\t  (\n" +
                        "\t    select j.code , j.status as statusa , jr.base_prise , jr.status as statusb , jr.id from jstock j left join jstock_range jr on j.id = jr.jstock_id where code = :code and jstock_strategy_id = :strategyId\n" +
                        "\t  ) as a\n" +
                        "\t  left join jstock_range jr on a.id = jr.parent_id\n" +
                        "\t) as b\n" +
                        "\tleft join jstock_range_record jrr on b.id = jrr.jstock_range_id \n" +
                        ")\n" +
                        "order by jrr.quote_time;")})

@Entity
@Data
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Table(name = "jstock", uniqueConstraints = {@UniqueConstraint(columnNames = "code")})
public class Jstock {

    private final @Id
    @GeneratedValue
    Long id = null;
    private String code;
    private String name;
    private String notes;
    private String status;
    private @OneToMany(mappedBy = "jstock")
    @JsonManagedReference
    List<JstockRange> jstockRanges;
    private Date createDate;
    private Date modifyDate;
}
