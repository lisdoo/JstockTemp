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
package com.lisdoo.jstock.exchange;

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
public class JstockStrategy {

	private final @Id @GeneratedValue Long id = null;

	/*
	 * 范围
	 */
	private Float priceRange;

	/*
	 * 次数
	 */
	private Integer count;

	/*
	 * 偏移方法
	 */
	private Float offset;

	/*
	 * 频率
	 */
	private String fre;

	/*
	 * 产生时间
	 */
	private Date createDate;

	/*
	 * 修改时间
	 */
	private Date modifyDate;
}
