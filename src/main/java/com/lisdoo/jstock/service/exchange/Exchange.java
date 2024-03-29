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
public class Exchange {

	private final @Id @GeneratedValue Long id = null;

	/*
	 * 交易价格
	 */
	private Float exchangePrise;

	/*
	 * 本次交易量
	 */
	private int exchangeVolume;

	/*
	 * 本次交易额
	 */
	private Float exchangeAmount;

	/*
	 * 买、卖
	 */
	private String operation;

	/*
	 * 产生时间
	 */
	private Date createDate;
}
