/* Verwandlung Online Judge - A cross-platform judge online system
 * Copyright (C) 2018 Haozhe Xie <cshzxie@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *                              _ooOoo_  
 *                             o8888888o  
 *                             88" . "88  
 *                             (| -_- |)  
 *                             O\  =  /O  
 *                          ____/`---'\____  
 *                        .'  \\|     |//  `.  
 *                       /  \\|||  :  |||//  \  
 *                      /  _||||| -:- |||||-  \  
 *                      |   | \\\  -  /// |   |  
 *                      | \_|  ''\---/''  |   |  
 *                      \  .-\__  `-`  ___/-. /  
 *                    ___`. .'  /--.--\  `. . __  
 *                 ."" '<  `.___\_<|>_/___.'  >'"".  
 *                | | :  `- \`.;`\ _ /`;.`/ - ` : | |  
 *                \  \ `-.   \_ __\ /__ _/   .-` /  /  
 *           ======`-.____`-.___\_____/___.-`____.-'======  
 *                              `=---=' 
 *
 *                          HERE BE BUDDHA
 *
 */
package org.verwandlung.voj.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;

import org.verwandlung.voj.web.exception.ResourceNotFoundException;
import org.verwandlung.voj.web.util.ResponseData;

import java.util.HashMap;
import java.util.Map;

/**
 * 异常处理的控制器.
 *
 */
@Api(tags = "异常处理的控制器")
@ControllerAdvice
public class ExceptionHandlingController {
	/**
	 * 处理MissingServletRequestParameterException异常的方法.
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 返回一个包含异常信息的ModelAndView对象
	 */
	@ApiOperation(value = "处理MissingServletRequestParameterException异常的方法")
	@ResponseStatus(value=HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseData badRequestView(
			HttpServletRequest request, HttpServletResponse response) {
//		ModelAndView view = new ModelAndView("errors/404");
		Map<String, Object> result = new HashMap<>();
		result.put("msg", "跳转到404");
		return ResponseData.ok().data("result",result);
	}
	
	/**
	 * 处理ResourceNotFoundException异常的方法.
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 返回一个包含异常信息的ModelAndView对象
	 */
	@ApiOperation(value = "处理ResourceNotFoundException异常的方法")
	@ResponseStatus(value=HttpStatus.NOT_FOUND)
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseData notFoundView(
			HttpServletRequest request, HttpServletResponse response) {
//		ModelAndView view = new ModelAndView("errors/404");
		Map<String, Object> result = new HashMap<>();
		result.put("msg", "跳转到404");
		return ResponseData.ok().data("result",result);
	}
	
	/**
	 * 处理HttpRequestMethodNotSupportedException异常的方法.
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 返回一个包含异常信息的ModelAndView对象
	 */
	@ApiOperation(value = "处理HttpRequestMethodNotSupportedException异常的方法")
	@ResponseStatus(value=HttpStatus.METHOD_NOT_ALLOWED)
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseData methodNotAllowedView(
			HttpServletRequest request, HttpServletResponse response) {
//		ModelAndView view = new ModelAndView("errors/404");
		Map<String, Object> result = new HashMap<>();
		result.put("msg", "跳转到404");
		return ResponseData.ok().data("result",result);
	}
	
	/**
	 * 处理通用Exception异常的方法.
	 * @param ex - 抛出的异常对象
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 返回一个包含异常信息的ModelAndView对象
	 */
	@ApiOperation(value = "处理通用Exception异常的方法")
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public ResponseData internalServerErrorView(
			Exception ex, HttpServletRequest request, HttpServletResponse response) {
		LOGGER.catching(ex);
		
//		ModelAndView view = new ModelAndView("errors/500");
		Map<String, Object> result = new HashMap<>();
		result.put("msg", "跳转到500");
		return ResponseData.ok().data("result",result);
	}
	
	/**
	 * 日志记录器.
	 */
	private static final Logger LOGGER = LogManager.getLogger(org.verwandlung.voj.web.controller.ExceptionHandlingController.class);
}
