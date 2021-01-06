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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class JstockRange {

	private final @Id @GeneratedValue Long id = null;

	/*
	 * 所属
	 */
	private @ManyToOne @JsonBackReference
	Jstock jstock;

	/*
	 * 基准价格
	 */
	private Float basePrise;

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
	 * 平均价格
	 */
	private Float averagePrise;

	/*
	 * 引出的
	 */
	private @OneToMany(mappedBy = "parent") @JsonManagedReference
	List<JstockRange> childrens;

	/*
	 * 引出的
	 */
	private @ManyToOne @JsonBackReference JstockRange parent;

	/*
	 * 档位
	 */
	private Integer position;

	/*
	 * 档位价格
	 */
	private Float positionPrise;

	/*
	 * 状态
	 */
	private String status;

	/*
	 * 最后交易档位
	 */
	private Integer lastPosition;

	/*
	 * 最后交易时间
	 */
	private Date lastTradeDate;

	/*
	 * 信息
	 */
	@Column(length=5000)
	private String info;

	/*
	 * 产生时间
	 */
	private Date createDate;

	/*
	 * 修改时间
	 */
	private Date modifyDate;
}
