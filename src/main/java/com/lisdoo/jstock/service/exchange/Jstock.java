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
                query = "select CONCAT_WS('/',:requestURL,jr.jstock_strategy_id ) as href, j.status as jsStatus , jr.base_prise as basePrise , jr.last_position as lastPosition , jr.last_trade_date as lastTradeDate, jr.status as rangeStatus , js.price_range as priceRange, js.count as count, js.fre as fre, CONCAT_WS('/',REPLACE(:requestURL,code,'strategy'),jr.jstock_strategy_id ) as strategyHref from jstock j left join jstock_range jr on j.id = jr.jstock_id left join jstock_strategy js on jr.jstock_strategy_id = js.id where j.code = :code"),

        @NamedNativeQuery(name = "Jstock.findAllJstocks", //
                query = "select code, CONCAT_WS('/',:requestURL,code) as href from jstock")})

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
