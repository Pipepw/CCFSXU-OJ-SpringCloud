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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.verwandlung.voj.web.service.DefaultClientService;
import org.verwandlung.voj.web.util.ResponseData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 处理应用程序公共的请求.
 *
 */
@Api(tags = "处理应用程序公共的请求")
@RestController
@RequestMapping(value="/consumer")
public class DefaultController_Consumer {

	/**
	 * 显示使用条款页面.
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 一个包含使用条款页面内容的ModelAndView对象
	 */
	@ApiOperation(value = "显示使用条款页面")
	@RequestMapping(value="/terms", method= RequestMethod.GET)
	public ResponseData termsView(
            HttpServletRequest request, HttpServletResponse response){
		return this.defaultClientService
				.termsView(request, response);
	}

	/**
	 * 显示隐私页面.
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 一个包含隐私页内面容的ModelAndView对象
	 */
	@ApiOperation(value = "显示隐私页面")
	@RequestMapping(value="/privacy", method=RequestMethod.GET)
	public ResponseData privacyView(
            HttpServletRequest request, HttpServletResponse response){
		return this.defaultClientService
				.privacyView(request, response);
	}

	/**
	 * 显示评测机信息页面.
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 一个包含评测机信息页面内容的ModelAndView对象
	 */
	@ApiOperation(value = "显示评测机信息页面")
	@RequestMapping(value="/judgers", method=RequestMethod.GET)
	public ResponseData judgersView(
            HttpServletRequest request, HttpServletResponse response){
		return this.defaultClientService
				.judgersView(request, response);
	}

	/**
	 * 获取评测机列表.
	 * @param offset - 当前加载评测机的UID
	 * @param request - HttpRequest对象
	 * @return 一个包含评测机列表信息的List<Map<String, String>>对象
	 */
	@ApiOperation(value = "获取评测机列表")
	@RequestMapping(value="/getJudgers.action", method=RequestMethod.GET)
	public @ResponseBody
	ResponseData getJudgersAction(
            @ApiParam(value = "当前加载评测机的UID", name = "startIndex")
            @RequestParam(value = "startIndex", required = false, defaultValue = "0") long offset,
            HttpServletRequest request){
		return this.defaultClientService
				.getJudgersAction(offset, request);
	}


	/**
	 * 显示帮助页面.
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 一个包含帮助页面内容的ModelAndView对象
	 */
	@ApiOperation(value = "显示帮助页面")
	@RequestMapping(value="/help", method=RequestMethod.GET)
	public ResponseData helpView(
            HttpServletRequest request, HttpServletResponse response){
		return this.defaultClientService
				.helpView(request, response);
	}

	/**
	 * 显示关于页面.
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 一个包含关于页面内容的ModelAndView对象
	 */
	@ApiOperation(value = "显示关于页面")
	@RequestMapping(value="/about", method=RequestMethod.GET)
	public ResponseData aboutView(
            HttpServletRequest request, HttpServletResponse response){
		return this.defaultClientService
				.aboutView(request, response);
	}

	/**
	 * 显示语言切换的页面.
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 一个包含语言切换页面内容的ModelAndView对象
	 */
	@ApiOperation(value = "显示语言切换的页面")
	@RequestMapping(value="/worldwide", method=RequestMethod.GET)
	public ResponseData worldwideView(
            @ApiParam(value = "语言切换的页面", name = "forward")
            @RequestParam(value = "forward", required = false, defaultValue = "") String forwardUrl,
            HttpServletRequest request, HttpServletResponse response){
		return this.defaultClientService
				.worldwideView(forwardUrl, request, response);
	}

	/**
	 * 处理用户切换语言的请求.
	 * @param language - 需要切换的语言代码
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 语言切换操作结果的HashMap<String, Boolean>对象
	 */
	@ApiOperation(value = "处理用户切换语言的请求")
	@RequestMapping(value="/worldwide.action", method=RequestMethod.GET)
	public @ResponseBody ResponseData localizationAction(
            @ApiParam(value = "需要切换的语言代码", name = "language")
            @RequestParam(value = "language") String language,
            HttpServletRequest request, HttpServletResponse response){
		return this.defaultClientService
				.localizationAction(language, request, response);
	}

	/**
	 * 显示升级浏览器页面.
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 一个包含升级浏览器页面内容的ModelAndView对象
	 */
	@ApiOperation(value = "显示升级浏览器页面")
	@RequestMapping(value="/not-supported", method=RequestMethod.GET)
	public ResponseData notSupportedView(
            HttpServletRequest request, HttpServletResponse response){
		return this.defaultClientService
				.notSupportedView(request, response);
	}

	@Autowired
	DefaultClientService defaultClientService;
}
