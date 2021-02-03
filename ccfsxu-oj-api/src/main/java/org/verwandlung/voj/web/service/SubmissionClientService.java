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
import java.io.IOException;

/**
 * 加载/显示评测的相关信息.
 *
 */
@FeignClient(value = "CCUSXU-OJ-DEPT")
@Api(tags = "加载/显示评测的相关信息")
@RequestMapping(value="/submission")
public interface SubmissionClientService {
	/**
	 * 显示评测列表的页面.
	 * @param problemId - 试题的唯一标识符
	 * @param username - 用户的用户名
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 包含提交列表的ModelAndView对象 
	 */
	@ApiOperation(value = "显示评测列表的页面")
	@RequestMapping(value="", method=RequestMethod.GET)
	public ResponseData submissionsView(
			@ApiParam(value="试题的唯一标识符", name="problemId")
			@RequestParam(value="problemId", required=false, defaultValue="0") long problemId,
			@ApiParam(value="用户的用户名", name="username")
			@RequestParam(value="username", required=false, defaultValue="") String username,
			HttpServletRequest request, HttpServletResponse response);
	
	/**
	 * 获取历史评测信息的列表.
	 * @param problemId - 试题的唯一标识符
	 * @param username - 用户的用户名
	 * @param startIndex - 当前加载的最后一条记录的提交唯一标识符
	 * @param request - HttpRequest对象
	 * @return 一个包含提交记录列表的HashMap对象
	 */
	@ApiOperation(value = "获取历史评测信息的列表")
	@RequestMapping(value="/getSubmissions.action", method=RequestMethod.GET)
	public @ResponseBody ResponseData getSubmissionsAction(
			@ApiParam(value="试题的唯一标识符", name="problemId")
			@RequestParam(value="problemId", required=false, defaultValue="0") long problemId,
			@ApiParam(value="用户的用户名", name="username")
			@RequestParam(value="username", required=false, defaultValue="") String username,
			@ApiParam(value="当前加载的最后一条记录的提交唯一标识符", name="startIndex")
			@RequestParam(value="startIndex") long startIndex,
			HttpServletRequest request);
	
	/**
	 * 获取最新的评测信息的列表.
	 * @param problemId - 试题的唯一标识符
	 * @param username - 用户的用户名
	 * @param startIndex - 当前加载的最新一条记录的提交唯一标识符
	 * @param request - HttpRequest对象
	 * @return 一个包含提交记录列表的HashMap对象
	 */
	@ApiOperation(value = "获取最新的评测信息的列表")
	@RequestMapping(value="/getLatestSubmissions.action", method=RequestMethod.GET)
	public @ResponseBody ResponseData getLatestSubmissionsAction(
			@ApiParam(value="试题的唯一标识符", name="problemId")
			@RequestParam(value="problemId", required=false, defaultValue="0") long problemId,
			@ApiParam(value="用户的用户名", name="username")
			@RequestParam(value="username", required=false, defaultValue="") String username,
			@ApiParam(value="当前加载的最新一条记录的提交唯一标识符", name="startIndex")
			@RequestParam(value="startIndex") long startIndex,
			HttpServletRequest request);
	
	/**
	 * 显示提交记录详细信息的页面.
	 * @param submissionId - 提交记录的唯一标识符
	 * @param request - HttpRequest对象
	 * @param response - HttpResponse对象
	 * @return 包含提交详细信息的ModelAndView对象 
	 */
	@ApiOperation(value = "显示提交记录详细信息的页面")
	@RequestMapping(value="/{submissionId}", method=RequestMethod.GET)
	public ResponseData submissionView(
			@ApiParam(value="提交记录的唯一标识符",name="submissionId")
			@PathVariable("submissionId") long submissionId,
			HttpServletRequest request, HttpServletResponse response);
	
	/**
	 * 获取实时的评测结果.
	 * @param submissionId - 提交记录的唯一标识符
	 * @return 包含评测结果信息的StreamingResponseBody对象
	 * @throws IOException 
	 */
	@ApiOperation(value = "获取实时的评测结果")
	@RequestMapping(value="/getRealTimeJudgeResult.action", method=RequestMethod.GET)
	public ResponseData getRealTimeJudgeResultAction(
			@RequestParam(value="submissionId") long submissionId,
			@RequestParam(value="csrfToken") String csrfToken,
			HttpServletRequest request, HttpServletResponse response);
	
	/**
	 * 获取提交记录的详细信息.
	 * @param submissionId - 提交记录的唯一标识符
	 * @param request - HttpRequest对象
	 * @return 包含提交记录详细信息的HashMap对象
	 */
	@ApiOperation(value = "获取提交记录的详细信息")
	@RequestMapping(value="/getSubmission.action", method=RequestMethod.GET)
	public @ResponseBody ResponseData getSubmissionAction(
			@ApiParam(value="提交记录的唯一标识符", name="submissionId")
			@RequestParam(value="submissionId") long submissionId,
			HttpServletRequest request);

	/**
	 * 提交正确率
	 * @return
	 */
	@ApiOperation(value = "提交正确率")
	@RequestMapping(value = "/submissionRank",method = RequestMethod.GET)
	public ResponseData getSubmissionRank();

}
