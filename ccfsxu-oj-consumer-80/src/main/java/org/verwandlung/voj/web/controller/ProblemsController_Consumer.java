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
import org.verwandlung.voj.web.service.ProblemsClientService;
import org.verwandlung.voj.web.util.ResponseData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * 处理用户的查看试题/提交评测等请求.
 *
 */
@Api(tags = "处理用户的查看试题/提交评测等请求")
@RestController
@RequestMapping(value="/consumer/p")
public class ProblemsController_Consumer {
	/**
	 * 显示试题库中的全部试题.
	 * @param startIndex - 试题的起始下标
	 * @param keyword - 关键词
	 * @param problemCategorySlug - 试题分类的别名
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 包含试题库页面信息的ModelAndView对象
	 * @throws UnsupportedEncodingException 
	 */
	@ApiOperation(value = "显示试题库中的全部试题")
	@RequestMapping(value="", method= RequestMethod.GET)
	public ResponseData problemsView(
            @ApiParam(value = "试题的起始下标", name = "start")
            @RequestParam(value = "start", required = false, defaultValue = "1") long startIndex,
            @ApiParam(value = "关键词", name = "keyword")
            @RequestParam(value = "keyword", required = false) String keyword,
            @ApiParam(value = "试题分类的别名", name = "category")
            @RequestParam(value = "category", required = false) String problemCategorySlug,
            HttpServletRequest request, HttpServletResponse response){
		return this.problemsClientService
		.problemsView(startIndex, keyword, problemCategorySlug, request, response);
	}

	/**
	 * 获取试题列表.
	 * @param startIndex - 试题的起始下标
	 * @param request - HttpRequest对象
	 * @return 一个包含试题列表的HashMap对象
	 */
	@ApiOperation(value = "获取试题列表")
	@RequestMapping(value="/getProblems.action", method=RequestMethod.GET)
	public @ResponseBody
	ResponseData getProblemsAction(
            @ApiParam(value = "试题的起始下标", name = "startIndex")
            @RequestParam(value = "startIndex") long startIndex,
            @ApiParam(value = "关键词", name = "keyword")
            @RequestParam(value = "keyword", required = false) String keyword,
            @ApiParam(value = "试题分类的别名", name = "category")
            @RequestParam(value = "category", required = false) String problemCategorySlug,
            HttpServletRequest request){
		return this.problemsClientService
				.getProblemsAction(startIndex, keyword, problemCategorySlug, request);
	}

	/**
	 * 加载试题的详细信息.
	 * @param problemId - 试题的唯一标识符
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 包含试题详细信息的ModelAndView对象
	 */
	@ApiOperation(value = "加载试题的详细信息")
	@RequestMapping(value="/{problemId}", method=RequestMethod.GET)
	public ResponseData problemView(
            @PathVariable("problemId") long problemId,
            HttpServletRequest request, HttpServletResponse response){
		return this.problemsClientService
				.problemView(problemId, request, response);
	}

	/**
	 * 加载试题题解页面.
	 * @param problemId - 试题的唯一标识符
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 包含试题题解信息的ModelAndView对象
	 */
	@ApiOperation(value = "加载试题题解页面")
	@RequestMapping(value="/{problemId}/solution", method=RequestMethod.GET)
	public ResponseData solutionView(
            @ApiParam(value = "试题的唯一标识符", name = "problemId")
            @PathVariable("problemId") long problemId,
            HttpServletRequest request, HttpServletResponse response){
		return this.problemsClientService
				.solutionView(problemId, request, response);
	}

	/**
	 * 创建提交.
	 * @param problemId - 试题的唯一标识符
	 * @param languageSlug - 编程语言的别名
	 * @param code - 代码
	 * @param csrfToken - 用于防止CSRF攻击的Token
	 * @param request - HttpRequest对象
	 * @return 一个包含提交记录创建结果的Map<String, Object>对象
	 */
	@ApiOperation(value = "创建提交")
	@RequestMapping(value="/createSubmission.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData createSubmissionAction(
            @ApiParam(value = "试题的唯一标识符", name = "problemId")
            @RequestParam(value = "problemId") long problemId,
            @ApiParam(value = "编程语言的别名", name = "languageSlug")
            @RequestParam(value = "languageSlug") String languageSlug,
            @ApiParam(value = "代码", name = "code")
            @RequestParam(value = "code") String code,
            @ApiParam(value = "用于防止CSRF攻击的Token", name = "csrfToken")
            @RequestParam(value = "csrfToken") String csrfToken,
            HttpServletRequest request){
		return this.problemsClientService
				.createSubmissionAction(problemId, languageSlug, code, csrfToken, request);
	}

	@Autowired
	ProblemsClientService problemsClientService;
}
