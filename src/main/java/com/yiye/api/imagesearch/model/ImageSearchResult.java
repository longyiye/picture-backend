package com.yiye.api.imagesearch.model;

import lombok.Data;

/**
 * 360搜图图片搜索结果
 */
@Data
public class ImageSearchResult {

	/**
	 * 图片地址
	 */
	private String imgUrl;

	/**
	 * 标题
	 */
	private String title;

	/**
	 * 图片key
	 */
	private String imgkey;

	/**
	 * HTTP
	 */
	private String http;

	/**
	 * HTTPS
	 */
	private String https;

}
