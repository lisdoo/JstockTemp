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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class JstockRangeRecord {

	private final @Id @GeneratedValue Long id = null;

	/*
	 * 所属顶层
	 */
	private @ManyToOne JstockRange pJstockRange;

	/*
	 * 所属
	 */
	private @ManyToOne JstockRange jstockRange;

	/*
	 * 基准价格
	 */
	private Float price;

	/*
	 * 交易策略
	 */
	private @ManyToOne JstockStrategy jstockStrategy;

	/*
	 * 总交易量
	 */
	private int volume;

	/*
	 * 总交易额
	 */
	private Float amount;

	/*
	 * 状态
	 */
	private String status;

	/*
	 * 确认
	 */
	private Boolean conform;

	/*
	 * 报价时间
	 */
	private Date quoteTime;

	/*
	 * 产生时间
	 */
	private Date createDate;

	/*
	 * 确认时间
	 */
	private Date conformDate;
}
