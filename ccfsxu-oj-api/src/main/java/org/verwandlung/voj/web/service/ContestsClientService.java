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
package org.verwandlung.voj.web.service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.verwandlung.voj.web.util.ResponseData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 处理竞赛的相关请求.
 *
 */
@FeignClient(value = "CCUSXU-OJ-DEPT")
@Api(tags = "处理竞赛的相关请求")
@RequestMapping(value="/contest")
public interface ContestsClientService {
	/**
	 * 显示竞赛列表页面.
	 * @param keyword - 竞赛的关键词
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 一个包含竞赛列表页面内容的ModelAndView对象
	 */
	@ApiOperation(value = "显示竞赛列表页面")
	@RequestMapping(value="", method=RequestMethod.GET)
	public ResponseData contestsView(
			@ApiParam(value="关键字", name="keyword", required = false)
			@RequestParam(value="keyword", required = false) String keyword,
			HttpServletRequest request, HttpServletResponse response);
	
	/**
	 * 获取竞赛的列表.
	 * @param keyword - 竞赛的关键词
	 * @param startIndex - 当前加载的最后一条记录的索引值 (Index)
	 * @param request - HttpRequest对象
	 * @return 一个包含竞赛列表的HashMap对象
	 */
	@ApiOperation(value = "获取竞赛的列表")
	@RequestMapping(value="/getContests.action", method=RequestMethod.GET)
	public @ResponseBody ResponseData getContestsAction(
			@ApiParam(value="关键字",name="keyword")
			@RequestParam(value="keyword", required=false) String keyword,
			@ApiParam(value="当前加载的最后一条记录的索引值 (Index)",name="startIndex")
			@RequestParam(value="startIndex") long startIndex,
			HttpServletRequest request);
	
	/**
	 * 显示竞赛详细信息的页面.
	 * @param contestId - 竞赛的唯一标识符
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 包含提交详细信息的ModelAndView对象 
	 */
	@ApiOperation(value = "显示竞赛详细信息的页面")
	@RequestMapping(value="/{contestId}", method=RequestMethod.GET)
	public ResponseData contestView(
			@ApiParam(value="竞赛的唯一标识符",name="contestId")
			@PathVariable("contestId") long contestId,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * 处理用户参加竞赛的请求.
	 * @param contestId - 竞赛的唯一标识符
	 * @param csrfToken - 用于防止CSRF攻击的Token
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 包含是否成功参加竞赛状态信息的Map对象
	 */
	@ApiOperation(value = "处理用户参加竞赛的请求")
	@RequestMapping(value="/{contestId}/attend.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData attendContestAction(
			@ApiParam(value="竞赛的唯一标识符",name="contestId")
			@PathVariable("contestId") long contestId,
			@ApiParam(value="用于防止CSRF攻击的Token", name="csrfToken")
			@RequestParam(value="csrfToken") String csrfToken,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * 显示排行榜.
	 * @param contestId - 竞赛的唯一标识符
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 包含竞赛排行榜的ModelAndView对象
	 */
	@ApiOperation(value = "显示排行榜")
	@RequestMapping(value="/{contestId}/leader_board", method=RequestMethod.GET)
	public ResponseData leaderBoardView(
			@ApiParam(value="竞赛的唯一标识符",name="contestId")
			@PathVariable("contestId") long contestId,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * 显示竞赛中的试题信息.
	 * @param contestId - 竞赛的唯一标识符
	 * @param problemId - 试题的唯一标识符
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 包含竞赛试题信息的ModelAndView对象
	 */
	@ApiOperation(value = "显示竞赛中的试题信息")
	@RequestMapping(value="/{contestId}/p/{problemId}", method=RequestMethod.GET)
	public ResponseData problemView(
			@ApiParam(value="竞赛的唯一标识符", name="contestId")
			@PathVariable("contestId") long contestId,
			@ApiParam(value="试题的唯一标识符", name="problemId")
			@PathVariable("problemId") long problemId,
			HttpServletRequest request, HttpServletResponse response);

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
	@RequestMapping(value="/{contestId}/createSubmission.action", method=RequestMethod.POST)
	public @ResponseBody ResponseData createSubmissionAction(
			@ApiParam(value = "试题的唯一标识符", name = "problemId")
			@RequestParam(value = "problemId") long problemId,
			@ApiParam(value = "编程语言的别名", name = "languageSlug")
			@RequestParam(value = "languageSlug") String languageSlug,
			@ApiParam(value = "代码", name = "code")
			@RequestParam(value = "code") String code,
			@ApiParam(value = "用于防止CSRF攻击的Token", name = "csrfToken")
			@RequestParam(value = "csrfToken") String csrfToken,
			HttpServletRequest request, @PathVariable String contestId);

}
